package com.ruoyi.business.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.dashboard.service.IBizDashboardService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 工作台统计
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/dashboard")
public class BizDashboardController extends BaseController
{
    @Autowired
    private IBizDashboardService dashboardService;

    /**
     * 工作台汇总（待办数/公告 + 按角色差异化的业务统计卡片）
     */
    @GetMapping("/summary")
    public AjaxResult summary()
    {
        return success(dashboardService.summary());
    }

    /**
     * 经营数据大屏（近 N 天，默认 30）：新增客户/到访/报价/定金/图纸/施工/签约/收款
     */
    @GetMapping("/screen")
    public AjaxResult screen(@RequestParam(value = "days", defaultValue = "30") int days)
    {
        return success(dashboardService.screen(days));
    }
}
