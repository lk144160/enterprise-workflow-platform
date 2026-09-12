-- ----------------------------------------------------------------------------
-- 装修业务运营平台 业务建表脚本
-- 前置：先执行 ry_20260417.sql（基础系统表）与 quartz.sql
-- 库名：renovation_ops（业务表与系统表同库）
-- ----------------------------------------------------------------------------

-- ----------------------------
-- 1. 客户线索表
-- ----------------------------
drop table if exists crm_customer;
create table crm_customer (
  customer_id        bigint(20)      not null auto_increment    comment '线索ID',
  customer_name      varchar(64)     not null                   comment '客户姓名',
  phone              varchar(20)     not null                   comment '手机号',
  source             varchar(30)     not null                   comment '线索来源（字典 crm_customer_source）',
  estate             varchar(100)    default null               comment '楼盘名称',
  house_type         varchar(30)     default null               comment '户型',
  area               decimal(8,2)    default null               comment '建筑面积',
  budget             varchar(30)     default null               comment '预算区间',
  demand             text            default null               comment '装修需求描述',
  intention_level    char(1)         default null               comment '意向等级（A/B/C/D 字典 crm_intention_level）',
  status             char(1)         not null default '1'       comment '状态（1待跟进 2跟进中 3已转化 4已签约 5已流失）',
  owner_name         varchar(64)     default null               comment '业务员姓名（直接录入）',
  designer_id        bigint(20)      default null               comment '负责设计师user_id（可选绑定）',
  advisor_id         bigint(20)      default null               comment '家装顾问user_id（可选绑定）',
  dept_id            bigint(20)      default null               comment '归属部门',
  latest_follow_time datetime        default null               comment '最近跟进时间',
  next_follow_time   datetime        default null               comment '下次跟进时间',
  loss_reason        varchar(500)    default null               comment '流失原因',
  pay_method         varchar(10)     default null               comment '付款方式（字典 biz_pay_type，可选）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (customer_id),
  key idx_crm_customer_phone (phone),
  key idx_crm_customer_status (status),
  key idx_crm_customer_owner (owner_id),
  key idx_crm_customer_dept (dept_id)
) engine=innodb auto_increment=100 comment = '客户线索表';

-- ----------------------------
-- 2. 线索跟进记录表
-- ----------------------------
drop table if exists crm_follow_record;
create table crm_follow_record (
  record_id          bigint(20)      not null auto_increment    comment '记录ID',
  customer_id        bigint(20)      not null                   comment '线索ID',
  follow_type        varchar(30)     not null                   comment '跟进方式（字典 crm_follow_type）',
  content            varchar(1000)   not null                   comment '跟进内容',
  follow_time        datetime        not null                   comment '跟进时间',
  next_follow_time   datetime        default null               comment '下次跟进时间',
  visit_count        int(4)          default 1                  comment '到访人数（仅到访记录）',
  companion          varchar(100)    default null               comment '同行人描述（仅到访记录）',
  reception_user_id  bigint(20)      default null               comment '接待人user_id（仅到访记录）',
  visit_purpose      varchar(30)     default null               comment '到访目的（字典 crm_visit_purpose，仅到访记录）',
  feedback           varchar(30)     default null               comment '客户反馈（字典 crm_visit_feedback，仅到访记录）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (record_id),
  key idx_follow_customer (customer_id)
) engine=innodb auto_increment=100 comment = '线索跟进记录表';

-- ----------------------------
-- 3. 报价单表
-- ----------------------------
drop table if exists biz_quote;
create table biz_quote (
  quote_id           bigint(20)      not null auto_increment    comment '报价ID',
  quote_no           varchar(32)     not null                   comment '报价编号（BJ+yyyyMMdd+4位流水）',
  customer_id        bigint(20)      not null                   comment '客户ID',
  project_name       varchar(100)    not null                   comment '项目名称',
  total_amount       decimal(12,2)   not null default 0         comment '报价金额（直接录入）',
  discount_amount    decimal(12,2)   default 0                  comment '优惠金额',
  final_amount       decimal(12,2)   not null default 0         comment '最终报价',
  status             char(1)         not null default '0'       comment '状态（0草稿 1审批中 2已通过 3已驳回 4已作废）',
  version            int(4)          default 1                  comment '版本号',
  submit_time        datetime        default null               comment '提交时间',
  approve_time       datetime        default null               comment '审批完成时间',
  dept_id            bigint(20)      default null               comment '归属部门',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (quote_id),
  unique key uk_quote_no (quote_no),
  key idx_quote_customer (customer_id),
  key idx_quote_status (status),
  key idx_quote_dept (dept_id)
) engine=innodb auto_increment=100 comment = '报价单表';

