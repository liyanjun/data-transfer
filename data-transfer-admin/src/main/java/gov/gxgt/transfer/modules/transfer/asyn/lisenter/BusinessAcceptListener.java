package gov.gxgt.transfer.modules.transfer.asyn.lisenter;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.xstream.XStream;
import gov.gxgt.transfer.common.utils.DateUtils;
import gov.gxgt.transfer.modules.transfer.asyn.event.BusinessAcceptEvent;
import gov.gxgt.transfer.modules.transfer.config.TransferConfig;
import gov.gxgt.transfer.modules.transfer.entity.InCatalogEntity;
import gov.gxgt.transfer.modules.transfer.entity.TransferAuditItemEntity;
import gov.gxgt.transfer.modules.transfer.entity.TransferRequestRecordEntity;
import gov.gxgt.transfer.modules.transfer.service.InCatalogService;
import gov.gxgt.transfer.modules.transfer.service.TransferAuditItemService;
import gov.gxgt.transfer.modules.transfer.service.TransferRequestRecordService;
import gov.gxgt.transfer.modules.transfer.utils.MapEntryConverter;
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
public class BusinessAcceptListener {


    private static final Logger logger = LoggerFactory.getLogger(BusinessAcceptListener.class);

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
    public void onApplicationEvent(BusinessAcceptEvent businessAcceptEvent) throws IOException, DocumentException {
        TransferRequestRecordEntity transferRequestRecordEntity = null;
        String accessToken = tokenUtils.getAccessToken();
        // 锁行，开始发送受理信息
        YthBdcEntity ythBdcEntity = ythBdcService.getOne(new QueryWrapper<YthBdcEntity>().eq("ID", businessAcceptEvent.getSource()));
        if (ythBdcEntity.getState() != 1) {
            // 已推送，不管了
            return;
        }
        InCatalogEntity inCatalogEntity = inCatalogService.getOne(new QueryWrapper<InCatalogEntity>().select("*").
                eq("\"CantonCode\"", ythBdcEntity.getAreaCode().replace("451302", "451300").replace("451021", "451003")).
                eq("\"TaskState\"", 1).
                eq("\"IsDeleted\"", 0).
                eq("\"Name\"", ythBdcEntity.getSpsx()).le("rownum", 1));
        if (ythBdcEntity.getAreaCode().startsWith("4514") && !"451400".equals(ythBdcEntity.getAreaCode())) {
            inCatalogEntity = inCatalogService.getOne(new QueryWrapper<InCatalogEntity>().select("*").
                    eq("\"CantonCode\"", ythBdcEntity.getAreaCode()).
                    eq("\"TaskState\"", 1).
                    in("\"DeptCode\"", "12451422MB0173307F",
                            "114514810077235433",
                            "11451425007768442A",
                            "11451424007763115W",
                            "12452133MB0430238X",
                            "11451421MB1521254M",
                            "11451402090700990A"
                    ).
                    eq("\"Name\"", ythBdcEntity.getSpsx()).le("rownum", 1));
        }
        String target = ythBdcEntity.getId() + "@" + ythBdcEntity.getAreaCode() + "@" + ythBdcEntity.getSpsx();
        if (inCatalogEntity == null) {
            ythBdcEntity.setState(-2);
            ythBdcService.updateById(ythBdcEntity);
            logger.error(target + "：找不到相应的事项。");
            return;
        }
        Map map = objectMapper.readValue(ythBdcEntity.getDataSl(), Map.class);
        getItemInfo(map, inCatalogEntity);
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
            transferRequestRecordEntity = transferRequestRecordService.saveRequest(objectMapper.writeValueAsString(map), ythBdcEntity.getId(), 2);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(transferConfig.getUrl() + "httpapi/approve/receiveBusinessAccept", request, String.class);
            String result = responseEntity.getBody();
            transferRequestRecordEntity.setResponse(result);
            transferRequestRecordEntity.setResponseTime(new Date());
            if (result.contains("请求成功") || result.contains("当前业务已")) {
                ythBdcEntity.setState(2);
                ythBdcService.updateById(ythBdcEntity);
                transferRequestRecordService.updateById(transferRequestRecordEntity);
            } else if(!result.contains("接口令牌不存在") && !result.contains("当前业务已")) {
                ythBdcEntity.setException(result);
                ythBdcEntity.setState(-2);
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

    private void getItemInfo(Map map, InCatalogEntity inCatalogEntity) throws JsonProcessingException {
//        Map json = new HashMap(16);
//        Map param = new HashMap(16);
//        param.put("AREA_CODE", inCatalogEntity.getCantonCode());
//        param.put("TIME_STAMP", "0");
//        param.put("TASK_STATE", "1");
//        param.put("IS_HISTORY", "0");
//        param.put("TASK_CODE", inCatalogEntity.getCode().trim());
//        json.put("param", param);
//
//        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//        headers.setContentType(type);
//        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
//        HttpEntity<String> formEntity = new HttpEntity<String>(objectMapper.writeValueAsString(json), headers);
//        Map result = restTemplate.postForEntity(transferConfig.getUrl() + "getareaaudititemdata?access_token=" + tokenUtils.getAccessToken(), formEntity, Map.class).getBody();
//        List<Map> list = ((List) ((Map) result.get("data")).get("list"));
        map.put("SXBM", StringUtils.isNotBlank(inCatalogEntity.getChildCode()) ? inCatalogEntity.getChildCode() : inCatalogEntity.getCode());
        ((Map) map.get("YUSHEN")).put("YWYSZT", "1");
        ((Map) map.get("YUSHEN")).put("YWYSRBM", ((Map) map.get("SHOULI")).get("YWSLRBM"));
        ((Map) map.get("YUSHEN")).put("YWYSRMC", ((Map) map.get("SHOULI")).get("YWSLRMC"));
        ((Map) map.get("YUSHEN")).put("YWYSQHBM", ((Map) map.get("SHOULI")).get("YWSLQHBM"));
        ((Map) map.get("YUSHEN")).put("YWYSYJ", "无");
        ((Map) map.get("YUSHEN")).put("BZ", ((Map) map.get("SHOULI")).get("BZ"));
        ((Map) map.get("YUSHEN")).put("YWYSQHMC", ((Map) map.get("SHOULI")).get("YWSLQHMC"));
        ((Map) map.get("YUSHEN")).put("YWYSBMBM", ((Map) map.get("SHOULI")).get("YWSLBMBM"));
        ((Map) map.get("YUSHEN")).put("YWYSSJ", ((Map) map.get("SHOULI")).get("YWSLSJ"));
        ((Map) map.get("YUSHEN")).put("YWYSBMMC", ((Map) map.get("SHOULI")).get("YWSLBMMC"));

    }

}
