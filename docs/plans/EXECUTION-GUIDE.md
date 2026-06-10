# 执行指南 - 分离会话模式

## 当前状态

基础架构已搭建完成：

### ✅ 已完成
- [x] 后端项目结构（Spring Boot 3 + Java 21）
- [x] 前端项目结构（React 18 + TypeScript + Vite）
- [x] 公共模块：统一响应 R.java、分页 PageResult、异常处理
- [x] 配置类：MyBatis-Plus、Redis、Knife4j、字段自动填充
- [x] JWT 工具类
- [x] Docker Compose（PostgreSQL + Redis + MinIO）
- [x] 数据库初始化 SQL（30+ 表）
- [x] 前端基础：路由、Axios 封装、登录/首页占位
- [x] 实施计划文档

### 📋 下一步
打开新的工作树会话，按照实施计划逐步执行任务。

---

## 如何开启新会话

### 方式 1：使用 git worktree（推荐）

```bash
# 在工作树中打开新会话
cd /root/emc-lims
git init
git worktree add ../emc-lims-dev T1-backend-foundation
```

### 方式 2：直接在当前目录继续

打开新的 pi 会话，切换到 `/root/emc-lims` 目录，告知 AI：

> "使用 executing-plans 技能，按照 docs/plans/2026-06-10-emc-lims-system.md 中的任务清单执行。从 T1 开始。"

---

## 执行顺序

按照实施计划中的任务清单顺序执行：

### Phase 1: 基础架构 + MVP（T1-T38）

| 任务编号 | 任务名称 | 预计时间 |
|---------|---------|---------|
| T1 | 项目目录结构 | 已完成 |
| T2 | 后端 Spring Boot 项目 | 已完成 |
| T3 | MyBatis-Plus 配置 | 已完成 |
| T4 | 前端 React 项目 | 已完成 |
| T5 | Docker Compose 配置 | 已完成 |
| T6-T10 | 用户认证 + RBAC 权限 | 下一步 |
| T11-T14 | 客户管理模块 | 后续 |
| T15-T22 | 样品管理模块 | 后续 |
| T23-T30 | 测试管理模块 | 后续 |
| T31-T38 | 报告管理模块 | 后续 |

### Phase 2: 扩展模块（T39-T56）

| 任务编号 | 任务名称 |
|---------|---------|
| T39-T44 | 设备管理模块 |
| T45-T50 | 人员管理模块 |
| T51-T56 | 标准管理模块 |

### Phase 3: 优化与完善（T57-T60）

| 任务编号 | 任务名称 |
|---------|---------|
| T57-T60 | 全局搜索、统计仪表盘、日志审计、系统配置 |

---

## 启动测试

### 启动 Docker 基础设施

```bash
cd /root/emc-lims/docker
docker compose up -d
```

### 验证数据库

```bash
docker exec -it emc-lims-postgres psql -U emc_lims -d emc_lims -c "\dt"
```

### 启动后端

```bash
cd /root/emc-lims/backend
./mvnw spring-boot:run
```

访问 `http://localhost:8080/api/doc.html` 查看 API 文档

### 启动前端

```bash
cd /root/emc-lims/frontend
npm install
npm run dev
```

访问 `http://localhost:3000` 查看前端

---

## 关键文件索引

| 文件路径 | 说明 |
|---------|------|
| `docs/plans/2026-06-10-emc-lims-system.md` | 完整实施计划 |
| `docker/init.sql` | 数据库初始化脚本 |
| `backend/pom.xml` | Maven 依赖配置 |
| `backend/src/main/resources/application.yml` | 应用配置 |
| `backend/src/main/java/com/emclims/EmcLimsApplication.java` | 启动类 |
| `frontend/package.json` | 前端依赖配置 |
| `frontend/vite.config.ts` | Vite 配置 |
| `docker/docker-compose.yml` | Docker 编排文件 |
