-- ============================================
-- Flyway V7 — 教务管理模块种子数据
-- 填充：课程、学院、专业、培养方案、教师、班级、学生、选课、毕业审核
-- 所有 INSERT 使用 IGNORE，保证幂等
-- ============================================
SET NAMES utf8mb4;

-- ========== 0. 防止培养方案重复 ==========
-- 确保同一个专业+年级只有一份方案（幂等：先检查是否已存在）
SET @idx_count = (SELECT COUNT(*) FROM information_schema.STATISTICS
                  WHERE TABLE_SCHEMA = DATABASE()
                    AND TABLE_NAME = 'edu_training_plan'
                    AND INDEX_NAME = 'uk_major_grade');
SET @ddl = IF(@idx_count = 0,
    'ALTER TABLE `edu_training_plan` ADD UNIQUE INDEX `uk_major_grade` (`major_id`, `grade`)',
    'SELECT ''uk_major_grade already exists'' AS msg');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 调整现有方案学分为可验证范围（100→16 为四年制不可行，调整为测试友好值；
-- 真实系统可改为 100+，此处方便毕业审核功能演示）
UPDATE `edu_training_plan`
SET `total_required_credits`     = 16.0,
    `major_elective_min_credits` = 4.0,
    `general_elective_min_credits` = 3.0
WHERE `plan_id` = 3001;

UPDATE `edu_training_plan`
SET `total_required_credits`     = 16.0,
    `major_elective_min_credits` = 6.0,
    `general_elective_min_credits` = 3.0
WHERE `plan_id` = 3002;

-- ========== 1. 课程 (edu_course) ==========
INSERT IGNORE INTO `edu_course`
(`course_id`, `course_name`, `course_code`, `credit`, `course_hours`, `course_type`, `teacher_id`,
 `semester`, `course_status`, `course_deleted`, `course_remark`, `gmt_create`, `gmt_modified`)
VALUES
-- 必修课 (课程ID 1-12)
(1,  '高等数学A',       'CRS000001', 4.0, 64, 'required', 6001, '2024-2025-1', 1, 0, NULL, NOW(), NOW()),
(2,  '线性代数',         'CRS000002', 3.0, 48, 'required', 6001, '2024-2025-1', 1, 0, NULL, NOW(), NOW()),
(3,  '程序设计基础',     'CRS000003', 3.0, 48, 'required', 6002, '2024-2025-1', 1, 0, NULL, NOW(), NOW()),
(8,  '数据结构与算法',   'CRS000008', 4.0, 64, 'required', 6002, '2024-2025-2', 1, 0, NULL, NOW(), NOW()),
(9,  '操作系统',         'CRS000009', 4.0, 64, 'required', 6003, '2025-2026-1', 1, 0, NULL, NOW(), NOW()),
(10, '计算机网络',       'CRS000010', 3.0, 48, 'required', 6003, '2025-2026-1', 1, 0, NULL, NOW(), NOW()),
(11, '数据库系统原理',   'CRS000011', 3.0, 48, 'required', 6004, '2025-2026-1', 1, 0, NULL, NOW(), NOW()),
(12, '编译原理',         'CRS000012', 3.0, 48, 'required', 6002, '2025-2026-2', 1, 0, NULL, NOW(), NOW()),
(19, '概率论与数理统计', 'CRS000019', 3.0, 48, 'required', 6001, '2024-2025-2', 1, 0, NULL, NOW(), NOW()),
(20, '软件工程概论',     'CRS000020', 3.0, 48, 'required', 6004, '2025-2026-1', 1, 0, NULL, NOW(), NOW()),
-- 专业选修课
(4,  'Java企业级开发',   'CRS000004', 2.0, 32, 'elective', 6002, '2024-2025-2', 1, 0, NULL, NOW(), NOW()),
(5,  'Python科学计算',   'CRS000005', 2.0, 32, 'elective', 6004, '2025-2026-1', 1, 0, NULL, NOW(), NOW()),
(13, '人工智能导论',     'CRS000013', 2.0, 32, 'elective', 6005, '2025-2026-2', 1, 0, NULL, NOW(), NOW()),
(14, '云计算与大数据',   'CRS000014', 2.0, 32, 'elective', 6005, '2025-2026-2', 1, 0, NULL, NOW(), NOW()),
(15, '移动应用开发',     'CRS000015', 2.0, 32, 'elective', 6006, '2025-2026-1', 1, 0, NULL, NOW(), NOW()),
(16, 'Web前端技术',      'CRS000016', 2.0, 32, 'elective', 6006, '2025-2026-1', 1, 0, NULL, NOW(), NOW()),
-- 通识选修课
(6,  '大学英语',         'CRS000006', 2.0, 32, 'elective', 6007, '2024-2025-1', 1, 0, NULL, NOW(), NOW()),
(7,  '大学体育',         'CRS000007', 1.0, 32, 'elective', 6008, '2024-2025-1', 1, 0, NULL, NOW(), NOW()),
(17, '思想道德与法治',   'CRS000017', 2.0, 32, 'elective', 6007, '2024-2025-2', 1, 0, NULL, NOW(), NOW()),
(18, '创新创业基础',     'CRS000018', 1.0, 16, 'elective', 6007, '2025-2026-2', 1, 0, NULL, NOW(), NOW());

