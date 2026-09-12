<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="queryParams.customerId" placeholder="请选择客户" clearable filterable style="width: 160px">
          <el-option v-for="c in customerOptions" :key="c.customerId" :label="c.customerName" :value="c.customerId" />
        </el-select>
      </el-form-item>
      <el-form-item label="图纸名称" prop="drawingName">
        <el-input v-model="queryParams.drawingName" placeholder="请输入图纸名称" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="drawingType">
        <el-select v-model="queryParams.drawingType" placeholder="图纸类型" clearable style="width: 130px">
          <el-option v-for="dict in biz_drawing_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px">
          <el-option label="待确认" value="0" />
          <el-option label="已确认" value="1" />
          <el-option label="已作废" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Upload" @click="handleUpload" v-hasPermi="['biz:drawing:add']">上传图纸</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="drawingList">
      <el-table-column label="图纸名称" align="center" prop="drawingName" :show-overflow-tooltip="true" />
      <el-table-column label="客户" align="center" prop="customerName" :show-overflow-tooltip="true" width="110">
        <template #default="scope">{{ scope.row.customerName || '-' }}</template>
      </el-table-column>
      <el-table-column label="关联合同" align="center" width="120">
        <template #default="scope">
          <span v-if="scope.row.contractAmount">{{ '￥' + Number(scope.row.contractAmount).toLocaleString() }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" prop="drawingType" width="90">
        <template #default="scope">
          <dict-tag :options="biz_drawing_type" :value="scope.row.drawingType" />
        </template>
      </el-table-column>
      <el-table-column label="文件名" align="center" prop="fileName" :show-overflow-tooltip="true" width="180">
        <template #default="scope">
          <el-link v-if="isLink(scope.row.fileUrl)" type="primary" :underline="false" style="font-size: 12px">
            <el-icon><Link /></el-icon>&nbsp;{{ scope.row.fileName || "在线链接" }}
          </el-link>
          <span v-else>{{ scope.row.fileName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="版本" align="center" width="80">
        <template #default="scope">
          <el-tag size="small" :type="scope.row.isCurrent === '1' ? 'success' : 'info'">V{{ scope.row.version }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <el-tag size="small" v-if="scope.row.status === '0'" type="warning">待确认</el-tag>
          <el-tag size="small" v-else-if="scope.row.status === '1'" type="success">已确认</el-tag>
          <el-tag size="small" v-else type="info">已作废</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="确认人" align="center" prop="confirmUserName" width="90" />
      <el-table-column label="确认时间" align="center" prop="confirmTime" width="160">
        <template #default="scope">{{ parseTime(scope.row.confirmTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Download" @click="handleDownload(scope.row)">下载</el-button>
          <el-button v-if="scope.row.status === '0' && scope.row.isCurrent === '1'" link type="success" icon="Check" @click="handleConfirm(scope.row)" v-hasPermi="['biz:drawing:confirm']">确认</el-button>
          <el-button v-if="scope.row.status !== '1'" link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['biz:drawing:query']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 上传对话框 -->
    <el-dialog title="上传图纸" v-model="uploadOpen" width="560px" append-to-body>
      <el-form ref="uploadRef" :model="uploadForm" :rules="uploadRules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="绑定客户" prop="customerId">
              <el-select v-model="uploadForm.customerId" placeholder="可选" clearable filterable style="width: 100%" @change="handleCustomerChange">
                <el-option v-for="c in customerOptions" :key="c.customerId" :label="c.customerName" :value="c.customerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联合同" prop="contractId">
              <el-select v-model="uploadForm.contractId" placeholder="可选" clearable filterable style="width: 100%">
                <el-option v-for="c in contractOptions" :key="c.contractId" :label="contractLabel(c)" :value="c.contractId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="图纸名称" prop="drawingName">
          <el-input v-model="uploadForm.drawingName" placeholder="同名图纸自动版本+1" />
        </el-form-item>
        <el-form-item label="图纸类型" prop="drawingType">
          <el-select v-model="uploadForm.drawingType" placeholder="请选择图纸类型">
            <el-option v-for="dict in biz_drawing_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件格式" prop="fileSource">
          <el-radio-group v-model="uploadForm.fileSource" @change="handleSourceChange">
            <el-radio value="file">文件上传</el-radio>
            <el-radio value="link">在线链接</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="uploadForm.fileSource === 'link'" label="图纸链接" prop="fileUrl">
          <el-input v-model="uploadForm.fileUrl" placeholder="请输入图纸在线链接（http/https）" clearable>
            <template #prepend><el-icon><Link /></el-icon></template>
          </el-input>
          <div class="el-upload__tip">支持网盘/云文档等可公开访问的图纸链接</div>
        </el-form-item>
        <el-form-item v-else label="图纸文件" prop="fileUrl">
          <el-upload
            ref="uploadComp"
            :limit="1"
            accept=".dwg,.pdf,.jpg,.jpeg,.png,.zip,.rar"
            :headers="upload.headers"
            :action="upload.url"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :auto-upload="true"
            :file-list="fileList"
          >
            <el-button type="primary" icon="Upload">选择文件上传</el-button>
            <template #tip>
              <div class="el-upload__tip">支持 dwg/pdf/图片/压缩包，单文件不超过 50MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="saving" @click="submitUpload">保 存</el-button>
          <el-button @click="uploadOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Drawing">
import { listDrawing, addDrawing, confirmDrawing, delDrawing } from "@/api/biz/drawing";
import { listCustomer } from "@/api/biz/customer";
import { listContract } from "@/api/biz/contract";
import { getToken } from "@/utils/auth";

const baseUrl = import.meta.env.VITE_APP_BASE_API;

const { proxy } = getCurrentInstance();
const { biz_drawing_type } = proxy.useDict("biz_drawing_type");

const drawingList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const uploadOpen = ref(false);
const customerOptions = ref([]);
const allContractOptions = ref([]);
const fileList = ref([]);
const saving = ref(false);

const upload = {
  headers: { Authorization: "Bearer " + getToken() },
  url: baseUrl + "/common/upload"
};

const queryParams = ref({ pageNum: 1, pageSize: 10, customerId: null, drawingName: null, drawingType: null, status: null });

const uploadForm = ref({});
const uploadRules = {
  drawingName: [{ required: true, message: "图纸名称不能为空", trigger: "blur" }],
  drawingType: [{ required: true, message: "请选择图纸类型", trigger: "change" }],
  fileUrl: [
    { required: true, message: "请先上传图纸文件或填写图纸链接", trigger: "change" },
    {
      validator: (rule, value, callback) => {
        if (uploadForm.value.fileSource === "link" && value && !/^https?:\/\/.+/i.test(value)) {
          callback(new Error("链接必须以 http:// 或 https:// 开头"));
        } else {
          callback();
        }
      },
      trigger: ["change", "blur"]
    }
  ]
};

/** 是否在线链接（http/https 开头） */
function isLink(url) {
  return /^https?:\/\//i.test(url || "");
}

/** 合同下拉展示：客户姓名（楼盘） */
function contractLabel(c) {
  const customer = c.customerName || "";
  return customer + (c.estate ? `（${c.estate}）` : "");
}

/** 上传对话框合同选项：选了客户则仅显示该客户合同，否则显示全部 */
const contractOptions = computed(() => {
  if (uploadForm.value.customerId) {
    return allContractOptions.value.filter(c => c.customerId === uploadForm.value.customerId);
  }
  return allContractOptions.value;
});

function getList() {
  loading.value = true;
  listDrawing(queryParams.value).then(res => {
    drawingList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function getOptions() {
  listCustomer({ pageNum: 1, pageSize: 500 }).then(res => {
    customerOptions.value = res.rows || [];
  });
  listContract({ pageNum: 1, pageSize: 500 }).then(res => {
    allContractOptions.value = res.rows || [];
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

function handleUpload() {
  uploadForm.value = { customerId: null, contractId: null, drawingName: null, drawingType: null, fileSource: "file", fileUrl: null, fileName: null };
  fileList.value = [];
  proxy.resetForm("uploadRef");
  uploadOpen.value = true;
}

/** 切换客户时清空已选合同（避免跨客户脏数据） */
function handleCustomerChange() {
  uploadForm.value.contractId = null;
}

/** 切换文件格式时清空已填的文件/链接，避免脏数据 */
function handleSourceChange() {
  uploadForm.value.fileUrl = null;
  uploadForm.value.fileName = null;
  fileList.value = [];
  proxy.$refs["uploadRef"] && proxy.$refs["uploadRef"].clearValidate("fileUrl");
}

function handleUploadSuccess(res) {
  if (res.code === 200) {
    uploadForm.value.fileUrl = res.url;
    uploadForm.value.fileName = res.fileName || (res.url && res.url.split("/").pop());
    if (!uploadForm.value.drawingName) {
      uploadForm.value.drawingName = uploadForm.value.fileName ? uploadForm.value.fileName.replace(/\.[^.]+$/, "") : null;
    }
    proxy.$modal.msgSuccess("文件上传成功");
  } else {
    proxy.$modal.msgError(res.msg || "上传失败");
  }
}

function handleUploadError() {
  proxy.$modal.msgError("文件上传失败");
}

function submitUpload() {
  // 链接格式前置拦截，全局提示确保可见
  if (uploadForm.value.fileSource === "link" && uploadForm.value.fileUrl && !/^https?:\/\/.+/i.test(uploadForm.value.fileUrl)) {
    proxy.$modal.msgWarning("链接必须以 http:// 或 https:// 开头");
    return;
  }
  proxy.$refs["uploadRef"].validate(valid => {
    if (valid) {
      const isLinkMode = uploadForm.value.fileSource === "link";
      if (!uploadForm.value.fileUrl) {
        proxy.$modal.msgWarning(isLinkMode ? "请填写图纸链接" : "请先上传图纸文件");
        return;
      }
      const data = { ...uploadForm.value };
      // 链接模式下文件名默认取图纸名称
      if (isLinkMode && !data.fileName) {
        data.fileName = data.drawingName;
      }
      delete data.fileSource;
      saving.value = true;
      addDrawing(data).then(() => {
        proxy.$modal.msgSuccess("图纸保存成功");
        uploadOpen.value = false;
        getList();
      }).finally(() => { saving.value = false; });
    }
  });
}

function handleConfirm(row) {
  proxy.$modal.confirm('确认图纸"' + row.drawingName + ' V' + row.version + '"？确认后作为交付版本。').then(() => {
    return confirmDrawing(row.drawingId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("已确认");
  }).catch(() => {});
}

function handleDownload(row) {
  // 在线链接直接打开，本地上传文件拼接代理前缀
  window.open(isLink(row.fileUrl) ? row.fileUrl : baseUrl + row.fileUrl, "_blank");
}

function handleDelete(row) {
  proxy.$modal.confirm('确认删除图纸"' + row.drawingName + ' V' + row.version + '"？').then(() => {
    return delDrawing(row.drawingId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

getOptions();
getList();
</script>
