<template>
  <div class="screen">
    <!-- 顶部标题栏 -->
    <div class="screen-header">
      <div class="header-left">
        <img src="@/assets/icons/svg/dashboard.svg" class="logo-img" alt="装修业务运营平台" />
        <span class="sys-title">装修业务运营平台 · 经营数据大屏</span>
      </div>
      <div class="header-right">
        <el-radio-group v-model="days" size="small" @change="loadData">
          <el-radio-button :value="7">近7天</el-radio-button>
          <el-radio-button :value="30">近30天</el-radio-button>
          <el-radio-button :value="90">近90天</el-radio-button>
        </el-radio-group>
        <span class="range-text">{{ screen.beginTime }} ~ {{ screen.endTime }}</span>
        <el-button size="small" icon="Refresh" circle @click="loadData" />
      </div>
    </div>

    <!-- 指标卡 -->
    <div class="kpi-row">
      <div class="kpi-card">
        <div class="kpi-value">{{ kpi.customerTotal }}</div>
        <div class="kpi-label">新增客户</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-value">{{ kpi.visitTotal }}</div>
        <div class="kpi-label">客户到访</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-value">{{ kpi.quoteTotal }}</div>
        <div class="kpi-label">报价单数</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-value">￥{{ money(kpi.depositTotal) }}</div>
        <div class="kpi-label">定金净收</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-value">{{ kpi.signCount }}</div>
        <div class="kpi-label">签约数量</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-value">￥{{ money(kpi.signAmount) }}</div>
        <div class="kpi-label">签约金额</div>
      </div>
      <div class="kpi-card">
        <div class="kpi-value">￥{{ money(kpi.receivedAmount) }}</div>
        <div class="kpi-label">区间已收款</div>
      </div>
      <div class="kpi-card warn">
        <div class="kpi-value">{{ screen.payment?.dueList?.length || 0 }}</div>
        <div class="kpi-label">未来7天待收期次</div>
      </div>
    </div>

    <!-- 第一行：客户 -->
    <div class="grid-row">
      <panel title="新增客户趋势">
        <div ref="customerTrendRef" class="chart chart-lg"></div>
      </panel>
      <panel title="新增客户来源分布">
        <div ref="customerSourceRef" class="chart chart-lg"></div>
      </panel>
      <panel title="客户到访趋势与近期明细">
        <div ref="visitTrendRef" class="chart chart-sm2"></div>
        <div class="visit-list">
          <div v-if="!visitList.length" class="empty-tip">暂无到访记录</div>
          <div v-for="(v, i) in visitList" :key="i" class="visit-item">
            <span class="v-customer">{{ v.customer }}</span>
            <span class="v-meta">{{ shortTime(v.followTime) }} · {{ v.visitCount || 1 }}人<template v-if="v.purposeLabel"> · {{ v.purposeLabel }}</template></span>
          </div>
        </div>
      </panel>
    </div>

    <!-- 第二行：报价 / 定金 / 图纸 -->
    <div class="grid-row">
      <panel title="报价情况趋势">
        <div ref="quoteTrendRef" class="chart chart-lg"></div>
      </panel>
      <panel title="定金情况">
        <div class="deposit-summary">
          <div class="deposit-item">
            <span class="d-num">{{ screen.deposit?.summary?.receivedCount || 0 }} 笔</span>
            <span class="d-amt">￥{{ money(screen.deposit?.summary?.receivedAmount) }}</span>
            <span class="d-label">已收定金</span>
          </div>
          <div class="deposit-item">
            <span class="d-num">{{ screen.deposit?.summary?.deductedCount || 0 }} 笔</span>
            <span class="d-amt">￥{{ money(screen.deposit?.summary?.deductedAmount) }}</span>
            <span class="d-label">已抵扣</span>
          </div>
          <div class="deposit-item">
            <span class="d-num">{{ screen.deposit?.summary?.refundCount || 0 }} 笔</span>
            <span class="d-label">已退还</span>
          </div>
        </div>
        <div ref="depositTrendRef" class="chart chart-sm"></div>
      </panel>
      <panel title="图纸产出（区间内，按类型）">
        <div ref="drawingRef" class="chart chart-lg"></div>
      </panel>
    </div>

    <!-- 第三行：施工进度全景 -->
    <div class="grid-row site-row">
      <panel title="工地状态分布">
        <div ref="siteStatusRef" class="chart chart-md"></div>
      </panel>
      <panel title="在建工地 · 施工阶段分布">
        <div ref="siteStageRef" class="chart chart-md"></div>
      </panel>
      <panel title="施工进度明细" class="site-list-panel">
        <div class="site-kpi-strip">
          <div class="sk-item"><b class="c-run">{{ siteSummary.running || 0 }}</b><span>在建</span></div>
          <div class="sk-item"><b class="c-prep">{{ siteSummary.preparing || 0 }}</b><span>待开工</span></div>
          <div class="sk-item"><b class="c-done">{{ siteSummary.finished || 0 }}</b><span>已完工</span></div>
          <div class="sk-item"><b class="c-danger">{{ siteSummary.overdue || 0 }}</b><span>逾期</span></div>
          <div class="sk-item"><b class="c-soon">{{ siteSummary.soonFinish || 0 }}</b><span>半月内完工</span></div>
          <div class="sk-item"><b class="c-run">{{ siteSummary.avgProgress || 0 }}%</b><span>在建均进度</span></div>
        </div>
        <div class="site-list">
          <div v-if="!siteList.length" class="empty-tip">暂无工地</div>
          <div v-for="s in siteList" :key="s.siteId" class="site-item" :class="{ 'is-overdue': s.remainDays < 0 }">
            <div class="site-line1">
              <span class="site-customer">{{ s.customer }}</span>
              <span class="site-right">
                <span v-if="s.area" class="site-area">{{ s.area }}㎡</span>
                <span class="site-status" :class="'st-' + s.status">{{ s.statusLabel }}</span>
              </span>
            </div>
            <div class="site-line2">
              <span class="site-stage">{{ stageText(s) }}</span>
              <span v-if="s.status === '1' && s.deviation != null" class="deviation" :class="deviationClass(s.deviation)">{{ deviationText(s.deviation) }}</span>
            </div>
            <div class="dual-bar">
              <div class="bar-row">
                <span class="bar-label">阶段</span>
                <div class="bar-track"><div class="bar-fill f-stage" :style="{ width: (Number(s.progress) || 0) + '%' }"></div></div>
                <span class="bar-num">{{ s.progress || 0 }}%</span>
              </div>
              <div v-if="s.status === '1'" class="bar-row">
                <span class="bar-label">工期</span>
                <div class="bar-track"><div class="bar-fill f-time" :style="{ width: (Number(s.timeProgress) || 0) + '%' }"></div></div>
                <span class="bar-num">{{ s.timeProgress || 0 }}%</span>
              </div>
            </div>
            <div class="site-line3">
              <span>{{ s.planStart || '-' }} ~ {{ s.planEnd || '-' }}</span>
              <span class="days-info" :class="daysClass(s)">{{ daysText(s) }}</span>
            </div>
            <div v-if="s.designerName || s.supervisorName" class="site-line4">
              <span v-if="s.designerName">设计 {{ s.designerName }}</span>
              <span v-if="s.supervisorName">监理 {{ s.supervisorName }}</span>
            </div>
          </div>
        </div>
      </panel>
    </div>

    <!-- 第四行：签约三维度 -->
    <div class="grid-row">
      <panel title="签约趋势（数量 + 金额）">
        <div ref="signTrendRef" class="chart chart-md"></div>
      </panel>
      <panel title="签约情况 · 未签约客户意向等级">
        <div ref="intentionRef" class="chart chart-md"></div>
      </panel>
      <panel title="下期预计签约（按意向等级 × 报价排序）">
        <el-table :data="screen.sign?.quotePendingList || []" size="small" height="260"
          :header-cell-style="tableHeaderStyle" :cell-style="cellStyle">
          <el-table-column label="客户" prop="customer" min-width="130" show-overflow-tooltip />
          <el-table-column label="意向" prop="intentionLevel" width="55" align="center">
            <template #default="scope">
              <span class="intention-tag" :class="'il-' + scope.row.intentionLevel">{{ scope.row.intentionLevel || '未评' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="最新报价" prop="finalAmount" width="100" align="right">
            <template #default="scope">￥{{ money(scope.row.finalAmount) }}</template>
          </el-table-column>
          <el-table-column label="定金" prop="depositAmount" width="85" align="right">
            <template #default="scope">
              <span :class="scope.row.depositAmount ? 'has-deposit' : ''">{{ scope.row.depositAmount ? '￥' + money(scope.row.depositAmount) : '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="报价日" width="75" align="center">
            <template #default="scope">{{ shortDate(scope.row.quoteTime) }}</template>
          </el-table-column>
        </el-table>
      </panel>
    </div>

    <!-- 第五行：收款 -->
    <div class="grid-row payment-row">
      <panel title="收款情况 · 未来一周待收期次">
        <el-table :data="screen.payment?.dueList || []" size="small" height="220"
          :header-cell-style="tableHeaderStyle" :cell-style="cellStyle">
          <el-table-column label="客户" prop="customer" min-width="140" show-overflow-tooltip />
          <el-table-column label="期次" width="70" align="center">
            <template #default="scope">第{{ scope.row.periodNo }}期</template>
          </el-table-column>
          <el-table-column label="应收金额" prop="planAmount" width="110" align="right">
            <template #default="scope">￥{{ money(scope.row.planAmount) }}</template>
          </el-table-column>
          <el-table-column label="计划收款日" prop="planDate" width="110" align="center" />
        </el-table>
      </panel>
      <panel title="收款金额趋势">
        <div ref="paymentTrendRef" class="chart chart-pay"></div>
      </panel>
    </div>

    <!-- 第六行：人员多维度对比 -->
    <div class="grid-row person-row">
      <panel title="设计师多维度对比">
        <el-table :data="screen.designerStats || []" size="small" height="240"
          :header-cell-style="tableHeaderStyle" :cell-style="cellStyle">
          <el-table-column label="设计师" prop="designer" min-width="80" show-overflow-tooltip />
          <el-table-column label="签约额" width="100" align="right">
            <template #default="scope"><span class="amt-text">￥{{ money(scope.row.signAmount) }}</span></template>
          </el-table-column>
          <el-table-column label="签约数" prop="signCount" width="60" align="center" />
          <el-table-column label="在建" prop="runningSiteCount" width="50" align="center" />
          <el-table-column label="已完工" prop="doneSiteCount" width="60" align="center" />
          <el-table-column label="图纸" prop="drawingCount" width="50" align="center" />
        </el-table>
      </panel>
      <panel title="业务员多维度对比">
        <el-table :data="screen.salesmanStats || []" size="small" height="240"
          :header-cell-style="tableHeaderStyle" :cell-style="cellStyle">
          <el-table-column label="业务员" prop="salesman" min-width="80" show-overflow-tooltip />
          <el-table-column label="新增客户" prop="customerCount" width="70" align="center" />
          <el-table-column label="签约数" prop="signCount" width="60" align="center" />
          <el-table-column label="签约额" width="100" align="right">
            <template #default="scope"><span class="amt-text">￥{{ money(scope.row.signAmount) }}</span></template>
          </el-table-column>
          <el-table-column label="到访" prop="visitCount" width="50" align="center" />
          <el-table-column label="报价" prop="quoteCount" width="50" align="center" />
        </el-table>
      </panel>
    </div>
  </div>
</template>

<script setup name="Index">
import * as echarts from "echarts";
import { defineComponent, h } from "vue";
import { getScreenData } from "@/api/biz/dashboard";

/** 深色面板容器（局部组件） */
const Panel = defineComponent({
  name: "ScreenPanel",
  props: { title: String },
  setup(props, { slots }) {
    return () => h("div", { class: "panel" }, [
      h("div", { class: "panel-title" }, props.title),
      h("div", { class: "panel-body" }, slots.default ? slots.default() : [])
    ]);
  }
});

const days = ref(30);
const screen = ref({});

const chartInstances = [];

/** 折线/面积趋势图配置（支持 tooltip 显示当日客户明细） */
function trendOption(points, color, isArea, detailList) {
  const dates = points.map(p => p.date.slice(5));
  const fullDates = points.map(p => p.date);
  const values = points.map(p => Number(p.value));
  const tooltipFormatter = detailList ? function(params) {
    const idx = params[0].dataIndex;
    const fullDate = fullDates[idx];
    let html = fullDate + '<br/>数量: <b>' + values[idx] + '</b>';
    const details = detailList.filter(d => d.date === fullDate);
    if (details.length) {
      html += '<div style="margin-top:4px;border-top:1px solid #1e4b8f;padding-top:4px;max-height:180px;overflow-y:auto">';
      details.forEach((d, i) => {
        if (i >= 10) return;
        html += '<div>' + d.customer;
        if (d.amount) html += ' ￥' + money(d.amount);
        if (d.source) html += ' [' + d.source + ']';
        if (d.statusLabel) html += ' [' + d.statusLabel + ']';
        if (d.visitCount) html += ' ' + d.visitCount + '人';
        if (d.purpose) html += ' ' + d.purpose;
        if (d.designer) html += ' 设计:' + d.designer;
        html += '</div>';
      });
      if (details.length > 10) html += '<div style="color:#6f93c4">...共' + details.length + '条</div>';
      html += '</div>';
    }
    return html;
  } : undefined;
  return {
    grid: { left: 40, right: 12, top: 22, bottom: 24 },
    tooltip: { trigger: "axis", backgroundColor: "rgba(8,22,48,.92)", borderColor: "#1e4b8f", textStyle: { color: "#cfe3ff" }, appendToBody: true, extraCssText: "z-index: 999", formatter: tooltipFormatter },
    xAxis: { type: "category", data: dates, axisLine: { lineStyle: { color: "#1e4b8f" } }, axisLabel: { color: "#6f93c4", interval: "auto" } },
    yAxis: { type: "value", splitLine: { lineStyle: { color: "rgba(30,75,143,.25)" } }, axisLabel: { color: "#6f93c4" } },
    series: [{
      type: isArea ? "line" : "bar",
      data: values,
      smooth: true,
      barMaxWidth: 14,
      itemStyle: { color, borderRadius: isArea ? 0 : [3, 3, 0, 0] },
      lineStyle: { color, width: 2 },
      areaStyle: isArea ? { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
        { offset: 0, color: color + "66" }, { offset: 1, color: color + "05" }
      ]) } : undefined
    }]
  };
}

/** 环形图配置（detailMap 可选，tooltip 展示对应工地明细） */
function pieOption(rows, colors, detailMap) {
  const tooltipFormatter = detailMap ? function(params) {
    const name = params.name;
    let html = name + ': <b>' + params.value + '</b>个工地';
    const sites = detailMap[name] || [];
    if (sites.length) {
      html += '<div style="margin-top:4px;border-top:1px solid #1e4b8f;padding-top:4px;max-height:180px;overflow-y:auto">';
      sites.forEach((s, i) => {
        if (i >= 10) return;
        html += '<div>' + s.customer;
        if (s.currentStage) html += ' · ' + s.currentStage;
        if (s.progress != null) html += ' ' + s.progress + '%';
        html += '</div>';
      });
      if (sites.length > 10) html += '<div style="color:#6f93c4">...共' + sites.length + '个</div>';
      html += '</div>';
    }
    return html;
  } : undefined;
  return {
    tooltip: { trigger: "item", backgroundColor: "rgba(8,22,48,.92)", borderColor: "#1e4b8f", textStyle: { color: "#cfe3ff" }, appendToBody: true, extraCssText: "z-index: 999", formatter: tooltipFormatter },
    legend: { bottom: 0, textStyle: { color: "#8fb2e0", fontSize: 11 }, itemWidth: 10, itemHeight: 10 },
    series: [{
      type: "pie", radius: ["42%", "68%"], center: ["50%", "44%"],
      label: { color: "#cfe3ff", fontSize: 11 },
      labelLine: { lineStyle: { color: "#33517e" } },
      data: (rows || []).map((r, i) => ({ name: r.name, value: Number(r.value), itemStyle: { color: colors[i % colors.length] } }))
    }]
  };
}

/** 横向柱图配置（detailMap 可选，tooltip 展示对应工地明细） */
function barYOption(rows, colors, detailMap) {
  const names = (rows || []).map(r => r.name);
  const values = (rows || []).map(r => Number(r.value));
  const tooltipFormatter = detailMap ? function(params) {
    const name = params[0].name;
    let html = name + ': <b>' + params[0].value + '</b>个工地';
    const sites = detailMap[name] || [];
    if (sites.length) {
      html += '<div style="margin-top:4px;border-top:1px solid #1e4b8f;padding-top:4px;max-height:180px;overflow-y:auto">';
      sites.forEach((s, i) => {
        if (i >= 10) return;
        html += '<div>' + s.customer;
        if (s.designerName) html += ' 设计:' + s.designerName;
        if (s.progress != null) html += ' ' + s.progress + '%';
        html += '</div>';
      });
      if (sites.length > 10) html += '<div style="color:#6f93c4">...共' + sites.length + '个</div>';
      html += '</div>';
    }
    return html;
  } : undefined;
  return {
    grid: { left: 80, right: 30, top: 10, bottom: 22 },
    tooltip: { trigger: "axis", backgroundColor: "rgba(8,22,48,.92)", borderColor: "#1e4b8f", textStyle: { color: "#cfe3ff" }, appendToBody: true, extraCssText: "z-index: 999", formatter: tooltipFormatter, axisPointer: { type: "shadow" } },
    xAxis: { type: "value", splitLine: { lineStyle: { color: "rgba(30,75,143,.25)" } }, axisLabel: { color: "#6f93c4" } },
    yAxis: { type: "category", data: names, axisLine: { lineStyle: { color: "#1e4b8f" } }, axisLabel: { color: "#8fb2e0" } },
    series: [{
      type: "bar", data: values, barMaxWidth: 16,
      itemStyle: { borderRadius: [0, 4, 4, 0], color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
        { offset: 0, color: colors[0] }, { offset: 1, color: colors[1] }
      ]) }
    }]
  };
}

