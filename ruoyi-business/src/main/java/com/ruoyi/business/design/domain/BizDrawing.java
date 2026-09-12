package com.ruoyi.business.design.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 图纸版本对象 biz_drawing
 *
 * @author renovationops
 */
public class BizDrawing extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 图纸ID */
    private Long drawingId;

    /** 客户ID（可选绑定） */
    private Long customerId;

    /** 合同ID（可选绑定） */
    private Long contractId;

    /** 图纸名称 */
    private String drawingName;

    /** 图纸类型（字典 biz_drawing_type） */
    private String drawingType;

    /** 文件地址 */
    private String fileUrl;

    /** 原始文件名 */
    private String fileName;

    /** 版本号 */
    private Integer version;

    /** 是否当前版本（1是 0否） */
    private String isCurrent;

    /** 状态（0待确认 1已确认 2已作废） */
    private String status;

    /** 确认人 */
    private Long confirmUserId;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    /** 确认人姓名（展示） */
    private String confirmUserName;

    /** 客户姓名（展示） */
    private String customerName;

    /** 合同金额（展示） */
    private BigDecimal contractAmount;

    public Long getDrawingId() { return drawingId; }
    public void setDrawingId(Long drawingId) { this.drawingId = drawingId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public String getDrawingName() { return drawingName; }
    public void setDrawingName(String drawingName) { this.drawingName = drawingName; }
    public String getDrawingType() { return drawingType; }
    public void setDrawingType(String drawingType) { this.drawingType = drawingType; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public String getIsCurrent() { return isCurrent; }
    public void setIsCurrent(String isCurrent) { this.isCurrent = isCurrent; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getConfirmUserId() { return confirmUserId; }
    public void setConfirmUserId(Long confirmUserId) { this.confirmUserId = confirmUserId; }
    public Date getConfirmTime() { return confirmTime; }
    public void setConfirmTime(Date confirmTime) { this.confirmTime = confirmTime; }
    public String getConfirmUserName() { return confirmUserName; }
    public void setConfirmUserName(String confirmUserName) { this.confirmUserName = confirmUserName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public BigDecimal getContractAmount() { return contractAmount; }
    public void setContractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; }
}
