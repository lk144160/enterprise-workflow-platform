<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="物资名称" prop="materialName">
        <el-input v-model="queryParams.materialName" placeholder="请输入物资名称" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="物资分类" clearable style="width: 120px">
          <el-option v-for="dict in biz_material_category" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 100px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['biz:material:add']">新增物资</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="materialList">
      <el-table-column label="物资名称" align="center" prop="materialName" :show-overflow-tooltip="true" />
      <el-table-column label="规格型号" align="center" prop="spec" :show-overflow-tooltip="true" />
      <el-table-column label="分类" align="center" prop="category" width="90">
        <template #default="scope">
          <dict-tag :options="biz_material_category" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="单位" align="center" prop="unit" width="70">
        <template #default="scope">
          <dict-tag :options="biz_measure_unit" :value="scope.row.unit" />
        </template>
      </el-table-column>
      <el-table-column label="安全库存" align="center" prop="safetyStock" width="90" />
      <el-table-column label="当前库存" align="center" prop="quantity" width="90">
        <template #default="scope">
          <span :style="isLow(scope.row) ? 'color:#f56c6c;font-weight:bold' : ''">{{ scope.row.quantity || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="参考单价" align="center" prop="refPrice" width="90" />
      <el-table-column label="库存预警" align="center" width="90">
        <template #default="scope">
          <el-tag v-if="isLow(scope.row)" type="danger" size="small">低于安全库存</el-tag>
          <el-tag v-else type="success" size="small">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['biz:material:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['biz:material:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改 -->
    <el-dialog :title="title" v-model="open" width="560px" append-to-body>
      <el-form ref="materialRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="物资名称" prop="materialName">
              <el-input v-model="form.materialName" placeholder="请输入物资名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号" prop="spec">
              <el-input v-model="form.spec" placeholder="如：25kg/袋" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类">
                <el-option v-for="dict in biz_material_category" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计量单位" prop="unit">
              <el-select v-model="form.unit" placeholder="请选择单位">
                <el-option v-for="dict in biz_measure_unit" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="安全库存" prop="safetyStock">
              <el-input-number v-model="form.safetyStock" :precision="2" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="参考单价" prop="refPrice">
              <el-input-number v-model="form.refPrice" :precision="2" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Material">
import { listMaterial, getMaterial, addMaterial, updateMaterial, delMaterial } from "@/api/biz/material";

const { proxy } = getCurrentInstance();
const { biz_material_category, biz_measure_unit } = proxy.useDict("biz_material_category", "biz_measure_unit");

const materialList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const title = ref("");

const queryParams = ref({ pageNum: 1, pageSize: 10, materialNo: null, materialName: null, category: null, status: null });

const rules = {
  materialName: [{ required: true, message: "物资名称不能为空", trigger: "blur" }],
  category: [{ required: true, message: "请选择分类", trigger: "change" }],
  unit: [{ required: true, message: "请选择计量单位", trigger: "change" }]
};

const form = ref({});

function isLow(row) {
  return Number(row.quantity || 0) <= Number(row.safetyStock || 0);
}

function getList() {
  loading.value = true;
  listMaterial(queryParams.value).then(res => {
    materialList.value = res.rows;
    total.value = res.total;
    loading.value = false;
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
  form.value = { materialId: null, materialName: null, spec: null, category: null, unit: null, safetyStock: 0, refPrice: 0, status: "0" };
  proxy.resetForm("materialRef");
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增物资";
}

function handleUpdate(row) {
  reset();
  getMaterial(row.materialId).then(res => {
    form.value = res.data;
    open.value = true;
    title.value = "修改物资";
  });
}

function handleDelete(row) {
  proxy.$modal.confirm('确认删除物资"' + row.materialName + '"？有库存结余的物资不可删除。').then(() => {
    return delMaterial(row.materialId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

function submitForm() {
  proxy.$refs["materialRef"].validate(valid => {
    if (valid) {
      if (form.value.materialId != null) {
        updateMaterial(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addMaterial(form.value).then(() => {
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

getList();
</script>