/** 签约趋势双轴图（柱=数量，折线=金额，tooltip 显示当日签约客户） */
function signTrendOption(points, detailList) {
  const dates = points.map(p => p.date.slice(5));
  const fullDates = points.map(p => p.date);
  const counts = points.map(p => Number(p.cnt));
  const amounts = points.map(p => Number(p.amount));
  const tooltipFormatter = detailList ? function(params) {
    const idx = params[0].dataIndex;
    const fullDate = fullDates[idx];
    let html = fullDate + '<br/>签约数: <b>' + counts[idx] + '</b><br/>签约金额: <b>￥' + money(amounts[idx]) + '</b>';
    const details = detailList.filter(d => d.date === fullDate);
    if (details.length) {
      html += '<div style="margin-top:4px;border-top:1px solid #1e4b8f;padding-top:4px">';
      details.forEach((d, i) => {
        if (i >= 10) return;
        html += '<div>' + d.customer + ' ￥' + money(d.amount);
        if (d.designer) html += ' 设计:' + d.designer;
        html += '</div>';
      });
      if (details.length > 10) html += '<div style="color:#6f93c4">...共' + details.length + '条</div>';
      html += '</div>';
    }
    return html;
  } : undefined;
  return {
    grid: { left: 44, right: 52, top: 30, bottom: 24 },
    tooltip: { trigger: "axis", backgroundColor: "rgba(8,22,48,.92)", borderColor: "#1e4b8f", textStyle: { color: "#cfe3ff" }, appendToBody: true, extraCssText: "z-index: 999", formatter: tooltipFormatter },
    legend: { data: ["签约数", "签约金额"], top: 0, textStyle: { color: "#8fb2e0", fontSize: 11 }, itemWidth: 12, itemHeight: 10 },
    xAxis: { type: "category", data: dates, axisLine: { lineStyle: { color: "#1e4b8f" } }, axisLabel: { color: "#6f93c4", interval: "auto" } },
    yAxis: [
      { type: "value", name: "数量", nameTextStyle: { color: "#6f93c4" }, splitLine: { lineStyle: { color: "rgba(30,75,143,.25)" } }, axisLabel: { color: "#6f93c4" } },
      { type: "value", name: "金额", nameTextStyle: { color: "#6f93c4" }, splitLine: { show: false }, axisLabel: { color: "#6f93c4", formatter: v => v >= 10000 ? (v / 10000) + "万" : v } }
    ],
    series: [
      {
        name: "签约数", type: "bar", yAxisIndex: 0, data: counts, barMaxWidth: 12,
        itemStyle: { color: "#3a8ee6", borderRadius: [3, 3, 0, 0] }
      },
      {
        name: "签约金额", type: "line", yAxisIndex: 1, data: amounts, smooth: true,
        lineStyle: { color: "#f2b03d", width: 2 }, itemStyle: { color: "#f2b03d" },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: "#f2b03d44" }, { offset: 1, color: "#f2b03d05" }
        ]) }
      }
    ]
  };
}

