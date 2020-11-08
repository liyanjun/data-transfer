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

import gov.gxgt.transfer.modules.transfer.entity.TransferRequestRecordEntity;
import gov.gxgt.transfer.modules.transfer.service.TransferRequestRecordService;



/**
 * 数据同步通讯记录
 *
 * @author liyanjun
 * @email 
 * @date 2020-06-24 10:31:26
 */
@RestController
@RequestMapping("transfer/transferrequestrecord")
public class TransferRequestRecordController {
    @Autowired
    private TransferRequestRecordService transferRequestRecordService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("transfer:transferrequestrecord:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = transferRequestRecordService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("transfer:transferrequestrecord:info")
    public R info(@PathVariable("id") Integer id){
        TransferRequestRecordEntity transferRequestRecord = transferRequestRecordService.getById(id);

        return R.ok().put("transferRequestRecord", transferRequestRecord);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("transfer:transferrequestrecord:save")
    public R save(@RequestBody TransferRequestRecordEntity transferRequestRecord){
        transferRequestRecordService.save(transferRequestRecord);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("transfer:transferrequestrecord:update")
    public R update(@RequestBody TransferRequestRecordEntity transferRequestRecord){
        ValidatorUtils.validateEntity(transferRequestRecord);
        transferRequestRecordService.updateById(transferRequestRecord);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("transfer:transferrequestrecord:delete")
    public R delete(@RequestBody Integer[] ids){
        transferRequestRecordService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
