import request from '@/utils/request'

// 查询员工档案列表
export function listEmployee(query) {
  return request({ url: '/hr/employee/list', method: 'get', params: query })
}

// 查询员工档案详细
export function getEmployee(employeeId) {
  return request({ url: '/hr/employee/' + employeeId, method: 'get' })
}

// 劳动合同即将到期员工
export function listContractExpire() {
  return request({ url: '/hr/employee/contractExpire', method: 'get' })
}

// 新增员工档案（入职办理）
export function addEmployee(data) {
  return request({ url: '/hr/employee', method: 'post', data: data })
}

// 修改员工档案
export function updateEmployee(data) {
  return request({ url: '/hr/employee', method: 'put', data: data })
}

// 删除员工档案
export function delEmployee(employeeId) {
  return request({ url: '/hr/employee/' + employeeId, method: 'delete' })
}

// 离职登记 { employeeId, leaveDate, leaveReason }
export function leaveEmployee(data) {
  return request({ url: '/hr/employee/leave', method: 'post', data: data })
}