const PIE_COLORS = ["#00e0c6", "#3a8ee6", "#f2b03d", "#e8648e", "#8f6fe8", "#5ad8a6", "#f7725b", "#4fc3f7"];

function money(v) {
  return Number(v || 0).toLocaleString("zh-CN", { maximumFractionDigits: 0 });
}

/** datetime -> MM-DD HH:mm */
function shortTime(v) {
  return v ? String(v).slice(5, 16) : "-";
}

/** datetime/date -> MM-DD */
function shortDate(v) {
  return v ? String(v).slice(5, 10) : "-";
}

/** 指标卡汇总 */
const kpi = computed(() => ({
  customerTotal: screen.value.customer?.total || 0,
  visitTotal: screen.value.visit?.total || 0,
  quoteTotal: screen.value.quote?.total || 0,
  depositTotal: screen.value.deposit?.total || 0,
  signCount: screen.value.sign?.summary?.cnt || 0,
  signAmount: screen.value.sign?.summary?.amount || 0,
  receivedAmount: screen.value.payment?.receivedSummary?.amount || 0
}));

const siteList = computed(() => screen.value.site?.siteList || []);
const siteSummary = computed(() => screen.value.site?.summary || {});
const visitList = computed(() => screen.value.visit?.recentList || []);

/** 工地当前步骤描述 */
function stageText(s) {
  if (s.status === '2') {
    return '已竣工' + (s.finishDate ? ' · ' + shortDate(s.finishDate) : '');
  }
  if (s.currentStage) {
    return `第${(Number(s.doneStages) || 0) + 1}/${s.totalStages}步 · ${s.currentStage}`;
  }
  if (s.status === '1') {
    return `共${s.totalStages || 0}步 · 暂无进行中工序`;
  }
  return `共${s.totalStages || 0}步 · 待开工`;
}

