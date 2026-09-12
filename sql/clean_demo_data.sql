-- =====================================================
-- 清理大屏演示数据（dashboard_demo_data.sql 写入的测试数据）
-- 导入命令（务必带 utf8 参数）：
--   mysql -uroot -proot --default-character-set=utf8 ry-vue < clean_demo_data.sql
-- =====================================================

-- 安全提示：先执行 SELECT 确认数据范围
-- SELECT COUNT(*) FROM crm_customer WHERE remark LIKE '大屏演示%';

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 施工阶段（site_id 3-8）
DELETE FROM biz_progress_stage WHERE site_id BETWEEN 3 AND 8;

-- 2. 工地（site_id 3-8）
DELETE FROM biz_site WHERE site_id BETWEEN 3 AND 8;

-- 3. 收款记录（payment_id 109-120）
DELETE FROM biz_payment_record WHERE payment_id BETWEEN 109 AND 120;

-- 4. 收款计划（plan_id 120-143）
DELETE FROM biz_payment_plan WHERE plan_id BETWEEN 120 AND 143;

-- 还原老合同两笔计划日期（dashboard_demo_data.sql 修改了 plan_id=101 和 116）
UPDATE biz_payment_plan SET plan_date = NULL WHERE plan_id = 101 AND plan_date = '2026-08-28';
UPDATE biz_payment_plan SET plan_date = NULL WHERE plan_id = 116 AND plan_date = '2026-08-30';

-- 5. 合同（contract_id 105-116，还原 paid_amount）
UPDATE biz_contract SET paid_amount = 0 WHERE contract_id BETWEEN 105 AND 116;
DELETE FROM biz_contract WHERE contract_id BETWEEN 105 AND 116;

-- 6. 图纸（drawing_id 5-16）
DELETE FROM biz_drawing WHERE drawing_id BETWEEN 5 AND 16;

-- 7. 定金（deposit_id 2-13）
DELETE FROM biz_deposit WHERE deposit_id BETWEEN 2 AND 13;

-- 8. 报价单（quote_id 106-117）
DELETE FROM biz_quote WHERE quote_id BETWEEN 106 AND 117;

-- 9. 到访跟进记录（record_id 105-116）
DELETE FROM crm_follow_record WHERE record_id BETWEEN 105 AND 116;

-- 10. 客户（customer_id 106-129，remark 含 '大屏演示'）
DELETE FROM crm_customer WHERE customer_id BETWEEN 106 AND 129;

SET FOREIGN_KEY_CHECKS = 1;

-- 验证清理结果（预期全部为 0）
SELECT '=== 清理验证 ===' AS info;
SELECT 'crm_customer' AS tbl, COUNT(*) AS remain FROM crm_customer WHERE customer_id BETWEEN 106 AND 129
UNION ALL
SELECT 'crm_follow_record', COUNT(*) FROM crm_follow_record WHERE record_id BETWEEN 105 AND 116
UNION ALL
SELECT 'biz_quote', COUNT(*) FROM biz_quote WHERE quote_id BETWEEN 106 AND 117
UNION ALL
SELECT 'biz_deposit', COUNT(*) FROM biz_deposit WHERE deposit_id BETWEEN 2 AND 13
UNION ALL
SELECT 'biz_drawing', COUNT(*) FROM biz_drawing WHERE drawing_id BETWEEN 5 AND 16
UNION ALL
SELECT 'biz_contract', COUNT(*) FROM biz_contract WHERE contract_id BETWEEN 105 AND 116
UNION ALL
SELECT 'biz_payment_plan', COUNT(*) FROM biz_payment_plan WHERE plan_id BETWEEN 120 AND 143
UNION ALL
SELECT 'biz_payment_record', COUNT(*) FROM biz_payment_record WHERE payment_id BETWEEN 109 AND 120
UNION ALL
SELECT 'biz_site', COUNT(*) FROM biz_site WHERE site_id BETWEEN 3 AND 8
UNION ALL
SELECT 'biz_progress_stage', COUNT(*) FROM biz_progress_stage WHERE stage_id BETWEEN 22 AND 81;
