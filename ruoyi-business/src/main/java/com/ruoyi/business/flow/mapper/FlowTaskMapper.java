package com.ruoyi.business.flow.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.business.flow.domain.FlowTask;

/**
 * 审批任务 Mapper 接口
 *
 * @author renovationops
 */
public interface FlowTaskMapper
{
    public FlowTask selectFlowTaskById(Long taskId);

    /** 我的待办（关联实例信息） */
    public List<FlowTask> selectTodoTaskList(FlowTask flowTask);

    /** 实例全部任务（审批历史） */
    public List<FlowTask> selectTaskListByInstanceId(Long instanceId);

    public int insertFlowTask(FlowTask flowTask);

    public int updateFlowTask(FlowTask flowTask);

    /** 将同节点其余待审批任务置失效 */
    public int invalidPendingByNode(@Param("instanceId") Long instanceId, @Param("nodeId") Long nodeId, @Param("excludeTaskId") Long excludeTaskId);

    /** 统计节点待审批任务数（会签用） */
    public int countPendingByNode(@Param("instanceId") Long instanceId, @Param("nodeId") Long nodeId);

    /** 按角色+部门查询用户ID（审批人解析） */
    public List<Long> selectUserIdsByRoleAndDept(@Param("roleId") Long roleId, @Param("deptId") Long deptId);

    /** 按角色查询全部用户ID */
    public List<Long> selectUserIdsByRole(@Param("roleId") Long roleId);
}
