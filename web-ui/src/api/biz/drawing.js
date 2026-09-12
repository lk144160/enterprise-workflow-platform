import request from '@/utils/request'

// 查询图纸列表
export function listDrawing(query) {
  return request({ url: '/biz/drawing/list', method: 'get', params: query })
}

// 上传图纸（含新版本）
export function addDrawing(data) {
  return request({ url: '/biz/drawing', method: 'post', data: data })
}

// 确认图纸
export function confirmDrawing(drawingId) {
  return request({ url: '/biz/drawing/confirm/' + drawingId, method: 'post' })
}

// 删除图纸
export function delDrawing(drawingId) {
  return request({ url: '/biz/drawing/' + drawingId, method: 'delete' })
}
