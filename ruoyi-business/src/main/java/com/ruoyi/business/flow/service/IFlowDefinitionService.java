package com.ruoyi.business.flow.service;

import java.util.List;
import com.ruoyi.business.flow.domain.FlowDefinition;

/**
 * 审批流定义配置服务接口
 *
 * @author renovationops
 */
public interface IFlowDefinitionService
{
    public FlowDefinition selectFlowDefinitionById(Long flowId);

    public FlowDefinition selectFlowDefinitionByCode(String flowCode);

    public List<FlowDefinition> selectFlowDefinitionList(FlowDefinition flowDefinition);

    /**
     * 新增流程（含节点）
     */
    public int insertFlowDefinition(FlowDefinition flowDefinition);

    /**
     * 修改流程（整体替换节点）
     */
    public int updateFlowDefinition(FlowDefinition flowDefinition);

    public int deleteFlowDefinitionByIds(Long[] flowIds);
}