-- ----------------------------
-- 4. 合同表
-- ----------------------------
drop table if exists biz_contract;
create table biz_contract (
  contract_id        bigint(20)      not null auto_increment    comment '合同ID',
  contract_no        varchar(32)     not null                   comment '合同编号（HT+yyyyMMdd+4位流水）',
  customer_id        bigint(20)      not null                   comment '客户ID',
  quote_id           bigint(20)      default null               comment '关联报价ID',
  contract_amount    decimal(12,2)   not null default 0         comment '合同金额',
  paid_amount        decimal(12,2)   default 0                  comment '累计已收金额（冗余）',
  amount_diff_note   varchar(500)    default null               comment '与报价差异说明',
  sign_date          date            default null               comment '签订日期',
  start_date         date            default null               comment '工期开始',
  end_date           date            default null               comment '工期结束',
  finish_date        date            default null               comment '完工日期（完工登记写入）',
  status             char(1)         not null default '0'       comment '状态（0待审批 1审批中 2已生效 3已完工 4已归档 5已终止）',
  dept_id            bigint(20)      default null               comment '归属部门',
  owner_user_id      bigint(20)      default null               comment '合同负责人user_id（可选绑定）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (contract_id),
  unique key uk_contract_no (contract_no),
  key idx_contract_customer (customer_id),
  key idx_contract_quote (quote_id),
  key idx_contract_status (status),
  key idx_contract_dept (dept_id),
  key idx_contract_owner (owner_user_id)
) engine=innodb auto_increment=100 comment = '合同表';

-- ----------------------------
-- 5. 收款计划表（四期期次）
-- ----------------------------
drop table if exists biz_payment_plan;
create table biz_payment_plan (
  plan_id            bigint(20)      not null auto_increment    comment '期次ID',
  contract_id        bigint(20)      not null                   comment '合同ID',
  period_no          int(2)          not null                   comment '期次（1-4）',
  period_name        varchar(30)     not null                   comment '期次名称（签约款/设计款/施工款/尾款）',
  ratio              decimal(5,2)    not null                   comment '比例（%，合计100）',
  plan_amount        decimal(12,2)   not null default 0         comment '应收金额',
  trigger_type       varchar(30)     not null                   comment '到期触发（contract_effect/design_finish/disclosure_confirm/finish_register）',
  trigger_biz_id     bigint(20)      default null               comment '触发里程碑业务单据ID',
  plan_date          date            default null               comment '计划收款日',
  due_time           datetime        default null               comment '实际到期时间（里程碑达成时间）',
  status             char(1)         not null default '0'       comment '状态（0未到期 1待收款 2已结清 3已减免 4已作废）',
  receive_amount     decimal(12,2)   default 0                  comment '累计实收（含冲正负数）',
  reduce_amount      decimal(12,2)   default 0                  comment '减免金额',
  settle_time        datetime        default null               comment '结清/减免时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (plan_id),
  unique key uk_plan_contract_period (contract_id, period_no),
  key idx_plan_status (status)
) engine=innodb auto_increment=100 comment = '收款计划表（四期期次）';

