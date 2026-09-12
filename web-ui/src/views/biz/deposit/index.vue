<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="客户" prop="customerName">
        <el-input v-model="queryParams.customerName" placeholder="请输入客户姓名" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="定金编号" prop="depositNo">
        <el-input v-model="queryParams.depositNo" placeholder="请输入定金编号" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="定金状态" clearable style="width: 130px">
          <el-option v-for="dict in biz_deposit_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="收款时间" style="width: 308px">
        <el-date-picker v-model="dateRange" value-format="YYYY-MM-DD" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:deposit:add']">登记定金</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="depositList">
      <el-table-column label="客户姓名" align="center" prop="customerName" width="100" />
      <el-table-column label="楼盘" align="center" prop="estate" :show-overflow-tooltip="true" />
      <el-table-column label="定金金额" align="center" prop="amount" width="110">
        <template #default="scope">
          <span style="color: #f56c6c; font-weight: bold">￥{{ Number(scope.row.amount).toLocaleString() }}</span>
        </template>
      </el-table-column>
      <el-table-column label="收款方式" align="center" prop="payType" width="90">
        <template #default="scope">
          <dict-tag :options="biz_pay_type" :value="scope.row.payType" />
        </template>
      </el-table-column>
      <el-table-column label="收款时间" align="center" prop="payTime" width="160" />
      <el-table-column label="关联报价" align="center" prop="quoteNo" width="140">
        <template #default="scope">{{ scope.row.quoteNo || '—' }}</template>
      </el-table-column>
      <el-table-column label="抵扣合同" align="center" prop="contractNo" width="140">
        <template #default="scope">{{ scope.row.contractNo || '—' }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="biz_deposit_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" v-if="scope.row.status === '0'" @click="handleUpdate(scope.row)" v-hasPermi="['biz:deposit:edit']">修改</el-button>
          <el-button link type="success" icon="Money" v-if="scope.row.status === '0'" @click="handleDeduct(scope.row)" v-hasPermi="['biz:deposit:deduct']">签约抵扣</el-button>
          <el-button link type="warning" icon="RefreshLeft" v-if="scope.row.status === '0'" @click="handleRefund(scope.row)" v-hasPermi="['biz:deposit:refund']">申请退还</el-button>
          <el-button link type="danger" icon="Delete" v-if="scope.row.status === '0'" @click="handleDelete(scope.row)" v-hasPermi="['biz:deposit:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 登记/修改定金弹窗 -->
    <el-dialog :title="form.depositId ? '修改定金' : '登记定金'" v-model="open" width="520px" append-to-body>
      <el-form ref="depositRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" filterable placeholder="选择客户" style="width: 100%">
            <el-option v-for="c in customers" :key="c.customerId" :label="(c.customerName || '') + (c.estate ? '（' + c.estate + '）' : '')" :value="c.customerId" />
          </el-select>
        </el-form-item>
        <el-form-item label="定金金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0.01" :precision="2" :controls="false" style="width: 100%" placeholder="元" />
        </el-form-item>
        <el-form-item label="收款方式" prop="payType">
          <el-select v-model="form.payType" placeholder="选择收款方式" style="width: 100%">
            <el-option v-for="dict in biz_pay_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="收款时间" prop="payTime">
          <el-date-picker v-model="form.payTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="关联报价" prop="quoteId">
          <el-select v-model="form.quoteId" clearable placeholder="可选，选择该客户已通过报价" style="width: 100%">
            <el-option v-for="q in quoteOptions" :key="q.quoteId" :label="q.quoteNo + ' ' + (q.projectName || '')" :value="q.quoteId" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 签约抵扣弹窗 -->
    <el-dialog title="签约抵扣" v-model="deductOpen" width="500px" append-to-body>
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px"
        :title="'定金 ' + deductRow.depositNo + '（￥' + Number(deductRow.amount || 0).toLocaleString() + '）将全额转入所选合同第1期（签约款）收款'" />
      <el-form label-width="90px">
        <el-form-item label="选择合同">
          <el-select v-model="deductContractId" filterable placeholder="选择该客户已生效合同" style="width: 100%">
            <el-option v-for="c in deductContracts" :key="c.contractId" :label="(c.customerName || '') + (c.estate ? '（' + c.estate + '）' : '')" :value="c.contractId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitDeduct">确认抵扣</el-button>
        <el-button @click="deductOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 申请退还弹窗 -->
    <el-dialog title="申请定金退还" v-model="refundOpen" width="500px" append-to-body>
      <el-alert type="warning" :closable="false" show-icon style="margin-bottom: 12px"
        :title="'定金 ' + refundRow.depositNo + '（￥' + Number(refundRow.amount || 0).toLocaleString() + '）退还申请将提交审批'" />
      <el-form label-width="90px">
        <el-form-item label="退还原因">
          <el-input v-model="refundReason" type="textarea" :rows="3" placeholder="请填写退还原因（必填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitRefund">提交审批</el-button>
        <el-button @click="refundOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Deposit">
