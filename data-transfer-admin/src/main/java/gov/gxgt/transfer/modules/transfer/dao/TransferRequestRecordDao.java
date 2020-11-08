package gov.gxgt.transfer.modules.transfer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import gov.gxgt.transfer.modules.transfer.entity.TransferRequestRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据同步通讯记录
 * 
 * @author liyanjun
 * @email 
 * @date 2020-06-24 10:31:26
 */
@Mapper
public interface TransferRequestRecordDao extends BaseMapper<TransferRequestRecordEntity> {
	
}
