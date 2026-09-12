package com.ruoyi.business.dashboard.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 工作台统计 Mapper 接口
 *
 * @author renovationops
 */
public interface BizDashboardMapper
{
    /** 我的待办审批数 */
    public int countMyTodoTasks(@Param("userId") Long userId);

    /** 本月新增线索数 */
    public int countMonthNewCustomers();

    /** 本月报价数 */
    public int countMonthQuotes();

    /** 本月签约金额 */
    public BigDecimal sumMonthSignAmount();

    /** 本月已收定金金额（不含已退还） */
    public BigDecimal sumMonthDepositReceived();

    /** 逾期应收期次数（待收款且计划收款日已过） */
    public int countOverduePayments();

    /** 待确认图纸数（当前版本） */
    public int countPendingDrawings();

    /** 待收款期次数 */
    public int countUncollectedPayments();

    /** 待打款报销数（已审批通过未打款） */
    public int countPendingPayExpenses();

    /** 低库存预警数 */
    public int countLowStock();

    // ==================== 大屏统计 ====================

    /** 每日新增客户数 */
    public List<Map<String, Object>> countCustomerDaily(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 每日到访次数 */
    public List<Map<String, Object>> countVisitDaily(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 每日报价单数 */
    public List<Map<String, Object>> countQuoteDaily(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 每日定金收款金额（净额，不含已退还） */
    public List<Map<String, Object>> sumDepositDaily(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 每日实收金额 */
    public List<Map<String, Object>> sumPaymentDaily(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 客户按来源分组 */
    public List<Map<String, Object>> countCustomerBySource(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 定金状态汇总（区间内） */
    public List<Map<String, Object>> sumDepositByStatus(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 图纸按类型统计（当前版本） */
    public List<Map<String, Object>> countDrawingByType(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 工地按状态统计 */
    public List<Map<String, Object>> countSiteByStatus();

    /** 工地进度列表（含当前阶段与完成率） */
    public List<Map<String, Object>> selectSiteProgressList();

    /** 进行中阶段分布 */
    public List<Map<String, Object>> countStageRunning();

    /** 签约汇总（区间内） */
    public Map<String, Object> sumSignRange(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 每日签约数量与金额 */
    public List<Map<String, Object>> countSignDaily(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 未签约客户意向等级分布 */
    public List<Map<String, Object>> countIntentionOfUnsigned();

    /** 已报价未签约客户（预计签约池，全量按意向等级+报价排序） */
    public List<Map<String, Object>> selectQuotePendingList();

    /** 未来7天待收款期次 */
    public List<Map<String, Object>> selectPaymentDueList();

    /** 区间内收款汇总 */
    public Map<String, Object> sumPaymentReceived(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 到访明细（最近到访，供大屏滚动展示） */
    public List<Map<String, Object>> selectRecentVisits(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    // ==================== 趋势明细查询（tooltip 展示当日客户） ====================

    /** 新增客户明细（区间内，按日期） */
    public List<Map<String, Object>> selectCustomerDetails(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 到访明细（区间内，按日期） */
    public List<Map<String, Object>> selectVisitDetails(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 报价明细（区间内，按日期） */
    public List<Map<String, Object>> selectQuoteDetails(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 定金明细（区间内，按日期） */
    public List<Map<String, Object>> selectDepositDetails(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 签约明细（区间内，按日期） */
    public List<Map<String, Object>> selectSignDetails(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 收款明细（区间内，按日期） */
    public List<Map<String, Object>> selectPaymentDetails(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    // ==================== 人员多维度对比统计 ====================

    /** 设计师多维度统计 */
    public List<Map<String, Object>> selectDesignerStats(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /** 业务员多维度统计 */
    public List<Map<String, Object>> selectSalesmanStats(@Param("beginTime") String beginTime, @Param("endTime") String endTime);
}
