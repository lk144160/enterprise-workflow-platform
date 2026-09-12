import request from '@/utils/request'

// 查询报价单列表
export function listQuote(query) {
  return request({ url: '/biz/quote/list', method: 'get', params: query })
}

// 查询报价单详细（含明细）
export function getQuote(quoteId) {
  return request({ url: '/biz/quote/' + quoteId, method: 'get' })
}

// 新增报价单
export function addQuote(data) {
  return request({ url: '/biz/quote', method: 'post', data: data })
}

// 修改报价单
export function updateQuote(data) {
  return request({ url: '/biz/quote', method: 'put', data: data })
}

// 删除报价单
export function delQuote(quoteId) {
  return request({ url: '/biz/quote/' + quoteId, method: 'delete' })
}

// 提交审批
export function submitQuote(quoteId) {
  return request({ url: '/biz/quote/submit/' + quoteId, method: 'post' })
}

// 撤销审批
export function cancelQuote(quoteId) {
  return request({ url: '/biz/quote/cancel/' + quoteId, method: 'post' })
}

// 作废
export function invalidateQuote(quoteId) {
  return request({ url: '/biz/quote/invalidate/' + quoteId, method: 'post' })
}
