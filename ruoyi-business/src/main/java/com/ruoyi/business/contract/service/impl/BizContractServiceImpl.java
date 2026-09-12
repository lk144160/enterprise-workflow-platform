package com.ruoyi.business.contract.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.common.BusinessNoService;
import com.ruoyi.business.attachment.service.IBizAttachmentService;
import com.ruoyi.business.contract.domain.BizContract;
import com.ruoyi.business.contract.domain.BizPaymentPlan;
import com.ruoyi.business.contract.mapper.BizContractMapper;
import com.ruoyi.business.contract.mapper.BizPaymentPlanMapper;
import com.ruoyi.business.contract.service.IBizContractService;
import com.ruoyi.business.contract.service.IBizPaymentService;
import com.ruoyi.business.contract.service.PaymentConstants;
import com.ruoyi.business.crm.service.ICrmCustomerService;
import com.ruoyi.business.flow.domain.FlowInstance;
import com.ruoyi.business.flow.service.FlowCallback;
import com.ruoyi.business.flow.service.FlowConstants;
import com.ruoyi.business.flow.service.IFlowEngineService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 合同服务实现（含审批流回调与四期计划联动）
 *
 * @author renovationops
 */
@Service
public class BizContractServiceImpl implements IBizContractService, FlowCallback
{
    /** 合同状态 */
    public static final String STATUS_DRAFT = "0";
    public static final String STATUS_AUDITING = "1";
    public static final String STATUS_EFFECTIVE = "2";
    public static final String STATUS_FINISHED = "3";
    public static final String STATUS_ARCHIVED = "4";
    public static final String STATUS_TERMINATED = "5";

    @Autowired
    private BizContractMapper contractMapper;

    @Autowired
    private BizPaymentPlanMapper planMapper;

    @Autowired
    private BusinessNoService businessNoService;

    @Autowired
    private IBizAttachmentService attachmentService;

    @Autowired
    private IFlowEngineService flowEngineService;

    @Autowired
    private ICrmCustomerService customerService;

    @Autowired
    @Lazy
    private IBizPaymentService paymentService;

    @Override
    public BizContract selectBizContractById(Long contractId)
    {
        BizContract contract = contractMapper.selectBizContractById(contractId);
        if (contract != null)
        {
            contract.setPlans(planMapper.selectPlansByContractId(contractId));
        }
        return contract;
    }

    @Override
    public List<BizContract> selectBizContractList(BizContract bizContract)
    {
        return contractMapper.selectBizContractList(bizContract);
    }

    @Override
    @Transactional
    public int insertBizContract(BizContract contract)
    {
        if (contract.getContractAmount() == null || contract.getContractAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("合同金额必须大于0");
        }
        contract.setContractNo(businessNoService.nextNo("HT"));
        contract.setStatus(STATUS_DRAFT);
        contract.setDeptId(SecurityUtils.getLoginUser().getUser().getDeptId());
        contract.setCreateBy(SecurityUtils.getUsername());
        int rows = contractMapper.insertBizContract(contract);
        // 自动生成四期收款计划
        for (String[] period : PaymentConstants.DEFAULT_PERIODS)
        {
            BizPaymentPlan plan = new BizPaymentPlan();
            plan.setContractId(contract.getContractId());
            plan.setPeriodNo(Integer.parseInt(period[0]));
            plan.setPeriodName(period[1]);
            plan.setRatio(new BigDecimal(period[2]));
            plan.setPlanAmount(calcPlanAmount(contract.getContractAmount(), new BigDecimal(period[2])));
            plan.setTriggerType(period[3]);
            plan.setStatus(PaymentConstants.PLAN_STATUS_PENDING);
            plan.setCreateBy(SecurityUtils.getUsername());
            planMapper.insertBizPaymentPlan(plan);
        }
        return rows;
    }

    @Override
    @Transactional
    public int updateBizContract(BizContract contract)
    {
        BizContract exist = mustGet(contract.getContractId());
        if (!STATUS_DRAFT.equals(exist.getStatus()))
        {
            throw new ServiceException("仅待审批状态的合同可修改");
        }
        if (contract.getContractAmount() != null && contract.getContractAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("合同金额必须大于0");
        }
        contract.setUpdateBy(SecurityUtils.getUsername());
        int rows = contractMapper.updateBizContract(contract);
        // 金额变化时同步重算未结清期次应收
        if (contract.getContractAmount() != null && contract.getContractAmount().compareTo(exist.getContractAmount()) != 0)
        {
            List<BizPaymentPlan> plans = planMapper.selectPlansByContractId(contract.getContractId());
            for (BizPaymentPlan plan : plans)
            {
                if (PaymentConstants.PLAN_STATUS_PENDING.equals(plan.getStatus()))
                {
                    BizPaymentPlan update = new BizPaymentPlan();
                    update.setPlanId(plan.getPlanId());
                    update.setPlanAmount(calcPlanAmount(contract.getContractAmount(), plan.getRatio()));
                    planMapper.updateBizPaymentPlan(update);
                }
            }
        }
        return rows;
    }

