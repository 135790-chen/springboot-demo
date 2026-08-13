-- ============================================
-- Flyway V1 — 教学服务初始化
-- 包含：教师、课程、选课、培养方案、毕业审核、学院、专业、班级、学生
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

-- ========== 3. 班级表 ==========
CREATE TABLE IF NOT EXISTS `edu_class` (
  `class_id`      BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `class_name`    VARCHAR(128) NOT NULL COMMENT '班级名称',
  `class_code`    VARCHAR(64)  NOT NULL COMMENT '班级编码',
  `college_id`    BIGINT       DEFAULT NULL COMMENT '学院ID',
  `major_id`      BIGINT       DEFAULT NULL COMMENT '专业ID',
  `grade`         VARCHAR(32)  DEFAULT NULL COMMENT '年级',
  `major`         VARCHAR(128) DEFAULT NULL COMMENT '专业名称（冗余）',
  `class_sort`    INT          DEFAULT 0  COMMENT '显示顺序',
  `class_status`  INT          DEFAULT 1  COMMENT '状态：1-正常 0-禁用',
  `class_deleted` INT          DEFAULT 0  COMMENT '删除状态：1-删除 0-未删除',
  `class_remark`  VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `gmt_create`    DATETIME     NOT NULL COMMENT '创建时间',
  `gmt_modified`  DATETIME     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`class_id`),
  UNIQUE INDEX `uk_class_code` (`class_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='班级表';

-- ========== 4. 学生表 ==========
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
  `grade`           VARCHAR(32)  DEFAULT NULL COMMENT '年级',
  `gmt_create`      DATETIME     NOT NULL COMMENT '创建时间',
  `gmt_modified`    DATETIME     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`student_id`),
  UNIQUE INDEX `uk_student_no` (`student_no`),
  INDEX `idx_class_id` (`class_id`),
  INDEX `idx_student_name` (`student_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生表';

-- ========== 5. 教师表 ==========
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

-- ========== 6. 课程表 ==========
CREATE TABLE IF NOT EXISTS `edu_course` (
  `course_id`      BIGINT        NOT NULL COMMENT '主键，雪花ID',
  `course_name`    VARCHAR(256)  NOT NULL COMMENT '课程名称',
  `course_code`    VARCHAR(64)   NOT NULL COMMENT '课程编码',
  `credit`         DECIMAL(3,1)  DEFAULT NULL COMMENT '学分',
  `course_hours`   INT           DEFAULT NULL COMMENT '学时',
  `max_students`   INT           DEFAULT 100 COMMENT '课程最大容量',
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

-- ========== 7. 学生选课关联表 ==========
CREATE TABLE IF NOT EXISTS `edu_student_course` (
  `rel_id`       BIGINT        NOT NULL COMMENT '主键，雪花ID',
  `student_id`   BIGINT        NOT NULL COMMENT '学生ID',
  `course_id`    BIGINT        NOT NULL COMMENT '课程ID',
  `score`        DECIMAL(5,2)  DEFAULT NULL COMMENT '成绩',
  `rel_status`   INT           DEFAULT 1  COMMENT '状态：1-在读 2-已修完 3-退课',
  `confirm_status` INT         DEFAULT 1  COMMENT '确认状态：0-预扣待确认 1-已确认 2-失败已回滚',
  `gmt_create`   DATETIME      NOT NULL COMMENT '创建时间',
  `gmt_modified` DATETIME      NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`rel_id`),
  UNIQUE INDEX `uk_student_course` (`student_id`, `course_id`),
  INDEX `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生选课关联表';

-- ========== 8. 培养方案表 ==========
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
  UNIQUE INDEX `uk_major_grade` (`major_id`, `grade`),
  INDEX `idx_major_grade` (`major_id`, `grade`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='培养方案表';

-- ========== 9. 方案-课程关联表 ==========
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

-- ========== 10. 毕业审核结果表 ==========
CREATE TABLE IF NOT EXISTS `edu_graduation_result` (
  `result_id`                     BIGINT        NOT NULL COMMENT '主键，雪花ID',
  `student_id`                    BIGINT        NOT NULL COMMENT '学生ID',
  `plan_id`                       BIGINT        NOT NULL COMMENT '培养方案ID',
  `total_earned_credits`          DECIMAL(5,1)  NOT NULL COMMENT '已获总学分',
  `required_earned_credits`       DECIMAL(5,1)  NOT NULL COMMENT '必修已获学分',
  `major_elective_earned_credits` DECIMAL(5,1)  NOT NULL COMMENT '专业选修已获学分',
  `general_elective_earned_credits` DECIMAL(5,1) NOT NULL COMMENT '通识选修已获学分',
  `passed`                        INT           NOT NULL COMMENT '是否通过：1-通过 0-不通过',
  `missing_items`                 TEXT          DEFAULT NULL COMMENT '缺失项明细',
  `review_time`                   DATETIME      NOT NULL COMMENT '审核时间',
  `gmt_create`                    DATETIME      NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`result_id`),
  INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='毕业审核结果表';

-- ========== 11. 种子数据：学院 ==========
INSERT IGNORE INTO `edu_college` (`college_id`, `college_name`, `college_code`, `college_status`, `college_deleted`, `gmt_create`, `gmt_modified`) VALUES
(1001, '计算机学院',         'CS',  1, 0, NOW(), NOW()),
(1002, '数学与统计学院',     'MATH', 1, 0, NOW(), NOW()),
(1003, '外国语学院',         'FL',  1, 0, NOW(), NOW()),
(1004, '电子信息工程学院',   'EE',  1, 0, NOW(), NOW()),
(1005, '经济管理学院',       'EM',  1, 0, NOW(), NOW()),
(1006, '法学院',             'LAW', 1, 0, NOW(), NOW());

-- ========== 12. 种子数据：专业 ==========
INSERT IGNORE INTO `edu_major` (`major_id`, `major_name`, `major_code`, `college_id`, `major_status`, `major_deleted`, `gmt_create`, `gmt_modified`) VALUES
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

-- ========== 13. 种子数据：班级 ==========
INSERT IGNORE INTO `edu_class`
(`class_id`, `class_name`, `class_code`, `college_id`, `major_id`, `grade`, `major`, `class_sort`, `class_status`, `class_deleted`, `gmt_create`, `gmt_modified`)
VALUES
(5001, '计科2401班', 'CLS-CST-2401', 1001, 2001, '2024', '计算机科学与技术', 1, 1, 0, NOW(), NOW()),
(5002, '软件2401班', 'CLS-SE-2401',  1001, 2002, '2024', '软件工程',         2, 1, 0, NOW(), NOW()),
(5003, '电信2401班', 'CLS-EE-2401',  1004, 2006, '2024', '电子信息工程',     1, 1, 0, NOW(), NOW()),
(5004, '工商2401班', 'CLS-BA-2401',  1005, 2008, '2024', '工商管理',         1, 1, 0, NOW(), NOW());

-- ========== 14. 种子数据：教师 ==========
INSERT IGNORE INTO `edu_teacher`
(`teacher_id`, `teacher_no`, `teacher_name`, `gender`, `phone`, `email`, `title`, `teacher_status`, `teacher_deleted`, `gmt_create`, `gmt_modified`)
VALUES
(6001, 'TCH000001', '张建国', 1, '13800001001', 'zhangjg@example.com', '教授',   1, 0, NOW(), NOW()),
(6002, 'TCH000002', '李明辉', 1, '13800001002', 'limh@example.com',   '副教授', 1, 0, NOW(), NOW()),
(6003, 'TCH000003', '王芳',   2, '13800001003', 'wangfang@example.com', '副教授', 1, 0, NOW(), NOW()),
(6004, 'TCH000004', '陈志远', 1, '13800001004', 'chenzy@example.com',  '讲师',   1, 0, NOW(), NOW()),
(6005, 'TCH000005', '刘洋',   1, '13800001005', 'liuyang@example.com', '讲师',   1, 0, NOW(), NOW()),
(6006, 'TCH000006', '赵晓燕', 2, '13800001006', 'zhaoxy@example.com',  '讲师',   1, 0, NOW(), NOW()),
(6007, 'TCH000007', '周文博', 1, '13800001007', 'zhouwb@example.com',  '副教授', 1, 0, NOW(), NOW()),
(6008, 'TCH000008', '吴雪梅', 2, '13800001008', 'wuxm@example.com',    '助教',   1, 0, NOW(), NOW());

-- ========== 15. 种子数据：课程 ==========
INSERT IGNORE INTO `edu_course`
(`course_id`, `course_name`, `course_code`, `credit`, `course_hours`, `max_students`, `course_type`, `teacher_id`, `semester`, `course_status`, `course_deleted`, `gmt_create`, `gmt_modified`)
VALUES
(1,  '高等数学A',       'CRS000001', 4.0, 64, 100, 'required', 6001, '2024-2025-1', 1, 0, NOW(), NOW()),
(2,  '线性代数',         'CRS000002', 3.0, 48, 100, 'required', 6001, '2024-2025-1', 1, 0, NOW(), NOW()),
(3,  '程序设计基础',     'CRS000003', 3.0, 48, 100, 'required', 6002, '2024-2025-1', 1, 0, NOW(), NOW()),
(4,  'Java企业级开发',   'CRS000004', 2.0, 32, 50,  'elective', 6002, '2024-2025-2', 1, 0, NOW(), NOW()),
(5,  'Python科学计算',   'CRS000005', 2.0, 32, 50,  'elective', 6004, '2025-2026-1', 1, 0, NOW(), NOW()),
(6,  '大学英语',         'CRS000006', 2.0, 32, 100, 'elective', 6007, '2024-2025-1', 1, 0, NOW(), NOW()),
(7,  '大学体育',         'CRS000007', 1.0, 32, 100, 'elective', 6008, '2024-2025-1', 1, 0, NOW(), NOW()),
(8,  '数据结构与算法',   'CRS000008', 4.0, 64, 50,  'required', 6002, '2024-2025-2', 1, 0, NOW(), NOW()),
(9,  '操作系统',         'CRS000009', 4.0, 64, 50,  'required', 6003, '2025-2026-1', 1, 0, NOW(), NOW()),
(10, '计算机网络',       'CRS000010', 3.0, 48, 50,  'required', 6003, '2025-2026-1', 1, 0, NOW(), NOW()),
(11, '数据库系统原理',   'CRS000011', 3.0, 48, 50,  'required', 6004, '2025-2026-1', 1, 0, NOW(), NOW()),
(12, '编译原理',         'CRS000012', 3.0, 48, 50,  'required', 6002, '2025-2026-2', 1, 0, NOW(), NOW()),
(13, '人工智能导论',     'CRS000013', 2.0, 32, 50,  'elective', 6005, '2025-2026-2', 1, 0, NOW(), NOW()),
(14, '云计算与大数据',   'CRS000014', 2.0, 32, 50,  'elective', 6005, '2025-2026-2', 1, 0, NOW(), NOW()),
(15, '移动应用开发',     'CRS000015', 2.0, 32, 50,  'elective', 6006, '2025-2026-1', 1, 0, NOW(), NOW()),
(16, 'Web前端技术',      'CRS000016', 2.0, 32, 50,  'elective', 6006, '2025-2026-1', 1, 0, NOW(), NOW()),
(17, '思想道德与法治',   'CRS000017', 2.0, 32, 100, 'elective', 6007, '2024-2025-2', 1, 0, NOW(), NOW()),
(18, '创新创业基础',     'CRS000018', 1.0, 16, 100, 'elective', 6007, '2025-2026-2', 1, 0, NOW(), NOW()),
(19, '概率论与数理统计', 'CRS000019', 3.0, 48, 50,  'required', 6001, '2024-2025-2', 1, 0, NOW(), NOW()),
(20, '软件工程概论',     'CRS000020', 3.0, 48, 50,  'required', 6004, '2025-2026-1', 1, 0, NOW(), NOW());

-- ========== 16. 种子数据：培养方案 ==========
INSERT IGNORE INTO `edu_training_plan`
(`plan_id`, `plan_name`, `major_id`, `grade`, `version`, `total_required_credits`, `major_elective_min_credits`, `general_elective_min_credits`, `plan_status`, `plan_deleted`, `gmt_create`, `gmt_modified`)
VALUES
(3001, '计算机科学与技术专业 2024 版培养方案', 2001, '2024', 1, 16, 4,  3, 1, 0, NOW(), NOW()),
(3002, '软件工程专业 2024 版培养方案',         2002, '2024', 1, 16, 6,  3, 1, 0, NOW(), NOW()),
(3003, '数据科学与大数据专业 2024 版培养方案', 2003, '2024', 1, 16, 5,  3, 1, 0, NOW(), NOW()),
(3004, '数学与应用数学专业 2024 版培养方案',   2004, '2024', 1, 16, 4,  3, 1, 0, NOW(), NOW()),
(3005, '英语专业 2024 版培养方案',             2005, '2024', 1, 14, 6,  3, 1, 0, NOW(), NOW()),
(3006, '电子信息工程专业 2024 版培养方案',     2006, '2024', 1, 18, 4,  3, 1, 0, NOW(), NOW()),
(3007, '通信工程专业 2024 版培养方案',         2007, '2024', 1, 16, 5,  3, 1, 0, NOW(), NOW()),
(3008, '工商管理专业 2024 版培养方案',         2008, '2024', 1, 15, 6,  4, 1, 0, NOW(), NOW()),
(3009, '会计学专业 2024 版培养方案',           2009, '2024', 1, 16, 5,  3, 1, 0, NOW(), NOW()),
(3010, '法学专业 2024 版培养方案',             2010, '2024', 1, 16, 4,  4, 1, 0, NOW(), NOW());

-- ========== 17. 种子数据：方案-课程关联 ==========
INSERT IGNORE INTO `edu_plan_course`
(`rel_id`, `plan_id`, `course_id`, `course_category`, `is_required`, `gmt_create`)
VALUES
-- Plan 3001 (CS-CST)
(4001, 3001, 1,  'REQUIRED',         1, NOW()),
(4002, 3001, 2,  'REQUIRED',         1, NOW()),
(4003, 3001, 3,  'REQUIRED',         1, NOW()),
(4004, 3001, 4,  'MAJOR_ELECTIVE',   0, NOW()),
(4005, 3001, 5,  'MAJOR_ELECTIVE',   0, NOW()),
(4006, 3001, 6,  'GENERAL_ELECTIVE', 0, NOW()),
(4007, 3001, 7,  'GENERAL_ELECTIVE', 0, NOW()),
(4008, 3001, 8,  'REQUIRED',         1, NOW()),
(4009, 3001, 9,  'REQUIRED',         1, NOW()),
(4010, 3001, 13, 'MAJOR_ELECTIVE',   0, NOW()),
(4011, 3001, 17, 'GENERAL_ELECTIVE', 0, NOW()),

-- Plan 3002 (CS-SE)
(4012, 3002, 1,  'REQUIRED',         1, NOW()),
(4013, 3002, 2,  'REQUIRED',         1, NOW()),
(4014, 3002, 8,  'REQUIRED',         1, NOW()),
(4015, 3002, 11, 'REQUIRED',         1, NOW()),
(4016, 3002, 12, 'REQUIRED',         1, NOW()),
(4017, 3002, 20, 'REQUIRED',         1, NOW()),
(4018, 3002, 4,  'MAJOR_ELECTIVE',   0, NOW()),
(4019, 3002, 5,  'MAJOR_ELECTIVE',   0, NOW()),
(4020, 3002, 14, 'MAJOR_ELECTIVE',   0, NOW()),
(4021, 3002, 15, 'MAJOR_ELECTIVE',   0, NOW()),
(4022, 3002, 16, 'MAJOR_ELECTIVE',   0, NOW()),
(4023, 3002, 6,  'GENERAL_ELECTIVE', 0, NOW()),
(4024, 3002, 7,  'GENERAL_ELECTIVE', 0, NOW()),
(4025, 3002, 17, 'GENERAL_ELECTIVE', 0, NOW()),
(4026, 3002, 18, 'GENERAL_ELECTIVE', 0, NOW()),

-- Plan 3003 (CS-DS)
(4027, 3003, 1,  'REQUIRED',         1, NOW()),
(4028, 3003, 2,  'REQUIRED',         1, NOW()),
(4029, 3003, 3,  'REQUIRED',         1, NOW()),
(4030, 3003, 8,  'REQUIRED',         1, NOW()),
(4031, 3003, 11, 'REQUIRED',         1, NOW()),
(4032, 3003, 5,  'MAJOR_ELECTIVE',   0, NOW()),
(4033, 3003, 13, 'MAJOR_ELECTIVE',   0, NOW()),
(4034, 3003, 14, 'MAJOR_ELECTIVE',   0, NOW()),
(4035, 3003, 6,  'GENERAL_ELECTIVE', 0, NOW()),
(4036, 3003, 17, 'GENERAL_ELECTIVE', 0, NOW()),
(4037, 3003, 18, 'GENERAL_ELECTIVE', 0, NOW()),

-- Plan 3004 (MATH-AM)
(4038, 3004, 1,  'REQUIRED',         1, NOW()),
(4039, 3004, 2,  'REQUIRED',         1, NOW()),
(4040, 3004, 19, 'REQUIRED',         1, NOW()),
(4041, 3004, 3,  'REQUIRED',         1, NOW()),
(4042, 3004, 5,  'MAJOR_ELECTIVE',   0, NOW()),
(4043, 3004, 13, 'MAJOR_ELECTIVE',   0, NOW()),
(4044, 3004, 6,  'GENERAL_ELECTIVE', 0, NOW()),
(4045, 3004, 7,  'GENERAL_ELECTIVE', 0, NOW()),
(4046, 3004, 17, 'GENERAL_ELECTIVE', 0, NOW());

	-- ========== 18. 学生 / 选课 / 毕业审核（不使用固定种子数据） ==========
	-- 学生数据由 student-service 统一管理（student_db.edu_student）。
	-- teaching_db.edu_student 用于选课 JOIN 和课程查学生，需与 student_db 同步。
	-- 选课记录请通过 API 创建（POST /api/edu/student-course）。
	-- 毕业审核请通过 POST /api/edu/graduation/check/{studentId} 触发。

