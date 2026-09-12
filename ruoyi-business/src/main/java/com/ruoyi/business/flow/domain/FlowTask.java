package com.ruoyi.business.flow.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 审批任务 flow_task
 *
 * @author renovationops
 */
public class FlowTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long taskId;

    /** 实例ID */
    private Long instanceId;

    /** 节点ID */
    private Long nodeId;

    /** 节点名称 */
    private String nodeName;

    /** 审批人 */
    private Long approverId;

    /** 审批人姓名（关联查询） */
    private String approverName;

    /** 状态（0待审批 1同意 2驳回 3失效 4转交） */
    private String status;

    /** 审批意见 */
    private String opinion;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    /** ---- 关联实例展示字段 ---- */
    /** 业务类型 */
    private String bizType;

    /** 业务单据ID */
    private Long bizId;

    /** 单据标题 */
    private String bizTitle;

    /** 单据金额 */
    private java.math.BigDecimal bizAmount;

    /** 发起人 */
    private Long startUserId;

    /** 发起人姓名 */
    private String startUserName;

    /** 客户姓名（按业务类型关联解析，无客户的业务为空） */
    private String customerName;

    /** 发起时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getInstanceId() { return instanceId; }
    public void setInstanceId(Long instanceId) { this.instanceId = instanceId; }
    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }
    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOpinion() { return opinion; }
    public void setOpinion(String opinion) { this.opinion = opinion; }
    public Date getHandleTime() { return handleTime; }
    public void setHandleTime(Date handleTime) { this.handleTime = handleTime; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public Long getBizId() { return bizId; }
    public void setBizId(Long bizId) { this.bizId = bizId; }
    public String getBizTitle() { return bizTitle; }
    public void setBizTitle(String bizTitle) { this.bizTitle = bizTitle; }
    public java.math.BigDecimal getBizAmount() { return bizAmount; }
    public void setBizAmount(java.math.BigDecimal bizAmount) { this.bizAmount = bizAmount; }
    public Long getStartUserId() { return startUserId; }
    public void setStartUserId(Long startUserId) { this.startUserId = startUserId; }
    public String getStartUserName() { return startUserName; }
    public void setStartUserName(String startUserName) { this.startUserName = startUserName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
}
