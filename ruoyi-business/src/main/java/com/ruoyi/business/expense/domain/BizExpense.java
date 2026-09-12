package com.ruoyi.business.expense.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 报销单对象 biz_expense
 *
 * @author renovationops
 */
public class BizExpense extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报销ID */
    private Long expenseId;

    /** 报销编号（BX+yyyyMMdd+4位流水） */
    private String expenseNo;

    /** 申请人user_id */
    private Long applicantId;

    /** 申请部门 */
    private Long deptId;

    /** 费用类型（字典 biz_expense_type） */
    private String expenseType;

    /** 报销金额 */
    private BigDecimal amount;

    /** 费用发生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expenseDate;

    /** 发票张数（与附件校验） */
    private Integer invoiceCount;

    /** 费用说明 */
    private String description;

    /** 状态（0草稿 1审批中 2已通过 3已驳回 4已打款 5已作废） */
    private String status;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /** 打款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /** 申请人姓名（展示） */
    private String applicantName;

    /** 部门名称（展示） */
    private String deptName;

    public Long getExpenseId() { return expenseId; }
    public void setExpenseId(Long expenseId) { this.expenseId = expenseId; }
    public String getExpenseNo() { return expenseNo; }
    public void setExpenseNo(String expenseNo) { this.expenseNo = expenseNo; }
    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getExpenseType() { return expenseType; }
    public void setExpenseType(String expenseType) { this.expenseType = expenseType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Date getExpenseDate() { return expenseDate; }
    public void setExpenseDate(Date expenseDate) { this.expenseDate = expenseDate; }
    public Integer getInvoiceCount() { return invoiceCount; }
    public void setInvoiceCount(Integer invoiceCount) { this.invoiceCount = invoiceCount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getSubmitTime() { return submitTime; }
    public void setSubmitTime(Date submitTime) { this.submitTime = submitTime; }
    public Date getPayTime() { return payTime; }
    public void setPayTime(Date payTime) { this.payTime = payTime; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
}
