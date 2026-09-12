package com.ruoyi.business.flow.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.flow.domain.FlowInstance;
import com.ruoyi.business.flow.domain.FlowTask;
import com.ruoyi.business.flow.service.IFlowEngineService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.annotation.RepeatSubmit;

/**
 * 审批中心
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/flow")
public class FlowController extends BaseController
{
    @Autowired
    private IFlowEngineService flowEngineService;

    /**
     * 我的待办列表
     */
    @GetMapping("/todo/list")
    public TableDataInfo todoList(FlowTask flowTask)
    {
        startPage();
        List<FlowTask> list = flowEngineService.selectTodoTaskList(flowTask);
        return getDataTable(list);
    }

    /**
     * 我的待办数量
     */
    @GetMapping("/todo/count")
    public AjaxResult todoCount()
    {
        return success(flowEngineService.countTodoTask());
    }

    /**
     * 我的申请列表
     */
    @GetMapping("/mine/list")
    public TableDataInfo mineList(FlowInstance instance)
    {
        startPage();
        List<FlowInstance> list = flowEngineService.selectMyInstanceList(instance);
        return getDataTable(list);
    }

    /**
     * 审批详情（含审批历史）
     */
    @GetMapping("/instance/{instanceId}")
    public AjaxResult instance(@PathVariable Long instanceId)
    {
        return success(flowEngineService.getInstanceDetail(instanceId));
    }

    /**
     * 处理审批任务 body: { taskId, action: agree/reject, opinion }
     */
    @PostMapping("/handle")
    @RepeatSubmit(interval = 3000)
    @Log(title = "审批处理", businessType = BusinessType.UPDATE)
    public AjaxResult handle(@RequestBody Map<String, Object> body)
    {
        Long taskId = Long.parseLong(String.valueOf(body.get("taskId")));
        String action = String.valueOf(body.get("action"));
        String opinion = body.get("opinion") == null ? "" : String.valueOf(body.get("opinion"));
        flowEngineService.handleTask(taskId, action, opinion);
        return success();
    }

    /**
     * 转交审批任务 body: { taskId, targetUserId, opinion }
     */
    @PostMapping("/transfer")
    @RepeatSubmit(interval = 3000)
    @Log(title = "审批转交", businessType = BusinessType.UPDATE)
    public AjaxResult transfer(@RequestBody Map<String, Object> body)
    {
        Long taskId = Long.parseLong(String.valueOf(body.get("taskId")));
        Long targetUserId = Long.parseLong(String.valueOf(body.get("targetUserId")));
        String opinion = body.get("opinion") == null ? "" : String.valueOf(body.get("opinion"));
        flowEngineService.transferTask(taskId, targetUserId, opinion);
        return success();
    }

    /**
     * 撤销审批
     */
    @PostMapping("/cancel/{instanceId}")
    @Log(title = "审批撤销", businessType = BusinessType.UPDATE)
    public AjaxResult cancel(@PathVariable Long instanceId)
    {
        flowEngineService.cancelInstance(instanceId);
        return success();
    }
}
