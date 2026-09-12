import request from '@/utils/request'

// 查询业务附件列表
export function listAttachment(query) {
  return request({ url: '/business/attachment/list', method: 'get', params: query })
}

// 删除业务附件
export function delAttachment(attachmentId) {
  return request({ url: '/business/attachment/' + attachmentId, method: 'delete' })
}

// 保存附件关联记录 { bizType, bizId, fileName, fileUrl }
export function addAttachment(data) {
  return request({ url: '/business/attachment', method: 'post', data: data })
}
