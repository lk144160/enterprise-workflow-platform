package com.ruoyi.business.site.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.business.attachment.domain.BizAttachment;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 施工阶段对象 biz_progress_stage
 *
 * @author renovationops
 */
public class BizProgressStage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 阶段ID */
    private Long stageId;

    /** 工地ID */
    private Long siteId;

    /** 阶段类型（字典 biz_stage_type） */
    private String stageType;

    /** 步骤名称（自定义小标题，空则取字典标签） */
    private String stageName;

    /** 顺序号 */
    private Integer sortOrder;

    /** 计划开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planStartDate;

    /** 计划结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planEndDate;

    /** 实际开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualStartDate;

    /** 实际完成日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualEndDate;

    /** 状态（0未开始 1进行中 2已完成） */
    private String status;

    /** 是否延期（1是 0否） */
    private Integer lateFlag;

    /** 现场照片（biz_type=site_stage） */
    private List<BizAttachment> attachments;

    /** 插入位置：在该步骤之前插入（空=追加到末尾，非表字段） */
    private Long insertBeforeStageId;

    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public String getStageType() { return stageType; }
    public void setStageType(String stageType) { this.stageType = stageType; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Date getPlanStartDate() { return planStartDate; }
    public void setPlanStartDate(Date planStartDate) { this.planStartDate = planStartDate; }
    public Date getPlanEndDate() { return planEndDate; }
    public void setPlanEndDate(Date planEndDate) { this.planEndDate = planEndDate; }
    public Date getActualStartDate() { return actualStartDate; }
    public void setActualStartDate(Date actualStartDate) { this.actualStartDate = actualStartDate; }
    public Date getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(Date actualEndDate) { this.actualEndDate = actualEndDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getLateFlag() { return lateFlag; }
    public void setLateFlag(Integer lateFlag) { this.lateFlag = lateFlag; }
    public List<BizAttachment> getAttachments() { return attachments; }
    public void setAttachments(List<BizAttachment> attachments) { this.attachments = attachments; }
    public Long getInsertBeforeStageId() { return insertBeforeStageId; }
    public void setInsertBeforeStageId(Long insertBeforeStageId) { this.insertBeforeStageId = insertBeforeStageId; }
}
