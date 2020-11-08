package gov.gxgt.transfer.modules.transfer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import gov.gxgt.transfer.common.utils.PageUtils;
import gov.gxgt.transfer.modules.transfer.entity.TransferRequestRecordEntity;

import java.util.Map;

/**
 * 数据同步通讯记录
 *
 * @author liyanjun
 * @email 
 * @date 2020-06-24 10:31:26
 */
public interface TransferRequestRecordService extends IService<TransferRequestRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);

    TransferRequestRecordEntity saveRequest(String request, String id, Integer type);
}

