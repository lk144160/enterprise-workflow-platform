package com.ruoyi.business.contract.service;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.business.contract.domain.BizPaymentPlan;
import com.ruoyi.business.contract.domain.BizPaymentRecord;

/**
 * 收款服务接口（计划/登记/冲正/减免）
 *
 * @author renovationops
 */
public interface IBizPaymentService
{
    // ==================== 触发联动 ====================
    /**
     * 里程碑事件触发期次到期
     *
     * @param contractId 合同ID
     * @param triggerType contract_effect/design_finish/disclosure_confirm/finish_register
     * @param bizId 触发里程碑的业务单据ID
     */
    void triggerPlans(Long contractId, String triggerType, Long bizId);

    // ==================== 计划 ====================
    public List<BizPaymentPlan> selectPlanList(BizPaymentPlan plan);

    /** 按客户聚合的收款计划汇总（同一客户一条记录） */
    public List<BizPaymentPlan> selectCustomerPlanSummary(BizPaymentPlan plan);

    public List<BizPaymentPlan> selectPlansByContractId(Long contractId);

    /** 调整收款计划（比例合计须100，重算应收金额） */
    public void adjustPlans(List<BizPaymentPlan> plans);

    // ==================== 收款登记 ====================
    public int registerPayment(BizPaymentRecord record);

    /** 冲正 */
    public int reversePayment(Long paymentId, String reverseReason);

    /** 减免申请（发起审批） */
    public void applyReduce(Long planId, BigDecimal reduceAmount, String reason);

    public List<BizPaymentRecord> selectRecordList(BizPaymentRecord record);

    public List<BizPaymentRecord> selectRecordsByContractId(Long contractId);

    public BizPaymentRecord selectRecordById(Long paymentId);
}
