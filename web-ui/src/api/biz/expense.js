import request from '@/utils/request'

// 查询报销单列表
export function listExpense(query) {
  return request({ url: '/biz/expense/list', method: 'get', params: query })
}

// 查询报销单详细
export function getExpense(expenseId) {
  return request({ url: '/biz/expense/' + expenseId, method: 'get' })
}

// 新增报销单
export function addExpense(data) {
  return request({ url: '/biz/expense', method: 'post', data: data })
}

// 修改报销单
export function updateExpense(data) {
  return request({ url: '/biz/expense', method: 'put', data: data })
}

// 删除报销单
export function delExpense(expenseId) {
  return request({ url: '/biz/expense/' + expenseId, method: 'delete' })
}

// 提交审批
export function submitExpense(expenseId) {
  return request({ url: '/biz/expense/submit/' + expenseId, method: 'post' })
}

// 打款登记
export function payExpense(expenseId) {
  return request({ url: '/biz/expense/pay/' + expenseId, method: 'post' })
}

// 作废
export function invalidateExpense(expenseId) {
  return request({ url: '/biz/expense/invalidate/' + expenseId, method: 'post' })
}
