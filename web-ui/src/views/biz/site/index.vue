<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="工地编号" prop="siteNo">
        <el-input v-model="queryParams.siteNo" placeholder="请输入工地编号" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="小区名称" prop="estate">
        <el-input v-model="queryParams.estate" placeholder="请输入小区名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="客户名称" prop="customerName">
        <el-input v-model="queryParams.customerName" placeholder="请输入客户名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="工地状态" clearable style="width: 140px">
          <el-option v-for="dict in biz_site_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:site:add']">新开工地</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="siteList">
      <el-table-column label="工地编号" prop="siteNo" width="150" />
      <el-table-column label="开工日期" prop="startDate" width="100" align="center">
        <template #default="scope">{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="小区名称" prop="estate" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="面积m²" prop="area" width="80" align="right" />
      <el-table-column label="门锁密码" width="120" align="center">
        <template #default="scope">
          <span v-if="scope.row.keyPassword">
            {{ visibleKeys.has(scope.row.siteId) ? scope.row.keyPassword : '●●●●●●' }}
            <el-icon style="cursor: pointer; vertical-align: -2px" @click="toggleKey(scope.row.siteId)">
              <View v-if="visibleKeys.has(scope.row.siteId)" /><Hide v-else />
            </el-icon>
          </span>
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="客户名称" prop="customerName" width="100" :show-overflow-tooltip="true" />
      <el-table-column label="设计师" prop="designerName" width="100" :show-overflow-tooltip="true" />
      <el-table-column label="监理" prop="supervisorName" width="100" :show-overflow-tooltip="true" />
      <el-table-column label="施工进度" width="170">
        <template #default="scope">
          <div class="progress-cell">
            <el-progress :percentage="progressOf(scope.row)" :stroke-width="8" :color="scope.row.lateFlag === 1 ? '#F56C6C' : undefined" />
            <div class="stage-now">
              <dict-tag :options="biz_stage_type" :value="scope.row.currentStageType" v-if="scope.row.currentStageType" />
              <span v-else>{{ scope.row.status === '2' ? '全部完成' : '未开工' }}</span>
              <el-tag v-if="scope.row.lateFlag === 1" type="danger" size="small">延期</el-tag>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template #default="scope"><dict-tag :options="biz_site_status" :value="scope.row.status" /></template>
      </el-table-column>
      <el-table-column label="操作" width="170" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['biz:site:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['biz:site:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新开工地/修改 对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="siteRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="关联合同" prop="contractId" v-if="form.siteId == null">
          <el-select v-model="form.contractId" placeholder="请选择已生效合同" style="width: 100%" @change="handleContractChange">
            <el-option v-for="c in contracts" :key="c.contractId" :label="(c.customerName || '') + (c.estate ? '（' + c.estate + '）' : '')" :value="c.contractId" />
          </el-select>
        </el-form-item>
        <el-form-item label="合同编号" v-if="form.siteId != null">
          <el-input :model-value="form.contractNo" disabled />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开工日期" prop="startDate">
              <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="选择开工日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面积m²" prop="area">
              <el-input-number v-model="form.area" :min="0" :precision="2" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="小区名称" prop="estate">
          <el-input v-model="form.estate" placeholder="请输入小区名称" />
        </el-form-item>
        <el-form-item label="门锁密码" prop="keyPassword">
          <el-input v-model="form.keyPassword" placeholder="门锁密码/钥匙存放说明" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="设计师" prop="designerId">
              <el-select v-model="form.designerId" placeholder="选择设计师" style="width: 100%" @change="val => (form.designerName = userNameOf(val))">
                <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="监理" prop="supervisorId">
              <el-select v-model="form.supervisorId" placeholder="选择监理（项目经理）" style="width: 100%" @change="val => (form.supervisorName = userNameOf(val))">
                <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Site">
import { listSite, getSite, listAvailableContracts, addSite, updateSite, delSite } from "@/api/biz/site";
import { listUser } from "@/api/system/user";
import { View, Hide } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance();
const router = useRouter();
const { biz_site_status, biz_stage_type } = proxy.useDict("biz_site_status", "biz_stage_type");

const siteList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const title = ref("");
const contracts = ref([]);
const userOptions = ref([]);
const visibleKeys = ref(new Set());

const queryParams = ref({ pageNum: 1, pageSize: 10, siteNo: null, estate: null, customerName: null, status: null });

const rules = {
  contractId: [{ required: true, message: "请选择关联合同", trigger: "change" }],
  startDate: [{ required: true, message: "请选择开工日期", trigger: "change" }],
  keyPassword: [{ required: true, message: "请填写门锁密码", trigger: "blur" }]
};

const form = ref({});

function getList() {
  loading.value = true;
  listSite(queryParams.value).then(res => {
    siteList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function progressOf(row) {
  if (!row.totalStages) return 0;
  return Math.round(((row.doneStages || 0) / row.totalStages) * 100);
}

function toggleKey(siteId) {
  if (visibleKeys.value.has(siteId)) {
    visibleKeys.value.delete(siteId);
  } else {
    visibleKeys.value.add(siteId);
  }
  visibleKeys.value = new Set(visibleKeys.value);
}

function userNameOf(userId) {
  const u = userOptions.value.find(x => x.userId === userId);
  return u ? u.nickName : "";
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
  form.value = { siteId: null, contractId: null, startDate: null, estate: null, area: null, keyPassword: null, designerId: null, designerName: null, supervisorId: null, supervisorName: null, remark: null };
  proxy.resetForm("siteRef");
}

function handleAdd() {
  reset();
  Promise.all([listAvailableContracts(), listUser({ pageNum: 1, pageSize: 500, status: "0" })]).then(([c, u]) => {
    contracts.value = c.data || [];
    userOptions.value = u.rows || [];
  });
  open.value = true;
  title.value = "新开工地";
}

function handleContractChange(contractId) {
  const c = contracts.value.find(x => x.contractId === contractId);
  if (c) {
    form.value.estate = c.estate;
    form.value.area = c.area;
    form.value.startDate = c.startDate;
    form.value.designerId = c.ownerUserId;
    form.value.designerName = c.ownerUserName;
  }
}

function handleUpdate(row) {
  reset();
  listUser({ pageNum: 1, pageSize: 500, status: "0" }).then(u => (userOptions.value = u.rows || []));
  getSite(row.siteId).then(res => {
    form.value = res.data;
    form.value.stages = null;
    open.value = true;
    title.value = "修改工地";
  });
}

function submitForm() {
  proxy.$refs["siteRef"].validate(valid => {
    if (valid) {
      if (form.value.siteId != null) {
        updateSite(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addSite(form.value).then(() => {
          proxy.$modal.msgSuccess("开工成功，已按标准工序铺排施工计划");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row) {
  proxy.$modal.confirm('确认删除工地【' + row.siteNo + '】（' + (row.estate || '') + '）？阶段计划与现场照片将一并删除。').then(() => {
    return delSite(row.siteId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

function handleDetail(row) {
  router.push("/design/site/detail/" + row.siteId);
}

function cancel() {
  open.value = false;
  reset();
}

getList();
</script>

<style scoped>
.progress-cell { line-height: 1.4; }
.stage-now { margin-top: 2px; display: flex; gap: 6px; align-items: center; }
</style>