-- ----------------------------
-- 6. 收款记录表
-- ----------------------------
drop table if exists biz_payment_record;
create table biz_payment_record (
  payment_id             bigint(20)   not null auto_increment    comment '收款ID',
  contract_id            bigint(20)   not null                   comment '合同ID',
  plan_id                bigint(20)   not null                   comment '期次ID',
  period_no              int(2)       not null                   comment '期次（冗余）',
  amount                 decimal(12,2) not null default 0        comment '实收金额（冲正记录为负数）',
  pay_type               varchar(30)  default null               comment '收款方式（字典 biz_pay_type）',
  pay_time               datetime     not null                   comment '到账时间',
  voucher_url            varchar(500) default null               comment '凭证附件地址',
  deviation_note         varchar(500) default null               comment '偏差说明（实收与应收差额时必填）',
  operator_id            bigint(20)   default null               comment '登记人',
  reverse_of_payment_id  bigint(20)   default null               comment '冲正关联的原收款ID',
  reverse_reason         varchar(500) default null               comment '冲正原因',
  create_by              varchar(64)  default ''                 comment '创建者',
  create_time            datetime     default null               comment '创建时间',
  update_by              varchar(64)  default ''                 comment '更新者',
  update_time            datetime     default null               comment '更新时间',
  remark                 varchar(500) default null               comment '备注',
  primary key (payment_id),
  key idx_payment_contract (contract_id),
  key idx_payment_plan (plan_id)
) engine=innodb auto_increment=100 comment = '收款记录表';

-- ----------------------------
-- 6.1 定金记录表（报价后签约前收取，签约抵扣/退还）
-- ----------------------------
drop table if exists biz_deposit;
create table biz_deposit (
  deposit_id        bigint(20)      not null auto_increment    comment '定金ID',
  deposit_no        varchar(30)     not null                   comment '定金编号（DJ+yyyyMMdd+4位流水）',
  customer_id       bigint(20)      not null                   comment '客户ID',
  quote_id          bigint(20)      default null               comment '关联报价ID（可选）',
  contract_id       bigint(20)      default null               comment '抵扣合同ID（抵扣后回填）',
  amount            decimal(12,2)   not null default 0         comment '定金金额',
  pay_type          varchar(10)     default null               comment '收款方式（字典 biz_pay_type）',
  pay_time          datetime        not null                   comment '收款时间',
  voucher_url       varchar(500)    default null               comment '收款凭证地址',
  status            char(1)         not null default '0'       comment '状态（0已收定金 1已抵扣 2退还审批中 3已退还）',
  refund_time       datetime        default null               comment '退还时间',
  refund_reason     varchar(500)    default null               comment '退还原因',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime        default null               comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime        default null               comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (deposit_id),
  unique key uk_deposit_no (deposit_no),
  key idx_deposit_customer (customer_id),
  key idx_deposit_contract (contract_id),
  key idx_deposit_status (status)
) engine=innodb auto_increment=100 comment = '定金记录表';

-- ----------------------------
-- 7. 图纸表（版本）
-- ----------------------------
drop table if exists biz_drawing;
create table biz_drawing (
  drawing_id         bigint(20)      not null auto_increment    comment '图纸ID',
  customer_id        bigint(20)      default null               comment '客户ID（可选绑定）',
  contract_id        bigint(20)      default null               comment '合同ID（可选绑定）',
  drawing_name       varchar(100)    not null                   comment '图纸名称',
  drawing_type       varchar(30)     default null               comment '图纸类型（字典 biz_drawing_type）',
  file_url           varchar(500)    not null                   comment '文件地址',
  file_name          varchar(200)    default null               comment '原始文件名',
  version            int(4)          default 1                  comment '版本号',
  is_current         char(1)         default '1'                comment '是否当前版本（1是 0否）',
  status             char(1)         default '0'                comment '状态（0待确认 1已确认 2已作废）',
  confirm_user_id    bigint(20)      default null               comment '确认人',
  confirm_time       datetime        default null               comment '确认时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (drawing_id),
  key idx_drawing_customer (customer_id),
  key idx_drawing_contract (contract_id)
) engine=innodb auto_increment=100 comment = '图纸版本表';

