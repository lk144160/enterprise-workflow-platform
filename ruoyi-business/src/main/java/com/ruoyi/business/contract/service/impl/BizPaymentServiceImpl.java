package com.ruoyi.business.contract.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.contract.domain.BizContract;
import com.ruoyi.business.contract.domain.BizPaymentPlan;
import com.ruoyi.business.contract.domain.BizPaymentRecord;
import com.ruoyi.business.contract.mapper.BizContractMapper;
import com.ruoyi.business.contract.mapper.BizPaymentPlanMapper;
import com.ruoyi.business.contract.mapper.BizPaymentRecordMapper;
import com.ruoyi.business.contract.service.IBizPaymentService;
import com.ruoyi.business.contract.service.PaymentConstants;
import com.ruoyi.business.flow.mapper.FlowInstanceMapper;
import com.ruoyi.business.flow.service.FlowCallback;
import com.ruoyi.business.flow.service.FlowConstants;
import com.ruoyi.business.flow.service.IFlowEngineService;
import com.ruoyi.business.message.service.ISysMessageService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 收款服务实现（计划/登记/冲正/减免 + 减免审批回调）
 *
 * @author renovationops
 */
@Service
public class BizPaymentServiceImpl implements IBizPaymentService, FlowCallback
{
    @Autowired
    private BizPaymentPlanMapper planMapper;

    @Autowired
    private BizPaymentRecordMapper recordMapper;

    @Autowired
    private BizContractMapper contractMapper;

    @Autowired
    private IFlowEngineService flowEngineService;

    @Autowired
    private FlowInstanceMapper instanceMapper;

    @Autowired
    private ISysMessageService messageService;

    // ==================== 触发联动 ====================

    @Override
    @Transactional
    public void triggerPlans(Long contractId, String triggerType, Long bizId)
    {
        List<BizPaymentPlan> plans = planMapper.selectPendingPlansByTrigger(contractId, triggerType);
        for (BizPaymentPlan plan : plans)
        {
            BizPaymentPlan update = new BizPaymentPlan();
            update.setPlanId(plan.getPlanId());
            update.setStatus(PaymentConstants.PLAN_STATUS_WAITING);
            update.setDueTime(new Date());
            update.setTriggerBizId(bizId);
            update.setUpdateBy(SecurityUtils.getUsername());
            planMapper.updateBizPaymentPlan(update);
        }
    }

    // ==================== 计划 ====================

    @Override
    public List<BizPaymentPlan> selectPlanList(BizPaymentPlan plan)
    {
        List<BizPaymentPlan> list = planMapper.selectBizPaymentPlanList(plan);
        fillRemain(list);
        return list;
    }

    @Override
    public List<BizPaymentPlan> selectCustomerPlanSummary(BizPaymentPlan plan)
    {
        return planMapper.selectCustomerPlanSummary(plan);
    }

    @Override
    public List<BizPaymentPlan> selectPlansByContractId(Long contractId)
    {
        List<BizPaymentPlan> list = planMapper.selectPlansByContractId(contractId);
        fillRemain(list);
        return list;
    }

    @Override
    @Transactional
    public void adjustPlans(List<BizPaymentPlan> plans)
    {
        if (StringUtils.isEmpty(plans))
        {
            throw new ServiceException("收款计划不能为空");
        }
        // 期次名称任何状态均可修改；比例/金额/收款日仅未到期/待收款期次可调整
        Map<Long, BizPaymentPlan> oldPlans = new HashMap<>();
        BigDecimal ratioSum = BigDecimal.ZERO;
        for (BizPaymentPlan plan : plans)
        {
            BizPaymentPlan old = planMapper.selectBizPaymentPlanById(plan.getPlanId());
            if (old == null)
            {
                throw new ServiceException("收款期次不存在");
            }
            oldPlans.put(plan.getPlanId(), old);
            if (isAdjustable(old.getStatus()))
            {
                if (plan.getRatio() == null || plan.getRatio().compareTo(BigDecimal.ZERO) <= 0)
                {
                    throw new ServiceException("期次比例必须大于0");
                }
                ratioSum = ratioSum.add(plan.getRatio());
            }
            else
            {
                // 不可调整期次按原比例参与合计校验
                ratioSum = ratioSum.add(old.getRatio());
            }
        }
        if (ratioSum.compareTo(new BigDecimal("100")) != 0)
        {
            throw new ServiceException("四期比例合计必须等于100%，当前合计：" + ratioSum + "%");
        }
        for (BizPaymentPlan plan : plans)
        {
            BizPaymentPlan old = oldPlans.get(plan.getPlanId());
            BizPaymentPlan update = new BizPaymentPlan();
            update.setPlanId(plan.getPlanId());
            update.setPeriodName(plan.getPeriodName());
            update.setUpdateBy(SecurityUtils.getUsername());
            if (isAdjustable(old.getStatus()))
            {
                BizContract contract = contractMapper.selectBizContractById(plan.getContractId());
                if (contract == null)
                {
                    throw new ServiceException("合同不存在");
                }
                BigDecimal planAmount = contract.getContractAmount().multiply(plan.getRatio())
                        .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
                update.setRatio(plan.getRatio());
                update.setPlanAmount(planAmount);
                update.setPlanDate(plan.getPlanDate());
            }
            planMapper.updateBizPaymentPlan(update);
        }
    }

