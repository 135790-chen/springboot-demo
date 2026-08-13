-- ============================================
-- Flyway V6 — 教务层级体系：学院 → 专业 → 培养方案 → 毕业审核
-- ============================================
SET NAMES utf8mb4;

-- ========== 1. 学院表 ==========
CREATE TABLE IF NOT EXISTS `edu_college` (
  `college_id`   BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `college_name` VARCHAR(128) NOT NULL COMMENT '学院名称',
  `college_code` VARCHAR(64)  NOT NULL COMMENT '学院编码',
  `college_status` INT        DEFAULT 1  COMMENT '状态：1-正常 0-禁用',
  `college_deleted` INT       DEFAULT 0  COMMENT '删除状态：1-删除 0-未删除',
  `college_remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `gmt_create`    DATETIME    NOT NULL COMMENT '创建时间',
  `gmt_modified`  DATETIME    NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`college_id`),
  UNIQUE INDEX `uk_college_code` (`college_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学院表';

-- ========== 2. 专业表 ==========
CREATE TABLE IF NOT EXISTS `edu_major` (
  `major_id`      BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `major_name`    VARCHAR(128) NOT NULL COMMENT '专业名称',
  `major_code`    VARCHAR(64)  NOT NULL COMMENT '专业编码',
  `college_id`    BIGINT       NOT NULL COMMENT '所属学院ID',
  `major_status`  INT          DEFAULT 1  COMMENT '状态：1-正常 0-禁用',
  `major_deleted` INT          DEFAULT 0  COMMENT '删除状态：1-删除 0-未删除',
  `major_remark`  VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `gmt_create`    DATETIME     NOT NULL COMMENT '创建时间',
  `gmt_modified`  DATETIME     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`major_id`),
  UNIQUE INDEX `uk_major_code` (`major_code`),
  INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专业表';

-- ========== 3. 培养方案表 ==========
CREATE TABLE IF NOT EXISTS `edu_training_plan` (
  `plan_id`                    BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `plan_name`                  VARCHAR(256) NOT NULL COMMENT '方案名称',
  `major_id`                   BIGINT       NOT NULL COMMENT '专业ID',
  `grade`                      VARCHAR(32)  NOT NULL COMMENT '适用年级（入学年份）',
  `version`                    INT          DEFAULT 1  COMMENT '版本号',
  `total_required_credits`     DECIMAL(5,1) NOT NULL COMMENT '必修课最低总学分',
  `major_elective_min_credits` DECIMAL(5,1) NOT NULL COMMENT '专业选修课最低学分',
  `general_elective_min_credits` DECIMAL(5,1) NOT NULL COMMENT '通识选修课最低学分',
  `plan_status`                INT          DEFAULT 1  COMMENT '状态：1-启用 0-停用',
  `plan_deleted`               INT          DEFAULT 0  COMMENT '删除状态：1-删除 0-未删除',
  `plan_remark`                VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `gmt_create`                 DATETIME     NOT NULL COMMENT '创建时间',
  `gmt_modified`               DATETIME     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`plan_id`),
  INDEX `idx_major_grade` (`major_id`, `grade`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='培养方案表';

-- ========== 4. 方案-课程关联表 ==========
CREATE TABLE IF NOT EXISTS `edu_plan_course` (
  `rel_id`          BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `plan_id`         BIGINT       NOT NULL COMMENT '培养方案ID',
  `course_id`       BIGINT       NOT NULL COMMENT '课程ID',
  `course_category` VARCHAR(32)  NOT NULL COMMENT '课程类别：REQUIRED-必修 MAJOR_ELECTIVE-专业选修 GENERAL_ELECTIVE-通识选修',
  `is_required`     INT          DEFAULT 1  COMMENT '是否必选：1-必选 0-可选',
  `gmt_create`      DATETIME     NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`rel_id`),
  UNIQUE INDEX `uk_plan_course` (`plan_id`, `course_id`),
  INDEX `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='培养方案课程关联表';

-- ========== 5. 毕业审核结果表 ==========
CREATE TABLE IF NOT EXISTS `edu_graduation_result` (
  `result_id`                     BIGINT        NOT NULL COMMENT '主键，雪花ID',
  `student_id`                    BIGINT        NOT NULL COMMENT '学生ID',
  `plan_id`                       BIGINT        NOT NULL COMMENT '培养方案ID',
  `total_earned_credits`          DECIMAL(5,1)  NOT NULL COMMENT '已获总学分',
  `required_earned_credits`       DECIMAL(5,1)  NOT NULL COMMENT '必修已获学分',
  `major_elective_earned_credits` DECIMAL(5,1)  NOT NULL COMMENT '专业选修已获学分',
  `general_elective_earned_credits` DECIMAL(5,1) NOT NULL COMMENT '通识选修已获学分',
  `passed`                        INT           NOT NULL COMMENT '是否通过：1-通过 0-不通过',
  `missing_items`                 JSON          DEFAULT NULL COMMENT '缺失项明细（JSON格式）',
  `review_time`                   DATETIME      NOT NULL COMMENT '审核时间',
  `gmt_create`                    DATETIME      NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`result_id`),
  INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='毕业审核结果表';

-- ========== 6. 扩展班级表：新增学院和专业外键 ==========
ALTER TABLE `edu_class`
    ADD COLUMN `college_id` BIGINT DEFAULT NULL COMMENT '学院ID'
    AFTER `class_code`,
    ADD COLUMN `major_id` BIGINT DEFAULT NULL COMMENT '专业ID'
    AFTER `college_id`;


-- ========== 7. 种子数据 ==========

-- 学院
INSERT INTO `edu_college` (`college_id`, `college_name`, `college_code`, `college_status`, `college_deleted`, `gmt_create`, `gmt_modified`) VALUES
(1001, '计算机学院',         'CS',  1, 0, NOW(), NOW()),
(1002, '数学与统计学院',     'MATH', 1, 0, NOW(), NOW()),
(1003, '外国语学院',         'FL',  1, 0, NOW(), NOW());

-- 专业
INSERT INTO `edu_major` (`major_id`, `major_name`, `major_code`, `college_id`, `major_status`, `major_deleted`, `gmt_create`, `gmt_modified`) VALUES
(2001, '计算机科学与技术',   'CS-CST',   1001, 1, 0, NOW(), NOW()),
(2002, '软件工程',           'CS-SE',    1001, 1, 0, NOW(), NOW()),
(2003, '数据科学与大数据',   'CS-DS',    1001, 1, 0, NOW(), NOW()),
(2004, '数学与应用数学',     'MATH-AM',  1002, 1, 0, NOW(), NOW()),
(2005, '英语',               'FL-EN',    1003, 1, 0, NOW(), NOW());

-- 培养方案：计算机科学与技术 2024 级
INSERT INTO `edu_training_plan` (`plan_id`, `plan_name`, `major_id`, `grade`, `version`,
  `total_required_credits`, `major_elective_min_credits`, `general_elective_min_credits`,
  `plan_status`, `plan_deleted`, `gmt_create`, `gmt_modified`) VALUES
(3001, '计算机科学与技术专业 2024 版培养方案', 2001, '2024', 1,
 100, 20, 10,
 1, 0, NOW(), NOW()),
(3002, '软件工程专业 2024 版培养方案', 2002, '2024', 1,
 95, 25, 10,
 1, 0, NOW(), NOW());

-- 方案课程关联（计算机科学与技术 2024）
INSERT INTO `edu_plan_course` (`rel_id`, `plan_id`, `course_id`, `course_category`, `is_required`, `gmt_create`) VALUES
(4001, 3001, 1,  'REQUIRED',         1, NOW()),
(4002, 3001, 2,  'REQUIRED',         1, NOW()),
(4003, 3001, 3,  'REQUIRED',         1, NOW()),
(4004, 3001, 4,  'MAJOR_ELECTIVE',   0, NOW()),
(4005, 3001, 5,  'MAJOR_ELECTIVE',   0, NOW()),
(4006, 3001, 6,  'GENERAL_ELECTIVE', 0, NOW()),
(4007, 3001, 7,  'GENERAL_ELECTIVE', 0, NOW());

-- 将现有班级关联到计算机学院
UPDATE `edu_class` SET `college_id` = 1001, `major_id` = 2001 WHERE `college_id` IS NULL;
