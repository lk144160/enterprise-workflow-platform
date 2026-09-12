package com.ruoyi.business.deposit.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.deposit.domain.BizDeposit;
import com.ruoyi.business.deposit.service.IBizDepositService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 定金管理
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/biz/deposit")
public class BizDepositController extends BaseController
{
    @Autowired
    private IBizDepositService depositService;

    /**
     * 定金列表
     */
    @PreAuthorize("@ss.hasPermi('biz:deposit:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizDeposit bizDeposit)
    {
        startPage();
        List<BizDeposit> list = depositService.selectBizDepositList(bizDeposit);
        return getDataTable(list);
    }

    /**
     * 客户已收未抵扣定金合计（客户卡片徽标用）
     */
    @PreAuthorize("@ss.hasPermi('biz:deposit:query')")
    @GetMapping("/receivedSum/{customerId}")
    public AjaxResult receivedSum(@PathVariable Long customerId)
    {
        return success(depositService.selectReceivedSumByCustomer(customerId));
    }

    /**
     * 定金详情
     */
    @PreAuthorize("@ss.hasPermi('biz:deposit:query')")
    @GetMapping("/{depositId}")
    public AjaxResult getInfo(@PathVariable Long depositId)
    {
        return success(depositService.selectBizDepositById(depositId));
    }

    /**
     * 新增定金
     */
    @PreAuthorize("@ss.hasPermi('biz:deposit:add')")
    @Log(title = "定金", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizDeposit bizDeposit)
    {
        return toAjax(depositService.insertBizDeposit(bizDeposit));
    }

    /**
     * 修改定金
     */
    @PreAuthorize("@ss.hasPermi('biz:deposit:edit')")
    @Log(title = "定金", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizDeposit bizDeposit)
    {
        return toAjax(depositService.updateBizDeposit(bizDeposit));
    }

    /**
     * 删除定金
     */
    @PreAuthorize("@ss.hasPermi('biz:deposit:remove')")
    @Log(title = "定金", businessType = BusinessType.DELETE)
    @DeleteMapping("/{depositIds}")
    public AjaxResult remove(@PathVariable Long[] depositIds)
    {
        return toAjax(depositService.deleteBizDepositByIds(depositIds));
    }

    /**
     * 签约抵扣
     */
    @PreAuthorize("@ss.hasPermi('biz:deposit:deduct')")
    @Log(title = "定金签约抵扣", businessType = BusinessType.UPDATE)
    @PostMapping("/deduct/{depositId}/{contractId}")
    public AjaxResult deduct(@PathVariable Long depositId, @PathVariable Long contractId)
    {
        return toAjax(depositService.deductDeposit(depositId, contractId));
    }

    /**
     * 发起退还审批
     */
    @PreAuthorize("@ss.hasPermi('biz:deposit:refund')")
    @Log(title = "定金退还申请", businessType = BusinessType.UPDATE)
    @PostMapping("/refund/{depositId}")
    public AjaxResult refund(@PathVariable Long depositId, @RequestBody BizDeposit body)
    {
        depositService.applyRefund(depositId, body.getRefundReason());
        return success();
    }
}
