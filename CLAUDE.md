# CLAUDE.md

本文件为 Claude Code（claude.ai/code）在此仓库中工作时提供指导。

## 构建与测试命令

```bash
# === 后端 ===
# 编译所有模块
mvn compile

# 运行全部测试（68 条用例，约 5 秒——纯 Mockito，不启动 Spring 上下文）
mvn test

# 运行单个测试类
mvn test -pl student-service -Dtest=StudentServiceImplTest

# 运行单个测试方法
mvn test -pl student-service -Dtest=StudentServiceImplTest#addStudent_normal_success

# 打包（跳过测试）
mvn package -Dmaven.test.skip=true

# 生成 JaCoCo 覆盖率报告（输出路径：target/site/jacoco/index.html）
mvn test jacoco:report

# === 前端 ===
# 安装依赖
cd frontend && npm install

# 启动开发服务器（Vite 热更新，代理 API 到 :8080）
cd frontend && npm run dev

# 生产构建（产物输出到 ../gateway/src/main/resources/static/）
cd frontend && npm run build

# === Docker ===
# 启动所有基础设施 + 应用容器
docker-compose up -d

# 代码变更后单独重建某个服务的镜像
docker-compose up -d --build student-service
```

所有命令均在仓库根目录执行。需要 Java 17。

## 架构概览

这是一个基于 **Spring Cloud Alibaba 微服务** 的智慧教务管理平台：8 个 Maven 模块，7 个运行时服务，通过 Docker Compose 编排（共 14 个容器）。

**模块与服务对应关系：**

| 模块 | 服务 | 端口 | 职责 |
|--------|---------|------|------|
| `common` | （库） | — | 共享实体、DTO/VO、`Result<T>`、`JwtUtil`、`GlobalExceptionHandler`、`@RateLimit` AOP、`@RequirePermission` AOP、`JwtAuthInterceptor`、Sentinel BlockHandler/UrlCleaner |
| `gateway` | 网关 | 8080 | SPA 宿主 + API 代理 + Knife4j 文档聚合。无数据库 |
| `auth-service` | 认证服务 | 8081 | 注册/登录/登出/注销 + RBAC 角色权限。MySQL `user`/`sys_*` 表 + Redis 黑名单 |
| `student-service` | 学生服务 | 8082 | 学生 CRUD + 学籍层级（学院/专业/班级关联）。`edu_student` 表 |
| `message-service` | 消息服务 | 8083 | 内嵌 Kafka（KRaft 模式）生产者+消费者。`student-topic` 模拟外部推送、`enrollment-topic` 选课确认回调 |
| `organization-service` | 组织架构服务 | 8084 | 学校/学院/专业/班级/辅导员/督导评价管理 |
| `teaching-service` | 教学服务 | 8085 | 课程/教师/选课（秒杀）/培养方案/毕业审核/排课 |
| `statistics-service` | 统计服务 | 8086 | 数据看板/统计快照/领导驾驶舱 |

**请求链路：** 浏览器 → `:8080`（网关 `GatewayProxyController`，通过 `@LoadBalanced` RestTemplate 基于 Nacos 服务发现做手动代理）→ `auth-service` / `student-service` / `message-service` / `organization-service` / `teaching-service` / `statistics-service`。

