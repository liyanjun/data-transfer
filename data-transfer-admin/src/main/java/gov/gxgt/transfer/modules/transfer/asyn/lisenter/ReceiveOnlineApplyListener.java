package gov.gxgt.transfer.modules.transfer.asyn.lisenter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gxgt.transfer.modules.transfer.asyn.event.BusinessAcceptEvent;
import gov.gxgt.transfer.modules.transfer.asyn.event.RecerviOnlineApplyEvent;
import gov.gxgt.transfer.modules.transfer.config.TransferConfig;
import gov.gxgt.transfer.modules.transfer.entity.InCatalogEntity;
import gov.gxgt.transfer.modules.transfer.entity.TransferAuditItemEntity;
import gov.gxgt.transfer.modules.transfer.entity.TransferRequestRecordEntity;
import gov.gxgt.transfer.modules.transfer.service.InCatalogService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.*;

/**
 * @author liyanjun
 */
@Component
public class ReceiveOnlineApplyListener {


    private static final Logger logger = LoggerFactory.getLogger(ReceiveOnlineApplyListener.class);

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

    @Autowired
    private InCatalogService inCatalogService;

    @Async
    @EventListener
    @Transactional
    public void onApplicationEvent(RecerviOnlineApplyEvent recerviOnlineApplyEvent) throws IOException, DocumentException {
        TransferRequestRecordEntity transferRequestRecordEntity = null;
        String accessToken = tokenUtils.getAccessToken();
        // 锁行，开始发送申报信息
        YthBdcEntity ythBdcEntity = ythBdcService.getOne(new QueryWrapper<YthBdcEntity>().eq("ID", recerviOnlineApplyEvent.getSource()));
        if (ythBdcEntity.getState() != 0) {
            // 已推送，不管了
            return;
        }
        // TODO 这里事项名称要搞定
        InCatalogEntity inCatalogEntity = inCatalogService.getOne(new QueryWrapper<InCatalogEntity>().
                eq("CANTONCODE", ythBdcEntity.getAreaCode()).eq("NAME", "抵押权首次登记").le("rownum", 1));
        if (inCatalogEntity == null) {
            logger.error(ythBdcEntity.getId() + "：找不到相应的事项。");
            ythBdcEntity.setException(ythBdcEntity.getId() + "：接口查询不到相应的事项。");
            ythBdcEntity.setState(-1);
            ythBdcService.updateById(ythBdcEntity);
            return;
        }
        Map map = objectMapper.readValue(ythBdcEntity.getDataSb(), Map.class);
        map = getItemInfo(map, inCatalogEntity);
        if (map == null) {
            logger.error(ythBdcEntity.getId() + "：找不到相应的事项。");
            ythBdcEntity.setException(ythBdcEntity.getId() + "：接口查询不到相应的事项。");
            ythBdcEntity.setState(-1);
            ythBdcService.updateById(ythBdcEntity);
            return;
        }
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + XMLUtils.multilayerMapToXml(map, false, "APPROVEDATAINFO");
        MultiValueMap requestMap = new LinkedMultiValueMap(16);
        requestMap.put("access_token", Collections.singletonList(accessToken));
        requestMap.put("accessToken", Collections.singletonList(tokenUtils.getToken()));
        requestMap.put("xmlStr", Collections.singletonList(xml.replace("SXBM_SHORT ", "SXBM_SHORT").replace("&lt;", "<").replace("&gt;", ">")));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(requestMap, headers);
        try {
            // 保存发送记录
            transferRequestRecordEntity = transferRequestRecordService.saveRequest(objectMapper.writeValueAsString(map), ythBdcEntity.getId(), 1);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(transferConfig.getUrl() + "httpapi/approve/getOnlineApply", request, String.class);
            String result = responseEntity.getBody();
            transferRequestRecordEntity.setResponse(result);
            transferRequestRecordEntity.setResponseTime(new Date());
            if (result.contains("请求成功") || result.contains("当前业务已")) {
                ythBdcEntity.setState(1);
                ythBdcService.updateById(ythBdcEntity);
                transferRequestRecordService.updateById(transferRequestRecordEntity);
            } else {
                ythBdcEntity.setException(result);
                ythBdcEntity.setState(-1);
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

    private Map getItemInfo(Map map, InCatalogEntity inCatalogEntity) throws JsonProcessingException {
        Map json = new HashMap(16);
        Map param = new HashMap(16);
        param.put("AREA_CODE", inCatalogEntity.getCantoncode());
        param.put("TIME_STAMP", "0");
        param.put("TASK_STATE", "1");
        param.put("IS_HISTORY", "0");
        param.put("TASK_CODE", inCatalogEntity.getCode().trim());
        json.put("param", param);

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<String>(objectMapper.writeValueAsString(json), headers);
        Map result = restTemplate.postForEntity(transferConfig.getUrl() + "getareaaudititemdata?access_token=" + tokenUtils.getAccessToken(), formEntity, Map.class).getBody();
//        logger.debug(objectMapper.writeValueAsString(result));
        if ("未找到匹配的信息!".equals(((Map) (result.get("STATUS"))).get("TEXT"))) {
            map = null;
            return map;
        }
        Map selectItem = null;
        List<Map> list = ((List) ((Map) result.get("data")).get("list"));
        for (Map temp : list) {
            Map item = (Map) temp.get("AUDIT_ITEM");
            if (item.get("task_code").equals(inCatalogEntity.getCode().trim())) {
                selectItem = temp;
            }
        }
        for (Map temp : list) {
            Map item = (Map) temp.get("AUDIT_ITEM");
            if (item.get("ywcode") != null && StringUtils.isNotBlank(item.get("ywcode").toString())) {
                if (inCatalogEntity.getChildcode() != null && item.get("ywcode").equals(inCatalogEntity.getChildcode().trim())) {
                    selectItem = temp;
                }
            }
        }
        if (selectItem == null) {
            map = null;
            return map;
        }
        Map item = (Map) selectItem.get("AUDIT_ITEM");
        List<Map> meterials = (List) selectItem.get("AUDIT_MATERIAL");
        Object otherItem = selectItem.get("AUDIT_ITEM_CONDITION");
//        map.put("SBLSH ", "shenbaoceshiceshiceshiceshi");
        map.put("SXBM", StringUtils.isBlank(item.get("ywcode").toString()) ? item.get("task_code") : item.get("ywcode"));
        map.put("SXBM_SHORT ", item.get("task_code"));
        map.put("SXBM_SHORT", item.get("task_code"));
        map.put("SXMC", item.get("task_name") + "自然资源厅测试");
        map.put("YWBLQHDM", map.get("YWBLQHDM") + "000000");
        map.put("SXBBBM", item.get("item_id"));
        map.put("SXBDBM", item.get("rowguid"));
        map.put("SXQXBM", otherItem instanceof Map ? ((Map) otherItem).get("rowguid") : "事项情形编码");
        map.put("SXQXMC", otherItem instanceof Map ? ((Map) otherItem).get("condition_name") : "");
        String mString = "";
        for (Map material : meterials) {
            Map m = new HashMap(16);
            m.put("STUFF_SEQ", UUID.randomUUID());
            m.put("SBLSH_SHORT", map.get("SBLSH_SHORT"));
            m.put("SBLSH", map.get("SBLSH"));
            m.put("WJLX", "0");
            m.put("CLLX", "0");
            m.put("CLSL", "1");
            m.put("ATTACH_NAME", material.get("material_name"));
            m.put("ATTACH_ID", "");
            m.put("ATTACH_PATH", "");
            m.put("CLMC", material.get("material_name"));
            mString += XMLUtils.multilayerMapToXml(m, false, "MATERIAL");
        }
        map.put("MATERIALDATA", mString);
        map.remove("MATERIAL");
        return map;
    }

}