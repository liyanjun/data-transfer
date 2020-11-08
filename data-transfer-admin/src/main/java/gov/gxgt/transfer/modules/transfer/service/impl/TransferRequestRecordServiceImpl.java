package gov.gxgt.transfer.modules.transfer.service.impl;

import gov.gxgt.transfer.common.utils.PageUtils;
import gov.gxgt.transfer.common.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import gov.gxgt.transfer.modules.transfer.dao.TransferRequestRecordDao;
import gov.gxgt.transfer.modules.transfer.entity.TransferRequestRecordEntity;
import gov.gxgt.transfer.modules.transfer.service.TransferRequestRecordService;


@Service("transferRequestRecordService")
public class TransferRequestRecordServiceImpl extends ServiceImpl<TransferRequestRecordDao, TransferRequestRecordEntity> implements TransferRequestRecordService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TransferRequestRecordEntity> page = this.page(
                new Query<TransferRequestRecordEntity>().getPage(params),
                new QueryWrapper<TransferRequestRecordEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public TransferRequestRecordEntity saveRequest(String request, String id, Integer type) {
        TransferRequestRecordEntity transferRequestRecordEntity = new TransferRequestRecordEntity();
        transferRequestRecordEntity.setRequest(request);
        transferRequestRecordEntity.setCreateTime(new Date());
        transferRequestRecordEntity.setSourceId(id);
        transferRequestRecordEntity.setType(type);
        baseMapper.insert(transferRequestRecordEntity);
        return transferRequestRecordEntity;
    }

}
