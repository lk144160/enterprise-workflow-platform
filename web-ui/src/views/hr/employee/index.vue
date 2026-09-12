<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="部门" prop="deptId">
        <el-select v-model="queryParams.deptId" placeholder="请选择部门" clearable style="width: 140px">
          <el-option v-for="d in deptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="在职状态" clearable style="width: 120px">
          <el-option v-for="dict in hr_employee_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['hr:employee:add']">入职登记</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Bell" @click="handleExpire" v-hasPermi="['hr:employee:query']">合同到期提醒</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="employeeList">
      <el-table-column label="姓名" align="center" prop="name" width="90" />
      <el-table-column label="性别" align="center" prop="gender" width="60">
        <template #default="scope">
          <span>{{ scope.row.gender === '0' ? '男' : scope.row.gender === '1' ? '女' : '未知' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="联系电话" align="center" prop="phone" width="120" />
      <el-table-column label="部门" align="center" prop="deptName" width="100" />
      <el-table-column label="岗位" align="center" prop="post" width="100" />
      <el-table-column label="入职日期" align="center" prop="entryDate" width="100">
        <template #default="scope">{{ parseTime(scope.row.entryDate, '{y}-{m}-{d}') }}</template>
      </el-table-column>
      <el-table-column label="合同到期" align="center" prop="contractExpireDate" width="110">
        <template #default="scope">
          <span :style="isExpiringSoon(scope.row) ? 'color:#e6a23c;font-weight:bold' : ''">{{ parseTime(scope.row.contractExpireDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <dict-tag :options="hr_employee_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="系统账号" align="center" prop="userName" width="110" />
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['hr:employee:query']">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['hr:employee:edit']">修改</el-button>
          <el-button v-if="scope.row.status !== '3'" link type="danger" icon="CircleClose" @click="handleLeave(scope.row)" v-hasPermi="['hr:employee:leave']">离职</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 入职登记/修改 -->
    <el-dialog :title="title" v-model="open" width="720px" append-to-body>
      <el-form ref="employeeRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio value="0">男</el-radio>
                <el-radio value="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="部门" prop="deptId">
              <el-select v-model="form.deptId" placeholder="请选择部门">
                <el-option v-for="d in deptOptions" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位" prop="post">
              <el-input v-model="form.post" placeholder="如：设计师" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="学历" prop="education">
              <el-select v-model="form.education" placeholder="请选择学历">
                <el-option v-for="dict in hr_education" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基本工资" prop="salary">
              <el-input-number v-model="form.salary" :precision="2" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker v-model="form.entryDate" type="date" value-format="YYYY-MM-DD" placeholder="选择入职日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="试用到期日" prop="probationEndDate">
              <el-date-picker v-model="form.probationEndDate" type="date" value-format="YYYY-MM-DD" placeholder="选择试用到期日" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="转正日期" prop="regularDate">
              <el-date-picker v-model="form.regularDate" type="date" value-format="YYYY-MM-DD" placeholder="选择转正日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同到期日" prop="contractExpireDate">
              <el-date-picker v-model="form.contractExpireDate" type="date" value-format="YYYY-MM-DD" placeholder="劳动合同到期日" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 离职对话框 -->
    <el-dialog title="离职登记" v-model="leaveOpen" width="500px" append-to-body>
      <el-alert type="warning" :closable="false" show-icon title="离职登记将同步停用该员工的系统账号" style="margin-bottom: 16px" />
      <el-form label-width="90px">
        <el-form-item label="离职日期" required>
          <el-date-picker v-model="leaveForm.leaveDate" type="date" value-format="YYYY-MM-DD" placeholder="选择离职日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="离职原因" required>
          <el-input v-model="leaveForm.leaveReason" type="textarea" :rows="3" placeholder="请输入离职原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="danger" @click="submitLeave">确认离职</el-button>
          <el-button @click="leaveOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情抽屉 -->
    <el-drawer v-model="detailOpen" :title="'员工详情 - ' + detail.name" size="560px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="员工编号">{{ detail.employeeNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detail.gender === '0' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号" :span="2">{{ maskIdCard(detail.idCard) }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detail.phone }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ detail.deptName }}</el-descriptions-item>
        <el-descriptions-item label="岗位">{{ detail.post }}</el-descriptions-item>
        <el-descriptions-item label="学历">{{ detail.education }}</el-descriptions-item>
        <el-descriptions-item label="入职日期">{{ parseTime(detail.entryDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="转正日期">{{ parseTime(detail.regularDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="合同到期日">{{ parseTime(detail.contractExpireDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="基本工资">{{ maskSalary(detail.salary) }}</el-descriptions-item>
        <el-descriptions-item label="系统账号">{{ detail.userName || '未关联' }}</el-descriptions-item>
        <el-descriptions-item label="离职日期">{{ parseTime(detail.leaveDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="离职原因" :span="2">{{ detail.leaveReason }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <!-- 到期提醒 -->
    <el-dialog title="劳动合同到期提醒（30天内）" v-model="expireOpen" width="640px" append-to-body>
      <el-table :data="expireList" size="small" border>
        <el-table-column label="姓名" prop="name" width="80" />
        <el-table-column label="部门" prop="deptName" width="100" />
        <el-table-column label="合同到期日" prop="contractExpireDate" width="120">
          <template #default="scope">{{ parseTime(scope.row.contractExpireDate, '{y}-{m}-{d}') }}</template>
        </el-table-column>
        <el-table-column label="剩余天数">
          <template #default="scope">
            <el-tag size="small" :type="remainDays(scope.row) <= 7 ? 'danger' : 'warning'">{{ remainDays(scope.row) }} 天</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="Employee">
import { listEmployee, getEmployee, addEmployee, updateEmployee, leaveEmployee, listContractExpire } from "@/api/biz/employee";
import { listDept } from "@/api/system/dept";

const { proxy } = getCurrentInstance();
const { hr_employee_status, hr_education } = proxy.useDict("hr_employee_status", "hr_education");

const employeeList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const leaveOpen = ref(false);
const detailOpen = ref(false);
const expireOpen = ref(false);
const title = ref("");
const deptOptions = ref([]);
const expireList = ref([]);
const detail = ref({});
const leaveForm = ref({ employeeId: null, leaveDate: null, leaveReason: null });

const queryParams = ref({ pageNum: 1, pageSize: 10, name: null, employeeNo: null, deptId: null, status: null });

const rules = {
  name: [{ required: true, message: "姓名不能为空", trigger: "blur" }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur" }],
  idCard: [{ pattern: /^\d{17}[\dXx]$/, message: "身份证号格式不正确", trigger: "blur" }],
  deptId: [{ required: true, message: "请选择部门", trigger: "change" }],
  entryDate: [{ required: true, message: "请选择入职日期", trigger: "change" }],
  contractExpireDate: [{ required: true, message: "请选择合同到期日", trigger: "change" }]
};

const form = ref({});

function getList() {
  loading.value = true;
  listEmployee(queryParams.value).then(res => {
    employeeList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function getDeptOptions() {
  listDept().then(res => {
    deptOptions.value = (res.data || []).filter(d => d.status === "0" && d.deptId !== 100);
  });
}

function remainDays(row) {
  if (!row.contractExpireDate) return "-";
  const diff = new Date(row.contractExpireDate).getTime() - Date.now();
  return Math.max(0, Math.ceil(diff / 86400000));
}

function isExpiringSoon(row) {
  if (row.status === "3" || !row.contractExpireDate) return false;
  return remainDays(row) <= 30;
}

function maskIdCard(idCard) {
  if (!idCard) return "-";
  return idCard.length >= 11 ? idCard.slice(0, 4) + "**********" + idCard.slice(-4) : idCard;
}

function maskSalary(salary) {
  if (salary === null || salary === undefined) return "-";
  return "￥****（敏感数据）";
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
  form.value = { employeeId: null, name: null, gender: "0", idCard: null, phone: null, deptId: null, post: null, education: null, salary: null, entryDate: null, probationEndDate: null, regularDate: null, contractExpireDate: null };
  proxy.resetForm("employeeRef");
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "入职登记";
}

function handleUpdate(row) {
  reset();
  getEmployee(row.employeeId).then(res => {
    form.value = res.data;
    open.value = true;
    title.value = "修改员工档案";
  });
}

function handleDetail(row) {
  getEmployee(row.employeeId).then(res => {
    detail.value = res.data;
    detailOpen.value = true;
  });
}

function handleExpire() {
  listContractExpire().then(res => {
    expireList.value = res.data || [];
    expireOpen.value = true;
  });
}

function handleLeave(row) {
  leaveForm.value = { employeeId: row.employeeId, leaveDate: null, leaveReason: null };
  leaveOpen.value = true;
}

function submitLeave() {
  if (!leaveForm.value.leaveDate || !leaveForm.value.leaveReason) {
    proxy.$modal.msgWarning("离职日期与原因必填");
    return;
  }
  leaveEmployee(leaveForm.value).then(() => {
    proxy.$modal.msgSuccess("离职登记成功，系统账号已同步停用");
    leaveOpen.value = false;
    getList();
  });
}

function submitForm() {
  proxy.$refs["employeeRef"].validate(valid => {
    if (valid) {
      if (form.value.employeeId != null) {
        updateEmployee(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addEmployee(form.value).then(() => {
          proxy.$modal.msgSuccess("入职登记成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function cancel() {
  open.value = false;
  reset();
}

getDeptOptions();
getList();
</script>
