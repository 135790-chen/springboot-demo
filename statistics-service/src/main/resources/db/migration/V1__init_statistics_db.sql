-- 教育统计表（定时任务每日快照）
CREATE TABLE IF NOT EXISTS edu_stat (
    stat_id      BIGINT PRIMARY KEY COMMENT '统计ID',
    stat_date    DATE NOT NULL COMMENT '统计日期',
    total_students     INT DEFAULT 0 COMMENT '在读学生数',
    total_teachers     INT DEFAULT 0 COMMENT '在职教师数',
    total_courses      INT DEFAULT 0 COMMENT '开课课程数',
    total_enrollments  INT DEFAULT 0 COMMENT '在读选课记录数',
    avg_score   DECIMAL(5,2) DEFAULT NULL COMMENT '已修完课程平均分',
    fail_count  INT DEFAULT 0 COMMENT '不及格人次（score < 60）',
    gmt_create  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教育统计快照';
