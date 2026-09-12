package com.ruoyi.business.flow.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.business.flow.domain.FlowDefinition;
import com.ruoyi.business.flow.domain.FlowNode;
import com.ruoyi.business.flow.mapper.FlowDefinitionMapper;
import com.ruoyi.business.flow.mapper.FlowNodeMapper;
import com.ruoyi.business.flow.service.IFlowDefinitionService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 审批流定义配置服务实现
 *
 * @author renovationops
 */
@Service
public class FlowDefinitionServiceImpl implements IFlowDefinitionService
{
    @Autowired
    private FlowDefinitionMapper definitionMapper;

    @Autowired
    private FlowNodeMapper nodeMapper;

    @Override
    public FlowDefinition selectFlowDefinitionById(Long flowId)
    {
        FlowDefinition definition = definitionMapper.selectFlowDefinitionById(flowId);
        if (definition != null)
        {
            definition.setNodes(nodeMapper.selectNodeListByFlowId(flowId));
        }
        return definition;
    }

    @Override
    public FlowDefinition selectFlowDefinitionByCode(String flowCode)
    {
        return definitionMapper.selectFlowDefinitionByCode(flowCode);
    }

    @Override
    public List<FlowDefinition> selectFlowDefinitionList(FlowDefinition flowDefinition)
    {
        List<FlowDefinition> list = definitionMapper.selectFlowDefinitionList(flowDefinition);
        for (FlowDefinition definition : list)
        {
            definition.setNodes(nodeMapper.selectNodeListByFlowId(definition.getFlowId()));
        }
        return list;
    }

    @Override
    @Transactional
    public int insertFlowDefinition(FlowDefinition flowDefinition)
    {
        validate(flowDefinition);
        int rows = definitionMapper.insertFlowDefinition(flowDefinition);
        saveNodes(flowDefinition);
        return rows;
    }

    @Override
    @Transactional
    public int updateFlowDefinition(FlowDefinition flowDefinition)
    {
        validate(flowDefinition);
        // 整体替换节点
        nodeMapper.deleteNodeByFlowIds(new Long[] { flowDefinition.getFlowId() });
        int rows = definitionMapper.updateFlowDefinition(flowDefinition);
        saveNodes(flowDefinition);
        return rows;
    }

    @Override
    @Transactional
    public int deleteFlowDefinitionByIds(Long[] flowIds)
    {
        nodeMapper.deleteNodeByFlowIds(flowIds);
        return definitionMapper.deleteFlowDefinitionByIds(flowIds);
    }

    private void saveNodes(FlowDefinition flowDefinition)
    {
        if (flowDefinition.getNodes() == null)
        {
            return;
        }
        for (FlowNode node : flowDefinition.getNodes())
        {
            node.setFlowId(flowDefinition.getFlowId());
            node.setCreateBy(flowDefinition.getUpdateBy() != null ? flowDefinition.getUpdateBy() : flowDefinition.getCreateBy());
            nodeMapper.insertFlowNode(node);
        }
    }

    private void validate(FlowDefinition flowDefinition)
    {
        if (StringUtils.isEmpty(flowDefinition.getFlowCode()))
        {
            throw new ServiceException("流程编码不能为空");
        }
        if (flowDefinition.getNodes() == null || flowDefinition.getNodes().isEmpty())
        {
            throw new ServiceException("流程至少需要一个审批节点");
        }
        for (FlowNode node : flowDefinition.getNodes())
        {
            if (!"1".equals(node.getApproverType()) && !"2".equals(node.getApproverType()) && !"3".equals(node.getApproverType()))
            {
                throw new ServiceException("节点【" + node.getNodeName() + "】审批人类型不合法");
            }
            if (!"2".equals(node.getApproverType()) && StringUtils.isEmpty(node.getApproverValue()))
            {
                throw new ServiceException("节点【" + node.getNodeName() + "】未配置审批人");
            }
        }
    }
}
