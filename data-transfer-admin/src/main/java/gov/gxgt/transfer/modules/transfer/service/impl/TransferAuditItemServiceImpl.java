package gov.gxgt.transfer.modules.transfer.service.impl;

import gov.gxgt.transfer.common.utils.PageUtils;
import gov.gxgt.transfer.common.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import gov.gxgt.transfer.modules.transfer.dao.TransferAuditItemDao;
import gov.gxgt.transfer.modules.transfer.entity.TransferAuditItemEntity;
import gov.gxgt.transfer.modules.transfer.service.TransferAuditItemService;


@Service("transferAuditItemService")
public class TransferAuditItemServiceImpl extends ServiceImpl<TransferAuditItemDao, TransferAuditItemEntity> implements TransferAuditItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TransferAuditItemEntity> page = this.page(
                new Query<TransferAuditItemEntity>().getPage(params),
                new QueryWrapper<TransferAuditItemEntity>()
        );

        return new PageUtils(page);
    }

}
