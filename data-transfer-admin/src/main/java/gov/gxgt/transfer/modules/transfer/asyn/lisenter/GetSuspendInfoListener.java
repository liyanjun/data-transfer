package gov.gxgt.transfer.modules.transfer.asyn.lisenter;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gxgt.transfer.common.utils.DateUtils;
import gov.gxgt.transfer.modules.transfer.asyn.event.BusinessAcceptEvent;
import gov.gxgt.transfer.modules.transfer.asyn.event.GetSuspendInfoEvent;
import gov.gxgt.transfer.modules.transfer.config.TransferConfig;
import gov.gxgt.transfer.modules.transfer.entity.InCatalogEntity;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyanjun
 */
@Component
public class GetSuspendInfoListener {


    private static final Logger logger = LoggerFactory.getLogger(GetSuspendInfoListener.class);

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
    public void onApplicationEvent(GetSuspendInfoEvent getSuspendInfoEvent) throws IOException, DocumentException {
        TransferRequestRecordEntity transferRequestRecordEntity = null;
        String accessToken = tokenUtils.getAccessToken();
        // 锁行，开始发送受理信息
        YthBdcEntity ythBdcEntity = ythBdcService.getOne(new QueryWrapper<YthBdcEntity>().eq("ID", getSuspendInfoEvent.getSource()));
        if (ythBdcEntity.getState() != 5) {
            // 已挂起不管了
            return;
        }
        InCatalogEntity inCatalogEntity = inCatalogService.getOne(new QueryWrapper<InCatalogEntity>().select("*").
                eq("\"CantonCode\"", ythBdcEntity.getAreaCode().replace("451302", "451300")).
                eq("\"TaskState\"", 1).
                eq("\"Name\"", ythBdcEntity.getSpsx()).le("rownum", 1));
        if (ythBdcEntity.getAreaCode().startsWith("4514")) {
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
            logger.error(target + "：找不到相应的事项。");
            return;
        }
//        MultiValueMap infoRequestMap = new LinkedMultiValueMap(16);
//        infoRequestMap.put("access_token", Collections.singletonList(accessToken));
//        infoRequestMap.put("accessToken", Collections.singletonList(tokenUtils.getToken()));
//        infoRequestMap.put("xmlStr", Collections.singletonList("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//                "<APPROVEDATAINFO>\n" +
//                "\t<SBLSH_SHORT>" + ythBdcEntity.getLsh() + "</SBLSH_SHORT>\n" +
//                "<SXBM>" + (StringUtils.isNotBlank(inCatalogEntity.getChildCode()) ? inCatalogEntity.getChildCode() : inCatalogEntity.getCode()) + "</SXBM>\n" +
//                "</APPROVEDATAINFO>\n"));
//        HttpHeaders infoHeaders = new HttpHeaders();
//        infoHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        HttpEntity<MultiValueMap<String, String>> infoRequest = new HttpEntity<MultiValueMap<String, String>>(infoRequestMap, infoHeaders);
//        ResponseEntity<String> infoResponseEntity = restTemplate.postForEntity(transferConfig.getUrl() + "httpapi/approve/getApplyInfo", infoRequest, String.class);
//        String limitDate = infoResponseEntity.getBody().substring(infoResponseEntity.getBody().indexOf("<LIMIT_TIME>") + 12, infoResponseEntity.getBody().indexOf("<LIMIT_TIME>") + 31);
        Map sl = objectMapper.readValue(ythBdcEntity.getDataSl(), Map.class);
        String limitDate =  DateUtils.format(DateUtils.addDateDays(DateUtils.stringToDate(((Map) sl.get("SHOULI")).get("YWSLSJ").toString(), DateUtils.DATE_TIME_PATTERN), inCatalogEntity.getDaysOfPromise()), DateUtils.DATE_TIME_PATTERN);
        Map bj = objectMapper.readValue(ythBdcEntity.getDataBj(), Map.class);
        int minute = 0 - DateUtils.daysBettwen(((Map) bj.get("SPBANJIE")).get("BJSJ").toString(), limitDate);
        if(minute < 0){
            ythBdcEntity.setState(7);
            ythBdcService.updateById(ythBdcEntity);
            return;
        }
        Map map = getItemInfo(ythBdcEntity, objectMapper.readValue(ythBdcEntity.getDataBj(), Map.class), objectMapper.readValue(ythBdcEntity.getDataSl(), Map.class), inCatalogEntity, limitDate);

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
            transferRequestRecordEntity = transferRequestRecordService.saveRequest(objectMapper.writeValueAsString(map), ythBdcEntity.getId(), 5);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(transferConfig.getUrl() + "httpapi/approve/getSuspendInfo", request, String.class);
            String result = responseEntity.getBody();
            transferRequestRecordEntity.setResponse(result);
            transferRequestRecordEntity.setResponseTime(new Date());
            if (result.contains("请求成功") || result.contains("当前业务已")) {
                ythBdcEntity.setState(6);
                ythBdcService.updateById(ythBdcEntity);
                transferRequestRecordService.updateById(transferRequestRecordEntity);
            } else {
                ythBdcEntity.setException(result);
                ythBdcEntity.setState(-6);
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

    private Map getItemInfo(YthBdcEntity ythBdcEntity, Map bj, Map sl, InCatalogEntity inCatalogEntity, String limitDate) throws JsonProcessingException {
        int minute = 0 - DateUtils.daysBettwen(((Map) bj.get("SPBANJIE")).get("BJSJ").toString(), limitDate);
        if(minute < 0){
            minute = 0 - minute;
        }
        Map map = new HashMap();
        map.put("SXBM", StringUtils.isNotBlank(inCatalogEntity.getChildCode()) ? inCatalogEntity.getChildCode() : inCatalogEntity.getCode());
        map.put("SBLSH_SHORT", ythBdcEntity.getLsh());
        map.put("SUSPENDTYPE", 0);
        Map subMap = new HashMap(32);
        subMap.put("SBLSH_SHORT", ythBdcEntity.getLsh());
        subMap.put("SBLSH", ythBdcEntity.getLsh());
        subMap.put("SXBM", map.get("SXBM"));
        subMap.put("SXBM_SHORT", map.get("SXBM"));
        subMap.put("SXQXBM", StringUtils.isNotBlank(inCatalogEntity.getRowGuid()) ? inCatalogEntity.getRowGuid() : "事项情形编码");
        subMap.put("XH", 1);
        subMap.put("TBCXZL", "B");
        subMap.put("TBCXZLMC", "正常业务办理挂起。");
        subMap.put("TBCXQDLY", "正常的受理项目后等待业主补齐材料");
        subMap.put("SQNR", "正常的受理项目后等待业主补齐材料。");

        // 特别程序开始日期
        subMap.put("TBCXKSRQ", ((Map) sl.get("SHOULI")).get("YWSLSJ"));
        // 特别程序批准人
        subMap.put("TBCXPZR", ((Map) bj.get("SPBANJIE")).get("SPRXM"));
        // 特别程序时限，挂起一阵时间。。。
        subMap.put("TBCXSX", (minute / 24 / 60) + 3);
        subMap.put("TBCXSXDW", "Z");
        subMap.put("XZQHDM", ythBdcEntity.getAreaCode());
        map.put("SPTEBIESHENQING", subMap);
        return map;
    }

}
