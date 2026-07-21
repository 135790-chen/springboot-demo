# 后端安全边界表

## 一、接口级安全边界

### 认证服务

| 接口 | 登录状态 | 权限 | 数据归属 | 注入风险 | 风险类型 | 处理逻辑 | 验证方式 |
|------|---------|------|---------|---------|---------|---------|---------|
| POST /auth/register | 否 | 无 | 自己 | 低 | 弱密码、用户名撞库 | @Valid 校验长度+格式；BCrypt 强度10加密存库；用户名 UNIQUE 索引防重复 | 发空用户名/短密码/重复用户名，期望 400 |
| POST /auth/login | 否 | 无 | 自己 | 低 | 暴力破解、帐号枚举 | 统一返回"用户名或密码错误"不区分；无锁定机制（风险） | 错误密码 10 次，期望统一报错不暴露用户是否存在 |
| POST /auth/logout | 是 | 无 | 自己的Token | 无 | 已登出Token重用 | Redis 黑名单记录；删除活跃记录 | 登出后用旧 Token 调 /auth/me，期望 400 |
| GET /auth/me | 是 | 无 | 自己的信息 | 无 | Token 伪造、重放 | JWT 签名验证 + Redis 黑名单双重校验 | 篡改 Token 调接口，期望 401 |
| DELETE /auth/account | 是 | 无 | 自己 | 低 | 误删、恶意注销 | 从 Token 提取 userId 删除；@Transactional 保证一致性 | 删除后查 /auth/me，期望 400 |

### 学生服务

| 接口 | 登录状态 | 权限 | 数据归属 | 注入风险 | 风险类型 | 处理逻辑 | 验证方式 |
|------|---------|------|---------|---------|---------|---------|---------|
| GET /students/page<br>GET /students | 是 | 所有人 | 所有人可见 | 低 | SQL 注入（LIKE 拼接） | MyBatis-Plus LambdaQueryWrapper 参数化查询；分页防全表扫 | 输入 SQL 片段作为 keyword，期望无影响 |
| GET /students/{id} | 是 | 所有人 | 所有人可见 | 低 | ID 越权遍历 | 路径参数型安全，MyBatis-Plus 预编译 | 输入超大 id/负数/字符串，期望 400 |
| GET /students/search | 是 | 所有人 | 所有人可见 | 中 | SQL 注入（模糊搜索 LIKE） | LambdaQueryWrapper.like() 自动参数化 | keyword="'; DROP TABLE--" 期望无影响 |
| GET /students/grade/{grade} | 是 | 所有人 | 所有人可见 | 中 | 路径参数注入、中文编码 | grade 参数经网关 UriComponentsBuilder 编码透传 | 中文"大一"正常返回；SQL 片段当 grade 传入，期望空结果 |
| POST /students | 是 | **admin** | 无 | 低 | 越权新增、邮箱重复 | JwtInterceptor: POST→校验 role=admin；Service 层 email 查重+UNIQUE 索引兜底；@Valid 校验字段 | 学生 Token 调新增，期望 403；重复邮箱期望 409 |
| PUT /students | 是 | **admin** | 无 | 低 | 越权修改、部分字段篡改 | JwtInterceptor: PUT→校验 role=admin；LambdaUpdateWrapper 只更新非空字段 | 学生 Token 调修改，期望 403；传空 id 期望 400 |
| DELETE /students/{id} | 是 | **admin** | 无 | 低 | 越权删除、ID 遍历 | JwtInterceptor: DELETE→校验 role=admin | 学生 Token 调删除，期望 403 |

### 消息服务（Kafka）

| 接口 | 登录状态 | 权限 | 数据归属 | 注入风险 | 风险类型 | 处理逻辑 | 验证方式 |
|------|---------|------|---------|---------|---------|---------|---------|
| POST /kafka/receive-student | **否** | 无 | 无 | 中 | **未受 JWT 保护**，数据注入 | WebConfig 中 /kafka/** 放行；@Valid 校验；邮箱去重；Kafka 发送失败降级日志 | 不带 Token 直接调，期望不 401（当前设计如此） |
| GET /kafka/status | **否** | 无 | 无 | 无 | 信息泄露 | 仅返回 Kafka 状态，不暴露敏感信息 | 访问 /kafka/status，期望只有 "Kafka 已连接" |

---

## 二、公共安全机制

| 安全层 | 机制 | 覆盖范围 | 说明 |
|--------|------|---------|------|
| 网关层 | 请求代理，Header 透传 | 所有请求 | 非 host 头全部透传；UriComponentsBuilder 防中文乱码 |
| 拦截器层 | JwtInterceptor | student-service 全部（除放行） | 校验 Token 签名 + 有效期；增删改操作校验 role=admin |
| 校验层 | @Valid + Jakarta Validation | POST/PUT 请求体 | @NotBlank (姓名/年级/用户名), @Min(1) (年龄), @Email (邮箱), @Size(密码≥6位) |
| 业务层 | Service 查重 + 事务 | 新增/修改 | email 查重 → 插库，UNIQUE 索引兜底；@Transactional 回滚 |
| 数据库层 | UNIQUE + CHECK | student 表 | idx_student_email UNIQUE；chk_age_positive CHECK(age>0)；user 表 username UNIQUE |
| 密码存储 | BCrypt 强度10 | user 表 | 注册时 `passwordEncoder.encode()`，登录时 `matches()` |
| Token 管理 | JWT 签名 + Redis 黑名单 | 全局 | JWT 含 role 声明；登出后 Token 进黑名单；TTL=剩余有效期 |

---

## 三、已知风险清单

| 风险 | 严重度 | 当前状态 | 建议修复 |
|------|--------|---------|---------|
| 登录无暴力破解防护 | 高 | 无速率限制、无验证码、无账号锁定 | 加 Guava RateLimiter 或 Sentinel 限流 |
| HTTP 明文传输 | 高 | Token 和密码明文走网络 | Nginx 反代 + HTTPS |
| /kafka/** 未受 JWT 保护 | 中 | WebConfig 放行 /kafka/** | 移除放行或加独立 API Key |
| JWT Secret 存在 Nacos 配置中 | 中 | Nacos 未开认证时可直接读取 | JWT_SECRET 仅用环境变量 |
| 无操作审计日志 | 低 | 增删改无操作人记录 | Service 层加操作人字段 |
| 无 CSRF 防护 | 低 | 前后端分离，依赖 Token 而非 Cookie | 暂不紧急 |
