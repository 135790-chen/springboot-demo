SET NAMES utf8mb4;

-- 为培养方案课程关联表增加学期顺序字段，支持按时间逻辑排列课程
ALTER TABLE `edu_plan_course`
    ADD COLUMN `semester_order` INT DEFAULT NULL COMMENT '学期顺序: 1-第一学期, 2-第二学期... NULL=未指定'
    AFTER `is_required`;
