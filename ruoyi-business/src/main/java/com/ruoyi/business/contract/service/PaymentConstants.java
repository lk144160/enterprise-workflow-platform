package com.ruoyi.business.contract.service;

/**
 * 收款相关常量
 *
 * @author renovationops
 */
public class PaymentConstants
{
    /** 到期触发类型 */
    public static final String TRIGGER_CONTRACT_EFFECT = "contract_effect";
    public static final String TRIGGER_DESIGN_FINISH = "design_finish";
    public static final String TRIGGER_DISCLOSURE_CONFIRM = "disclosure_confirm";
    public static final String TRIGGER_FINISH_REGISTER = "finish_register";

    /** 计划状态 */
    public static final String PLAN_STATUS_PENDING = "0";
    public static final String PLAN_STATUS_WAITING = "1";
    public static final String PLAN_STATUS_SETTLED = "2";
    public static final String PLAN_STATUS_REDUCED = "3";
    public static final String PLAN_STATUS_INVALID = "4";

    /** 四期默认配置（期次、名称、比例、触发类型） */
    public static final String[][] DEFAULT_PERIODS = {
            { "1", "签约款", "30", TRIGGER_CONTRACT_EFFECT },
            { "2", "设计款", "30", TRIGGER_DESIGN_FINISH },
            { "3", "施工款", "30", TRIGGER_DISCLOSURE_CONFIRM },
            { "4", "尾款", "10", TRIGGER_FINISH_REGISTER }
    };
}