/** 天数信息（在建：已施工/剩余/逾期；待开工：倒计时） */
function daysText(s) {
  if (s.status === '0') {
    if (s.daysToStart != null) {
      return s.daysToStart > 0 ? `${s.daysToStart}天后开工` : '应开工未开工';
    }
    return '';
  }
  if (s.status === '1') {
    const parts = [];
    if (s.openDays != null) parts.push(`已施工${s.openDays}天`);
    if (s.remainDays != null) {
      parts.push(s.remainDays < 0 ? `逾期${Math.abs(s.remainDays)}天` : `剩${s.remainDays}天`);
    }
    return parts.join(' · ');
  }
  return '';
}

function daysClass(s) {
  if (s.status === '0') return s.daysToStart != null && s.daysToStart <= 0 ? 'd-danger' : 'd-info';
  if (s.status === '1' && s.remainDays != null) {
    if (s.remainDays < 0) return 'd-danger';
    if (s.remainDays <= 15) return 'd-soon';
    return 'd-info';
  }
  return 'd-info';
}

/** 进度偏差文案：正=超前 负=滞后 0=正常 */
function deviationText(d) {
  if (d > 0) return `超前${d}%`;
  if (d < 0) return `滞后${Math.abs(d)}%`;
  return '进度正常';
}

