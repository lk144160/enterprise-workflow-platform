package com.ruoyi.business.dashboard.service;

import java.util.Map;

/**
 * 工作台统计Service接口
 *
 * @author renovationops
 */
public interface IBizDashboardService
{
    /**
     * 汇总当前用户工作台数据：
     * 公共返回待办数与公告；业务统计卡片按角色差异化返回
     */
    public Map<String, Object> summary();

    /**
     * 经营数据大屏（近 N 天）：
     * 新增客户/到访/报价/定金/图纸/施工/签约（含预计签约）/收款全维度统计
     */
    public Map<String, Object> screen(int days);
}
