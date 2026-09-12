package com.ruoyi.business.flow.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.business.flow.domain.FlowInstance;

/**
 * 审批流实例 Mapper 接口
 *
 * @author renovationops
 */
public interface FlowInstanceMapper
{
    public FlowInstance selectFlowInstanceById(Long instanceId);

    /** 查询业务单据当前进行中的实例 */
    public FlowInstance selectActiveInstanceByBiz(@Param("bizType") String bizType, @Param("bizId") Long bizId);

    /** 查询业务单据最近一次实例（含已结束，回调取金额用） */
    public FlowInstance selectLatestInstanceByBiz(@Param("bizType") String bizType, @Param("bizId") Long bizId);

    public List<FlowInstance> selectFlowInstanceList(FlowInstance flowInstance);

    /** 我的申请列表 */
    public List<FlowInstance> selectMyInstanceList(FlowInstance flowInstance);

    public int insertFlowInstance(FlowInstance flowInstance);

    public int updateFlowInstance(FlowInstance flowInstance);

    public int deleteFlowInstanceById(Long instanceId);
}
