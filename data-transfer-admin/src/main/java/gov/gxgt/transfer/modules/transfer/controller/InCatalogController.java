package gov.gxgt.transfer.modules.transfer.controller;

import java.util.Arrays;
import java.util.Map;

import gov.gxgt.transfer.common.utils.PageUtils;
import gov.gxgt.transfer.common.utils.R;
import gov.gxgt.transfer.common.validator.ValidatorUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.gxgt.transfer.modules.transfer.entity.InCatalogEntity;
import gov.gxgt.transfer.modules.transfer.service.InCatalogService;



/**
 * ${comments}
 *
 * @author liyanjun
 * @email 
 * @date 2020-11-08 22:05:29
 */
@RestController
@RequestMapping("transfer/incatalog")
public class InCatalogController {
    @Autowired
    private InCatalogService inCatalogService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("transfer:incatalog:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = inCatalogService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("transfer:incatalog:info")
    public R info(@PathVariable("id") String id){
        InCatalogEntity inCatalog = inCatalogService.getById(id);

        return R.ok().put("inCatalog", inCatalog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("transfer:incatalog:save")
    public R save(@RequestBody InCatalogEntity inCatalog){
        inCatalogService.save(inCatalog);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("transfer:incatalog:update")
    public R update(@RequestBody InCatalogEntity inCatalog){
        ValidatorUtils.validateEntity(inCatalog);
        inCatalogService.updateById(inCatalog);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("transfer:incatalog:delete")
    public R delete(@RequestBody String[] ids){
        inCatalogService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