**公共模块：** 每个服务通过 `@SpringBootApplication` 上的 `scanBasePackages` 扫描 `com.example.demo.common`。`common` 模块提供：
- `Result<T>` — 统一 API 响应格式 `{code, message, data}`
- `JwtUtil` — HMAC-SHA JWT（jjwt 0.12.3）；Token 携带 `userId`、`username`、`role`、`studentId`、`permissions`；配置键 `jwt.secret` / `jwt.expiration`
- `GlobalExceptionHandler` — `@RestControllerAdvice` 捕获参数校验异常、业务异常、数据重复冲突和未知异常 → 统一转为 `Result` 格式
- `JwtAuthInterceptor`（`common/interceptor`）— 通用 JWT 认证拦截器，只校验 Token 签名/有效期并注入用户信息到 request attributes，**不做授权判断**
- `@RequirePermission` + `PermissionAspect` — 方法级权限校验，从 request attributes 读取权限列表，与注解声明的权限编码匹配，无权限返回 403
- `@RateLimit` + `RateLimitAspect` — 基于 Redis `SET NX EX` 的分布式限流，Redis 不可用时自动降级为 `ConcurrentHashMap` 进程内限流
- `SentinelBlockHandler`（`common/sentinel`）— 实现 Sentinel `BlockExceptionHandler` 接口，限流/熔断触发时返回 JSON 格式 `Result`（而非默认白页）
- `SentinelUrlCleaner`（`common/sentinel`）— URL 归一化（`/students/123` → `/students/*`），便于按 URL 模式配置流控规则
- 实体类（`Student`、`Clazz`、`Teacher`、`Course`、`Enrollment`、`School`、`College`、`Major`、`SysUser`、`SysRole`、`SysPermission` 等）使用 MyBatis-Plus 的 `@TableName` / `@TableId` 注解；另有 `dto/`（请求消息 DTO）与 `vo/`（跨服务聚合视图）

## 关键设计模式

### 网关：手动 RestTemplate 代理，非 Spring Cloud Gateway
网关使用单个 `GatewayProxyController` 控制器。它定义 `@LoadBalanced RestTemplate`（底层使用 Apache HttpClient 5），按路径前缀手动代理请求。顶层前缀映射：`/auth/**` → `http://auth-service`，`/students/**` → `http://student-service`，`/kafka/**` → `http://message-service`，`/organization/**` → `http://organization-service`，`/teaching/**` → `http://teaching-service`，`/statistics/**` → `http://statistics-service`，`/api/leader/**` → `http://statistics-service`。`/api/edu/**` 按更具体的子路径分流：`/college|/major|/class` → organization-service，`/course|/teacher|/student-course|/graduation|/training-plan|/classroom|/timeslot|/schedule` → teaching-service，`/stat` → statistics-service，其余兜底 → student-service。同时代理 `/v3/api-docs-*` 端点用于 Knife4j 文档聚合。后端返回的错误响应（4xx/5xx）透明透传——`ErrorHandler` 为空操作。

### 服务容错：Sentinel 三层保护

**网关层：** `GatewayProxyService`（`gateway/src/main/java/.../gateway/GatewayProxyService.java`）封装了所有下游代理调用，通过 `@SentinelResource(value = "gateway_proxy", fallback = "proxyFallback", blockHandler = "proxyBlocked")` 提供熔断和降级：

- **`proxyFallback`** — 下游服务调用异常时（连接超时、500 错误等）返回 `{"code":503,"message":"xxx 服务暂时不可用"}`
- **`proxyBlocked`** — Sentinel 流控/熔断规则触发时（QPS 超限或电路打开）返回 `{"code":429,"message":"系统繁忙，请稍后重试"}`

同时启用 Sentinel 的 `CommonFilter`（`spring.cloud.sentinel.filter.enabled=true`），为每个 URL 路径自动创建 Sentinel 资源，支持 URL 级别的 QPS 限流。`SentinelUrlCleaner` 将含数字 ID 的路径归一化（`/students/123` → `/students/*`），避免每个 ID 都生成独立资源。

**服务层：** 关键接口通过 `@SentinelResource` 注解实现方法级保护。

**规则管理：**
- Sentinel Dashboard（`http://localhost:8858`，sentinel/sentinel）提供实时 QPS/拒绝量监控和规则可视化修改
- Nacos 持久化规则：`nacos-configs/sentinel-rules/` 目录下的 JSON 文件发布到 Nacos `SENTINEL_GROUP`，服务重启后自动加载
- 规则数据源配置在各服务的 Nacos 配置文件中（`spring.cloud.sentinel.datasource.ds-flow.nacos.*`）

**与 `@RateLimit` 的关系：** `@RateLimit` 保留用于简单防刷场景（如注册 3 秒一次），Sentinel 用于生产级 QPS 限流、熔断和热点保护。两者互补，不冲突。

