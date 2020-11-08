package gov.gxgt.transfer.modules.transfer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gov.gxgt.transfer.common.utils.PageUtils;
import gov.gxgt.transfer.modules.transfer.entity.TransferAuditItemEntity;

import java.util.Map;

/**
 * ${comments}
 *
 * @author liyanjun
 * @email 
 * @date 2020-06-29 14:53:57
 */
public interface TransferAuditItemService extends IService<TransferAuditItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

