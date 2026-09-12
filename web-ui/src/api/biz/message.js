import request from '@/utils/request'

// 我的站内消息列表
export function listMessage(query) {
  return request({ url: '/system/message/list', method: 'get', params: query })
}

// 未读消息数
export function unreadCount() {
  return request({ url: '/system/message/unreadCount', method: 'get' })
}

// 标记已读
export function markRead(messageId) {
  return request({ url: '/system/message/read/' + messageId, method: 'post' })
}

// 全部已读
export function markAllRead() {
  return request({ url: '/system/message/readAll', method: 'post' })
}
