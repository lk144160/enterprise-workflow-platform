import request from '@/utils/request'

// 客情统计（汇总 + 客户/跟进/签约明细）
export function getReport(query) {
  return request({ url: '/crm/report/list', method: 'get', params: query })
}