-- ========== 2. 学院 (edu_college) — 追加 3 所 ==========
INSERT IGNORE INTO `edu_college`
(`college_id`, `college_name`, `college_code`, `college_status`, `college_deleted`, `college_remark`, `gmt_create`, `gmt_modified`)
VALUES
(1004, '电子信息工程学院', 'EE',  1, 0, NULL, NOW(), NOW()),
(1005, '经济管理学院',     'EM',  1, 0, NULL, NOW(), NOW()),
(1006, '法学院',           'LAW', 1, 0, NULL, NOW(), NOW());

-- ========== 3. 专业 (edu_major) — 追加 5 个 ==========
INSERT IGNORE INTO `edu_major`
(`major_id`, `major_name`, `major_code`, `college_id`, `major_status`, `major_deleted`, `major_remark`, `gmt_create`, `gmt_modified`)
VALUES
(2006, '电子信息工程',     'EE-EIE',   1004, 1, 0, NULL, NOW(), NOW()),
(2007, '通信工程',         'EE-CE',    1004, 1, 0, NULL, NOW(), NOW()),
(2008, '工商管理',         'EM-BA',    1005, 1, 0, NULL, NOW(), NOW()),
(2009, '会计学',           'EM-AC',    1005, 1, 0, NULL, NOW(), NOW()),
(2010, '法学',             'LAW-LAW',  1006, 1, 0, NULL, NOW(), NOW());

-- ========== 4. 培养方案 (edu_training_plan) — 补齐所有专业 ==========
INSERT IGNORE INTO `edu_training_plan`
(`plan_id`, `plan_name`, `major_id`, `grade`, `version`,
 `total_required_credits`, `major_elective_min_credits`, `general_elective_min_credits`,
 `plan_status`, `plan_deleted`, `plan_remark`, `gmt_create`, `gmt_modified`)
VALUES
(3003, '数据科学与大数据专业 2024 版培养方案', 2003, '2024', 1, 16, 5,  3, 1, 0, NULL, NOW(), NOW()),
(3004, '数学与应用数学专业 2024 版培养方案',   2004, '2024', 1, 16, 4,  3, 1, 0, NULL, NOW(), NOW()),
(3005, '英语专业 2024 版培养方案',             2005, '2024', 1, 14, 6,  3, 1, 0, NULL, NOW(), NOW()),
(3006, '电子信息工程专业 2024 版培养方案',     2006, '2024', 1, 18, 4,  3, 1, 0, NULL, NOW(), NOW()),
(3007, '通信工程专业 2024 版培养方案',         2007, '2024', 1, 16, 5,  3, 1, 0, NULL, NOW(), NOW()),
(3008, '工商管理专业 2024 版培养方案',         2008, '2024', 1, 15, 6,  4, 1, 0, NULL, NOW(), NOW()),
(3009, '会计学专业 2024 版培养方案',           2009, '2024', 1, 16, 5,  3, 1, 0, NULL, NOW(), NOW()),
(3010, '法学专业 2024 版培养方案',             2010, '2024', 1, 16, 4,  4, 1, 0, NULL, NOW(), NOW());

