# 智慧教务系统 — 未来规划蓝图

> ⚠️ 本文档为**未来规划**，当前代码库尚未实现。现有系统是一个聚焦学生/教师/课程/选课 CRUD 的微服务教学项目。

## 一、核心设计原则

### 身份与业务对象分离

不使用继承（`class Student extends User`），而是通过 RBAC 中间表解耦：

```
User ─── UserRole ─── Role ─── RolePermission ─── Permission
 │
 └── Student / Teacher / CollegeAdmin / …  (业务实体，独立于 User)
```

### RBAC 表结构

```sql
-- 用户表
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(128),
  status INT DEFAULT 1,
  gmt_create DATETIME,
  gmt_modified DATETIME
);

-- 角色表
CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY,
  role_code VARCHAR(32) UNIQUE NOT NULL,  -- SUPER_ADMIN, ACADEMIC_ADMIN, COLLEGE_ADMIN, TEACHER, STUDENT …
  role_name VARCHAR(64) NOT NULL,
  gmt_create DATETIME
);

-- 用户-角色关联
CREATE TABLE sys_user_role (
  id BIGINT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  UNIQUE (user_id, role_id)
);

-- 权限表
CREATE TABLE sys_permission (
  id BIGINT PRIMARY KEY,
  perm_code VARCHAR(64) UNIQUE NOT NULL,  -- course:view, course:edit, score:input …
  perm_name VARCHAR(64) NOT NULL
);

-- 角色-权限关联
CREATE TABLE sys_role_permission (
  id BIGINT PRIMARY KEY,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  UNIQUE (role_id, permission_id)
);
```

---

## 二、组织架构层级

```
School (学校)
  │
  └── College (学院)
        │
        └── Major (专业)
              │
              ├── Class (班级) → Student (学生)
              │     └── counselor_id → Counselor (辅导员)
              │
              ├── TrainingPlan (培养方案)
              │     └── PlanCourse → Course (课程)
              │
              └── CourseTeacher (开课安排)
                    ├── Teacher (教师)
                    └── Course (课程)
```

### 表关系速查

| 子表 | 外键 | 父表 | 关系 |
|------|------|------|------|
| College | school_id | School | N:1 |
| Major | college_id | College | N:1 |
| Class | major_id | Major | N:1 |
| Class | counselor_id | Counselor | N:1 |
| Student | class_id | Class | N:1 |
| TrainingPlan | major_id | Major | N:1 |
| PlanCourse | plan_id, course_id | Plan, Course | M:N |
| CourseTeacher | course_id, teacher_id | Course, Teacher | M:N |
| Enrollment | student_id, course_id | Student, Course | M:N |

---

## 三、角色体系

### 1. 超级管理员 (SuperAdmin)
- **实体**: 不单独建表，通过 `sys_user_role.role_code = 'SUPER_ADMIN'` 标识
- **权限**: 系统配置、用户管理、角色/权限管理、日志查看
- **API**: `/api/admin/users`, `/api/admin/roles`, `/api/admin/logs`

### 2. 教务处 (AcademicAdmin)
- **职责**: 全校教学管理——课程审批、培养方案审核、教学计划审核、排课管理、成绩监管
- **API**:
  - `POST /api/academic/plans` — 创建教学计划
  - `PUT /api/academic/plans/{id}/approve` — 审核方案

### 3. 学院管理员 (CollegeAdmin)
- **关系**: 属于一个学院（`college_admin.college_id = College.id`）
- **权限**: 管理本学院教师、专业、课程、班级
- **API**:
  - `GET /api/college/{id}/teachers`
  - `POST /api/college/teachers`
  - `DELETE /api/college/teachers/{id}`

### 4. 专业负责人 (MajorDirector)
- **关系**: `Major.director_id → User`
- **职责**: 制定和维护培养方案
- **API**:
  - `POST /api/major/{id}/training-plan`
  - `PUT /api/training-plan/{id}/submit`

### 5. 任课教师 (Teacher)
- **关系**: 通过 `CourseTeacher` 中间表关联课程（一个老师多门课，一门课多个老师）
- **API**:
  - `GET /api/teacher/courses` — 我的课程
  - `POST /api/teacher/scores` — 成绩录入

### 6. 辅导员 (Counselor)
- **关系**: `Class.counselor_id → Counselor`
- **职责**: 查看班级学生学籍、成绩、选课情况
- **API**:
  - `GET /api/counselor/classes`
  - `GET /api/counselor/students`
  - `GET /api/counselor/student/{id}`

### 7. 学生 (Student)
- **关系**: Student → Class → Major → College
- **API**:
  - `GET /api/student/courses`
  - `POST /api/student/course/select`
  - `DELETE /api/student/course/{id}`
  - `GET /api/student/scores`

### 8. 督导 (Supervisor)
- **关系**: 通过 `teacher_evaluation` 中间表跟踪多位教师
- **API**:
  - `GET /api/supervisor/classes` — 听课查看
  - `POST /api/supervisor/evaluation` — 提交评价

### 9. 校领导 (Leader)
- **权限**: 全校数据驾驶舱（只读）
- **API**: `GET /api/leader/dashboard` — 返回学生数、教师数、课程数、毕业率等统计

---

## 四、推荐微服务拆分

```
gateway :8080
  │
  ├── auth-service :8081         用户认证 / 角色 / 权限
  ├── organization-service       学校 → 学院 → 专业 → 班级
  ├── teaching-service           课程 / 教师 / 排课 / 培养方案
  ├── student-service            学生 / 选课 / 成绩
  ├── evaluation-service         督导评价
  └── statistics-service         校领导看板 / 数据统计
```

---

## 五、API 目录建议

| 前缀 | 职责 | 目标服务 |
|------|------|----------|
| `/api/auth/**` | 登录/登出/注册 | auth-service |
| `/api/org/**` | school/college/major/class CRUD | organization-service |
| `/api/student/**` | 学生信息、选课、成绩查看 | student-service |
| `/api/teacher/**` | 我的课程、成绩录入 | teaching-service |
| `/api/academic/**` | 培养方案、教学计划、审核 | teaching-service |
| `/api/supervisor/**` | 督导评价 | evaluation-service |
| `/api/leader/**` | 数据驾驶舱 | statistics-service |
| `/api/admin/**` | 用户/角色/权限管理 | auth-service |

---

## 六、当前系统已实现 vs 未实现

| 模块 | 状态 |
|------|------|
| 学生 CRUD | ✅ 已实现 |
| 班级 CRUD | ✅ 已实现 |
| 教师 CRUD | ✅ 已实现 |
| 课程 CRUD | ✅ 已实现 |
| 选课 / 秒杀 | ✅ 已实现 |
| 学院 CRUD | ✅ 已实现 |
| 专业 CRUD | ✅ 已实现 |
| 培养方案 CRUD | ✅ 已实现 |
| 毕业审核规则引擎 | ✅ 已实现 |
| JWT 认证 | ✅ 已实现 |
| 简单角色（role 字符串） | ✅ 已实现 |
| RBAC（UserRole/Role/Permission） | ❌ 未实现 |
| 学校 (School) 实体 | ❌ 未实现 |
| 教务处 / 学院管理员 / 专业负责人 | ❌ 未实现 |
| 辅导员 (Counselor) | ❌ 未实现 |
| 督导 (Supervisor) + 评价 | ❌ 未实现 |
| 校领导数据驾驶舱 | ❌ 未实现 |
| 排课系统 | ❌ 未实现 |
| 服务拆分（当前 4 个服务） | ❌ 未实现 |
