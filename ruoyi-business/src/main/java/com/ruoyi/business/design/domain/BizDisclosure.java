package com.ruoyi.business.design.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 技术交底对象 biz_disclosure
 *
 * @author renovationops
 */
public class BizDisclosure extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 交底ID */
    private Long disclosureId;

    /** 交底编号（JB+yyyyMMdd+4位流水） */
    private String disclosureNo;

    /** 合同ID */
    private Long contractId;

    /** 交底内容（设计要点/材料要求/施工注意事项） */
    private String content;

    /** 会议日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date meetingDate;

    /** 内部参与人user_id集合（逗号分隔） */
    private String attendees;

    /** 外部参与人（工长姓名） */
    private String externalAttendees;

    /** 状态（0草稿 1审批中 2已确认 3已驳回） */
    private String status;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    /** 归属部门 */
    private Long deptId;

    /** 合同编号（展示） */
    private String contractNo;

    /** 合同金额（展示） */
    private BigDecimal contractAmount;

    /** 客户姓名（展示） */
    private String customerName;

    public Long getDisclosureId() { return disclosureId; }
    public void setDisclosureId(Long disclosureId) { this.disclosureId = disclosureId; }
    public String getDisclosureNo() { return disclosureNo; }
    public void setDisclosureNo(String disclosureNo) { this.disclosureNo = disclosureNo; }
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getMeetingDate() { return meetingDate; }
    public void setMeetingDate(Date meetingDate) { this.meetingDate = meetingDate; }
    public String getAttendees() { return attendees; }
    public void setAttendees(String attendees) { this.attendees = attendees; }
    public String getExternalAttendees() { return externalAttendees; }
    public void setExternalAttendees(String externalAttendees) { this.externalAttendees = externalAttendees; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getConfirmTime() { return confirmTime; }
    public void setConfirmTime(Date confirmTime) { this.confirmTime = confirmTime; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }
    public BigDecimal getContractAmount() { return contractAmount; }
    public void setContractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
