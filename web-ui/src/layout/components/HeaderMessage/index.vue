<template>
  <el-popover ref="messagePopover" placement="bottom-end" :width="340" trigger="manual" v-model:visible="messageVisible" popper-class="message-popover">
    <template #reference>
      <div class="message-trigger" @mouseenter="onEnter" @mouseleave="onLeave">
        <el-badge :value="unreadCount" :hidden="unreadCount <= 0" :max="99">
          <el-icon :size="18"><Bell /></el-icon>
        </el-badge>
      </div>
    </template>

    <div class="message-header">
      <span class="message-title">消息通知</span>
      <span v-if="messageList.length > 0" class="message-mark-all" @click="handleMarkAllRead">全部已读</span>
    </div>

    <div v-if="messageLoading" class="message-loading">加载中...</div>
    <div v-else-if="messageList.length === 0" class="message-empty">暂无消息</div>
    <div v-else class="message-list">
      <div v-for="item in messageList" :key="item.messageId" class="message-item" :class="{ 'is-read': item.isRead === '1' }" @click="openMessage(item)">
        <el-tag size="small" :type="msgTagType(item.msgType)" class="message-tag">
          {{ msgTypeLabel(item.msgType) }}
        </el-tag>
        <div class="message-main">
          <div class="message-item-title">{{ item.title }}</div>
          <div class="message-item-content">{{ item.content }}</div>
        </div>
        <span class="message-item-date">{{ shortTime(item.createTime) }}</span>
      </div>
    </div>

    <div v-if="messageList.length > 0" class="message-footer" @click="messageVisible = false">共 {{ messageList.length }} 条，点击消息可跳转关联业务</div>
  </el-popover>
</template>

<script setup>
import { listMessage, unreadCount as fetchUnreadCount, markRead, markAllRead } from '@/api/biz/message'
import useUserStore from '@/store/modules/user'

const { proxy } = getCurrentInstance()
const { sys_msg_type: msg_type_options } = proxy.useDict('sys_msg_type')

const router = useRouter()
const userStore = useUserStore()

const messagePopover = ref(null)
const messageList = ref([])
const unreadCount = ref(0)
const messageLoading = ref(false)
const messageVisible = ref(false)
const messageLeaveTimer = ref(null)
let pollTimer = null

const MSG_LABELS = { todo: '待办', result: '审批结果', notice: '通知', warn: '预警', remind: '提醒' }
const MSG_TAG = { todo: 'primary', result: 'success', notice: 'info', warn: 'danger', remind: 'warning' }

function msgTypeLabel(type) {
  const d = (msg_type_options.value || []).find(o => o.value === type)
  return d ? d.label : (MSG_LABELS[type] || '消息')
}

function msgTagType(type) {
  return MSG_TAG[type] || 'info'
}

function shortTime(time) {
  if (!time) return ''
  return String(time).length > 10 ? String(time).slice(5, 16) : time
}

function loadUnread() {
  if (!userStore.id) return
  fetchUnreadCount().then(res => {
    unreadCount.value = Number(res.data || res.msg || 0) || 0
  }).catch(() => {})
}

function loadList() {
  messageLoading.value = true
  listMessage({ pageNum: 1, pageSize: 20 }).then(res => {
    messageList.value = res.rows || []
    loadUnread()
  }).finally(() => {
    messageLoading.value = false
  })
}

function onEnter() {
  clearTimeout(messageLeaveTimer.value)
  messageVisible.value = true
  loadList()
  bindPopperHover()
}

function onLeave() {
  messageLeaveTimer.value = setTimeout(() => { messageVisible.value = false }, 150)
}

function bindPopperHover() {
  const popper = messagePopover.value?.popperRef?.contentRef
  if (popper && !popper._messageBound) {
    popper._messageBound = true
    popper.addEventListener('mouseenter', () => clearTimeout(messageLeaveTimer.value))
    popper.addEventListener('mouseleave', () => {
      messageLeaveTimer.value = setTimeout(() => { messageVisible.value = false }, 100)
    })
  }
}

function openMessage(item) {
  if (item.isRead === '0') {
    markRead(item.messageId).catch(() => {})
    item.isRead = '1'
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }
  messageVisible.value = false
  if (item.routePath) {
    const query = {}
    if (item.routeParams) {
      try {
        Object.assign(query, JSON.parse(item.routeParams))
      } catch (e) { /* 非JSON参数忽略 */ }
    }
    router.push({ path: item.routePath, query }).catch(() => {})
  }
}

function handleMarkAllRead() {
  markAllRead().then(() => {
    messageList.value = messageList.value.map(m => ({ ...m, isRead: '1' }))
    unreadCount.value = 0
  }).catch(() => {})
}

onMounted(() => {
  loadUnread()
  pollTimer = setInterval(loadUnread, 60000)
})

onBeforeUnmount(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style lang="scss" scoped>
.message-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  cursor: pointer;
  border-radius: 50%;
  transition: background 0.2s;
  &:hover { background: rgba(0, 0, 0, 0.06); }
}
</style>

<style lang="scss">
.message-popover { padding: 0 !important; }
.message-popover .message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  border-bottom: 1px solid #f0f0f0;
  .message-title { font-weight: 600; font-size: 14px; color: #303133; }
  .message-mark-all { font-size: 12px; color: #2b7cc1; cursor: pointer; }
  .message-mark-all:hover { color: #1a5fa8; }
}
.message-popover .message-loading,
.message-popover .message-empty {
  padding: 24px 0;
  text-align: center;
  color: #999;
  font-size: 13px;
}
.message-popover .message-list { max-height: 360px; overflow-y: auto; }
.message-popover .message-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 10px 14px;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
  &:last-child { border-bottom: none; }
  &:hover { background: #f7f9fb; }
}
.message-popover .message-item.is-read .message-tag,
.message-popover .message-item.is-read .message-item-title,
.message-popover .message-item.is-read .message-item-content,
.message-popover .message-item.is-read .message-item-date { opacity: 0.5; filter: grayscale(1); }
.message-popover .message-tag { flex-shrink: 0; margin-top: 1px; }
.message-popover .message-main { flex: 1; min-width: 0; }
.message-popover .message-item-title {
  font-size: 13px;
  color: #303133;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.message-popover .message-item-content {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 2px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.message-popover .message-item-date {
  flex-shrink: 0;
  font-size: 11px;
  color: #c0c4cc;
  margin-top: 2px;
}
.message-popover .message-footer {
  padding: 8px 14px;
  text-align: center;
  font-size: 12px;
  color: #909399;
  border-top: 1px solid #f0f0f0;
}
</style>
