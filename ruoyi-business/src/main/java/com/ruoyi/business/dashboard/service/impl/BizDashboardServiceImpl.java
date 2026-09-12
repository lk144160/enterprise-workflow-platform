package com.ruoyi.business.dashboard.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.business.dashboard.mapper.BizDashboardMapper;
import com.ruoyi.business.dashboard.service.IBizDashboardService;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysNoticeService;

/**
 * 工作台统计服务实现
 * 统计卡片按角色差异化返回，非授权角色不含对应数据集
 *
 * @author renovationops
 */
@Service
public class BizDashboardServiceImpl implements IBizDashboardService
{
    /** 角色标识 */
    private static final String ROLE_MARKET_MANAGER = "market_manager";
    private static final String ROLE_DESIGN_DIRECTOR = "design_director";
    private static final String ROLE_ADMIN_FINANCE = "admin_finance";
    private static final String ROLE_GM = "gm";

    private static final int NOTICE_LIMIT = 5;

    @Autowired
    private BizDashboardMapper dashboardMapper;

    @Autowired
    private ISysNoticeService noticeService;

    @Override
    public Map<String, Object> summary()
    {
        Long userId = SecurityUtils.getUserId();
        Map<String, Object> data = new HashMap<>();
        data.put("todoCount", dashboardMapper.countMyTodoTasks(userId));
        data.put("notices", latestNotices());

        boolean market = hasRole(ROLE_MARKET_MANAGER) || hasRole(ROLE_GM) || SecurityUtils.isAdmin(userId);
        boolean design = hasRole(ROLE_DESIGN_DIRECTOR) || hasRole(ROLE_GM) || SecurityUtils.isAdmin(userId);
        boolean finance = hasRole(ROLE_ADMIN_FINANCE) || hasRole(ROLE_GM) || SecurityUtils.isAdmin(userId);

        if (market)
        {
            Map<String, Object> card = new HashMap<>();
            card.put("monthNewCustomers", dashboardMapper.countMonthNewCustomers());
            card.put("monthQuotes", dashboardMapper.countMonthQuotes());
            card.put("monthSignAmount", dashboardMapper.sumMonthSignAmount());
            card.put("monthDepositAmount", dashboardMapper.sumMonthDepositReceived());
            card.put("overduePayments", dashboardMapper.countOverduePayments());
            data.put("market", card);
        }
        if (design)
        {
            Map<String, Object> card = new HashMap<>();
            card.put("pendingDrawings", dashboardMapper.countPendingDrawings());
            data.put("design", card);
        }
        if (finance)
        {
            Map<String, Object> card = new HashMap<>();
            card.put("uncollectedPayments", dashboardMapper.countUncollectedPayments());
            card.put("pendingPayExpenses", dashboardMapper.countPendingPayExpenses());
            card.put("lowStockCount", dashboardMapper.countLowStock());
            data.put("finance", card);
        }
        return data;
    }

    /** 最新公告（前端工作台展示） */
    private List<SysNotice> latestNotices()
    {
        List<SysNotice> notices = noticeService.selectNoticeList(new SysNotice());
        return notices.size() > NOTICE_LIMIT ? notices.subList(0, NOTICE_LIMIT) : notices;
    }

