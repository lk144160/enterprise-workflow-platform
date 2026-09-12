package com.ruoyi.business.contract.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.business.contract.domain.BizPaymentPlan;

/**
 * 收款计划 Mapper 接口
 *
 * @author renovationops
 */
public interface BizPaymentPlanMapper
{
    public BizPaymentPlan selectBizPaymentPlanById(Long planId);

    public List<BizPaymentPlan> selectPlansByContractId(Long contractId);

    public List<BizPaymentPlan> selectBizPaymentPlanList(BizPaymentPlan plan);

    /** 按客户聚合的收款计划汇总（同一客户一条记录） */
    public List<BizPaymentPlan> selectCustomerPlanSummary(BizPaymentPlan plan);

    /** 到期与逾期未收期次（待收款且计划收款日已到，提醒扫描用） */
    public List<BizPaymentPlan> selectDueRemindList();

    public int insertBizPaymentPlan(BizPaymentPlan plan);

    public int updateBizPaymentPlan(BizPaymentPlan plan);

    /** 按合同与触发类型查询未结清期次（业务触发用） */
    public List<BizPaymentPlan> selectPendingPlansByTrigger(@Param("contractId") Long contractId, @Param("triggerType") String triggerType);

    /** 合同下未结清期次数量（归档校验用：状态0/1的期次） */
    public int countUnsettledPlans(Long contractId);

    /** 合同下未作废期次置为作废（终止用） */
    public int invalidatePlansByContractId(Long contractId);
}
