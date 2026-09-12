<template>
  <el-dialog :title="form.remindId ? '修改提醒' : '新增提醒'" v-model="visible" width="560px" append-to-body @close="handleClose">
    <el-form ref="remindRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="提醒标题" prop="title">
        <el-input v-model="form.title" placeholder="如：回访张先生 / 周五前确认报价" maxlength="100" show-word-limit />
      </el-form-item>
      <el-form-item label="提醒时间" prop="remindTime">
        <el-date-picker v-model="form.remindTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="选择触发时间" style="width: 100%" :disabled-date="disabledDate" />
      </el-form-item>
      <el-form-item label="重复规则" prop="repeatType">
        <el-radio-group v-model="form.repeatType">
          <el-radio v-for="d in repeat_options" :key="d.value" :value="d.value">{{ d.label }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="接收人" prop="receiverId">
        <el-select v-model="form.receiverId" filterable placeholder="选择接收人（默认自己）" style="width: 100%">
          <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName + '（' + u.userName + '）'" :value="u.userId" />
        </el-select>
      </el-form-item>
      <el-form-item label="关联业务">
        <el-col :span="11">
          <el-select v-model="form.relateType" placeholder="可选" clearable style="width: 100%" @change="handleRelateTypeChange">
            <el-option label="客户" value="customer" />
            <el-option label="报价单" value="quote" />
            <el-option label="合同" value="contract" />
          </el-select>
        </el-col>
        <el-col :span="12" :offset="1">
          <el-select v-if="form.relateType" v-model="form.relateId" filterable placeholder="选择关联记录" style="width: 100%" @change="handleRelateChange">
            <el-option v-for="o in relateOptions" :key="o.id" :label="o.label" :value="o.id" />
          </el-select>
        </el-col>
      </el-form-item>
      <el-form-item label="提醒内容">
        <el-input v-model="form.content" type="textarea" :rows="3" placeholder="提醒的详细说明（可选）" maxlength="500" show-word-limit />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="RemindForm">
import { listUser } from '@/api/system/user'
import { listCustomer } from '@/api/biz/customer'
import { listQuote } from '@/api/biz/quote'
import { listContract } from '@/api/biz/contract'
import { addRemind, updateRemind } from '@/api/biz/remind'
import useUserStore from '@/store/modules/user'

const { proxy } = getCurrentInstance()
const { biz_remind_repeat: repeat_options } = proxy.useDict('biz_remind_repeat')

const visible = ref(false)
const userOptions = ref([])
const relateOptions = ref([])
const userStore = useUserStore()

const data = reactive({
  form: {},
  rules: {
    title: [{ required: true, message: '提醒标题不能为空', trigger: 'blur' }],
    remindTime: [{ required: true, message: '请选择提醒时间', trigger: 'change' }]
  }
})
const { form, rules } = toRefs(data)

/** 打开弹窗：row 有 remindId 为修改，prefill 为预填（如 {relateType, relateId, relateName}） */
function open(row, prefill) {
  visible.value = true
  reset()
  loadUsers()
  if (row && row.remindId) {
    form.value = { ...row }
  } else {
    form.value = {
      remindId: undefined,
      title: '',
      content: '',
      remindTime: '',
      repeatType: '0',
      receiverId: userStore.id,
      relateType: prefill ? prefill.relateType : undefined,
      relateId: prefill ? prefill.relateId : undefined,
      relateName: prefill ? prefill.relateName : undefined
    }
    if (prefill && prefill.relateType) {
      loadRelateOptions(prefill.relateType)
    }
  }
  nextTick(() => {
    if (row && row.remindId && row.relateType) {
      loadRelateOptions(row.relateType)
    }
  })
}

function reset() {
  form.value = { receiverId: userStore.id, repeatType: '0' }
  relateOptions.value = []
  proxy.resetForm('remindRef')
}

function handleClose() {
  visible.value = false
}

function cancel() {
  visible.value = false
}

/** 仅允许选择今天及以后的日期 */
function disabledDate(date) {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return date.getTime() < today.getTime()
}

function loadUsers() {
  listUser({ pageNum: 1, pageSize: 500, status: '0' }).then(res => {
    userOptions.value = res.rows || []
  })
}

function handleRelateTypeChange(val) {
  form.value.relateId = undefined
  form.value.relateName = undefined
  if (val) {
    loadRelateOptions(val)
  } else {
    relateOptions.value = []
  }
}

function loadRelateOptions(type) {
  if (type === 'customer') {
    listCustomer({ pageNum: 1, pageSize: 500 }).then(res => {
      relateOptions.value = (res.rows || []).map(c => ({ id: c.customerId, label: c.customerName + (c.estate ? '（' + c.estate + '）' : ''), name: c.customerName }))
    })
  } else if (type === 'quote') {
    listQuote({ pageNum: 1, pageSize: 500 }).then(res => {
      relateOptions.value = (res.rows || []).map(q => ({ id: q.quoteId, label: q.projectName + '（' + q.customerName + '）', name: q.projectName }))
    })
  } else if (type === 'contract') {
    listContract({ pageNum: 1, pageSize: 500 }).then(res => {
      relateOptions.value = (res.rows || []).map(c => ({ id: c.contractId, label: c.customerName + '（￥' + c.contractAmount + ' ' + (c.estate || '') + '）', name: c.customerName }))
    })
  }
}

function handleRelateChange(id) {
  const opt = relateOptions.value.find(o => o.id === id)
  form.value.relateName = opt ? opt.name : undefined
}

function submitForm() {
  proxy.$refs['remindRef'].validate(valid => {
    if (!valid) return
    if (form.value.relateType && !form.value.relateId) {
      proxy.$modal.msgWarning('请选择关联的业务记录，或清空关联业务')
      return
    }
    if (!form.value.relateType) {
      form.value.relateId = undefined
      form.value.relateName = undefined
    }
    if (form.value.remindId) {
      updateRemind(form.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        visible.value = false
        emit('ok')
      })
    } else {
      addRemind(form.value).then(() => {
        proxy.$modal.msgSuccess('新增成功')
        visible.value = false
        emit('ok')
      })
    }
  })
}

defineExpose({ open })
const emit = defineEmits(['ok'])
</script>