### 认证与授权：JWT + RBAC + Redis 三重保障
- **JWT** 携带 `userId`、`username`、`role`、`studentId`、`permissions`——登录时从 RBAC 查询权限列表写入 Token，下游无需查数据库
- **认证（Authentication）** 由 `common` 的 `JwtAuthInterceptor`（MVC 拦截器，各业务服务在 `WebConfig` 中注册）完成：只校验 Token 签名和有效期，将 `userId`/`username`/`role`/`studentId`/`permissions` 注入 request attributes
- **授权（Authorization）** 由 `@RequirePermission` + `PermissionAspect`（AOP）完成：从 request attributes 读取 `permissions`，与注解声明的权限编码（如 `course:edit`）匹配，不满足返回 403
- **RBAC** 在 auth-service：5 张表（`sys_user` / `sys_role` / `sys_permission` / `sys_user_role` / `sys_role_permission`），9 种角色、37 项权限编码；`RbacService` 提供角色/权限/用户-角色/角色-权限管理，`RbacController` 暴露 `/api/rbac/**` 管理接口
- **Spring Security** 在 auth-service 中启用：`JwtAuthenticationFilter` 填充 `SecurityContext`，`@EnableMethodSecurity` 支持 `@PreAuthorize("hasAuthority('course:manage')")` 方法级鉴权（与 `@RequirePermission` 互补）
- **Redis** 存储已登出 Token 的黑名单（TTL = JWT 剩余有效期）与活跃 Token（`token:active:{userId}`）。登出时写入黑名单并删除活跃记录
- **BCrypt** 强度 10 用于密码哈希；管理员注册由邀请码（`admin888`）控制，注册时同步在 `sys_user` 建记录并分配 `SUPER_ADMIN` / `STUDENT` 角色

### 测试：纯 Mockito + Lambda 缓存技巧
测试**不启动 Spring 上下文**——使用 `mock()` 创建 Mapper 并通过反射注入到 Service 实例中。全量测试套件在 5 秒内完成。测试集中在 `auth-service`（AuthService/RbacService/JwtUtil）、`student-service`（StudentService）与 `message-service`（Kafka 生产者/消费者）三个模块，共 68 条用例。

当测试涉及 `LambdaUpdateWrapper.set()` 的代码时，该 wrapper 需要 MyBatis-Plus 通常在 Spring 初始化时生成的列元数据。解决方案是 `LambdaCacheInitializer`：通过反射填充 `LambdaUtils.COLUMN_CACHE_MAP`，为每个实体提供字段映射（如 `"STUDENTNAME,student_name"` → `ColumnCache`）。对于涉及 `LambdaUpdateWrapper.set()` 路径的测试类，在 `@BeforeAll` 中调用 `LambdaCacheInitializer.initAll()` 即可。

JaCoCo 报告按模块生成至 `target/site/jacoco/index.html`。

### 数据库：Flyway 迁移
- `auth-service`：`V1`（user 表），`V2`（user 增加 student_id），`V3`（RBAC 5 张表 + 种子数据）
- `student-service`：`V1`（学生表）→ `V8`（组织实体）；含课程容量、学院/专业/班级层级、种子教务数据
- `organization-service`：`V1__init_org_db.sql`（学校/学院/专业/班级/辅导员/督导/评价表）
- `teaching-service`：`V1`（课程/教师/选课）→ `V6`（排课表）；含 `enrollment_outbox` 本地消息表（`V4`）
- `statistics-service`：`V1__init_statistics_db.sql`（统计快照表）
- JDBC URL 中配置 `createDatabaseIfNotExist=true`——首次连接自动创建数据库

### Kafka：KRaft 内嵌，优雅降级 + 本地消息表
message-service 使用内嵌 Kafka 的 KRaft 模式（无需 ZooKeeper）。Kafka 不可用时自动降级为日志输出，不阻塞主流程。

两个 topic：
- `student-topic` — 模拟外部系统推送学生数据（`/kafka/receive-student` 先写库再发消息）
- `enrollment-topic` — 秒杀选课异步确认链路：teaching-service 预扣后投递，`EnrollmentConsumer` 消费后回调 teaching-service `/confirm` 落库