-- ----------------------------
-- 8. 技术交底表
-- ----------------------------
drop table if exists biz_disclosure;
create table biz_disclosure (
  disclosure_id      bigint(20)      not null auto_increment    comment '交底ID',
  disclosure_no      varchar(32)     not null                   comment '交底编号（JB+yyyyMMdd+4位流水）',
  contract_id        bigint(20)      not null                   comment '合同ID',
  content            text            not null                   comment '交底内容（设计要点/材料要求/施工注意事项）',
  meeting_date       date            default null               comment '会议日期',
  attendees          varchar(500)    default null               comment '内部参与人user_id集合（逗号分隔）',
  external_attendees varchar(200)    default null               comment '外部参与人（工长姓名）',
  status             char(1)         not null default '0'       comment '状态（0草稿 1审批中 2已确认 3已驳回）',
  confirm_time       datetime        default null               comment '确认时间',
  dept_id            bigint(20)      default null               comment '归属部门',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (disclosure_id),
  unique key uk_disclosure_no (disclosure_no),
  key idx_disclosure_contract (contract_id),
  key idx_disclosure_status (status)
) engine=innodb auto_increment=100 comment = '技术交底表';

-- ----------------------------
-- 9. 报销单表
-- ----------------------------
drop table if exists biz_expense;
create table biz_expense (
  expense_id         bigint(20)      not null auto_increment    comment '报销ID',
  expense_no         varchar(32)     not null                   comment '报销编号（BX+yyyyMMdd+4位流水）',
  applicant_id       bigint(20)      not null                   comment '申请人user_id',
  dept_id            bigint(20)      default null               comment '申请部门',
  expense_type       varchar(30)     not null                   comment '费用类型（字典 biz_expense_type）',
  amount             decimal(12,2)   not null default 0         comment '报销金额',
  expense_date       date            not null                   comment '费用发生日期',
  invoice_count      int(4)          default 0                  comment '发票张数（与附件校验）',
  description        varchar(500)    default null               comment '费用说明',
  status             char(1)         not null default '0'       comment '状态（0草稿 1审批中 2已通过 3已驳回 4已打款 5已作废）',
  submit_time        datetime        default null               comment '提交时间',
  pay_time           datetime        default null               comment '打款时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (expense_id),
  unique key uk_expense_no (expense_no),
  key idx_expense_applicant (applicant_id),
  key idx_expense_dept (dept_id),
  key idx_expense_status (status)
) engine=innodb auto_increment=100 comment = '报销单表';

-- ----------------------------
-- 10. 员工档案表
-- ----------------------------
drop table if exists hr_employee;
create table hr_employee (
  employee_id        bigint(20)      not null auto_increment    comment '档案ID',
  user_id            bigint(20)      default null               comment '关联系统账号user_id（可空）',
  employee_no        varchar(32)     not null                   comment '员工编号',
  name               varchar(64)     not null                   comment '姓名',
  gender             char(1)         default '0'                comment '性别（0男 1女 2未知）',
  birthday           date            default null               comment '出生日期',
  id_card            varchar(32)     default null               comment '身份证号（敏感，脱敏展示）',
  phone              varchar(20)     default null               comment '联系电话',
  dept_id            bigint(20)      default null               comment '部门',
  post_id            bigint(20)      default null               comment '岗位',
  education          varchar(30)     default null               comment '学历（字典 hr_education）',
  entry_date         date            default null               comment '入职日期',
  probation_end_date date            default null               comment '试用到期日',
  formal_date        date            default null               comment '转正日期',
  contract_expire_date date          default null               comment '劳动合同到期日（到期提醒）',
  base_salary        decimal(10,2)   default null               comment '基本工资（敏感，脱敏展示）',
  status             char(1)         not null default '1'       comment '状态（1在职 2试用期 3已离职）',
  leave_date         date            default null               comment '离职日期',
  leave_reason       varchar(500)    default null               comment '离职原因',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (employee_id),
  unique key uk_employee_no (employee_no),
  key idx_employee_user (user_id),
  key idx_employee_dept (dept_id)
) engine=innodb auto_increment=100 comment = '员工档案表';

