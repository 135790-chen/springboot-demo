-- ============================================
-- Flyway V6 — 排课系统核心模块
-- 包含：教室表、时间段表、排课结果表 + 种子数据
-- ============================================
SET NAMES utf8mb4;

-- ========== 1. 教室表 ==========
CREATE TABLE IF NOT EXISTS `edu_classroom` (
  `classroom_id`      BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `classroom_name`    VARCHAR(128) NOT NULL COMMENT '教室名称',
  `classroom_code`    VARCHAR(64)  NOT NULL COMMENT '教室编码',
  `capacity`          INT          NOT NULL DEFAULT 0  COMMENT '容量（座位数）',
  `classroom_type`    VARCHAR(32)  NOT NULL DEFAULT 'NORMAL' COMMENT '教室类型：NORMAL/MULTIMEDIA/LAB/LECTURE_HALL',
  `location`          VARCHAR(256) DEFAULT NULL COMMENT '位置描述',
  `building`          VARCHAR(64)  DEFAULT NULL COMMENT '楼栋',
  `floor`             INT          DEFAULT NULL COMMENT '楼层',
  `classroom_status`  INT          DEFAULT 1  COMMENT '状态：1-可用 0-禁用',
  `classroom_deleted` INT          DEFAULT 0  COMMENT '删除状态：1-删除 0-未删除',
  `classroom_remark`  VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `gmt_create`        DATETIME     NOT NULL COMMENT '创建时间',
  `gmt_modified`      DATETIME     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`classroom_id`),
  UNIQUE INDEX `uk_classroom_code` (`classroom_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教室表';

-- ========== 2. 时间段表 ==========
CREATE TABLE IF NOT EXISTS `edu_time_slot` (
  `slot_id`       BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `slot_name`     VARCHAR(64)  NOT NULL COMMENT '时段名称，如"周一第1-2节"',
  `day_of_week`   INT          NOT NULL COMMENT '星期几：1-周一 … 7-周日',
  `start_period`  INT          NOT NULL COMMENT '开始节次，1-12',
  `end_period`    INT          NOT NULL COMMENT '结束节次，1-12',
  `slot_status`   INT          DEFAULT 1  COMMENT '状态：1-可用 0-禁用',
  `gmt_create`    DATETIME     NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`slot_id`),
  UNIQUE INDEX `uk_day_period` (`day_of_week`, `start_period`, `end_period`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='时间段表';

-- ========== 3. 排课结果表 ==========
CREATE TABLE IF NOT EXISTS `edu_schedule` (
  `schedule_id`     BIGINT       NOT NULL COMMENT '主键，雪花ID',
  `course_id`       BIGINT       NOT NULL COMMENT '课程ID',
  `teacher_id`      BIGINT       NOT NULL COMMENT '教师ID',
  `classroom_id`    BIGINT       NOT NULL COMMENT '教室ID',
  `time_slot_id`    BIGINT       NOT NULL COMMENT '时间段ID',
  `clazz_id`        BIGINT       NOT NULL COMMENT '班级ID',
  `semester`        VARCHAR(32)  NOT NULL COMMENT '学期，如"2025-2026-1"',
  `week_start`      INT          DEFAULT 1  COMMENT '起始教学周',
  `week_end`        INT          DEFAULT 18 COMMENT '结束教学周',
  `schedule_status` INT          DEFAULT 1  COMMENT '状态：1-正常 0-取消',
  `schedule_deleted` INT         DEFAULT 0  COMMENT '删除状态：1-删除 0-未删除',
  `schedule_remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `gmt_create`      DATETIME     NOT NULL COMMENT '创建时间',
  `gmt_modified`    DATETIME     NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`schedule_id`),
  UNIQUE INDEX `uk_teacher_timeslot_semester` (`teacher_id`, `time_slot_id`, `semester`, `schedule_deleted`),
  UNIQUE INDEX `uk_classroom_timeslot_semester` (`classroom_id`, `time_slot_id`, `semester`, `schedule_deleted`),
  UNIQUE INDEX `uk_class_timeslot_semester` (`clazz_id`, `time_slot_id`, `semester`, `schedule_deleted`),
  INDEX `idx_schedule_semester` (`semester`),
  INDEX `idx_schedule_teacher` (`teacher_id`, `semester`),
  INDEX `idx_schedule_classroom` (`classroom_id`, `semester`),
  INDEX `idx_schedule_class` (`clazz_id`, `semester`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='排课结果表';

-- ========== 4. 种子数据：时间段（周一至周五，每天5个时段 = 25条） ==========
INSERT IGNORE INTO `edu_time_slot` (`slot_id`, `slot_name`, `day_of_week`, `start_period`, `end_period`, `slot_status`, `gmt_create`) VALUES
(7001, '周一第1-2节',  1, 1, 2, 1, NOW()),
(7002, '周一第3-4节',  1, 3, 4, 1, NOW()),
(7003, '周一第5-6节',  1, 5, 6, 1, NOW()),
(7004, '周一第7-8节',  1, 7, 8, 1, NOW()),
(7005, '周一第9-10节', 1, 9, 10, 1, NOW()),
(7006, '周二第1-2节',  2, 1, 2, 1, NOW()),
(7007, '周二第3-4节',  2, 3, 4, 1, NOW()),
(7008, '周二第5-6节',  2, 5, 6, 1, NOW()),
(7009, '周二第7-8节',  2, 7, 8, 1, NOW()),
(7010, '周二第9-10节', 2, 9, 10, 1, NOW()),
(7011, '周三第1-2节',  3, 1, 2, 1, NOW()),
(7012, '周三第3-4节',  3, 3, 4, 1, NOW()),
(7013, '周三第5-6节',  3, 5, 6, 1, NOW()),
(7014, '周三第7-8节',  3, 7, 8, 1, NOW()),
(7015, '周三第9-10节', 3, 9, 10, 1, NOW()),
(7016, '周四第1-2节',  4, 1, 2, 1, NOW()),
(7017, '周四第3-4节',  4, 3, 4, 1, NOW()),
(7018, '周四第5-6节',  4, 5, 6, 1, NOW()),
(7019, '周四第7-8节',  4, 7, 8, 1, NOW()),
(7020, '周四第9-10节', 4, 9, 10, 1, NOW()),
(7021, '周五第1-2节',  5, 1, 2, 1, NOW()),
(7022, '周五第3-4节',  5, 3, 4, 1, NOW()),
(7023, '周五第5-6节',  5, 5, 6, 1, NOW()),
(7024, '周五第7-8节',  5, 7, 8, 1, NOW()),
(7025, '周五第9-10节', 5, 9, 10, 1, NOW());

-- ========== 5. 种子数据：教室 ==========
INSERT IGNORE INTO `edu_classroom`
(`classroom_id`, `classroom_name`, `classroom_code`, `capacity`, `classroom_type`, `location`, `building`, `floor`, `classroom_status`, `classroom_deleted`, `gmt_create`, `gmt_modified`)
VALUES
(8001, '教学楼A-101', 'BLDG-A-101', 120, 'LECTURE_HALL', 'A栋1楼东侧',   '教学楼A', 1, 1, 0, NOW(), NOW()),
(8002, '教学楼A-201', 'BLDG-A-201', 80,  'MULTIMEDIA',   'A栋2楼东侧',   '教学楼A', 2, 1, 0, NOW(), NOW()),
(8003, '教学楼A-301', 'BLDG-A-301', 80,  'MULTIMEDIA',   'A栋3楼东侧',   '教学楼A', 3, 1, 0, NOW(), NOW()),
(8004, '教学楼B-101', 'BLDG-B-101', 60,  'NORMAL',       'B栋1楼西侧',   '教学楼B', 1, 1, 0, NOW(), NOW()),
(8005, '教学楼B-201', 'BLDG-B-201', 60,  'NORMAL',       'B栋2楼西侧',   '教学楼B', 2, 1, 0, NOW(), NOW()),
(8006, '实验楼C-201', 'LAB-C-201',  40,  'LAB',          'C栋2楼计算机中心','实验楼C', 2, 1, 0, NOW(), NOW()),
(8007, '实验楼C-301', 'LAB-C-301',  40,  'LAB',          'C栋3楼计算机中心','实验楼C', 3, 1, 0, NOW(), NOW()),
(8008, '综合楼D-101', 'BLDG-D-101', 150, 'LECTURE_HALL', 'D栋1楼报告厅',  '综合楼D', 1, 1, 0, NOW(), NOW());
