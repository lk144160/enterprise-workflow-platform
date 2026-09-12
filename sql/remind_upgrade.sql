-- ----------------------------------------------------------------------------
-- 自定义提醒功能升级脚本（在 ry-vue 库执行，需带 --default-character-set=utf8）
-- ----------------------------------------------------------------------------

-- 1. 提醒表
drop table if exists biz_remind;
create table biz_remind (
  remind_id     bigint(20)    not null auto_increment  comment '提醒ID',
  title         varchar(100)  not null                 comment '提醒标题',
  content       varchar(500)  default null             comment '提醒内容',
  remind_time   datetime      not null                 comment '触发时间',
  repeat_type   char(1)       default '0'              comment '重复规则（0一次性 1每天 2每周 3每月）',
  relate_type   varchar(20)   default null             comment '关联业务类型（customer/quote/contract）',
  relate_id     bigint(20)    default null             comment '关联业务ID',
  relate_name   varchar(100)  default null             comment '关联业务名称（冗余展示）',
  receiver_id   bigint(20)    not null                 comment '接收人',
  status        char(1)       default '0'              comment '状态（0待触发 1已触发 2已取消）',
  create_by     varchar(64)   default ''               comment '创建者',
  create_time   datetime      default null             comment '创建时间',
  update_by     varchar(64)   default ''               comment '更新者',
  update_time   datetime      default null             comment '更新时间',
  remark        varchar(500)  default null             comment '备注',
  primary key (remind_id),
  key idx_remind_time (remind_time),
  key idx_remind_receiver (receiver_id),
  key idx_remind_status (status)
) engine=innodb auto_increment=100 comment = '自定义提醒表';

-- 2. 菜单（顶级：提醒管理 2150 + 按钮 2151-2155）
insert into sys_menu values ('2150', '提醒管理', '0', '4', 'remind', 'biz/remind/index', '', '', 1, 0, 'C', '0', '0', 'biz:remind:list', 'message', 'admin', sysdate(), '', null, '自定义提醒菜单');
insert into sys_menu values ('2151', '提醒查询', '2150', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:remind:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2152', '提醒新增', '2150', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:remind:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2153', '提醒修改', '2150', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:remind:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2154', '提醒删除', '2150', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:remind:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values ('2155', '提醒取消', '2150', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:remind:cancel', '#', 'admin', sysdate(), '', null, '');

-- 角色分配（提醒为个人功能，所有角色可用）
insert into sys_role_menu values (2, 2150);
insert into sys_role_menu values (2, 2151);
insert into sys_role_menu values (2, 2152);
insert into sys_role_menu values (2, 2153);
insert into sys_role_menu values (2, 2154);
insert into sys_role_menu values (2, 2155);
insert into sys_role_menu values (10, 2150);
insert into sys_role_menu values (10, 2151);
insert into sys_role_menu values (10, 2152);
insert into sys_role_menu values (10, 2153);
insert into sys_role_menu values (10, 2154);
insert into sys_role_menu values (10, 2155);
insert into sys_role_menu values (11, 2150);
insert into sys_role_menu values (11, 2151);
insert into sys_role_menu values (11, 2152);
insert into sys_role_menu values (11, 2153);
insert into sys_role_menu values (11, 2154);
insert into sys_role_menu values (11, 2155);
insert into sys_role_menu values (12, 2150);
insert into sys_role_menu values (12, 2151);
insert into sys_role_menu values (12, 2152);
insert into sys_role_menu values (12, 2153);
insert into sys_role_menu values (12, 2154);
insert into sys_role_menu values (12, 2155);
insert into sys_role_menu values (13, 2150);
insert into sys_role_menu values (13, 2151);
insert into sys_role_menu values (13, 2152);
insert into sys_role_menu values (13, 2153);
insert into sys_role_menu values (13, 2154);
insert into sys_role_menu values (13, 2155);
insert into sys_role_menu values (14, 2150);
insert into sys_role_menu values (14, 2151);
insert into sys_role_menu values (14, 2152);
insert into sys_role_menu values (14, 2153);
insert into sys_role_menu values (14, 2154);
insert into sys_role_menu values (14, 2155);
insert into sys_role_menu values (15, 2150);
insert into sys_role_menu values (15, 2151);
insert into sys_role_menu values (15, 2152);
insert into sys_role_menu values (15, 2153);
insert into sys_role_menu values (15, 2154);
insert into sys_role_menu values (15, 2155);
insert into sys_role_menu values (100, 2150);
insert into sys_role_menu values (100, 2151);
insert into sys_role_menu values (100, 2152);
insert into sys_role_menu values (100, 2153);
insert into sys_role_menu values (100, 2154);
insert into sys_role_menu values (100, 2155);

-- 3. 定时任务（每分钟扫描到点提醒，misfire立即补触发）
insert into sys_job values (104, '自定义提醒扫描', 'DEFAULT', 'bizRemindTask.scanCustomRemind()', '0 * * * * ?', '1', '1', '0', 'admin', sysdate(), '', null, '每分钟扫描到点的自定义提醒，经通知渠道推送（站内信，预留短信等）');

-- 4. 字典
insert into sys_dict_type values (126, '提醒重复规则', 'biz_remind_repeat', '0', 'admin', sysdate(), '', null, '自定义提醒重复规则');
insert into sys_dict_data values (214, 0, '一次性', '0', 'biz_remind_repeat', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values (215, 1, '每天', '1', 'biz_remind_repeat', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values (216, 2, '每周', '2', 'biz_remind_repeat', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values (217, 3, '每月', '3', 'biz_remind_repeat', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_type values (127, '提醒状态', 'biz_remind_status', '0', 'admin', sysdate(), '', null, '自定义提醒状态');
insert into sys_dict_data values (218, 0, '待触发', '0', 'biz_remind_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values (219, 1, '已触发', '1', 'biz_remind_status', '', 'info', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values (220, 2, '已取消', '2', 'biz_remind_status', '', 'danger', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_type values (128, '站内消息类型', 'sys_msg_type', '0', 'admin', sysdate(), '', null, '站内消息类型');
insert into sys_dict_data values (221, 0, '待办', 'todo', 'sys_msg_type', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values (222, 1, '审批结果', 'result', 'sys_msg_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values (223, 2, '通知', 'notice', 'sys_msg_type', '', 'info', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values (224, 3, '预警', 'warn', 'sys_msg_type', '', 'danger', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values (225, 4, '提醒', 'remind', 'sys_msg_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