teaching-service 侧用 **Outbox（本地消息表）** 保证投递不丢失：预扣 + 写 `enrollment_outbox`(PENDING) 在同一事务；`OutboxRetryTask` 每 30 秒重投 PENDING 记录，最多 3 次，超限标记 FAILED 并回滚 Redis 库存。

### 可观测性体系
- **链路追踪：** Micrometer Tracing（Brave 桥接）→ Zipkin（`:9411`）。网关创建根 span，trace 上下文通过请求头向下游传播
- **指标采集：** 每个服务通过 Actuator 暴露 `/actuator/prometheus`。Prometheus（`:9090`）抓取全部 7 个服务。Grafana（`:3000`，admin/admin）预置了 JVM Micrometer 监控面板
- **告警：** `prometheus-alerts.yml` 定义了服务宕机、JVM 堆 > 90%、CPU > 80%、5xx 错误率 > 5%、P99 延迟 > 2s 五条规则

### 定时任务（`@EnableScheduling`）
- `statistics-service`：`StatSnapshotTask` — 每天 23:59 通过 REST API 聚合各服务数据生成统计快照（用系统 JWT 做服务间认证）
- `teaching-service`：`OutboxRetryTask` — 每 30 秒扫描 `enrollment_outbox` 的 PENDING 记录重投 Kafka，超限回滚库存

## 配置

所有服务使用 **Nacos**（`:8848`）进行服务发现和配置管理。连接地址通过 `bootstrap.yml` 中的 `spring.cloud.nacos.config.server-addr` 指定（由 `spring-cloud-starter-bootstrap` 保证在 `application.yml` 之前加载）。Docker Compose 设置 `NACOS_SERVICE_HOST` / `DB_HOST` / `REDIS_HOST` / `ZIPKIN_HOST` / `SENTINEL_DASHBOARD_HOST` 环境变量，`bootstrap.yml` 和 Nacos 配置中引用这些变量。

**Nacos 配置文件** 位于 `nacos-configs/` 目录：`shared-config.yaml`（JWT 密钥、管理员邀请码、Sentinel 公共配置、链路追踪/Actuator 配置）以及各服务的 `{service}.yaml`（MySQL/Redis/Kafka/Knife4j/Sentinel 数据源设置）。`sentinel-rules/` 子目录存放 Sentinel 流控和降级规则 JSON 文件，需发布到 Nacos `SENTINEL_GROUP` 以持久化。

**审计字段：** 所有教务实体的 `gmt_create` 和 `gmt_modified` 由 `MyMetaObjectHandler`（`student-service/src/main/java/.../config/MyMetaObjectHandler.java`）通过 MyBatis-Plus `MetaObjectHandler` 自动填充。

**Spring Security** 在 auth-service 中使用：CSRF 禁用、Session 无状态，URL 层全部放行（由 `JwtAuthInterceptor` 做 URL 级控制），方法级权限由 `@PreAuthorize` / `@RequirePermission` 负责。

CI 流水线（`.github/workflows/ci.yml`）在单个 Job 中编译 → 测试 → 打包 → 构建全部 7 个服务的 Docker 镜像，包含 Maven 和 Docker 层缓存。

补充文档：`docs/security-boundary.md`（安全审查），`INTERVIEW.md`（面试要点）。

## 服务与端口（Docker Compose）

| 服务 | 端口 | 凭证 |
|---------|------|-------------|
| 网关 + 前端 | 8080 | demo / 123456 |
| 认证服务 | 8081 | — |
| 学生服务 | 8082 | — |
| 消息服务 | 8083 | — |
| 组织架构服务 | 8084 | — |
| 教学服务 | 8085 | — |
| 统计服务 | 8086 | — |
| MySQL | 3306 | root / 123456 |
| Redis | 6379 | — |
| Nacos | 8848 | 无认证 |
| Zipkin | 9411 | — |
| Prometheus | 9090 | — |
| Grafana | 3000 | admin / admin |
| Sentinel Dashboard | 8858 | sentinel / sentinel |
| Kafka (KRaft) | 9092 | — |