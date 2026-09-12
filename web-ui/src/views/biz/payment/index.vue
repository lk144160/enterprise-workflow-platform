<template>
  <div class="app-container">
    <el-tabs v-model="activeTab">
      <!-- 收款计划（按客户聚合） -->
      <el-tab-pane label="收款计划" name="plan">
        <el-form :model="planQuery" ref="planQueryRef" :inline="true">
          <el-form-item label="客户" prop="customerName">
            <el-input v-model="planQuery.customerName" placeholder="请输入客户姓名" clearable style="width: 140px" @keyup.enter="loadPlans" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="planQuery.status" placeholder="收款状态" clearable style="width: 130px">
              <el-option v-for="dict in biz_pay_period_status" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="loadPlans">搜索</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="planLoading" :data="planList" @expand-change="handleExpandChange">
          <el-table-column type="expand">
            <template #default="scope">
              <div class="expand-wrap">
                <!-- 客户基础信息 -->
                <el-divider content-position="left">客户基础信息</el-divider>
                <el-descriptions :column="3" border size="small">
                  <el-descriptions-item label="客户姓名">{{ expandData[scope.row.customerId]?.customer?.customerName || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="手机号">{{ expandData[scope.row.customerId]?.customer?.phone || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="客户负责人">{{ expandData[scope.row.customerId]?.customer?.ownerName || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="来源"><dict-tag :options="crm_customer_source" :value="expandData[scope.row.customerId]?.customer?.source" /></el-descriptions-item>
                  <el-descriptions-item label="意向等级"><dict-tag :options="crm_intention_level" :value="expandData[scope.row.customerId]?.customer?.intentionLevel" /></el-descriptions-item>
                  <el-descriptions-item label="付款方式">
                    <dict-tag v-if="expandData[scope.row.customerId]?.customer?.payMethod" :options="biz_pay_type" :value="expandData[scope.row.customerId].customer.payMethod" />
                    <span v-else>-</span>
                  </el-descriptions-item>
                  <el-descriptions-item label="楼盘">{{ expandData[scope.row.customerId]?.customer?.estate || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="户型">{{ expandData[scope.row.customerId]?.customer?.houseType || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="面积">{{ expandData[scope.row.customerId]?.customer?.area ? expandData[scope.row.customerId].customer.area + ' ㎡' : '-' }}</el-descriptions-item>
                  <el-descriptions-item label="预算">{{ expandData[scope.row.customerId]?.customer?.budget || '-' }}</el-descriptions-item>
                  <el-descriptions-item label="客户状态"><dict-tag :options="crm_customer_status" :value="expandData[scope.row.customerId]?.customer?.status" /></el-descriptions-item>
                  <el-descriptions-item label="装修需求">{{ expandData[scope.row.customerId]?.customer?.demand || '-' }}</el-descriptions-item>
                </el-descriptions>
                <!-- 各合同期次明细 -->
                <template v-for="ct in (expandData[scope.row.customerId]?.contracts || [])" :key="ct.contractId">
                  <el-divider content-position="left">合同 {{ ct.contractNo }}（￥{{ ct.contractAmount }}）</el-divider>
                  <el-table :data="ct.plans" size="small" border>
                    <el-table-column label="期次" align="center" prop="periodNo" width="60" />
                    <el-table-column label="期次名称" align="center" prop="periodName" width="110">
                      <template #default="sub">
                        <dict-tag v-if="matchDict(sub.row.periodName)" :options="biz_pay_period_name" :value="sub.row.periodName" />
                        <span v-else>{{ sub.row.periodName }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="比例" align="center" prop="ratio" width="70">
                      <template #default="sub">{{ sub.row.ratio }}%</template>
                    </el-table-column>
                    <el-table-column label="应收金额" align="center" prop="planAmount" width="110" />
                    <el-table-column label="累计实收" align="center" prop="receiveAmount" width="110" />
                    <el-table-column label="剩余应收" align="center" prop="remainAmount" width="110">
                      <template #default="sub">
                        <span :style="Number(sub.row.remainAmount) > 0 ? 'color:#f56c6c' : ''">{{ sub.row.remainAmount }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="计划收款日" align="center" prop="planDate" width="110">
                      <template #default="sub">{{ parseTime(sub.row.planDate, '{y}-{m}-{d}') }}</template>
                    </el-table-column>
                    <el-table-column label="状态" align="center" prop="status" width="90">
                      <template #default="sub">
                        <dict-tag :options="biz_pay_period_status" :value="sub.row.status" />
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="240" align="center" class-name="small-padding fixed-width">
                      <template #default="sub">
                        <el-button v-if="sub.row.status === '1'" link type="success" icon="Money" @click="handleRegister(sub.row, scope.row.customerId)" v-hasPermi="['biz:payment:register']">收款登记</el-button>
                        <el-button v-if="sub.row.status === '1'" link type="warning" icon="Discount" @click="handleReduce(sub.row)" v-hasPermi="['biz:payment:reduce']">减免</el-button>
                        <el-button link type="primary" icon="Setting" @click="handleAdjust(sub.row)" v-hasPermi="['biz:payment:planEdit']">调整计划</el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </template>
                <el-empty v-if="expandData[scope.row.customerId] && !expandData[scope.row.customerId].loaded" description="加载中..." :image-size="40" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="客户" align="center" prop="customerName" width="110" />
          <el-table-column label="客户负责人" align="center" prop="ownerName" width="100">
            <template #default="scope">{{ scope.row.ownerName || '-' }}</template>
          </el-table-column>
          <el-table-column label="楼盘" align="center" prop="estate" :show-overflow-tooltip="true" />
          <el-table-column label="合同金额" align="center" prop="contractAmount" width="110" />
          <el-table-column label="应收合计" align="center" prop="planAmount" width="110" />
          <el-table-column label="累计实收" align="center" prop="receiveAmount" width="110" />
          <el-table-column label="剩余应收" align="center" prop="remainAmount" width="110">
            <template #default="scope">
              <span :style="Number(scope.row.remainAmount) > 0 ? 'color:#f56c6c' : ''">{{ scope.row.remainAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="期次进度" align="center" width="100">
            <template #default="scope">
              <el-tag size="small" :type="scope.row.settledCount >= scope.row.periodCount ? 'success' : 'warning'">
                已结清 {{ scope.row.settledCount }}/{{ scope.row.periodCount }} 期
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="下期收款日" align="center" prop="nextPlanDate" width="110">
            <template #default="scope">{{ parseTime(scope.row.nextPlanDate, '{y}-{m}-{d}') || '-' }}</template>
          </el-table-column>
          <el-table-column label="状态" align="center" width="90">
            <template #default="scope">
              <el-tag size="small" :type="scope.row.settledCount >= scope.row.periodCount ? 'success' : 'primary'">
                {{ scope.row.settledCount >= scope.row.periodCount ? '已结清' : '收款中' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="planTotal > 0" :total="planTotal" v-model:page="planQuery.pageNum" v-model:limit="planQuery.pageSize" @pagination="loadPlans" />
      </el-tab-pane>

      <!-- 收款记录 -->
      <el-tab-pane label="收款记录" name="record">
        <el-form :model="recordQuery" ref="recordQueryRef" :inline="true">
          <el-form-item label="客户" prop="customerName">
            <el-input v-model="recordQuery.customerName" placeholder="请输入客户姓名" clearable style="width: 140px" @keyup.enter="loadRecords" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="loadRecords">搜索</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="recordLoading" :data="recordList">
          <el-table-column label="客户" align="center" prop="customerName" width="100" />
          <el-table-column label="期次" align="center" prop="periodNo" width="60" />
          <el-table-column label="期次名称" align="center" prop="periodName" width="100">
            <template #default="scope">
              <dict-tag v-if="matchDict(scope.row.periodName)" :options="biz_pay_period_name" :value="scope.row.periodName" />
              <span v-else>{{ scope.row.periodName }}</span>
            </template>
          </el-table-column>
          <el-table-column label="金额" align="center" prop="amount" width="120">
            <template #default="scope">
              <span :style="Number(scope.row.amount) < 0 ? 'color:#f56c6c' : ''">{{ scope.row.amount }}</span>
            </template>
          </el-table-column>
          <el-table-column label="收款方式" align="center" prop="payType" width="100">
            <template #default="scope">
              <dict-tag :options="biz_pay_type" :value="scope.row.payType" />
            </template>
          </el-table-column>
          <el-table-column label="到账时间" align="center" prop="payTime" width="160">
            <template #default="scope">{{ parseTime(scope.row.payTime) }}</template>
          </el-table-column>
          <el-table-column label="登记人" align="center" prop="operatorName" width="100" />
          <el-table-column label="冲正原因" align="center" prop="reverseReason" :show-overflow-tooltip="true" />
          <el-table-column label="操作" width="100" align="center">
            <template #default="scope">
              <el-button v-if="Number(scope.row.amount) > 0" link type="danger" icon="RefreshLeft" @click="handleReverse(scope.row)" v-hasPermi="['biz:payment:reverse']">冲正</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="recordTotal > 0" :total="recordTotal" v-model:page="recordQuery.pageNum" v-model:limit="recordQuery.pageSize" @pagination="loadRecords" />
      </el-tab-pane>
    </el-tabs>

    <!-- 收款登记对话框 -->
    <el-dialog title="收款登记" v-model="registerOpen" width="560px" append-to-body>
      <el-form ref="registerRef" :model="registerForm" :rules="registerRules" label-width="100px">
        <el-form-item label="合同">
          <span>{{ registerForm.contractNo }}（{{ registerForm.customerName }}）</span>
        </el-form-item>
        <el-form-item label="期次">
          <span>第{{ registerForm.periodNo }}期 {{ registerForm.periodName }}（应收 {{ registerForm.planAmount }}，剩余 {{ registerForm.remainAmount }}）</span>
        </el-form-item>
        <el-form-item label="实收金额" prop="amount">
          <el-input-number v-model="registerForm.amount" :precision="2" :min="0.01" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="收款方式" prop="payType">
          <el-select v-model="registerForm.payType" placeholder="请选择收款方式">
            <el-option v-for="dict in biz_pay_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="到账时间" prop="payTime">
          <el-date-picker v-model="registerForm.payTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择到账时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="偏差说明" prop="deviationNote">
          <el-input v-model="registerForm.deviationNote" type="textarea" :rows="2" placeholder="实收与剩余应收不一致时必填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitRegister">确 定</el-button>
          <el-button @click="registerOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 冲正对话框 -->
    <el-dialog title="收款冲正" v-model="reverseOpen" width="500px" append-to-body>
      <el-alert type="warning" :closable="false" show-icon title="冲正将生成一条等额负数记录冲抵该笔收款，请谨慎操作" style="margin-bottom: 16px" />
      <el-form label-width="90px">
        <el-form-item label="冲正原因" required>
          <el-input v-model="reverseReason" type="textarea" :rows="3" placeholder="请输入冲正原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="danger" @click="submitReverse">确认冲正</el-button>
          <el-button @click="reverseOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 减免对话框 -->
    <el-dialog title="收款减免申请" v-model="reduceOpen" width="500px" append-to-body>
      <el-form ref="reduceRef" :model="reduceForm" :rules="reduceRules" label-width="90px">
        <el-form-item label="减免金额" prop="reduceAmount">
          <el-input-number v-model="reduceForm.reduceAmount" :precision="2" :min="0.01" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="减免原因" prop="reason">
          <el-input v-model="reduceForm.reason" type="textarea" :rows="3" placeholder="请输入减免原因（将提交总经理审批）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitReduce">提交审批</el-button>
          <el-button @click="reduceOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 调整计划对话框（四期） -->
    <el-dialog :title="'调整收款计划 - ' + adjustContractNo" v-model="adjustOpen" width="720px" append-to-body>
      <el-alert type="info" :closable="false" show-icon title="期次名称任何状态均可修改；比例与收款日仅未到期/待收款期次可调整，四期比例合计必须为100%" style="margin-bottom: 16px" />
      <el-table :data="adjustPlans" size="small" border>
        <el-table-column label="期次" prop="periodNo" width="60" align="center" />
        <el-table-column label="期次名称" width="140">
          <template #default="scope">
            <el-input v-model="scope.row.periodName" size="small" maxlength="30" placeholder="如：一期款/水电款" />
          </template>
        </el-table-column>
        <el-table-column label="比例(%)" width="150">
          <template #default="scope">
            <el-input-number v-model="scope.row.ratio" :precision="0" :min="0" :max="100" size="small" controls-position="right" style="width: 100%" :disabled="!canEditPlan(scope.row)" @change="calcPlanAmount(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="应收金额" prop="planAmount" width="110" align="center" />
        <el-table-column label="已收" prop="receiveAmount" width="100" align="center" />
        <el-table-column label="计划收款日" width="170">
          <template #default="scope">
            <el-date-picker v-model="scope.row.planDate" type="date" value-format="YYYY-MM-DD" size="small" style="width: 100%" :disabled="!canEditPlan(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="90" align="center">
          <template #default="scope">
            <dict-tag :options="biz_pay_period_status" :value="scope.row.status" />
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 10px; text-align: right">
        比例合计：<span :style="ratioSum === 100 ? 'color:#67c23a' : 'color:#f56c6c'">{{ ratioSum }}%</span>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAdjust">保 存</el-button>
          <el-button @click="adjustOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Payment">
import { listPlan, getPlansByContract, adjustPlans as adjustPlansApi, listRecord, registerPayment, reversePayment, reducePayment } from "@/api/biz/payment";
import { listContract, getContract } from "@/api/biz/contract";
import { getCustomer } from "@/api/biz/customer";

const { proxy } = getCurrentInstance();
const { biz_pay_period_status, biz_pay_period_name, biz_pay_type, crm_customer_source, crm_intention_level, crm_customer_status } =
  proxy.useDict("biz_pay_period_status", "biz_pay_period_name", "biz_pay_type", "crm_customer_source", "crm_intention_level", "crm_customer_status");

const activeTab = ref("plan");
const planList = ref([]);
const planLoading = ref(false);
const planTotal = ref(0);
const planQuery = ref({ pageNum: 1, pageSize: 10, customerName: null, status: null });

/** 展开行数据：{ [customerId]: { customer, contracts: [{...合同, plans: []}], loaded } } */
const expandData = ref({});

const recordList = ref([]);
const recordLoading = ref(false);
const recordTotal = ref(0);
const recordQuery = ref({ pageNum: 1, pageSize: 10, contractNo: null });

const registerOpen = ref(false);
const registerForm = ref({});
const registerRules = {
  amount: [{ required: true, message: "请输入实收金额", trigger: "blur" }],
  payType: [{ required: true, message: "请选择收款方式", trigger: "change" }],
  payTime: [{ required: true, message: "请选择到账时间", trigger: "change" }]
};

const reverseOpen = ref(false);
const reverseReason = ref("");
const reverseId = ref(null);

const reduceOpen = ref(false);
const reduceForm = ref({});
const reduceRules = {
  reduceAmount: [{ required: true, message: "请输入减免金额", trigger: "blur" }],
  reason: [{ required: true, message: "请输入减免原因", trigger: "blur" }]
};

const adjustOpen = ref(false);
const adjustPlans = ref([]);
const adjustContractNo = ref("");
const adjustContractAmount = ref(0);

const ratioSum = computed(() => adjustPlans.value.reduce((s, p) => s + Number(p.ratio || 0), 0));

function loadPlans() {
  planLoading.value = true;
  listPlan(planQuery.value).then(res => {
    planList.value = res.rows;
    planTotal.value = res.total;
    planLoading.value = false;
  });
}

/** 展开客户行：加载客户基础信息 + 各合同期次明细 */
function handleExpandChange(row, expandedRows) {
  if (expandedRows.includes(row) && !expandData.value[row.customerId]?.loaded) {
    loadExpandData(row);
  }
}

function loadExpandData(row) {
  const customerId = row.customerId;
  expandData.value[customerId] = { customer: {}, contracts: [], loaded: false };
  getCustomer(customerId).then(res => {
    expandData.value[customerId].customer = res.data || {};
  });
  listContract({ pageNum: 1, pageSize: 50, customerId: customerId }).then(res => {
    const contracts = res.rows || [];
    contracts.forEach(ct => { ct.plans = []; });
    expandData.value[customerId].contracts = contracts;
    // 逐个合同加载四期计划
    Promise.all(contracts.map(ct => getPlansByContract(ct.contractId).then(r => { ct.plans = r.data || []; })))
      .finally(() => { expandData.value[customerId].loaded = true; });
  });
}

/** 期次名称是否命中字典（未命中说明为自定义名称，按原文显示） */
function matchDict(val) {
  return biz_pay_period_name.value.some(d => d.value === val);
}

function loadRecords() {
  recordLoading.value = true;
  listRecord(recordQuery.value).then(res => {
    recordList.value = res.rows;
    recordTotal.value = res.total;
    recordLoading.value = false;
  });
}

/** 已结清/已减免/已作废的期次仅可修改名称，不可调整比例与收款日 */
function canEditPlan(plan) {
  return ['0', '1'].includes(plan.status);
}

function calcPlanAmount(plan) {
  plan.planAmount = (adjustContractAmount.value * Number(plan.ratio || 0) / 100).toFixed(2);
}

/** 操作成功后刷新已展开客户的明细 */
function refreshExpanded() {
  Object.keys(expandData.value).forEach(cid => {
    loadExpandData({ customerId: Number(cid) });
  });
}

function handleRegister(row, customerId) {
  // 客户档案已维护付款方式时预填，否则默认现金
  const payMethod = customerId ? expandData.value[customerId]?.customer?.payMethod : null;
  registerForm.value = { ...row, amount: Number(row.remainAmount), payType: payMethod || "1", payTime: null, deviationNote: null };
  registerOpen.value = true;
}

function submitRegister() {
  proxy.$refs["registerRef"].validate(valid => {
    if (valid) {
      registerPayment(registerForm.value).then(() => {
        proxy.$modal.msgSuccess("收款登记成功");
        registerOpen.value = false;
        loadPlans();
        loadRecords();
        refreshExpanded();
      });
    }
  });
}

function handleReverse(row) {
  reverseId.value = row.paymentId;
  reverseReason.value = "";
  reverseOpen.value = true;
}

function submitReverse() {
  if (!reverseReason.value) {
    proxy.$modal.msgWarning("请输入冲正原因");
    return;
  }
  reversePayment({ paymentId: reverseId.value, reverseReason: reverseReason.value }).then(() => {
    proxy.$modal.msgSuccess("冲正成功");
    reverseOpen.value = false;
    loadRecords();
    loadPlans();
    refreshExpanded();
  });
}

function handleReduce(row) {
  reduceForm.value = { planId: row.planId, reduceAmount: null, reason: null };
  reduceOpen.value = true;
}

function submitReduce() {
  proxy.$refs["reduceRef"].validate(valid => {
    if (valid) {
      reducePayment(reduceForm.value).then(() => {
        proxy.$modal.msgSuccess("减免申请已提交审批");
        reduceOpen.value = false;
        loadPlans();
        refreshExpanded();
      });
    }
  });
}

function handleAdjust(row) {
  getContract(row.contractId).then(cres => {
    adjustContractAmount.value = Number(cres.data?.contractAmount || 0);
    return getPlansByContract(row.contractId);
  }).then(res => {
    adjustPlans.value = res.data || [];
    adjustContractNo.value = row.contractNo;
    adjustOpen.value = true;
  });
}

function submitAdjust() {
  if (ratioSum.value !== 100) {
    proxy.$modal.msgWarning("四期比例合计必须为100%");
    return;
  }
  if (adjustPlans.value.some(p => !String(p.periodName || '').trim())) {
    proxy.$modal.msgWarning("期次名称不能为空");
    return;
  }
  adjustPlansApi(adjustPlans.value).then(() => {
    proxy.$modal.msgSuccess("收款计划已调整");
    adjustOpen.value = false;
    loadPlans();
    refreshExpanded();
  });
}

watch(activeTab, (tab) => {
  if (tab === "record" && recordList.value.length === 0) {
    loadRecords();
  }
});

loadPlans();
</script>

<style scoped>
.expand-wrap {
  padding: 8px 16px 16px;
  background: #fafafa;
}
</style>
