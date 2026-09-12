<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="审批类型" prop="bizType">
        <el-select v-model="queryParams.bizType" placeholder="全部类型" clearable style="width: 150px">
          <el-option v-for="dict in flow_biz_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 120px">
          <el-option label="审批中" value="1" />
          <el-option label="已通过" value="2" />
          <el-option label="已驳回" value="3" />
          <el-option label="已撤销" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="发起时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="-"
          start-placeholder="开始"
          end-placeholder="结束"
          style="width: 220px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="mineList">
      <el-table-column label="审批类型" align="center" prop="bizType" width="110">
        <template #default="scope">
          <dict-tag :options="flow_biz_type" :value="scope.row.bizType" />
        </template>
      </el-table-column>
      <el-table-column label="申请事项" align="center" prop="bizTitle" :show-overflow-tooltip="true" />
      <el-table-column label="金额" align="center" prop="bizAmount" width="110" />
      <el-table-column label="当前节点" align="center" prop="currentNodeName" width="110" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发起时间" align="center" prop="startTime" width="160">
        <template #default="scope">{{ parseTime(scope.row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="160">
        <template #default="scope">{{ parseTime(scope.row.endTime) || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row.instanceId)">详情</el-button>
          <el-button v-if="scope.row.status === '1'" link type="danger" icon="RefreshLeft" @click="handleCancel(scope.row)">撤销</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 审批详情抽屉 -->
    <el-drawer v-model="detailOpen" title="审批详情" size="800px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="审批类型"><dict-tag :options="flow_biz_type" :value="detail.bizType" /></el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(detail.status) }}</el-descriptions-item>
        <el-descriptions-item label="申请事项" :span="2">{{ detail.bizTitle }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ detail.customerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="金额">{{ detail.bizAmount ? '￥' + detail.bizAmount : '-' }}</el-descriptions-item>
        <el-descriptions-item label="发起人">{{ detail.startUserName }}</el-descriptions-item>
        <el-descriptions-item label="发起时间">{{ parseTime(detail.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ parseTime(detail.endTime) || '-' }}</el-descriptions-item>
      </el-descriptions>
      <!-- 单据详情 + 图片附件 -->
      <FlowBizDetail :detail="detail" />
      <template v-if="detail.customerId">
        <el-divider content-position="left">客户基础信息</el-divider>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="客户姓名">{{ customerInfo.customerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ customerInfo.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源"><dict-tag :options="crm_customer_source" :value="customerInfo.source" /></el-descriptions-item>
          <el-descriptions-item label="意向等级"><dict-tag :options="crm_intention_level" :value="customerInfo.intentionLevel" /></el-descriptions-item>
          <el-descriptions-item label="楼盘">{{ customerInfo.estate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="户型">{{ customerInfo.houseType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="面积">{{ customerInfo.area ? customerInfo.area + ' ㎡' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="预算">{{ customerInfo.budget || '-' }}</el-descriptions-item>
          <el-descriptions-item label="客户状态"><dict-tag :options="crm_customer_status" :value="customerInfo.status" /></el-descriptions-item>
          <el-descriptions-item label="装修需求">{{ customerInfo.demand || '-' }}</el-descriptions-item>
        </el-descriptions>
        <el-divider content-position="left">报价记录</el-divider>
        <el-table :data="quoteList" size="small" :row-class-name="quoteRowClass">
          <el-table-column label="项目名称" align="center" prop="projectName" :show-overflow-tooltip="true">
            <template #default="scope">
              {{ scope.row.projectName }}
              <el-tag v-if="isCurrentQuote(scope.row)" size="small" type="warning" style="margin-left: 4px">当前审批</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="报价金额" align="center" prop="totalAmount" width="100" />
          <el-table-column label="优惠" align="center" prop="discountAmount" width="80" />
          <el-table-column label="最终报价" align="center" prop="finalAmount" width="100" />
          <el-table-column label="状态" align="center" prop="status" width="85">
            <template #default="scope">
              <dict-tag :options="biz_audit_status" :value="scope.row.status" />
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime" width="150">
            <template #default="scope">{{ parseTime(scope.row.createTime) }}</template>
          </el-table-column>
        </el-table>
      </template>
      <el-divider content-position="left">审批流程</el-divider>
      <el-timeline>
        <el-timeline-item
          v-for="task in detail.tasks || []"
          :key="task.taskId"
          :timestamp="parseTime(task.handleTime) || '待处理'"
          :type="timelineType(task.status)"
          :hollow="task.status === '0'"
          placement="top"
        >
          <div style="font-weight: bold">{{ task.nodeName }}</div>
          <div>处理人：{{ task.approverName }}
            <el-tag v-if="task.status !== '0'" size="small" style="margin-left: 8px" :type="timelineType(task.status)">{{ taskText(task.status) }}</el-tag>
          </div>
          <div v-if="task.opinion" style="color: #909399; font-size: 12px; margin-top: 4px">意见：{{ task.opinion }}</div>
        </el-timeline-item>
      </el-timeline>
    </el-drawer>
  </div>
</template>

<script setup name="FlowMine">
import { listMine, getInstance, cancelInstance } from "@/api/biz/flow";
import { getCustomer } from "@/api/biz/customer";
import { listQuote } from "@/api/biz/quote";
import FlowBizDetail from "@/views/flow/components/FlowBizDetail.vue";

const { proxy } = getCurrentInstance();
const { flow_biz_type, crm_customer_source, crm_intention_level, crm_customer_status, biz_audit_status } =
  proxy.useDict("flow_biz_type", "crm_customer_source", "crm_intention_level", "crm_customer_status", "biz_audit_status");

const mineList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const detailOpen = ref(false);
const detail = ref({});
const customerInfo = ref({});
const quoteList = ref([]);
const dateRange = ref([]);

const queryParams = ref({ pageNum: 1, pageSize: 10, bizType: null, status: null });

function statusText(status) {
  return { "1": "审批中", "2": "已通过", "3": "已驳回", "4": "已撤销" }[status] || status;
}

function statusType(status) {
  return { "1": "primary", "2": "success", "3": "danger", "4": "info" }[status] || "info";
}

function taskText(status) {
  return { "0": "待处理", "1": "已同意", "2": "已驳回", "3": "已转交", "4": "已撤销" }[status] || status;
}

function timelineType(status) {
  return { "0": "primary", "1": "success", "2": "danger", "3": "warning", "4": "info" }[status] || "primary";
}

function getList() {
  loading.value = true;
  const params = { ...queryParams.value };
  if (dateRange.value && dateRange.value.length === 2) {
    params.params = { beginTime: dateRange.value[0], endTime: dateRange.value[1] };
  }
  listMine(params).then(res => {
    mineList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

function handleDetail(instanceId) {
  getInstance(instanceId).then(res => {
    detail.value = res.data;
    detailOpen.value = true;
    loadCustomerContext(res.data);
  });
}

/** 加载审批关联客户的基础信息与报价记录 */
function loadCustomerContext(instance) {
  customerInfo.value = {};
  quoteList.value = [];
  if (!instance.customerId) return;
  getCustomer(instance.customerId).then(res => {
    customerInfo.value = res.data || {};
  });
  listQuote({ pageNum: 1, pageSize: 100, customerId: instance.customerId }).then(res => {
    quoteList.value = res.rows || [];
  });
}

/** 是否为本次审批的报价单 */
function isCurrentQuote(row) {
  return detail.value.bizType === "quote" && row.quoteId === detail.value.bizId;
}

function quoteRowClass({ row }) {
  return isCurrentQuote(row) ? "current-quote-row" : "";
}

function handleCancel(row) {
  proxy.$modal.confirm('确认撤销申请"' + row.bizTitle + '"？').then(() => {
    return cancelInstance(row.instanceId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("已撤销");
  }).catch(() => {});
}

getList();
</script>

<style scoped>
:deep(.el-table .current-quote-row) {
  --el-table-tr-bg-color: #fdf6ec;
}
</style>
