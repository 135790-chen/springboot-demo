-- ============================================
-- Flyway V2 — 用户表增加 student_id 映射
-- 学生用户通过此字段关联 edu_student 表
-- ============================================
ALTER TABLE `user`
    ADD COLUMN `student_id` BIGINT DEFAULT NULL COMMENT '关联的学生ID（学生角色才有值）'
    AFTER `role`;
