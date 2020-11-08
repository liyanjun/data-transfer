package gov.gxgt.transfer.modules.transfer.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gxgt.transfer.modules.transfer.asyn.event.BusinessAcceptEvent;
import gov.gxgt.transfer.modules.transfer.entity.TransferRequestRecordEntity;
import gov.gxgt.transfer.modules.transfer.service.TransferRequestRecordService;
import gov.gxgt.transfer.modules.yth.entity.YthBdcEntity;
import gov.gxgt.transfer.modules.yth.service.YthBdcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 办件系统检查发送业务受理信息
 *
 * @author liyanjun
 */
@Component("test")
public class TestTask {
    private Logger logger = LoggerFactory.getLogger(TestTask.class);

    @Autowired
    private ApplicationContext applicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TransferRequestRecordService transferRequestRecordService;

    public void run(String temp) throws IOException {
        // 一次查100条
        List<TransferRequestRecordEntity> ythBdcEntityList = transferRequestRecordService.list(new QueryWrapper<TransferRequestRecordEntity>().
                like("RESPONSE", "成功").like("REQUEST", "{\"ISPRECHECK"));
        for (TransferRequestRecordEntity transferRequestRecordEntity: ythBdcEntityList) {
            Map map = objectMapper.readValue(transferRequestRecordEntity.getRequest(), Map.class);
            System.out.println(map.get("SBLSH"));
        }
    }
}
