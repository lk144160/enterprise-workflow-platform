<template>
  <div class="app-container">
    <!-- 筛选区 -->
    <el-form :inline="true">
      <el-form-item label="统计时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :shortcuts="shortcuts"
          style="width: 260px"
          @change="getReportData"
        />
      </el-form-item>
      <el-form-item label="业务员">
        <el-input v-model="ownerName" placeholder="全部业务员" clearable style="width: 140px" @keyup.enter="getReportData" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="getReportData">统 计</el-button>
        <el-button type="warning" icon="Download" v-hasPermi="['crm:report:export']" @click="handleExport">一键导出 Excel</el-button>
      </el-form-item>
    </el-form>

    <!-- 概览卡片 -->
    <el-row :gutter="12" class="stat-row" v-loading="loading">
      <el-col :span="4" v-for="card in cards" :key="card.label">
        <div class="stat-card">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 明细 Tabs -->
    <el-tabs v-model="activeTab" style="margin-top: 12px">
      <el-tab-pane :label="'客户明细（' + customers.length + '）'" name="customer">
        <el-table :data="customers" size="small" v-loading="loading">
          <el-table-column label="客户姓名" align="center" prop="customerName" width="100" />
          <el-table-column label="手机号" align="center" prop="phone" width="120" />
          <el-table-column label="楼盘" align="center" prop="estate" :show-overflow-tooltip="true" />
          <el-table-column label="户型" align="center" prop="houseType" width="90" />
          <el-table-column label="来源" align="center" prop="source" width="90">
            <template #default="scope"><dict-tag :options="crm_customer_source" :value="scope.row.source" /></template>
          </el-table-column>
          <el-table-column label="意向等级" align="center" prop="intentionLevel" width="90">
            <template #default="scope"><dict-tag :options="crm_intention_level" :value="scope.row.intentionLevel" /></template>
          </el-table-column>
          <el-table-column label="状态" align="center" prop="status" width="90">
            <template #default="scope"><dict-tag :options="crm_customer_status" :value="scope.row.status" /></template>
          </el-table-column>
          <el-table-column label="业务员" align="center" prop="ownerName" width="90" />
          <el-table-column label="创建时间" align="center" prop="createTime" width="160">
            <template #default="scope">{{ parseTime(scope.row.createTime) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane :label="'跟进明细（' + follows.length + '）'" name="follow">
        <el-table :data="follows" size="small" v-loading="loading">
          <el-table-column label="客户姓名" align="center" prop="customerName" width="100" />
          <el-table-column label="手机号" align="center" prop="phone" width="120" />
          <el-table-column label="楼盘" align="center" prop="customerEstate" :show-overflow-tooltip="true" />
          <el-table-column label="跟进方式" align="center" prop="followType" width="90">
            <template #default="scope">
              <el-tag v-if="scope.row.followType === '3'" size="small" type="warning">到访</el-tag>
              <dict-tag v-else :options="crm_follow_type" :value="scope.row.followType" />
            </template>
          </el-table-column>
          <el-table-column label="跟进内容" align="center" prop="content" :show-overflow-tooltip="true" />
          <el-table-column label="跟进人" align="center" prop="createByName" width="90" />
          <el-table-column label="跟进时间" align="center" prop="followTime" width="160">
            <template #default="scope">{{ parseTime(scope.row.followTime) }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane :label="'签约明细（' + contracts.length + '）'" name="contract">
        <el-table :data="contracts" size="small" v-loading="loading">
          <el-table-column label="合同号" align="center" prop="contractNo" width="140" />
          <el-table-column label="客户姓名" align="center" prop="customerName" width="100" />
          <el-table-column label="手机号" align="center" prop="customerPhone" width="120" />
          <el-table-column label="楼盘" align="center" prop="estate" :show-overflow-tooltip="true" />
          <el-table-column label="合同金额" align="center" prop="contractAmount" width="110">
            <template #default="scope">{{ scope.row.contractAmount ? '￥' + scope.row.contractAmount : '-' }}</template>
          </el-table-column>
          <el-table-column label="已收金额" align="center" prop="paidAmount" width="110">
            <template #default="scope">{{ scope.row.paidAmount ? '￥' + scope.row.paidAmount : '-' }}</template>
          </el-table-column>
          <el-table-column label="签约日期" align="center" prop="signDate" width="110">
            <template #default="scope">{{ parseTime(scope.row.signDate, '{y}-{m}-{d}') || '-' }}</template>
          </el-table-column>
          <el-table-column label="负责人" align="center" prop="ownerUserName" width="90" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="CrmReport">
