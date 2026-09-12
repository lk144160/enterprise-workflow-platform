<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="70px">
      <el-form-item label="提醒标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入提醒标题" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="重复规则" prop="repeatType">
        <el-select v-model="queryParams.repeatType" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="d in repeat_options" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="d in status_options" :key="d.value" :label="d.label" :value="d.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:remind:add']">新增提醒</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="remindList">
      <el-table-column label="提醒标题" prop="title" min-width="160" show-overflow-tooltip />
      <el-table-column label="提醒内容" prop="content" min-width="180" show-overflow-tooltip />
      <el-table-column label="触发时间" prop="remindTime" width="160" align="center">
        <template #default="scope">
          <span :class="{ 'text-danger': scope.row.status === '0' && isOverdue(scope.row) }">{{ scope.row.remindTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="重复规则" prop="repeatType" width="90" align="center">
        <template #default="scope">
          <dict-tag :options="repeat_options" :value="scope.row.repeatType" />
        </template>
      </el-table-column>
      <el-table-column label="关联业务" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <el-tag v-if="scope.row.relateType" size="small" :type="relateTag(scope.row.relateType)">{{ relateLabel(scope.row.relateType) }}</el-tag>
          <span v-if="scope.row.relateName" style="margin-left: 6px">{{ scope.row.relateName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="接收人" prop="receiverName" width="100" align="center" />
      <el-table-column label="状态" prop="status" width="90" align="center">
        <template #default="scope">
          <dict-tag :options="status_options" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" v-if="scope.row.status === '0'" @click="handleUpdate(scope.row)" v-hasPermi="['biz:remind:edit']">修改</el-button>
          <el-button link type="warning" icon="CircleClose" v-if="scope.row.status === '0'" @click="handleCancel(scope.row)" v-hasPermi="['biz:remind:cancel']">取消</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['biz:remind:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize"
      @pagination="getList" />

    <remind-form ref="remindFormRef" @ok="getList" />
  </div>
</template>

<script setup name="Remind">
import { listRemind, cancelRemind, delRemind } from '@/api/biz/remind'
import RemindForm from '@/components/RemindForm'

const { proxy } = getCurrentInstance()
const { biz_remind_repeat: repeat_options, biz_remind_status: status_options } = proxy.useDict('biz_remind_repeat', 'biz_remind_status')

const remindList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)

const data = reactive({
  queryParams: { pageNum: 1, pageSize: 10, title: undefined, repeatType: undefined, status: undefined }
})
const { queryParams } = toRefs(data)

const RELATE_LABELS = { customer: '客户', quote: '报价单', contract: '合同' }

function getList() {
  loading.value = true
  listRemind(queryParams.value).then(res => {
    remindList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function isOverdue(row) {
  return row.remindTime && new Date(row.remindTime.replace(/-/g, '/')).getTime() <= Date.now()
}

function relateLabel(type) {
  return RELATE_LABELS[type] || type
}

function relateTag(type) {
  return { customer: 'primary', quote: 'success', contract: 'warning' }[type] || 'info'
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleAdd() {
  proxy.$refs['remindFormRef'].open()
}

function handleUpdate(row) {
  proxy.$refs['remindFormRef'].open(row)
}

function handleCancel(row) {
  proxy.$modal.confirm('确定取消提醒【' + row.title + '】吗？取消后将不再触发。').then(() => {
    return cancelRemind(row.remindId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('取消成功')
  }).catch(() => {})
}

function handleDelete(row) {
  proxy.$modal.confirm('确定删除提醒【' + row.title + '】吗？').then(() => {
    return delRemind(row.remindId)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

getList()
</script>
