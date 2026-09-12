package com.ruoyi.business.deposit.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 定金记录对象 biz_deposit
 *
 * @author renovationops
 */
public class BizDeposit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 定金ID */
    private Long depositId;

    /** 定金编号（DJ+yyyyMMdd+4位流水） */
    private String depositNo;

    /** 客户ID */
    private Long customerId;

    /** 关联报价ID */
    private Long quoteId;

    /** 抵扣合同ID（抵扣后回填） */
    private Long contractId;

    /** 定金金额 */
    private BigDecimal amount;

    /** 收款方式（字典biz_pay_type） */
    private String payType;

    /** 收款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /** 收款凭证URL */
    private String voucherUrl;

    /** 状态（0已收定金 1已抵扣 2退还审批中 3已退还） */
    private String status;

    /** 退还时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date refundTime;

    /** 退还原因 */
    private String refundReason;

    /** 客户姓名（展示） */
    private String customerName;

    /** 客户手机号（展示） */
    private String customerPhone;

    /** 楼盘（展示） */
    private String estate;

    /** 关联报价编号（展示） */
    private String quoteNo;

    /** 抵扣合同编号（展示） */
    private String contractNo;

    public Long getDepositId() { return depositId; }
    public void setDepositId(Long depositId) { this.depositId = depositId; }
    public String getDepositNo() { return depositNo; }
    public void setDepositNo(String depositNo) { this.depositNo = depositNo; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getQuoteId() { return quoteId; }
    public void setQuoteId(Long quoteId) { this.quoteId = quoteId; }
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }
    public Date getPayTime() { return payTime; }
    public void setPayTime(Date payTime) { this.payTime = payTime; }
    public String getVoucherUrl() { return voucherUrl; }
    public void setVoucherUrl(String voucherUrl) { this.voucherUrl = voucherUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getRefundTime() { return refundTime; }
    public void setRefundTime(Date refundTime) { this.refundTime = refundTime; }
    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getEstate() { return estate; }
    public void setEstate(String estate) { this.estate = estate; }
    public String getQuoteNo() { return quoteNo; }
    public void setQuoteNo(String quoteNo) { this.quoteNo = quoteNo; }
    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }
}