import { getReport } from "@/api/biz/report";

const { proxy } = getCurrentInstance();
const { crm_customer_source, crm_intention_level, crm_customer_status, crm_follow_type } =
  proxy.useDict("crm_customer_source", "crm_intention_level", "crm_customer_status", "crm_follow_type");

const loading = ref(false);
const dateRange = ref([]);
const ownerName = ref(null);
const activeTab = ref("customer");
const summary = ref({});
const customers = ref([]);
const follows = ref([]);
const contracts = ref([]);

/** 快捷时间选项 */
const shortcuts = [
  { text: "今日", value: () => { const d = new Date(); return [d, d]; } },
  { text: "本周", value: () => { const d = new Date(); const day = (d.getDay() + 6) % 7; const start = new Date(d); start.setDate(d.getDate() - day); return [start, d]; } },
  { text: "本月", value: () => { const d = new Date(); return [new Date(d.getFullYear(), d.getMonth(), 1), d]; } },
  { text: "上月", value: () => { const d = new Date(); return [new Date(d.getFullYear(), d.getMonth() - 1, 1), new Date(d.getFullYear(), d.getMonth(), 0)]; } },
  { text: "近30天", value: () => { const d = new Date(); const start = new Date(d); start.setDate(d.getDate() - 29); return [start, d]; } },
];

const cards = computed(() => [
  { label: "新增线索", value: summary.value.newCustomers ?? "-" },
  { label: "跟进次数", value: summary.value.followCount ?? "-" },
  { label: "到访次数", value: summary.value.visitCount ?? "-" },
  { label: "报价单数", value: summary.value.quoteCount ?? "-" },
  { label: "签约单数", value: summary.value.signCount ?? "-" },
  { label: "签约金额(元)", value: summary.value.signAmount ?? "-" },
]);

function getReportData() {
  if (!dateRange.value || dateRange.value.length !== 2) {
    proxy.$modal.msgWarning("请选择统计时间范围");
    return;
  }
  loading.value = true;
  getReport({ beginTime: dateRange.value[0], endTime: dateRange.value[1], ownerName: ownerName.value }).then(res => {
    const data = res.data || {};
    summary.value = data.summary || {};
    customers.value = data.customers || [];
    follows.value = data.follows || [];
    contracts.value = data.contracts || [];
    loading.value = false;
  }).catch(() => { loading.value = false; });
}

function handleExport() {
  if (!dateRange.value || dateRange.value.length !== 2) {
    proxy.$modal.msgWarning("请选择统计时间范围");
    return;
  }
  proxy.download("crm/report/export", {
    beginTime: dateRange.value[0],
    endTime: dateRange.value[1],
    ownerName: ownerName.value
  }, `客情统计_${dateRange.value[0]}_${dateRange.value[1]}.xlsx`);
}

// 初始化：默认本月
dateRange.value = [proxy.parseTime(new Date(), '{y}-{m}-01'), proxy.parseTime(new Date(), '{y}-{m}-{d}')];
getReportData();
</script>

<style scoped>
.stat-row {
  margin-bottom: 4px;
}
.stat-card {
  padding: 16px 8px;
  text-align: center;
  background: #f5f7fa;
  border-radius: 6px;
}
.stat-value {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.stat-label {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}
</style>
