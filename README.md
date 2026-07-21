# 学生管理系统 —— Spring Cloud 微服务实战

基于 Spring Boot 3.2 + Spring Cloud Alibaba 构建的微服务学生管理系统，完整实现认证鉴权、服务发现、配置中心、消息队列、API 文档聚合和前后端分离。

## 架构图

```
浏览器 (http://localhost:8080)
    |
    v
+---------------------------------------+
|  Gateway :8080                        |
|  前端页面 + API 网关 + 文档聚合         |
+-----+----------+----------+-----------+
      |          |          |
      v          v          v
+--------+ +--------+ +-------------+
| Auth   | |Student | | Message     |
| :8081  | |:8082   | | :8084       |
| 认证    | | 学生   | | Kafka 消息   |
+---+----+ +---+----+ +------+------+
    |          |             |
    v          v             v
+---------------------------------------+
|  MySQL + Redis + Nacos + Zipkin        |
|  (Docker Compose 一键启动)             |
+---------------------------------------+
```

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 基础框架 | Spring Boot | 3.2.0 |
| 微服务框架 | Spring Cloud | 2023.0.0 |
| 服务注册/配置 | Nacos | 2.3.2 |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis (Lettuce) | 7 |
| 认证 | JWT + BCrypt | jjwt 0.12.3 |
| 消息队列 | Kafka (KRaft 内嵌) | 3.6 |
| API 文档 | Knife4j + SpringDoc | 4.3.0 |
| 参数校验 | Jakarta Validation | 3.0.2 |
| 数据库迁移 | Flyway | 9.22.3 |
| 链路追踪 | Micrometer Tracing + Zipkin | -- |
| 测试 | JUnit 5 + Mockito + MockMvc | -- |

## 项目结构

```
springboot-demo/
|-- common/                 公共模块 (实体、DTO、JWT、异常处理、统一返回)
|-- auth-service/           认证服务 (注册/登录/登出/注销, JWT 签发, Redis管理)
|-- student-service/        学生服务 (CRUD、分页、搜索、筛选、权限拦截)
|-- message-service/        消息服务 (内嵌 Kafka, 模拟外部系统推送)
|-- gateway/                API 网关 (请求转发、Knife4j 文档聚合、前端页面)
|-- nacos-configs/          Nacos 配置文件
|-- docker-compose.yml      一键启动 MySQL + Redis + Nacos
|-- init-nacos.py           一键导入 Nacos 配置
|-- pom.xml                 父 POM (依赖版本统一管理)
```

## 快速启动

### 环境要求

- JDK 17+
- Docker (或自行安装 MySQL 8.0 + Redis 7 + Nacos 2.3.2)
- IntelliJ IDEA

### 1. 启动基础设施

```bash
docker-compose up -d
```

自动启动 MySQL (3306)、Redis (6379)、Nacos (8848)。

### 2. 导入 Nacos 配置

```bash
python init-nacos.py
```

导入 5 个配置文件到 Nacos。

### 3. IDEA 启动服务

按顺序启动 4 个服务的 main 方法:

```
common (编译) -> auth-service -> student-service -> message-service -> gateway
```

### 4. 访问

| 地址 | 说明 |
|------|------|
| http://localhost:8080 | 前端页面 |
| http://localhost:8080/doc.html | API 文档 (Knife4j) |
| http://localhost:8848/nacos | Nacos 控制台 |
| http://localhost:9411 | Zipkin 链路追踪 |

### 5. 登录

- 用户名: demo
- 密码: 123456
- 角色: 管理员
- 管理员邀请码: admin888

## 核心功能

### 认证系统

- JWT 签发与验证 (Token 携带 userId、username、role)
- BCrypt 密码加密 (强度 10)
- Redis 黑名单机制 (登出后 Token 失效)
- 管理员邀请码 (注册时正确填写 -> admin 角色)
- 用户注销 (删除账号 + 清理 Redis)
- @Valid 参数校验 (用户名长度、密码长度、邮箱格式)

### 学生管理

- 分页查询 (首页/末页/跳转)
- 按姓名模糊搜索
- 按年级筛选
- 点击行选中 -> 部分字段更新
- 新增去重 (邮箱唯一性检查 + 数据库 UNIQUE 索引)
- 数据库 CHECK 约束 (年龄 > 0)
- @Transactional 事务管理

### 权限控制

- 角色隔离 (admin / student)
- 前端按角色显隐 (管理员 5 卡片, 学生 3 卡片)
- JwtInterceptor 后端拦截 (学生调增删改 -> 403)
- JWT Token 解析提取 role (无需查数据库)

### 消息队列

- 内嵌 Kafka (KRaft 模式, 无需 ZooKeeper)
- 模拟外部系统推送学生数据
- 降级模式 (Kafka 不可用 -> 日志输出, 不阻塞)

### API 文档

- Knife4j 三服务文档聚合
- BearerAuth 安全方案
- 每个接口 Authorization 输入框 (先登录拿 Token, 填 Bearer [token])

### 数据库版本管理

- Flyway 自动建表
- createDatabaseIfNotExist=true 自动建库
- 换台电脑只需启动, 无需手动执行 SQL

### 质量保障

- 33 个单元测试 (Service + Controller + JwtUtil)
- 全局异常处理器 (参数校验、业务错误、未知异常统一返回)
- 统一返回格式 Result<T> (code + message + data)

### 可观测性

- Micrometer Tracing + Brave + Zipkin 分布式链路追踪
- 一次请求经过 Gateway → auth-service → MySQL 的完整调用链可查
- 各环节耗时自动采集，Zipkin UI 可视化

## API 简介

| 服务 | 路径 | 方法 | 说明 | 需要登录 |
|------|------|------|------|----------|
| Auth | /auth/register | POST | 注册 | -- |
| Auth | /auth/login | POST | 登录 | -- |
| Auth | /auth/logout | POST | 登出 | -- |
| Auth | /auth/me | GET | 当前用户信息 | Yes |
| Auth | /auth/account | DELETE | 注销账号 | Yes |
| Student | /students/page | GET | 分页查询 | Yes |
| Student | /students/search | GET | 姓名搜索 | Yes |
| Student | /students/grade/{grade} | GET | 年级筛选 | Yes |
| Student | /students | POST | 新增 (管理员) | Yes |
| Student | /students | PUT | 更新 (管理员) | Yes |
| Student | /students/{id} | DELETE | 删除 (管理员) | Yes |
| Message | /kafka/receive-student | POST | Kafka 推送 | Yes |

## 设计亮点

1. **防御式编程** — 网关 -> 拦截器 -> @Valid -> Service -> 数据库约束, 5 层防线
2. **JWT + Redis 双保险** — JWT 管签名验证, Redis 管登出黑名单
3. **优雅降级** — Kafka 不可用时自动切换模拟模式, 不阻塞主流程
4. **Flyway + createDatabaseIfNotExist** — 零 SQL 部署, db 随项目走
5. **common 模块抽象** — Result、JwtUtil、异常处理, 写一次处处用
