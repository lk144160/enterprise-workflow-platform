package com.ruoyi.business.flow.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.flow.domain.FlowDefinition;
import com.ruoyi.business.flow.service.IFlowDefinitionService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;

/**
 * 流程配置管理（仅管理员）
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/flow/definition")
public class FlowDefinitionController extends BaseController
{
    @Autowired
    private IFlowDefinitionService flowDefinitionService;

    /**
     * 流程定义列表
     */
    @PreAuthorize("@ss.hasPermi('system:flow:list')")
    @GetMapping("/list")
    public TableDataInfo list(FlowDefinition flowDefinition)
    {
        startPage();
        List<FlowDefinition> list = flowDefinitionService.selectFlowDefinitionList(flowDefinition);
        return getDataTable(list);
    }

    /**
     * 流程详情（含节点）
     */
    @PreAuthorize("@ss.hasPermi('system:flow:query')")
    @GetMapping("/{flowId}")
    public AjaxResult getInfo(@PathVariable Long flowId)
    {
        return success(flowDefinitionService.selectFlowDefinitionById(flowId));
    }

    /**
     * 新增流程
     */
    @PreAuthorize("@ss.hasPermi('system:flow:add')")
    @Log(title = "流程配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FlowDefinition flowDefinition)
    {
        flowDefinition.setCreateBy(getUsername());
        return toAjax(flowDefinitionService.insertFlowDefinition(flowDefinition));
    }

    /**
     * 修改流程（整体替换节点）
     */
    @PreAuthorize("@ss.hasPermi('system:flow:edit')")
    @Log(title = "流程配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FlowDefinition flowDefinition)
    {
        flowDefinition.setUpdateBy(getUsername());
        return toAjax(flowDefinitionService.updateFlowDefinition(flowDefinition));
    }

    /**
     * 删除流程
     */
    @PreAuthorize("@ss.hasPermi('system:flow:remove')")
    @Log(title = "流程配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{flowIds}")
    public AjaxResult remove(@PathVariable Long[] flowIds)
    {
        return toAjax(flowDefinitionService.deleteFlowDefinitionByIds(flowIds));
    }
}
