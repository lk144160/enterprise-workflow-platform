<template>
  <el-dialog :title="title" v-model="visible" width="560px" append-to-body @close="handleClose">
    <el-form ref="quoteRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="form.customerId" placeholder="请选择客户（线索）" filterable :disabled="locked" style="width: 100%">
          <el-option v-for="c in customerOptions" :key="c.customerId" :label="c.customerName + '（' + c.phone + '）'" :value="c.customerId" />
        </el-select>
      </el-form-item>
      <el-form-item label="项目名称" prop="projectName">
        <el-input v-model="form.projectName" placeholder="如：张先生家全包装修" />
      </el-form-item>
      <el-form-item label="报价金额" prop="totalAmount">
        <el-input-number v-model="form.totalAmount" :precision="2" :min="0" controls-position="right" style="width: 100%" />
      </el-form-item>
      <el-form-item label="优惠金额" prop="discountAmount">
        <el-input-number v-model="form.discountAmount" :precision="2" :min="0" :max="Number(form.totalAmount || 0)" controls-position="right" style="width: 100%" />
      </el-form-item>
      <el-form-item label="最终报价">
        <span style="color: #f56c6c; font-weight: bold">￥{{ finalAmount }}</span>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" maxlength="500" show-word-limit />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="visible = false">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="QuoteFormDialog">
import { getQuote, addQuote, updateQuote } from "@/api/biz/quote";
import { listCustomer } from "@/api/biz/customer";

const { proxy } = getCurrentInstance();

const visible = ref(false);
const title = ref("");
const locked = ref(false);
const customerOptions = ref([]);
const form = ref({});

const rules = {
  customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
  projectName: [{ required: true, message: "项目名称不能为空", trigger: "blur" }],
  totalAmount: [{ required: true, message: "报价金额不能为空", trigger: "blur" }]
};

const finalAmount = computed(() => {
  return Math.max(0, Number(form.value.totalAmount || 0) - Number(form.value.discountAmount || 0)).toFixed(2);
});

const emit = defineEmits(["success"]);

/** 打开弹窗：presetCustomer 传入时锁定客户 */
function open(row, presetCustomer) {
  form.value = { quoteId: null, customerId: null, projectName: null, totalAmount: 0, discountAmount: 0, remark: null };
  locked.value = false;
  if (row && row.quoteId) {
    // 修改
    getQuote(row.quoteId).then(res => {
      form.value = { ...res.data };
      locked.value = !!presetCustomer;
      title.value = "修改报价单";
      visible.value = true;
    });
  } else {
    // 新增
    if (presetCustomer) {
      form.value.customerId = presetCustomer.customerId;
      locked.value = true;
    }
    title.value = "新增报价单";
    visible.value = true;
  }
}

function handleClose() {
  proxy.resetForm("quoteRef");
}

function submitForm() {
  proxy.$refs["quoteRef"].validate(valid => {
    if (valid) {
      if (Number(form.value.totalAmount || 0) <= 0) {
        proxy.$modal.msgWarning("报价金额必须大于0");
        return;
      }
      const data = { ...form.value, finalAmount: finalAmount.value };
      if (data.quoteId != null) {
        updateQuote(data).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          visible.value = false;
          emit("success");
        });
      } else {
        addQuote(data).then(() => {
          proxy.$modal.msgSuccess("新增成功");
          visible.value = false;
          emit("success");
        });
      }
    }
  });
}

function getCustomerOptions() {
  listCustomer({ pageNum: 1, pageSize: 500 }).then(res => {
    customerOptions.value = res.rows || [];
  });
}

getCustomerOptions();

defineExpose({ open });
</script>
