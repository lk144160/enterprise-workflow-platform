<template>
  <div class="app-container" v-loading="loading">
    <!-- 页头 -->
    <div class="detail-header">
      <el-button icon="ArrowLeft" circle plain @click="goBack" />
      <div class="header-title">
        <span class="title-text">工地详情 {{ detail.siteNo }}</span>
        <dict-tag :options="biz_site_status" :value="detail.status" v-if="detail.status" />
        <el-tag v-if="detail.lateFlag === 1" type="danger" size="small">延期中</el-tag>
      </div>
    </div>

    <el-row :gutter="16">
      <!-- 客户信息 -->
      <el-col :lg="12" :xs="24">
        <el-card shadow="never" class="info-card">
          <template #header><span class="card-title">客户信息</span></template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="客户姓名">{{ customer.customerName || detail.customerName || '—' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ customer.phone || detail.customerPhone || '—' }}</el-descriptions-item>
            <el-descriptions-item label="线索来源"><dict-tag :options="crm_customer_source" :value="customer.source" v-if="customer.source" /><span v-else>—</span></el-descriptions-item>
            <el-descriptions-item label="意向等级"><dict-tag :options="crm_intention_level" :value="customer.intentionLevel" v-if="customer.intentionLevel" /><span v-else>—</span></el-descriptions-item>
            <el-descriptions-item label="客户状态"><dict-tag :options="crm_customer_status" :value="customer.status" v-if="customer.status" /><span v-else>—</span></el-descriptions-item>
            <el-descriptions-item label="到访次数">
              <el-tag v-if="customer.visitCount > 0" type="warning" size="small" effect="plain">到访 {{ customer.visitCount }} 次</el-tag>
              <span v-else>—</span>
            </el-descriptions-item>
            <el-descriptions-item label="楼盘名称">{{ customer.estate || detail.estate || '—' }}</el-descriptions-item>
            <el-descriptions-item label="户型">{{ customer.houseType || '—' }}</el-descriptions-item>
            <el-descriptions-item label="建筑面积">{{ customer.area != null ? customer.area + ' m²' : (detail.area != null ? detail.area + ' m²' : '—') }}</el-descriptions-item>
            <el-descriptions-item label="预算区间">{{ customer.budget || '—' }}</el-descriptions-item>
            <el-descriptions-item label="负责业务员">{{ customer.ownerName || '—' }}</el-descriptions-item>
            <el-descriptions-item label="跟进设计师">{{ customer.designerName || detail.designerName || '—' }}</el-descriptions-item>
            <el-descriptions-item label="已收定金">
              <span v-if="customer.depositSum > 0" class="money">￥{{ Number(customer.depositSum).toLocaleString() }}</span>
              <span v-else>—</span>
            </el-descriptions-item>
            <el-descriptions-item label="最近跟进">{{ parseTime(customer.latestFollowTime, '{y}-{m}-{d}') || '—' }}</el-descriptions-item>
            <el-descriptions-item label="装修需求" :span="2">{{ customer.demand || '—' }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ customer.remark || detail.remark || '—' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- 工地 + 合同信息 -->
      <el-col :lg="12" :xs="24">
        <el-card shadow="never" class="info-card">
          <template #header><span class="card-title">工地信息</span></template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="工地编号">{{ detail.siteNo }}</el-descriptions-item>
            <el-descriptions-item label="工地状态"><dict-tag :options="biz_site_status" :value="detail.status" /></el-descriptions-item>
            <el-descriptions-item label="开工日期">{{ parseTime(detail.startDate, '{y}-{m}-{d}') || '—' }}</el-descriptions-item>
            <el-descriptions-item label="完工日期">{{ parseTime(detail.finishDate, '{y}-{m}-{d}') || '—' }}</el-descriptions-item>
            <el-descriptions-item label="设计师">{{ detail.designerName || '—' }}</el-descriptions-item>
            <el-descriptions-item label="监理（项目经理）">{{ detail.supervisorName || '—' }}</el-descriptions-item>
            <el-descriptions-item label="门锁密码">
              <span v-if="detail.keyPassword">
                {{ keyVisible ? detail.keyPassword : '●●●●●●' }}
                <el-icon style="cursor: pointer; vertical-align: -2px" @click="keyVisible = !keyVisible">
                  <View v-if="keyVisible" /><Hide v-else />
                </el-icon>
              </span>
              <span v-else>—</span>
            </el-descriptions-item>
            <el-descriptions-item label="合同编号">{{ detail.contractNo || '—' }}</el-descriptions-item>
            <el-descriptions-item label="合同金额"><span class="money">￥{{ Number(detail.contractAmount || 0).toLocaleString() }}</span></el-descriptions-item>
            <el-descriptions-item label="合同工期">{{ parseTime(detail.contractStartDate, '{y}-{m}-{d}') }} ~ {{ parseTime(detail.contractEndDate, '{y}-{m}-{d}') }}</el-descriptions-item>
          </el-descriptions>
          <div class="progress-block">
            <div class="progress-label">
              整体进度 {{ detail.doneStages || 0 }}/{{ detail.totalStages || 0 }}
              <span v-if="detail.currentStageType" style="margin-left: 8px">当前：<dict-tag :options="biz_stage_type" :value="detail.currentStageType" /></span>
            </div>
            <el-progress :percentage="progressOf(detail)" :stroke-width="12" :color="detail.lateFlag === 1 ? '#F56C6C' : undefined" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 施工计划 -->
    <el-card shadow="never" class="plan-card">
      <template #header>
        <div class="plan-header">
          <span class="card-title">施工计划</span>
          <el-button type="primary" plain icon="Plus" size="small" @click="handleAddStage" v-hasPermi="['biz:site:edit']">新增施工步骤</el-button>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item v-for="stage in detail.stages" :key="stage.stageId"
          :type="stage.status === '2' ? 'success' : stage.status === '1' ? 'primary' : 'info'"
          :hollow="stage.status === '0'"
          :timestamp="'计划 ' + (parseTime(stage.planStartDate, '{y}-{m}-{d}') || '未定') + ' ~ ' + (parseTime(stage.planEndDate, '{y}-{m}-{d}') || '未定')">
          <div class="stage-item">
            <div class="stage-head">
              <span class="stage-name">{{ stageTitle(stage) }}</span>
              <dict-tag :options="biz_stage_type" :value="stage.stageType" />
              <dict-tag :options="biz_stage_status" :value="stage.status" />
              <el-tag v-if="stage.lateFlag === 1 && stage.status !== '2'" type="danger" size="small">延期</el-tag>
              <span class="stage-actual" v-if="stage.status !== '0'">
                实际 {{ parseTime(stage.actualStartDate, '{y}-{m}-{d}') || '—' }}<template v-if="stage.actualEndDate"> ~ {{ parseTime(stage.actualEndDate, '{y}-{m}-{d}') }}</template>
              </span>
            </div>
            <div class="stage-remark" v-if="stage.remark">{{ stage.remark }}</div>
            <div class="stage-photos" v-if="stage.attachments && stage.attachments.length">
              <div v-for="att in stage.attachments" :key="att.attachmentId" class="photo-wrap">
                <el-image class="photo" :src="fullUrl(att.fileUrl)"
                  :preview-src-list="stage.attachments.map(a => fullUrl(a.fileUrl))"
                  :initial-index="stage.attachments.indexOf(att)"
                  :preview-teleported="true" fit="cover" hide-on-click-modal />
                <div class="photo-name" :title="att.fileName">{{ att.fileName }}</div>
                <el-button class="photo-download" size="small" type="primary" plain icon="Download" @click="downloadAttachment(att)">下载</el-button>
              </div>
            </div>
            <div class="stage-actions">
              <el-button size="small" type="primary" plain @click="handleStart(stage)" v-if="stage.status === '0'" v-hasPermi="['biz:site:advance']">开始施工</el-button>
              <el-button size="small" type="success" plain @click="handleComplete(stage)" v-if="stage.status !== '2'" v-hasPermi="['biz:site:advance']">标记完成</el-button>
              <el-button size="small" plain icon="Edit" @click="handleEditStage(stage)" v-hasPermi="['biz:site:edit']">编辑</el-button>
              <el-button size="small" plain type="danger" icon="Delete" @click="handleDeleteStage(stage)" v-if="stage.status === '0'" v-hasPermi="['biz:site:edit']">删除</el-button>
              <el-upload v-if="stage.status !== '2'" :headers="upload.headers" :action="upload.url" accept="image/*"
                :show-file-list="false" multiple :on-success="res => handleStageUpload(res, stage)" style="display: inline-block; margin-left: 10px"
                v-hasPermi="['biz:site:advance']">
                <el-button size="small" plain icon="Camera">现场照片（可多选）</el-button>
              </el-upload>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <!-- 新增施工步骤 -->
    <el-dialog title="新增施工步骤" v-model="addStageOpen" width="480px" append-to-body>
      <el-form ref="addStageRef" :model="addStageForm" :rules="addStageRules" label-width="90px">
        <el-form-item label="步骤名称" prop="stageName">
          <el-input v-model="addStageForm.stageName" placeholder="如：中央空调安装" maxlength="64" />
        </el-form-item>
        <el-form-item label="插入位置">
          <el-select v-model="addStageForm.insertBeforeStageId" placeholder="追加到末尾" clearable style="width: 100%">
            <el-option label="追加到末尾" :value="null" />
            <el-option v-for="stage in detail.stages" :key="stage.stageId" :value="stage.stageId"
              :label="'插到【' + stageTitle(stage) + '】之前'" />
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="计划开始">
              <el-date-picker v-model="addStageForm.planStartDate" type="date" value-format="YYYY-MM-DD" placeholder="可选" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划结束">
              <el-date-picker v-model="addStageForm.planEndDate" type="date" value-format="YYYY-MM-DD" placeholder="可选" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="addStageForm.remark" type="textarea" :rows="2" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitAddStage">确 定</el-button>
        <el-button @click="addStageOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 编辑施工步骤 -->
    <el-dialog :title="'编辑施工步骤 - ' + stageTitle(stageForm)" v-model="stageOpen" width="480px" append-to-body>
      <el-form ref="stageRef" :model="stageForm" label-width="90px">
        <el-form-item label="步骤名称">
          <el-input v-model="stageForm.stageName" :placeholder="stageLabel(stageForm.stageType)" maxlength="64" />
        </el-form-item>
        <el-form-item label="计划开始">
          <el-date-picker v-model="stageForm.planStartDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="计划结束">
          <el-date-picker v-model="stageForm.planEndDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实际开始">
          <el-date-picker v-model="stageForm.actualStartDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" placeholder="补录实际开始日期" />
        </el-form-item>
        <el-form-item label="实际完成">
          <el-date-picker v-model="stageForm.actualEndDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" placeholder="填写后该步骤自动转为已完成" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="stageForm.remark" type="textarea" :rows="2" placeholder="现场情况说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitStage">确 定</el-button>
        <el-button @click="stageOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="SiteDetail">
import { getSite, startStage, completeStage, updateStage, addStage, delStage } from "@/api/biz/site";
import { getCustomer } from "@/api/biz/customer";
import { addAttachment } from "@/api/biz/attachment";
import { getToken } from "@/utils/auth";
import { View, Hide } from '@element-plus/icons-vue';

const baseUrl = import.meta.env.VITE_APP_BASE_API;
const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();
const { biz_site_status, biz_stage_type, biz_stage_status, crm_customer_source, crm_intention_level, crm_customer_status } =
  proxy.useDict("biz_site_status", "biz_stage_type", "biz_stage_status", "crm_customer_source", "crm_intention_level", "crm_customer_status");

const loading = ref(false);
const detail = ref({});
const customer = ref({});
const keyVisible = ref(false);
const stageOpen = ref(false);
const stageForm = ref({});
const addStageOpen = ref(false);
const addStageForm = ref({});
const addStageRules = {
  stageName: [{ required: true, message: "请填写施工步骤名称", trigger: "blur" }]
};

const upload = {
  headers: { Authorization: "Bearer " + getToken() },
  url: baseUrl + "/common/upload"
};

onMounted(() => {
  loadDetail();
});

function loadDetail() {
  loading.value = true;
  const siteId = route.params.siteId;
  getSite(siteId).then(res => {
    detail.value = res.data || {};
    loadCustomer(detail.value.customerId);
  }).finally(() => {
    loading.value = false;
  });
}

function loadCustomer(customerId) {
  if (!customerId) return;
  getCustomer(customerId).then(res => {
    customer.value = res.data || {};
  }).catch(() => {});
}

function goBack() {
  router.push("/design/site");
}

function progressOf(row) {
  if (!row.totalStages) return 0;
  return Math.round(((row.doneStages || 0) / row.totalStages) * 100);
}

/** 步骤小标题：自定义名称优先，空则取字典标签 */
function stageTitle(stage) {
  return stage.stageName || stageLabel(stage.stageType);
}

function stageLabel(stageType) {
  const d = biz_stage_type.value.find(x => x.value === stageType);
  return d ? d.label : stageType;
}

function fullUrl(url) {
  if (!url) return "";
  if (/^https?:\/\//i.test(url)) return url;
  return baseUrl + url;
}

/** 附件下载（blob 方式，保留原文件名） */
function downloadAttachment(att) {
  fetch(fullUrl(att.fileUrl)).then(res => res.blob()).then(blob => {
    const a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = att.fileName || "现场照片";
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(a.href);
  }).catch(() => {
    proxy.$modal.msgError("下载失败，请重试");
  });
}

function handleStart(stage) {
  proxy.$modal.confirm('确认开始【' + stageTitle(stage) + '】施工？').then(() => startStage(stage.stageId)).then(() => {
    proxy.$modal.msgSuccess("已开始施工");
    loadDetail();
  }).catch(() => {});
}

function handleComplete(stage) {
  proxy.$modal.confirm('确认将【' + stageTitle(stage) + '】标记为完成？').then(() => completeStage(stage.stageId)).then(() => {
    proxy.$modal.msgSuccess("阶段已完成");
    loadDetail();
  }).catch(() => {});
}

function handleEditStage(stage) {
  stageForm.value = {
    stageId: stage.stageId,
    stageType: stage.stageType,
    stageName: stage.stageName,
    planStartDate: stage.planStartDate,
    planEndDate: stage.planEndDate,
    actualStartDate: stage.actualStartDate,
    actualEndDate: stage.actualEndDate,
    remark: stage.remark
  };
  stageOpen.value = true;
}

function submitStage() {
  const data = { ...stageForm.value };
  // 名称清空时传空串，后端以字典标签兜底展示
  if (!data.stageName) delete data.stageName;
  updateStage(data).then(() => {
    proxy.$modal.msgSuccess("施工步骤已更新");
    stageOpen.value = false;
    loadDetail();
  });
}

function handleAddStage() {
  addStageForm.value = { siteId: detail.value.siteId, stageName: null, insertBeforeStageId: null, planStartDate: null, planEndDate: null, remark: null };
  addStageOpen.value = true;
}

function submitAddStage() {
  proxy.$refs["addStageRef"].validate(valid => {
    if (!valid) return;
    addStage(addStageForm.value).then(() => {
      proxy.$modal.msgSuccess("施工步骤已新增");
      addStageOpen.value = false;
      loadDetail();
    });
  });
}

function handleDeleteStage(stage) {
  proxy.$modal.confirm('确认删除施工步骤【' + stageTitle(stage) + '】？该步骤的现场照片将一并删除。').then(() => delStage(stage.stageId)).then(() => {
    proxy.$modal.msgSuccess("施工步骤已删除");
    loadDetail();
  }).catch(() => {});
}

let refreshTimer = null;
function handleStageUpload(res, stage) {
  if (res.code === 200) {
    addAttachment({ bizType: "site_stage", bizId: stage.stageId, fileName: res.fileName, fileUrl: res.url }).then(() => {
      proxy.$modal.msgSuccess("现场照片已上传");
      // 多张连传时合并为一次刷新
      if (refreshTimer) clearTimeout(refreshTimer);
      refreshTimer = setTimeout(loadDetail, 600);
    });
  }
}
</script>

<style scoped>
.detail-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.header-title { display: flex; align-items: center; gap: 8px; }
.title-text { font-size: 18px; font-weight: bold; color: #303133; }
.info-card { margin-bottom: 16px; }
.card-title { font-weight: bold; }
.money { color: #f56c6c; font-weight: bold; }
.progress-block { margin-top: 16px; }
.progress-label { font-size: 13px; color: #606266; display: flex; align-items: center; margin-bottom: 6px; }
.plan-card { margin-bottom: 16px; }
.plan-header { display: flex; justify-content: space-between; align-items: center; }
.stage-item { padding-bottom: 4px; }
.stage-head { display: flex; gap: 6px; align-items: center; flex-wrap: wrap; }
.stage-name { font-size: 15px; font-weight: bold; color: #303133; }
.stage-actual { font-size: 12px; color: #909399; }
.stage-remark { font-size: 13px; color: #606266; margin-top: 4px; }
.stage-photos { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 8px; }
.photo-wrap { width: 96px; text-align: center; }
.photo { width: 96px; height: 96px; border-radius: 4px; border: 1px solid #ebeef5; }
.photo-name { font-size: 12px; color: #909399; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-top: 2px; }
.photo-download { width: 96px; margin-top: 4px; padding: 4px 0; }
.stage-actions { margin-top: 8px; }
</style>
