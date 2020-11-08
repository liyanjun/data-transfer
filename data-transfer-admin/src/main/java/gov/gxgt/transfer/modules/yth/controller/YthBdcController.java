package gov.gxgt.transfer.modules.yth.controller;

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

import gov.gxgt.transfer.modules.yth.entity.YthBdcEntity;
import gov.gxgt.transfer.modules.yth.service.YthBdcService;



/**
 * 一体化不动产推送信息
 *
 * @author liyanjun
 * @email 
 * @date 2020-06-24 15:20:05
 */
@RestController
@RequestMapping("yth/ythbdc")
public class YthBdcController {
    @Autowired
    private YthBdcService ythBdcService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("yth:ythbdc:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = ythBdcService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("yth:ythbdc:info")
    public R info(@PathVariable("id") String id){
        YthBdcEntity ythBdc = ythBdcService.getById(id);

        return R.ok().put("ythBdc", ythBdc);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("yth:ythbdc:save")
    public R save(@RequestBody YthBdcEntity ythBdc){
        ythBdcService.save(ythBdc);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("yth:ythbdc:update")
    public R update(@RequestBody YthBdcEntity ythBdc){
        ValidatorUtils.validateEntity(ythBdc);
        ythBdcService.updateById(ythBdc);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("yth:ythbdc:delete")
    public R delete(@RequestBody String[] ids){
        ythBdcService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
