package com.ruoyi.business.flow.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.business.attachment.domain.BizAttachment;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 审批流实例 flow_instance
 *
 * @author renovationops
 */
public class FlowInstance extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 实例ID */
    private Long instanceId;

    /** 流程ID */
    private Long flowId;

    /** 流程编码 */
    private String flowCode;

    /** 业务类型 */
    private String bizType;

    /** 业务单据ID */
    private Long bizId;

    /** 业务单据快照标题 */
    private String bizTitle;

    /** 业务单据金额（条件路由用） */
    private BigDecimal bizAmount;

    /** 当前节点（NULL为已结束） */
    private Long currentNodeId;

    /** 状态（1进行中 2通过 3驳回 4撤销） */
    private String status;

    /** 发起人 */
    private Long startUserId;

    /** 发起人姓名（关联查询） */
    private String startUserName;

    /** 客户姓名（按业务类型关联解析，无客户的业务为空） */
    private String customerName;

    /** 客户ID（按业务类型关联解析，无客户的业务为空） */
    private Long customerId;

    /** 发起时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 当前节点名称（关联查询） */
    private String currentNodeName;

    /** 审批历史（详情用） */
    private List<FlowTask> tasks;

    /** 业务单据详情（详情用，按 bizType 动态：报价/合同/收款期次/交底/报销/出库单） */
    private Object bizDetail;

    /** 附件/图片列表（详情用，如报销发票） */
    private List<BizAttachment> attachments;

    public Long getInstanceId() { return instanceId; }
    public void setInstanceId(Long instanceId) { this.instanceId = instanceId; }
    public Long getFlowId() { return flowId; }
    public void setFlowId(Long flowId) { this.flowId = flowId; }
    public String getFlowCode() { return flowCode; }
    public void setFlowCode(String flowCode) { this.flowCode = flowCode; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }
    public String getBizTitle() { return bizTitle; }
    public void setBizTitle(String bizTitle) { this.bizTitle = bizTitle; }
    public BigDecimal getBizAmount() { return bizAmount; }
    public void setBizAmount(BigDecimal bizAmount) { this.bizAmount = bizAmount; }
    public Long getCurrentNodeId() { return currentNodeId; }
    public void setCurrentNodeId(Long currentNodeId) { this.currentNodeId = currentNodeId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getStartUserId() { return startUserId; }
    public void setStartUserId(Long startUserId) { this.startUserId = startUserId; }
    public String getStartUserName() { return startUserName; }
    public void setStartUserName(String startUserName) { this.startUserName = startUserName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public String getCurrentNodeName() { return currentNodeName; }
    public void setCurrentNodeName(String currentNodeName) { this.currentNodeName = currentNodeName; }
    public List<FlowTask> getTasks() { return tasks; }
    public void setTasks(List<FlowTask> tasks) { this.tasks = tasks; }
    public Object getBizDetail() { return bizDetail; }
    public void setBizDetail(Object bizDetail) { this.bizDetail = bizDetail; }
    public List<BizAttachment> getAttachments() { return attachments; }
    public void setAttachments(List<BizAttachment> attachments) { this.attachments = attachments; }
}
