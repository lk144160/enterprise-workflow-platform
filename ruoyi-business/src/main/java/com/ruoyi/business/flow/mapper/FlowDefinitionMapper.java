package com.ruoyi.business.flow.mapper;

import java.util.List;
import com.ruoyi.business.flow.domain.FlowDefinition;

/**
 * 审批流定义 Mapper 接口
 *
 * @author renovationops
 */
public interface FlowDefinitionMapper
{
    public FlowDefinition selectFlowDefinitionById(Long flowId);

    public FlowDefinition selectFlowDefinitionByCode(String flowCode);

    public List<FlowDefinition> selectFlowDefinitionList(FlowDefinition flowDefinition);

    public int insertFlowDefinition(FlowDefinition flowDefinition);

    public int updateFlowDefinition(FlowDefinition flowDefinition);

    public int deleteFlowDefinitionById(Long flowId);

    public int deleteFlowDefinitionByIds(Long[] flowIds);
}
