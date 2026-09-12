import request from '@/utils/request'

// 查询定金列表
export function listDeposit(query) {
  return request({ url: '/biz/deposit/list', method: 'get', params: query })
}

// 客户已收未抵扣定金合计
export function getReceivedSum(customerId) {
  return request({ url: '/biz/deposit/receivedSum/' + customerId, method: 'get' })
}

// 查询定金详细
export function getDeposit(depositId) {
  return request({ url: '/biz/deposit/' + depositId, method: 'get' })
}

// 新增定金
export function addDeposit(data) {
  return request({ url: '/biz/deposit', method: 'post', data: data })
}

// 修改定金
export function updateDeposit(data) {
  return request({ url: '/biz/deposit', method: 'put', data: data })
}

// 删除定金
export function delDeposit(depositId) {
  return request({ url: '/biz/deposit/' + depositId, method: 'delete' })
}

// 签约抵扣
export function deductDeposit(depositId, contractId) {
  return request({ url: '/biz/deposit/deduct/' + depositId + '/' + contractId, method: 'post' })
}

// 发起退还审批
export function refundDeposit(depositId, refundReason) {
  return request({ url: '/biz/deposit/refund/' + depositId, method: 'post', data: { refundReason } })
}
