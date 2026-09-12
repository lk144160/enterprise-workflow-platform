<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="客户姓名" prop="customerName">
        <el-input v-model="queryParams.customerName" placeholder="请输入客户姓名" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="跟进方式" prop="followType">
        <el-select v-model="queryParams.followType" placeholder="跟进方式" clearable style="width: 130px">
          <el-option v-for="dict in crm_follow_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="跟进时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recordList">
      <el-table-column label="客户姓名" align="center" prop="customerName" width="110" />
      <el-table-column label="手机号" align="center" prop="phone" width="120" />
      <el-table-column label="跟进方式" align="center" width="100">
        <template #default="scope">
          <dict-tag :options="crm_follow_type" :value="scope.row.followType" />
        </template>
      </el-table-column>
      <el-table-column label="跟进内容" align="center" prop="content" :show-overflow-tooltip="true" />
      <el-table-column label="跟进时间" align="center" width="160">
        <template #default="scope">{{ parseTime(scope.row.followTime) }}</template>
      </el-table-column>
      <el-table-column label="下次跟进" align="center" width="160">
        <template #default="scope">{{ parseTime(scope.row.nextFollowTime) || "-" }}</template>
      </el-table-column>
      <el-table-column label="跟进人" align="center" prop="createByName" width="100" />
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="Follow">
import { listFollowRecord } from "@/api/biz/customer";

const { proxy } = getCurrentInstance();
const { crm_follow_type } = proxy.useDict("crm_follow_type");

const recordList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const dateRange = ref([]);

const queryParams = ref({ pageNum: 1, pageSize: 10, customerName: null, followType: null });

/** 查询跟进记录列表 */
function getList() {
  loading.value = true;
  listFollowRecord(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    recordList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

getList();
</script>
