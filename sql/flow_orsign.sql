-- =====================================================
-- 审批流改为单级或签（2026-08-25 二次调整）
-- 每条流程仅一个节点：设计部经理(角色13) 或 超级管理员(角色1)，任一人审批即通过
-- 前提：FlowEngineServiceImpl.resolveApprovers 已支持类型3逗号分隔多角色
-- =====================================================

-- 1. 清空两级节点，重建为单级或签节点
DELETE FROM flow_node;
INSERT INTO flow_node (node_id, flow_id, node_order, node_name, approver_type, approver_value, multi_sign, condition_amount, create_by, create_time, remark) VALUES
(146, 1, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', NOW(), '报价审批-或签'),
(147, 2, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', NOW(), '合同审批-或签'),
(148, 3, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', NOW(), '减免审批-或签'),
(149, 4, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', NOW(), '交底审批-或签'),
(150, 5, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', NOW(), '报销审批-或签'),
(151, 6, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', NOW(), '出库审批-或签');

-- 2. 进行中实例 113（报价）：挂到新节点 146，补 admin 待办形成或签对
UPDATE flow_instance SET current_node_id = 146 WHERE instance_id = 113;
UPDATE flow_task SET node_id = 146, node_name = '设计部经理/管理员' WHERE task_id = 132;
INSERT INTO flow_task (instance_id, node_id, node_name, approver_id, status, create_by, create_time)
SELECT 113, 146, '设计部经理/管理员', 1, '0', 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM flow_task WHERE instance_id = 113 AND node_id = 146 AND approver_id = 1);

-- 验证
SELECT flow_id, node_order, node_name, approver_type, approver_value, multi_sign FROM flow_node ORDER BY flow_id;
SELECT task_id, instance_id, node_id, node_name, approver_id, status FROM flow_task WHERE instance_id = 113 ORDER BY task_id;
