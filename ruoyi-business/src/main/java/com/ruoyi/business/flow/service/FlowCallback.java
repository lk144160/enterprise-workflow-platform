package com.ruoyi.business.flow.service;

/**
 * 审批流业务回调接口
 * 业务模块实现本接口并注册为 Spring Bean，引擎按 bizType 分发回调
 *
 * @author renovationops
 */
public interface FlowCallback
{
    /**
     * 回调对应的业务类型（与 flow_definition.biz_type 一致）
     */
    String bizType();

    /**
     * 审批全部通过
     */
    void onApproved(Long bizId);

    /**
     * 任一节点驳回
     */
    void onRejected(Long bizId);

    /**
     * 发起人撤销（业务单据应回到草稿态）
     */
    void onCanceled(Long bizId);
}
