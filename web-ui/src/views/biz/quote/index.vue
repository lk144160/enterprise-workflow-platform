<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="项目名称" prop="projectName">
        <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户" prop="customerName">
        <el-input v-model="queryParams.customerName" placeholder="请输入客户姓名" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="报价状态" clearable style="width: 130px">
          <el-option v-for="dict in biz_audit_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:quote:add']">新增</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="quoteList">
      <el-table-column label="项目名称" align="center" prop="projectName" :show-overflow-tooltip="true" />
      <el-table-column label="客户" align="center" prop="customerName" width="100" />
      <el-table-column label="手机号" align="center" prop="customerPhone" width="120" />
      <el-table-column label="报价金额" align="center" prop="totalAmount" width="110" />
      <el-table-column label="优惠金额" align="center" prop="discountAmount" width="100" />
      <el-table-column label="最终报价" align="center" prop="finalAmount" width="110" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <dict-tag :options="biz_audit_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" width="230" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['biz:quote:query']">详情</el-button>
          <el-button v-if="['0','3'].includes(scope.row.status)" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['biz:quote:edit']">修改</el-button>
          <el-button v-if="['0','3'].includes(scope.row.status)" link type="success" icon="Promotion" @click="handleSubmit(scope.row)" v-hasPermi="['biz:quote:submit']">提交</el-button>
          <el-button v-if="scope.row.status === '1'" link type="warning" icon="RefreshLeft" @click="handleCancel(scope.row)" v-hasPermi="['biz:quote:cancel']">撤销</el-button>
          <el-button v-if="['0','3'].includes(scope.row.status)" link type="danger" icon="Delete" @click="handleInvalidate(scope.row)" v-hasPermi="['biz:quote:remove']">作废</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改弹窗（公共组件） -->
    <quote-form-dialog ref="quoteFormRef" @success="getList" />

    <!-- 详情弹窗（公共组件） -->
    <quote-detail-dialog ref="quoteDetailRef" />
  </div>
</template>

<script setup name="Quote">
import { listQuote, submitQuote, cancelQuote, invalidateQuote } from "@/api/biz/quote";
import QuoteFormDialog from "./components/QuoteFormDialog.vue";
import QuoteDetailDialog from "./components/QuoteDetailDialog.vue";

const { proxy } = getCurrentInstance();
const { biz_audit_status } = proxy.useDict("biz_audit_status");

const quoteList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const quoteFormRef = ref(null);
const quoteDetailRef = ref(null);

const queryParams = ref({ pageNum: 1, pageSize: 10, projectName: null, customerName: null, status: null });

function getList() {
  loading.value = true;
  listQuote(queryParams.value).then(res => {
    quoteList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

function handleAdd() {
  quoteFormRef.value.open(null);
}

function handleUpdate(row) {
  quoteFormRef.value.open(row);
}

function handleDetail(row) {
  quoteDetailRef.value.open(row);
}

function handleSubmit(row) {
  proxy.$modal.confirm('确认提交报价单"' + row.projectName + '"进入审批？').then(() => {
    return submitQuote(row.quoteId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("提交成功");
  }).catch(() => {});
}

function handleCancel(row) {
  proxy.$modal.confirm('确认撤销报价单"' + row.projectName + '"的审批？').then(() => {
    return cancelQuote(row.quoteId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("已撤销");
  }).catch(() => {});
}

function handleInvalidate(row) {
  proxy.$modal.confirm('确认作废报价单"' + row.projectName + '"？').then(() => {
    return invalidateQuote(row.quoteId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("已作废");
  }).catch(() => {});
}

getList();
</script>