-- ========== 5. 方案-课程关联 (edu_plan_course) — 补齐各方案课程 ==========
INSERT IGNORE INTO `edu_plan_course`
(`rel_id`, `plan_id`, `course_id`, `course_category`, `is_required`, `gmt_create`)
VALUES
-- Plan 3001 (CS-CST): V6 已有 4001-4007 (courses 1,2,3 | 4,5 | 6,7)，这里追加更多课程
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
(4046, 3004, 17, 'GENERAL_ELECTIVE', 0, NOW()),

-- Plan 3005 (FL-EN)
(4047, 3005, 6,  'REQUIRED',         1, NOW()),
(4048, 3005, 17, 'REQUIRED',         1, NOW()),
(4049, 3005, 3,  'REQUIRED',         1, NOW()),
(4050, 3005, 18, 'MAJOR_ELECTIVE',   0, NOW()),
(4051, 3005, 7,  'GENERAL_ELECTIVE', 0, NOW()),

-- Plan 3006 (EE-EIE)
(4052, 3006, 1,  'REQUIRED',         1, NOW()),
(4053, 3006, 2,  'REQUIRED',         1, NOW()),
(4054, 3006, 10,'REQUIRED',         1, NOW()),
(4055, 3006, 9,  'REQUIRED',         1, NOW()),
(4056, 3006, 14, 'MAJOR_ELECTIVE',   0, NOW()),
(4057, 3006, 15, 'MAJOR_ELECTIVE',   0, NOW()),
(4058, 3006, 6,  'GENERAL_ELECTIVE', 0, NOW()),
(4059, 3006, 7,  'GENERAL_ELECTIVE', 0, NOW()),
(4060, 3006, 17, 'GENERAL_ELECTIVE', 0, NOW()),

-- Plan 3007 (EE-CE)
(4061, 3007, 1,  'REQUIRED',         1, NOW()),
(4062, 3007, 2,  'REQUIRED',         1, NOW()),
(4063, 3007, 10,'REQUIRED',         1, NOW()),
(4064, 3007, 9,  'REQUIRED',         1, NOW()),
(4065, 3007, 12, 'REQUIRED',         1, NOW()),
(4066, 3007, 14, 'MAJOR_ELECTIVE',   0, NOW()),
(4067, 3007, 15, 'MAJOR_ELECTIVE',   0, NOW()),
(4068, 3007, 16, 'MAJOR_ELECTIVE',   0, NOW()),
(4069, 3007, 6,  'GENERAL_ELECTIVE', 0, NOW()),
(4070, 3007, 17, 'GENERAL_ELECTIVE', 0, NOW()),
(4071, 3007, 18, 'GENERAL_ELECTIVE', 0, NOW()),

-- Plan 3008 (EM-BA)
(4072, 3008, 3,  'REQUIRED',         1, NOW()),
(4073, 3008, 11, 'REQUIRED',         1, NOW()),
(4074, 3008, 20, 'REQUIRED',         1, NOW()),
(4075, 3008, 15, 'MAJOR_ELECTIVE',   0, NOW()),
(4076, 3008, 16, 'MAJOR_ELECTIVE',   0, NOW()),
(4077, 3008, 6,  'GENERAL_ELECTIVE', 0, NOW()),
(4078, 3008, 7,  'GENERAL_ELECTIVE', 0, NOW()),
(4079, 3008, 17, 'GENERAL_ELECTIVE', 0, NOW()),
(4080, 3008, 18, 'GENERAL_ELECTIVE', 0, NOW()),

