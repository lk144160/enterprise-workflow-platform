-- ----------------------------------------------------------------------------
-- 施工进度优化升级脚本
-- 1. biz_progress_stage 增加 stage_name（自定义步骤小标题，空则显示字典标签）
-- 2. 新增工地详情隐藏路由页面（菜单2174，跳转新页面展示客户全量信息）
-- 3. 各施工进度可见角色补授详情页路由权限
-- 用法：mysql -uroot -p --default-character-set=utf8 ry-vue < site_detail_upgrade.sql
-- ----------------------------------------------------------------------------

-- 1. 步骤自定义名称字段
alter table biz_progress_stage add column stage_name varchar(64) default null comment '步骤名称（自定义小标题，空则取字典标签）' after stage_type;

-- 2. 工地详情隐藏路由（挂项目管理目录下，visible='1' 不在侧边栏显示；完整路由 /design/site/detail/{siteId}）
insert into sys_menu values('2174', '工地详情页', '2050', '9', 'site/detail/:siteId', 'biz/site/detail', '', '', 1, 0, 'C', '1', '0', 'biz:site:query', '#', 'admin', sysdate(), '', null, '工地详情独立页面（隐藏路由）');

-- 3. 角色权限补授（与2160施工进度可见角色一致：12设计师/13设计经理/14行政财务/15老板/16项目经理）
insert into sys_role_menu values (12, 2174);
insert into sys_role_menu values (13, 2174);
insert into sys_role_menu values (14, 2174);
insert into sys_role_menu values (15, 2174);
insert into sys_role_menu values (16, 2174);

-- 3.1 安全兜底：拥有施工进度(2160)但缺项目管理目录(2050)的角色补授目录，避免隐藏路由无法注册
insert into sys_role_menu (role_id, menu_id)
select distinct rm.role_id, 2050 from sys_role_menu rm
where rm.menu_id = 2160
  and not exists (select 1 from sys_role_menu x where x.role_id = rm.role_id and x.menu_id = 2050);

-- 4. 施工阶段字典补充「自定义步骤」类型（手动新增的步骤归入该类型）
insert into sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
values (11, '自定义步骤', 'custom', 'biz_stage_type', '', 'info', 'N', '0', 'admin', sysdate(), '', null, '手动新增的施工步骤');
