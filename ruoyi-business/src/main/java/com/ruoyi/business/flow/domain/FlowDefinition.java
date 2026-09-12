package com.ruoyi.business.flow.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 审批流定义 flow_definition
 *
 * @author renovationops
 */
public class FlowDefinition extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流程ID */
    private Long flowId;

    /** 流程编码 */
    private String flowCode;

    /** 流程名称 */
    private String flowName;

    /** 关联业务类型 */
    private String bizType;

    /** 状态（0启用 1停用） */
    private String status;

    /** 节点列表（配置用） */
    private java.util.List<FlowNode> nodes;

    public Long getFlowId() { return flowId; }
    public void setFlowId(Long flowId) { this.flowId = flowId; }
    public String getFlowCode() { return flowCode; }
    public void setFlowCode(String flowCode) { this.flowCode = flowCode; }
    public String getFlowName() { return flowName; }
    public void setFlowName(String flowName) { this.flowName = flowName; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.util.List<FlowNode> getNodes() { return nodes; }
    public void setNodes(java.util.List<FlowNode> nodes) { this.nodes = nodes; }
}
