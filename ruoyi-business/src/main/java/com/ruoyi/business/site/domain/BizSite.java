package com.ruoyi.business.site.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 施工工地对象 biz_site
 *
 * @author renovationops
 */
public class BizSite extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 工地ID */
    private Long siteId;

    /** 工地编号（GD+yyyyMMdd+4位流水） */
    private String siteNo;

    /** 合同ID（唯一，一合同一工地） */
    private Long contractId;

    /** 客户ID */
    private Long customerId;

    /** 小区名称 */
    private String estate;

    /** 面积（m²） */
    private BigDecimal area;

    /** 门锁密码（钥匙） */
    private String keyPassword;

    /** 设计师user_id */
    private Long designerId;

    /** 设计师姓名（冗余展示） */
    private String designerName;

    /** 监理user_id（=项目经理） */
    private Long supervisorId;

    /** 监理姓名（冗余展示） */
    private String supervisorName;

    /** 开工日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /** 完工日期（全部阶段完成后自动记录） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date finishDate;

    /** 状态（0待开工 1施工中 2已完工） */
    private String status;

    /** 归属部门 */
    private Long deptId;

    /** 客户姓名（展示） */
    private String customerName;

    /** 客户手机号（展示） */
    private String customerPhone;

    /** 合同编号（展示） */
    private String contractNo;

    /** 合同金额（展示） */
    private BigDecimal contractAmount;

    /** 合同工期开始（展示） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractStartDate;

    /** 合同工期结束（展示） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date contractEndDate;

    /** 总阶段数（聚合） */
    private Integer totalStages;

    /** 已完成阶段数（聚合） */
    private Integer doneStages;

    /** 当前阶段类型字典值（聚合，首个未完成阶段） */
    private String currentStageType;

    /** 是否延期（1是 0否，当前阶段计划结束已过且未完成） */
    private Integer lateFlag;

    /** 阶段列表（详情用） */
    private List<BizProgressStage> stages;

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public String getSiteNo() { return siteNo; }
    public void setSiteNo(String siteNo) { this.siteNo = siteNo; }
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getEstate() { return estate; }
    public void setEstate(String estate) { this.estate = estate; }
    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }
    public String getKeyPassword() { return keyPassword; }
    public void setKeyPassword(String keyPassword) { this.keyPassword = keyPassword; }
    public Long getDesignerId() { return designerId; }
    public void setDesignerId(Long designerId) { this.designerId = designerId; }
    public String getDesignerName() { return designerName; }
    public void setDesignerName(String designerName) { this.designerName = designerName; }
    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }
    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getFinishDate() { return finishDate; }
    public void setFinishDate(Date finishDate) { this.finishDate = finishDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getContractNo() { return contractNo; }
    public void setContractNo(String contractNo) { this.contractNo = contractNo; }
    public BigDecimal getContractAmount() { return contractAmount; }
    public void setContractAmount(BigDecimal contractAmount) { this.contractAmount = contractAmount; }
    public Date getContractStartDate() { return contractStartDate; }
    public void setContractStartDate(Date contractStartDate) { this.contractStartDate = contractStartDate; }
    public Date getContractEndDate() { return contractEndDate; }
    public void setContractEndDate(Date contractEndDate) { this.contractEndDate = contractEndDate; }
    public Integer getTotalStages() { return totalStages; }
    public void setTotalStages(Integer totalStages) { this.totalStages = totalStages; }
    public Integer getDoneStages() { return doneStages; }
    public void setDoneStages(Integer doneStages) { this.doneStages = doneStages; }
    public String getCurrentStageType() { return currentStageType; }
    public void setCurrentStageType(String currentStageType) { this.currentStageType = currentStageType; }
    public Integer getLateFlag() { return lateFlag; }
    public void setLateFlag(Integer lateFlag) { this.lateFlag = lateFlag; }
    public List<BizProgressStage> getStages() { return stages; }
    public void setStages(List<BizProgressStage> stages) { this.stages = stages; }
}
