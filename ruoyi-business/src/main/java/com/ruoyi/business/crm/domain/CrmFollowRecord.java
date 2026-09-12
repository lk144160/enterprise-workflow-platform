package com.ruoyi.business.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 线索跟进记录对象 crm_follow_record
 *
 * @author renovationops
 */
public class CrmFollowRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 线索ID */
    private Long customerId;

    /** 跟进方式（字典 crm_follow_type） */
    private String followType;

    /** 跟进内容 */
    private String content;

    /** 跟进时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date followTime;

    /** 下次跟进时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextFollowTime;

    /** 到访人数（仅到访记录） */
    private Integer visitCount;

    /** 同行人描述（仅到访记录） */
    private String companion;

    /** 接待人（仅到访记录） */
    private Long receptionUserId;

    /** 到访目的（字典 crm_visit_purpose，仅到访记录） */
    private String visitPurpose;

    /** 客户反馈（字典 crm_visit_feedback，仅到访记录） */
    private String feedback;

    /** 跟进人（展示） */
    private String createByName;

    /** 接待人姓名（展示） */
    private String receptionUserName;

    /** 到访序号（第N次到访，仅到访记录） */
    private Integer visitSeq;

    /** 客户姓名（展示，列表筛选用） */
    private String customerName;

    /** 手机号（展示） */
    private String phone;

    /** 客户楼盘（展示，报表用） */
    private String customerEstate;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getFollowType() { return followType; }
    public void setFollowType(String followType) { this.followType = followType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getFollowTime() { return followTime; }
    public void setFollowTime(Date followTime) { this.followTime = followTime; }
    public Date getNextFollowTime() { return nextFollowTime; }
    public void setNextFollowTime(Date nextFollowTime) { this.nextFollowTime = nextFollowTime; }
    public Integer getVisitCount() { return visitCount; }
    public void setVisitCount(Integer visitCount) { this.visitCount = visitCount; }
    public String getCompanion() { return companion; }
    public void setCompanion(String companion) { this.companion = companion; }
    public Long getReceptionUserId() { return receptionUserId; }
    public void setReceptionUserId(Long receptionUserId) { this.receptionUserId = receptionUserId; }
    public String getVisitPurpose() { return visitPurpose; }
    public void setVisitPurpose(String visitPurpose) { this.visitPurpose = visitPurpose; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public String getCreateByName() { return createByName; }
    public void setCreateByName(String createByName) { this.createByName = createByName; }
    public String getReceptionUserName() { return receptionUserName; }
    public void setReceptionUserName(String receptionUserName) { this.receptionUserName = receptionUserName; }
    public Integer getVisitSeq() { return visitSeq; }
    public void setVisitSeq(Integer visitSeq) { this.visitSeq = visitSeq; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCustomerEstate() { return customerEstate; }
    public void setCustomerEstate(String customerEstate) { this.customerEstate = customerEstate; }
}
