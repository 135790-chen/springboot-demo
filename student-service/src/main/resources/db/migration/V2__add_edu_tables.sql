-- V2: 扩展教育管理系统 —— 教师、课程、班级、选课
-- 依赖: V1 需先执行 (student 表已存在)

-- 教师表
CREATE TABLE IF NOT EXISTS teacher
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL COMMENT '教师姓名',
    email       VARCHAR(100) COMMENT '邮箱',
    phone       VARCHAR(20) COMMENT '电话',
    title       VARCHAR(50) COMMENT '职称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_teacher_email (email)
) COMMENT '教师';

-- 课程表
CREATE TABLE IF NOT EXISTS course
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL COMMENT '课程名称',
    credit      DECIMAL(2, 1) NOT NULL DEFAULT 1.0 COMMENT '学分',
    teacher_id  BIGINT COMMENT '授课教师ID',
    semester    VARCHAR(20) COMMENT '学期 (例: 2024-秋季)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '课程';

-- 班级表
CREATE TABLE IF NOT EXISTS clazz
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL COMMENT '班级名称',
    grade           VARCHAR(20) COMMENT '年级',
    head_teacher_id BIGINT COMMENT '班主任ID',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '班级';

-- 选课表 (student N:M course)
CREATE TABLE IF NOT EXISTS enrollment
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id  BIGINT NOT NULL COMMENT '课程ID',
    score      DECIMAL(4, 1) COMMENT '成绩',
    semester   VARCHAR(20) COMMENT '学期',
    UNIQUE KEY uk_student_course (student_id, course_id)
) COMMENT '选课记录';

-- 给 student 表补班级字段
ALTER TABLE student
    ADD COLUMN class_id BIGINT COMMENT '班级ID' AFTER grade;
