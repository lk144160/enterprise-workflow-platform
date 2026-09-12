-- ----------------------------------------------------------------------------
-- 装修业务运营平台 初始化数据脚本
-- 前置：ry_20260417.sql + quartz.sql + business_schema.sql
-- ----------------------------------------------------------------------------

-- ----------------------------
-- 1. 部门（示例组织，全部数据均为虚构占位内容）
-- ----------------------------
insert into sys_dept values(200, 0,   '0',       '装修业务运营平台',   0, 'gm',       '00000000100', 'org@example.invalid', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(201, 200, '0,200',   '市场部',     1, 'mmanager', '',            '',          '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(202, 200, '0,200',   '设计部',     2, 'director', '',            '',          '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(203, 200, '0,200',   '行政财务部', 3, 'finance',  '',            '',          '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(204, 200, '0,200',   '项目部',     4, 'project',  '',            '',          '0', '0', 'admin', sysdate(), '', null);

-- ----------------------------
-- 2. 角色（data_scope: 1全部 2本部门 5仅本人）
-- ----------------------------
insert into sys_role values('11', '市场部经理',   'market_manager',  6, '2', 1, 1, '0', '0', 'admin', sysdate(), '', null, '市场部经理（客户信息录入）');
insert into sys_role values('12', '设计师',       'design_staff',    7, '5', 1, 1, '0', '0', 'admin', sysdate(), '', null, '设计师（负责报价与合同）');
insert into sys_role values('13', '设计部经理',   'design_director', 8, '2', 1, 1, '0', '0', 'admin', sysdate(), '', null, '设计部经理');
insert into sys_role values('14', '行政兼财务',   'admin_finance',   9, '2', 1, 1, '0', '0', 'admin', sysdate(), '', null, '行政兼财务');
insert into sys_role values('15', '老板',         'gm',             10, '1', 1, 1, '0', '0', 'admin', sysdate(), '', null, '老板（终审）');
insert into sys_role values('16', '项目经理',     'project_manager', 11, '1', 1, 1, '0', '0', 'admin', sysdate(), '', null, '项目经理（项目视角只读+出库申请）');
insert into sys_role values('17', '设计助理',     'design_assistant', 12, '5', 1, 1, '0', '0', 'admin', sysdate(), '', null, '设计助理（协助制图）');

-- ----------------------------
-- 2.1 角色-菜单权限矩阵（岗位制，2026-08-23 与 role_upgrade.sql 保持一致）
-- 普通角色（基础框架示例用户）：仅提醒
insert into sys_role_menu values (2, 2150);
insert into sys_role_menu values (2, 2151);
insert into sys_role_menu values (2, 2152);
insert into sys_role_menu values (2, 2153);
insert into sys_role_menu values (2, 2154);
insert into sys_role_menu values (2, 2155);
-- 老板（15）：全部业务菜单
insert into sys_role_menu VALUES
(15,2000),(15,2010),(15,2140),(15,2011),(15,2012),(15,2013),(15,2014),(15,2015),(15,2141),
(15,2156),(15,2157),
(15,2020),(15,2021),(15,2022),(15,2023),(15,2024),(15,2025),(15,2026),(15,2027),
(15,2030),(15,2031),(15,2032),(15,2033),(15,2034),(15,2035),(15,2036),(15,2037),(15,2038),
(15,2040),(15,2041),(15,2042),(15,2043),(15,2044),(15,2045),
(15,2050),(15,2060),(15,2061),(15,2062),(15,2063),(15,2070),(15,2071),(15,2072),(15,2073),(15,2074),(15,2075),
(15,2057),(15,2064),(15,2058),(15,2065),(15,2059),(15,2066),
(15,2160),(15,2161),(15,2166),(15,2174),
(15,2167),(15,2168),(15,2169),(15,2170),(15,2171),(15,2172),(15,2173),
(15,2080),(15,2081),(15,2082),(15,2083),(15,2084),(15,2085),(15,2086),(15,2087),
(15,2090),(15,2091),(15,2092),(15,2093),(15,2094),(15,2095),(15,2096),(15,2097),
(15,2100),(15,2101),(15,2102),(15,2103),(15,2104),(15,2105),
(15,2110),(15,2111),(15,2112),(15,2113),(15,2114),(15,2115),
(15,2120),(15,2121),(15,2122),
(15,2150),(15,2151),(15,2152),(15,2153),(15,2154),(15,2155);
-- 市场部经理（11）：客户全部+统计、报价/合同/收款只读、报销本人
insert into sys_role_menu VALUES
(11,2000),(11,2010),(11,2140),(11,2011),(11,2012),(11,2013),(11,2014),(11,2015),(11,2141),
(11,2156),(11,2157),
(11,2020),(11,2021),
(11,2167),(11,2168),(11,2169),(11,2172),(11,2173),
(11,2030),(11,2031),
(11,2040),(11,2041),
(11,2080),(11,2081),(11,2082),(11,2083),(11,2084),(11,2086),
(11,2120),(11,2121),(11,2122),
(11,2150),(11,2151),(11,2152),(11,2153),(11,2154),(11,2155);
-- 设计部经理（13）：客户/统计查看、报价全部、合同增改提、收款视图、图纸全部、交底全部
insert into sys_role_menu VALUES
(13,2000),(13,2010),(13,2140),(13,2011),(13,2141),(13,2156),(13,2157),
(13,2020),(13,2021),(13,2022),(13,2023),(13,2024),(13,2025),(13,2026),(13,2027),
(13,2167),(13,2168),(13,2169),(13,2172),(13,2173),
(13,2030),(13,2031),(13,2032),(13,2033),(13,2035),
(13,2050),(13,2059),(13,2066),
(13,2060),(13,2061),(13,2062),(13,2063),
(13,2070),(13,2071),(13,2072),(13,2073),(13,2074),(13,2075),
(13,2160),(13,2161),(13,2174),
(13,2080),(13,2081),(13,2082),(13,2083),(13,2084),(13,2086),
(13,2120),(13,2121),(13,2122),
(13,2150),(13,2151),(13,2152),(13,2153),(13,2154),(13,2155);
-- 设计师（12）：客户查看、报价增改提、合同增改提+完工登记、图纸查传、交底增改提
insert into sys_role_menu VALUES
(12,2000),(12,2010),(12,2140),(12,2011),(12,2141),
(12,2020),(12,2021),(12,2022),(12,2023),(12,2025),(12,2026),
(12,2167),(12,2168),(12,2169),(12,2172),
(12,2030),(12,2031),(12,2032),(12,2033),(12,2035),(12,2036),
(12,2050),(12,2060),(12,2061),(12,2062),
(12,2070),(12,2071),(12,2072),(12,2073),(12,2075),
(12,2160),(12,2161),(12,2166),(12,2174),
(12,2080),(12,2081),(12,2082),(12,2083),(12,2084),(12,2086),
(12,2120),(12,2121),(12,2122),
(12,2150),(12,2151),(12,2152),(12,2153),(12,2154),(12,2155);
-- 设计助理（17）：客户查看、图纸查传、交底查看
insert into sys_role_menu VALUES
(17,2000),(17,2010),(17,2140),(17,2011),(17,2141),
(17,2050),(17,2060),(17,2061),(17,2062),
(17,2070),(17,2071),
(17,2080),(17,2081),(17,2082),(17,2083),(17,2084),(17,2086),
(17,2120),(17,2121),(17,2122),
(17,2150),(17,2151),(17,2152),(17,2153),(17,2154),(17,2155);
-- 项目经理（16）：图纸/交底查看、报价/合同/收款视图、物资台账查、出库申请、报销本人
insert into sys_role_menu VALUES
(16,2050),(16,2060),(16,2061),(16,2070),(16,2071),
(16,2057),(16,2064),(16,2058),(16,2065),(16,2059),(16,2066),
(16,2160),(16,2161),(16,2162),(16,2163),(16,2164),(16,2165),(16,2166),(16,2174),
(16,2080),(16,2100),(16,2101),(16,2110),(16,2111),(16,2113),
(16,2081),(16,2082),(16,2083),(16,2084),(16,2086),
(16,2120),(16,2121),(16,2122),
(16,2150),(16,2151),(16,2152),(16,2153),(16,2154),(16,2155);
-- 行政兼财务（14）：客户/统计查看、合同查+归档、收款全部、报销全部、员工档案、物资出入库
insert into sys_role_menu VALUES
(14,2000),(14,2010),(14,2140),(14,2011),(14,2141),(14,2156),(14,2157),
(14,2030),(14,2031),(14,2037),
(14,2040),(14,2041),(14,2042),(14,2043),(14,2044),(14,2045),
(14,2080),(14,2081),(14,2082),(14,2083),(14,2084),(14,2085),(14,2086),(14,2087),
(14,2090),(14,2091),(14,2092),(14,2093),(14,2094),(14,2095),(14,2096),(14,2097),
(14,2100),(14,2101),(14,2102),(14,2103),(14,2104),(14,2105),
(14,2110),(14,2111),(14,2112),(14,2114),(14,2115),
(14,2160),(14,2161),(14,2174),
(14,2167),(14,2168),(14,2172),
(14,2120),(14,2121),(14,2122),
(14,2150),(14,2151),(14,2152),(14,2153),(14,2154),(14,2155);

-- ----------------------------
-- 3. 业务菜单
-- ----------------------------
-- 一级目录
insert into sys_menu values('2000', '市场管理', '0', '5', 'market',  null, '', '', 1, 0, 'M', '0', '0', '', 'peoples',   'admin', sysdate(), '', null, '市场管理目录');
insert into sys_menu values('2050', '项目管理', '0', '6', 'design',  null, '', '', 1, 0, 'M', '0', '0', '', 'build',     'admin', sysdate(), '', null, '项目管理目录（图纸/交底/商务视图）');
insert into sys_menu values('2080', '行政财务', '0', '7', 'finance', null, '', '', 1, 0, 'M', '0', '0', '', 'money',     'admin', sysdate(), '', null, '行政财务目录');
insert into sys_menu values('2120', '审批中心', '0', '8', 'flow',    null, '', '', 1, 0, 'M', '0', '0', '', 'clipboard', 'admin', sysdate(), '', null, '审批中心目录');

-- 客情（二级目录：客户档案 + 跟进动态 + 报价管理）
insert into sys_menu values('2010', '客情',     '2000', '1', 'customer', '',                      '', '', 1, 0, 'M', '0', '0', '', 'peoples',         'admin', sysdate(), '', null, '客情目录');
insert into sys_menu values('2140', '客户档案', '2010', '1', 'profile',  'crm/customer/index',    '', '', 1, 0, 'C', '0', '0', 'crm:customer:list', 'people',          'admin', sysdate(), '', null, '客户档案菜单');
insert into sys_menu values('2141', '跟进动态', '2010', '2', 'follow',   'crm/follow/index',      '', '', 1, 0, 'C', '0', '0', 'crm:customer:list', 'chat-dot-round',  'admin', sysdate(), '', null, '跟进动态菜单');
insert into sys_menu values('2011', '线索查询', '2140', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'crm:customer:query',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2012', '线索新增', '2140', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'crm:customer:add',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2013', '线索修改', '2140', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'crm:customer:edit',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2014', '线索删除', '2140', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'crm:customer:remove',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2015', '线索跟进', '2140', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'crm:customer:follow',   '#', 'admin', sysdate(), '', null, '');

-- 报价管理（挂在客情目录下）
insert into sys_menu values('2020', '报价管理', '2010', '3', 'quote',       'biz/quote/index',      '', '', 1, 0, 'C', '0', '0', 'biz:quote:list', 'documentation', 'admin', sysdate(), '', null, '报价管理菜单');
insert into sys_menu values('2021', '报价查询', '2020', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:quote:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2022', '报价新增', '2020', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:quote:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2023', '报价修改', '2020', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:quote:edit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2024', '报价删除', '2020', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:quote:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2025', '报价提交', '2020', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:quote:submit',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2026', '报价撤销', '2020', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:quote:cancel',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2027', '报价导出', '2020', '7', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:quote:export',  '#', 'admin', sysdate(), '', null, '');

-- 客情统计（挂在客情目录下）
insert into sys_menu values('2156', '客情统计', '2010', '4', 'report', 'crm/report/index', '', '', 1, 0, 'C', '0', '0', 'crm:report:list', 'chart', 'admin', sysdate(), '', null, '客情统计菜单');
insert into sys_menu values('2157', '客情统计导出', '2156', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'crm:report:export', '#', 'admin', sysdate(), '', null, '');

-- 合同管理
insert into sys_menu values('2030', '合同管理', '2000', '2', 'contract',    'biz/contract/index',   '', '', 1, 0, 'C', '0', '0', 'biz:contract:list', 'tab', 'admin', sysdate(), '', null, '合同管理菜单');
insert into sys_menu values('2031', '合同查询', '2030', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:contract:query',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2032', '合同新增', '2030', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:contract:add',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2033', '合同修改', '2030', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:contract:edit',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2034', '合同删除', '2030', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:contract:remove',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2035', '合同提交', '2030', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:contract:submit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2036', '完工登记', '2030', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:contract:finish',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2037', '合同归档', '2030', '7', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:contract:archive',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2038', '合同终止', '2030', '8', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:contract:terminate', '#', 'admin', sysdate(), '', null, '');

-- 收款管理
insert into sys_menu values('2040', '收款管理', '2000', '3', 'payment',     'biz/payment/index',    '', '', 1, 0, 'C', '0', '0', 'biz:payment:list', 'money', 'admin', sysdate(), '', null, '收款管理菜单');
insert into sys_menu values('2041', '收款计划查询', '2040', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:payment:plan',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2042', '收款计划调整', '2040', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:payment:planEdit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2043', '收款登记', '2040', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:payment:register', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2044', '收款冲正', '2040', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:payment:reverse',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2045', '收款减免', '2040', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:payment:reduce',   '#', 'admin', sysdate(), '', null, '');

-- 提醒管理（个人功能，所有角色可用）
insert into sys_menu values('2150', '提醒管理', '0',   '4', 'remind', 'biz/remind/index', '', '', 1, 0, 'C', '0', '0', 'biz:remind:list', 'message', 'admin', sysdate(), '', null, '自定义提醒菜单');
insert into sys_menu values('2151', '提醒查询', '2150', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:remind:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2152', '提醒新增', '2150', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:remind:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2153', '提醒修改', '2150', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:remind:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2154', '提醒删除', '2150', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:remind:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2155', '提醒取消', '2150', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:remind:cancel', '#', 'admin', sysdate(), '', null, '');

-- 图纸管理
insert into sys_menu values('2060', '图纸管理', '2050', '1', 'drawing',     'biz/drawing/index',    '', '', 1, 0, 'C', '0', '0', 'biz:drawing:list', 'upload', 'admin', sysdate(), '', null, '图纸管理菜单');
insert into sys_menu values('2061', '图纸查询', '2060', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:drawing:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2062', '图纸上传', '2060', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:drawing:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2063', '图纸确认', '2060', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:drawing:confirm', '#', 'admin', sysdate(), '', null, '');

-- 技术交底
insert into sys_menu values('2070', '技术交底', '2050', '2', 'disclosure',  'biz/disclosure/index', '', '', 1, 0, 'C', '0', '0', 'biz:disclosure:list', 'guide', 'admin', sysdate(), '', null, '技术交底菜单');
insert into sys_menu values('2071', '交底查询', '2070', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:disclosure:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2072', '交底新增', '2070', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:disclosure:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2073', '交底修改', '2070', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:disclosure:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2074', '交底删除', '2070', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:disclosure:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2075', '交底提交', '2070', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:disclosure:submit', '#', 'admin', sysdate(), '', null, '');

-- 设计部关联查询（报价/合同/收款只读视图，复用市场部页面，无操作按钮权限）
insert into sys_menu values('2057', '报价查询', '2050', '4', 'quoteView',    'biz/quote/index',    '', '', 1, 0, 'C', '0', '0', 'biz:quote:list',    'documentation', 'admin', sysdate(), '', null, '设计部报价只读视图');
insert into sys_menu values('2064', '报价详情', '2057', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:quote:query',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2058', '合同查询', '2050', '5', 'contractView', 'biz/contract/index', '', '', 1, 0, 'C', '0', '0', 'biz:contract:list', 'tab', 'admin', sysdate(), '', null, '设计部合同只读视图');
insert into sys_menu values('2065', '合同详情', '2058', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:contract:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2059', '收款查询', '2050', '6', 'paymentView',  'biz/payment/index',  '', '', 1, 0, 'C', '0', '0', 'biz:payment:list',  'money', 'admin', sysdate(), '', null, '设计部收款只读视图');
insert into sys_menu values('2066', '收款详情', '2059', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:payment:plan',   '#', 'admin', sysdate(), '', null, '');

-- 施工进度管理
insert into sys_menu values('2160', '施工进度', '2050', '3', 'site', 'biz/site/index', '', '', 1, 0, 'C', '0', '0', 'biz:site:list', 'time', 'admin', sysdate(), '', null, '施工进度管理菜单');
insert into sys_menu values('2161', '工地查询', '2160', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2162', '工地新增', '2160', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2163', '工地修改', '2160', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:edit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2164', '工地删除', '2160', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2165', '阶段推进', '2160', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:advance', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2166', '密码查看', '2160', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:site:viewkey', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2174', '工地详情页', '2050', '9', 'site/detail/:siteId', 'biz/site/detail', '', '', 1, 0, 'C', '1', '0', 'biz:site:query', '#', 'admin', sysdate(), '', null, '工地详情独立页面（隐藏路由）');

-- 定金管理
insert into sys_menu values('2167', '定金管理', '2010', '3', 'deposit', 'biz/deposit/index', '', '', 1, 0, 'C', '0', '0', 'biz:deposit:list', 'money', 'admin', sysdate(), '', null, '定金管理菜单');
insert into sys_menu values('2168', '定金查询', '2167', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:deposit:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2169', '定金登记', '2167', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:deposit:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2170', '定金修改', '2167', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:deposit:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2171', '定金删除', '2167', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:deposit:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2172', '签约抵扣', '2167', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:deposit:deduct', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2173', '申请退还', '2167', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:deposit:refund', '#', 'admin', sysdate(), '', null, '');

-- 报销管理
insert into sys_menu values('2081', '报销管理', '2080', '1', 'expense',     'biz/expense/index',    '', '', 1, 0, 'C', '0', '0', 'biz:expense:list', 'expense', 'admin', sysdate(), '', null, '报销管理菜单');
insert into sys_menu values('2082', '报销查询', '2081', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:expense:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2083', '报销新增', '2081', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:expense:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2084', '报销修改', '2081', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:expense:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2085', '报销删除', '2081', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:expense:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2086', '报销提交', '2081', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:expense:submit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2087', '打款登记', '2081', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:expense:pay',    '#', 'admin', sysdate(), '', null, '');

-- 员工档案
insert into sys_menu values('2090', '员工档案', '2080', '2', 'employee',    'hr/employee/index',    '', '', 1, 0, 'C', '0', '0', 'hr:employee:list', 'user', 'admin', sysdate(), '', null, '员工档案菜单');
insert into sys_menu values('2091', '档案查询', '2090', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'hr:employee:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2092', '档案新增', '2090', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'hr:employee:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2093', '档案修改', '2090', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'hr:employee:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2094', '档案删除', '2090', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'hr:employee:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2095', '入职办理', '2090', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'hr:employee:entry',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2096', '离职登记', '2090', '6', '', '', '', '', 1, 0, 'F', '0', '0', 'hr:employee:leave',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2097', '档案导出', '2090', '7', '', '', '', '', 1, 0, 'F', '0', '0', 'hr:employee:export', '#', 'admin', sysdate(), '', null, '');

-- 物资台账
insert into sys_menu values('2100', '物资台账', '2080', '3', 'material',    'biz/material/index',   '', '', 1, 0, 'C', '0', '0', 'biz:material:list', 'shopping', 'admin', sysdate(), '', null, '物资台账菜单');
insert into sys_menu values('2101', '物资查询', '2100', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:material:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2102', '物资新增', '2100', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:material:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2103', '物资修改', '2100', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:material:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2104', '物资删除', '2100', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:material:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2105', '物资导出', '2100', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:material:export', '#', 'admin', sysdate(), '', null, '');

-- 出入库管理
insert into sys_menu values('2110', '出入库管理', '2080', '4', 'stock',     'biz/stock/index',      '', '', 1, 0, 'C', '0', '0', 'biz:stock:list', 'component', 'admin', sysdate(), '', null, '出入库管理菜单');
insert into sys_menu values('2111', '出入库查询', '2110', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:stock:query',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2112', '入库登记',   '2110', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:stock:in',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2113', '出库申请',   '2110', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:stock:apply',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2114', '出库审批',   '2110', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:stock:audit',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2115', '库存预警',   '2110', '5', '', '', '', '', 1, 0, 'F', '0', '0', 'biz:stock:inventory', '#', 'admin', sysdate(), '', null, '');

-- 审批中心
insert into sys_menu values('2121', '我的待办', '2120', '1', 'todo', 'flow/todo/index', '', '', 1, 0, 'C', '0', '0', 'flow:todo:list', 'todo', 'admin', sysdate(), '', null, '我的待办菜单');
insert into sys_menu values('2122', '我的申请', '2120', '2', 'mine', 'flow/mine/index', '', '', 1, 0, 'C', '0', '0', 'flow:mine:list', 'form', 'admin', sysdate(), '', null, '我的申请菜单');

-- 流程配置（挂在系统管理下）
insert into sys_menu values('2130', '流程配置', '1', '7', 'flowDefinition', 'flow/definition/index', '', '', 1, 0, 'C', '0', '0', 'system:flow:list', 'tree', 'admin', sysdate(), '', null, '流程配置菜单');
insert into sys_menu values('2131', '流程查询', '2130', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'system:flow:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2132', '流程新增', '2130', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'system:flow:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2133', '流程修改', '2130', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'system:flow:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2134', '流程删除', '2130', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'system:flow:remove', '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 4. 角色-菜单权限（不写死绑定）
-- ----------------------------
-- 角色菜单权限已在上方 2.1 节按岗位制写入，后续可由管理员在
-- 【系统管理 → 角色管理 → 修改角色 → 菜单权限】界面调整。
-- admin 为超级管理员，天然拥有全部菜单与操作权限。
-- 岗位制权限矩阵（2026-08-23）：
--   11 市场部经理  ：客户信息录入+全部管理、客情统计、报价/合同/收款只读、报销、审批中心
--   12 设计师      ：客户查看、报价增改提、合同增改提+完工、图纸查传、交底增改提、报销、审批中心
--   13 设计部经理  ：客户/统计查看、报价全部、合同增改提、收款视图、图纸全部、交底全部、报销、审批中心
--   14 行政兼财务  ：客户查看、合同查+归档、收款全部、报销全部(含打款)、员工档案、物资出入库、审批中心
--   15 老板        ：全部业务菜单
--   16 项目经理    ：图纸/交底查看、报价/合同/收款视图、物资台账查、出库申请、报销、审批中心
--   17 设计助理    ：客户查看、图纸查传、交底查看、报销、审批中心

-- ----------------------------
-- 5. 测试用户（密码 admin123456）
-- ----------------------------
insert into sys_user values(101, 201, 'mmanager',  '市场经理', '00', 'mmanager@example.invalid',  '00000000102', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '市场部经理（客户信息录入）');
insert into sys_user values(102, 202, 'designer',  '示例设计师','00', 'designer@example.invalid',  '00000000103', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '设计师');
insert into sys_user values(103, 202, 'director',  '设计经理', '00', 'director@example.invalid',  '00000000104', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '设计部经理');
insert into sys_user values(104, 203, 'finance',   '行政财务', '00', 'finance@example.invalid',   '00000000105', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '行政财务');
insert into sys_user values(105, 200, 'gm',        '总经理',   '00', 'gm@example.invalid',        '00000000106', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '总经理');
insert into sys_user values(107, 204, 'project',   '项目经理', '00', 'project@example.invalid',   '00000000107', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '项目经理账号');

insert into sys_user_role values (101, 11);
insert into sys_user_role values (102, 12);
insert into sys_user_role values (103, 13);
insert into sys_user_role values (104, 14);
insert into sys_user_role values (105, 15);
insert into sys_user_role values (107, 16);

-- ----------------------------
-- 6. 业务字典
-- ----------------------------
insert into sys_dict_type values(100, '线索来源',     'crm_customer_source',    '0', 'admin', sysdate(), '', null, '线索来源列表');
insert into sys_dict_type values(101, '意向等级',     'crm_intention_level',    '0', 'admin', sysdate(), '', null, '意向等级列表');
insert into sys_dict_type values(102, '跟进方式',     'crm_follow_type',        '0', 'admin', sysdate(), '', null, '跟进方式列表');
insert into sys_dict_type values(103, '线索状态',     'crm_customer_status',    '0', 'admin', sysdate(), '', null, '线索状态列表');
insert into sys_dict_type values(104, '审批通用状态', 'biz_audit_status',       '0', 'admin', sysdate(), '', null, '审批通用状态列表');
insert into sys_dict_type values(105, '合同状态',     'biz_contract_status',    '0', 'admin', sysdate(), '', null, '合同状态列表');
insert into sys_dict_type values(106, '收款期次名称', 'biz_pay_period_name',    '0', 'admin', sysdate(), '', null, '收款期次名称列表');
insert into sys_dict_type values(107, '收款期次状态', 'biz_pay_period_status',  '0', 'admin', sysdate(), '', null, '收款期次状态列表');
insert into sys_dict_type values(108, '期次到期触发', 'biz_pay_trigger',        '0', 'admin', sysdate(), '', null, '期次到期触发列表');
insert into sys_dict_type values(109, '收款方式',     'biz_pay_type',           '0', 'admin', sysdate(), '', null, '收款方式列表');
insert into sys_dict_type values(111, '图纸类型',     'biz_drawing_type',       '0', 'admin', sysdate(), '', null, '图纸类型列表');
insert into sys_dict_type values(112, '图纸状态',     'biz_drawing_status',     '0', 'admin', sysdate(), '', null, '图纸状态列表');
insert into sys_dict_type values(113, '设计类型',     'biz_plan_type',          '0', 'admin', sysdate(), '', null, '设计类型列表');
insert into sys_dict_type values(114, '费用类型',     'biz_expense_type',       '0', 'admin', sysdate(), '', null, '费用类型列表');
insert into sys_dict_type values(115, '报销状态',     'biz_expense_status',     '0', 'admin', sysdate(), '', null, '报销状态列表');
insert into sys_dict_type values(116, '物资分类',     'biz_material_category',  '0', 'admin', sysdate(), '', null, '物资分类列表');
insert into sys_dict_type values(117, '出入库来源',   'biz_stock_source',       '0', 'admin', sysdate(), '', null, '出入库来源列表');
insert into sys_dict_type values(118, '出入库单状态', 'biz_stock_status',       '0', 'admin', sysdate(), '', null, '出入库单状态列表');
insert into sys_dict_type values(119, '计量单位',     'biz_measure_unit',       '0', 'admin', sysdate(), '', null, '计量单位列表');
insert into sys_dict_type values(120, '出入库类型',   'biz_stock_order_type',   '0', 'admin', sysdate(), '', null, '出入库类型列表');
insert into sys_dict_type values(121, '审批人类型',   'flow_approver_type',     '0', 'admin', sysdate(), '', null, '审批人类型列表');
insert into sys_dict_type values(122, '审批实例状态', 'flow_instance_status',   '0', 'admin', sysdate(), '', null, '审批实例状态列表');
insert into sys_dict_type values(123, '审批任务状态', 'flow_task_status',       '0', 'admin', sysdate(), '', null, '审批任务状态列表');
insert into sys_dict_type values(124, '员工状态',     'hr_employee_status',     '0', 'admin', sysdate(), '', null, '员工状态列表');
insert into sys_dict_type values(125, '学历',         'hr_education',           '0', 'admin', sysdate(), '', null, '学历列表');
insert into sys_dict_type values(126, '提醒重复规则', 'biz_remind_repeat',      '0', 'admin', sysdate(), '', null, '自定义提醒重复规则');
insert into sys_dict_type values(127, '提醒状态',     'biz_remind_status',      '0', 'admin', sysdate(), '', null, '自定义提醒状态');
insert into sys_dict_type values(135, '定金状态',     'biz_deposit_status',     '0', 'admin', sysdate(), '', null, '定金记录状态');
insert into sys_dict_type values(132, '站内消息类型', 'sys_msg_type',           '0', 'admin', sysdate(), '', null, '站内消息类型');
insert into sys_dict_type values(129, '到访目的',     'crm_visit_purpose',      '0', 'admin', sysdate(), '', null, '客户到访目的列表');
insert into sys_dict_type values(130, '客户到访反馈', 'crm_visit_feedback',     '0', 'admin', sysdate(), '', null, '客户到访反馈列表');
insert into sys_dict_type values(131, '审批业务类型', 'flow_biz_type',          '0', 'admin', sysdate(), '', null, '审批业务类型列表');
insert into sys_dict_type values(136, '工地状态',     'biz_site_status',        '0', 'admin', sysdate(), '', null, '施工工地状态列表');
insert into sys_dict_type values(133, '阶段状态',     'biz_stage_status',       '0', 'admin', sysdate(), '', null, '施工阶段状态列表');
insert into sys_dict_type values(134, '施工阶段',     'biz_stage_type',         '0', 'admin', sysdate(), '', null, '装修标准工序列表');

insert into sys_dict_data values(100, 1, '电话咨询',   '1', 'crm_customer_source',   '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(101, 2, '门店到访',   '2', 'crm_customer_source',   '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(102, 3, '网络推广',   '3', 'crm_customer_source',   '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(103, 4, '老客转介绍', '4', 'crm_customer_source',   '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(104, 5, '异业合作',   '5', 'crm_customer_source',   '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(105, 1, 'A级(高)',    'A', 'crm_intention_level',   '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(106, 2, 'B级(中)',    'B', 'crm_intention_level',   '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(107, 3, 'C级(低)',    'C', 'crm_intention_level',   '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(108, 4, 'D级(无效)',  'D', 'crm_intention_level',   '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(109, 1, '电话',       '1', 'crm_follow_type',       '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(110, 2, '微信',       '2', 'crm_follow_type',       '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(111, 3, '到访',       '3', 'crm_follow_type',       '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(112, 4, '量房',       '4', 'crm_follow_type',       '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(113, 1, '待跟进', '1', 'crm_customer_status',   '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(114, 2, '跟进中', '2', 'crm_customer_status',   '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(115, 3, '已转化', '3', 'crm_customer_status',   '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(116, 4, '已签约', '4', 'crm_customer_status',   '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(117, 5, '已流失', '5', 'crm_customer_status',   '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(118, 1, '草稿',   '0', 'biz_audit_status',      '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(119, 2, '审批中', '1', 'biz_audit_status',      '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(120, 3, '已通过', '2', 'biz_audit_status',      '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(121, 4, '已驳回', '3', 'biz_audit_status',      '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(122, 5, '已作废', '4', 'biz_audit_status',      '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(123, 1, '待审批', '0', 'biz_contract_status',   '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(124, 2, '审批中', '1', 'biz_contract_status',   '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(125, 3, '已生效', '2', 'biz_contract_status',   '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(126, 4, '已完工', '3', 'biz_contract_status',   '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(127, 5, '已归档', '4', 'biz_contract_status',   '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(128, 6, '已终止', '5', 'biz_contract_status',   '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(129, 1, '签约款', '1', 'biz_pay_period_name',   '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(130, 2, '设计款', '2', 'biz_pay_period_name',   '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(131, 3, '施工款', '3', 'biz_pay_period_name',   '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(132, 4, '尾款',   '4', 'biz_pay_period_name',   '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(133, 1, '未到期', '0', 'biz_pay_period_status', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(134, 2, '待收款', '1', 'biz_pay_period_status', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(135, 3, '已结清', '2', 'biz_pay_period_status', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(136, 4, '已减免', '3', 'biz_pay_period_status', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(137, 5, '已作废', '4', 'biz_pay_period_status', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(138, 1, '合同生效',   'contract_effect',     'biz_pay_trigger', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '第1期签约款');
insert into sys_dict_data values(139, 2, '设计完成',   'design_finish',       'biz_pay_trigger', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '第2期设计款');
insert into sys_dict_data values(140, 3, '交底确认',   'disclosure_confirm',  'biz_pay_trigger', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '第3期施工款');
insert into sys_dict_data values(141, 4, '完工登记',   'finish_register',     'biz_pay_trigger', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '第4期尾款');
insert into sys_dict_data values(142, 1, '转账',   '1', 'biz_pay_type',          '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(143, 2, '现金',   '2', 'biz_pay_type',          '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(144, 3, 'POS',    '3', 'biz_pay_type',          '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(145, 4, '微信',   '4', 'biz_pay_type',          '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(146, 5, '支付宝', '5', 'biz_pay_type',          '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(152, 1, '平面布置图', '1', 'biz_drawing_type',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(153, 2, '效果图',     '2', 'biz_drawing_type',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(154, 3, '水电图',     '3', 'biz_drawing_type',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(155, 4, '施工图',     '4', 'biz_drawing_type',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(156, 5, '节点大样图', '5', 'biz_drawing_type',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(157, 1, '待确认', '0', 'biz_drawing_status',    '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(158, 2, '已确认', '1', 'biz_drawing_status',    '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(159, 3, '已作废', '2', 'biz_drawing_status',    '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(160, 1, '全案设计', '1', 'biz_plan_type',       '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(161, 2, '局部设计', '2', 'biz_plan_type',       '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(162, 3, '软装设计', '3', 'biz_plan_type',       '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(163, 1, '差旅费',   '1', 'biz_expense_type',    '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(164, 2, '招待费',   '2', 'biz_expense_type',    '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(165, 3, '材料采购', '3', 'biz_expense_type',    '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(166, 4, '办公费',   '4', 'biz_expense_type',    '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(167, 5, '其他',     '5', 'biz_expense_type',    '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(168, 1, '草稿',   '0', 'biz_expense_status',    '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(169, 2, '审批中', '1', 'biz_expense_status',    '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(170, 3, '已通过', '2', 'biz_expense_status',    '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(171, 4, '已驳回', '3', 'biz_expense_status',    '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(172, 5, '已打款', '4', 'biz_expense_status',    '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(173, 6, '已作废', '5', 'biz_expense_status',    '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(174, 1, '主材',     '1', 'biz_material_category', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(175, 2, '辅材',     '2', 'biz_material_category', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(176, 3, '软装',     '3', 'biz_material_category', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(177, 4, '办公物资', '4', 'biz_material_category', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(178, 1, '采购入库',     'purchase_in',  'biz_stock_source', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(179, 2, '项目领用出库', 'project_out',  'biz_stock_source', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(180, 3, '退货入库',     'return_in',    'biz_stock_source', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(181, 4, '报废出库',     'scrap_out',    'biz_stock_source', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(182, 1, '草稿',   '0', 'biz_stock_status',      '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(183, 2, '审批中', '1', 'biz_stock_status',      '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(184, 3, '已完成', '2', 'biz_stock_status',      '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(185, 4, '已驳回', '3', 'biz_stock_status',      '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(186, 5, '已作废', '4', 'biz_stock_status',      '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(187, 1, '项',     '1', 'biz_measure_unit',      '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(188, 2, '平方米', '2', 'biz_measure_unit',      '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(189, 3, '米',     '3', 'biz_measure_unit',      '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(190, 4, '件',     '4', 'biz_measure_unit',      '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(191, 5, '套',     '5', 'biz_measure_unit',      '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(192, 6, '桶',     '6', 'biz_measure_unit',      '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(193, 1, '入库', '1', 'biz_stock_order_type',    '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(194, 2, '出库', '2', 'biz_stock_order_type',    '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(195, 1, '指定用户', '1', 'flow_approver_type',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(196, 2, '部门主管', '2', 'flow_approver_type',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(197, 3, '指定角色', '3', 'flow_approver_type',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(198, 1, '进行中', '1', 'flow_instance_status',  '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(199, 2, '通过',   '2', 'flow_instance_status',  '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(200, 3, '驳回',   '3', 'flow_instance_status',  '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(201, 4, '撤销',   '4', 'flow_instance_status',  '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(202, 1, '待审批', '0', 'flow_task_status',      '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(203, 2, '同意',   '1', 'flow_task_status',      '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(204, 3, '驳回',   '2', 'flow_task_status',      '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(205, 4, '失效',   '3', 'flow_task_status',      '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(206, 5, '转交',   '4', 'flow_task_status',      '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(207, 1, '在职',   '1', 'hr_employee_status',    '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(208, 2, '试用期', '2', 'hr_employee_status',    '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(209, 3, '已离职', '3', 'hr_employee_status',    '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(210, 1, '高中',     '1', 'hr_education',       '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(211, 2, '大专',     '2', 'hr_education',       '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(212, 3, '本科',     '3', 'hr_education',       '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(213, 4, '硕士及以上','4', 'hr_education',      '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(214, 0, '一次性',    '0',      'biz_remind_repeat', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(215, 1, '每天',      '1',      'biz_remind_repeat', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(216, 2, '每周',      '2',      'biz_remind_repeat', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(217, 3, '每月',      '3',      'biz_remind_repeat', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(218, 0, '待触发',    '0',      'biz_remind_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(219, 1, '已触发',    '1',      'biz_remind_status', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(220, 2, '已取消',    '2',      'biz_remind_status', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(258, 0, '已收定金',  '0',      'biz_deposit_status', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(259, 1, '已抵扣',    '1',      'biz_deposit_status', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(260, 2, '退还审批中','2',      'biz_deposit_status', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(261, 3, '已退还',    '3',      'biz_deposit_status', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(225, 4, '提醒',      'remind', 'sys_msg_type',      '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(226, 1, '看方案',     '1', 'crm_visit_purpose',   '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(227, 2, '看主材',     '2', 'crm_visit_purpose',   '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(228, 3, '比价',       '3', 'crm_visit_purpose',   '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(229, 4, '签约洽谈',   '4', 'crm_visit_purpose',   '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(230, 1, '非常感兴趣', '1', 'crm_visit_feedback',  '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(231, 2, '比较认可',   '2', 'crm_visit_feedback',  '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(232, 3, '一般观望',   '3', 'crm_visit_feedback',  '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(233, 4, '仍有顾虑',   '4', 'crm_visit_feedback',  '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(236, 1, '报价审批',     'quote',          'flow_biz_type', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(237, 2, '合同审批',     'contract',       'flow_biz_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(238, 3, '收款减免审批', 'payment_reduce', 'flow_biz_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(239, 4, '交底审批',     'disclosure',     'flow_biz_type', '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(240, 5, '报销审批',     'expense',        'flow_biz_type', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(241, 6, '出库审批',     'stock_out',      'flow_biz_type', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(262, 7, '定金退还审批', 'deposit_refund', 'flow_biz_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '');
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
insert into sys_dict_data values(263, 11, '自定义步骤', 'custom',     'biz_stage_type', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '手动新增的施工步骤');

-- ----------------------------
-- 7. 流程定义与节点（approver_type 3=指定角色，approver_value=角色ID）
-- ----------------------------
insert into flow_definition values(1, 'QUOTE_APPROVAL',         '报价审批',     'quote',          '0', 'admin', sysdate(), '', null, '设计部经理/管理员或签');
insert into flow_definition values(2, 'CONTRACT_APPROVAL',      '合同审批',     'contract',       '0', 'admin', sysdate(), '', null, '设计部经理/管理员或签');
insert into flow_definition values(3, 'PAYMENT_REDUCE_APPROVAL','收款减免审批', 'payment_reduce', '0', 'admin', sysdate(), '', null, '设计部经理/管理员或签');
insert into flow_definition values(4, 'DISCLOSURE_APPROVAL',    '技术交底审批', 'disclosure',     '0', 'admin', sysdate(), '', null, '设计部经理/管理员或签');
insert into flow_definition values(5, 'EXPENSE_APPROVAL',       '报销审批',     'expense',        '0', 'admin', sysdate(), '', null, '设计部经理/管理员或签');
insert into flow_definition values(6, 'STOCK_OUT_APPROVAL',     '出库审批',     'stock_out',      '0', 'admin', sysdate(), '', null, '设计部经理/管理员或签');
insert into flow_definition values(7, 'DEPOSIT_REFUND_APPROVAL','定金退还审批', 'deposit_refund', '0', 'admin', sysdate(), '', null, '设计部经理/管理员或签');

insert into flow_node values(1, 1, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', sysdate(), '', null, '报价审批-或签');
insert into flow_node values(2, 2, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', sysdate(), '', null, '合同审批-或签');
insert into flow_node values(3, 3, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', sysdate(), '', null, '减免审批-或签');
insert into flow_node values(4, 4, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', sysdate(), '', null, '交底审批-或签');
insert into flow_node values(5, 5, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', sysdate(), '', null, '报销审批-或签');
insert into flow_node values(6, 6, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', sysdate(), '', null, '出库审批-或签');
insert into flow_node values(7, 7, 1, '设计部经理/管理员', '3', '13,1', '1', null, 'admin', sysdate(), '', null, '定金退还-或签');

-- ----------------------------
-- 8. 参数配置
-- ----------------------------
insert into sys_config values(100, '四期收款默认比例', 'pay.plan.ratios', '30,30,30,10', 'Y', 'admin', sysdate(), '', null, '四期收款默认比例模板，逗号分隔，合计必须为100');

-- ----------------------------
-- 9. 定时任务（业务提醒扫描）
-- ----------------------------
insert into sys_job values(100, '线索超期跟进扫描', 'DEFAULT', 'bizRemindTask.scanOverdueFollow()',   '0 0 9 * * ?', '3', '1', '0', 'admin', sysdate(), '', null, '待跟进/跟进中且7天未跟进的线索，提醒负责人与部门市场主管');
insert into sys_job values(101, '收款到期与逾期扫描', 'DEFAULT', 'bizRemindTask.scanPaymentDue()',     '0 0 9 * * ?', '3', '1', '0', 'admin', sysdate(), '', null, '待收款且计划收款日已到的期次，提醒合同负责人与行政财务');
insert into sys_job values(102, '劳动合同到期提醒', 'DEFAULT', 'bizRemindTask.scanContractExpire()',  '0 0 9 * * ?', '3', '1', '0', 'admin', sysdate(), '', null, '30天内到期的在职员工合同，提醒行政财务');
insert into sys_job values(103, '库存预警扫描',     'DEFAULT', 'bizRemindTask.scanLowStock()',        '0 0 * * * ?', '3', '1', '0', 'admin', sysdate(), '', null, '每小时扫描低于安全库存的物资，提醒行政财务');
insert into sys_job values(104, '自定义提醒扫描',   'DEFAULT', 'bizRemindTask.scanCustomRemind()',     '0 * * * * ?', '1', '1', '0', 'admin', sysdate(), '', null, '每分钟扫描到点的自定义提醒，经通知渠道推送（站内信，预留短信等）');
