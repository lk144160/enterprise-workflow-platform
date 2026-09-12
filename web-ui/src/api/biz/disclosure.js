import request from '@/utils/request'

// 查询技术交底列表
export function listDisclosure(query) {
  return request({ url: '/biz/disclosure/list', method: 'get', params: query })
}

// 查询技术交底详细
export function getDisclosure(disclosureId) {
  return request({ url: '/biz/disclosure/' + disclosureId, method: 'get' })
}

// 新增技术交底
export function addDisclosure(data) {
  return request({ url: '/biz/disclosure', method: 'post', data: data })
}

// 修改技术交底
export function updateDisclosure(data) {
  return request({ url: '/biz/disclosure', method: 'put', data: data })
}

// 删除技术交底
export function delDisclosure(disclosureId) {
  return request({ url: '/biz/disclosure/' + disclosureId, method: 'delete' })
}

// 提交审批
export function submitDisclosure(disclosureId) {
  return request({ url: '/biz/disclosure/submit/' + disclosureId, method: 'post' })
}
