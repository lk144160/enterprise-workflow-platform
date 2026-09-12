package com.ruoyi.business.contract.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 收款记录对象 biz_payment_record
 *
 * @author renovationops
 */
public class BizPaymentRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 收款ID */
    private Long paymentId;

    /** 合同ID */
    private Long contractId;

    /** 期次ID */
    private Long planId;

    /** 期次（冗余） */
    private Integer periodNo;

    /** 实收金额（冲正记录为负数） */
    private BigDecimal amount;

    /** 收款方式（字典 biz_pay_type） */
    private String payType;

    /** 到账时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /** 凭证附件地址 */
    private String voucherUrl;

    /** 偏差说明（实收与应收差额时必填） */
    private String deviationNote;

    /** 登记人 */
    private Long operatorId;

    /** 冲正关联的原收款ID */
    private Long reverseOfPaymentId;

    /** 冲正原因 */
    private String reverseReason;

    /** 合同编号（展示） */
    private String contractNo;

    /** 客户姓名（展示） */
    private String customerName;

    /** 期次名称（展示） */
    private String periodName;

    /** 登记人姓名（展示） */
    private String operatorName;

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public Integer getPeriodNo() { return periodNo; }
    public void setPeriodNo(Integer periodNo) { this.periodNo = periodNo; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }
    public Date getPayTime() { return payTime; }
    public void setPayTime(Date payTime) { this.payTime = payTime; }
    public String getVoucherUrl() { return voucherUrl; }
    public void setVoucherUrl(String voucherUrl) { this.voucherUrl = voucherUrl; }
    public String getDeviationNote() { return deviationNote; }
    public void setDeviationNote(String deviationNote) { this.deviationNote = deviationNote; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public Long getReverseOfPaymentId() { return reverseOfPaymentId; }
    public void setReverseOfPaymentId(Long reverseOfPaymentId) { this.reverseOfPaymentId = reverseOfPaymentId; }
    public String getReverseReason() { return reverseReason; }
    public void setReverseReason(String reverseReason) { this.reverseReason = reverseReason; }
    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPeriodName() { return periodName; }
    public void setPeriodName(String periodName) { this.periodName = periodName; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
}
