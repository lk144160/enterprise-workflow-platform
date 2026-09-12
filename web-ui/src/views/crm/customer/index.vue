<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="客户姓名" prop="customerName">
        <el-input v-model="queryParams.customerName" placeholder="请输入客户姓名" clearable style="width: 180px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="楼盘" prop="estate">
        <el-input v-model="queryParams.estate" placeholder="请输入楼盘名称" clearable style="width: 160px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="线索状态" clearable style="width: 140px">
          <el-option v-for="dict in crm_customer_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="业务员" prop="ownerName">
        <el-input v-model="queryParams.ownerName" placeholder="请输入业务员姓名" clearable style="width: 140px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="负责设计师" prop="designerId">
        <el-select v-model="queryParams.designerId" placeholder="请选择负责设计师" clearable filterable style="width: 150px">
          <el-option v-for="u in designerOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
        </el-select>
      </el-form-item>
      <el-form-item label="家装顾问" prop="advisorId">
        <el-select v-model="queryParams.advisorId" placeholder="请选择家装顾问" clearable filterable style="width: 150px">
          <el-option v-for="u in advisorOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['crm:customer:add']">新增</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="customerList">
      <el-table-column label="客户姓名" align="center" prop="customerName" width="120">
        <template #default="scope">
          <div>{{ scope.row.customerName }}</div>
          <el-tag v-if="scope.row.visitCount > 0" type="warning" size="small" effect="plain" style="margin-top: 2px">到访 {{ scope.row.visitCount }} 次</el-tag>
          <el-tag v-if="scope.row.depositSum > 0" type="danger" size="small" effect="plain" style="margin-top: 2px">定金 ￥{{ Number(scope.row.depositSum).toLocaleString() }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="手机号" align="center" prop="phone" width="120" />
      <el-table-column label="来源" align="center" prop="source" width="90">
        <template #default="scope">
          <dict-tag :options="crm_customer_source" :value="scope.row.source" />
        </template>
      </el-table-column>
      <el-table-column label="楼盘" align="center" prop="estate" :show-overflow-tooltip="true" />
      <el-table-column label="面积(㎡)" align="center" prop="area" width="90" />
      <el-table-column label="预算" align="center" prop="budget" width="100" />
      <el-table-column label="意向" align="center" prop="intentionLevel" width="80">
        <template #default="scope">
          <dict-tag :options="crm_intention_level" :value="scope.row.intentionLevel" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <dict-tag :options="crm_customer_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="业务员" align="center" prop="ownerName" width="90">
        <template #default="scope">{{ scope.row.ownerName || '-' }}</template>
      </el-table-column>
      <el-table-column label="负责设计师" align="center" prop="designerName" width="100">
        <template #default="scope">{{ scope.row.designerName || '-' }}</template>
      </el-table-column>
      <el-table-column label="家装顾问" align="center" prop="advisorName" width="100">
        <template #default="scope">{{ scope.row.advisorName || '-' }}</template>
      </el-table-column>
      <el-table-column label="登记人" align="center" prop="createByName" width="100">
        <template #default="scope">{{ scope.row.createByName || '-' }}</template>
      </el-table-column>
      <el-table-column label="最近跟进" align="center" prop="latestFollowTime" width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.latestFollowTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-if="['1','2'].includes(scope.row.status)" link type="primary" icon="ChatDotRound" @click="handleFollow(scope.row)" v-hasPermi="['crm:customer:follow']">跟进</el-button>
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['crm:customer:query']">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['crm:customer:edit']">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="640px" append-to-body>
      <el-form ref="customerRef" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="customerName">
              <el-input v-model="form.customerName" placeholder="请输入客户姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="线索来源" prop="source">
              <el-select v-model="form.source" placeholder="请选择来源">
                <el-option v-for="dict in crm_customer_source" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="意向等级" prop="intentionLevel">
              <el-select v-model="form.intentionLevel" placeholder="请选择意向等级">
                <el-option v-for="dict in crm_intention_level" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="楼盘名称" prop="estate">
              <el-input v-model="form.estate" placeholder="请输入楼盘名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="户型" prop="houseType">
              <el-input v-model="form.houseType" placeholder="如：三室两厅" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="建筑面积" prop="area">
              <el-input-number v-model="form.area" :precision="2" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预算区间" prop="budget">
              <el-input v-model="form.budget" placeholder="如：10-15万" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="负责设计师" prop="designerId">
              <el-select v-model="form.designerId" placeholder="可选，后期可补绑" clearable filterable style="width: 100%">
                <el-option v-for="u in designerOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家装顾问" prop="advisorId">
              <el-select v-model="form.advisorId" placeholder="可选，后期可补绑" clearable filterable style="width: 100%">
                <el-option v-for="u in advisorOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="业务员" prop="ownerName">
              <el-input v-model="form.ownerName" placeholder="请输入业务员姓名" maxlength="64" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款方式" prop="payMethod">
              <el-select v-model="form.payMethod" placeholder="可选，客户常用付款方式" clearable style="width: 100%">
                <el-option v-for="dict in biz_pay_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="装修需求" prop="demand">
          <el-input v-model="form.demand" type="textarea" :rows="3" placeholder="请输入装修需求描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 跟进对话框 -->
    <el-dialog :title="followForm.followType === '3' ? '客户到访登记' : '线索跟进'" v-model="followOpen" width="560px" append-to-body>
      <el-form ref="followRef" :model="followForm" :rules="followRules" label-width="90px">
        <el-form-item label="客户">
          <span>{{ currentCustomer.customerName }}（{{ currentCustomer.phone }}）</span>
        </el-form-item>
        <el-form-item label="跟进方式" prop="followType">
          <el-select v-model="followForm.followType" placeholder="请选择跟进方式" @change="handleFollowTypeChange">
            <el-option v-for="dict in crm_follow_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <template v-if="followForm.followType === '3'">
          <el-row>
            <el-col :span="12">
              <el-form-item label="到访人数" prop="visitCount">
                <el-input-number v-model="followForm.visitCount" :min="1" :max="99" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="接待人" prop="receptionUserId">
                <el-select v-model="followForm.receptionUserId" placeholder="默认当前登录人" clearable filterable style="width: 100%">
                  <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickName" :value="u.userId" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="到访目的" prop="visitPurpose">
                <el-select v-model="followForm.visitPurpose" placeholder="请选择到访目的" clearable style="width: 100%">
                  <el-option v-for="dict in crm_visit_purpose" :key="dict.value" :label="dict.label" :value="dict.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="客户反馈" prop="feedback">
                <el-select v-model="followForm.feedback" placeholder="请选择客户反馈" clearable style="width: 100%">
                  <el-option v-for="dict in crm_visit_feedback" :key="dict.value" :label="dict.label" :value="dict.value" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="同行人" prop="companion">
            <el-input v-model="followForm.companion" placeholder="如：爱人、父母（选填）" maxlength="100" />
          </el-form-item>
        </template>
        <el-form-item :label="followForm.followType === '3' ? '接待记录' : '跟进内容'" prop="content">
          <el-input v-model="followForm.content" type="textarea" :rows="3" :placeholder="followForm.followType === '3' ? '请输入本次接待过程记录' : '请输入跟进内容'" />
        </el-form-item>
        <el-form-item v-if="followForm.followType === '3'" label="备注" prop="remark">
          <el-input v-model="followForm.remark" type="textarea" :rows="2" placeholder="其他补充说明（选填）" maxlength="500" />
        </el-form-item>
        <el-form-item label="下次跟进" prop="nextFollowTime">
          <el-date-picker v-model="followForm.nextFollowTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择下次跟进时间" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFollow">确 定</el-button>
          <el-button @click="followOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情抽屉（Tab：基本信息/跟进记录/报价记录） -->
    <el-drawer v-model="detailOpen" :title="'线索详情 - ' + detail.customerName" size="700px">
      <el-tabs v-model="detailTab">
        <el-tab-pane label="基本信息" name="info">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="客户姓名">{{ detail.customerName }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ detail.phone }}</el-descriptions-item>
            <el-descriptions-item label="来源">{{ detail.source }}</el-descriptions-item>
            <el-descriptions-item label="意向等级">{{ detail.intentionLevel }}</el-descriptions-item>
            <el-descriptions-item label="楼盘">{{ detail.estate }}</el-descriptions-item>
            <el-descriptions-item label="户型">{{ detail.houseType }}</el-descriptions-item>
            <el-descriptions-item label="面积">{{ detail.area }} ㎡</el-descriptions-item>
            <el-descriptions-item label="预算">{{ detail.budget }}</el-descriptions-item>
            <el-descriptions-item label="业务员">{{ detail.ownerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="负责设计师">{{ detail.designerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="家装顾问">{{ detail.advisorName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="付款方式">
              <dict-tag v-if="detail.payMethod" :options="biz_pay_type" :value="detail.payMethod" />
              <span v-else>-</span>
            </el-descriptions-item>
            <el-descriptions-item label="登记人">{{ detail.createByName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
            <el-descriptions-item label="最近跟进">{{ parseTime(detail.latestFollowTime) }}</el-descriptions-item>
            <el-descriptions-item label="下次跟进">{{ parseTime(detail.nextFollowTime) }}</el-descriptions-item>
            <el-descriptions-item label="装修需求" :span="2">{{ detail.demand }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="跟进记录" name="follow">
          <el-timeline v-if="detail.followRecords && detail.followRecords.length" style="padding: 12px 8px">
            <el-timeline-item v-for="record in detail.followRecords" :key="record.recordId" :timestamp="parseTime(record.followTime)" placement="top" :type="record.followType === '3' ? 'warning' : 'primary'" :hollow="record.followType !== '3'">
              <template v-if="record.followType === '3'">
                <div style="margin-bottom: 4px">
                  <el-tag size="small" type="warning" effect="dark" style="margin-right: 6px">第{{ record.visitSeq }}次到访</el-tag>
                  <dict-tag v-if="record.visitPurpose" :options="crm_visit_purpose" :value="record.visitPurpose" style="margin-right: 6px" />
                  <dict-tag v-if="record.feedback" :options="crm_visit_feedback" :value="record.feedback" />
                </div>
                <div style="color: #909399; font-size: 12px; margin-bottom: 4px">
                  <span v-if="record.visitCount">到访 {{ record.visitCount }} 人</span>
                  <span v-if="record.companion">｜同行：{{ record.companion }}</span>
                  <span v-if="record.receptionUserName">｜接待人：{{ record.receptionUserName }}</span>
                </div>
                <div>{{ record.content }}</div>
                <div v-if="record.remark" style="color: #909399; font-size: 12px; margin-top: 4px">备注：{{ record.remark }}</div>
              </template>
              <template v-else>
                <dict-tag :options="crm_follow_type" :value="record.followType" style="margin-right: 8px" />{{ record.content }}
              </template>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无跟进记录" :image-size="60" />
        </el-tab-pane>
        <el-tab-pane :label="'报价记录' + (quoteTotal ? '(' + quoteTotal + ')' : '')" name="quote">
          <el-row class="mb8" style="padding: 0 8px">
            <el-col :span="1.5">
              <el-button type="primary" plain icon="Plus" size="small" @click="handleAddQuote" v-hasPermi="['biz:quote:add']">新增报价</el-button>
            </el-col>
          </el-row>
          <el-table v-loading="quoteLoading" :data="quoteList" size="small">
            <el-table-column label="项目名称" align="center" prop="projectName" :show-overflow-tooltip="true" />
            <el-table-column label="报价金额" align="center" prop="totalAmount" width="100" />
            <el-table-column label="优惠" align="center" prop="discountAmount" width="80" />
            <el-table-column label="最终报价" align="center" prop="finalAmount" width="100" />
            <el-table-column label="状态" align="center" prop="status" width="85">
              <template #default="scope">
                <dict-tag :options="biz_audit_status" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
              <template #default="scope">
                <el-button link type="primary" icon="View" size="small" @click="handleViewQuote(scope.row)" v-hasPermi="['biz:quote:query']">详情</el-button>
                <el-button v-if="['0','3'].includes(scope.row.status)" link type="primary" icon="Edit" size="small" @click="handleEditQuote(scope.row)" v-hasPermi="['biz:quote:edit']">修改</el-button>
                <el-button v-if="['0','3'].includes(scope.row.status)" link type="success" icon="Promotion" size="small" @click="handleSubmitQuote(scope.row)" v-hasPermi="['biz:quote:submit']">提交</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane :label="'定金记录' + (depositTotal ? '(' + depositTotal + ')' : '')" name="deposit">
          <el-row class="mb8" style="padding: 0 8px">
            <el-col :span="1.5">
              <el-button type="primary" plain icon="Plus" size="small" @click="handleAddDeposit" v-hasPermi="['biz:deposit:add']">登记定金</el-button>
            </el-col>
          </el-row>
          <el-table v-loading="depositLoading" :data="depositList" size="small">
            <el-table-column label="定金编号" align="center" prop="depositNo" width="130" />
            <el-table-column label="定金金额" align="center" prop="amount" width="100">
              <template #default="scope">
                <span style="color: #f56c6c; font-weight: bold">￥{{ Number(scope.row.amount).toLocaleString() }}</span>
              </template>
            </el-table-column>
            <el-table-column label="收款方式" align="center" prop="payType" width="80">
              <template #default="scope">
                <dict-tag :options="biz_pay_type" :value="scope.row.payType" />
              </template>
            </el-table-column>
            <el-table-column label="收款时间" align="center" prop="payTime" width="150" />
            <el-table-column label="抵扣合同" align="center" prop="contractNo" width="120">
              <template #default="scope">{{ scope.row.contractNo || '—' }}</template>
            </el-table-column>
            <el-table-column label="状态" align="center" prop="status" width="85">
              <template #default="scope">
                <dict-tag :options="biz_deposit_status" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
              <template #default="scope">
                <el-button v-if="scope.row.status === '0'" link type="success" icon="Money" size="small" @click="handleDeductDeposit(scope.row)" v-hasPermi="['biz:deposit:deduct']">签约抵扣</el-button>
                <el-button v-if="scope.row.status === '0'" link type="warning" icon="RefreshLeft" size="small" @click="handleRefundDeposit(scope.row)" v-hasPermi="['biz:deposit:refund']">退还</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane :label="'提醒记录' + (remindTotal ? '(' + remindTotal + ')' : '')" name="remind">
          <el-row class="mb8" style="padding: 0 8px">
            <el-col :span="1.5">
              <el-button type="primary" plain icon="AlarmClock" size="small" @click="handleAddRemind" v-hasPermi="['biz:remind:add']">添加提醒</el-button>
            </el-col>
          </el-row>
          <el-table v-loading="remindLoading" :data="remindList" size="small">
            <el-table-column label="提醒标题" align="center" prop="title" :show-overflow-tooltip="true" />
            <el-table-column label="触发时间" align="center" prop="remindTime" width="150" />
            <el-table-column label="重复规则" align="center" prop="repeatType" width="85">
              <template #default="scope">
                <dict-tag :options="biz_remind_repeat" :value="scope.row.repeatType" />
              </template>
            </el-table-column>
            <el-table-column label="接收人" align="center" prop="receiverName" width="90" />
            <el-table-column label="状态" align="center" prop="status" width="85">
              <template #default="scope">
                <dict-tag :options="biz_remind_status" :value="scope.row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center" class-name="small-padding fixed-width">
              <template #default="scope">
                <el-button v-if="scope.row.status === '0'" link type="danger" icon="CircleClose" size="small" @click="handleCancelRemind(scope.row)" v-hasPermi="['biz:remind:cancel']">取消</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-drawer>

    <!-- 提醒新增/修改弹窗（公共组件） -->
    <remind-form ref="remindFormRef" @ok="getRemindList" />

    <!-- 报价新增/修改弹窗（公共组件） -->
    <quote-form-dialog ref="quoteFormRef" @success="getQuoteList" />

    <!-- 报价详情弹窗（公共组件） -->
    <quote-detail-dialog ref="quoteDetailRef" />
  </div>
</template>

<script setup name="Customer">
import { listCustomer, getCustomer, addCustomer, updateCustomer, delCustomer, followCustomer } from "@/api/biz/customer";
import { listUser } from "@/api/system/user";
import { listQuote, submitQuote } from "@/api/biz/quote";
import { listDeposit, addDeposit, deductDeposit, refundDeposit } from "@/api/biz/deposit";
import { listContract } from "@/api/biz/contract";
import { listRemind, cancelRemind } from "@/api/biz/remind";
import QuoteFormDialog from "@/views/biz/quote/components/QuoteFormDialog.vue";
import QuoteDetailDialog from "@/views/biz/quote/components/QuoteDetailDialog.vue";
import RemindForm from "@/components/RemindForm";

const { proxy } = getCurrentInstance();
const { crm_customer_source, crm_intention_level, crm_follow_type, crm_customer_status, crm_visit_purpose, crm_visit_feedback, biz_audit_status, biz_pay_type, biz_remind_repeat, biz_remind_status, biz_deposit_status } = proxy.useDict("crm_customer_source", "crm_intention_level", "crm_follow_type", "crm_customer_status", "crm_visit_purpose", "crm_visit_feedback", "biz_audit_status", "biz_pay_type", "biz_remind_repeat", "biz_remind_status", "biz_deposit_status");

const customerList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const open = ref(false);
const followOpen = ref(false);
const detailOpen = ref(false);
const detailTab = ref("info");
const quoteList = ref([]);
const quoteLoading = ref(false);
const quoteTotal = ref(0);
const quoteFormRef = ref(null);
const quoteDetailRef = ref(null);
const title = ref("");
const userOptions = ref([]);
const designerOptions = ref([]);
const advisorOptions = ref([]);
const currentCustomer = ref({});
const detail = ref({});
const followForm = ref({});

const queryParams = ref({ pageNum: 1, pageSize: 10, customerName: null, phone: null, estate: null, status: null, ownerName: null, designerId: null, advisorId: null });

const rules = {
  customerName: [{ required: true, message: "客户姓名不能为空", trigger: "blur" }],
  phone: [
    { required: true, message: "手机号不能为空", trigger: "blur" },
    { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号", trigger: "blur" }
  ],
  source: [{ required: true, message: "请选择线索来源", trigger: "change" }]
};
const followRules = {
  content: [{ required: true, message: "跟进内容不能为空", trigger: "blur" }],
  followType: [{ required: true, message: "请选择跟进方式", trigger: "change" }]
};

const form = ref({});

/** 查询列表 */
function getList() {
  loading.value = true;
  listCustomer(queryParams.value).then(res => {
    customerList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

/** 查询用户下拉（设计师=设计部 202 含子部门；家装顾问不限制部门，全员可选） */
function getUserOptions() {
  listUser({ pageNum: 1, pageSize: 500, status: "0" }).then(res => {
    const users = res.rows || [];
    userOptions.value = users;
    const inDeptTree = (u, rootId) => {
      const d = u.dept || {};
      return d.deptId === rootId || String(d.ancestors || "").split(",").includes(String(rootId));
    };
    designerOptions.value = users.filter(u => inDeptTree(u, 202));
    advisorOptions.value = users;
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
  form.value = { customerId: null, customerName: null, phone: null, source: null, estate: null, houseType: null, area: null, budget: null, demand: null, intentionLevel: null, ownerName: null, payMethod: null, designerId: null, advisorId: null };
  proxy.resetForm("customerRef");
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增线索";
}

function handleUpdate(row) {
  reset();
  getCustomer(row.customerId).then(res => {
    form.value = res.data;
    open.value = true;
    title.value = "修改线索";
  });
}

function handleDetail(row) {
  getCustomer(row.customerId).then(res => {
    detail.value = res.data;
    detailTab.value = "info";
    detailOpen.value = true;
    getQuoteList();
    getRemindList();
    getDepositList();
  });
}

/** 定金记录（当前客户） */
const depositList = ref([]);
const depositLoading = ref(false);
const depositTotal = ref(0);
const deductContractId = ref(null);

function getDepositList() {
  if (!detail.value.customerId) return;
  depositLoading.value = true;
  listDeposit({ pageNum: 1, pageSize: 100, customerId: detail.value.customerId }).then(res => {
    depositList.value = res.rows || [];
    depositTotal.value = res.total || 0;
    depositLoading.value = false;
  });
}

function handleAddDeposit() {
  proxy.$prompt("请输入定金金额（元）", "登记定金", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    inputPattern: /^[0-9]+(\.[0-9]{1,2})?$/,
    inputErrorMessage: "金额格式不正确（最多两位小数）"
  }).then(({ value }) => {
    addDeposit({
      customerId: detail.value.customerId,
      amount: Number(value),
      payType: "1",
      payTime: proxy.parseTime(new Date(), '{y}-{m}-{d} {h}:{i}:{s}')
    }).then(() => {
      getDepositList();
      proxy.$modal.msgSuccess("定金登记成功");
    });
  }).catch(() => {});
}

function handleDeductDeposit(row) {
  deductContractId.value = null;
  listContract({ pageNum: 1, pageSize: 100, customerId: detail.value.customerId, status: "2" }).then(res => {
    const contracts = res.rows || [];
    if (!contracts.length) {
      proxy.$modal.msgWarning("该客户暂无已生效合同，无法抵扣");
      return;
    }
    proxy.$msgbox({
      title: "签约抵扣",
      message: h => h("div", null, [
        h("p", { style: "margin: 0 0 10px; color: #606266" }, "定金 " + row.depositNo + "（￥" + Number(row.amount).toLocaleString() + "）将转入所选合同第1期收款"),
        h("el-select", { style: "width: 100%", placeholder: "选择合同", modelValue: deductContractId.value, "onUpdate:modelValue": v => (deductContractId.value = v) },
          contracts.map(c => h("el-option", { key: c.contractId, label: (c.customerName || "") + (c.estate ? "（" + c.estate + "）" : ""), value: c.contractId })))
      ]),
      showCancelButton: true,
      confirmButtonText: "确认抵扣",
      cancelButtonText: "取消"
    }).then(() => {
      if (!deductContractId.value) {
        proxy.$modal.msgWarning("请选择合同");
        return;
      }
      deductDeposit(row.depositId, deductContractId.value).then(() => {
        proxy.$modal.msgSuccess("抵扣成功，已转入合同第1期收款");
        getDepositList();
      });
    }).catch(() => {});
  });
}

function handleRefundDeposit(row) {
  proxy.$prompt("请输入退还原因", "申请定金退还", {
    confirmButtonText: "提交审批",
    cancelButtonText: "取消",
    inputType: "textarea",
    inputValidator: v => (v && v.trim()) ? true : "退还原因必填"
  }).then(({ value }) => {
    refundDeposit(row.depositId, value).then(() => {
      getDepositList();
      proxy.$modal.msgSuccess("已提交退还审批");
    });
  }).catch(() => {});
}

/** 报价记录（当前客户） */
function getQuoteList() {
  if (!detail.value.customerId) return;
  quoteLoading.value = true;
  listQuote({ pageNum: 1, pageSize: 100, customerId: detail.value.customerId }).then(res => {
    quoteList.value = res.rows || [];
    quoteTotal.value = res.total || 0;
    quoteLoading.value = false;
  });
}

function handleAddQuote() {
  quoteFormRef.value.open(null, { customerId: detail.value.customerId });
}

/** 提醒记录（当前客户） */
const remindList = ref([]);
const remindLoading = ref(false);
const remindTotal = ref(0);
const remindFormRef = ref(null);

function getRemindList() {
  if (!detail.value.customerId) return;
  remindLoading.value = true;
  listRemind({ pageNum: 1, pageSize: 100, relateType: "customer", relateId: detail.value.customerId }).then(res => {
    remindList.value = res.rows || [];
    remindTotal.value = res.total || 0;
    remindLoading.value = false;
  });
}

function handleAddRemind() {
  remindFormRef.value.open(null, {
    relateType: "customer",
    relateId: detail.value.customerId,
    relateName: detail.value.customerName
  });
}

function handleCancelRemind(row) {
  proxy.$modal.confirm('确定取消提醒【' + row.title + '】吗？').then(() => {
    return cancelRemind(row.remindId);
  }).then(() => {
    getRemindList();
    proxy.$modal.msgSuccess("取消成功");
  }).catch(() => {});
}

function handleEditQuote(row) {
  quoteFormRef.value.open(row, { customerId: detail.value.customerId });
}

function handleViewQuote(row) {
  quoteDetailRef.value.open(row);
}

function handleSubmitQuote(row) {
  proxy.$modal.confirm('确认提交报价单"' + row.projectName + '"进入审批？').then(() => {
    return submitQuote(row.quoteId);
  }).then(() => {
    getQuoteList();
    getList();
    proxy.$modal.msgSuccess("提交成功");
  }).catch(() => {});
}

function handleFollow(row) {
  currentCustomer.value = row;
  followForm.value = { customerId: row.customerId, followType: "1", content: null, nextFollowTime: null, visitCount: 1, companion: null, receptionUserId: null, visitPurpose: null, feedback: null, remark: null };
  followOpen.value = true;
}

/** 切换跟进方式：非到访时清空到访专属字段 */
function handleFollowTypeChange(val) {
  if (val !== "3") {
    followForm.value.visitCount = 1;
    followForm.value.companion = null;
    followForm.value.receptionUserId = null;
    followForm.value.visitPurpose = null;
    followForm.value.feedback = null;
    followForm.value.remark = null;
  }
}

function submitForm() {
  proxy.$refs["customerRef"].validate(valid => {
    if (valid) {
      const data = { ...form.value, payMethod: form.value.payMethod || '' };
      if (form.value.customerId != null) {
        updateCustomer(data).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addCustomer(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function submitFollow() {
  proxy.$refs["followRef"].validate(valid => {
    if (valid) {
      followCustomer(followForm.value).then(() => {
        proxy.$modal.msgSuccess("跟进成功");
        followOpen.value = false;
        getList();
      });
    }
  });
}

function cancel() {
  open.value = false;
  reset();
}

getUserOptions();
getList();
</script>
