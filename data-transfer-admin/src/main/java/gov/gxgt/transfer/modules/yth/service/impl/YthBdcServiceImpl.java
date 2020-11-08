package gov.gxgt.transfer.modules.yth.service.impl;

import gov.gxgt.transfer.common.utils.PageUtils;
import gov.gxgt.transfer.common.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import gov.gxgt.transfer.modules.yth.dao.YthBdcDao;
import gov.gxgt.transfer.modules.yth.entity.YthBdcEntity;
import gov.gxgt.transfer.modules.yth.service.YthBdcService;


@Service("ythBdcService")
public class YthBdcServiceImpl extends ServiceImpl<YthBdcDao, YthBdcEntity> implements YthBdcService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<YthBdcEntity> page = this.page(
                new Query<YthBdcEntity>().getPage(params),
                new QueryWrapper<YthBdcEntity>()
        );

        return new PageUtils(page);
    }

}