-- Plan 3009 (EM-AC)
(4081, 3009, 1,  'REQUIRED',         1, NOW()),
(4082, 3009, 2,  'REQUIRED',         1, NOW()),
(4083, 3009, 11, 'REQUIRED',         1, NOW()),
(4084, 3009, 20, 'REQUIRED',         1, NOW()),
(4085, 3009, 5,  'MAJOR_ELECTIVE',   0, NOW()),
(4086, 3009, 14, 'MAJOR_ELECTIVE',   0, NOW()),
(4087, 3009, 6,  'GENERAL_ELECTIVE', 0, NOW()),
(4088, 3009, 17, 'GENERAL_ELECTIVE', 0, NOW()),
(4089, 3009, 18, 'GENERAL_ELECTIVE', 0, NOW()),

-- Plan 3010 (LAW-LAW)
(4090, 3010, 6,  'REQUIRED',         1, NOW()),
(4091, 3010, 17, 'REQUIRED',         1, NOW()),
(4092, 3010, 3,  'REQUIRED',         1, NOW()),
(4093, 3010, 20, 'REQUIRED',         1, NOW()),
(4094, 3010, 18, 'MAJOR_ELECTIVE',   0, NOW()),
(4095, 3010, 7,  'GENERAL_ELECTIVE', 0, NOW());

-- ========== 6. 教师 (edu_teacher) ==========
INSERT IGNORE INTO `edu_teacher`
(`teacher_id`, `teacher_no`, `teacher_name`, `gender`, `phone`, `email`, `title`,
 `teacher_status`, `teacher_deleted`, `teacher_remark`, `gmt_create`, `gmt_modified`)
VALUES
(6001, 'TCH000001', '张建国', 1, '13800001001', 'zhangjg@example.com', '教授',      1, 0, NULL, NOW(), NOW()),
(6002, 'TCH000002', '李明辉', 1, '13800001002', 'limh@example.com',   '副教授',    1, 0, NULL, NOW(), NOW()),
(6003, 'TCH000003', '王芳',   2, '13800001003', 'wangfang@example.com', '副教授',  1, 0, NULL, NOW(), NOW()),
(6004, 'TCH000004', '陈志远', 1, '13800001004', 'chenzy@example.com',  '讲师',      1, 0, NULL, NOW(), NOW()),
(6005, 'TCH000005', '刘洋',   1, '13800001005', 'liuyang@example.com', '讲师',      1, 0, NULL, NOW(), NOW()),
(6006, 'TCH000006', '赵晓燕', 2, '13800001006', 'zhaoxy@example.com',  '讲师',      1, 0, NULL, NOW(), NOW()),
(6007, 'TCH000007', '周文博', 1, '13800001007', 'zhouwb@example.com',  '副教授',    1, 0, NULL, NOW(), NOW()),
(6008, 'TCH000008', '吴雪梅', 2, '13800001008', 'wuxm@example.com',    '助教',      1, 0, NULL, NOW(), NOW());

-- ========== 7. 班级 (edu_class) ==========
INSERT IGNORE INTO `edu_class`
(`class_id`, `class_name`, `class_code`, `college_id`, `major_id`,
 `grade`, `major`, `class_sort`, `class_status`, `class_deleted`, `class_remark`, `gmt_create`, `gmt_modified`)
VALUES
-- 计算机学院
(5001, '计科2401班', 'CLS-CST-2401', 1001, 2001, '2024', '计算机科学与技术', 1, 1, 0, NULL, NOW(), NOW()),
(5002, '软件2401班', 'CLS-SE-2401',  1001, 2002, '2024', '软件工程',         2, 1, 0, NULL, NOW(), NOW()),
-- 电子信息工程学院
(5003, '电信2401班', 'CLS-EE-2401',  1004, 2006, '2024', '电子信息工程',     1, 1, 0, NULL, NOW(), NOW()),
-- 经济管理学院
(5004, '工商2401班', 'CLS-BA-2401',  1005, 2008, '2024', '工商管理',         1, 1, 0, NULL, NOW(), NOW());