    /** 未到期/待收款期次可调整比例与收款日；其余状态仅可改名 */
    private boolean isAdjustable(String status)
    {
        return PaymentConstants.PLAN_STATUS_PENDING.equals(status)
                || PaymentConstants.PLAN_STATUS_WAITING.equals(status);
    }

    // ==================== 收款登记 ====================

    @Override
    @Transactional
    public int registerPayment(BizPaymentRecord record)
    {
        BizPaymentPlan plan = mustGetPlan(record.getPlanId());
        if (!PaymentConstants.PLAN_STATUS_WAITING.equals(plan.getStatus()))
        {
            throw new ServiceException("期次【" + plan.getPeriodName() + "】当前状态不可登记收款");
        }
        if (record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("实收金额必须大于0");
        }
        if (record.getPayTime() == null)
        {
            record.setPayTime(new Date());
        }
        BigDecimal remain = remainOf(plan);
        // 实收与剩余应收存在差额时，偏差说明必填
        if (record.getAmount().compareTo(remain) != 0 && StringUtils.isEmpty(record.getDeviationNote()))
        {
            throw new ServiceException("实收金额与剩余应收不一致，必须填写偏差说明");
        }
        record.setContractId(plan.getContractId());
        record.setPeriodNo(plan.getPeriodNo());
        record.setOperatorId(SecurityUtils.getUserId());
        record.setCreateBy(SecurityUtils.getUsername());
        int rows = recordMapper.insertBizPaymentRecord(record);

        // 更新期次累计实收与结清状态
        BigDecimal received = nvl(plan.getReceiveAmount()).add(record.getAmount());
        updatePlanAfterChange(plan, received, plan.getReduceAmount());

        // 更新合同累计已收冗余
        addContractPaid(plan.getContractId(), record.getAmount());
        return rows;
    }

    @Override
    @Transactional
    public int reversePayment(Long paymentId, String reverseReason)
    {
        BizPaymentRecord origin = recordMapper.selectBizPaymentRecordById(paymentId);
        if (origin == null)
        {
            throw new ServiceException("收款记录不存在");
        }
        if (origin.getAmount() == null || origin.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("冲正记录不可再次冲正");
        }
        if (StringUtils.isEmpty(reverseReason))
        {
            throw new ServiceException("冲正原因必填");
        }
        BizPaymentPlan plan = mustGetPlan(origin.getPlanId());
        // 插入负数冲正记录
        BizPaymentRecord reverse = new BizPaymentRecord();
        reverse.setContractId(origin.getContractId());
        reverse.setPlanId(origin.getPlanId());
        reverse.setPeriodNo(origin.getPeriodNo());
        reverse.setAmount(origin.getAmount().negate());
        reverse.setPayType(origin.getPayType());
        reverse.setPayTime(new Date());
        reverse.setReverseOfPaymentId(paymentId);
        reverse.setReverseReason(reverseReason);
        reverse.setOperatorId(SecurityUtils.getUserId());
        reverse.setCreateBy(SecurityUtils.getUsername());
        int rows = recordMapper.insertBizPaymentRecord(reverse);

        // 回退期次累计实收与结清状态
        BigDecimal received = nvl(plan.getReceiveAmount()).subtract(origin.getAmount());
        updatePlanAfterChange(plan, received, plan.getReduceAmount());

        // 回退合同累计已收
        addContractPaid(plan.getContractId(), origin.getAmount().negate());
        return rows;
    }

    @Override
    @Transactional
    public void applyReduce(Long planId, BigDecimal reduceAmount, String reason)
    {
        BizPaymentPlan plan = mustGetPlan(planId);
        if (!PaymentConstants.PLAN_STATUS_WAITING.equals(plan.getStatus()))
        {
            throw new ServiceException("仅待收款期次可申请减免");
        }
        if (reduceAmount == null || reduceAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("减免金额必须大于0");
        }
        BigDecimal remain = remainOf(plan);
        if (reduceAmount.compareTo(remain) > 0)
        {
            throw new ServiceException("减免金额不能超过剩余应收 " + remain + " 元");
        }
        BizContract contract = contractMapper.selectBizContractById(plan.getContractId());
        String title = "收款减免 " + (contract == null ? "" : contract.getContractNo()) + " " + plan.getPeriodName();
        flowEngineService.startFlow(FlowConstants.PAYMENT_REDUCE_APPROVAL, FlowConstants.BIZ_TYPE_PAYMENT_REDUCE,
                planId, title, reduceAmount);
    }

    @Override
    public List<BizPaymentRecord> selectRecordList(BizPaymentRecord record)
    {
        return recordMapper.selectBizPaymentRecordList(record);
    }

