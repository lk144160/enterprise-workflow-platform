<template>
  <el-dialog :title="'报价单详情 - ' + detail.quoteNo" v-model="visible" width="560px" append-to-body>
    <el-descriptions :column="2" border size="small">
      <el-descriptions-item label="报价编号">{{ detail.quoteNo }}</el-descriptions-item>
      <el-descriptions-item label="客户">{{ detail.customerName }}</el-descriptions-item>
      <el-descriptions-item label="项目名称" :span="2">{{ detail.projectName }}</el-descriptions-item>
      <el-descriptions-item label="报价金额">￥{{ detail.totalAmount }}</el-descriptions-item>
      <el-descriptions-item label="优惠金额">￥{{ detail.discountAmount }}</el-descriptions-item>
      <el-descriptions-item label="最终报价">
        <span style="color: #f56c6c; font-weight: bold">￥{{ detail.finalAmount }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
      <el-descriptions-item label="提交时间">{{ parseTime(detail.submitTime) || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审批完成时间">{{ parseTime(detail.approveTime) || '-' }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup name="QuoteDetailDialog">
import { getQuote } from "@/api/biz/quote";

const visible = ref(false);
const detail = ref({});

function open(row) {
  getQuote(row.quoteId).then(res => {
    detail.value = res.data;
    visible.value = true;
  });
}

defineExpose({ open });
</script>
