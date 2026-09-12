<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="流程编码" prop="flowCode">
        <el-input v-model="queryParams.flowCode" placeholder="请输入流程编码" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="流程名称" prop="flowName">
        <el-input v-model="queryParams.flowName" placeholder="请输入流程名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:flow:add']">新增</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="definitionList">
      <el-table-column label="流程编码" align="center" prop="flowCode" width="200" />
      <el-table-column label="流程名称" align="center" prop="flowName" />
      <el-table-column label="业务类型" align="center" prop="bizType" width="120">
        <template #default="scope">
          <dict-tag :options="flow_biz_type" :value="scope.row.bizType" />
        </template>
      </el-table-column>
      <el-table-column label="节点数" align="center" width="80">
        <template #default="scope">
          <el-tag size="small" type="info">{{ (scope.row.nodes || []).length }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template #default="scope">
          <el-tag size="small" :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:flow:edit']">修改</el-button>
          <el-button link type="primary" icon="Setting" @click="handleToggle(scope.row)" v-hasPermi="['system:flow:edit']">{{ scope.row.status === '0' ? '停用' : '启用' }}</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:flow:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/修改 -->
    <el-dialog :title="title" v-model="open" width="860px" append-to-body>
      <el-form ref="definitionRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="流程编码" prop="flowCode">
              <el-input v-model="form.flowCode" placeholder="如 QUOTE_APPROVAL" :disabled="form.flowId != null" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="流程名称" prop="flowName">
              <el-input v-model="form.flowName" placeholder="如 报价审批流" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="业务类型" prop="bizType">
              <el-select v-model="form.bizType" placeholder="请选择业务类型">
                <el-option v-for="dict in flow_biz_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider content-position="left">审批节点（按顺序流转）</el-divider>
      <el-row class="mb8">
        <el-button type="primary" plain icon="Plus" size="small" @click="addNode">添加节点</el-button>
      </el-row>
      <el-table :data="form.nodes" size="small" border>
        <el-table-column label="顺序" prop="nodeOrder" width="60" align="center" />
        <el-table-column label="节点名称" width="140">
          <template #default="scope">
            <el-input v-model="scope.row.nodeName" size="small" placeholder="如 部门主管审批" />
          </template>
        </el-table-column>
        <el-table-column label="审批人类型" width="140">
          <template #default="scope">
            <el-select v-model="scope.row.approverType" size="small" @change="scope.row.approverValue = null">
              <el-option label="指定用户" value="1" />
              <el-option label="部门主管" value="2" />
              <el-option label="指定角色" value="3" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="审批人" min-width="180">
          <template #default="scope">
            <el-select v-if="scope.row.approverType === '1'" v-model="scope.row.userIds" multiple size="small" filterable placeholder="选择用户" style="width: 100%">
              <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
            </el-select>
            <el-input v-else-if="scope.row.approverType === '2'" v-model="scope.row.approverValue" size="small" disabled value="发起人部门主管" />
            <el-select v-else v-model="scope.row.approverValue" size="small" filterable placeholder="选择角色" style="width: 100%">
              <el-option v-for="r in roleOptions" :key="r.roleId" :label="r.roleName" :value="String(r.roleId)" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="签署方式" width="110">
          <template #default="scope">
            <el-select v-model="scope.row.signType" size="small">
              <el-option label="或签" value="1" />
              <el-option label="会签" value="2" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="金额阈值" width="150">
          <template #default="scope">
            <el-input-number v-model="scope.row.amountThreshold" :precision="2" :min="0" size="small" controls-position="right" style="width: 100%" placeholder="留空不过滤" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60" align="center">
          <template #default="scope">
            <el-button link type="danger" icon="Delete" size="small" @click="removeNode(scope.$index)" />
          </template>
        </el-table-column>
      </el-table>
      <div class="el-upload__tip" style="margin-top: 8px">金额阈值：单据金额 ≥ 该值时节点才生效（0 或留空表示始终生效），用于大额单据追加总经理审批。</div>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="FlowDefinition">
import { listDefinition, getDefinition, addDefinition, updateDefinition, delDefinition } from "@/api/biz/flow";
import { listUser } from "@/api/system/user";
import { listRole } from "@/api/system/role";

const { proxy } = getCurrentInstance();
const { flow_biz_type } = proxy.useDict("flow_biz_type");

const definitionList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const title = ref("");
const userOptions = ref([]);
const roleOptions = ref([]);

const queryParams = ref({ pageNum: 1, pageSize: 10, flowCode: null, flowName: null });

const rules = {
  flowCode: [{ required: true, message: "流程编码不能为空", trigger: "blur" }],
  flowName: [{ required: true, message: "流程名称不能为空", trigger: "blur" }],
  bizType: [{ required: true, message: "请选择业务类型", trigger: "change" }]
};

const form = ref({ nodes: [] });

function getList() {
  loading.value = true;
  listDefinition(queryParams.value).then(res => {
    definitionList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function getOptions() {
  listUser({ pageNum: 1, pageSize: 500, status: "0" }).then(res => {
    userOptions.value = res.rows || [];
  });
  listRole().then(res => {
    roleOptions.value = res.rows || [];
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
  form.value = { flowId: null, flowCode: null, flowName: null, bizType: null, status: "0", nodes: [] };
  proxy.resetForm("definitionRef");
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增审批流程";
}

function handleUpdate(row) {
  reset();
  getDefinition(row.flowId).then(res => {
    const data = res.data;
    (data.nodes || []).forEach(n => {
      n.userIds = n.approverType === "1" && n.approverValue ? n.approverValue.split(",").map(Number) : [];
    });
    form.value = data;
    open.value = true;
    title.value = "修改审批流程";
  });
}

function handleToggle(row) {
  const action = row.status === "0" ? "停用" : "启用";
  proxy.$modal.confirm('确认' + action + '流程"' + row.flowName + '"？').then(() => {
    return updateDefinition({ ...row, status: row.status === "0" ? "1" : "0" });
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess(action + "成功");
  }).catch(() => {});
}

function handleDelete(row) {
  proxy.$modal.confirm('确认删除流程"' + row.flowName + '"？').then(() => {
    return delDefinition(row.flowId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

function addNode() {
  form.value.nodes.push({ nodeOrder: form.value.nodes.length + 1, nodeName: null, approverType: "1", userIds: [], approverValue: null, signType: "1", amountThreshold: null });
}

function removeNode(index) {
  form.value.nodes.splice(index, 1);
  form.value.nodes.forEach((n, i) => { n.nodeOrder = i + 1; });
}

function submitForm() {
  proxy.$refs["definitionRef"].validate(valid => {
    if (valid) {
      if (!form.value.nodes || form.value.nodes.length === 0) {
        proxy.$modal.msgWarning("请至少配置一个审批节点");
        return;
      }
      const payload = { ...form.value };
      payload.nodes = form.value.nodes.map(n => ({
        ...n,
        approverValue: n.approverType === "1" ? (n.userIds || []).join(",") : n.approverValue
      }));
      if (payload.nodes.some(n => !n.nodeName)) {
        proxy.$modal.msgWarning("节点名称不能为空");
        return;
      }
      if (payload.nodes.some(n => n.approverType !== "2" && !n.approverValue)) {
        proxy.$modal.msgWarning("请为每个节点选择审批人");
        return;
      }
      if (form.value.flowId != null) {
        updateDefinition(payload).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addDefinition(payload).then(() => {
          proxy.$modal.msgSuccess("新增成功");
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

getOptions();
getList();
</script>