function deviationClass(d) {
  if (d > 0) return 'dev-ahead';
  if (d < 0) return 'dev-behind';
  return 'dev-ok';
}

const tableHeaderStyle = { background: "#0d2350", color: "#8fb2e0", borderBottom: "1px solid #1e4b8f" };
const cellStyle = { background: "transparent", color: "#cfe3ff", borderBottom: "1px solid rgba(30,75,143,.3)" };

function renderCharts() {
  const s = screen.value;
  if (s.customer?.trend) chartInstances.push(chart(customerTrendRef, trendOption(s.customer.trend, "#00e0c6", true, s.customer.detailList)));
  if (s.customer?.sourceStats) chartInstances.push(chart(customerSourceRef, pieOption(s.customer.sourceStats, PIE_COLORS)));
  if (s.visit?.trend) chartInstances.push(chart(visitTrendRef, trendOption(s.visit.trend, "#f2b03d", false, s.visit.detailList)));
  if (s.quote?.trend) chartInstances.push(chart(quoteTrendRef, trendOption(s.quote.trend, "#3a8ee6", false, s.quote.detailList)));
  if (s.deposit?.trend) chartInstances.push(chart(depositTrendRef, trendOption(s.deposit.trend, "#e8648e", true, s.deposit.detailList)));
  // 工地状态饼图：tooltip 显示该状态下的工地明细
  const statusDetailMap = {};
  (s.site?.siteList || []).forEach(site => {
    const label = site.statusLabel || site.status;
    if (!statusDetailMap[label]) statusDetailMap[label] = [];
    statusDetailMap[label].push(site);
  });
  // 阶段柱图：tooltip 显示该阶段的工地明细
  const stageDetailMap = {};
  (s.site?.siteList || []).forEach(site => {
    if (site.currentStage) {
      if (!stageDetailMap[site.currentStage]) stageDetailMap[site.currentStage] = [];
      stageDetailMap[site.currentStage].push(site);
    }
  });
  if (s.drawing) chartInstances.push(chart(drawingRef, barYOption(s.drawing, ["#00e0c6", "#1450a3"])));
  if (s.site?.statusStats) chartInstances.push(chart(siteStatusRef, pieOption(s.site.statusStats, ["#f2b03d", "#00e0c6", "#3a8ee6"], statusDetailMap)));
  if (s.site?.stageStats) chartInstances.push(chart(siteStageRef, barYOption(s.site.stageStats, ["#4fc3f7", "#1450a3"], stageDetailMap)));
  if (s.sign?.intentionStats) chartInstances.push(chart(intentionRef, pieOption(s.sign.intentionStats, ["#e8648e", "#f2b03d", "#3a8ee6", "#5ad8a6", "#8f6fe8"])));
  if (s.sign?.trend) chartInstances.push(chart(signTrendRef, signTrendOption(s.sign.trend, s.sign.detailList)));
  if (s.payment?.trend) chartInstances.push(chart(paymentTrendRef, trendOption(s.payment.trend, "#5ad8a6", true, s.payment.detailList)));
}

