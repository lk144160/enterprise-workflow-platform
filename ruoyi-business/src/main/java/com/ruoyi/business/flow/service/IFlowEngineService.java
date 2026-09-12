package com.ruoyi.business.flow.service;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.business.flow.domain.FlowInstance;
import com.ruoyi.business.flow.domain.FlowTask;

/**
 * 审批流引擎服务接口
 *
 * @author renovationops
 */
public interface IFlowEngineService
{
    /**
     * 发起审批：创建实例并推进到第一个有效节点
     *
     * @param flowCode 流程编码
     * @param bizType 业务类型
     * @param bizId 业务单据ID
     * @param bizTitle 单据快照标题
     * @param amount 单据金额（条件路由用，可空）
     * @return 审批实例
     */
    FlowInstance startFlow(String flowCode, String bizType, Long bizId, String bizTitle, BigDecimal amount);

    /**
     * 处理审批任务（同意/驳回）
     *
     * @param taskId 任务ID
     * @param action agree / reject
     * @param opinion 审批意见（驳回必填）
     */
    void handleTask(Long taskId, String action, String opinion);

    /**
     * 转交审批任务
     */
    void transferTask(Long taskId, Long targetUserId, String opinion);

    /**
     * 撤销审批（发起人，且尚无任何节点处理）
     *
     * @param instanceId 实例ID
     */
    void cancelInstance(Long instanceId);

    /**
     * 我的待办任务列表
     */
    List<FlowTask> selectTodoTaskList(FlowTask flowTask);

    /**
     * 我的申请列表
     */
    List<FlowInstance> selectMyInstanceList(FlowInstance flowInstance);

    /**
     * 实例详情（含节点定义与审批历史）
     */
    FlowInstance getInstanceDetail(Long instanceId);

    /**
     * 按业务单据查询进行中的实例
     */
    FlowInstance selectActiveInstance(String bizType, Long bizId);

    /**
     * 我的待办数量
     */
    int countTodoTask();
}
