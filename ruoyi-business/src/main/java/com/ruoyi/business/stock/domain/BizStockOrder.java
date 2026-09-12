package com.ruoyi.business.stock.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 出入库单对象 biz_stock_order
 *
 * @author renovationops
 */
public class BizStockOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 单据ID */
    private Long orderId;

    /** 单据编号（RK/CK+yyyyMMdd+4位流水） */
    private String orderNo;

    /** 单据类型（1入库 2出库） */
    private String orderType;

    /** 来源（字典 biz_stock_source） */
    private String sourceType;

    /** 关联合同（项目领用必填） */
    private Long contractId;

    /** 申请人 */
    private Long applicantId;

    /** 经办人（行政财务） */
    private Long handlerId;

    /** 合计金额 */
    private BigDecimal totalAmount;

    /** 状态（0草稿 1审批中 2已出库/已完成 3已驳回 4已作废） */
    private String status;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completeTime;

    /** 归属部门 */
    private Long deptId;

    /** 合同编号（展示） */
    private String contractNo;

    /** 客户姓名（展示） */
    private String customerName;

    /** 申请人姓名（展示） */
    private String applicantName;

    /** 经办人姓名（展示） */
    private String handlerName;

    /** 明细（编辑/详情用） */
    private List<BizStockOrderItem> items;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }
    public Long getHandlerId() { return handlerId; }
    public void setHandlerId(Long handlerId) { this.handlerId = handlerId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getApplyTime() { return applyTime; }
    public void setApplyTime(Date applyTime) { this.applyTime = applyTime; }
    public Date getCompleteTime() { return completeTime; }
    public void setCompleteTime(Date completeTime) { this.completeTime = completeTime; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getHandlerName() { return handlerName; }
    public void setHandlerName(String handlerName) { this.handlerName = handlerName; }
    public List<BizStockOrderItem> getItems() { return items; }
    public void setItems(List<BizStockOrderItem> items) { this.items = items; }
}
