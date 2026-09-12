package com.ruoyi.business.flow.mapper;

import java.util.List;
import com.ruoyi.business.flow.domain.FlowNode;

/**
 * 审批流节点 Mapper 接口
 *
 * @author renovationops
 */
public interface FlowNodeMapper
{
    public FlowNode selectFlowNodeById(Long nodeId);

    public List<FlowNode> selectNodeListByFlowId(Long flowId);

    public List<FlowNode> selectFlowNodeList(FlowNode flowNode);

    public int insertFlowNode(FlowNode flowNode);

    public int updateFlowNode(FlowNode flowNode);

    public int deleteFlowNodeById(Long nodeId);

    public int deleteNodeByFlowIds(Long[] flowIds);
}
