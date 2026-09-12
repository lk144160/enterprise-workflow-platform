<template>
  <div>
    <!-- 单据详情（按业务类型渲染完整信息） -->
    <template v-if="biz">
      <el-divider content-position="left">单据详情</el-divider>

      <!-- 报价单 -->
      <el-descriptions v-if="detail.bizType === 'quote'" :column="2" border size="small">
        <el-descriptions-item label="报价单号">{{ biz.quoteNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ biz.projectName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报价金额">{{ money(biz.totalAmount) }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">{{ money(biz.discountAmount) }}</el-descriptions-item>
        <el-descriptions-item label="最终报价">{{ money(biz.finalAmount) }}</el-descriptions-item>
        <el-descriptions-item label="版本">V{{ biz.version || 1 }}</el-descriptions-item>
        <el-descriptions-item label="状态"><dict-tag :options="biz_audit_status" :value="biz.status" /></el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ parseTime(biz.submitTime) || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 合同 -->
      <el-descriptions v-else-if="detail.bizType === 'contract'" :column="2" border size="small">
        <el-descriptions-item label="合同号">{{ biz.contractNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ biz.customerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同金额">{{ money(biz.contractAmount) }}</el-descriptions-item>
        <el-descriptions-item label="已付金额">{{ money(biz.paidAmount) }}</el-descriptions-item>
        <el-descriptions-item label="签约日期">{{ parseTime(biz.signDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态"><dict-tag :options="biz_contract_status" :value="biz.status" /></el-descriptions-item>
        <el-descriptions-item label="开工日期">{{ parseTime(biz.startDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="竣工日期">{{ parseTime(biz.endDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item v-if="biz.amountDiffNote" label="差额说明" :span="2">{{ biz.amountDiffNote }}</el-descriptions-item>
      </el-descriptions>

      <!-- 收款减免（期次） -->
      <el-descriptions v-else-if="detail.bizType === 'payment_reduce'" :column="2" border size="small">
        <el-descriptions-item label="合同号">{{ biz.contractNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ biz.customerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="期次名称">{{ biz.periodName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="第几期">第 {{ biz.periodNo || '-' }} 期</el-descriptions-item>
        <el-descriptions-item label="应收金额">{{ money(biz.planAmount) }}</el-descriptions-item>
        <el-descriptions-item label="已收金额">{{ money(biz.receiveAmount) }}</el-descriptions-item>
        <el-descriptions-item label="已减免金额">{{ money(biz.reduceAmount) }}</el-descriptions-item>
        <el-descriptions-item label="剩余应收">{{ remainAmount() }}</el-descriptions-item>
        <el-descriptions-item label="应收日期">{{ parseTime(biz.dueTime, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态"><dict-tag :options="biz_pay_period_status" :value="biz.status" /></el-descriptions-item>
      </el-descriptions>

      <!-- 交底 -->
      <el-descriptions v-else-if="detail.bizType === 'disclosure'" :column="2" border size="small">
        <el-descriptions-item label="交底单号">{{ biz.disclosureNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同号">{{ biz.contractNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ biz.customerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同金额">{{ money(biz.contractAmount) }}</el-descriptions-item>
        <el-descriptions-item label="会议日期">{{ parseTime(biz.meetingDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态"><dict-tag :options="biz_audit_status" :value="biz.status" /></el-descriptions-item>
        <el-descriptions-item label="参会人员">{{ biz.attendees || '-' }}</el-descriptions-item>
        <el-descriptions-item label="外部参会">{{ biz.externalAttendees || '-' }}</el-descriptions-item>
        <el-descriptions-item label="交底内容" :span="2">{{ biz.content || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 报销 -->
      <el-descriptions v-else-if="detail.bizType === 'expense'" :column="2" border size="small">
        <el-descriptions-item label="报销编号">{{ biz.expenseNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="费用类型"><dict-tag :options="biz_expense_type" :value="biz.expenseType" /></el-descriptions-item>
        <el-descriptions-item label="报销金额">{{ money(biz.amount) }}</el-descriptions-item>
        <el-descriptions-item label="费用日期">{{ parseTime(biz.expenseDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ biz.applicantName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ biz.deptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发票张数">{{ biz.invoiceCount || 0 }} 张</el-descriptions-item>
        <el-descriptions-item label="状态"><dict-tag :options="biz_expense_status" :value="biz.status" /></el-descriptions-item>
        <el-descriptions-item label="费用说明" :span="2">{{ biz.description || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 定金退还 -->
      <el-descriptions v-else-if="detail.bizType === 'deposit_refund'" :column="2" border size="small">
        <el-descriptions-item label="定金编号">{{ biz.depositNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ biz.customerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="楼盘">{{ biz.estate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="定金金额">{{ money(biz.amount) }}</el-descriptions-item>
        <el-descriptions-item label="收款方式"><dict-tag :options="biz_pay_type" :value="biz.payType" /></el-descriptions-item>
        <el-descriptions-item label="收款时间">{{ parseTime(biz.payTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="关联报价">{{ biz.quoteNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前状态"><dict-tag :options="biz_deposit_status" :value="biz.status" /></el-descriptions-item>
        <el-descriptions-item label="退还原因" :span="2">{{ biz.refundReason || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 出库单 -->
      <template v-else-if="detail.bizType === 'stock_out'">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="出库单号">{{ biz.orderNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="单据类型"><dict-tag :options="biz_stock_order_type" :value="biz.orderType" /></el-descriptions-item>
          <el-descriptions-item label="来源类型"><dict-tag :options="biz_stock_source" :value="biz.sourceType" /></el-descriptions-item>
          <el-descriptions-item label="总金额">{{ money(biz.totalAmount) }}</el-descriptions-item>
          <el-descriptions-item label="关联合同">{{ biz.contractNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="客户">{{ biz.customerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ biz.applicantName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="经办人">{{ biz.handlerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ parseTime(biz.applyTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态"><dict-tag :options="biz_stock_status" :value="biz.status" /></el-descriptions-item>
        </el-descriptions>
        <template v-if="biz.items && biz.items.length">
          <div style="margin: 8px 0 4px; font-weight: bold; font-size: 13px">出库明细</div>
          <el-table :data="biz.items" size="small">
            <el-table-column label="物资名称" align="center" prop="materialName" :show-overflow-tooltip="true" />
            <el-table-column label="规格" align="center" prop="spec" :show-overflow-tooltip="true" />
            <el-table-column label="单位" align="center" prop="unit" width="60" />
            <el-table-column label="数量" align="center" prop="quantity" width="80" />
            <el-table-column label="单价" align="center" prop="unitPrice" width="90" />
            <el-table-column label="金额" align="center" prop="amount" width="100" />
          </el-table>
        </template>
      </template>
    </template>

    <!-- 图片附件 -->
    <template v-if="attachments.length">
      <el-divider content-position="left">图片附件（点击放大）</el-divider>
      <div class="att-list">
        <div v-for="(att, index) in attachments" :key="att.attachmentId" class="att-item">
          <el-image
            v-if="isImage(att)"
            :src="fullUrl(att.fileUrl)"
            :preview-src-list="attachments.filter(isImage).map(a => fullUrl(a.fileUrl))"
            :initial-index="imageIndex(att)"
            fit="cover"
            preview-teleported
            class="att-img"
          >
            <template #error>
              <div class="att-err"><el-icon><picture-filled /></el-icon></div>
            </template>
          </el-image>
          <a v-else :href="fullUrl(att.fileUrl)" target="_blank" class="att-file">
            <el-icon><document /></el-icon>
            <span>{{ att.fileName || '附件' }}</span>
          </a>
          <div class="att-name" :title="att.fileName">{{ att.fileName }}</div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
const { proxy } = getCurrentInstance();
const { biz_audit_status, biz_contract_status, biz_pay_period_status, biz_expense_type, biz_expense_status, biz_stock_order_type, biz_stock_source, biz_stock_status, biz_pay_type, biz_deposit_status } =
  proxy.useDict("biz_audit_status", "biz_contract_status", "biz_pay_period_status", "biz_expense_type", "biz_expense_status", "biz_stock_order_type", "biz_stock_source", "biz_stock_status", "biz_pay_type", "biz_deposit_status");

const props = defineProps({
  /** 审批实例（含 bizType/bizDetail/attachments） */
  detail: {
    type: Object,
    default: () => ({})
  }
});

const biz = computed(() => props.detail.bizDetail || null);
const attachments = computed(() => (props.detail.attachments || []).filter(a => a && a.fileUrl));

function money(v) {
  return (v === null || v === undefined || v === "") ? "-" : "￥" + v;
}

/** 期次剩余应收 = 应收 - 已收 - 已减免 */
function remainAmount() {
  const b = biz.value;
  if (!b || b.planAmount === null || b.planAmount === undefined) return "-";
  const num = (v) => Number(v || 0);
  return "￥" + (num(b.planAmount) - num(b.receiveAmount) - num(b.reduceAmount)).toFixed(2);
}

function isImage(att) {
  return /\.(jpe?g|png|gif|bmp|webp)$/i.test(att.fileUrl || "");
}

function fullUrl(url) {
  if (!url) return "";
  if (/^https?:\/\//i.test(url)) return url;
  return import.meta.env.VITE_APP_BASE_API + url;
}

/** 当前附件在图片列表中的预览起始位置 */
function imageIndex(att) {
  const imgs = attachments.value.filter(isImage);
  return imgs.findIndex(a => a.attachmentId === att.attachmentId);
}
</script>

<style scoped>
.att-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.att-item {
  width: 96px;
  text-align: center;
}
.att-img {
  width: 96px;
  height: 96px;
  border-radius: 4px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
}
.att-err {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: #c0c4cc;
  font-size: 28px;
}
.att-file {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 96px;
  height: 96px;
  border-radius: 4px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  color: #409eff;
  text-decoration: none;
  font-size: 12px;
  gap: 4px;
}
.att-name {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
