# 学生选课管理系统 —— Spring Cloud 微服务实战

> 一个聚焦学生/教师/课程/选课 CRUD 的微服务教学项目，演示认证鉴权、服务发现、限流、熔断降级、秒杀、链路追踪等技术实践。

基于 Spring Boot 3.2 + Spring Cloud Alibaba 构建，完整覆盖认证鉴权、服务发现、配置中心、消息队列、链路追踪、监控告警和前后端分离。

## 系统定位

这是一个**微服务架构教学项目**，技术栈的演示和教学是核心目标。业务层面覆盖了完整的教务管理功能：

- ✅ 学生/班级/教师/课程 CRUD、分页查询、多维度筛选
- ✅ 学院/专业层级体系、培养方案、毕业审核
- ✅ 基于 Redis Lua 的高并发秒杀选课
- ✅ 每日统计快照与数据看板
- ✅ Vue 3 + TypeScript + Element Plus 现代化前端

所有功能设计围绕「演示 Spring Cloud 微服务技术」展开。

## 架构图

```
                                ┌─────────────────────────┐
                                │  Sentinel Dashboard :8858│
                                │  实时流量监控 + 规则管理    │
                                └──────────┬──────────────┘
                                           │
                                ┌─────────────────────────┐
                                │  Prometheus :9090        │
                                │  + Grafana :3000         │
                                │  监控 + 看板              │
                                └──────────┬──────────────┘
                                           │ metrics
  浏览器 (http://localhost:8080)           │
      │                                    │
      v                                    │
┌──────────────────────────────────────────────────────────────────────────────┐
│  Gateway :8080                                                               │
│  Vue 3 SPA 前端 (Vite 构建) + RestTemplate 代理 + Knife4j 文档聚合              │
│  + Sentinel 入口限流 (CommonFilter) + 出口熔断 (@SentinelResource)             │
│                                                                              │
│  /auth/**         → auth-service         :8081                               │
│  /students/**     → student-service      :8082                               │
│  /api/edu/**      → student-service      :8082                               │
│  /kafka/**        → message-service      :8083                               │
│  /organization/** → organization-service :8084                               │
│  /teaching/**     → teaching-service     :8085                               │
│  /statistics/**   → statistics-service   :8086                               │
│  /v3/api-docs-*  (文档聚合, 分别转发到各服务)                                   │
└──┬────────────┬────────────┬────────────┬────────────┬────────────┬──────────┘
   │            │            │            │            │            │
   v            v            v            v            v            v
┌──────┐  ┌──────────┐  ┌─────────┐  ┌───────────┐  ┌──────────┐  ┌──────────┐
│ Auth │  │ Student  │  │ Message │  │Organization│  │ Teaching │  │Statistics│
│:8081 │  │ :8082    │  │ :8083   │  │ :8084      │  │ :8085    │  │ :8086    │
│认证   │  │ 学生     │  │Kafka消息│  │ 学校/学院  │  │ 课程/教师 │  │ 数据看板  │
│JWT+RBAC│  │学籍 CRUD │  │内嵌KRaft│  │ 专业/班级  │  │ 秒杀/毕业 │  │ 统计快照  │
└──┬───┘  └──┬───────┘  └────┬────┘  └─────┬─────┘  └────┬─────┘  └────┬─────┘
   │         │               │               │               │               │
   v         v               v               v               v               v
┌──────────────────────────────────────────────────────────────────────────────┐
│  MySQL :3306  Redis :6379  Nacos :8848                                       │
│  Zipkin :9411  Kafka :9092  Sentinel-Dashboard :8858                          │
│  (Docker Compose 一键启动 14 个容器)                                           │
└──────────────────────────────────────────────────────────────────────────────┘
```

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 基础框架 | Spring Boot | 3.2.0 |
| 微服务框架 | Spring Cloud | 2023.0.0 |
| 前端框架 | Vue 3 + TypeScript + Vite | 3.5 |
| UI 组件库 | Element Plus | 2.14 |
| 图表库 | ECharts (vue-echarts) | 6.1 |
| 服务注册/配置 | Nacos | 2.3.2 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis (Lettuce) | 7 |
| 认证 | JWT + BCrypt | jjwt 0.12.3 |
| 消息队列 | Kafka (KRaft 内嵌) | 3.6 |
| API 文档 | Knife4j + SpringDoc | 4.3.0 |
| 参数校验 | Jakarta Validation | 3.0.2 |
| 数据库迁移 | Flyway | 9.22.3 |
| 服务容错 | Sentinel（限流 + 熔断 + 降级） | 1.8.6 |
| 链路追踪 | Micrometer Tracing + Zipkin | — |
| 监控告警 | Prometheus + Grafana | — |
| 容器化 | Docker Compose | — |
| 测试 | JUnit 5 + Mockito | 68 cases |

