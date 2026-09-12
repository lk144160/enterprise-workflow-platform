<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="类型" prop="orderType">
        <el-select v-model="queryParams.orderType" placeholder="单据类型" clearable style="width: 110px">
          <el-option label="入库" value="1" />
          <el-option label="出库" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="来源" prop="sourceType">
        <el-select v-model="queryParams.sourceType" placeholder="来源" clearable style="width: 120px">
          <el-option v-for="dict in biz_stock_source" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 110px">
          <el-option v-for="dict in biz_stock_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" plain icon="Download" @click="handleAdd('1')" v-hasPermi="['biz:stock:in']">入库登记</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Upload" @click="handleAdd('2')" v-hasPermi="['biz:stock:in']">出库申请</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList">
      <el-table-column label="类型" align="center" width="70">
        <template #default="scope">
          <el-tag size="small" :type="scope.row.orderType === '1' ? 'success' : 'warning'">{{ scope.row.orderType === '1' ? '入库' : '出库' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="来源" align="center" prop="sourceType" width="100">
        <template #default="scope">
          <dict-tag :options="biz_stock_source" :value="scope.row.sourceType" />
        </template>
      </el-table-column>
      <el-table-column label="关联客户" align="center" prop="customerName" width="140">
        <template #default="scope">{{ scope.row.customerName || '-' }}</template>
      </el-table-column>
      <el-table-column label="明细数" align="center" prop="itemCount" width="80" />
      <el-table-column label="金额合计" align="center" prop="totalAmount" width="110" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <dict-tag :options="biz_stock_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="申请人" align="center" prop="createBy" width="100" />
      <el-table-column label="申请时间" align="center" prop="createTime" width="160">
        <template #default="scope">{{ parseTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="210" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['biz:stock:query']">详情</el-button>
          <el-button v-if="['0','3'].includes(scope.row.status)" link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['biz:stock:in']">修改</el-button>
          <el-button v-if="['0','3'].includes(scope.row.status)" link type="success" icon="Promotion" @click="handleSubmit(scope.row)" v-hasPermi="['biz:stock:apply']">提交</el-button>
          <el-button v-if="scope.row.status === '0'" link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['biz:stock:in']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改出入库单 -->
    <el-dialog :title="dialogTitle" v-model="open" width="900px" append-to-body>
      <el-form ref="orderRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="单据类型">
              <el-tag :type="form.orderType === '1' ? 'success' : 'warning'">{{ form.orderType === '1' ? '入库' : '出库' }}</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="来源" prop="sourceType">
              <el-select v-model="form.sourceType" placeholder="请选择来源">
                <el-option v-for="dict in biz_stock_source" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item v-if="form.orderType === '2' && form.sourceType === '2'" label="关联合同" prop="contractId">
              <el-select v-model="form.contractId" placeholder="项目领用必选合同" filterable style="width: 100%">
                <el-option v-for="c in contractOptions" :key="c.contractId" :label="(c.customerName || '') + (c.estate ? '（' + c.estate + '）' : '')" :value="c.contractId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider content-position="left">物资明细</el-divider>
      <el-row class="mb8">
        <el-button type="primary" plain icon="Plus" size="small" @click="addItem">添加物资行</el-button>
      </el-row>
      <el-table :data="form.items" size="small" border>
        <el-table-column label="物资" min-width="200">
          <template #default="scope">
            <el-select v-model="scope.row.materialId" size="small" filterable placeholder="请选择物资" style="width: 100%">
              <el-option v-for="m in materialOptions" :key="m.materialId" :label="m.materialName + '（库存' + (m.quantity || 0) + '）'" :value="m.materialId" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="140">
          <template #default="scope">
            <el-input-number v-model="scope.row.quantity" :precision="2" :min="0.01" size="small" controls-position="right" style="width: 100%" @change="calcRow(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column v-if="form.orderType === '1'" label="单价" width="140">
          <template #default="scope">
            <el-input-number v-model="scope.row.unitPrice" :precision="2" :min="0" size="small" controls-position="right" style="width: 100%" @change="calcRow(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="金额" width="110" align="center">
          <template #default="scope">{{ scope.row.amount }}</template>
        </el-table-column>
        <el-table-column label="备注" min-width="120">
          <template #default="scope">
            <el-input v-model="scope.row.remark" size="small" placeholder="备注" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60" align="center">
          <template #default="scope">
            <el-button link type="danger" icon="Delete" size="small" @click="removeItem(scope.$index)" />
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情抽屉 -->
    <el-drawer v-model="detailOpen" :title="'单据详情 - ' + detail.orderNo" size="640px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="单据编号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ detail.orderType === '1' ? '入库' : '出库' }}</el-descriptions-item>
        <el-descriptions-item label="来源">{{ detail.sourceType }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
        <el-descriptions-item label="关联合同">{{ detail.contractNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detail.createBy }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail.items || []" size="small" border style="margin-top: 12px">
        <el-table-column label="物资名称" prop="materialName" />
        <el-table-column label="单位" prop="unit" width="60" align="center" />
        <el-table-column label="数量" prop="quantity" width="80" align="center" />
        <el-table-column label="单价" prop="unitPrice" width="90" align="center" />
        <el-table-column label="金额" prop="amount" width="100" align="center" />
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup name="StockOrder">
import { listStockOrder, getStockOrder, addStockOrder, updateStockOrder, delStockOrder, submitStockOrder } from "@/api/biz/stock";
import { listMaterial } from "@/api/biz/material";
import { listContract } from "@/api/biz/contract";

const { proxy } = getCurrentInstance();
const { biz_stock_source, biz_stock_status } = proxy.useDict("biz_stock_source", "biz_stock_status");

const orderList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const detailOpen = ref(false);
const materialOptions = ref([]);
const contractOptions = ref([]);
const detail = ref({});

const queryParams = ref({ pageNum: 1, pageSize: 10, orderNo: null, orderType: null, sourceType: null, status: null });

const rules = {
  sourceType: [{ required: true, message: "请选择来源", trigger: "change" }],
  contractId: [{ required: true, message: "项目领用必须关联合同", trigger: "change" }]
};

const form = ref({ items: [] });

const dialogTitle = computed(() => form.value.orderType === "1" ? "入库登记" : "出库申请");

function getList() {
  loading.value = true;
  listStockOrder(queryParams.value).then(res => {
    orderList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function getOptions() {
  listMaterial({ pageNum: 1, pageSize: 500, status: "0" }).then(res => {
    materialOptions.value = res.rows || [];
  });
  listContract({ pageNum: 1, pageSize: 500, status: "2" }).then(res => {
    contractOptions.value = res.rows || [];
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
  form.value = { orderId: null, orderType: "1", sourceType: null, contractId: null, items: [] };
  proxy.resetForm("orderRef");
}

function handleAdd(orderType) {
  reset();
  form.value.orderType = orderType;
  form.value.sourceType = orderType === "1" ? "1" : "2";
  open.value = true;
}

function handleUpdate(row) {
  reset();
  getStockOrder(row.orderId).then(res => {
    form.value = res.data;
    form.value.items = res.data.items || [];
    open.value = true;
  });
}

function handleDetail(row) {
  getStockOrder(row.orderId).then(res => {
    detail.value = res.data;
    detailOpen.value = true;
  });
}

function addItem() {
  form.value.items.push({ materialId: null, quantity: 1, unitPrice: null, amount: null, remark: null });
}

function removeItem(index) {
  form.value.items.splice(index, 1);
}

function calcRow(row) {
  row.amount = (Number(row.quantity || 0) * Number(row.unitPrice || 0)).toFixed(2);
}

function submitForm() {
  proxy.$refs["orderRef"].validate(valid => {
    if (valid) {
      if (!form.value.items || form.value.items.length === 0) {
        proxy.$modal.msgWarning("请至少添加一条物资明细");
        return;
      }
      if (form.value.items.some(i => !i.materialId)) {
        proxy.$modal.msgWarning("请选择所有明细行的物资");
        return;
      }
      if (form.value.orderId != null) {
        updateStockOrder(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addStockOrder(form.value).then(() => {
          proxy.$modal.msgSuccess("保存成功（草稿）");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleSubmit(row) {
  const tip = row.orderType === "1" ? "提交后直接完成入库并更新库存" : "提交后进入出库审批流程";
  proxy.$modal.confirm('确认提交该' + (row.orderType === '1' ? '入库' : '出库') + '单？' + tip + "。").then(() => {
    return submitStockOrder(row.orderId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("提交成功");
  }).catch(() => {});
}

function handleDelete(row) {
  proxy.$modal.confirm('确认删除该' + (row.orderType === '1' ? '入库' : '出库') + '草稿单？').then(() => {
    return delStockOrder(row.orderId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

function cancel() {
  open.value = false;
  reset();
}

getOptions();
getList();
</script>
