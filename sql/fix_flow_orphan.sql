-- =====================================================
-- 审批流孤儿任务修复（2026-08-23）
-- 背景：role_upgrade.sql 重置 flow_node 后节点ID变更（新114-123），
--       进行中的实例/任务仍引用旧节点ID（108/110），处理时报 NPE。
-- 修复：按流程映射修正引用到新节点。
--   flow 1 报价流程第一节点   → 新节点 114（设计部经理）
--   flow 3 收款减免流程第一节点 → 新节点 117（市场部经理）
-- =====================================================

-- 1. 实例 106（报价 quote）：旧节点108 → 114
UPDATE flow_instance SET current_node_id = 114 WHERE instance_id = 106;
UPDATE flow_task SET node_id = 114, node_name = '设计部经理' WHERE task_id IN (113, 114);

-- 2. 实例 108/111（收款减免 payment_reduce）：旧节点110 → 117
UPDATE flow_instance SET current_node_id = 117 WHERE instance_id IN (108, 111);
UPDATE flow_task SET node_id = 117, node_name = '市场部经理' WHERE task_id IN (118, 128);

-- 3. ry(用户2，无审批中心菜单)名下的待办转给 admin(用户1)，保证可处理
UPDATE flow_task SET approver_id = 1 WHERE task_id IN (114, 118, 128) AND approver_id = 2;

-- 验证
SELECT task_id, instance_id, node_id, node_name, approver_id, status FROM flow_task WHERE status = '0';
SELECT instance_id, flow_id, biz_type, current_node_id, status FROM flow_instance WHERE status = '1';
