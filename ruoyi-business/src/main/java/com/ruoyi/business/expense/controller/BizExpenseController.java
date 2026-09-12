package com.ruoyi.business.expense.controller;

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
import com.ruoyi.business.expense.domain.BizExpense;
import com.ruoyi.business.expense.service.IBizExpenseService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 报销管理
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/biz/expense")
public class BizExpenseController extends BaseController
{
    @Autowired
    private IBizExpenseService expenseService;

    /**
     * 报销单列表
     */
    @PreAuthorize("@ss.hasPermi('biz:expense:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizExpense expense)
    {
        startPage();
        List<BizExpense> list = expenseService.selectBizExpenseList(expense);
        return getDataTable(list);
    }

    /**
     * 报销单详情
     */
    @PreAuthorize("@ss.hasPermi('biz:expense:query')")
    @GetMapping("/{expenseId}")
    public AjaxResult getInfo(@PathVariable Long expenseId)
    {
        return success(expenseService.selectBizExpenseById(expenseId));
    }

    /**
     * 新增报销单（默认为本人申请）
     */
    @PreAuthorize("@ss.hasPermi('biz:expense:add')")
    @Log(title = "报销单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizExpense expense)
    {
        return toAjax(expenseService.insertBizExpense(expense));
    }

    /**
     * 修改报销单
     */
    @PreAuthorize("@ss.hasPermi('biz:expense:edit')")
    @Log(title = "报销单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizExpense expense)
    {
        return toAjax(expenseService.updateBizExpense(expense));
    }

    /**
     * 删除报销单
     */
    @PreAuthorize("@ss.hasPermi('biz:expense:remove')")
    @Log(title = "报销单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{expenseIds}")
    public AjaxResult remove(@PathVariable Long[] expenseIds)
    {
        return toAjax(expenseService.deleteBizExpenseByIds(expenseIds));
    }

    /**
     * 提交审批（校验发票附件张数）
     */
    @PreAuthorize("@ss.hasPermi('biz:expense:submit')")
    @Log(title = "报销单提交审批", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{expenseId}")
    public AjaxResult submit(@PathVariable Long expenseId)
    {
        expenseService.submitExpense(expenseId);
        return success();
    }

    /**
     * 打款登记
     */
    @PreAuthorize("@ss.hasPermi('biz:expense:pay')")
    @Log(title = "报销单打款登记", businessType = BusinessType.UPDATE)
    @PostMapping("/pay/{expenseId}")
    public AjaxResult pay(@PathVariable Long expenseId)
    {
        expenseService.payExpense(expenseId);
        return success();
    }

    /**
     * 作废
     */
    @PreAuthorize("@ss.hasPermi('biz:expense:edit')")
    @Log(title = "报销单作废", businessType = BusinessType.UPDATE)
    @PostMapping("/invalidate/{expenseId}")
    public AjaxResult invalidate(@PathVariable Long expenseId)
    {
        expenseService.invalidateExpense(expenseId);
        return success();
    }
}
