package com.ruoyi.business.contract.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 收款计划对象 biz_payment_plan（四期期次）
 *
 * @author renovationops
 */
public class BizPaymentPlan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 期次ID */
    private Long planId;

    /** 合同ID */
    private Long contractId;

    /** 期次（1-4） */
    private Integer periodNo;

    /** 期次名称（签约款/设计款/施工款/尾款） */
    private String periodName;

    /** 比例（%，合计100） */
    private BigDecimal ratio;

    /** 应收金额 */
    private BigDecimal planAmount;

    /** 到期触发（contract_effect/design_finish/disclosure_confirm/finish_register） */
    private String triggerType;

    /** 触发里程碑业务单据ID */
    private Long triggerBizId;

    /** 计划收款日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate;

    /** 实际到期时间（里程碑达成时间） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dueTime;

    /** 状态（0未到期 1待收款 2已结清 3已减免 4已作废） */
    private String status;

    /** 累计实收（含冲正负数） */
    private BigDecimal receiveAmount;

    /** 减免金额 */
    private BigDecimal reduceAmount;

    /** 结清/减免时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date settleTime;

    /** 合同编号（展示） */
    private String contractNo;

    /** 客户姓名（展示） */
    private String customerName;

    /** 剩余应收（展示，服务端计算） */
    private BigDecimal remainAmount;

    /** 合同负责人user_id（展示，提醒扫描用） */
    private Long ownerId;

    /** ---- 客户维度汇总展示字段（收款计划列表按客户聚合） ---- */
    /** 客户ID */
    private Long customerId;

    /** 楼盘（展示） */
    private String estate;

    /** 客户负责人姓名（展示） */
    private String ownerName;

    /** 客户付款方式（字典 biz_pay_type，展示） */
    private String payMethod;

    /** 合同数 */
    private Integer contractCount;

    /** 合同金额合计 */
    private BigDecimal contractAmount;

    /** 期次总数 */
    private Integer periodCount;

    /** 已结清期次数（含减免/作废） */
    private Integer settledCount;

    /** 下期收款日（最早的待收款计划日） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date nextPlanDate;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public Integer getPeriodNo() { return periodNo; }
    public void setPeriodNo(Integer periodNo) { this.periodNo = periodNo; }
    public String getPeriodName() { return periodName; }
    public void setPeriodName(String periodName) { this.periodName = periodName; }
    public BigDecimal getRatio() { return ratio; }
    public void setRatio(BigDecimal ratio) { this.ratio = ratio; }
    public BigDecimal getPlanAmount() { return planAmount; }
    public void setPlanAmount(BigDecimal planAmount) { this.planAmount = planAmount; }
    public String getTriggerType() { return triggerType; }
    public void setTriggerType(String triggerType) { this.triggerType = triggerType; }
    public Long getTriggerBizId() { return triggerBizId; }
    public void setTriggerBizId(Long triggerBizId) { this.triggerBizId = triggerBizId; }
    public Date getPlanDate() { return planDate; }
    public void setPlanDate(Date planDate) { this.planDate = planDate; }
    public Date getDueTime() { return dueTime; }
    public void setDueTime(Date dueTime) { this.dueTime = dueTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getReceiveAmount() { return receiveAmount; }
    public void setReceiveAmount(BigDecimal receiveAmount) { this.receiveAmount = receiveAmount; }
    public BigDecimal getReduceAmount() { return reduceAmount; }
    public void setReduceAmount(BigDecimal reduceAmount) { this.reduceAmount = reduceAmount; }
    public Date getSettleTime() { return settleTime; }
    public void setSettleTime(Date settleTime) { this.settleTime = settleTime; }
    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public BigDecimal getRemainAmount() { return remainAmount; }
    public void setRemainAmount(BigDecimal remainAmount) { this.remainAmount = remainAmount; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getEstate() { return estate; }
    public void setEstate(String estate) { this.estate = estate; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getPayMethod() { return payMethod; }
    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
    public Integer getContractCount() { return contractCount; }
    public void setContractCount(Integer contractCount) { this.contractCount = contractCount; }
    public BigDecimal getContractAmount() { return contractAmount; }
    public void setContractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; }
    public Integer getPeriodCount() { return periodCount; }
    public void setPeriodCount(Integer periodCount) { this.periodCount = periodCount; }
    public Integer getSettledCount() { return settledCount; }
    public void setSettledCount(Integer settledCount) { this.settledCount = settledCount; }
    public Date getNextPlanDate() { return nextPlanDate; }
    public void setNextPlanDate(Date nextPlanDate) { this.nextPlanDate = nextPlanDate; }
}