-- ----------------------------
-- 11. 物资档案表
-- ----------------------------
drop table if exists biz_material;
create table biz_material (
  material_id        bigint(20)      not null auto_increment    comment '物资ID',
  material_no        varchar(32)     not null                   comment '物资编号',
  material_name      varchar(100)    not null                   comment '物资名称',
  spec               varchar(200)    default null               comment '规格型号',
  unit               varchar(20)     default null               comment '计量单位（字典 biz_measure_unit）',
  category           varchar(30)     default null               comment '分类（字典 biz_material_category）',
  safety_stock       decimal(10,2)   default 0                  comment '安全库存',
  ref_price          decimal(12,2)   default null               comment '参考单价',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (material_id),
  unique key uk_material_no (material_no)
) engine=innodb auto_increment=100 comment = '物资档案表';

-- ----------------------------
-- 12. 库存表
-- ----------------------------
drop table if exists biz_inventory;
create table biz_inventory (
  id                 bigint(20)      not null auto_increment    comment '主键',
  material_id        bigint(20)      not null                   comment '物资ID',
  quantity           decimal(10,2)   default 0                  comment '库存数量',
  avg_price          decimal(12,2)   default 0                  comment '移动加权平均价',
  update_time        datetime        default null               comment '更新时间',
  primary key (id),
  unique key uk_inventory_material (material_id)
) engine=innodb auto_increment=100 comment = '库存表';

-- ----------------------------
-- 13. 出入库单表
-- ----------------------------
drop table if exists biz_stock_order;
create table biz_stock_order (
  order_id           bigint(20)      not null auto_increment    comment '单据ID',
  order_no           varchar(32)     not null                   comment '单据编号（RK/CK+yyyyMMdd+4位流水）',
  order_type         char(1)         not null                   comment '单据类型（1入库 2出库）',
  source_type        varchar(30)     not null                   comment '来源（字典 biz_stock_source）',
  contract_id        bigint(20)      default null               comment '关联合同（项目领用必填）',
  applicant_id       bigint(20)      default null               comment '申请人',
  handler_id         bigint(20)      default null               comment '经办人（行政财务）',
  total_amount       decimal(12,2)   default 0                  comment '合计金额',
  status             char(1)         not null default '0'       comment '状态（0草稿 1审批中 2已出库/已完成 3已驳回 4已作废）',
  apply_time         datetime        default null               comment '申请时间',
  complete_time      datetime        default null               comment '完成时间',
  dept_id            bigint(20)      default null               comment '归属部门',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (order_id),
  unique key uk_order_no (order_no),
  key idx_stock_contract (contract_id),
  key idx_stock_applicant (applicant_id),
  key idx_stock_status (status)
) engine=innodb auto_increment=100 comment = '出入库单表';

-- ----------------------------
-- 14. 出入库明细表
-- ----------------------------
drop table if exists biz_stock_order_item;
create table biz_stock_order_item (
  item_id            bigint(20)      not null auto_increment    comment '明细ID',
  order_id           bigint(20)      not null                   comment '单据ID',
  material_id        bigint(20)      not null                   comment '物资ID',
  quantity           decimal(10,2)   not null                   comment '数量',
  unit_price         decimal(12,2)   default 0                  comment '单价',
  amount             decimal(12,2)   default 0                  comment '金额',
  primary key (item_id),
  key idx_stock_item_order (order_id),
  key idx_stock_item_material (material_id)
) engine=innodb auto_increment=100 comment = '出入库明细表';

-- ----------------------------
-- 15. 审批流定义表
-- ----------------------------
drop table if exists flow_definition;
create table flow_definition (
  flow_id            bigint(20)      not null auto_increment    comment '流程ID',
  flow_code          varchar(50)     not null                   comment '流程编码（唯一）',
  flow_name          varchar(100)    not null                   comment '流程名称',
  biz_type           varchar(50)     not null                   comment '关联业务类型（quote/contract/...）',
  status             char(1)         default '0'                comment '状态（0启用 1停用）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (flow_id),
  unique key uk_flow_code (flow_code)
) engine=innodb auto_increment=100 comment = '审批流定义表';

