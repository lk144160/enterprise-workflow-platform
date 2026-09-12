-- =====================================================
-- 装修业务运营平台 施工进度管理升级脚本（工地档案+施工阶段）
-- 日期：2026-08-23
-- 新增：biz_site 工地表 / biz_progress_stage 阶段表
--       字典 biz_site_status / biz_stage_status / biz_stage_type
--       菜单 2160-2166（挂"项目管理"目录 2050）
-- =====================================================

-- 1. 工地表
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

-- 2. 施工阶段表
drop table if exists biz_progress_stage;
create table biz_progress_stage (
  stage_id           bigint(20)      not null auto_increment    comment '阶段ID',
  site_id            bigint(20)      not null                   comment '工地ID',
  stage_type         varchar(30)     not null                   comment '阶段类型（字典 biz_stage_type）',
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

-- 3. 字典
insert into sys_dict_type values(132, '工地状态',   'biz_site_status',  '0', 'admin', sysdate(), '', null, '施工工地状态列表');
insert into sys_dict_type values(133, '阶段状态',   'biz_stage_status', '0', 'admin', sysdate(), '', null, '施工阶段状态列表');
insert into sys_dict_type values(134, '施工阶段',   'biz_stage_type',   '0', 'admin', sysdate(), '', null, '装修标准工序列表');

insert into sys_dict_data values(242, 1, '待开工', '0', 'biz_site_status',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(243, 2, '施工中', '1', 'biz_site_status',  '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(244, 3, '已完工', '2', 'biz_site_status',  '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');

insert into sys_dict_data values(245, 1, '未开始', '0', 'biz_stage_status', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(246, 2, '进行中', '1', 'biz_stage_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(247, 3, '已完成', '2', 'biz_stage_status', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');

insert into sys_dict_data values(248, 1,  '开工准备', 'prepare',      'biz_stage_type', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(249, 2,  '拆改工程', 'demolition',   'biz_stage_type', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(250, 3,  '水电改造', 'hydropower',   'biz_stage_type', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(251, 4,  '防水工程', 'waterproof',   'biz_stage_type', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(252, 5,  '泥瓦工程', 'masonry',      'biz_stage_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(253, 6,  '木工工程', 'carpentry',    'biz_stage_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(254, 7,  '油漆工程', 'paint',        'biz_stage_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(255, 8,  '安装阶段', 'installation', 'biz_stage_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(256, 9,  '软装进场', 'soft_furnish', 'biz_stage_type', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(257, 10, '竣工验收', 'acceptance',   'biz_stage_type', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');

-- 4. 菜单（挂项目管理目录 2050，排在交底之后）
update sys_menu set order_num = 4 where menu_id = 2057;
update sys_menu set order_num = 5 where menu_id = 2058;
update sys_menu set order_num = 6 where menu_id = 2059;
insert into sys_menu values('2160', '施工进度', '2050', '3', 'site', 'biz/site/index', '', '', 1, 0, 'C', '0', '0', 'biz:site:list', 'time', 'admin', sysdate(), '', null, '施工进度管理菜单');
insert into sys_menu values('2161', '工地查询', '2160', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2162', '工地新增', '2160', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2163', '工地修改', '2160', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:edit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2164', '工地删除', '2160', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2165', '阶段推进', '2160', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:advance', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2166', '密码查看', '2160', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:viewkey', '#', 'admin', sysdate(), '', null, '');

-- 5. 角色菜单分配
-- 项目经理16：全部操作（生成计划/推进/照片/密码）
insert into sys_role_menu values (16, 2160);
insert into sys_role_menu values (16, 2161);
insert into sys_role_menu values (16, 2162);
insert into sys_role_menu values (16, 2163);
insert into sys_role_menu values (16, 2164);
insert into sys_role_menu values (16, 2165);
insert into sys_role_menu values (16, 2166);
-- 老板15：查看+密码
insert into sys_role_menu values (15, 2160);
insert into sys_role_menu values (15, 2161);
insert into sys_role_menu values (15, 2166);
-- 设计师12：查看+密码
insert into sys_role_menu values (12, 2160);
insert into sys_role_menu values (12, 2161);
insert into sys_role_menu values (12, 2166);
-- 设计部经理13：查看
insert into sys_role_menu values (13, 2160);
insert into sys_role_menu values (13, 2161);
-- 行政兼财务14：查看
insert into sys_role_menu values (14, 2160);
insert into sys_role_menu values (14, 2161);
