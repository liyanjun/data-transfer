package gov.gxgt.transfer.modules.transfer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gov.gxgt.transfer.common.utils.PageUtils;
import gov.gxgt.transfer.modules.transfer.entity.InCatalogEntity;

import java.util.Map;

/**
 * ${comments}
 *
 * @author liyanjun
 * @email 
 * @date 2020-11-08 22:05:29
 */
public interface InCatalogService extends IService<InCatalogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

