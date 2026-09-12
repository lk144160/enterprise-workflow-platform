package com.ruoyi.business.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 客户线索对象 crm_customer
 *
 * @author renovationops
 */
public class CrmCustomer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 线索ID */
    private Long customerId;

    /** 客户姓名 */
    private String customerName;

    /** 手机号 */
    private String phone;

    /** 线索来源（字典 crm_customer_source） */
    private String source;

    /** 楼盘名称 */
    private String estate;

    /** 户型 */
    private String houseType;

    /** 建筑面积 */
    private java.math.BigDecimal area;

    /** 预算区间 */
    private String budget;

    /** 装修需求描述 */
    private String demand;

    /** 意向等级（A/B/C/D） */
    private String intentionLevel;

    /** 状态（1待跟进 2跟进中 3已转化 4已签约 5已流失） */
    private String status;

    /** 业务员姓名（直接录入） */
    private String ownerName;

    /** 负责设计师user_id（可选绑定） */
    private Long designerId;

    /** 家装顾问user_id（可选绑定） */
    private Long advisorId;

    /** 归属部门 */
    private Long deptId;

    /** 最近跟进时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date latestFollowTime;

    /** 下次跟进时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextFollowTime;

    /** 流失原因 */
    private String lossReason;

    /** 付款方式（字典 biz_pay_type，可选） */
    private String payMethod;

    /** 到访次数（展示） */
    private Integer visitCount;

    /** 已收未抵扣定金合计（展示） */
    private BigDecimal depositSum;

    /** 负责设计师姓名（展示） */
    private String designerName;

    /** 家装顾问姓名（展示） */
    private String advisorName;

    /** 登记人昵称（展示，创建人） */
    private String createByName;

    /** 部门名称（展示） */
    private String deptName;

    /** 跟进记录（详情用） */
    private List<CrmFollowRecord> followRecords;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getEstate() { return estate; }
    public void setEstate(String estate) { this.estate = estate; }
    public String getHouseType() { return houseType; }
    public void setHouseType(String houseType) { this.houseType = houseType; }
    public java.math.BigDecimal getArea() { return area; }
    public void setArea(java.math.BigDecimal area) { this.area = area; }
    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }
    public String getDemand() { return demand; }
    public void setDemand(String demand) { this.demand = demand; }
    public String getIntentionLevel() { return intentionLevel; }
    public void setIntentionLevel(String intentionLevel) { this.intentionLevel = intentionLevel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getPayMethod() { return payMethod; }
    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
    public Integer getVisitCount() { return visitCount; }
    public void setVisitCount(Integer visitCount) { this.visitCount = visitCount; }
    public BigDecimal getDepositSum() { return depositSum; }
    public void setDepositSum(BigDecimal depositSum) { this.depositSum = depositSum; }
    public Long getDesignerId() { return designerId; }
    public void setDesignerId(Long designerId) { this.designerId = designerId; }
    public Long getAdvisorId() { return advisorId; }
    public void setAdvisorId(Long advisorId) { this.advisorId = advisorId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Date getLatestFollowTime() { return latestFollowTime; }
    public void setLatestFollowTime(Date latestFollowTime) { this.latestFollowTime = latestFollowTime; }
    public Date getNextFollowTime() { return nextFollowTime; }
    public void setNextFollowTime(Date nextFollowTime) { this.nextFollowTime = nextFollowTime; }
    public String getLossReason() { return lossReason; }
    public void setLossReason(String lossReason) { this.lossReason = lossReason; }
    public String getDesignerName() { return designerName; }
    public void setDesignerName(String designerName) { this.designerName = designerName; }
    public String getAdvisorName() { return advisorName; }
    public void setAdvisorName(String advisorName) { this.advisorName = advisorName; }
    public String getCreateByName() { return createByName; }
    public void setCreateByName(String createByName) { this.createByName = createByName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public List<CrmFollowRecord> getFollowRecords() { return followRecords; }
    public void setFollowRecords(List<CrmFollowRecord> followRecords) { this.followRecords = followRecords; }
}