function chart(refObj, option) {
  if (!refObj || !refObj.value) return null;
  const inst = echarts.init(refObj.value);
  inst.setOption(option);
  return inst;
}

function loadData() {
  getScreenData(days.value).then(res => {
    disposeCharts();
    screen.value = res.data || {};
    nextTick(renderCharts);
  });
}

function disposeCharts() {
  chartInstances.forEach(c => c && c.dispose());
  chartInstances.length = 0;
}

function handleResize() {
  chartInstances.forEach(c => c && c.resize());
}

onMounted(() => {
  loadData();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  disposeCharts();
});

// 模板 ref 声明（script setup 自动绑定同名变量）
const customerTrendRef = ref(null);
const customerSourceRef = ref(null);
const visitTrendRef = ref(null);
const quoteTrendRef = ref(null);
const depositTrendRef = ref(null);
const drawingRef = ref(null);
const siteStatusRef = ref(null);
const siteStageRef = ref(null);
const intentionRef = ref(null);
const signTrendRef = ref(null);
const paymentTrendRef = ref(null);
</script>

<style lang="scss" scoped>
.screen {
  min-height: calc(100vh - 84px);
  background: radial-gradient(1200px 600px at 20% -10%, #10305f 0%, #081630 55%, #060f22 100%);
  padding: 14px 16px 20px;
  margin: -8px;
  color: #cfe3ff;
}

.screen-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 6px 4px 14px;
  .header-left { display: flex; align-items: center; gap: 10px; }
  .logo-img {
    width: 32px; height: 32px; border-radius: 4px;
    box-shadow: 0 0 8px rgba(0, 224, 198, .4);
  }
  .sys-title { font-size: 20px; font-weight: 600; letter-spacing: 2px; color: #e8f2ff; }
  .header-right { display: flex; align-items: center; gap: 12px; }
  .range-text { font-size: 12px; color: #6f93c4; }
}

.kpi-row {
  display: grid; grid-template-columns: repeat(8, 1fr); gap: 12px; margin-bottom: 12px;
  .kpi-card {
    background: linear-gradient(160deg, rgba(20, 52, 105, .55), rgba(10, 25, 55, .75));
    border: 1px solid rgba(58, 142, 230, .25);
    border-radius: 8px; padding: 12px 8px; text-align: center;
    .kpi-value { font-size: 22px; font-weight: 700; color: #00e6c3; line-height: 1.3; }
    .kpi-label { font-size: 12px; color: #8fb2e0; margin-top: 2px; }
    &.warn .kpi-value { color: #f2b03d; }
  }
}

.grid-row {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; margin-bottom: 12px;
  &.site-row { grid-template-columns: 1fr 1fr 1.4fr; }
  &.payment-row { grid-template-columns: 1fr 1.2fr; }
  &.person-row { grid-template-columns: 1fr 1fr; }
}

:deep(.panel) {
  background: linear-gradient(170deg, rgba(16, 42, 88, .5), rgba(8, 20, 45, .85));
  border: 1px solid rgba(58, 142, 230, .22);
  border-radius: 8px; display: flex; flex-direction: column;
  .panel-title {
    padding: 9px 14px; font-size: 13px; font-weight: 600; color: #9fc6f5;
    border-bottom: 1px solid rgba(30, 75, 143, .4);
    background: linear-gradient(90deg, rgba(20, 60, 120, .5), transparent);
    &::before {
      content: ""; display: inline-block; width: 3px; height: 12px; margin-right: 8px;
      background: #00e0c6; border-radius: 2px; vertical-align: -1px;
    }
  }
  .panel-body { flex: 1; padding: 6px 8px 8px; display: flex; flex-direction: column; min-height: 0; }
}

.chart { width: 100%; }
.chart-lg { height: 230px; }
.chart-md { height: 240px; }
.chart-sm { height: 130px; }
.chart-sm2 { height: 140px; }
.chart-pay { height: 200px; }

.visit-list {
  flex: 1; overflow-y: auto; max-height: 105px; margin-top: 4px; padding-right: 4px;
  &::-webkit-scrollbar { width: 5px; }
  &::-webkit-scrollbar-thumb { background: #1e4b8f; border-radius: 3px; }
  .empty-tip { text-align: center; color: #6f93c4; padding: 14px 0; }
  .visit-item {
    display: flex; justify-content: space-between; align-items: center; gap: 8px;
    padding: 3px 8px; margin-bottom: 3px; border-radius: 4px;
    background: rgba(13, 35, 80, .45); border: 1px solid rgba(58, 142, 230, .15);
    .v-customer { font-size: 12px; color: #e8f2ff; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .v-meta { font-size: 11px; color: #6f93c4; white-space: nowrap; }
  }
}

.deposit-summary {
  display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; padding: 4px 4px 6px;
  .deposit-item {
    text-align: center; padding: 8px 4px; border-radius: 6px;
    background: rgba(13, 35, 80, .5); border: 1px solid rgba(58, 142, 230, .18);
    .d-num { display: block; font-size: 13px; color: #cfe3ff; }
    .d-amt { display: block; font-size: 15px; font-weight: 700; color: #f2b03d; margin: 2px 0; }
    .d-label { display: block; font-size: 12px; color: #6f93c4; }
  }
}

.site-kpi-strip {
  display: grid; grid-template-columns: repeat(6, 1fr); gap: 6px; margin-bottom: 8px;
  .sk-item {
    text-align: center; padding: 5px 2px; border-radius: 5px;
    background: rgba(13, 35, 80, .5); border: 1px solid rgba(58, 142, 230, .18);
    b { display: block; font-size: 16px; line-height: 1.3; }
    span { display: block; font-size: 11px; color: #6f93c4; }
    .c-run { color: #00e6c3; }
    .c-prep { color: #f2b03d; }
    .c-done { color: #5ad8a6; }
    .c-danger { color: #f7725b; }
    .c-soon { color: #4fc3f7; }
  }
}

.site-list {
  flex: 1; overflow-y: auto; max-height: 330px; padding-right: 4px;
  &::-webkit-scrollbar { width: 5px; }
  &::-webkit-scrollbar-thumb { background: #1e4b8f; border-radius: 3px; }
  .empty-tip { text-align: center; color: #6f93c4; padding: 30px 0; }
  .site-item {
    padding: 8px 10px; margin-bottom: 8px; border-radius: 6px;
    background: rgba(13, 35, 80, .5); border: 1px solid rgba(58, 142, 230, .18);
    &.is-overdue { border-color: rgba(247, 114, 91, .55); box-shadow: 0 0 6px rgba(247, 114, 91, .15) inset; }
    .site-line1 { display: flex; justify-content: space-between; align-items: center; }
    .site-customer { font-size: 13px; font-weight: 600; color: #e8f2ff; }
    .site-right { display: flex; align-items: center; gap: 6px; }
    .site-area { font-size: 11px; color: #6f93c4; }
    .site-status { font-size: 11px; padding: 1px 8px; border-radius: 10px; }
    .st-0 { color: #f2b03d; background: rgba(242, 176, 61, .12); }
    .st-1 { color: #00e0c6; background: rgba(0, 224, 198, .12); }
    .st-2 { color: #5ad8a6; background: rgba(90, 216, 166, .12); }
    .site-line2 {
      display: flex; justify-content: space-between; align-items: center; margin: 4px 0 3px;
      .site-stage { font-size: 12px; color: #8fb2e0; }
    }
    .deviation { font-size: 11px; padding: 1px 7px; border-radius: 9px; }
    .dev-ahead { color: #5ad8a6; background: rgba(90, 216, 166, .14); }
    .dev-behind { color: #f7725b; background: rgba(247, 114, 91, .14); }
    .dev-ok { color: #8fb2e0; background: rgba(58, 142, 230, .14); }
    .dual-bar { display: flex; flex-direction: column; gap: 3px; margin: 2px 0; }
    .bar-row { display: flex; align-items: center; gap: 6px; }
    .bar-label { width: 24px; font-size: 10px; color: #6f93c4; text-align: right; flex-shrink: 0; }
    .bar-track { flex: 1; height: 7px; border-radius: 4px; background: rgba(30, 75, 143, .35); overflow: hidden; }
    .bar-fill { height: 100%; border-radius: 4px; transition: width .4s ease; }
    .f-stage { background: linear-gradient(90deg, #0e8f81, #00e0c6); }
    .f-time { background: linear-gradient(90deg, #1450a3, #4fc3f7); }
    .bar-num { width: 34px; font-size: 10px; color: #8fb2e0; text-align: right; flex-shrink: 0; }
    .site-line3 {
      display: flex; justify-content: space-between; font-size: 11px; color: #6f93c4; margin-top: 4px;
      .days-info { font-weight: 600; }
      .d-danger { color: #f7725b; }
      .d-soon { color: #4fc3f7; }
      .d-info { color: #8fb2e0; }
    }
    .site-line4 {
      display: flex; gap: 12px; font-size: 11px; color: #55789f; margin-top: 2px;
    }
  }
}

.intention-tag {
  display: inline-block; min-width: 22px; text-align: center; font-size: 11px;
  padding: 1px 4px; border-radius: 3px;
}
.il-A { color: #e8648e; background: rgba(232, 100, 142, .15); }
.il-B { color: #f2b03d; background: rgba(242, 176, 61, .15); }
.il-C { color: #3a8ee6; background: rgba(58, 142, 230, .15); }
.il-D { color: #5ad8a6; background: rgba(90, 216, 166, .15); }

.has-deposit { color: #f2b03d; font-weight: 600; }

:deep(.el-table) {
  background: transparent; --el-table-border-color: rgba(30, 75, 143, .3);
  --el-table-tr-bg-color: transparent; --el-table-header-bg-color: #0d2350;
  &::before { display: none; }
  .el-table__row:hover > td { background: rgba(20, 60, 120, .3) !important; }
  .el-scrollbar__view { background: transparent; }
}
.amt-text { color: #f2b03d; font-weight: 600; }

@media (max-width: 1500px) {
  .kpi-row { grid-template-columns: repeat(4, 1fr); }
  .grid-row, .grid-row.site-row, .grid-row.payment-row, .grid-row.person-row { grid-template-columns: repeat(2, 1fr); }
  .site-kpi-strip { grid-template-columns: repeat(3, 1fr); }
}
</style>
