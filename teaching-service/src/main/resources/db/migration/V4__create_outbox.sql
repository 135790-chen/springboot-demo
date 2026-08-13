-- ============================================
-- Flyway V4 — 选课本地表（edu_enrollment_outbox）
-- 用于保证 Redis 扣库存 → Kafka → MySQL 的最终一致性
-- 状态: 0=PENDING 1=SUCCESS 2=FAILED
-- ============================================
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `edu_enrollment_outbox` (
    `outbox_id`     BIGINT       NOT NULL COMMENT '主键，雪花ID',
    `request_id`    VARCHAR(64)  NOT NULL COMMENT 'Redis Lua 去重 requestId',
    `student_id`    BIGINT       NOT NULL COMMENT '学生ID',
    `course_id`     BIGINT       NOT NULL COMMENT '课程ID',
    `status`        INT          DEFAULT 0 COMMENT '0=PENDING 1=SUCCESS 2=FAILED',
    `retry_count`   INT          DEFAULT 0 COMMENT '重试次数',
    `error_msg`     VARCHAR(512) DEFAULT NULL COMMENT '最后一次错误信息',
    `gmt_create`    DATETIME     NOT NULL COMMENT '创建时间',
    `gmt_modified`  DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`outbox_id`),
    INDEX `idx_status_gmt` (`status`, `gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='选课本地表（最终一致性保障）';
