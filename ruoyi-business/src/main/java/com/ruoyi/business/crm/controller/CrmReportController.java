package com.ruoyi.business.crm.controller;

import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.crm.domain.CrmReportQuery;
import com.ruoyi.business.crm.service.ICrmReportService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;

/**
 * 客情统计
 *
 * @author renovationops
 */
@RestController
@RequestMapping("/crm/report")
public class CrmReportController extends BaseController
{
    @Autowired
    private ICrmReportService reportService;

    /**
     * 客情统计（汇总 + 客户/跟进/签约明细）
     */
    @PreAuthorize("@ss.hasPermi('crm:report:list')")
    @GetMapping("/list")
    public AjaxResult list(CrmReportQuery query)
    {
        return success(reportService.statistics(query));
    }

    /**
     * 导出客情统计 Excel
     */
    @PreAuthorize("@ss.hasPermi('crm:report:export')")
    @Log(title = "客情统计", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmReportQuery query)
    {
        reportService.exportExcel(response, query);
    }
}
