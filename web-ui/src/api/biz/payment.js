import request from '@/utils/request'

// 收款计划列表
export function listPlan(query) {
  return request({ url: '/biz/payment/plan/list', method: 'get', params: query })
}

// 合同的四期收款计划
export function getPlansByContract(contractId) {
  return request({ url: '/biz/payment/plan/contract/' + contractId, method: 'get' })
}

// 调整收款计划 [ { planId, contractId, ratio, planDate } ]
export function adjustPlans(data) {
  return request({ url: '/biz/payment/plan/adjust', method: 'post', data: data })
}

// 收款记录列表
export function listRecord(query) {
  return request({ url: '/biz/payment/record/list', method: 'get', params: query })
}

// 收款登记
export function registerPayment(data) {
  return request({ url: '/biz/payment/register', method: 'post', data: data })
}

// 收款冲正 { paymentId, reverseReason }
export function reversePayment(data) {
  return request({ url: '/biz/payment/reverse', method: 'post', data: data })
}

// 收款减免申请 { planId, reduceAmount, reason }
export function reducePayment(data) {
  return request({ url: '/biz/payment/reduce', method: 'post', data: data })
}
