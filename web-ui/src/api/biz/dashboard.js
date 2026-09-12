import request from '@/utils/request'

// 工作台汇总（按角色返回统计卡片）
export function getSummary() {
  return request({ url: '/dashboard/summary', method: 'get' })
}

// 经营数据大屏（近 N 天）
export function getScreenData(days) {
  return request({ url: '/dashboard/screen', method: 'get', params: { days } })
}
