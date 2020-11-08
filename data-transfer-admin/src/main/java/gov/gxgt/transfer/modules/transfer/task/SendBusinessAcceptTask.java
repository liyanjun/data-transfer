package gov.gxgt.transfer.modules.transfer.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import gov.gxgt.transfer.modules.transfer.asyn.event.BusinessAcceptEvent;
import gov.gxgt.transfer.modules.yth.entity.YthBdcEntity;
import gov.gxgt.transfer.modules.yth.service.YthBdcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 办件系统检查发送业务受理信息
 *
 * @author liyanjun
 */
@Component("sendBusinessAcceptTask")
public class SendBusinessAcceptTask {
    private Logger logger = LoggerFactory.getLogger(SendBusinessAcceptTask.class);

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private YthBdcService ythBdcService;

    public void run(String temp) {
        // 一次查30条
        List<YthBdcEntity> ythBdcEntityList = ythBdcService.list(new QueryWrapper<YthBdcEntity>().
                eq("STATE", 1).
                isNotNull("AREA_CODE").
                isNotNull("DATA_SL").
                lt("rownum", 30));
        for (YthBdcEntity ythBdcEntity: ythBdcEntityList) {
            applicationContext.publishEvent(new BusinessAcceptEvent(ythBdcEntity.getId()));
        }
    }
}
