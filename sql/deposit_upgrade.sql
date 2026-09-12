-- =====================================================
-- 定金管理（2026-08-25）
-- 业务：客户交定金（报价后签约前）→ 签约抵扣（转合同第1期收款）/ 退还（走审批）
-- =====================================================

-- 1. 定金表
CREATE TABLE `biz_deposit` (
  `deposit_id`    bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '定金ID',
  `deposit_no`    varchar(30)  NOT NULL COMMENT '定金编号(DJ+yyyyMMdd+4位流水)',
  `customer_id`   bigint(20)   NOT NULL COMMENT '客户ID',
  `quote_id`      bigint(20)   DEFAULT NULL COMMENT '关联报价ID(可选)',
  `contract_id`   bigint(20)   DEFAULT NULL COMMENT '抵扣合同ID(抵扣后回填)',
  `amount`        decimal(12,2) NOT NULL DEFAULT 0.00 COMMENT '定金金额',
  `pay_type`      varchar(10)  DEFAULT NULL COMMENT '收款方式(字典biz_pay_type)',
  `pay_time`      datetime     NOT NULL COMMENT '收款时间',
  `voucher_url`   varchar(500) DEFAULT NULL COMMENT '收款凭证URL',
  `status`        char(1)      NOT NULL DEFAULT '0' COMMENT '状态(0已收定金 1已抵扣 2退还审批中 3已退还)',
  `refund_time`   datetime     DEFAULT NULL COMMENT '退还时间',
  `refund_reason` varchar(500) DEFAULT NULL COMMENT '退还原因',
  `create_by`     varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
  `update_by`     varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`   datetime     DEFAULT NULL COMMENT '更新时间',
  `remark`        varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`deposit_id`),
  UNIQUE KEY `uk_deposit_no` (`deposit_no`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_contract` (`contract_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定金记录表';

-- 2. 状态字典
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) VALUES ('定金状态', 'biz_deposit_status', '0', 'admin', NOW(), '定金记录状态');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time) VALUES
(1, '已收定金', '0', 'biz_deposit_status', '', 'primary', 'Y', '0', 'admin', NOW()),
(2, '已抵扣',   '1', 'biz_deposit_status', '', 'success', 'N', '0', 'admin', NOW()),
(3, '退还审批中','2', 'biz_deposit_status', '', 'warning', 'N', '0', 'admin', NOW()),
(4, '已退还',   '3', 'biz_deposit_status', '', 'info',    'N', '0', 'admin', NOW());

-- 3. 菜单：客情目录(2010)下定金管理 2167 + 按钮 2168-2173
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
(2167, '定金管理', 2010, 3, 'deposit', 'biz/deposit/index', 'C', '0', '0', 'biz:deposit:list', 'money', 'admin', NOW()),
(2168, '定金查询', 2167, 1, '', '', 'F', '0', '0', 'biz:deposit:query',  '#', 'admin', NOW()),
(2169, '定金登记', 2167, 2, '', '', 'F', '0', '0', 'biz:deposit:add',    '#', 'admin', NOW()),
(2170, '定金修改', 2167, 3, '', '', 'F', '0', '0', 'biz:deposit:edit',   '#', 'admin', NOW()),
(2171, '定金删除', 2167, 4, '', '', 'F', '0', '0', 'biz:deposit:remove', '#', 'admin', NOW()),
(2172, '签约抵扣', 2167, 5, '', '', 'F', '0', '0', 'biz:deposit:deduct', '#', 'admin', NOW()),
(2173, '申请退还', 2167, 6, '', '', 'F', '0', '0', 'biz:deposit:refund', '#', 'admin', NOW());

-- 4. 退还审批流程（与现行全部流程一致：设计部经理/管理员或签）
INSERT INTO flow_definition (flow_id, flow_code, flow_name, biz_type, status, create_by, create_time, remark) VALUES
(7, 'DEPOSIT_REFUND_APPROVAL', '定金退还审批', 'deposit_refund', '0', 'admin', NOW(), '设计部经理/管理员或签');
INSERT INTO flow_node (flow_id, node_order, node_name, approver_type, approver_value, multi_sign, condition_amount, create_by, create_time, remark) VALUES
(7, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', NOW(), '定金退还-或签');

-- 5. 角色分配：老板15/管理员（超管自带）、市场经理11/业务员10、设计经理13/设计师12、行政财务14
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(15, 2167),(15, 2168),(15, 2169),(15, 2170),(15, 2171),(15, 2172),(15, 2173),
(11, 2167),(11, 2168),(11, 2169),(11, 2172),(11, 2173),
(10, 2167),(10, 2168),(10, 2169),(10, 2172),
(13, 2167),(13, 2168),(13, 2169),(13, 2172),(13, 2173),
(12, 2167),(12, 2168),(12, 2169),(12, 2172),
(14, 2167),(14, 2168),(14, 2172);

-- 验证
SELECT menu_id, menu_name, parent_id FROM sys_menu WHERE menu_id BETWEEN 2167 AND 2173;
SELECT flow_id, flow_name, biz_type FROM flow_definition WHERE flow_id = 7;
