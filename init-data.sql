-- ============================================
-- 教育管理系统测试数据（适配 V3 edu_* 表结构）
-- 运行方式: mysql -u root -p123456 student_db < init-data.sql
-- ============================================

-- ========== 教师（4人）==========
INSERT INTO edu_teacher (teacher_id, teacher_no, teacher_name, gender, phone, email, title,
  teacher_status, teacher_deleted, gmt_create, gmt_modified) VALUES
(1, 'TCH000001', '张明', 1, '13900000001', 'zhangming@edu.cn', '教授',       1, 0, NOW(), NOW()),
(2, 'TCH000002', '李华', 2, '13900000002', 'lihua@edu.cn',     '副教授',     1, 0, NOW(), NOW()),
(3, 'TCH000003', '王丽', 2, '13900000003', 'wangli@edu.cn',    '讲师',       1, 0, NOW(), NOW()),
(4, 'TCH000004', '陈强', 1, '13900000004', 'chenqiang@edu.cn', '教授',       1, 0, NOW(), NOW());

-- ========== 班级（5个）==========
INSERT INTO edu_class (class_id, class_name, class_code, grade, major, class_sort,
  class_status, class_deleted, class_remark, gmt_create, gmt_modified) VALUES
(1, '计算机科学2024-1班', 'CLS000001', '2024级', '计算机科学与技术', 0, 1, 0, '班主任:张明', NOW(), NOW()),
(2, '计算机科学2023-1班', 'CLS000002', '2023级', '计算机科学与技术', 0, 1, 0, '班主任:李华', NOW(), NOW()),
(3, '计算机科学2023-2班', 'CLS000003', '2023级', '计算机科学与技术', 0, 1, 0, '班主任:张明', NOW(), NOW()),
(4, '计算机科学2022-1班', 'CLS000004', '2022级', '计算机科学与技术', 0, 1, 0, '班主任:陈强', NOW(), NOW()),
(5, '计算机科学2021-1班', 'CLS000005', '2021级', '计算机科学与技术', 0, 1, 0, '班主任:王丽', NOW(), NOW());

-- ========== 课程（8门）==========
INSERT INTO edu_course (course_id, course_name, course_code, credit, course_hours, course_type,
  teacher_id, semester, course_status, course_deleted, gmt_create, gmt_modified) VALUES
(1, 'Java程序设计',   'CRS000001', 4.0, 64, 'required', 2, '2024-2025-1', 1, 0, NOW(), NOW()),
(2, '数据结构',       'CRS000002', 3.5, 56, 'required', 1, '2024-2025-1', 1, 0, NOW(), NOW()),
(3, '操作系统',       'CRS000003', 3.5, 56, 'required', 1, '2024-2025-1', 1, 0, NOW(), NOW()),
(4, 'Web开发',        'CRS000004', 3.0, 48, 'required', 2, '2024-2025-2', 1, 0, NOW(), NOW()),
(5, '人工智能导论',   'CRS000005', 2.0, 32, 'elective', 3, '2025-2026-1', 1, 0, NOW(), NOW()),
(6, 'Python基础',     'CRS000006', 2.5, 40, 'elective', 3, '2024-2025-1', 1, 0, NOW(), NOW()),
(7, '高等数学',       'CRS000007', 5.0, 80, 'required', 4, '2024-2025-1', 1, 0, NOW(), NOW()),
(8, '线性代数',       'CRS000008', 4.0, 64, 'required', 4, '2024-2025-2', 1, 0, NOW(), NOW());

-- ========== 学生（现有19人 → edu_student 格式）==========
INSERT INTO edu_student (student_id, student_no, student_name, gender, phone, email,
  birthday, class_id, enrollment_year, student_status, student_deleted, grade, gmt_create, gmt_modified) VALUES