## 项目结构

```
smart-edu-platform/
├── common/                 公共模块 (实体、DTO/VO、JWT、异常处理、统一返回、认证拦截器、限流/权限 AOP)
├── auth-service/           认证服务 (注册/登录/登出/注销, JWT 签发, BCrypt 加密, RBAC 角色权限)
├── student-service/        学生服务 (学生 CRUD + 学籍层级关联)
├── message-service/        消息服务 (内嵌 Kafka KRaft, student-topic 模拟推送 + enrollment-topic 选课确认回调)
├── organization-service/   组织架构服务 (学校/学院/专业/班级管理, 层级 CRUD)
├── teaching-service/       教学服务 (课程/教师/选课秒杀/培养方案/毕业审核/排课)
├── statistics-service/     统计服务 (数据看板/统计快照, 领导驾驶舱)
├── frontend/               Vue 3 + TS + Vite 前端 (Element Plus + ECharts, 构建产物输出到 gateway/static/)
├── gateway/                API 网关 (请求转发、Knife4j 文档聚合、前端静态资源宿主)
├── nacos-configs/          Nacos 共享配置 + 各服务专属配置 + Sentinel 规则
├── docs/                   补充文档 (安全边界等)
├── grafana/                Grafana 数据源 + JVM 监控看板
├── prometheus.yml          Prometheus 指标采集配置
├── prometheus-alerts.yml   Prometheus 告警规则 (5 条)
├── docker-compose.yml      一键启动全部 14 个容器
└── pom.xml                 父 POM (依赖版本统一管理)
```

## 快速启动

### 环境要求

- JDK 17+
- Docker (或自行安装 MySQL 8.0 + Redis 7 + Nacos 2.3.2)
- IntelliJ IDEA

### 1. 一键启动

```bash
docker-compose up -d
```

自动启动 MySQL、Redis、Nacos、Zipkin、Kafka、Sentinel Dashboard、Prometheus、Grafana 以及全部 7 个应用服务。

### 2. 导入 Nacos 配置

Nacos 配置已通过 `nacos-configs/` 目录中的 YAML 文件管理（共享配置 + 各服务专属配置），在服务启动时自动加载，无需手动导入。

### 3. 前端开发

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器（Vite 代理到 :8080，支持 HMR 热更新）
npm run dev

# 生产构建（产物自动输出到 ../gateway/src/main/resources/static/）
npm run build
```

开发时只需启动后端容器（`docker-compose up -d`），前端通过 `npm run dev` 独立运行在 `http://localhost:5173`，Vite 自动代理 API 请求到网关。

### 4. 访问

| 地址 | 说明 |
|------|------|
| http://localhost:8080 | 前端页面 |
| http://localhost:8080/doc.html | API 文档 (Knife4j) |
| http://localhost:8858 | Sentinel 控制台 (sentinel/sentinel) |
| http://localhost:8848/nacos | Nacos 控制台 |
| http://localhost:9411 | Zipkin 链路追踪 |
| http://localhost:9090 | Prometheus 指标 |
| http://localhost:3000 | Grafana 看板 (admin/admin) |
| http://localhost:8084 | 组织架构服务 |
| http://localhost:8085 | 教学服务 |
| http://localhost:8086 | 统计服务 |

### 4. 登录

- 用户名: `demo`，密码: `123456`，角色: 管理员
- 管理员邀请码: `admin888`