-- ----------------------------
-- 16. 审批流节点表
-- ----------------------------
drop table if exists flow_node;
create table flow_node (
  node_id            bigint(20)      not null auto_increment    comment '节点ID',
  flow_id            bigint(20)      not null                   comment '流程ID',
  node_order         int(4)          not null                   comment '节点顺序',
  node_name          varchar(100)    not null                   comment '节点名称',
  approver_type      char(1)         not null                   comment '审批人类型（1指定用户 2部门主管 3指定角色）',
  approver_value     varchar(200)    default null               comment '审批人（userId集合/角色ID）',
  multi_sign         char(1)         default '1'                comment '签署方式（1或签 2会签）',
  condition_amount   decimal(12,2)   default null               comment '金额阈值（单据金额≥该值节点生效）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (node_id),
  key idx_flow_node_flow (flow_id)
) engine=innodb auto_increment=100 comment = '审批流节点表';

-- ----------------------------
-- 17. 审批流实例表
-- ----------------------------
drop table if exists flow_instance;
create table flow_instance (
  instance_id        bigint(20)      not null auto_increment    comment '实例ID',
  flow_id            bigint(20)      not null                   comment '流程ID',
  flow_code          varchar(50)     not null                   comment '流程编码',
  biz_type           varchar(50)     not null                   comment '业务类型',
  biz_id             bigint(20)      not null                   comment '业务单据ID',
  biz_title          varchar(200)    default null               comment '业务单据快照标题',
  biz_amount         decimal(12,2)   default null               comment '业务单据金额（条件路由用）',
  current_node_id    bigint(20)      default null               comment '当前节点（NULL为已结束）',
  status             char(1)         not null default '1'       comment '状态（1进行中 2通过 3驳回 4撤销）',
  start_user_id      bigint(20)      not null                   comment '发起人',
  start_time         datetime        default null               comment '发起时间',
  end_time           datetime        default null               comment '结束时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (instance_id),
  key idx_instance_biz (biz_type, biz_id),
  key idx_instance_start_user (start_user_id),
  key idx_instance_status (status)
) engine=innodb auto_increment=100 comment = '审批流实例表';

-- ----------------------------
-- 18. 审批任务表
-- ----------------------------
drop table if exists flow_task;
create table flow_task (
  task_id            bigint(20)      not null auto_increment    comment '任务ID',
  instance_id        bigint(20)      not null                   comment '实例ID',
  node_id            bigint(20)      not null                   comment '节点ID',
  node_name          varchar(100)    default null               comment '节点名称（冗余展示）',
  approver_id        bigint(20)      not null                   comment '审批人user_id',
  status             char(1)         not null default '0'       comment '状态（0待审批 1同意 2驳回 3失效 4转交）',
  opinion            varchar(500)    default null               comment '审批意见',
  handle_time        datetime        default null               comment '处理时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (task_id),
  key idx_flow_task_instance (instance_id),
  key idx_flow_task_approver (approver_id, status),
  key idx_flow_task_node (node_id)
) engine=innodb auto_increment=100 comment = '审批任务表';

-- ----------------------------
-- 19. 业务附件表
-- ----------------------------
drop table if exists biz_attachment;
create table biz_attachment (
  attachment_id      bigint(20)      not null auto_increment    comment '附件ID',
  biz_type           varchar(50)     not null                   comment '业务类型（quote/contract/drawing/disclosure/expense/payment_voucher）',
  biz_id             bigint(20)      not null                   comment '业务单据ID',
  file_url           varchar(500)    not null                   comment '文件地址',
  file_name          varchar(200)    default null               comment '原始文件名',
  file_size          bigint(20)      default null               comment '文件大小（字节）',
  upload_user_id     bigint(20)      default null               comment '上传人',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (attachment_id),
  key idx_attachment_biz (biz_type, biz_id)
) engine=innodb auto_increment=100 comment = '业务附件表';

-- ----------------------------
-- 20. 站内消息表
-- ----------------------------
drop table if exists sys_message;
create table sys_message (
  message_id         bigint(20)      not null auto_increment    comment '消息ID',
  receiver_id        bigint(20)      not null                   comment '接收人user_id',
  msg_type           varchar(30)     default 'notice'           comment '类型（todo待办/result审批结果/notice公告/warn预警/remind提醒）',
  title              varchar(200)    default null               comment '标题',
  content            varchar(1000)   default null               comment '内容',
  route_path         varchar(200)    default null               comment '前端跳转路由',
  route_params       varchar(500)    default null               comment '跳转参数（JSON）',
  is_read            char(1)         default '0'                comment '已读（0未读 1已读）',
  read_time          datetime        default null               comment '阅读时间',
  create_time        datetime        default null               comment '创建时间',
  primary key (message_id),
  key idx_message_receiver (receiver_id, is_read)
) engine=innodb auto_increment=100 comment = '站内消息表';

