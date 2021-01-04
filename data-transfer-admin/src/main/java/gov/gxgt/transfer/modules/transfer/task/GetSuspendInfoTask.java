package gov.gxgt.transfer.modules.transfer.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import gov.gxgt.transfer.modules.transfer.asyn.event.BusinessAcceptEvent;
import gov.gxgt.transfer.modules.transfer.asyn.event.GetSuspendInfoEndEvent;
import gov.gxgt.transfer.modules.transfer.asyn.event.GetSuspendInfoEvent;
import gov.gxgt.transfer.modules.yth.entity.YthBdcEntity;
import gov.gxgt.transfer.modules.yth.service.YthBdcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 超期挂起任务
 *
 * @author liyanjun
 */
@Component("getSuspendInfoTask")
public class GetSuspendInfoTask {
    private Logger logger = LoggerFactory.getLogger(GetSuspendInfoTask.class);

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private YthBdcService ythBdcService;

    public void run(String temp) {
        // 一次查30条
        List<YthBdcEntity> ythBdcEntityList = ythBdcService.list(new QueryWrapper<YthBdcEntity>().
                eq("STATE", 5).
                isNotNull("AREA_CODE").
                lt("rownum", 50));
        for (YthBdcEntity ythBdcEntity: ythBdcEntityList) {
            applicationContext.publishEvent(new GetSuspendInfoEvent(ythBdcEntity.getId()));
        }
    }
}
