package com.ruoyi.business.deposit.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.contract.domain.BizContract;
import com.ruoyi.business.contract.domain.BizPaymentPlan;
import com.ruoyi.business.contract.domain.BizPaymentRecord;
import com.ruoyi.business.contract.mapper.BizContractMapper;
import com.ruoyi.business.contract.mapper.BizPaymentPlanMapper;
import com.ruoyi.business.contract.mapper.BizPaymentRecordMapper;
import com.ruoyi.business.contract.service.PaymentConstants;
import com.ruoyi.business.deposit.domain.BizDeposit;
import com.ruoyi.business.deposit.mapper.BizDepositMapper;
import com.ruoyi.business.deposit.service.IBizDepositService;
import com.ruoyi.business.flow.service.FlowCallback;
import com.ruoyi.business.flow.service.FlowConstants;
import com.ruoyi.business.flow.service.IFlowEngineService;
import com.ruoyi.business.message.service.ISysMessageService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定金服务实现（登记/抵扣/退还审批回调）
 *
 * @author renovationops
 */
@Service
public class BizDepositServiceImpl implements IBizDepositService, FlowCallback
{
    private static final Logger log = LoggerFactory.getLogger(BizDepositServiceImpl.class);

    /** 定金状态：0已收定金 1已抵扣 2退还审批中 3已退还 */
    public static final String STATUS_RECEIVED = "0";
    public static final String STATUS_DEDUCTED = "1";
    public static final String STATUS_REFUNDING = "2";
    public static final String STATUS_REFUNDED = "3";

    @Autowired
    private BizDepositMapper depositMapper;

    @Autowired
    private BizContractMapper contractMapper;

    @Autowired
    private BizPaymentPlanMapper planMapper;

    @Autowired
    private BizPaymentRecordMapper recordMapper;

    @Autowired
    private IFlowEngineService flowEngineService;

    @Autowired
    private ISysMessageService messageService;

    @Override
    public BizDeposit selectBizDepositById(Long depositId)
    {
        return depositMapper.selectBizDepositById(depositId);
    }

    @Override
    public List<BizDeposit> selectBizDepositList(BizDeposit bizDeposit)
    {
        return depositMapper.selectBizDepositList(bizDeposit);
    }

    @Override
    public BigDecimal selectReceivedSumByCustomer(Long customerId)
    {
        return depositMapper.selectReceivedSumByCustomer(customerId);
    }

    @Override
    @Transactional
    public int insertBizDeposit(BizDeposit deposit)
    {
        if (deposit.getCustomerId() == null)
        {
            throw new ServiceException("请选择客户");
        }
        if (deposit.getAmount() == null || deposit.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("定金金额必须大于0");
        }
        if (deposit.getPayTime() == null)
        {
            deposit.setPayTime(new Date());
        }
        deposit.setDepositNo(generateDepositNo());
        deposit.setStatus(STATUS_RECEIVED);
        deposit.setCreateBy(SecurityUtils.getUsername());
        return depositMapper.insertBizDeposit(deposit);
    }

    @Override
    @Transactional
    public int updateBizDeposit(BizDeposit deposit)
    {
        BizDeposit exist = mustGet(deposit.getDepositId());
        if (!STATUS_RECEIVED.equals(exist.getStatus()))
        {
            throw new ServiceException("仅【已收定金】状态的记录可修改");
        }
        if (deposit.getAmount() != null && deposit.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("定金金额必须大于0");
        }
        deposit.setUpdateBy(SecurityUtils.getUsername());
        return depositMapper.updateBizDeposit(deposit);
    }

    @Override
    @Transactional
    public int deleteBizDepositByIds(Long[] depositIds)
    {
        for (Long id : depositIds)
        {
            BizDeposit exist = mustGet(id);
            if (!STATUS_RECEIVED.equals(exist.getStatus()))
            {
                throw new ServiceException("定金【" + exist.getDepositNo() + "】已抵扣或已退还，不可删除");
            }
        }
        return depositMapper.deleteBizDepositByIds(depositIds);
    }

    /**
     * 签约抵扣：定金全额转入所选合同第1期（签约款）收款
     * 生成收款记录 → 回写期次累计实收/状态 → 回写合同累计已收 → 定金转已抵扣并回填合同ID
     */
    @Override
    @Transactional
    public int deductDeposit(Long depositId, Long contractId)
    {
        BizDeposit deposit = mustGet(depositId);
        if (!STATUS_RECEIVED.equals(deposit.getStatus()))
        {
            throw new ServiceException("仅【已收定金】状态可签约抵扣");
        }
        BizContract contract = contractMapper.selectBizContractById(contractId);
        if (contract == null)
        {
            throw new ServiceException("合同不存在");
        }
        if (!"2".equals(contract.getStatus()))
        {
            throw new ServiceException("仅【已生效】合同可抵扣定金");
        }
        if (!contract.getCustomerId().equals(deposit.getCustomerId()))
        {
            throw new ServiceException("合同与定金不属于同一客户，不可抵扣");
        }
        // 找合同第1期（签约款）
        List<BizPaymentPlan> plans = planMapper.selectPlansByContractId(contractId);
        BizPaymentPlan firstPlan = plans.stream()
                .filter(p -> Integer.valueOf(1).equals(p.getPeriodNo()))
                .findFirst()
                .orElseThrow(() -> new ServiceException("合同未生成收款计划，无法抵扣"));
        if (PaymentConstants.PLAN_STATUS_SETTLED.equals(firstPlan.getStatus()))
        {
            throw new ServiceException("第1期【" + firstPlan.getPeriodName() + "】已结清，无需抵扣");
        }

        // 1. 生成收款记录（备注定金抵扣来源）
        BizPaymentRecord record = new BizPaymentRecord();
        record.setContractId(contractId);
        record.setPlanId(firstPlan.getPlanId());
        record.setPeriodNo(firstPlan.getPeriodNo());
        record.setAmount(deposit.getAmount());
        record.setPayType(deposit.getPayType());
        record.setPayTime(new Date());
        record.setDeviationNote("定金抵扣：" + deposit.getDepositNo());
        record.setOperatorId(SecurityUtils.getUserId());
        record.setCreateBy(SecurityUtils.getUsername());
        recordMapper.insertBizPaymentRecord(record);

        // 2. 回写期次累计实收与状态
        BigDecimal received = nvl(firstPlan.getReceiveAmount()).add(deposit.getAmount());
        BizPlanUpdater updater = new BizPlanUpdater();
        updater.update(firstPlan, received);

        // 3. 回写合同累计已收
        BizContract contractUpdate = new BizContract();
        contractUpdate.setContractId(contractId);
        contractUpdate.setPaidAmount(nvl(contract.getPaidAmount()).add(deposit.getAmount()));
        contractUpdate.setUpdateBy(SecurityUtils.getUsername());
        contractMapper.updateBizContract(contractUpdate);

        // 4. 定金转已抵扣
        BizDeposit update = new BizDeposit();
        update.setDepositId(depositId);
        update.setContractId(contractId);
        update.setStatus(STATUS_DEDUCTED);
        update.setUpdateBy(SecurityUtils.getUsername());
        return depositMapper.updateBizDeposit(update);
    }