## 核心功能

### 认证系统

- JWT 签发与验证 (Token 携带 userId、username、role、studentId、permissions)
- BCrypt 密码加密 (强度 10)
- Redis 黑名单机制 (登出后 Token 失效) + 活跃 Token 追踪 (token:active:{userId})
- 管理员邀请码 (注册时正确填写 -> admin 角色)
- 用户注销 (删除账号 + 清理 Redis)
- @Valid 参数校验 (用户名长度、密码长度、邮箱格式)
- @RateLimit 分布式限流 — 注册 3 秒/次，登录 1 秒/次（Redis SET NX EX + 本地 ConcurrentHashMap 兜底）

### RBAC 权限体系

- **5 张表**：`sys_user` / `sys_role` / `sys_permission` / `sys_user_role` / `sys_role_permission`
- **9 种角色**：超级管理员、教务处、学院管理员、专业负责人、任课教师、辅导员、学生、督导、校领导
- **37 项权限编码**：`course:view`、`course:manage`、`score:input`、`graduation:review` 等，注册/登录时自动映射角色与权限
- **认证与授权分离**：`JwtAuthInterceptor`（common）只做认证，`@RequirePermission` + `PermissionAspect` 做授权
- **管理接口**：`/api/rbac/**` 提供角色 CRUD、权限查询、用户-角色分配、角色-权限分配
- **Spring Security**：`JwtAuthenticationFilter` + `@EnableMethodSecurity` + `@PreAuthorize` 方法级鉴权

### 选课并发控制（秒杀架构）

- **Redis + Lua 原子预扣** — 1000 人抢 100 个名额不超卖（`RedisStockService`，去重 + 扣库存一条 Lua 脚本完成）
- **Kafka 异步削峰** — 预扣成功后经 message-service 投递 `enrollment-topic`，`EnrollmentConsumer` 回调 teaching-service 落库确认
- **三级防护** — Redis 去重（防重复提交）→ Lua 库存预扣（防超卖）→ DB 最终确认（最终一致性）
- **Outbox 本地消息表** — 预扣 + 写 `enrollment_outbox`(PENDING) 同事务；`OutboxRetryTask` 每 30 秒重投，超限回滚库存
- **优雅降级** — Kafka 不可用时由重试任务兜底，不阻塞用户响应

### 服务容错（Sentinel — 限流 + 熔断 + 降级）

#### 三层保护体系

```
入口限流（网关 CommonFilter）        出口熔断（@SentinelResource）       服务层保护（方法级）
───────────────────────────── →  ───────────────────────────── →  ────────────────────
  URL 级别 QPS 控制                 下游服务调用熔断/降级              关键接口限流 + 热点防护
  /students/*    200 QPS           student-service 挂了            选课秒杀 5000 QPS
  /auth/login     10 QPS           → 立刻返回 503                  热门 courseId 单独限制
```

#### 核心能力

| 能力 | 说明 | 示例 |
|------|------|------|
| **限流 (Flow Control)** | 接口 QPS 超限时返回 429，保护系统不过载 | 选课接口 5000 QPS 上限 |
| **熔断 (Circuit Breaking)** | 下游服务连续失败 → 自动切断，防止级联故障 | student-service 宕机 → 网关 10 秒内不再尝试 |
| **降级 (Fallback)** | 熔断/异常时返回兜底响应，不抛异常白页 | 返回 `{"code":503,"message":"服务熔断，请稍后重试"}` |
| **热点保护 (Hotspot)** | 热门参数（如某个课程 ID）单独限流 | 1000 人抢同一门课，其他课不受影响 |
| **Dashboard** | 实时 QPS/拒绝量监控，规则可视化修改 | http://localhost:8858 |

#### 与原有 @RateLimit 的关系

| | @RateLimit（保留） | Sentinel（新增） |
|---|---|---|
| 粒度 | "同一方法 N 秒一次" | QPS 精确控制 |
| 适用场景 | 注册/登录防刷 | 高并发接口保护 |
| 熔断 | ❌ | ✅ |
| 规则变更 | 改代码，重启 | Dashboard 一键修改 |

