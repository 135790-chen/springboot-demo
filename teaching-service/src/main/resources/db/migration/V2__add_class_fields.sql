-- ============================================
-- Flyway V2 — 补齐 Clazz 实体新增字段
-- Clazz 实体新增了 counselor_id 字段，teaching_db 建表时遗漏
-- ============================================
SET NAMES utf8mb4;

ALTER TABLE `edu_class`
    ADD COLUMN `counselor_id` BIGINT DEFAULT NULL COMMENT '辅导员ID（关联 sys_counselor）'
    AFTER `major_id`;
