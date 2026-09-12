<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="客户" prop="customerName">
        <el-input v-model="queryParams.customerName" placeholder="请输入客户姓名" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 130px">
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:disclosure:add']">发起交底</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="disclosureList">
      <el-table-column label="客户" align="center" prop="customerName" width="90" />
      <el-table-column label="关联合同" align="center" width="120">
        <template #default="scope">
          <span v-if="scope.row.contractAmount">{{ '￥' + Number(scope.row.contractAmount).toLocaleString() }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="工长" align="center" prop="externalAttendees" width="100" />
      <el-table-column label="会议日期" align="center" prop="meetingDate" width="110">
        <template #default="scope">{{ parseTime(scope.row.meetingDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <dict-tag :options="biz_audit_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['biz:disclosure:query']">详情</el-button>
          <el-button v-if="['0','3'].includes(scope.row.status)" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['biz:disclosure:edit']">修改</el-button>
          <el-button v-if="['0','3'].includes(scope.row.status)" link type="success" icon="Promotion" @click="handleSubmit(scope.row)" v-hasPermi="['biz:disclosure:submit']">提交</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 发起/修改交底 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="disclosureRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="关联合同" prop="contractId">
          <el-select v-model="form.contractId" placeholder="请选择生效中的合同" filterable style="width: 100%">
            <el-option v-for="c in contractOptions" :key="c.contractId" :label="(c.customerName || '') + (c.estate ? '（' + c.estate + '）' : '')" :value="c.contractId" />
          </el-select>
        </el-form-item>
        <el-form-item label="会议日期" prop="meetingDate">
          <el-date-picker v-model="form.meetingDate" type="date" value-format="YYYY-MM-DD" placeholder="选择交底会议日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="内部参与人" prop="attendees">
          <el-select v-model="internalUserIds" multiple placeholder="请选择内部参与人" filterable style="width: 100%">
            <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="外部参与人" prop="externalAttendees">
          <el-input v-model="form.externalAttendees" placeholder="工长姓名，多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="交底内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="设计要点 / 材料要求 / 施工注意事项" />
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
    <el-drawer v-model="detailOpen" :title="'交底详情 - ' + detail.disclosureNo" size="560px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="交底编号">{{ detail.disclosureNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="biz_audit_status" :value="detail.status" />
        </el-descriptions-item>
        <el-descriptions-item label="客户">{{ detail.customerName }}</el-descriptions-item>
        <el-descriptions-item label="会议日期">{{ parseTime(detail.meetingDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="工长">{{ detail.externalAttendees }}</el-descriptions-item>
        <el-descriptions-item label="交底内容" :span="2">{{ detail.content }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup name="Disclosure">
import { listDisclosure, getDisclosure, addDisclosure, updateDisclosure, submitDisclosure } from "@/api/biz/disclosure";
import { listContract } from "@/api/biz/contract";
import { listUser } from "@/api/system/user";

const { proxy } = getCurrentInstance();
const { biz_audit_status } = proxy.useDict("biz_audit_status");

const disclosureList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const detailOpen = ref(false);
const title = ref("");
const contractOptions = ref([]);
const userOptions = ref([]);
const internalUserIds = ref([]);
const detail = ref({});

const queryParams = ref({ pageNum: 1, pageSize: 10, customerName: null, status: null });

const rules = {
  contractId: [{ required: true, message: "请选择关联合同", trigger: "change" }],
  meetingDate: [{ required: true, message: "请选择会议日期", trigger: "change" }],
  content: [{ required: true, message: "交底内容不能为空", trigger: "blur" }]
};

const form = ref({});

function getList() {
  loading.value = true;
  listDisclosure(queryParams.value).then(res => {
    disclosureList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function getOptions() {
  listContract({ pageNum: 1, pageSize: 500, status: "2" }).then(res => {
    contractOptions.value = res.rows || [];
  });
  listUser({ pageNum: 1, pageSize: 500, status: "0" }).then(res => {
    userOptions.value = res.rows || [];
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
  form.value = { disclosureId: null, contractId: null, meetingDate: null, attendees: null, externalAttendees: null, content: null };
  internalUserIds.value = [];
  proxy.resetForm("disclosureRef");
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "发起技术交底";
}

function handleUpdate(row) {
  reset();
  getDisclosure(row.disclosureId).then(res => {
    form.value = res.data;
    internalUserIds.value = form.value.attendees ? form.value.attendees.split(",").map(Number) : [];
    open.value = true;
    title.value = "修改技术交底";
  });
}

function handleDetail(row) {
  getDisclosure(row.disclosureId).then(res => {
    detail.value = res.data;
    detailOpen.value = true;
  });
}

function submitForm() {
  proxy.$refs["disclosureRef"].validate(valid => {
    if (valid) {
      form.value.attendees = internalUserIds.value.join(",");
      if (form.value.disclosureId != null) {
        updateDisclosure(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addDisclosure(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleSubmit(row) {
  proxy.$modal.confirm('确认提交客户"' + row.customerName + '"的交底单进入审批？审批通过将触发第二期收款计划。').then(() => {
    return submitDisclosure(row.disclosureId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("提交成功");
  }).catch(() => {});
}

function cancel() {
  open.value = false;
  reset();
}

getOptions();
getList();
</script>