    @Override
    @Transactional
    public int deleteBizContractByIds(Long[] contractIds)
    {
        for (Long contractId : contractIds)
        {
            BizContract exist = mustGet(contractId);
            if (!STATUS_DRAFT.equals(exist.getStatus()))
            {
                throw new ServiceException("合同【" + exist.getContractNo() + "】已进入流程，不允许删除");
            }
        }
        int rows = contractMapper.deleteBizContractByIds(contractIds);
        // 级联清理合同附件记录
        for (Long contractId : contractIds)
        {
            attachmentService.deleteByBiz(FlowConstants.BIZ_TYPE_CONTRACT, contractId);
        }
        return rows;
    }

    @Override
    @Transactional
    public void submitContract(Long contractId)
    {
        BizContract contract = mustGet(contractId);
        if (!STATUS_DRAFT.equals(contract.getStatus()))
        {
            throw new ServiceException("仅待审批状态的合同可提交");
        }
        BizContract update = new BizContract();
        update.setContractId(contractId);
        update.setStatus(STATUS_AUDITING);
        update.setUpdateBy(SecurityUtils.getUsername());
        contractMapper.updateBizContract(update);
        flowEngineService.startFlow(FlowConstants.CONTRACT_APPROVAL, FlowConstants.BIZ_TYPE_CONTRACT, contractId,
                "合同 " + contract.getContractNo() + " " + (contract.getCustomerName() == null ? "" : contract.getCustomerName()),
                contract.getContractAmount());
    }

    @Override
    @Transactional
    public void finishContract(Long contractId)
    {
        BizContract contract = mustGet(contractId);
        if (!STATUS_EFFECTIVE.equals(contract.getStatus()))
        {
            throw new ServiceException("仅已生效的合同可进行完工登记");
        }
        BizContract update = new BizContract();
        update.setContractId(contractId);
        update.setStatus(STATUS_FINISHED);
        update.setFinishDate(new Date());
        update.setUpdateBy(SecurityUtils.getUsername());
        contractMapper.updateBizContract(update);
        // 触发尾款期次到期
        paymentService.triggerPlans(contractId, PaymentConstants.TRIGGER_FINISH_REGISTER, contractId);
    }

    @Override
    @Transactional
    public void archiveContract(Long contractId)
    {
        BizContract contract = mustGet(contractId);
        if (!STATUS_FINISHED.equals(contract.getStatus()))
        {
            throw new ServiceException("仅已完工的合同可归档");
        }
        int unsettled = planMapper.countUnsettledPlans(contractId);
        if (unsettled > 0)
        {
            throw new ServiceException("存在未结清的收款期次，无法归档");
        }
        BizContract update = new BizContract();
        update.setContractId(contractId);
        update.setStatus(STATUS_ARCHIVED);
        update.setUpdateBy(SecurityUtils.getUsername());
        contractMapper.updateBizContract(update);
    }

    @Override
    @Transactional
    public void terminateContract(Long contractId, String reason)
    {
        BizContract contract = mustGet(contractId);
        if (!STATUS_EFFECTIVE.equals(contract.getStatus()) && !STATUS_FINISHED.equals(contract.getStatus()))
        {
            throw new ServiceException("仅已生效/已完工的合同可终止");
        }
        if (StringUtils.isEmpty(reason))
        {
            throw new ServiceException("终止原因必填");
        }
        // 未结清期次作废
        planMapper.invalidatePlansByContractId(contractId);
        BizContract update = new BizContract();
        update.setContractId(contractId);
        update.setStatus(STATUS_TERMINATED);
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setRemark("终止原因：" + reason);
        contractMapper.updateBizContract(update);
    }

    // ==================== 审批流回调 ====================

    @Override
    public String bizType()
    {
        return FlowConstants.BIZ_TYPE_CONTRACT;
    }

    @Override
    @Transactional
    public void onApproved(Long bizId)
    {
        BizContract update = new BizContract();
        update.setContractId(bizId);
        update.setStatus(STATUS_EFFECTIVE);
        update.setUpdateBy(SecurityUtils.getUsername());
        contractMapper.updateBizContract(update);
        // 合同生效：客户已签约 + 触发签约款到期
        BizContract contract = contractMapper.selectBizContractById(bizId);
        if (contract != null)
        {
            customerService.updateStatus(contract.getCustomerId(), "4");
            paymentService.triggerPlans(bizId, PaymentConstants.TRIGGER_CONTRACT_EFFECT, bizId);
        }
    }

    @Override
    @Transactional
    public void onRejected(Long bizId)
    {
        BizContract update = new BizContract();
        update.setContractId(bizId);
        update.setStatus(STATUS_DRAFT);
        update.setUpdateBy(SecurityUtils.getUsername());
        contractMapper.updateBizContract(update);
    }

    @Override
    @Transactional
    public void onCanceled(Long bizId)
    {
        onRejected(bizId);
    }

    // ==================== 私有方法 ====================

    private BizContract mustGet(Long contractId)
    {
        BizContract contract = contractMapper.selectBizContractById(contractId);
        if (contract == null)
        {
            throw new ServiceException("合同不存在");
        }
        return contract;
    }

    private BigDecimal calcPlanAmount(BigDecimal contractAmount, BigDecimal ratio)
    {
        return contractAmount.multiply(ratio).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
    }
}
