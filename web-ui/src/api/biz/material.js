import request from '@/utils/request'

// 查询物资档案列表
export function listMaterial(query) {
  return request({ url: '/biz/material/list', method: 'get', params: query })
}

// 查询物资详细
export function getMaterial(materialId) {
  return request({ url: '/biz/material/' + materialId, method: 'get' })
}

// 新增物资
export function addMaterial(data) {
  return request({ url: '/biz/material', method: 'post', data: data })
}

// 修改物资
export function updateMaterial(data) {
  return request({ url: '/biz/material', method: 'put', data: data })
}

// 删除物资
export function delMaterial(materialId) {
  return request({ url: '/biz/material/' + materialId, method: 'delete' })
}

// 库存台账列表
export function listInventory(query) {
  return request({ url: '/biz/material/inventory/list', method: 'get', params: query })
}

// 低于安全库存预警
export function listBelowSafety(query) {
  return request({ url: '/biz/material/inventory/belowSafety', method: 'get', params: query })
}