#### 规则管理

Sentinel 规则支持热更新，两种方式：

- **Dashboard 临时规则** — 在 `http://localhost:8858` 流控/降级规则页面直接修改，即时生效，重启后恢复
- **Nacos 持久化规则** — 将 `nacos-configs/sentinel-rules/` 目录下的 JSON 文件发布到 Nacos `SENTINEL_GROUP`，服务重启后自动加载

#### 自定义限流响应

所有 Sentinel 触发后的响应均为统一 `Result` JSON 格式：

```json
{"code":429, "message":"系统繁忙，请稍后重试"}
{"code":503, "message":"student-service 服务暂时不可用，请稍后重试"}
```

由 `common/.../sentinel/SentinelBlockHandler.java` 统一处理，不再显示默认白页。

### 学生管理

- 分页查询 (首页/末页/跳转)
- 按姓名模糊搜索
- 按年级筛选
- 点击行选中 -> 部分字段更新
- 新增去重 (邮箱唯一性检查 + 数据库 UNIQUE 索引)
- 数据库 CHECK 约束 (年龄 > 0)
- @Transactional 事务管理

### 权限控制

- RBAC 角色隔离 (9 种角色 → 37 项权限编码)
- 前端按角色/权限显隐 (管理员多卡片, 学生受限卡片)
- JwtAuthInterceptor 统一认证拦截 (未登录 -> 401)
- @RequirePermission + PermissionAspect 后端授权 (无权限 -> 403)
- JWT Token 解析提取 role + permissions (无需查数据库)

### 消息队列

- 内嵌 Kafka (KRaft 模式, 无需 ZooKeeper)
- `student-topic` 模拟外部系统推送学生数据
- `enrollment-topic` 秒杀选课异步确认回调
- Outbox 本地消息表 + 定时重投，保证消息不丢
- 降级模式 (Kafka 不可用 -> 日志输出, 不阻塞)

### API 文档

- Knife4j 六服务文档聚合
- BearerAuth 安全方案
- 每个接口 Authorization 输入框 (先登录拿 Token, 填 Bearer [token])

### 数据库版本管理

- Flyway 自动建表
- createDatabaseIfNotExist=true 自动建库
- 换台电脑只需启动, 无需手动执行 SQL

### 质量保障

- 68 个单元测试，纯 Mockito 不启动 Spring 上下文，全量约 5 秒（JaCoCo 报告）
- 全局异常处理器 (参数校验、业务错误、未知异常统一返回)
- 统一返回格式 Result\<T\> (code + message + data)

### 可观测性

- Micrometer Tracing + Brave + Zipkin 分布式链路追踪
- Prometheus 指标采集 + Grafana JVM 监控看板
- 一次请求经过 Gateway → Service → MySQL 的完整调用链可查

## 与真实教务系统的差距

本系统已覆盖「学校 → 学院 → 专业 → 班级 → 学生」组织层级、「培养方案 → 课程 → 选课 → 成绩」教学主链路，以及基于学分规则的毕业审核。

真实教务系统的完整模型仍要复杂得多：

```
学校 → 学院 → 专业 → 年级 → 班级 → 学生           ← 已覆盖
培养方案 → 课程体系 → 教学计划 → 开课 → 排课 → 选课  ← 已覆盖 排课/选课，开课/教学计划简化为培养方案
考试 → 成绩 → 毕业审核 → 学籍管理                  ← 已覆盖 成绩/毕业审核，考试/学籍管理未覆盖
```

真实系统最复杂的部分是**规则**：

> 学生 A，2024 级计算机科学，培养方案 2024 版——必修课 100 学分、专业选修 20 学分、通识课 10 学分。系统自动判断：这个学生能不能毕业？

本系统已实现**基于培养方案的学分毕业审核**（`GraduationServiceImpl`：按必修/专业选修/通识选修分类计算学分，校验最低学分要求，输出缺项清单），但课程前置依赖、先修课程、重修/补考等规则仍未覆盖。这些是后续迭代方向。

## API 简介

