package gov.gxgt.transfer.modules.transfer.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import gov.gxgt.transfer.common.utils.DateUtils;
import gov.gxgt.transfer.modules.transfer.asyn.event.RecerviOnlineApplyEvent;
import gov.gxgt.transfer.modules.yth.entity.YthBdcEntity;
import gov.gxgt.transfer.modules.yth.service.YthBdcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 办件系统检查发送业务受理信息
 *
 * @author liyanjun
 */
@Component("receiveOnlineApplyTask")
public class ReceiveOnlineApplyTask {
    private Logger logger = LoggerFactory.getLogger(ReceiveOnlineApplyTask.class);

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private YthBdcService ythBdcService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void run(String temp) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        Map map = null;
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT W_BDC_JFXT.WFD_HOLIDAY.* FROM W_BDC_JFXT.WFD_HOLIDAY WHERE HOLIDAY_STARTDATE = TO_DATE('" + DateUtils.format(date, DateUtils.DATE_PATTERN) + " 00:00:00', 'YYYY-MM-DD HH24:MI:SS') ");
        if (!list.isEmpty()) {
            map = list.iterator().next();
        }
        if (map != null && !map.isEmpty() && "2".equals(map.get("HOLIDAY_TYPE"))) {
            // 节假日
            return;
        }
        if (week == 0 || week == 6) {
            if (map != null && !map.isEmpty() && "1".equals(map.get("HOLIDAY_TYPE"))) {
                // 节假日补班的日子，还是要推
            } else {
                // 周末且不用补班不推
                return;
            }
        }
//        List<YthBdcEntity> tempList = ythBdcService.list(new QueryWrapper<YthBdcEntity>().
//                lt("STATE", 0).
//                gt("CREATE_TIME", DateUtils.stringToDate("2021-03-31 00:00:18", DateUtils.DATE_TIME_PATTERN)).
//                isNotNull("AREA_CODE").
//                isNotNull("DATA_SB").
//                lt("rownum", 200));
//        for (YthBdcEntity ythBdcEntity : tempList) {
//            List<Map<String, Object>> tList = jdbcTemplate.queryForList("SELECT * FROM W_BDC_JFXT.TRANSFER_REQUEST_RECORD WHERE EXCEPTION is null AND SOURCE_ID = \'" + ythBdcEntity.getId() + "\' order by type");
//            if (tList.isEmpty()) {
//                ythBdcEntity.setState(0);
//                ythBdcService.updateById(ythBdcEntity);
//                continue;
//            }
//            if ("1".equals(tList.get(tList.size() - 1).get("TYPE").toString())) {
//                ythBdcEntity.setState(1);
//            }
//            if ("2".equals(tList.get(tList.size() - 1).get("TYPE").toString())) {
//                ythBdcEntity.setState(2);
//            }
//            if ("3".equals(tList.get(tList.size() - 1).get("TYPE").toString())) {
//                ythBdcEntity.setState(3);
//            }
//            if ("4".equals(tList.get(tList.size() - 1).get("TYPE").toString())) {
//                ythBdcEntity.setState(4);
//            }
//            if ("5".equals(tList.get(tList.size() - 1).get("TYPE").toString())) {
//                ythBdcEntity.setState(4);
//            }
//            if ("6".equals(tList.get(tList.size() - 1).get("TYPE").toString())) {
//                ythBdcEntity.setState(6);
//            }
//            if ("7".equals(tList.get(tList.size() - 1).get("TYPE").toString())) {
//                ythBdcEntity.setState(7);
//            }
//            ythBdcService.updateById(ythBdcEntity);
//        }
        // 一次查100条
        List<YthBdcEntity> ythBdcEntityList = ythBdcService.list(new QueryWrapper<YthBdcEntity>().
                eq("STATE", 0).
//                eq("AREA_CODE", 450500).
        isNotNull("AREA_CODE").
                        isNotNull("DATA_SB").
                        lt("rownum", 60));

        for (YthBdcEntity ythBdcEntity : ythBdcEntityList) {
            applicationContext.publishEvent(new RecerviOnlineApplyEvent(ythBdcEntity.getId()));
        }
    }
}