    private boolean hasRole(String roleKey)
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (user.getRoles() == null)
        {
            return false;
        }
        for (SysRole role : user.getRoles())
        {
            if (roleKey.equals(role.getRoleKey()))
            {
                return true;
            }
        }
        return false;
    }

    // ==================== 经营数据大屏 ====================

    @Override
    public Map<String, Object> screen(int days)
    {
        if (days < 7 || days > 365)
        {
            days = 30;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String beginTime = LocalDate.now().minusDays(days - 1L).format(fmt);
        String endTime = LocalDate.now().format(fmt);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("days", days);
        data.put("beginTime", beginTime);
        data.put("endTime", endTime);

        // ---- 1. 新增客户 ----
        Map<String, Object> customer = new LinkedHashMap<>();
        List<String> dates = buildDateSeries(beginTime, endTime);
        customer.put("trend", fillDaily(dates, dashboardMapper.countCustomerDaily(beginTime, endTime), "count"));
        customer.put("total", sumDaily(customer.get("trend")));
        customer.put("sourceStats", dashboardMapper.countCustomerBySource(beginTime, endTime));
        customer.put("detailList", dashboardMapper.selectCustomerDetails(beginTime, endTime));
        data.put("customer", customer);

        // ---- 2. 客户到访 ----
        Map<String, Object> visit = new LinkedHashMap<>();
        visit.put("trend", fillDaily(dates, dashboardMapper.countVisitDaily(beginTime, endTime), "count"));
        visit.put("total", sumDaily(visit.get("trend")));
        visit.put("recentList", dashboardMapper.selectRecentVisits(beginTime, endTime));
        visit.put("detailList", dashboardMapper.selectVisitDetails(beginTime, endTime));
        data.put("visit", visit);

        // ---- 3. 报价情况 ----
        Map<String, Object> quote = new LinkedHashMap<>();
        quote.put("trend", fillDaily(dates, dashboardMapper.countQuoteDaily(beginTime, endTime), "count"));
        quote.put("total", sumDaily(quote.get("trend")));
        quote.put("detailList", dashboardMapper.selectQuoteDetails(beginTime, endTime));
        data.put("quote", quote);

        // ---- 4. 定金情况 ----
        Map<String, Object> deposit = new LinkedHashMap<>();
        deposit.put("trend", fillDaily(dates, dashboardMapper.sumDepositDaily(beginTime, endTime), "amount"));
        Map<String, Object> depositSummary = new LinkedHashMap<>();
        BigDecimal receivedAmount = BigDecimal.ZERO;
        int receivedCount = 0;
        BigDecimal deductedAmount = BigDecimal.ZERO;
        int deductedCount = 0;
        int refundCount = 0;
        for (Map<String, Object> row : dashboardMapper.sumDepositByStatus(beginTime, endTime))
        {
            String status = String.valueOf(row.get("status"));
            BigDecimal amount = toDecimal(row.get("amount"));
            int cnt = toInt(row.get("cnt"));
            if ("0".equals(status))
            {
                receivedAmount = receivedAmount.add(amount);
                receivedCount += cnt;
            }
            else if ("1".equals(status))
            {
                deductedAmount = deductedAmount.add(amount);
                deductedCount += cnt;
            }
            else if ("3".equals(status))
            {
                refundCount += cnt;
            }
        }
        depositSummary.put("receivedCount", receivedCount);
        depositSummary.put("receivedAmount", receivedAmount);
        depositSummary.put("deductedCount", deductedCount);
        depositSummary.put("deductedAmount", deductedAmount);
        depositSummary.put("refundCount", refundCount);
        deposit.put("summary", depositSummary);
        deposit.put("total", sumDaily(deposit.get("trend")));
        deposit.put("detailList", dashboardMapper.selectDepositDetails(beginTime, endTime));
        data.put("deposit", deposit);

        // ---- 5. 图纸产出（区间内新增的当前版本图纸，按类型） ----
        data.put("drawing", dashboardMapper.countDrawingByType(beginTime, endTime));

        // ---- 6. 施工进度（多维度量化） ----
        Map<String, Object> site = new LinkedHashMap<>();
        site.put("statusStats", dashboardMapper.countSiteByStatus());
        site.put("stageStats", dashboardMapper.countStageRunning());
        List<Map<String, Object>> sites = dashboardMapper.selectSiteProgressList();
        LocalDate today = LocalDate.now();
        int preparing = 0;
        int running = 0;
        int finished = 0;
        int overdue = 0;
        int soonFinish = 0;
        double progressSum = 0;
        for (Map<String, Object> s : sites)
        {
            int done = toInt(s.get("doneStages"));
            int total = toInt(s.get("totalStages"));
            double progress = total > 0 ? Math.round(done * 1000.0 / total) / 10.0 : 0;
            s.put("progress", progress);
            String st = String.valueOf(s.get("status"));
            LocalDate planStart = parseDate(s.get("planStart"));
            LocalDate planEnd = parseDate(s.get("planEnd"));
            if ("0".equals(st))
            {
                preparing++;
                if (planStart != null)
                {
                    // 距预计开工天数
                    s.put("daysToStart", ChronoUnit.DAYS.between(today, planStart));
                }
            }
            else if ("1".equals(st))
            {
                running++;
                progressSum += progress;
                LocalDate actualStart = parseDate(s.get("actualStart"));
                if (actualStart != null)
                {
                    s.put("openDays", ChronoUnit.DAYS.between(actualStart, today));
                }
                if (planStart != null && planEnd != null && !planEnd.isBefore(planStart))
                {
                    long totalDays = ChronoUnit.DAYS.between(planStart, planEnd);
                    long passed = ChronoUnit.DAYS.between(planStart, today);
                    // 工期时间消耗率（0~100）
                    double timeProgress = totalDays > 0 ? clamp(passed * 100.0 / totalDays, 0, 100) : 100;
                    s.put("timeProgress", Math.round(timeProgress * 10.0) / 10.0);
                    long remain = ChronoUnit.DAYS.between(today, planEnd);
                    s.put("remainDays", remain);
                    if (remain < 0)
                    {
                        overdue++;
                    }
                    else if (remain <= 15)
                    {
                        soonFinish++;
                    }
                    // 进度偏差 = 阶段完成率 - 工期消耗率（正=超前，负=滞后）
                    s.put("deviation", Math.round((progress - timeProgress) * 10.0) / 10.0);
                }
            }
            else if ("2".equals(st))
            {
                finished++;
            }
        }
        Map<String, Object> siteSummary = new LinkedHashMap<>();
        siteSummary.put("total", sites.size());
        siteSummary.put("preparing", preparing);
        siteSummary.put("running", running);
        siteSummary.put("finished", finished);
        siteSummary.put("overdue", overdue);
        siteSummary.put("soonFinish", soonFinish);
        siteSummary.put("avgProgress", running > 0 ? Math.round(progressSum * 10.0 / running) / 10.0 : 0);
        site.put("summary", siteSummary);
        site.put("siteList", sites);
        data.put("site", site);

        // ---- 7. 签约情况（含下期预计签约） ----
        Map<String, Object> sign = new LinkedHashMap<>();
        Map<String, Object> signSummary = dashboardMapper.sumSignRange(beginTime, endTime);
        signSummary.putIfAbsent("cnt", 0);
        signSummary.putIfAbsent("amount", BigDecimal.ZERO);
        sign.put("summary", signSummary);
        // 签约趋势：每日数量+金额双轴
        List<Map<String, Object>> signDailyRaw = dashboardMapper.countSignDaily(beginTime, endTime);
        List<Map<String, Object>> signTrend = new ArrayList<>();
        for (String date : dates)
        {
            int cnt = 0;
            BigDecimal amount = BigDecimal.ZERO;
            for (Map<String, Object> row : signDailyRaw)
            {
                if (date.equals(String.valueOf(row.get("date"))))
                {
                    cnt = toInt(row.get("cnt"));
                    amount = toDecimal(row.get("amount"));
                    break;
                }
            }
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", date);
            point.put("cnt", cnt);
            point.put("amount", amount);
            signTrend.add(point);
        }
        sign.put("trend", signTrend);
        sign.put("intentionStats", dashboardMapper.countIntentionOfUnsigned());
        sign.put("quotePendingList", dashboardMapper.selectQuotePendingList());
        sign.put("detailList", dashboardMapper.selectSignDetails(beginTime, endTime));
        data.put("sign", sign);

        // ---- 8. 合同收款 ----
        Map<String, Object> payment = new LinkedHashMap<>();
        payment.put("trend", fillDaily(dates, dashboardMapper.sumPaymentDaily(beginTime, endTime), "amount"));
        Map<String, Object> received = dashboardMapper.sumPaymentReceived(beginTime, endTime);
        received.putIfAbsent("cnt", 0);
        received.putIfAbsent("amount", BigDecimal.ZERO);
        payment.put("receivedSummary", received);
        payment.put("total", sumDaily(payment.get("trend")));
        payment.put("dueList", dashboardMapper.selectPaymentDueList());
        payment.put("detailList", dashboardMapper.selectPaymentDetails(beginTime, endTime));
        data.put("payment", payment);

        // ---- 9. 人员多维度对比 ----
        data.put("designerStats", dashboardMapper.selectDesignerStats(beginTime, endTime));
        data.put("salesmanStats", dashboardMapper.selectSalesmanStats(beginTime, endTime));

        return data;
    }

    /** 生成连续日期序列（yyyy-MM-dd） */
    private List<String> buildDateSeries(String beginTime, String endTime)
    {
        List<String> dates = new ArrayList<>();
        LocalDate begin = LocalDate.parse(beginTime);
        LocalDate end = LocalDate.parse(endTime);
        for (LocalDate d = begin; !d.isAfter(end); d = d.plusDays(1))
        {
            dates.add(d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        return dates;
    }

    /** 将按日分组结果填充为连续日期序列，缺失日期补 0 */
    private List<Map<String, Object>> fillDaily(List<String> dates, List<Map<String, Object>> rows, String valueKey)
    {
        Map<String, Object> byDate = new HashMap<>();
        for (Map<String, Object> row : rows)
        {
            byDate.put(String.valueOf(row.get("date")), row.get(valueKey));
        }
        List<Map<String, Object>> result = new ArrayList<>(dates.size());
        for (String date : dates)
        {
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", date);
            Object value = byDate.get(date);
            if ("amount".equals(valueKey))
            {
                point.put("value", toDecimal(value));
            }
            else
            {
                point.put("value", toInt(value));
            }
            result.add(point);
        }
        return result;
    }

    /** 汇总趋势合计 */
    private BigDecimal sumDaily(Object trend)
    {
        BigDecimal total = BigDecimal.ZERO;
        if (trend instanceof List)
        {
            for (Object point : (List<?>) trend)
            {
                if (point instanceof Map)
                {
                    total = total.add(toDecimal(((Map<?, ?>) point).get("value")));
                }
            }
        }
        return total;
    }

    private int toInt(Object value)
    {
        if (value == null)
        {
            return 0;
        }
        if (value instanceof Number)
        {
            return ((Number) value).intValue();
        }
        try
        {
            return new BigDecimal(String.valueOf(value)).intValue();
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    /** 安全解析日期（兼容 LocalDate/java.sql.Date/yyyy-MM-dd 字符串） */
    private LocalDate parseDate(Object value)
    {
        if (value == null)
        {
            return null;
        }
        if (value instanceof LocalDate)
        {
            return (LocalDate) value;
        }
        if (value instanceof java.sql.Date)
        {
            return ((java.sql.Date) value).toLocalDate();
        }
        try
        {
            String text = String.valueOf(value);
            return LocalDate.parse(text.length() > 10 ? text.substring(0, 10) : text);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private double clamp(double value, double min, double max)
    {
        return Math.max(min, Math.min(max, value));
    }

    private BigDecimal toDecimal(Object value)
    {
        if (value == null)
        {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal)
        {
            return (BigDecimal) value;
        }
        try
        {
            return new BigDecimal(String.valueOf(value));
        }
        catch (NumberFormatException e)
        {
            return BigDecimal.ZERO;
        }
    }
}