| 服务 | 路径 | 方法 | 说明 | 需要登录 |
|------|------|------|------|----------|
| Auth | /auth/register | POST | 注册 | — |
| Auth | /auth/login | POST | 登录 | — |
| Auth | /auth/logout | POST | 登出 | — |
| Auth | /auth/me | GET | 当前用户信息 | Yes |
| Auth | /auth/account | DELETE | 注销账号 | Yes |
| Auth | /api/rbac/roles | GET/POST | 角色查询/创建 | Yes |
| Auth | /api/rbac/permissions | GET | 权限列表 | Yes |
| Auth | /api/rbac/users/{id}/roles | POST | 为用户分配角色 | Yes |
| Auth | /api/rbac/roles/{id}/permissions | POST | 为角色分配权限 | Yes |
| Student | /students/page | GET | 学生分页 (含班级名/年级) | Yes |
| Student | /students/search | GET | 姓名模糊搜索 | Yes |
| Student | /students/grade/{grade} | GET | 按年级筛选 | Yes |
| Student | /api/edu/student-course/seckill | POST | 秒杀选课 (Redis+Lua+Kafka) | Yes |
| Student | /students | POST | 新增学生 (管理员) | Yes |
| Student | /students | PUT | 更新学生 (管理员) | Yes |
| Student | /students/{id} | DELETE | 删除学生 (管理员) | Yes |
| Student | /classes, /teachers, /courses, /enrollments | — | 班级/教师/课程/选课 CRUD | Yes |
| Message | /kafka/receive-student | POST | Kafka 推送学生数据 | — |
| Organization | /api/edu/school | GET/POST | 学校管理 | Yes |
| Organization | /api/edu/college | GET/POST | 学院管理 | Yes |
| Organization | /api/edu/major | GET/POST | 专业管理 | Yes |
| Organization | /api/edu/class | GET/POST | 班级管理 | Yes |
| Teaching | /api/edu/course | GET/POST | 课程管理 | Yes |
| Teaching | /api/edu/teacher | GET/POST | 教师管理 | Yes |
| Teaching | /api/edu/student-course | GET/POST | 选课管理 | Yes |
| Teaching | /api/edu/training-plan | GET/POST | 培养方案 | Yes |
| Teaching | /api/edu/graduation | GET/POST | 毕业审核 | Yes |
| Statistics | /api/edu/stat | GET | 统计查询 | Yes |
| Statistics | /api/leader/dashboard | GET | 领导驾驶舱 | Yes |

## 设计亮点

1. **秒杀选课架构** — Redis Lua 原子预扣 → Outbox 本地消息表 → Kafka 异步削峰 → DB 最终确认，1000 人抢 100 名额不超卖
2. **Sentinel 三层容错** — 网关入口限流（CommonFilter）→ 网关出口熔断（@SentinelResource）→ 服务层热点保护，防止系统过载和级联故障
3. **防御式编程** — Gateway → JwtAuthInterceptor → @Valid → Service 查重 → 数据库唯一索引，5 层防线
4. **JWT + RBAC + Redis 三重保障** — JWT 管签名/角色/权限，RBAC 管细粒度授权，Redis 管登出黑名单和活跃 Token 追踪
5. **优雅降级** — Sentinel 熔断时返回兜底 JSON；Kafka 不可用时自动切换日志模拟模式，不阻塞主流程；秒杀 Kafka 不可用时由 Outbox 重试兜底
6. **零 SQL 部署** — Flyway 自动建表建库，换台机器 `docker-compose up` 即用
7. **common 模块抽象** — Result、JwtUtil、全局异常处理、Sentinel BlockHandler、认证拦截器、权限 AOP、实体、VO、消息 DTO 写一次处处用
8. **LambdaUpdateWrapper 单测方案** — 纯 Mockito 环境下手动注入列缓存，不依赖 Spring 上下文
9. **RBAC 细粒度权限** — 5 表 + 9 角色 + 37 权限编码，认证与授权分离，`@RequirePermission` 声明式鉴权
10. **明确的范围边界** — 教学项目坦诚标注了覆盖与未覆盖的业务范围，不假装是完整产品
