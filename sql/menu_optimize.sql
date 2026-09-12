-- =====================================================
-- 菜单结构优化（2026-08-26）
-- 1. 删除基础框架官网菜单
-- 2. 「市场管理」改名「客户管理」
-- 3. 取消「客情」子目录层级，5 个子菜单上提到客户管理下
-- 4. 报价查询/合同查询从客户管理移到项目管理（只读视图归口）
-- 5. 顶级菜单重排：业务在前，系统在后
-- 6. 子菜单 order_num 重排
-- =====================================================

-- 1. 删除基础框架官网及其角色关联
delete from sys_role_menu where menu_id = 4;
delete from sys_menu where menu_id = 4;

-- 2. 市场管理 → 客户管理
update sys_menu set menu_name = '客户管理', order_num = 2 where menu_id = 2000;

-- 3. 客情子目录下的菜单上提到客户管理（parent 2000→2000 本身已是，这里改 parent_id=2010 的）
update sys_menu set parent_id = 2000, order_num = 1 where menu_id = 2140; -- 客户档案
update sys_menu set parent_id = 2000, order_num = 2 where menu_id = 2141; -- 跟进动态
update sys_menu set parent_id = 2000, order_num = 3 where menu_id = 2020;  -- 报价管理
update sys_menu set parent_id = 2000, order_num = 4 where menu_id = 2167;  -- 定金管理
update sys_menu set parent_id = 2000, order_num = 5 where menu_id = 2156; -- 客情统计

-- 4. 删除客情子目录（2010）及其角色关联
delete from sys_role_menu where menu_id = 2010;
delete from sys_menu where menu_id = 2010;

-- 5. 报价查询/合同查询移到项目管理下（只读视图归口）
update sys_menu set parent_id = 2050, order_num = 5 where menu_id = 2057; -- 报价查询
update sys_menu set parent_id = 2050, order_num = 6 where menu_id = 2058; -- 合同查询

-- 6. 顶级菜单 order_num 重排（业务在前，系统在后）
update sys_menu set order_num = 0 where menu_id = 2121; -- 我的待办
update sys_menu set order_num = 1 where menu_id = 2122; -- 我的申请
-- 2000 客户管理 order_num=2（已设）
update sys_menu set order_num = 3 where menu_id = 2030; -- 合同管理
update sys_menu set order_num = 4 where menu_id = 2050; -- 项目管理
update sys_menu set order_num = 5 where menu_id = 2040; -- 收款管理
update sys_menu set order_num = 6 where menu_id = 2080; -- 行政财务
update sys_menu set order_num = 7 where menu_id = 2150; -- 提醒管理
update sys_menu set order_num = 10 where menu_id = 1;   -- 系统管理
update sys_menu set order_num = 11 where menu_id = 2;   -- 系统监控
update sys_menu set order_num = 12 where menu_id = 3;   -- 系统工具

-- 7. 项目管理子菜单 order_num 重排
update sys_menu set order_num = 1 where menu_id = 2060; -- 图纸管理
update sys_menu set order_num = 2 where menu_id = 2070; -- 技术交底
update sys_menu set order_num = 3 where menu_id = 2160; -- 施工进度
-- 2059 收款查询 order_num=4（已有）
-- 2057 报价查询 order_num=5（已设）
-- 2058 合同查询 order_num=6（已设）

-- 8. 客户管理图标改为 people（更直观）
update sys_menu set icon = 'people' where menu_id = 2000;
-- 项目管理图标改为 build（更直观）
update sys_menu set icon = 'build' where menu_id = 2050;
-- 合同管理加图标
update sys_menu set icon = 'documentation' where menu_id = 2030;
-- 收款管理加图标
update sys_menu set icon = 'money' where menu_id = 2040;
-- 提醒管理加图标
update sys_menu set icon = 'message' where menu_id = 2150;
-- 行政财务加图标
update sys_menu set icon = 'wallet' where menu_id = 2080;
