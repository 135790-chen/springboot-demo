-- ============================================
-- Flyway V3 — 数据库重构：统一 edu_* 命名规范
-- 包含：雪花ID主键、软删除、状态字段、审计字段
-- 依赖: V1, V2 需先执行
-- ============================================
SET NAMES utf8mb4;

-- ========== 1. 班级表 edu_class ==========
CREATE TABLE IF NOT EXISTS `edu_class` (
  `class_id`      BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `class_name`    VARCHAR(128) NOT NULL COMMENT '班级名称',
  `class_code`    VARCHAR(64)  NOT NULL COMMENT '班级编码',
  `grade`         VARCHAR(32)  DEFAULT NULL COMMENT '年级',
  `major`         VARCHAR(128) DEFAULT NULL COMMENT '专业',
  `class_sort`    INT          DEFAULT 0  COMMENT '显示顺序',
  `class_status`  INT          DEFAULT 1  COMMENT '状态：1-正常 0-禁用',
  `class_deleted` INT          DEFAULT 0  COMMENT '删除状态：1-删除 0-未删除',
  `class_remark`  VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `gmt_create`    DATETIME     NOT NULL COMMENT '创建时间',
  `gmt_modified`  DATETIME     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`class_id`),
  UNIQUE INDEX `uk_class_code` (`class_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='班级表';

-- ========== 2. 学生表 edu_student ==========
CREATE TABLE IF NOT EXISTS `edu_student` (
  `student_id`      BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `student_no`      VARCHAR(64)  NOT NULL COMMENT '学号',
  `student_name`    VARCHAR(64)  NOT NULL COMMENT '学生姓名',
  `gender`          INT          DEFAULT 0  COMMENT '性别：1-男 2-女 0-未知',
  `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `email`           VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `birthday`        DATE         DEFAULT NULL COMMENT '出生日期',
  `class_id`        BIGINT       DEFAULT NULL COMMENT '所属班级ID',
  `enrollment_year` VARCHAR(8)   DEFAULT NULL COMMENT '入学年份',
  `student_status`  INT          DEFAULT 1  COMMENT '状态：1-在读 2-休学 3-毕业 0-退学',
  `student_deleted` INT          DEFAULT 0  COMMENT '删除状态：1-删除 0-未删除',
  `student_remark`  VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `grade`           VARCHAR(32)  DEFAULT NULL COMMENT '年级（冗余字段，方便查询）',
  `gmt_create`      DATETIME     NOT NULL COMMENT '创建时间',
  `gmt_modified`    DATETIME     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`student_id`),
  UNIQUE INDEX `uk_student_no` (`student_no`),
  INDEX `idx_class_id` (`class_id`),
  INDEX `idx_student_name` (`student_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生表';

-- ========== 3. 教师表 edu_teacher ==========
CREATE TABLE IF NOT EXISTS `edu_teacher` (
  `teacher_id`      BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `teacher_no`      VARCHAR(64)  NOT NULL COMMENT '教师工号',
  `teacher_name`    VARCHAR(64)  NOT NULL COMMENT '教师姓名',
  `gender`          INT          DEFAULT 0  COMMENT '性别：1-男 2-女 0-未知',
  `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `email`           VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `title`           VARCHAR(64)  DEFAULT NULL COMMENT '职称',
  `teacher_status`  INT          DEFAULT 1  COMMENT '状态：1-在职 0-离职',
  `teacher_deleted` INT          DEFAULT 0  COMMENT '删除状态：1-删除 0-未删除',
  `teacher_remark`  VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `gmt_create`      DATETIME     NOT NULL COMMENT '创建时间',
  `gmt_modified`    DATETIME     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`teacher_id`),
  UNIQUE INDEX `uk_teacher_no` (`teacher_no`),
  INDEX `idx_teacher_name` (`teacher_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教师表';

-- ========== 4. 课程表 edu_course ==========
CREATE TABLE IF NOT EXISTS `edu_course` (
  `course_id`      BIGINT        NOT NULL COMMENT '主键，雪花ID',
  `course_name`    VARCHAR(256)  NOT NULL COMMENT '课程名称',
  `course_code`    VARCHAR(64)   NOT NULL COMMENT '课程编码',
  `credit`         DECIMAL(3,1)  DEFAULT NULL COMMENT '学分',
  `course_hours`   INT           DEFAULT NULL COMMENT '学时',
  `course_type`    VARCHAR(32)   DEFAULT NULL COMMENT '课程类型：required-必修 elective-选修',
  `teacher_id`     BIGINT        DEFAULT NULL COMMENT '授课教师ID',
  `semester`       VARCHAR(32)   DEFAULT NULL COMMENT '开课学期',
  `course_status`  INT           DEFAULT 1  COMMENT '状态：1-开课 0-停课',
  `course_deleted` INT           DEFAULT 0  COMMENT '删除状态：1-删除 0-未删除',
  `course_remark`  VARCHAR(512)  DEFAULT NULL COMMENT '备注',
  `gmt_create`     DATETIME      NOT NULL COMMENT '创建时间',
  `gmt_modified`   DATETIME      NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`course_id`),
  UNIQUE INDEX `uk_course_code` (`course_code`),
  INDEX `idx_teacher_id` (`teacher_id`),
  INDEX `idx_course_name` (`course_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程表';

-- ========== 5. 学生选课关联表 edu_student_course ==========
CREATE TABLE IF NOT EXISTS `edu_student_course` (
  `rel_id`       BIGINT        NOT NULL COMMENT '主键，雪花ID',
  `student_id`   BIGINT        NOT NULL COMMENT '学生ID',
  `course_id`    BIGINT        NOT NULL COMMENT '课程ID',
  `score`        DECIMAL(5,2)  DEFAULT NULL COMMENT '成绩',
  `rel_status`   INT           DEFAULT 1  COMMENT '状态：1-在读 2-已修完 3-退课',
  `gmt_create`   DATETIME      NOT NULL COMMENT '创建时间',
  `gmt_modified` DATETIME      NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`rel_id`),
  UNIQUE INDEX `uk_student_course` (`student_id`, `course_id`),
  INDEX `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生选课关联表';

-- ========== 6. 数据迁移 ==========

-- 迁移教师数据
INSERT INTO `edu_teacher` (`teacher_id`, `teacher_no`, `teacher_name`, `gender`, `phone`, `email`, `title`,
  `teacher_status`, `teacher_deleted`, `teacher_remark`, `gmt_create`, `gmt_modified`)
SELECT
  `id`,
  CONCAT('TCH', LPAD(`id`, 6, '0')),
  `name`,
  0,
  `phone`,
  `email`,
  `title`,
  1, 0, NULL,
  COALESCE(`create_time`, NOW()),
  COALESCE(`create_time`, NOW())
FROM `teacher`;

-- 迁移班级数据
INSERT INTO `edu_class` (`class_id`, `class_name`, `class_code`, `grade`, `major`, `class_sort`,
  `class_status`, `class_deleted`, `class_remark`, `gmt_create`, `gmt_modified`)
SELECT
  `id`,
  `name`,
  CONCAT('CLS', LPAD(`id`, 6, '0')),
  `grade`,
  '计算机科学与技术',
  0,
  1, 0,
  CASE WHEN `head_teacher_id` IS NOT NULL
    THEN CONCAT('原班主任ID: ', `head_teacher_id`)
    ELSE NULL
  END,
  COALESCE(`create_time`, NOW()),
  COALESCE(`create_time`, NOW())
FROM `clazz`;

-- 迁移课程数据
INSERT INTO `edu_course` (`course_id`, `course_name`, `course_code`, `credit`, `course_hours`, `course_type`,
  `teacher_id`, `semester`, `course_status`, `course_deleted`, `course_remark`, `gmt_create`, `gmt_modified`)
SELECT
  `id`,
  `name`,
  CONCAT('CRS', LPAD(`id`, 6, '0')),
  `credit`,
  CAST(`credit` * 16 AS SIGNED),   -- 学分×16 估算学时
  'required',
  `teacher_id`,
  `semester`,
  1, 0, NULL,
  COALESCE(`create_time`, NOW()),
  COALESCE(`create_time`, NOW())
FROM `course`;

-- 迁移学生数据
INSERT INTO `edu_student` (`student_id`, `student_no`, `student_name`, `gender`, `phone`, `email`,
  `birthday`, `class_id`, `enrollment_year`, `student_status`, `student_deleted`,
  `student_remark`, `grade`, `gmt_create`, `gmt_modified`)
SELECT
  `id`,
  CONCAT('STU', LPAD(`id`, 8, '0')),
  `name`,
  0,
  `phone`,
  `email`,
  NULL,                          -- 旧表无生日数据
  `class_id`,
  CASE
    WHEN `grade` IN ('大一', 'freshman') THEN '2024'
    WHEN `grade` IN ('大二', 'sophomore') THEN '2023'
    WHEN `grade` IN ('大三', 'junior') THEN '2022'
    WHEN `grade` IN ('大四', 'senior') THEN '2021'
    ELSE NULL
  END,
  1, 0, NULL,
  `grade`,                       -- 保留原年级数据
  NOW(), NOW()
FROM `student`;

-- 迁移选课数据
INSERT INTO `edu_student_course` (`rel_id`, `student_id`, `course_id`, `score`, `rel_status`,
  `gmt_create`, `gmt_modified`)
SELECT
  `id`,
  `student_id`,
  `course_id`,
  `score`,
  2,                             -- 已修完（已有成绩）
  NOW(), NOW()
FROM `enrollment`;

-- ========== 7. 删除旧表 ==========
DROP TABLE IF EXISTS `enrollment`;
DROP TABLE IF EXISTS `course`;
DROP TABLE IF EXISTS `clazz`;
DROP TABLE IF EXISTS `teacher`;
DROP TABLE IF EXISTS `student`;
