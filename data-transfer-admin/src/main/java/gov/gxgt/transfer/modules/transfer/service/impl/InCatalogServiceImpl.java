package gov.gxgt.transfer.modules.transfer.service.impl;

import gov.gxgt.transfer.common.utils.PageUtils;
import gov.gxgt.transfer.common.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import gov.gxgt.transfer.modules.transfer.dao.InCatalogDao;
import gov.gxgt.transfer.modules.transfer.entity.InCatalogEntity;
import gov.gxgt.transfer.modules.transfer.service.InCatalogService;


@Service("inCatalogService")
public class InCatalogServiceImpl extends ServiceImpl<InCatalogDao, InCatalogEntity> implements InCatalogService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<InCatalogEntity> page = this.page(
                new Query<InCatalogEntity>().getPage(params),
                new QueryWrapper<InCatalogEntity>()
        );

        return new PageUtils(page);
    }

}