-- ----------------------------
-- 21. 自定义提醒表
-- ----------------------------
drop table if exists biz_remind;
create table biz_remind (
  remind_id          bigint(20)      not null auto_increment    comment '提醒ID',
  title              varchar(100)    not null                   comment '提醒标题',
  content            varchar(500)    default null               comment '提醒内容',
  remind_time        datetime        not null                   comment '触发时间',
  repeat_type        char(1)         default '0'                comment '重复规则（0一次性 1每天 2每周 3每月）',
  relate_type        varchar(20)     default null               comment '关联业务类型（customer/quote/contract）',
  relate_id          bigint(20)      default null               comment '关联业务ID',
  relate_name        varchar(100)    default null               comment '关联业务名称（冗余展示）',
  receiver_id        bigint(20)      not null                   comment '接收人',
  status             char(1)         default '0'                comment '状态（0待触发 1已触发 2已取消）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (remind_id),
  key idx_remind_time (remind_time),
  key idx_remind_receiver (receiver_id),
  key idx_remind_status (status)
) engine=innodb auto_increment=100 comment = '自定义提醒表';

-- ----------------------------
-- 22. 施工工地表
-- ----------------------------
drop table if exists biz_site;
create table biz_site (
  site_id            bigint(20)      not null auto_increment    comment '工地ID',
  site_no            varchar(32)     not null                   comment '工地编号（GD+yyyyMMdd+4位流水）',
  contract_id        bigint(20)      not null                   comment '合同ID（一合同一工地）',
  customer_id        bigint(20)      default null               comment '客户ID',
  estate             varchar(100)    default ''                 comment '小区名称',
  area               decimal(10,2)   default null               comment '面积（m²）',
  key_password       varchar(100)    default ''                 comment '门锁密码（钥匙）',
  designer_id        bigint(20)      default null               comment '设计师user_id',
  designer_name      varchar(30)     default ''                 comment '设计师姓名（冗余）',
  supervisor_id      bigint(20)      default null               comment '监理user_id（项目经理）',
  supervisor_name    varchar(30)     default ''                 comment '监理姓名（冗余）',
  start_date         date            default null               comment '开工日期',
  finish_date        date            default null               comment '完工日期（全部阶段完成后自动记录）',
  status             char(1)         not null default '0'       comment '状态（0待开工 1施工中 2已完工）',
  dept_id            bigint(20)      default null               comment '归属部门',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (site_id),
  unique key uk_site_no (site_no),
  unique key uk_site_contract (contract_id),
  key idx_site_status (status)
) engine=innodb auto_increment=100 comment = '施工工地表';

-- ----------------------------
-- 23. 施工阶段表
-- ----------------------------
drop table if exists biz_progress_stage;
create table biz_progress_stage (
  stage_id           bigint(20)      not null auto_increment    comment '阶段ID',
  site_id            bigint(20)      not null                   comment '工地ID',
  stage_type         varchar(30)     not null                   comment '阶段类型（字典 biz_stage_type）',
  stage_name         varchar(64)     default null               comment '步骤名称（自定义小标题，空则取字典标签）',
  sort_order         int(4)          not null default 0         comment '顺序号',
  plan_start_date    date            default null               comment '计划开始日期',
  plan_end_date      date            default null               comment '计划结束日期',
  actual_start_date  date            default null               comment '实际开始日期',
  actual_end_date    date            default null               comment '实际完成日期',
  status             char(1)         not null default '0'       comment '状态（0未开始 1进行中 2已完成）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime        default null               comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime        default null               comment '更新时间',
  remark             varchar(500)    default null               comment '备注（现场情况说明）',
  primary key (stage_id),
  key idx_stage_site (site_id)
) engine=innodb auto_increment=100 comment = '施工阶段表';
