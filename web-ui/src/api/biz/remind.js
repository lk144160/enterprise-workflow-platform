import request from '@/utils/request'

// 查询自定义提醒列表
export function listRemind(query) {
  return request({ url: '/biz/remind/list', method: 'get', params: query })
}

// 查询自定义提醒详细
export function getRemind(remindId) {
  return request({ url: '/biz/remind/' + remindId, method: 'get' })
}

// 新增自定义提醒
export function addRemind(data) {
  return request({ url: '/biz/remind', method: 'post', data: data })
}

// 修改自定义提醒
export function updateRemind(data) {
  return request({ url: '/biz/remind', method: 'put', data: data })
}

// 取消自定义提醒
export function cancelRemind(remindId) {
  return request({ url: '/biz/remind/cancel/' + remindId, method: 'put' })
}

// 删除自定义提醒
export function delRemind(remindId) {
  return request({ url: '/biz/remind/' + remindId, method: 'delete' })
}