    @Override
    public List<BizPaymentRecord> selectRecordsByContractId(Long contractId)
    {
        return recordMapper.selectRecordsByContractId(contractId);
    }

    @Override
    public BizPaymentRecord selectRecordById(Long paymentId)
    {
        return recordMapper.selectBizPaymentRecordById(paymentId);
    }

    // ==================== 减免审批回调（bizId = planId） ====================

    @Override
    public String bizType()
    {
        return FlowConstants.BIZ_TYPE_PAYMENT_REDUCE;
    }

    @Override
    @Transactional
    public void onApproved(Long bizId)
    {
        // 减免通过：读取最近实例上携带的减免金额写入期次
        com.ruoyi.business.flow.domain.FlowInstance latest = instanceMapper.selectLatestInstanceByBiz(
                FlowConstants.BIZ_TYPE_PAYMENT_REDUCE, bizId);
        BizPaymentPlan plan = mustGetPlan(bizId);
        BigDecimal reduceAmount = latest != null && latest.getBizAmount() != null ? latest.getBizAmount() : BigDecimal.ZERO;
        BigDecimal reduced = nvl(plan.getReduceAmount()).add(reduceAmount);
        updatePlanAfterChange(plan, plan.getReceiveAmount(), reduced);
    }

    @Override
    public void onRejected(Long bizId)
    {
        // 减免驳回：无需处理，期次保持待收款
    }

    @Override
    public void onCanceled(Long bizId)
    {
        // 减免撤销：无需处理
    }

    // ==================== 私有方法 ====================

    private BizPaymentPlan mustGetPlan(Long planId)
    {
        BizPaymentPlan plan = planMapper.selectBizPaymentPlanById(planId);
        if (plan == null)
        {
            throw new ServiceException("收款期次不存在");
        }
        return plan;
    }

    /**
     * 期次金额变化后统一处理状态：
     * 实收+减免 >= 应收 → 结清/减免；否则回到待收款
     */
    private void updatePlanAfterChange(BizPaymentPlan plan, BigDecimal received, BigDecimal reduced)
    {
        BigDecimal total = nvl(received).add(nvl(reduced));
        BigDecimal target = nvl(plan.getPlanAmount());
        BizPaymentPlan update = new BizPaymentPlan();
        update.setPlanId(plan.getPlanId());
        update.setReceiveAmount(nvl(received));
        if (reduced != null)
        {
            update.setReduceAmount(reduced);
        }
        if (total.compareTo(target) >= 0)
        {
            // 有减免成分标记为已减免，否则已结清
            update.setStatus(nvl(reduced).compareTo(BigDecimal.ZERO) > 0
                    ? PaymentConstants.PLAN_STATUS_REDUCED : PaymentConstants.PLAN_STATUS_SETTLED);
            update.setSettleTime(new Date());
            planMapper.updateBizPaymentPlan(update);
            // 上一期结清后，自动将下一期（未到期）置为待收款
            activateNextPeriod(plan);
        }
        else
        {
            update.setStatus(PaymentConstants.PLAN_STATUS_WAITING);
            planMapper.updateBizPaymentPlan(update);
        }
    }

    /** 上一期结清后，自动将下一期（未到期状态）置为待收款，便于直接收款 */
    private void activateNextPeriod(BizPaymentPlan plan)
    {
        List<BizPaymentPlan> plans = planMapper.selectPlansByContractId(plan.getContractId());
        Integer nextNo = (plan.getPeriodNo() == null ? null : plan.getPeriodNo() + 1);
        if (nextNo == null)
        {
            return;
        }
        for (BizPaymentPlan p : plans)
        {
            if (nextNo.equals(p.getPeriodNo()) && PaymentConstants.PLAN_STATUS_PENDING.equals(p.getStatus()))
            {
                BizPaymentPlan update = new BizPaymentPlan();
                update.setPlanId(p.getPlanId());
                update.setStatus(PaymentConstants.PLAN_STATUS_WAITING);
                update.setDueTime(new Date());
                planMapper.updateBizPaymentPlan(update);
                break;
            }
        }
    }

    private void addContractPaid(Long contractId, BigDecimal delta)
    {
        BizContract contract = contractMapper.selectBizContractById(contractId);
        if (contract != null)
        {
            BizContract update = new BizContract();
            update.setContractId(contractId);
            update.setPaidAmount(nvl(contract.getPaidAmount()).add(delta));
            contractMapper.updateBizContract(update);
        }
    }

    /** 剩余应收 = 应收 - 实收 - 减免 */
    private BigDecimal remainOf(BizPaymentPlan plan)
    {
        return nvl(plan.getPlanAmount()).subtract(nvl(plan.getReceiveAmount())).subtract(nvl(plan.getReduceAmount()));
    }

    private BigDecimal nvl(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void fillRemain(List<BizPaymentPlan> plans)
    {
        for (BizPaymentPlan plan : plans)
        {
            plan.setRemainAmount(remainOf(plan));
        }
    }
}
