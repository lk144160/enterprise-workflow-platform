-- =====================================================
-- 审批流统一调整（2026-08-25）
-- 全部 6 条流程统一为：设计部经理(或签) → 管理员(admin)
-- 同时修复进行中实例 113 的孤儿节点引用（旧 node 114 已不存在）
-- =====================================================

-- 1. 清空现有节点
DELETE FROM flow_node;

-- 2. 重建：每条流程两级（初审=设计部经理角色13，终审=管理员用户admin）
INSERT INTO flow_node (node_id, flow_id, node_order, node_name, approver_type, approver_value, multi_sign, condition_amount, create_by, create_time, remark) VALUES
(134, 1, 1, '设计部经理', '3', '13', '1', null, 'admin', NOW(), '报价初审'),
(135, 1, 2, '管理员',     '1', '1',  '1', null, 'admin', NOW(), '报价终审'),
(136, 2, 1, '设计部经理', '3', '13', '1', null, 'admin', NOW(), '合同初审'),
(137, 2, 2, '管理员',     '1', '1',  '1', null, 'admin', NOW(), '合同终审'),
(138, 3, 1, '设计部经理', '3', '13', '1', null, 'admin', NOW(), '减免初审'),
(139, 3, 2, '管理员',     '1', '1',  '1', null, 'admin', NOW(), '减免终审'),
(140, 4, 1, '设计部经理', '3', '13', '1', null, 'admin', NOW(), '交底初审'),
(141, 4, 2, '管理员',     '1', '1',  '1', null, 'admin', NOW(), '交底终审'),
(142, 5, 1, '设计部经理', '3', '13', '1', null, 'admin', NOW(), '报销初审'),
(143, 5, 2, '管理员',     '1', '1',  '1', null, 'admin', NOW(), '报销终审'),
(144, 6, 1, '设计部经理', '3', '13', '1', null, 'admin', NOW(), '出库初审'),
(145, 6, 2, '管理员',     '1', '1',  '1', null, 'admin', NOW(), '出库终审');

-- 3. 修正唯一进行中实例（113 报价，审批人 director 恰为设计部经理）
UPDATE flow_instance SET current_node_id = 134 WHERE instance_id = 113;
UPDATE flow_task SET node_id = 134, node_name = '设计部经理' WHERE task_id = 132;

-- 验证
SELECT flow_id, node_order, node_name, approver_type, approver_value, multi_sign FROM flow_node ORDER BY flow_id, node_order;
SELECT i.instance_id, i.biz_type, i.current_node_id, t.task_id, t.node_id, t.node_name, t.approver_id
FROM flow_instance i LEFT JOIN flow_task t ON t.instance_id=i.instance_id AND t.status='0' WHERE i.status='1';
