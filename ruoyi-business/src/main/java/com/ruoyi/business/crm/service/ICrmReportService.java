package com.ruoyi.business.crm.service;

import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import com.ruoyi.business.crm.domain.CrmReportQuery;

/**
 * 客情统计服务接口
 *
 * @author renovationops
 */
public interface ICrmReportService
{
    /**
     * 客情统计（汇总 + 客户/跟进/签约明细）
     */
    public Map<String, Object> statistics(CrmReportQuery query);

    /**
     * 导出客情统计 Excel（客户/跟进/签约三个明细 Sheet）
     */
    public void exportExcel(HttpServletResponse response, CrmReportQuery query);
}
