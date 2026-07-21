-- ============================================
-- Flyway V1 — 学生表（基线版本）
-- 包含：表结构、主键、邮箱唯一索引、年龄约束
-- ============================================
CREATE TABLE IF NOT EXISTS `student` (
  `id`    BIGINT       NOT NULL AUTO_INCREMENT,
  `name`  VARCHAR(50)  NOT NULL COMMENT '姓名',
  `age`   INT          NOT NULL COMMENT '年龄',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `grade` VARCHAR(20)  NOT NULL COMMENT '年级',
  `phone` VARCHAR(20)  DEFAULT NULL COMMENT '电话',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_student_email` (`email`),
  CONSTRAINT `chk_age_positive` CHECK (`age` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生表';