import { listDeposit, addDeposit, updateDeposit, delDeposit, deductDeposit, refundDeposit } from "@/api/biz/deposit";
import { listCustomer } from "@/api/biz/customer";
import { listQuote } from "@/api/biz/quote";
import { listContract } from "@/api/biz/contract";

const { proxy } = getCurrentInstance();
const { biz_deposit_status, biz_pay_type } = proxy.useDict("biz_deposit_status", "biz_pay_type");

const depositList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const dateRange = ref([]);
const open = ref(false);
const customers = ref([]);
const quoteOptions = ref([]);

const queryParams = ref({ pageNum: 1, pageSize: 10, customerName: null, depositNo: null, status: null });

const form = ref({});
const rules = {
  customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
  amount: [{ required: true, message: "请输入定金金额", trigger: "blur" }],
  payType: [{ required: true, message: "请选择收款方式", trigger: "change" }],
  payTime: [{ required: true, message: "请选择收款时间", trigger: "change" }]
};

const deductOpen = ref(false);
const deductRow = ref({});
const deductContractId = ref(null);
const deductContracts = ref([]);

const refundOpen = ref(false);
const refundRow = ref({});
const refundReason = ref("");

function getList() {
  loading.value = true;
  proxy.addDateRange(queryParams.value, dateRange.value, "PayTime");
  listDeposit(queryParams.value).then(res => {
    depositList.value = res.rows;
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

/** 登记弹窗：加载客户与该客户可选报价 */
function handleAdd() {
  form.value = { payTime: proxy.parseTime(new Date(), '{y}-{m}-{d} {h}:{i}:{s}') };
  loadCustomers();
  loadQuotes(null);
  open.value = true;
}

function handleUpdate(row) {
  form.value = { ...row };
  loadCustomers();
  loadQuotes(row.customerId);
  open.value = true;
}

function loadCustomers() {
  listCustomer({ pageNum: 1, pageSize: 1000 }).then(res => { customers.value = res.rows; });
}

function loadQuotes(customerId) {
  const params = { pageNum: 1, pageSize: 200, status: "2" };
  if (customerId) params.customerId = customerId;
  listQuote(params).then(res => { quoteOptions.value = res.rows; });
}

function submitForm() {
  proxy.$refs["depositRef"].validate(valid => {
    if (!valid) return;
    if (form.value.depositId) {
      updateDeposit(form.value).then(() => {
        proxy.$modal.msgSuccess("修改成功");
        open.value = false;
        getList();
      });
    } else {
      addDeposit(form.value).then(() => {
        proxy.$modal.msgSuccess("登记成功");
        open.value = false;
        getList();
      });
    }
  });
}

/** 签约抵扣：加载该客户已生效合同 */
function handleDeduct(row) {
  deductRow.value = row;
  deductContractId.value = null;
  listContract({ pageNum: 1, pageSize: 200, customerId: row.customerId, status: "2" }).then(res => {
    deductContracts.value = res.rows;
    deductOpen.value = true;
  });
}

function submitDeduct() {
  if (!deductContractId.value) {
    proxy.$modal.msgWarning("请选择要抵扣的合同");
    return;
  }
  deductDeposit(deductRow.value.depositId, deductContractId.value).then(() => {
    proxy.$modal.msgSuccess("抵扣成功，已转入合同第1期收款");
    deductOpen.value = false;
    getList();
  });
}

function handleRefund(row) {
  refundRow.value = row;
  refundReason.value = "";
  refundOpen.value = true;
}

function submitRefund() {
  if (!refundReason.value) {
    proxy.$modal.msgWarning("请填写退还原因");
    return;
  }
  refundDeposit(refundRow.value.depositId, refundReason.value).then(() => {
    proxy.$modal.msgSuccess("已提交退还审批");
    refundOpen.value = false;
    getList();
  });
}

function handleDelete(row) {
  proxy.$modal.confirm('确认删除客户"' + row.customerName + '"的定金记录？').then(() => {
    return delDeposit(row.depositId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

getList();
</script>