    @Override
    @Transactional
    public void applyRefund(Long depositId, String reason)
    {
        BizDeposit deposit = mustGet(depositId);
        if (!STATUS_RECEIVED.equals(deposit.getStatus()))
        {
            throw new ServiceException("仅【已收定金】状态可申请退还");
        }
        if (StringUtils.isEmpty(reason))
        {
            throw new ServiceException("退还原因必填");
        }
        String title = "定金退还 " + deposit.getDepositNo() + " " + (deposit.getCustomerName() == null ? "" : deposit.getCustomerName());
        flowEngineService.startFlow(FlowConstants.DEPOSIT_REFUND_APPROVAL, FlowConstants.BIZ_TYPE_DEPOSIT_REFUND,
                depositId, title, deposit.getAmount());
        // 暂存退还原因，状态转【退还审批中】（阻断此期间的抵扣/再次退还，驳回时回退）
        BizDeposit update = new BizDeposit();
        update.setDepositId(depositId);
        update.setRefundReason(reason);
        update.setStatus(STATUS_REFUNDING);
        update.setUpdateBy(SecurityUtils.getUsername());
        depositMapper.updateBizDeposit(update);
    }

    // ==================== 退还审批回调（bizId = depositId） ====================

    @Override
    public String bizType()
    {
        return FlowConstants.BIZ_TYPE_DEPOSIT_REFUND;
    }

    @Override
    @Transactional
    public void onApproved(Long bizId)
    {
        BizDeposit deposit = mustGet(bizId);
        BizDeposit update = new BizDeposit();
        update.setDepositId(bizId);
        update.setStatus(STATUS_REFUNDED);
        update.setRefundTime(new Date());
        update.setUpdateBy("system");
        depositMapper.updateBizDeposit(update);
        log.info("定金[{}]退还审批通过，已转已退还", deposit.getDepositNo());
    }

    @Override
    @Transactional
    public void onRejected(Long bizId)
    {
        // 驳回：回到已收定金
        BizDeposit update = new BizDeposit();
        update.setDepositId(bizId);
        update.setStatus(STATUS_RECEIVED);
        update.setUpdateBy("system");
        depositMapper.updateBizDeposit(update);
    }

    @Override
    @Transactional
    public void onCanceled(Long bizId)
    {
        onRejected(bizId);
    }

    // ==================== 私有方法 ====================

    private BizDeposit mustGet(Long depositId)
    {
        BizDeposit deposit = depositMapper.selectBizDepositById(depositId);
        if (deposit == null)
        {
            throw new ServiceException("定金记录不存在");
        }
        return deposit;
    }

    /** DJ+yyyyMMdd+4位流水 */
    private String generateDepositNo()
    {
        String date = com.ruoyi.common.utils.DateUtils.dateTimeNow("yyyyMMdd");
        // 简单流水：当日已有数量+1（并发极低场景足够）
        BizDeposit query = new BizDeposit();
        List<BizDeposit> today = depositMapper.selectBizDepositList(query);
        return "DJ" + date + String.format("%04d", today.size() + 1);
    }

    private BigDecimal nvl(BigDecimal v)
    {
        return v == null ? BigDecimal.ZERO : v;
    }

    /**
     * 期次回写内部辅助：received 已收合计、结清判断（与 BizPaymentServiceImpl 逻辑一致）
     */
    private class BizPlanUpdater
    {
        void update(BizPaymentPlan plan, BigDecimal received)
        {
            BizPaymentPlan update = new BizPaymentPlan();
            update.setPlanId(plan.getPlanId());
            update.setReceiveAmount(received);
            // 剩余应收 = 计划 - 已收 - 减免；收满即结清
            BigDecimal remain = plan.getPlanAmount()
                    .subtract(received)
                    .subtract(nvl(plan.getReduceAmount()));
            if (remain.compareTo(BigDecimal.ZERO) <= 0)
            {
                update.setStatus(PaymentConstants.PLAN_STATUS_SETTLED);
                update.setSettleTime(new Date());
            }
            else if (PaymentConstants.PLAN_STATUS_PENDING.equals(plan.getStatus()))
            {
                update.setStatus(PaymentConstants.PLAN_STATUS_WAITING);
            }
            update.setUpdateBy(SecurityUtils.getUsername());
            planMapper.updateBizPaymentPlan(update);
        }
    }
}
