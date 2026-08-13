-- ============================================
-- Flyway V1 — 组织架构服务：初始化 org_db
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
  `director_id`   BIGINT       DEFAULT NULL COMMENT '专业负责人ID（关联 sys_user）',
  `major_status`  INT          DEFAULT 1  COMMENT '状态：1-正常 0-禁用',
  `major_deleted` INT          DEFAULT 0  COMMENT '删除状态：1-删除 0-未删除',
  `major_remark`  VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `gmt_create`    DATETIME     NOT NULL COMMENT '创建时间',
  `gmt_modified`  DATETIME     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`major_id`),
  UNIQUE INDEX `uk_major_code` (`major_code`),
  INDEX `idx_college_id` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='专业表';

-- ========== 3. 班级表 ==========
CREATE TABLE IF NOT EXISTS `edu_class` (
  `class_id`      BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `class_name`    VARCHAR(128) NOT NULL COMMENT '班级名称',
  `class_code`    VARCHAR(64)  NOT NULL COMMENT '班级编码',
  `college_id`    BIGINT       DEFAULT NULL COMMENT '学院ID',
  `major_id`      BIGINT       DEFAULT NULL COMMENT '专业ID',
  `counselor_id`  BIGINT       DEFAULT NULL COMMENT '辅导员ID（关联 sys_counselor）',
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

-- ========== 4. 学校表 ==========
CREATE TABLE IF NOT EXISTS `sys_school` (
    `school_id`      BIGINT PRIMARY KEY COMMENT '雪花ID',
    `school_name`    VARCHAR(128) NOT NULL COMMENT '学校名称',
    `school_code`    VARCHAR(32)  DEFAULT NULL UNIQUE COMMENT '学校编码',
    `school_status`  INT          DEFAULT 1 COMMENT '1-正常, 0-禁用',
    `school_deleted` INT          DEFAULT 0 COMMENT '1-已删除, 0-正常',
    `school_remark`  VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `gmt_create`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校表';

-- ========== 5. 辅导员表 ==========
CREATE TABLE IF NOT EXISTS `sys_counselor` (
    `counselor_id`      BIGINT PRIMARY KEY COMMENT '雪花ID',
    `user_id`           BIGINT       DEFAULT NULL COMMENT '关联 sys_user.id',
    `counselor_no`      VARCHAR(32)  DEFAULT NULL UNIQUE COMMENT '工号',
    `counselor_name`    VARCHAR(64)  NOT NULL COMMENT '姓名',
    `phone`             VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`             VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `counselor_status`  INT          DEFAULT 1 COMMENT '1-在职, 0-离职',
    `counselor_deleted` INT          DEFAULT 0 COMMENT '1-已删除, 0-正常',
    `counselor_remark`  VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `gmt_create`        DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='辅导员表';

-- ========== 6. 督导表 ==========
CREATE TABLE IF NOT EXISTS `sys_supervisor` (
    `supervisor_id`      BIGINT PRIMARY KEY COMMENT '雪花ID',
    `user_id`            BIGINT       DEFAULT NULL COMMENT '关联 sys_user.id',
    `supervisor_no`      VARCHAR(32)  DEFAULT NULL UNIQUE COMMENT '工号',
    `supervisor_name`    VARCHAR(64)  NOT NULL COMMENT '姓名',
    `phone`              VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`              VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `supervisor_status`  INT          DEFAULT 1 COMMENT '1-在职, 0-离职',
    `supervisor_deleted` INT          DEFAULT 0 COMMENT '1-已删除, 0-正常',
    `supervisor_remark`  VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `gmt_create`         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='督导表';

-- ========== 7. 学院管理员关联表 ==========
CREATE TABLE IF NOT EXISTS `sys_college_admin` (
    `rel_id`     BIGINT PRIMARY KEY COMMENT '雪花ID',
    `user_id`    BIGINT   NOT NULL COMMENT '关联 sys_user.id',
    `college_id` BIGINT   NOT NULL COMMENT '关联 edu_college.college_id',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_college` (`user_id`, `college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院管理员关联表';

-- ========== 8. 督导评价表 ==========
CREATE TABLE IF NOT EXISTS `sys_teacher_evaluation` (
    `eval_id`       BIGINT PRIMARY KEY COMMENT '雪花ID',
    `supervisor_id` BIGINT         NOT NULL COMMENT '关联 sys_supervisor.supervisor_id',
    `teacher_id`    BIGINT         NOT NULL COMMENT '关联 edu_teacher.teacher_id',
    `course_id`     BIGINT         DEFAULT NULL COMMENT '关联 edu_course.course_id（听课课程）',
    `score`         DECIMAL(5,2)   DEFAULT NULL COMMENT '评分',
    `comment`       VARCHAR(1024)  DEFAULT NULL COMMENT '评价内容',
    `eval_time`     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    `gmt_create`    DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='督导评价表';

-- ========== 9. 种子数据 ==========

-- 学校
INSERT INTO `sys_school` (`school_id`, `school_name`, `school_code`) VALUES
(1, '智慧教务示范大学', 'SMART-EDU');

-- 学院
INSERT INTO `edu_college` (`college_id`, `college_name`, `college_code`, `college_status`, `college_deleted`, `gmt_create`, `gmt_modified`) VALUES
(1001, '计算机学院',         'CS',  1, 0, NOW(), NOW()),
(1002, '数学与统计学院',     'MATH', 1, 0, NOW(), NOW()),
(1003, '外国语学院',         'FL',  1, 0, NOW(), NOW()),
(1004, '电子信息工程学院',   'EE',  1, 0, NOW(), NOW()),
(1005, '经济管理学院',       'EM',  1, 0, NOW(), NOW()),
(1006, '法学院',             'LAW', 1, 0, NOW(), NOW());

-- 专业
INSERT INTO `edu_major` (`major_id`, `major_name`, `major_code`, `college_id`, `major_status`, `major_deleted`, `gmt_create`, `gmt_modified`) VALUES
(2001, '计算机科学与技术',   'CS-CST',   1001, 1, 0, NOW(), NOW()),
(2002, '软件工程',           'CS-SE',    1001, 1, 0, NOW(), NOW()),
(2003, '数据科学与大数据',   'CS-DS',    1001, 1, 0, NOW(), NOW()),
(2004, '数学与应用数学',     'MATH-AM',  1002, 1, 0, NOW(), NOW()),
(2005, '英语',               'FL-EN',    1003, 1, 0, NOW(), NOW()),
(2006, '电子信息工程',       'EE-EIE',   1004, 1, 0, NOW(), NOW()),
(2007, '通信工程',           'EE-CE',    1004, 1, 0, NOW(), NOW()),
(2008, '工商管理',           'EM-BA',    1005, 1, 0, NOW(), NOW()),
(2009, '会计学',             'EM-AC',    1005, 1, 0, NOW(), NOW()),
(2010, '法学',               'LAW-LAW',  1006, 1, 0, NOW(), NOW());

-- 班级
INSERT INTO `edu_class` (`class_id`, `class_name`, `class_code`, `college_id`, `major_id`,
    `grade`, `major`, `class_sort`, `class_status`, `class_deleted`, `class_remark`, `gmt_create`, `gmt_modified`) VALUES
(5001, '计科2401班', 'CLS-CST-2401', 1001, 2001, '2024', '计算机科学与技术', 1, 1, 0, NULL, NOW(), NOW()),
(5002, '软件2401班', 'CLS-SE-2401',  1001, 2002, '2024', '软件工程',         2, 1, 0, NULL, NOW(), NOW()),
(5003, '电信2401班', 'CLS-EE-2401',  1004, 2006, '2024', '电子信息工程',     1, 1, 0, NULL, NOW(), NOW()),
(5004, '工商2401班', 'CLS-BA-2401',  1005, 2008, '2024', '工商管理',         1, 1, 0, NULL, NOW(), NOW());

-- 辅导员
INSERT INTO `sys_counselor` (`counselor_id`, `user_id`, `counselor_no`, `counselor_name`, `phone`, `email`) VALUES
(1001, NULL, 'C0001', '张辅导员', '13800001001', 'counselor1@example.com'),
(1002, NULL, 'C0002', '李辅导员', '13800001002', 'counselor2@example.com');

-- 督导
INSERT INTO `sys_supervisor` (`supervisor_id`, `user_id`, `supervisor_no`, `supervisor_name`, `phone`, `email`) VALUES
(2001, NULL, 'S0001', '王督导', '13800002001', 'supervisor1@example.com'),
(2002, NULL, 'S0002', '赵督导', '13800002002', 'supervisor2@example.com');
