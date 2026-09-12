package com.ruoyi.business.contract.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 合同对象 biz_contract
 *
 * @author renovationops
 */
public class BizContract extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 合同ID */
    private Long contractId;

    /** 合同编号（HT+yyyyMMdd+4位流水） */
    private String contractNo;

    /** 客户ID */
    private Long customerId;

    /** 关联报价ID */
    private Long quoteId;

    /** 合同金额 */
    private BigDecimal contractAmount;

    /** 累计已收金额（冗余） */
    private BigDecimal paidAmount;

    /** 与报价差异说明 */
    private String amountDiffNote;

    /** 签订日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date signDate;

    /** 工期开始 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /** 工期结束 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 完工日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date finishDate;

    /** 状态（0待审批 1审批中 2已生效 3已完工 4已归档 5已终止） */
    private String status;

    /** 归属部门 */
    private Long deptId;

    /** 合同负责人user_id（可选绑定） */
    private Long ownerUserId;

    /** 合同负责人姓名（展示） */
    private String ownerUserName;

    /** 客户姓名（展示） */
    private String customerName;

    /** 客户手机号（展示） */
    private String customerPhone;

    /** 客户楼盘（展示） */
    private String estate;

    /** 客户户型（展示） */
    private String houseType;

    /** 客户面积（展示） */
    private java.math.BigDecimal area;

    /** 客户预算（展示，字典值） */
    private String budget;

    /** 关联报价编号（展示） */
    private String quoteNo;

    /** 收款计划（详情用） */
    private List<BizPaymentPlan> plans;

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getQuoteId() { return quoteId; }
    public void setQuoteId(Long quoteId) { this.quoteId = quoteId; }
    public BigDecimal getContractAmount() { return contractAmount; }
    public void setContractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public String getAmountDiffNote() { return amountDiffNote; }
    public void setAmountDiffNote(String amountDiffNote) { this.amountDiffNote = amountDiffNote; }
    public Date getSignDate() { return signDate; }
    public void setSignDate(Date signDate) { this.signDate = signDate; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public Date getFinishDate() { return finishDate; }
    public void setFinishDate(Date finishDate) { this.finishDate = finishDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public String getOwnerUserName() { return ownerUserName; }
    public void setOwnerUserName(String ownerUserName) { this.ownerUserName = ownerUserName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getEstate() { return estate; }
    public void setEstate(String estate) { this.estate = estate; }
    public String getHouseType() { return houseType; }
    public void setHouseType(String houseType) { this.houseType = houseType; }
    public java.math.BigDecimal getArea() { return area; }
    public void setArea(java.math.BigDecimal area) { this.area = area; }
    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }
    public String getQuoteNo() { return quoteNo; }
    public void setQuoteNo(String quoteNo) { this.quoteNo = quoteNo; }
    public List<BizPaymentPlan> getPlans() { return plans; }
    public void setPlans(List<BizPaymentPlan> plans) { this.plans = plans; }
}
