import request from '@/utils/request'

// 我的待办列表
export function listTodo(query) {
  return request({ url: '/flow/todo/list', method: 'get', params: query })
}

// 我的待办数量
export function countTodo() {
  return request({ url: '/flow/todo/count', method: 'get' })
}

// 我的申请列表
export function listMine(query) {
  return request({ url: '/flow/mine/list', method: 'get', params: query })
}

// 审批详情（含审批历史）
export function getInstance(instanceId) {
  return request({ url: '/flow/instance/' + instanceId, method: 'get' })
}

// 处理审批任务 { taskId, action: agree/reject, opinion }
export function handleTask(data) {
  return request({ url: '/flow/handle', method: 'post', data: data })
}

// 转交审批任务 { taskId, targetUserId, opinion }
export function transferTask(data) {
  return request({ url: '/flow/transfer', method: 'post', data: data })
}

// 撤销审批
export function cancelInstance(instanceId) {
  return request({ url: '/flow/cancel/' + instanceId, method: 'post' })
}

// 流程定义列表
export function listDefinition(query) {
  return request({ url: '/flow/definition/list', method: 'get', params: query })
}

// 流程定义详细（含节点）
export function getDefinition(flowId) {
  return request({ url: '/flow/definition/' + flowId, method: 'get' })
}

// 新增流程定义
export function addDefinition(data) {
  return request({ url: '/flow/definition', method: 'post', data: data })
}

// 修改流程定义
export function updateDefinition(data) {
  return request({ url: '/flow/definition', method: 'put', data: data })
}

// 删除流程定义
export function delDefinition(flowId) {
  return request({ url: '/flow/definition/' + flowId, method: 'delete' })
}