-- 同时更新 V6 残留的旧班级记录（如果存在且 college_id 为空）
UPDATE `edu_class` SET `college_id` = 1001, `major_id` = 2001 WHERE `college_id` IS NULL AND `class_id` != 0;

-- ========== 8. 学生 (edu_student) ==========
INSERT IGNORE INTO `edu_student`
(`student_id`, `student_no`, `student_name`, `gender`, `phone`, `email`, `birthday`,
 `class_id`, `enrollment_year`, `student_status`, `student_deleted`, `student_remark`, `grade`, `gmt_create`, `gmt_modified`)
VALUES
-- 计科2401班 → 用于毕业审核测试
(9001, 'STU20240001', '张三', 1, '13900001001', 'zhangsan@example.com',   '2002-05-15', 5001, '2024', 1, 0, NULL, '2024', NOW(), NOW()),
(9002, 'STU20240002', '李四', 1, '13900001002', 'lisi@example.com',       '2002-08-20', 5001, '2024', 1, 0, NULL, '2024', NOW(), NOW()),
(9003, 'STU20240003', '王五', 1, '13900001003', 'wangwu@example.com',     '2001-12-01', 5001, '2024', 1, 0, NULL, '2024', NOW(), NOW()),
(9004, 'STU20240004', '赵六', 1, '13900001004', 'zhaoliu@example.com',    '2002-03-08', 5001, '2024', 1, 0, NULL, '2024', NOW(), NOW()),
-- 软件2401班
(9005, 'STU20240005', '孙七', 2, '13900001005', 'sunqi@example.com',      '2002-07-12', 5002, '2024', 1, 0, NULL, '2024', NOW(), NOW()),
(9006, 'STU20240006', '周八', 1, '13900001006', 'zhouba@example.com',     '2001-11-25', 5002, '2024', 1, 0, NULL, '2024', NOW(), NOW()),
(9007, 'STU20240007', '吴九', 2, '13900001007', 'wujiu@example.com',      '2003-01-30', 5002, '2024', 1, 0, NULL, '2024', NOW(), NOW()),
-- 电信2401班
(9008, 'STU20240008', '郑十',   1, '13900001008', 'zhengshi@example.com',  '2002-04-18', 5003, '2024', 1, 0, NULL, '2024', NOW(), NOW()),
(9009, 'STU20240009', '冯十一', 2, '13900001009', 'fengsy@example.com',  '2002-09-05', 5003, '2024', 1, 0, NULL, '2024', NOW(), NOW()),
-- 工商2401班
(9010, 'STU20240010', '陈十二', 1, '13900001010', 'chenser@example.com',  '2002-06-22', 5004, '2024', 1, 0, NULL, '2024', NOW(), NOW()),
(9011, 'STU20240011', '褚十三', 2, '13900001011', 'chuss@example.com',     '2003-02-14', 5004, '2024', 1, 0, NULL, '2024', NOW(), NOW()),
-- 休学学生（用于边界测试）
(9012, 'STU20240012', '卫十四', 1, '13900001012', 'weiss@example.com',     '2002-10-30', 5001, '2024', 2, 0, '因病休学', '2024', NOW(), NOW());

-- ========== 9. 选课记录 (edu_student_course) ==========
-- 以下为「毕业审核」功能准备 4 种典型场景的数据：
--   张三: 全部通过 → PASS
--   李四: 缺失必修课 3+8   → FAIL
--   王五: 数据结构(8)分数 45 < 60 → FAIL
--   赵六: 必修全部通过，但专业选修学分不足 → FAIL
INSERT IGNORE INTO `edu_student_course`
(`rel_id`, `student_id`, `course_id`, `score`, `rel_status`, `confirm_status`, `gmt_create`, `gmt_modified`)
VALUES
-- 张三 (9001) — 全部通过
(7001,  9001, 1,  85, 2, 1, NOW(), NOW()),
(7002,  9001, 2,  78, 2, 1, NOW(), NOW()),
(7003,  9001, 3,  92, 2, 1, NOW(), NOW()),
(7004,  9001, 4,  88, 2, 1, NOW(), NOW()),
(7005,  9001, 5,  75, 2, 1, NOW(), NOW()),
(7006,  9001, 6,  82, 2, 1, NOW(), NOW()),
(7007,  9001, 7,  90, 2, 1, NOW(), NOW()),
(7008,  9001, 8,  86, 2, 1, NOW(), NOW()),
(7009,  9001, 9,  79, 2, 1, NOW(), NOW()),
(7010,  9001, 13, 84, 2, 1, NOW(), NOW()),
(7011,  9001, 17, 80, 2, 1, NOW(), NOW()),

