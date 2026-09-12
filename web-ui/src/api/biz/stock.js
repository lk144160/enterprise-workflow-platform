import request from '@/utils/request'

// 查询出入库单列表
export function listStockOrder(query) {
  return request({ url: '/biz/stock/list', method: 'get', params: query })
}

// 查询出入库单详细（含明细）
export function getStockOrder(orderId) {
  return request({ url: '/biz/stock/' + orderId, method: 'get' })
}

// 新增出入库单
export function addStockOrder(data) {
  return request({ url: '/biz/stock', method: 'post', data: data })
}

// 修改出入库单
export function updateStockOrder(data) {
  return request({ url: '/biz/stock', method: 'put', data: data })
}

// 删除出入库单
export function delStockOrder(orderId) {
  return request({ url: '/biz/stock/' + orderId, method: 'delete' })
}

// 提交（入库直接完成，出库进审批）
export function submitStockOrder(orderId) {
  return request({ url: '/biz/stock/submit/' + orderId, method: 'post' })
}

// 作废
export function invalidateStockOrder(orderId) {
  return request({ url: '/biz/stock/invalidate/' + orderId, method: 'post' })
}
