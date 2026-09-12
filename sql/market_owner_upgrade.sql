-- ----------------------------------------------------------------------------
-- 市场部调整升级脚本
-- 1. 客户"业务员"由用户绑定改为直接文本录入（owner_id → owner_name）
-- 2. 移除市场部业务员角色（角色10）及 market 测试账号，客户录入职责归市场部经理
-- 3. 移除"线索转移"按钮菜单（业务员可直接在修改中调整）
-- 用法：mysql -uroot -p --default-character-set=utf8 ry-vue < market_owner_upgrade.sql
-- ----------------------------------------------------------------------------

-- 1. 业务员改为文本字段：先加列，迁移历史数据（按旧绑定用户的昵称回填），再删旧列
alter table crm_customer add column owner_name varchar(64) default null comment '业务员姓名（直接录入）' after owner_id;
update crm_customer c left join sys_user u on u.user_id = c.owner_id set c.owner_name = u.nick_name where c.owner_id is not null;
alter table crm_customer drop column owner_id;

-- 2. 移除市场部业务员角色（角色10）及其菜单授权
delete from sys_role_menu where role_id = 10;
delete from sys_user_role where role_id = 10;
delete from sys_role where role_id = 10;

-- 3. 删除 market 测试账号（市场部业务员）
delete from sys_user_role where user_id = 100;
delete from sys_user where user_id = 100 and user_name = 'market';

-- 4. 移除"线索转移"按钮菜单（2016）及各角色授权
delete from sys_role_menu where menu_id = 2016;
delete from sys_menu where menu_id = 2016 and perms = 'crm:customer:transfer';
