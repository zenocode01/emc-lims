# EMC LIMS - 电磁兼容实验室信息管理系统

> 面向第三方检测认证机构的 EMC 电磁兼容实验室信息管理系统

## 系统概述

EMC LIMS 是一款专业的实验室信息管理系统，专为电磁兼容（EMC）检测认证机构设计。系统支持 CNAS、CMA、CE、FCC 等认证体系，覆盖样品管理、测试管理、报告管理、设备管理、人员管理、客户管理、标准管理等核心业务模块。

## 功能特性

### 核心功能
- **样品管理**：收样登记、样品编号、状态流转、留样管理、样品处置
- **测试管理**：测试计划、测试数据录入、Excel 导入导出、测试曲线
- **报告管理**：报告模板自定义（Word/PDF + 在线编辑器）、三级审核流、电子签章
- **设备管理**：设备台账、校准计划、使用记录、期间核查
- **人员管理**：人员档案、授权上岗、培训记录、能力矩阵
- **客户管理**：客户信息、合同管理、报价、进度跟踪
- **标准管理**：标准版本管理、测试方法库、限值表、产品分类

### 认证合规
- ✅ **CNAS**：原始记录可追溯、审核链、不确定度评估
- ✅ **CMA**：报告印章、检测项目授权、标准有效性
- ✅ **CE**：标准符合性声明（DoC）
- ✅ **FCC**：测试报告模板、设备标识

## 技术栈

| 层级 | 技术 |
|------|------|
| **前端** | React 18 + TypeScript + Ant Design Pro + Vite |
| **后端** | Spring Boot 3 + Java 21 + MyBatis-Plus |
| **数据库** | PostgreSQL 16 |
| **缓存** | Redis |
| **文件存储** | MinIO |
| **API 文档** | Knife4j (OpenAPI 3) |
| **部署** | Docker + Docker Compose |

## 快速开始

### 环境要求
- Java 21+
- Node.js 18+
- Docker & Docker Compose

### 1. 启动基础设施

```bash
cd docker
docker compose up -d
```

这将启动：
- PostgreSQL (端口 5432)
- Redis (端口 6379)
- MinIO (端口 9000/9001)

### 2. 启动后端

```bash
cd backend
./mvnw spring-boot:run
```

后端服务启动在 `http://localhost:8080/api`

API 文档：`http://localhost:8080/api/doc.html`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端服务启动在 `http://localhost:3000`

## 目录结构

```
emc-lims/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/emclims/
│   │   ├── common/            # 公共模块
│   │   └── module/            # 业务模块
│   │       ├── auth/          # 认证
│   │       ├── sample/        # 样品管理
│   │       ├── test/          # 测试管理
│   │       ├── report/        # 报告管理
│   │       ├── equipment/     # 设备管理
│   │       ├── personnel/     # 人员管理
│   │       ├── customer/      # 客户管理
│   │       ├── standard/      # 标准管理
│   │       └── system/        # 系统管理
│   └── src/main/resources/
├── frontend/                   # React 前端
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── components/        # 公共组件
│   │   ├── pages/             # 页面
│   │   ├── hooks/             # 自定义 Hooks
│   │   └── store/             # 状态管理
│   └── public/
├── docker/                     # Docker 配置
│   ├── docker-compose.yml
│   └── init.sql               # 数据库初始化
├── docs/                       # 文档
│   └── plans/                 # 实施计划
└── README.md
```

## 数据库

数据库初始化脚本在 `docker/init.sql`，包含：
- 8 大模块 30+ 数据表
- 基础字典数据
- 默认管理员账号（admin/admin123）

## 开发计划

详见 [实施计划](docs/plans/2026-06-10-emc-lims-system.md)

### Phase 1: MVP (2-3 个月)
- 项目基础架构
- 用户认证 + RBAC 权限
- 客户管理
- 样品管理
- 测试管理
- 报告管理

### Phase 2: 扩展模块 (6 个月)
- 设备管理
- 人员管理
- 标准管理
- Excel 导入导出
- 报告在线编辑器

### Phase 3: 优化扩展
- 仪器自动采集
- 电子签章
- 不确定度评估
- 统计分析
- AI 辅助分析

## License

MIT
