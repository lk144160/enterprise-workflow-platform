package com.ruoyi.business.flow.domain;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 审批流节点 flow_node
 *
 * @author renovationops
 */
public class FlowNode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private Long nodeId;

    /** 流程ID */
    private Long flowId;

    /** 节点顺序 */
    private Integer nodeOrder;

    /** 节点名称 */
    private String nodeName;

    /** 审批人类型（1指定用户 2部门主管 3指定角色） */
    private String approverType;

    /** 审批人值（userId集合/角色ID） */
    private String approverValue;

    /** 签署方式（1或签 2会签） */
    private String multiSign;

    /** 金额阈值（单据金额≥该值节点生效） */
    private BigDecimal conditionAmount;

    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }
    public Long getFlowId() { return flowId; }
    public void setFlowId(Long flowId) { this.flowId = flowId; }
    public Integer getNodeOrder() { return nodeOrder; }
    public void setNodeOrder(Integer nodeOrder) { this.nodeOrder = nodeOrder; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public String getApproverType() { return approverType; }
    public void setApproverType(String approverType) { this.approverType = approverType; }
    public String getApproverValue() { return approverValue; }
    public void setApproverValue(String approverValue) { this.approverValue = approverValue; }
    public String getMultiSign() { return multiSign; }
    public void setMultiSign(String multiSign) { this.multiSign = multiSign; }
    public BigDecimal getConditionAmount() { return conditionAmount; }
    public void setConditionAmount(BigDecimal conditionAmount) { this.conditionAmount = conditionAmount; }
}
