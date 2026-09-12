package com.ruoyi.business.contract.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.contract.domain.BizPaymentPlan;
import com.ruoyi.business.contract.domain.BizPaymentRecord;
import com.ruoyi.business.contract.service.IBizPaymentService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 收款管理（计划/登记/冲正/减免）
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/biz/payment")
public class BizPaymentController extends BaseController
{
    @Autowired
    private IBizPaymentService paymentService;

    /**
     * 收款计划列表（按客户聚合，同一客户一条记录）
     */
    @PreAuthorize("@ss.hasPermi('biz:payment:list')")
    @GetMapping("/plan/list")
    public TableDataInfo planList(BizPaymentPlan plan)
    {
        startPage();
        List<BizPaymentPlan> list = paymentService.selectCustomerPlanSummary(plan);
        return getDataTable(list);
    }

    /**
     * 合同的四期收款计划
     */
    @PreAuthorize("@ss.hasPermi('biz:payment:plan')")
    @GetMapping("/plan/contract/{contractId}")
    public AjaxResult plansOfContract(@PathVariable Long contractId)
    {
        return success(paymentService.selectPlansByContractId(contractId));
    }

    /**
     * 调整收款计划 body: [ { planId, contractId, ratio, planDate }, ... ]
     */
    @PreAuthorize("@ss.hasPermi('biz:payment:planEdit')")
    @Log(title = "收款计划调整", businessType = BusinessType.UPDATE)
    @PostMapping("/plan/adjust")
    public AjaxResult adjust(@RequestBody List<BizPaymentPlan> plans)
    {
        paymentService.adjustPlans(plans);
        return success();
    }

    /**
     * 收款记录列表
     */
    @PreAuthorize("@ss.hasPermi('biz:payment:list')")
    @GetMapping("/record/list")
    public TableDataInfo recordList(BizPaymentRecord record)
    {
        startPage();
        List<BizPaymentRecord> list = paymentService.selectRecordList(record);
        return getDataTable(list);
    }

    /**
     * 收款登记
     */
    @PreAuthorize("@ss.hasPermi('biz:payment:register')")
    @RepeatSubmit(interval = 3000)
    @Log(title = "收款登记", businessType = BusinessType.INSERT)
    @PostMapping("/register")
    public AjaxResult register(@RequestBody BizPaymentRecord record)
    {
        return toAjax(paymentService.registerPayment(record));
    }

    /**
     * 收款冲正 body: { paymentId, reverseReason }
     */
    @PreAuthorize("@ss.hasPermi('biz:payment:reverse')")
    @RepeatSubmit(interval = 3000)
    @Log(title = "收款冲正", businessType = BusinessType.UPDATE)
    @PostMapping("/reverse")
    public AjaxResult reverse(@RequestBody Map<String, Object> body)
    {
        Long paymentId = Long.parseLong(String.valueOf(body.get("paymentId")));
        String reverseReason = body.get("reverseReason") == null ? "" : String.valueOf(body.get("reverseReason"));
        return toAjax(paymentService.reversePayment(paymentId, reverseReason));
    }

    /**
     * 减免申请 body: { planId, reduceAmount, reason }
     */
    @PreAuthorize("@ss.hasPermi('biz:payment:reduce')")
    @Log(title = "收款减免申请", businessType = BusinessType.INSERT)
    @PostMapping("/reduce")
    public AjaxResult reduce(@RequestBody Map<String, Object> body)
    {
        Long planId = Long.parseLong(String.valueOf(body.get("planId")));
        java.math.BigDecimal reduceAmount = new java.math.BigDecimal(String.valueOf(body.get("reduceAmount")));
        String reason = body.get("reason") == null ? "" : String.valueOf(body.get("reason"));
        paymentService.applyReduce(planId, reduceAmount, reason);
        return success();
    }
}
