-- ============================================
-- Flyway V5 — 选课并发控制：课程容量 + 选课确认状态
-- ============================================
SET NAMES utf8mb4;

-- 1. 课程表：增加最大选课人数
ALTER TABLE `edu_course`
    ADD COLUMN `max_students` INT DEFAULT 100 COMMENT '课程最大容量'
    AFTER `course_hours`;

-- 2. 选课表：增加确认状态（0-预扣待确认 1-已确认 2-失败已回滚）
ALTER TABLE `edu_student_course`
    ADD COLUMN `confirm_status` INT DEFAULT 1 COMMENT '确认状态：0-预扣待确认 1-已确认 2-失败已回滚'
    AFTER `rel_status`;
