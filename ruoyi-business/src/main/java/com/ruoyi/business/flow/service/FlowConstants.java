package com.ruoyi.business.flow.service;

/**
 * 审批流常量
 *
 * @author renovationops
 */
public class FlowConstants
{
    /** 流程编码 */
    public static final String QUOTE_APPROVAL = "QUOTE_APPROVAL";
    public static final String CONTRACT_APPROVAL = "CONTRACT_APPROVAL";
    public static final String PAYMENT_REDUCE_APPROVAL = "PAYMENT_REDUCE_APPROVAL";
    public static final String DISCLOSURE_APPROVAL = "DISCLOSURE_APPROVAL";
    public static final String EXPENSE_APPROVAL = "EXPENSE_APPROVAL";
    public static final String STOCK_OUT_APPROVAL = "STOCK_OUT_APPROVAL";
    public static final String DEPOSIT_REFUND_APPROVAL = "DEPOSIT_REFUND_APPROVAL";

    /** 业务类型 */
    public static final String BIZ_TYPE_QUOTE = "quote";
    public static final String BIZ_TYPE_CONTRACT = "contract";
    public static final String BIZ_TYPE_PAYMENT_REDUCE = "payment_reduce";
    public static final String BIZ_TYPE_DISCLOSURE = "disclosure";
    public static final String BIZ_TYPE_EXPENSE = "expense";
    public static final String BIZ_TYPE_STOCK_OUT = "stock_out";
    public static final String BIZ_TYPE_DEPOSIT_REFUND = "deposit_refund";

    /** 实例状态 */
    public static final String INSTANCE_RUNNING = "1";
    public static final String INSTANCE_APPROVED = "2";
    public static final String INSTANCE_REJECTED = "3";
    public static final String INSTANCE_CANCELED = "4";

    /** 任务状态 */
    public static final String TASK_PENDING = "0";
    public static final String TASK_AGREE = "1";
    public static final String TASK_REJECT = "2";
    public static final String TASK_INVALID = "3";
    public static final String TASK_TRANSFER = "4";

    /** 审批动作 */
    public static final String ACTION_AGREE = "agree";
    public static final String ACTION_REJECT = "reject";
}
