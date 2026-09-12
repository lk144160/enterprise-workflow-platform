import request from '@/utils/request'

// 查询客户线索列表
export function listCustomer(query) {
  return request({ url: '/crm/customer/list', method: 'get', params: query })
}

// 查询客户线索详细（含跟进记录）
export function getCustomer(customerId) {
  return request({ url: '/crm/customer/' + customerId, method: 'get' })
}

// 新增客户线索
export function addCustomer(data) {
  return request({ url: '/crm/customer', method: 'post', data: data })
}

// 修改客户线索
export function updateCustomer(data) {
  return request({ url: '/crm/customer', method: 'put', data: data })
}

// 删除客户线索
export function delCustomer(customerId) {
  return request({ url: '/crm/customer/' + customerId, method: 'delete' })
}

// 新增跟进记录
export function followCustomer(data) {
  return request({ url: '/crm/customer/follow', method: 'post', data: data })
}

// 线索流失登记 { customerId, lossReason }
export function lossCustomer(data) {
  return request({ url: '/crm/customer/loss', method: 'post', data: data })
}

// 跟进记录列表（客情-跟进动态）
export function listFollowRecord(query) {
  return request({ url: '/crm/customer/follow/list', method: 'get', params: query })
}
