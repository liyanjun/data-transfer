package gov.gxgt.transfer.modules.transfer.asyn.lisenter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gxgt.transfer.modules.transfer.asyn.event.RecerviCourseInfoEvent;
import gov.gxgt.transfer.modules.transfer.config.TransferConfig;
import gov.gxgt.transfer.modules.transfer.entity.TransferAuditItemEntity;
import gov.gxgt.transfer.modules.transfer.entity.TransferRequestRecordEntity;
import gov.gxgt.transfer.modules.transfer.service.TransferAuditItemService;
import gov.gxgt.transfer.modules.transfer.service.TransferRequestRecordService;
import gov.gxgt.transfer.modules.transfer.utils.TokenUtils;
import gov.gxgt.transfer.modules.transfer.utils.XMLUtils;
import gov.gxgt.transfer.modules.yth.entity.YthBdcEntity;
import gov.gxgt.transfer.modules.yth.service.YthBdcService;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * @author liyanjun
 */
@Component
public class ReceiveCourseInfoListener {


    private static final Logger logger = LoggerFactory.getLogger(ReceiveCourseInfoListener.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private TransferConfig transferConfig;

    @Autowired
    private TransferRequestRecordService transferRequestRecordService;

    @Autowired
    private TransferAuditItemService transferAuditItemService;

    @Autowired
    private YthBdcService ythBdcService;

    @Async
    @EventListener
    public void onApplicationEvent(RecerviCourseInfoEvent recerviCourseInfoEvent) throws IOException, DocumentException {
        TransferRequestRecordEntity transferRequestRecordEntity = null;
        String accessToken = tokenUtils.getAccessToken();
        // 锁行，开始发送受理信息
        YthBdcEntity ythBdcEntity = ythBdcService.getOne(new QueryWrapper<YthBdcEntity>().eq("ID", recerviCourseInfoEvent.getSource()).last(" for update"));
        if (ythBdcEntity.getState() != 2) {
            // 已推送，不管了
            return;
        }
        TransferAuditItemEntity transferAuditItemEntity = transferAuditItemService.getOne(new QueryWrapper<TransferAuditItemEntity>().
                eq("area_code", ythBdcEntity.getAreaCode()).eq("SXMC", "抵押权登记").le("rownum", 1));
        if (transferAuditItemEntity == null) {
            transferAuditItemEntity = transferAuditItemService.getOne(new QueryWrapper<TransferAuditItemEntity>().
                    eq("area_code", ythBdcEntity.getAreaCode()).like("SXMC", "抵押权首次登记").le("rownum", 1));
        }
        if (transferAuditItemEntity == null) {
            logger.error(ythBdcEntity.getId() + "：找不到相应的事项。");
            return;
        }
        Map map = objectMapper.readValue(ythBdcEntity.getDataSp(), Map.class);
        getItemInfo(map, transferAuditItemEntity);
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + XMLUtils.multilayerMapToXml(map, false, "APPROVEDATAINFO");
        MultiValueMap requestMap = new LinkedMultiValueMap(16);
        requestMap.put("access_token", Collections.singletonList(accessToken));
        requestMap.put("accessToken", Collections.singletonList(tokenUtils.getToken()));
        requestMap.put("xmlStr", Collections.singletonList(xml));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(requestMap, headers);
        try {
            // 保存发送记录
            transferRequestRecordEntity = transferRequestRecordService.saveRequest(objectMapper.writeValueAsString(map), ythBdcEntity.getId(), 1);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(transferConfig.getUrl() + "httpapi/approve/receiveCourseInfo", request, String.class);
            String result = responseEntity.getBody();
            transferRequestRecordEntity.setResponse(result);
            transferRequestRecordEntity.setResponseTime(new Date());
            if (result.contains("请求成功") || result.contains("当前业务已")) {
                ythBdcEntity.setState(3);
                ythBdcService.updateById(ythBdcEntity);
                transferRequestRecordService.updateById(transferRequestRecordEntity);
            } else {
                ythBdcEntity.setException(result);
                ythBdcEntity.setState(-3);
                ythBdcService.updateById(ythBdcEntity);
                transferRequestRecordEntity.setException(result);
                transferRequestRecordService.updateById(transferRequestRecordEntity);
            }
        } catch (Exception e) {
            logger.error("推送数据异常", e);
            transferRequestRecordEntity.setException(e.getMessage());
            transferRequestRecordEntity.setResponseTime(new Date());
            transferRequestRecordService.updateById(transferRequestRecordEntity);
        }
    }

    private void getItemInfo(Map map, TransferAuditItemEntity transferAuditItemEntity) throws JsonProcessingException {
        Map json = new HashMap(16);
        Map param = new HashMap(16);
        param.put("AREA_CODE", transferAuditItemEntity.getAreaCode());
        param.put("TIME_STAMP", "0");
        param.put("TASK_STATE", "1");
        param.put("IS_HISTORY", "0");
        param.put("TASK_CODE", transferAuditItemEntity.getTaskCode().trim());
        json.put("param", param);

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<String>(objectMapper.writeValueAsString(json), headers);
        Map result = restTemplate.postForEntity(transferConfig.getUrl() + "getareaaudititemdata?access_token=" + tokenUtils.getAccessToken(), formEntity, Map.class).getBody();
        Map selectItem = null;
        List<Map> list = ((List) ((Map) result.get("data")).get("list"));
        for (Map temp : list) {
            Map item = (Map) temp.get("AUDIT_ITEM");
            if (StringUtils.isBlank(item.get("ywcode").toString())) {
                if (item.get("task_code").equals(transferAuditItemEntity.getTaskCode().trim())) {
                    selectItem = temp;
                }
            } else {
                if (item.get("ywcode").equals(transferAuditItemEntity.getYwcode().trim())) {
                    selectItem = temp;
                }
            }
        }
        Map item = (Map) selectItem.get("AUDIT_ITEM");
        map.put("SXBM", StringUtils.isBlank(item.get("ywcode").toString()) ? item.get("task_code") : item.get("ywcode"));
        ((Map) map.get("GDBSSPCL")).put("SXBM_SHORT", item.get("task_code"));
        ((Map) map.get("GDBSSPCL")).put("SXBM", item.get("task_code"));
        ((Map) map.get("GDBSSPCL")).put("SPRZWDM", "/");
        ((Map) map.get("GDBSSPCL")).put("SPRZWMC", "/");
    }


}
