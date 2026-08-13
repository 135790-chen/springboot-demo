-- ============================================
-- Flyway V3 — RBAC 权限体系
-- 创建 5 张 RBAC 表 + 种子数据 + 迁移现有用户
-- ============================================

-- 1. 系统用户表（与现有 user 表并行，逐步迁移）
CREATE TABLE `sys_user` (
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username`    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT 'BCrypt 加密密码',
    `email`       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `status`      INT          DEFAULT 1 COMMENT '1-正常, 0-禁用',
    `gmt_create`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 角色表
CREATE TABLE `sys_role` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `role_code`  VARCHAR(32) NOT NULL UNIQUE COMMENT '角色编码: SUPER_ADMIN, ACADEMIC_ADMIN, ...',
    `role_name`  VARCHAR(64) NOT NULL COMMENT '角色名称',
    `gmt_create` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 3. 用户-角色关联表
CREATE TABLE `sys_user_role` (
    `id`      BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT 'sys_user.id',
    `role_id` BIGINT NOT NULL COMMENT 'sys_role.id',
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 4. 权限表
CREATE TABLE `sys_permission` (
    `id`         BIGINT AUTO_INCREMENT PRIMARY KEY,
    `perm_code`  VARCHAR(64) NOT NULL UNIQUE COMMENT '权限编码: course:view, course:edit, ...',
    `perm_name`  VARCHAR(64) NOT NULL COMMENT '权限名称',
    `gmt_create` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 5. 角色-权限关联表
CREATE TABLE `sys_role_permission` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `role_id`       BIGINT NOT NULL COMMENT 'sys_role.id',
    `permission_id` BIGINT NOT NULL COMMENT 'sys_permission.id',
    UNIQUE KEY `uk_role_perm` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================
-- 种子数据：9 种角色
-- ============================================
INSERT INTO `sys_role` (`role_code`, `role_name`) VALUES
('SUPER_ADMIN',     '超级管理员'),
('ACADEMIC_ADMIN',  '教务处'),
('COLLEGE_ADMIN',   '学院管理员'),
('MAJOR_DIRECTOR',  '专业负责人'),
('TEACHER',         '任课教师'),
('COUNSELOR',       '辅导员'),
('STUDENT',         '学生'),
('SUPERVISOR',      '督导'),
('LEADER',          '校领导');

-- ============================================
-- 种子数据：权限项
-- ============================================
INSERT INTO `sys_permission` (`perm_code`, `perm_name`) VALUES
-- 用户管理
('user:view',       '查看用户'),
('user:create',     '创建用户'),
('user:edit',       '编辑用户'),
('user:delete',     '删除用户'),
-- 角色权限管理
('role:view',       '查看角色'),
('role:assign',     '分配角色'),
('permission:view', '查看权限'),
('permission:manage','管理权限'),
-- 学校管理
('school:view',     '查看学校'),
('school:manage',   '管理学校'),
-- 学院管理
('college:view',    '查看学院'),
('college:manage',  '管理学院'),
-- 专业管理
('major:view',      '查看专业'),
('major:manage',    '管理专业'),
-- 班级管理
('class:view',      '查看班级'),
('class:manage',    '管理班级'),
-- 学生管理
('student:view',    '查看学生'),
('student:manage',  '管理学生'),
-- 教师管理
('teacher:view',    '查看教师'),
('teacher:manage',  '管理教师'),
-- 课程管理
('course:view',     '查看课程'),
('course:manage',   '管理课程'),
-- 成绩管理
('score:view',      '查看成绩'),
('score:input',     '录入成绩'),
-- 培养方案
('training_plan:view',   '查看培养方案'),
('training_plan:manage', '管理培养方案'),
-- 选课管理
('enrollment:view',    '查看选课'),
('enrollment:manage',  '管理选课'),
-- 毕业审核
('graduation:view',    '查看毕业审核'),
('graduation:review',  '执行毕业审核'),
-- 统计
('stat:view',       '查看统计数据'),
-- 督导评价
('evaluation:view',   '查看评价'),
('evaluation:create', '创建评价'),
-- 校领导
('dashboard:view',  '查看数据驾驶舱');

-- ============================================
-- 种子数据：角色-权限关联
-- ============================================

-- SUPER_ADMIN: 全部权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT (SELECT id FROM sys_role WHERE role_code = 'SUPER_ADMIN'), id FROM sys_permission;

-- ACADEMIC_ADMIN: 教学管理相关
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'ACADEMIC_ADMIN'
  AND p.perm_code IN ('course:view', 'course:manage',
                      'training_plan:view', 'training_plan:manage',
                      'enrollment:view', 'enrollment:manage',
                      'graduation:view', 'graduation:review',
                      'stat:view', 'score:view',
                      'student:view', 'teacher:view', 'class:view',
                      'college:view', 'major:view', 'school:view');

-- COLLEGE_ADMIN: 本学院管理
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'COLLEGE_ADMIN'
  AND p.perm_code IN ('college:view', 'college:manage',
                      'major:view', 'major:manage',
                      'class:view', 'class:manage',
                      'student:view', 'student:manage',
                      'teacher:view', 'teacher:manage',
                      'course:view', 'score:view', 'enrollment:view');

-- MAJOR_DIRECTOR: 培养方案管理
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'MAJOR_DIRECTOR'
  AND p.perm_code IN ('training_plan:view', 'training_plan:manage',
                      'course:view', 'major:view',
                      'student:view', 'class:view');

-- TEACHER: 授课 + 成绩录入
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'TEACHER'
  AND p.perm_code IN ('course:view', 'score:view', 'score:input',
                      'student:view', 'class:view');

-- COUNSELOR: 班级学生管理
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'COUNSELOR'
  AND p.perm_code IN ('student:view', 'class:view', 'enrollment:view',
                      'score:view', 'graduation:view');

-- STUDENT: 只读个人信息 + 选课
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'STUDENT'
  AND p.perm_code IN ('student:view', 'course:view', 'score:view',
                      'enrollment:view', 'enrollment:manage');

-- SUPERVISOR: 听课评价
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'SUPERVISOR'
  AND p.perm_code IN ('evaluation:view', 'evaluation:create',
                      'course:view', 'teacher:view', 'class:view');

-- LEADER: 数据看板（只读）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'LEADER'
  AND p.perm_code IN ('dashboard:view', 'stat:view',
                      'student:view', 'teacher:view', 'course:view',
                      'college:view', 'major:view', 'school:view');

-- ============================================
-- 迁移现有 user 表数据到 sys_user
-- ============================================
INSERT INTO `sys_user` (`id`, `username`, `password`, `email`, `status`, `gmt_create`)
SELECT `id`, `username`, `password`, `email`, 1, `create_time`
FROM `user`
ON DUPLICATE KEY UPDATE `username` = VALUES(`username`);

-- ============================================
-- 迁移现有角色到 sys_user_role
-- ============================================
-- admin → SUPER_ADMIN
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, (SELECT id FROM sys_role WHERE role_code = 'SUPER_ADMIN')
FROM `user` u
WHERE u.role = 'admin'
  AND NOT EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id
                  AND ur.role_id = (SELECT id FROM sys_role WHERE role_code = 'SUPER_ADMIN'));

-- student → STUDENT
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, (SELECT id FROM sys_role WHERE role_code = 'STUDENT')
FROM `user` u
WHERE u.role = 'student'
  AND NOT EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id
                  AND ur.role_id = (SELECT id FROM sys_role WHERE role_code = 'STUDENT'));
