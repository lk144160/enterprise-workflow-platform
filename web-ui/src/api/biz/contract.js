import request from '@/utils/request'

// 查询合同列表
export function listContract(query) {
  return request({ url: '/biz/contract/list', method: 'get', params: query })
}

// 查询合同详细
export function getContract(contractId) {
  return request({ url: '/biz/contract/' + contractId, method: 'get' })
}

// 合同的收款计划
export function getContractPayments(contractId) {
  return request({ url: '/biz/contract/' + contractId + '/payments', method: 'get' })
}

// 客户的已审批报价（合同选报价用）
export function getCustomerQuotes(customerId) {
  return request({ url: '/biz/contract/quotes/' + customerId, method: 'get' })
}

// 新增合同
export function addContract(data) {
  return request({ url: '/biz/contract', method: 'post', data: data })
}

// 修改合同
export function updateContract(data) {
  return request({ url: '/biz/contract', method: 'put', data: data })
}

// 删除合同
export function delContract(contractId) {
  return request({ url: '/biz/contract/' + contractId, method: 'delete' })
}

// 提交审批
export function submitContract(contractId) {
  return request({ url: '/biz/contract/submit/' + contractId, method: 'post' })
}

// 完工登记
export function finishContract(contractId) {
  return request({ url: '/biz/contract/finish/' + contractId, method: 'post' })
}

// 归档
export function archiveContract(contractId) {
  return request({ url: '/biz/contract/archive/' + contractId, method: 'post' })
}

// 终止 { contractId, terminateReason }
export function terminateContract(data) {
  return request({ url: '/biz/contract/terminate', method: 'post', data: data })
}