-- 李四 (9002) — 缺失必修课 (缺 course 3, 8)
(7012,  9002, 1,  72, 2, 1, NOW(), NOW()),
(7013,  9002, 2,  68, 2, 1, NOW(), NOW()),
(7014,  9002, 4,  80, 2, 1, NOW(), NOW()),
(7015,  9002, 5,  75, 2, 1, NOW(), NOW()),
(7016,  9002, 6,  82, 2, 1, NOW(), NOW()),
(7017,  9002, 7,  90, 2, 1, NOW(), NOW()),
(7018,  9002, 9,  65, 2, 1, NOW(), NOW()),
(7019,  9002, 13, 84, 2, 1, NOW(), NOW()),
(7020,  9002, 17, 80, 2, 1, NOW(), NOW()),

-- 王五 (9003) — 数据结构(8)不及格
(7021,  9003, 1,  85, 2, 1, NOW(), NOW()),
(7022,  9003, 2,  78, 2, 1, NOW(), NOW()),
(7023,  9003, 3,  92, 2, 1, NOW(), NOW()),
(7024,  9003, 4,  88, 2, 1, NOW(), NOW()),
(7025,  9003, 5,  75, 2, 1, NOW(), NOW()),
(7026,  9003, 6,  82, 2, 1, NOW(), NOW()),
(7027,  9003, 7,  90, 2, 1, NOW(), NOW()),
(7028,  9003, 8,  45, 2, 1, NOW(), NOW()),
(7029,  9003, 9,  79, 2, 1, NOW(), NOW()),
(7030,  9003, 13, 84, 2, 1, NOW(), NOW()),
(7031,  9003, 17, 80, 2, 1, NOW(), NOW()),

-- 赵六 (9004) — 必修全过，但专业选修只拿了 2.0 分（缺 2.0）
(7032,  9004, 1,  88, 2, 1, NOW(), NOW()),
(7033,  9004, 2,  82, 2, 1, NOW(), NOW()),
(7034,  9004, 3,  95, 2, 1, NOW(), NOW()),
(7035,  9004, 4,  78, 2, 1, NOW(), NOW()),
(7036,  9004, 8,  90, 2, 1, NOW(), NOW()),
(7037,  9004, 9,  87, 2, 1, NOW(), NOW()),
(7038,  9004, 6,  80, 2, 1, NOW(), NOW()),
(7039,  9004, 7,  91, 2, 1, NOW(), NOW()),
(7040,  9004, 17, 76, 2, 1, NOW(), NOW());

-- ========== 10. 毕业审核样例结果 (edu_graduation_result) ==========
INSERT IGNORE INTO `edu_graduation_result`
(`result_id`, `student_id`, `plan_id`, `total_earned_credits`,
 `required_earned_credits`, `major_elective_earned_credits`, `general_elective_earned_credits`,
 `passed`, `missing_items`, `review_time`, `gmt_create`)
VALUES
(8001, 9001, 3001, 29.5, 18.0, 6.0, 5.5, 1, NULL, '2025-07-10 10:00:00', '2025-07-10 10:00:00'),
(8002, 9002, 3001, 18.5, 12.0, 4.0, 2.5, 0,
 JSON_ARRAY('必修课未通过: 程序设计基础 (课程ID=3)', '必修课未通过: 数据结构与算法 (课程ID=8)'),
 '2025-07-10 10:05:00', '2025-07-10 10:05:00');
