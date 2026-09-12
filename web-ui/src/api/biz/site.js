import request from '@/utils/request'

// 查询工地列表
export function listSite(query) {
  return request({ url: '/biz/site/list', method: 'get', params: query })
}

// 查询工地详情（含阶段与照片）
export function getSite(siteId) {
  return request({ url: '/biz/site/' + siteId, method: 'get' })
}

// 可开工的合同（已生效且未建工地）
export function listAvailableContracts() {
  return request({ url: '/biz/site/contracts', method: 'get' })
}

// 新开工地
export function addSite(data) {
  return request({ url: '/biz/site', method: 'post', data: data })
}

// 修改工地
export function updateSite(data) {
  return request({ url: '/biz/site', method: 'put', data: data })
}

// 删除工地
export function delSite(siteId) {
  return request({ url: '/biz/site/' + siteId, method: 'delete' })
}

// 阶段开始施工
export function startStage(stageId) {
  return request({ url: '/biz/site/stage/' + stageId + '/start', method: 'post' })
}

// 阶段完工
export function completeStage(stageId) {
  return request({ url: '/biz/site/stage/' + stageId + '/complete', method: 'post' })
}

// 调整阶段计划（步骤名称/计划起止/备注）
export function updateStage(data) {
  return request({ url: '/biz/site/stage', method: 'put', data: data })
}

// 新增自定义施工步骤
export function addStage(data) {
  return request({ url: '/biz/site/stage', method: 'post', data: data })
}

// 删除施工步骤（仅未开始的可删）
export function delStage(stageId) {
  return request({ url: '/biz/site/stage/' + stageId, method: 'delete' })
}
