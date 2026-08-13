-- ============================================
-- Flyway V8 — 组织架构实体扩展
-- 新增：学校、辅导员、督导、学院管理员、督导评价
-- 修改：班级增加辅导员ID、专业增加负责人ID
-- ============================================

-- 1. 学校表
CREATE TABLE `sys_school` (
    `school_id`      BIGINT PRIMARY KEY COMMENT '雪花ID',
    `school_name`    VARCHAR(128) NOT NULL COMMENT '学校名称',
    `school_code`    VARCHAR(32)  DEFAULT NULL UNIQUE COMMENT '学校编码',
    `school_status`  INT          DEFAULT 1 COMMENT '1-正常, 0-禁用',
    `school_deleted` INT          DEFAULT 0 COMMENT '1-已删除, 0-正常',
    `school_remark`  VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `gmt_create`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校表';

-- 2. 辅导员表
CREATE TABLE `sys_counselor` (
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

-- 3. 督导表
CREATE TABLE `sys_supervisor` (
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

-- 4. 学院管理员关联表
CREATE TABLE `sys_college_admin` (
    `rel_id`     BIGINT PRIMARY KEY COMMENT '雪花ID',
    `user_id`    BIGINT   NOT NULL COMMENT '关联 sys_user.id',
    `college_id` BIGINT   NOT NULL COMMENT '关联 edu_college.college_id',
    `gmt_create` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_college` (`user_id`, `college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院管理员关联表';

-- 5. 督导评价表
CREATE TABLE `sys_teacher_evaluation` (
    `eval_id`       BIGINT PRIMARY KEY COMMENT '雪花ID',
    `supervisor_id` BIGINT         NOT NULL COMMENT '关联 sys_supervisor.supervisor_id',
    `teacher_id`    BIGINT         NOT NULL COMMENT '关联 edu_teacher.teacher_id',
    `course_id`     BIGINT         DEFAULT NULL COMMENT '关联 edu_course.course_id（听课课程）',
    `score`         DECIMAL(5,2)   DEFAULT NULL COMMENT '评分',
    `comment`       VARCHAR(1024)  DEFAULT NULL COMMENT '评价内容',
    `eval_time`     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    `gmt_create`    DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='督导评价表';

-- 6. 班级增加辅导员ID
ALTER TABLE `edu_class`
    ADD COLUMN `counselor_id` BIGINT DEFAULT NULL COMMENT '辅导员ID（关联 sys_counselor）'
    AFTER `major_id`;

-- 7. 专业增加负责人ID
ALTER TABLE `edu_major`
    ADD COLUMN `director_id` BIGINT DEFAULT NULL COMMENT '专业负责人ID（关联 sys_user）'
    AFTER `college_id`;

-- 8. 种子数据：一所学校
INSERT INTO `sys_school` (`school_id`, `school_name`, `school_code`) VALUES
(1, '智慧教务示范大学', 'SMART-EDU');

-- 9. 种子数据：辅导员
INSERT INTO `sys_counselor` (`counselor_id`, `user_id`, `counselor_no`, `counselor_name`, `phone`, `email`) VALUES
(1001, NULL, 'C0001', '张辅导员', '13800001001', 'counselor1@example.com'),
(1002, NULL, 'C0002', '李辅导员', '13800001002', 'counselor2@example.com');

-- 10. 种子数据：督导
INSERT INTO `sys_supervisor` (`supervisor_id`, `user_id`, `supervisor_no`, `supervisor_name`, `phone`, `email`) VALUES
(2001, NULL, 'S0001', '王督导', '13800002001', 'supervisor1@example.com'),
(2002, NULL, 'S0002', '赵督导', '13800002002', 'supervisor2@example.com');
