-- ============================================
-- Flyway V3 — 补齐 edu_major 缺失的 director_id 列
-- organization-service 建表时有此列，teaching-service V1 遗漏
-- ============================================
SET NAMES utf8mb4;

ALTER TABLE `edu_major`
    ADD COLUMN `director_id` BIGINT DEFAULT NULL COMMENT '专业负责人ID（关联 sys_user）'
    AFTER `college_id`;
