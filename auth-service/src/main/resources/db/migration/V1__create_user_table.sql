-- ============================================
-- Flyway V1 — 用户表（基线版本）
-- 包含：表结构、主键、唯一索引、角色字段
-- ============================================
CREATE TABLE IF NOT EXISTS `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT,
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt 加密）',
  `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `role`        VARCHAR(20)  DEFAULT 'student' COMMENT '角色：admin / student',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
