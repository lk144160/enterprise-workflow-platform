<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="客户" prop="customerName">
        <el-input v-model="queryParams.customerName" placeholder="请输入客户姓名" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="合同状态" clearable style="width: 130px">
          <el-option v-for="dict in biz_contract_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:contract:add']">新增</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="contractList">
      <el-table-column label="客户" align="center" prop="customerName" width="100" />
      <el-table-column label="负责人" align="center" prop="ownerUserName" width="90">
        <template #default="scope">{{ scope.row.ownerUserName || '-' }}</template>
      </el-table-column>
      <el-table-column label="楼盘" align="center" prop="estate" width="140" :show-overflow-tooltip="true">
        <template #default="scope">{{ scope.row.estate || '-' }}</template>
      </el-table-column>
      <el-table-column label="合同金额" align="center" prop="contractAmount" width="110" />
      <el-table-column label="已收金额" align="center" prop="paidAmount" width="110" />
      <el-table-column label="签订日期" align="center" prop="signDate" width="110">
        <template #default="scope">
          <span>{{ parseTime(scope.row.signDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="工期" align="center" width="200">
        <template #default="scope">
          <span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }} ~ {{ parseTime(scope.row.endDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <dict-tag :options="biz_contract_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="270" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['biz:contract:query']">详情</el-button>
          <el-button v-if="scope.row.status === '0'" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['biz:contract:edit']">修改</el-button>
          <el-button v-if="scope.row.status === '0'" link type="success" icon="Promotion" @click="handleSubmit(scope.row)" v-hasPermi="['biz:contract:submit']">提交</el-button>
          <el-button v-if="scope.row.status === '2'" link type="warning" icon="CircleCheck" @click="handleFinish(scope.row)" v-hasPermi="['biz:contract:finish']">完工</el-button>
          <el-button v-if="scope.row.status === '3'" link type="info" icon="FolderChecked" @click="handleArchive(scope.row)" v-hasPermi="['biz:contract:archive']">归档</el-button>
          <el-button v-if="scope.row.status === '2'" link type="danger" icon="CircleClose" @click="handleTerminate(scope.row)" v-hasPermi="['biz:contract:terminate']">终止</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="contractRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="客户" prop="customerId">
          <el-select v-model="form.customerId" placeholder="请选择客户（线索）" filterable style="width: 100%" @change="onCustomerChange">
            <el-option v-for="c in customerOptions" :key="c.customerId" :label="c.customerName + '（' + c.phone + '）'" :value="c.customerId" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联报价" prop="quoteId">
          <el-select v-model="form.quoteId" placeholder="请选择该客户的已审批报价（可不选）" clearable style="width: 100%">
            <el-option v-for="q in quoteOptions" :key="q.quoteId" :label="q.projectName + '（￥' + q.finalAmount + '）'" :value="q.quoteId" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="ownerUserId">
          <el-select v-model="form.ownerUserId" placeholder="可选，绑定后可在后期修改中调整" clearable filterable style="width: 100%">
            <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="合同金额" prop="contractAmount">
              <el-input-number v-model="form.contractAmount" :precision="2" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="签订日期" prop="signDate">
              <el-date-picker v-model="form.signDate" type="date" value-format="YYYY-MM-DD" placeholder="选择签订日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="工期开始" prop="startDate">
              <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择开始日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工期结束" prop="endDate">
              <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" placeholder="选择结束日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="差异说明" prop="amountDiffNote">
          <el-input v-model="form.amountDiffNote" type="textarea" :rows="2" placeholder="合同金额与报价不一致时的说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情抽屉（客户信息 + 合同信息 + 四期收款计划） -->
    <el-drawer v-model="detailOpen" :title="'合同详情 - ' + detail.contractNo" size="720px">
      <el-divider content-position="left">客户信息</el-divider>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="客户姓名">{{ detail.customerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.customerPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="楼盘">{{ detail.estate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="户型">{{ detail.houseType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="面积">{{ detail.area ? detail.area + ' ㎡' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="预算">{{ detail.budget || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-divider content-position="left">合同信息</el-divider>
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="合同编号">{{ detail.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="biz_contract_status" :value="detail.status" />
        </el-descriptions-item>
        <el-descriptions-item label="合同金额">￥{{ detail.contractAmount }}</el-descriptions-item>
        <el-descriptions-item label="累计已收">￥{{ detail.paidAmount }}</el-descriptions-item>
        <el-descriptions-item label="关联报价">{{ detail.quoteNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ detail.ownerUserName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="签订日期">{{ parseTime(detail.signDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="工期" :span="2">{{ parseTime(detail.startDate, '{y}-{m}-{d}') }} ~ {{ parseTime(detail.endDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="差异说明" :span="2">{{ detail.amountDiffNote || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-divider content-position="left">合同附件</el-divider>
      <div class="att-toolbar">
        <el-upload v-hasPermi="['biz:contract:edit']" :headers="upload.headers" :action="upload.url"
          accept=".pdf,.doc,.docx,.xls,.xlsx,.jpg,.jpeg,.png,.gif,.bmp,.webp,.dwg,.dxf,.zip,.rar"
          :show-file-list="false" multiple :before-upload="beforeUpload" :on-success="handleUploadSuccess"
          :on-error="() => proxy.$modal.msgError('上传失败，请重试')">
          <el-button type="primary" plain icon="Upload">上传附件（可多选）</el-button>
        </el-upload>
        <span class="att-tip">支持 PDF/Word/Excel/图片/CAD 图纸，单文件不超过 50MB</span>
      </div>
      <div v-if="!attachments.length" class="att-empty">暂无附件，可上传合同扫描件、CAD 图纸等文件</div>
      <div v-else class="att-grid">
        <div v-for="att in attachments" :key="att.attachmentId" class="att-card">
          <div class="att-preview">
            <el-image v-if="isImage(att.fileUrl)" class="att-img" :src="fullUrl(att.fileUrl)"
              :preview-src-list="imageUrls" :initial-index="imageUrls.indexOf(fullUrl(att.fileUrl))"
              :preview-teleported="true" fit="cover" hide-on-click-modal />
            <div v-else class="att-img att-doc">
              <el-icon :size="44"><component :is="docIcon(att.fileName)" /></el-icon>
            </div>
          </div>
          <div class="att-name" :title="att.fileName">{{ att.fileName }}</div>
          <div class="att-meta">
            <span class="att-size">{{ formatSize(att.fileSize) }}</span>
            <span class="att-ops">
              <el-button link type="primary" icon="Download" @click="downloadAttachment(att)">下载</el-button>
              <el-button v-hasPermi="['biz:contract:edit']" link type="danger" icon="Delete" @click="handleDeleteAttachment(att)">删除</el-button>
            </span>
          </div>
        </div>
      </div>
      <el-divider content-position="left">四期收款计划</el-divider>
      <el-table :data="detail.plans || []" size="small" border>
        <el-table-column label="期次" prop="periodNo" width="60" align="center" />
        <el-table-column label="期次名称" prop="periodName" width="100" />
        <el-table-column label="比例" prop="ratio" width="70" align="center">
          <template #default="scope">{{ scope.row.ratio }}%</template>
        </el-table-column>
        <el-table-column label="计划金额" prop="planAmount" width="100" align="center" />
        <el-table-column label="计划收款日" prop="planDate" width="110" align="center">
          <template #default="scope">{{ parseTime(scope.row.planDate, '{y}-{m}-{d}') }}</template>
        </el-table-column>
        <el-table-column label="已收" prop="receiveAmount" width="90" align="center" />
        <el-table-column label="状态" prop="status" width="90" align="center">
          <template #default="scope">
            <dict-tag :options="biz_pay_period_status" :value="scope.row.status" />
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>

    <!-- 终止对话框 -->
    <el-dialog title="合同终止" v-model="terminateOpen" width="500px" append-to-body>
      <el-form label-width="90px">
        <el-form-item label="终止原因" required>
          <el-input v-model="terminateReason" type="textarea" :rows="3" placeholder="请输入终止原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="danger" @click="submitTerminate">确认终止</el-button>
          <el-button @click="terminateOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Contract">
import { listContract, getContract, addContract, updateContract, submitContract, finishContract, archiveContract, terminateContract, getCustomerQuotes } from "@/api/biz/contract";
import { listCustomer } from "@/api/biz/customer";
import { listUser } from "@/api/system/user";
import { listAttachment, addAttachment, delAttachment } from "@/api/biz/attachment";
import { getToken } from "@/utils/auth";

const baseUrl = import.meta.env.VITE_APP_BASE_API;
const upload = {
  headers: { Authorization: "Bearer " + getToken() },
  url: baseUrl + "/common/upload"
};
const MAX_FILE_SIZE = 50 * 1024 * 1024;

const { proxy } = getCurrentInstance();
const { biz_contract_status, biz_pay_period_status } = proxy.useDict("biz_contract_status", "biz_pay_period_status");

const contractList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const detailOpen = ref(false);
const terminateOpen = ref(false);
const terminateReason = ref("");
const title = ref("");
const customerOptions = ref([]);
const quoteOptions = ref([]);
const userOptions = ref([]);
const terminateId = ref(null);
const detail = ref({});
const attachments = ref([]);

/** 附件图片预览列表（仅图片类） */
const imageUrls = computed(() => attachments.value.filter(a => isImage(a.fileUrl)).map(a => fullUrl(a.fileUrl)));

function isImage(url) {
  return /\.(jpe?g|png|gif|bmp|webp)$/i.test(url || "");
}

/** 按扩展名返回文件类型图标 */
function docIcon(fileName) {
  const ext = (fileName || "").split(".").pop().toLowerCase();
  if (["pdf"].includes(ext)) return "Document";
  if (["doc", "docx", "txt"].includes(ext)) return "Tickets";
  if (["xls", "xlsx", "csv"].includes(ext)) return "Grid";
  if (["dwg", "dxf"].includes(ext)) return "Postcard";
  if (["zip", "rar", "7z"].includes(ext)) return "Box";
  return "Document";
}

function fullUrl(url) {
  if (!url) return "";
  if (/^https?:\/\//i.test(url)) return url;
  return baseUrl + url;
}

function formatSize(size) {
  if (!size) return "";
  if (size < 1024) return size + "B";
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + "KB";
  return (size / 1024 / 1024).toFixed(2) + "MB";
}

function loadAttachments(contractId) {
  listAttachment({ bizType: "contract", bizId: contractId }).then(res => {
    attachments.value = res.rows || [];
  });
}

function beforeUpload(file) {
  if (file.size > MAX_FILE_SIZE) {
    proxy.$modal.msgError("文件大小不能超过 50MB");
    return false;
  }
  return true;
}

function handleUploadSuccess(res, file) {
  if (res.code === 200) {
    addAttachment({
      bizType: "contract",
      bizId: detail.value.contractId,
      fileName: res.originalFilename || res.fileName,
      fileUrl: res.url,
      fileSize: file && file.size
    }).then(() => {
      proxy.$modal.msgSuccess("附件上传成功");
      loadAttachments(detail.value.contractId);
    });
  } else {
    proxy.$modal.msgError(res.msg || "上传失败");
  }
}

function handleDeleteAttachment(att) {
  proxy.$modal.confirm('确认删除附件"' + att.fileName + '"？').then(() => {
    return delAttachment(att.attachmentId);
  }).then(() => {
    proxy.$modal.msgSuccess("删除成功");
    loadAttachments(detail.value.contractId);
  }).catch(() => {});
}

/** 附件下载（blob 方式，保留原文件名） */
function downloadAttachment(att) {
  fetch(fullUrl(att.fileUrl)).then(res => res.blob()).then(blob => {
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = att.fileName || "合同附件";
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(a.href);
  }).catch(() => {
    proxy.$modal.msgError("下载失败，请重试");
  });
}

const queryParams = ref({ pageNum: 1, pageSize: 10, contractNo: null, customerName: null, status: null });

const rules = {
  customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
  contractAmount: [{ required: true, message: "请输入合同金额", trigger: "blur" }],
  signDate: [{ required: true, message: "请选择签订日期", trigger: "change" }],
  startDate: [{ required: true, message: "请选择工期开始日期", trigger: "change" }],
  endDate: [{ required: true, message: "请选择工期结束日期", trigger: "change" }]
};

const form = ref({});

function getList() {
  loading.value = true;
  listContract(queryParams.value).then(res => {
    contractList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function getCustomerOptions() {
  listCustomer({ pageNum: 1, pageSize: 500 }).then(res => {
    customerOptions.value = res.rows || [];
  });
  listUser({ pageNum: 1, pageSize: 500, status: '0' }).then(res => {
    userOptions.value = res.rows || [];
  });
}

function onCustomerChange(customerId) {
  form.value.quoteId = null;
  quoteOptions.value = [];
  if (customerId) {
    getCustomerQuotes(customerId).then(res => {
      quoteOptions.value = res.data || [];
    });
  }
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
  form.value = { contractId: null, customerId: null, quoteId: null, ownerUserId: null, contractAmount: 0, signDate: null, startDate: null, endDate: null, amountDiffNote: null };
  quoteOptions.value = [];
  proxy.resetForm("contractRef");
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增合同";
}

function handleUpdate(row) {
  reset();
  getContract(row.contractId).then(res => {
    form.value = res.data;
    open.value = true;
    title.value = "修改合同";
    onCustomerChange(form.value.customerId);
  });
}

function handleDetail(row) {
  getContract(row.contractId).then(res => {
    detail.value = res.data;
    detailOpen.value = true;
    loadAttachments(row.contractId);
  });
}

function submitForm() {
  proxy.$refs["contractRef"].validate(valid => {
    if (valid) {
      if (form.value.contractId != null) {
        // 未选择负责人时传0，后端nullif处理为清除绑定
        const data = { ...form.value, ownerUserId: form.value.ownerUserId ?? 0 };
        updateContract(data).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addContract(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功，已按默认比例生成四期收款计划");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleSubmit(row) {
  proxy.$modal.confirm('确认提交客户"' + row.customerName + '"的合同进入审批？').then(() => {
    return submitContract(row.contractId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("提交成功");
  }).catch(() => {});
}

function handleFinish(row) {
  proxy.$modal.confirm('确认登记客户"' + row.customerName + '"的合同完工？').then(() => {
    return finishContract(row.contractId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("完工登记成功");
  }).catch(() => {});
}

function handleArchive(row) {
  proxy.$modal.confirm('确认归档客户"' + row.customerName + '"的合同？归档后不可再操作。').then(() => {
    return archiveContract(row.contractId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("归档成功");
  }).catch(() => {});
}

function handleTerminate(row) {
  terminateId.value = row.contractId;
  terminateReason.value = "";
  terminateOpen.value = true;
}

function submitTerminate() {
  if (!terminateReason.value) {
    proxy.$modal.msgWarning("请输入终止原因");
    return;
  }
  terminateContract({ contractId: terminateId.value, terminateReason: terminateReason.value }).then(() => {
    proxy.$modal.msgSuccess("合同已终止");
    terminateOpen.value = false;
    getList();
  });
}

function cancel() {
  open.value = false;
  reset();
}

getCustomerOptions();
getList();
</script>

<style scoped>
.att-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}
.att-tip {
  font-size: 12px;
  color: #909399;
}
.att-empty {
  padding: 18px 0;
  text-align: center;
  font-size: 13px;
  color: #909399;
  background: #fafafa;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
}
.att-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 10px;
  margin-bottom: 8px;
}
.att-card {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 8px;
  transition: box-shadow .2s;
}
.att-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, .08);
}
.att-preview {
  height: 96px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
}
.att-img {
  width: 100%;
  height: 96px;
  border-radius: 4px;
  cursor: pointer;
}
.att-doc {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}
.att-name {
  margin-top: 6px;
  font-size: 12px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.att-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 2px;
}
.att-size {
  font-size: 11px;
  color: #909399;
}
.att-ops {
  display: flex;
  gap: 2px;
}
</style>
