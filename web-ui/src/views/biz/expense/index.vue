<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="费用类型" prop="expenseType">
        <el-select v-model="queryParams.expenseType" placeholder="费用类型" clearable style="width: 130px">
          <el-option v-for="dict in biz_expense_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 130px">
          <el-option v-for="dict in biz_expense_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:expense:add']">申请报销</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="expenseList">
      <el-table-column label="申请人" align="center" prop="applyUserName" width="90" />
      <el-table-column label="部门" align="center" prop="deptName" width="90" />
      <el-table-column label="费用类型" align="center" prop="expenseType" width="100">
        <template #default="scope">
          <dict-tag :options="biz_expense_type" :value="scope.row.expenseType" />
        </template>
      </el-table-column>
      <el-table-column label="金额" align="center" prop="amount" width="110" />
      <el-table-column label="发票张数" align="center" prop="invoiceCount" width="90" />
      <el-table-column label="费用日期" align="center" prop="expenseDate" width="110">
        <template #default="scope">{{ parseTime(scope.row.expenseDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <dict-tag :options="biz_expense_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['biz:expense:query']">详情</el-button>
          <el-button v-if="['0','3'].includes(scope.row.status)" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['biz:expense:edit']">修改</el-button>
          <el-button v-if="['0','3'].includes(scope.row.status)" link type="success" icon="Promotion" @click="handleSubmit(scope.row)" v-hasPermi="['biz:expense:submit']">提交</el-button>
          <el-button v-if="scope.row.status === '2'" link type="warning" icon="Money" @click="handlePay(scope.row)" v-hasPermi="['biz:expense:pay']">打款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 申请/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="expenseRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="费用类型" prop="expenseType">
              <el-select v-model="form.expenseType" placeholder="请选择费用类型">
                <el-option v-for="dict in biz_expense_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报销金额" prop="amount">
              <el-input-number v-model="form.amount" :precision="2" :min="0.01" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="费用日期" prop="expenseDate">
              <el-date-picker v-model="form.expenseDate" type="date" value-format="YYYY-MM-DD" placeholder="选择费用发生日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发票张数" prop="invoiceCount">
              <el-input-number v-model="form.invoiceCount" :min="0" :precision="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="费用说明" prop="expenseDesc">
          <el-input v-model="form.expenseDesc" type="textarea" :rows="3" placeholder="请输入费用说明" />
        </el-form-item>
        <el-form-item label="发票附件">
          <el-upload
            :headers="upload.headers"
            :action="upload.url"
            accept=".jpg,.jpeg,.png,.pdf"
            :on-success="handleUploadSuccess"
            :show-file-list="false"
            multiple
          >
            <el-button icon="Upload" :disabled="!form.expenseId">上传发票（保存单据后可用）</el-button>
            <template #tip>
              <div class="el-upload__tip">发票张数需与上传附件数一致（{{ uploadedCount }}/{{ form.invoiceCount || 0 }}）</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情抽屉 -->
    <el-drawer v-model="detailOpen" :title="'报销详情 - ' + detail.expenseNo" size="560px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="报销编号">{{ detail.expenseNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detail.applyUserName }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ detail.deptName }}</el-descriptions-item>
        <el-descriptions-item label="费用类型">{{ detail.expenseType }}</el-descriptions-item>
        <el-descriptions-item label="金额">￥{{ detail.amount }}</el-descriptions-item>
        <el-descriptions-item label="发票张数">{{ detail.invoiceCount }}</el-descriptions-item>
        <el-descriptions-item label="费用日期">{{ parseTime(detail.expenseDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="费用说明" :span="2">{{ detail.expenseDesc }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ parseTime(detail.submitTime) }}</el-descriptions-item>
        <el-descriptions-item label="打款时间">{{ parseTime(detail.payTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup name="Expense">
import { listExpense, getExpense, addExpense, updateExpense, submitExpense, payExpense } from "@/api/biz/expense";
import { addAttachment } from "@/api/biz/attachment";
import { getToken } from "@/utils/auth";

const baseUrl = import.meta.env.VITE_APP_BASE_API;

const { proxy } = getCurrentInstance();
const { biz_expense_type, biz_expense_status } = proxy.useDict("biz_expense_type", "biz_expense_status");

const expenseList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const detailOpen = ref(false);
const title = ref("");
const detail = ref({});
const uploadedCount = ref(0);

const upload = {
  headers: { Authorization: "Bearer " + getToken() },
  url: baseUrl + "/common/upload"
};

const queryParams = ref({ pageNum: 1, pageSize: 10, expenseNo: null, expenseType: null, status: null });

const rules = {
  expenseType: [{ required: true, message: "请选择费用类型", trigger: "change" }],
  amount: [{ required: true, message: "请输入报销金额", trigger: "blur" }],
  expenseDate: [{ required: true, message: "请选择费用发生日期", trigger: "change" }],
  invoiceCount: [{ required: true, message: "请输入发票张数", trigger: "blur" }]
};

const form = ref({});

const computedUploaded = computed(() => uploadedCount.value);

function getList() {
  loading.value = true;
  listExpense(queryParams.value).then(res => {
    expenseList.value = res.rows;
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

function reset() {
  form.value = { expenseId: null, expenseType: null, amount: null, expenseDate: null, invoiceCount: 0, expenseDesc: null };
  uploadedCount.value = 0;
  proxy.resetForm("expenseRef");
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "申请报销";
}

function handleUpdate(row) {
  reset();
  getExpense(row.expenseId).then(res => {
    form.value = res.data;
    uploadedCount.value = res.data.attachments ? res.data.attachments.length : 0;
    open.value = true;
    title.value = "修改报销单";
  });
}

function handleDetail(row) {
  getExpense(row.expenseId).then(res => {
    detail.value = res.data;
    detailOpen.value = true;
  });
}

function handleUploadSuccess(res) {
  if (res.code === 200 && form.value.expenseId) {
    addAttachment({ bizType: "expense", bizId: form.value.expenseId, fileName: res.fileName, fileUrl: res.url }).then(() => {
      uploadedCount.value += 1;
      proxy.$modal.msgSuccess("发票上传成功");
    });
  }
}

function submitForm() {
  proxy.$refs["expenseRef"].validate(valid => {
    if (valid) {
      if (form.value.expenseId != null) {
        updateExpense(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addExpense(form.value).then(() => {
          proxy.$modal.msgSuccess("报销单已保存为草稿，可继续上传发票");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleSubmit(row) {
  proxy.$modal.confirm('确认提交"' + row.applyUserName + '"的报销单（￥' + row.amount + '）进入审批？发票张数需与附件数一致。').then(() => {
    return submitExpense(row.expenseId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("提交成功");
  }).catch(() => {});
}

function handlePay(row) {
  proxy.$modal.confirm('确认为"' + row.applyUserName + '"的报销单（￥' + row.amount + '）登记打款？').then(() => {
    return payExpense(row.expenseId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("打款登记成功");
  }).catch(() => {});
}

function cancel() {
  open.value = false;
  reset();
}

getList();
</script>
