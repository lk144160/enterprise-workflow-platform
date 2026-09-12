<template>
  <div class="app-container">
    <el-row :gutter="16" style="margin-bottom: 12px">
      <el-col :span="12">
        <el-alert v-if="total > 0" :title="'您有 ' + total + ' 条待审批任务'" type="warning" show-icon :closable="false" />
        <el-alert v-else title="暂无待办审批任务" type="success" show-icon :closable="false" />
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="todoList">
      <el-table-column label="审批类型" align="center" prop="bizType" width="110">
        <template #default="scope">
          <dict-tag :options="flow_biz_type" :value="scope.row.bizType" />
        </template>
      </el-table-column>
      <el-table-column label="申请事项" align="center" prop="bizTitle" :show-overflow-tooltip="true" />
      <el-table-column label="客户" align="center" prop="customerName" width="90">
        <template #default="scope">{{ scope.row.customerName || '-' }}</template>
      </el-table-column>
      <el-table-column label="金额" align="center" prop="bizAmount" width="110" />
      <el-table-column label="申请人" align="center" prop="startUserName" width="100" />
      <el-table-column label="发起时间" align="center" prop="startTime" width="160">
        <template #default="scope">{{ parseTime(scope.row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="当前节点" align="center" prop="nodeName" width="110" />
      <el-table-column label="操作" width="240" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row.instanceId)">详情</el-button>
          <el-button link type="success" icon="Check" @click="openHandle(scope.row, 'agree')">同意</el-button>
          <el-button link type="danger" icon="Close" @click="openHandle(scope.row, 'reject')">驳回</el-button>
          <el-button link type="warning" icon="Right" @click="openTransfer(scope.row)">转交</el-button>
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

    <!-- 审批处理 -->
    <el-dialog :title="handleAction === 'agree' ? '审批同意' : '审批驳回'" v-model="handleOpen" width="500px" append-to-body>
      <el-form label-width="90px">
        <el-form-item label="申请事项">
          <span>{{ currentTask.bizTitle }}</span>
        </el-form-item>
        <el-form-item label="客户">
          <span>{{ currentTask.customerName || '-' }}</span>
        </el-form-item>
        <el-form-item :label="handleAction === 'agree' ? '审批意见' : '驳回原因'" :required="handleAction === 'reject'">
          <el-input v-model="handleOpinion" type="textarea" :rows="3" :placeholder="handleAction === 'agree' ? '审批意见（可选）' : '驳回原因（必填）'" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :type="handleAction === 'agree' ? 'primary' : 'danger'" @click="submitHandle">确 定</el-button>
          <el-button @click="handleOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 转交 -->
    <el-dialog title="审批转交" v-model="transferOpen" width="500px" append-to-body>
      <el-form label-width="90px">
        <el-form-item label="申请事项">
          <span>{{ currentTask.bizTitle }}</span>
        </el-form-item>
        <el-form-item label="客户">
          <span>{{ currentTask.customerName || '-' }}</span>
        </el-form-item>
        <el-form-item label="转交给" required>
          <el-select v-model="transferTargetId" placeholder="请选择转交对象" filterable style="width: 100%">
            <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="转交说明">
          <el-input v-model="transferOpinion" type="textarea" :rows="2" placeholder="转交说明（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitTransfer">确 定</el-button>
          <el-button @click="transferOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="FlowTodo">
import { listTodo, getInstance, handleTask, transferTask } from "@/api/biz/flow";
import { listUser } from "@/api/system/user";
import { getCustomer } from "@/api/biz/customer";
import { listQuote } from "@/api/biz/quote";
import FlowBizDetail from "@/views/flow/components/FlowBizDetail.vue";

const { proxy } = getCurrentInstance();
const { flow_biz_type, crm_customer_source, crm_intention_level, crm_customer_status, biz_audit_status } =
  proxy.useDict("flow_biz_type", "crm_customer_source", "crm_intention_level", "crm_customer_status", "biz_audit_status");

const todoList = ref([]);
const loading = ref(true);
const total = ref(0);
const detailOpen = ref(false);
const detail = ref({});
const customerInfo = ref({});
const quoteList = ref([]);
const handleOpen = ref(false);
const handleAction = ref("agree");
const handleOpinion = ref("");
const currentTask = ref({});
const transferOpen = ref(false);
const transferTargetId = ref(null);
const transferOpinion = ref("");
const userOptions = ref([]);

const queryParams = ref({ pageNum: 1, pageSize: 10 });

function statusText(status) {
  return { "1": "审批中", "2": "已通过", "3": "已驳回", "4": "已撤销" }[status] || status;
}

function taskText(status) {
  return { "0": "待处理", "1": "已同意", "2": "已驳回", "3": "已转交", "4": "已撤销" }[status] || status;
}

function timelineType(status) {
  return { "0": "primary", "1": "success", "2": "danger", "3": "warning", "4": "info" }[status] || "primary";
}

function getList() {
  loading.value = true;
  listTodo(queryParams.value).then(res => {
    todoList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
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

function openHandle(row, action) {
  currentTask.value = row;
  handleAction.value = action;
  handleOpinion.value = "";
  handleOpen.value = true;
}

function submitHandle() {
  if (handleAction.value === "reject" && !handleOpinion.value) {
    proxy.$modal.msgWarning("驳回时必须填写驳回原因");
    return;
  }
  handleTask({ taskId: currentTask.value.taskId, action: handleAction.value, opinion: handleOpinion.value }).then(() => {
    proxy.$modal.msgSuccess(handleAction.value === "agree" ? "已同意" : "已驳回");
    handleOpen.value = false;
    getList();
  });
}

function openTransfer(row) {
  currentTask.value = row;
  transferTargetId.value = null;
  transferOpinion.value = "";
  if (userOptions.value.length === 0) {
    listUser({ pageNum: 1, pageSize: 500, status: "0" }).then(res => {
      userOptions.value = res.rows || [];
    });
  }
  transferOpen.value = true;
}

function submitTransfer() {
  if (!transferTargetId.value) {
    proxy.$modal.msgWarning("请选择转交对象");
    return;
  }
  transferTask({ taskId: currentTask.value.taskId, targetUserId: transferTargetId.value, opinion: transferOpinion.value }).then(() => {
    proxy.$modal.msgSuccess("转交成功");
    transferOpen.value = false;
    getList();
  });
}

getList();
</script>

<style scoped>
:deep(.el-table .current-quote-row) {
  --el-table-tr-bg-color: #fdf6ec;
}
</style>