(1,  'STU00000001', 'ZhangSan',     1, '13800000001', 'zhangsan@example.com',    '2002-05-15', 2, '2023', 1, 0, '大二', NOW(), NOW()),
(2,  'STU00000002', 'LiSi',         2, '13800000002', 'lisi@example.com',        '2002-08-22', 3, '2023', 1, 0, '大二', NOW(), NOW()),
(3,  'STU00000003', 'WangWu',       1, '13800000003', 'wangwu@example.com',      '2001-03-10', 4, '2022', 1, 0, '大三', NOW(), NOW()),
(4,  'STU00000004', 'ZhaoLiu',      2, '13800000004', 'zhaoliu@example.com',     '2000-11-30', 5, '2021', 1, 0, '大四', NOW(), NOW()),
(5,  'STU00000005', 'SunQi',        1, '13800000005', 'sunqi@example.com',       '2003-01-18', 1, '2024', 1, 0, '大一', NOW(), NOW()),
(6,  'STU00000006', 'ZhouBa',       2, '13800000006', 'zhouba@example.com',      '2004-07-05', 1, '2024', 1, 0, '大一', NOW(), NOW()),
(7,  'STU00000007', 'WuJiu',        1, '13800000007', 'wujiu@example.com',       '2002-12-12', 2, '2023', 1, 0, '大二', NOW(), NOW()),
(8,  'STU00000008', 'ZhengShi',     2, '13800000008', 'zhengshi@example.com',    '2001-06-25', 4, '2022', 1, 0, '大三', NOW(), NOW()),
(9,  'STU00000009', 'QianShiyi',    1, '13800000009', 'qianshiyi@example.com',   '2003-04-14', 3, '2023', 1, 0, '大二', NOW(), NOW()),
(10, 'STU00000010', 'LiuShier',     2, '13800000010', 'liushier@example.com',    '2000-09-08', 5, '2021', 1, 0, '大四', NOW(), NOW()),
(11, 'STU00000011', 'HuangShisan',  1, '13800000011', 'huangshisan@example.com', '2001-02-28', 4, '2022', 1, 0, '大三', NOW(), NOW()),
(12, 'STU00000012', 'LinShisi',     2, '13800000012', 'linshisi@example.com',    '2004-10-17', 1, '2024', 1, 0, '大一', NOW(), NOW()),
(13, 'STU00000013', 'YangShiwu',    1, '13800000013', 'yangshiwu@example.com',   '2002-08-03', 3, '2023', 1, 0, '大二', NOW(), NOW()),
(14, 'STU00000014', 'ChenShiliu',   2, '13800000014', 'chenshiliu@example.com',  '2001-05-20', 2, '2023', 1, 0, '大二', NOW(), NOW()),
(15, 'STU00000015', 'HeShiqi',      1, '13800000015', 'heshiqi@example.com',     '2000-01-15', 5, '2021', 1, 0, '大四', NOW(), NOW()),
(16, 'STU00000016', 'GaoShiba',     2, '13800000016', 'gaoshiba@example.com',    '2003-11-09', 1, '2024', 1, 0, '大一', NOW(), NOW()),
(17, 'STU00000017', 'LuoShijiu',    1, '13800000017', 'luoshijiu@example.com',   '2004-03-22', 2, '2023', 1, 0, '大二', NOW(), NOW()),
(18, 'STU00000018', 'MaErshi',      2, '13800000018', 'maershi@example.com',     '2001-08-14', 4, '2022', 1, 0, '大三', NOW(), NOW()),
(19, 'STU00000019', 'ZhuErshiyi',   1, '13800000019', 'zhuershiyi@example.com',  '2002-06-30', 3, '2023', 1, 0, '大二', NOW(), NOW());

-- ========== 选课记录（97条）==========
-- 大二以上选专业基础课
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified)
SELECT 100000 + s.student_id * 100 + c.course_id,
       s.student_id, c.course_id,
       70 + FLOOR(RAND() * 30), 2,
       NOW(), NOW()
FROM edu_student s
CROSS JOIN edu_course c
WHERE s.grade IN ('大二', '大三', '大四')
  AND c.course_name IN ('Java程序设计', '数据结构', '操作系统');

-- 大三以上选高阶课
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified)
SELECT 200000 + s.student_id * 100 + c.course_id,
       s.student_id, c.course_id,
       65 + FLOOR(RAND() * 35), 2,
       NOW(), NOW()
FROM edu_student s
CROSS JOIN edu_course c
WHERE s.grade IN ('大三', '大四')
  AND c.course_name IN ('Web开发', '高等数学', '线性代数');

-- 大四选修AI和Python
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified)
SELECT 300000 + s.student_id * 100 + c.course_id,
       s.student_id, c.course_id,
       75 + FLOOR(RAND() * 25), 2,
       NOW(), NOW()
FROM edu_student s
CROSS JOIN edu_course c
WHERE s.grade IN ('大四')
  AND c.course_name IN ('人工智能导论', 'Python基础');

-- 大一选Python基础和高数
INSERT INTO edu_student_course (rel_id, student_id, course_id, score, rel_status, gmt_create, gmt_modified)
SELECT 400000 + s.student_id * 100 + c.course_id,
       s.student_id, c.course_id,
       60 + FLOOR(RAND() * 40), 2,
       NOW(), NOW()
FROM edu_student s
CROSS JOIN edu_course c
WHERE s.grade IN ('大一')
  AND c.course_name IN ('Python基础', '高等数学');
