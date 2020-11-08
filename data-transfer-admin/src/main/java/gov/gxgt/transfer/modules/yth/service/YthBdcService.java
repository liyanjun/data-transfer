package gov.gxgt.transfer.modules.yth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gov.gxgt.transfer.common.utils.PageUtils;
import gov.gxgt.transfer.modules.yth.entity.YthBdcEntity;

import java.util.Map;

/**
 * 一体化不动产推送信息
 *
 * @author liyanjun
 * @email 
 * @date 2020-06-24 15:20:05
 */
public interface YthBdcService extends IService<YthBdcEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

