-- ----------------------------------------------------------------------------
-- 客户到访记录增强升级脚本（在 ry-vue 库执行，需带 --default-character-set=utf8）
-- 注：若部分语句已执行过会报重复错误，可忽略对应报错
-- ----------------------------------------------------------------------------

-- 1. 跟进记录表增加到访专属字段
alter table crm_follow_record add column visit_count       int(4)       default 1    comment '到访人数（仅到访记录）'    after next_follow_time;
alter table crm_follow_record add column companion         varchar(100) default null comment '同行人描述（仅到访记录）'  after visit_count;
alter table crm_follow_record add column reception_user_id bigint(20)   default null comment '接待人user_id（仅到访记录）' after companion;
alter table crm_follow_record add column visit_purpose     varchar(30)  default null comment '到访目的（字典 crm_visit_purpose，仅到访记录）' after reception_user_id;
alter table crm_follow_record add column feedback          varchar(30)  default null comment '客户反馈（字典 crm_visit_feedback，仅到访记录）' after visit_purpose;

-- 2. 跟进方式字典：到店 -> 到访
update sys_dict_data set dict_label = '到访' where dict_code = 111 and dict_type = 'crm_follow_type';

-- 3. 新增字典：到访目的、客户到访反馈
insert into sys_dict_type values(129, '到访目的',     'crm_visit_purpose',  '0', 'admin', sysdate(), '', null, '客户到访目的列表');
insert into sys_dict_type values(130, '客户到访反馈', 'crm_visit_feedback', '0', 'admin', sysdate(), '', null, '客户到访反馈列表');

insert into sys_dict_data values(226, 1, '看方案',     '1', 'crm_visit_purpose',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(227, 2, '看主材',     '2', 'crm_visit_purpose',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(228, 3, '比价',       '3', 'crm_visit_purpose',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(229, 4, '签约洽谈',   '4', 'crm_visit_purpose',  '', 'default', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(230, 1, '非常感兴趣', '1', 'crm_visit_feedback', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(231, 2, '比较认可',   '2', 'crm_visit_feedback', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(232, 3, '一般观望',   '3', 'crm_visit_feedback', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '');
insert into sys_dict_data values(233, 4, '仍有顾虑',   '4', 'crm_visit_feedback', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '');
