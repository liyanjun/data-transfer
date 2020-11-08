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

import gov.gxgt.transfer.modules.transfer.entity.TransferAuditItemEntity;
import gov.gxgt.transfer.modules.transfer.service.TransferAuditItemService;



/**
 * ${comments}
 *
 * @author liyanjun
 * @email 
 * @date 2020-06-29 14:53:57
 */
@RestController
@RequestMapping("transfer/transferaudititem")
public class TransferAuditItemController {
    @Autowired
    private TransferAuditItemService transferAuditItemService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("transfer:transferaudititem:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = transferAuditItemService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{taskCode}")
    @RequiresPermissions("transfer:transferaudititem:info")
    public R info(@PathVariable("taskCode") String taskCode){
        TransferAuditItemEntity transferAuditItem = transferAuditItemService.getById(taskCode);

        return R.ok().put("transferAuditItem", transferAuditItem);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("transfer:transferaudititem:save")
    public R save(@RequestBody TransferAuditItemEntity transferAuditItem){
        transferAuditItemService.save(transferAuditItem);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("transfer:transferaudititem:update")
    public R update(@RequestBody TransferAuditItemEntity transferAuditItem){
        ValidatorUtils.validateEntity(transferAuditItem);
        transferAuditItemService.updateById(transferAuditItem);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("transfer:transferaudititem:delete")
    public R delete(@RequestBody String[] taskCodes){
        transferAuditItemService.removeByIds(Arrays.asList(taskCodes));

        return R.ok();
    }

}
