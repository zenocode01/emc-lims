# EMC LIMS 电磁兼容实验室信息管理系统

> **REQUIRED SUB-SKILL:** Use the executing-plans skill to implement this plan task-by-task.

**Goal:** 开发一套面向第三方检测认证机构的 EMC 电磁兼容 LIMS 系统，支持 CNAS/CMA/CE/FCC 认证体系，覆盖样品管理、测试项目管理、报告管理、设备管理、人员管理、客户管理、标准管理 7 大核心模块。

**Architecture:** 采用前后端分离架构，前端使用 React + TypeScript + Ant Design Pro 构建企业级管理界面，后端使用 Spring Boot 3 + Java 21 提供 RESTful API，数据库使用 PostgreSQL 16。系统采用分层架构（Controller-Service-Repository），支持 RBAC 细粒度权限控制，报告引擎支持 Word/PDF 模板编辑和在线表单式编辑。

**Tech Stack:** React 18 + TypeScript + Ant Design Pro + Vite | Spring Boot 3 + Java 21 + MyBatis-Plus | PostgreSQL 16 + Redis | Docker + Docker Compose | MinIO（对象存储）

---

## 目录

1. [系统架构设计](#1-系统架构设计)
2. [数据库设计](#2-数据库设计)
3. [模块详细设计](#3-模块详细设计)
4. [实施阶段规划](#4-实施阶段规划)
5. [任务清单](#5-任务清单)

---

## 1. 系统架构设计

### 1.1 总体架构图

```
┌─────────────────────────────────────────────────────────┐
│                     前端 (React + TS)                      │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│  │ 样品管理  │ │ 测试管理  │ │ 报告管理  │ │ 设备管理  │   │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │
│  │ 人员管理  │ │ 客户管理  │ │ 标准管理  │ │ 系统管理  │   │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │
└────────────────────────┬────────────────────────────────┘
                         │ RESTful API (JSON)
┌────────────────────────┴────────────────────────────────┐
│                   后端 (Spring Boot 3)                     │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐          │
│  │ Controller │ │  Service   │ │ Repository │          │
│  │  层        │ │    层      │ │    层      │          │
│  └────────────┘ └────────────┘ └────────────┘          │
│  ┌────────────────────────────────────────────┐        │
│  │            公共组件层                        │        │
│  │  认证/授权 │ 日志 │ 异常处理 │ 通用工具      │        │
│  └────────────────────────────────────────────┘        │
└────────────────────────┬────────────────────────────────┘
                         │
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
    ┌──────────┐  ┌──────────┐  ┌──────────┐
    │PostgreSQL │  │  Redis   │  │  MinIO   │
    │  数据库   │  │  缓存    │  │  文件存储 │
    └──────────┘  └──────────┘  └──────────┘
```

### 1.2 模块划分

| 模块 | 包路径 | 职责 |
|------|--------|------|
| 用户认证 | `auth` | JWT 登录、RBAC 权限、会话管理 |
| 样品管理 | `sample` | 收样、流转、状态跟踪、留样、处置 |
| 测试管理 | `test` | 测试计划、测试数据、标准/限值 |
| 报告管理 | `report` | 报告生成、模板引擎、审核流 |
| 设备管理 | `equipment` | 设备台账、校准、使用记录 |
| 人员管理 | `personnel` | 人员档案、授权、培训、能力矩阵 |
| 客户管理 | `customer` | 客户信息、合同、报价、进度 |
| 标准管理 | `standard` | 标准版本、测试方法、限值表 |
| 系统管理 | `system` | 字典、角色、菜单、配置 |

### 1.3 权限模型（RBAC + 数据权限）

```
用户 → 角色 → 权限（菜单+操作）
用户 → 部门 → 数据范围（全部/本部门/本部门及子部门/仅本人）
```

权限粒度：
- 菜单级：页面访问控制
- 按钮级：增删改查、导出、审核、签发
- 数据级：按部门/项目隔离数据

---

## 2. 数据库设计

### 2.1 核心实体关系图（ERD）

```
┌─────────┐     ┌──────────┐     ┌──────────┐
│  用户    │──M:N│   角色    │──M:N│  权限    │
│ user    │     │  role    │     │  menu    │
└────┬────┘     └──────────┘     └──────────┘
     │
     │ M
┌────┴────┐     ┌──────────┐     ┌──────────┐
│  部门    │──1:N│   合同    │──1:N│  样品    │
│ dept    │     │ contract │     │ sample   │
└─────────┘     └──────────┘     └────┬─────┘
                                      │
                                      │ M
                               ┌──────┴──────┐
                               │   测试项目   │
                               │  test_item  │
                               └──────┬──────┘
                                      │
                                      │ M
                               ┌──────┴──────┐
                               │   测试数据   │
                               │ test_record │
                               └──────┬──────┘
                                      │
                                      │ 1
                               ┌──────┴──────┐
                               │   报告      │
                               │  report     │
                               └─────────────┘
```

### 2.2 核心数据表清单

#### 2.2.1 基础模块

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `sys_user` | 用户表 | id, username, password, name, email, phone, dept_id, status |
| `sys_dept` | 部门表 | id, parent_id, name, code, sort, status |
| `sys_role` | 角色表 | id, name, code, type, status, data_scope |
| `sys_menu` | 菜单/权限表 | id, parent_id, name, type, path, component, permission, sort |
| `sys_dict` | 数据字典表 | id, type, code, value, label, sort |
| `sys_config` | 系统配置表 | id, key, value, description |

#### 2.2.2 样品管理

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `sample` | 样品主表 | id, sample_no, customer_id, contract_id, product_name, model, manufacturer, status, receive_date, test_standards, remark |
| `sample_image` | 样品照片 | id, sample_id, image_url, type, remark |
| `sample_log` | 样品流转日志 | id, sample_id, from_status, to_status, operator, operate_time, remark |
| `sample_retention` | 留样记录 | id, sample_id, retention_date, storage_location, status, disposition_date, disposition_method |

#### 2.2.3 测试管理

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `test_plan` | 测试计划表 | id, sample_id, customer_id, plan_no, test_items(JSON), status, plan_date, due_date |
| `test_item` | 测试项目定义 | id, code, name, standard, method, limit_value(JSON), category(emission/immunity), status |
| `test_record` | 测试数据记录 | id, test_plan_id, test_item_id, tester_id, test_date, result(pass/fail/na), measurement_value, limit_value, margin, instrument_id, test_condition, remarks, environment(JSON) |
| `test_curve` | 测试曲线数据 | id, test_record_id, frequency(MHz), amplitude(dBμV/dBμV/m), limit, margin, marker_points(JSON) |
| `test_environment` | 测试环境表 | id, type(emi_chamber/anechoic_chamber), spec, location, status |

#### 2.2.4 报告管理

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `report` | 报告主表 | id, report_no, sample_id, customer_id, status, version, issued_date, reviewer_id, approver_id, file_url |
| `report_template` | 报告模板表 | id, name, type, template_content(JSON/文件), language(zh/en), status, version |
| `report_audit_log` | 报告审核日志 | id, report_id, operator_id, action(create/review/approve/reject), comment, audit_time |
| `report_version` | 报告版本历史 | id, report_id, version, content, operator_id, operate_time |

#### 2.2.5 设备管理

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `equipment` | 设备台账 | id, equipment_no, name, model, manufacturer, serial_no, location, status, calibration_due, last_calibration |
| `equipment_calibration` | 校准记录 | id, equipment_id, calibration_date, due_date, calibration_org, certificate_no, result, attachment |
| `equipment_usage` | 使用记录 | id, equipment_id, test_plan_id, user_id, start_time, end_time, status, remark |
| `equipment_maintenance` | 维护保养记录 | id, equipment_id, maintenance_date, type, content, person, attachment |

#### 2.2.6 人员管理

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `personnel` | 人员档案 | id, user_id, name, id_card, education, major, title, hire_date, status |
| `personnel_authorization` | 授权上岗记录 | id, personnel_id, test_item_id, authorize_date, expire_date, authorizer_id |
| `personnel_training` | 培训记录 | id, personnel_id, course, trainer, train_date, duration, result, certificate |
| `competency_matrix` | 能力矩阵 | id, personnel_id, test_category, competency_level, assessment_date, assessor_id |

#### 2.2.7 客户管理

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `customer` | 客户信息 | id, name, type, contact, phone, email, address, industry, status |
| `customer_contact` | 联系人 | id, customer_id, name, phone, email, position, is_primary |
| `contract` | 合同/委托单 | id, contract_no, customer_id, sample_count, amount, sign_date, status, requirements(JSON) |
| `quotation` | 报价单 | id, contract_id, test_items(JSON), total_amount, discount, valid_until |

#### 2.2.8 标准管理

| 表名 | 说明 | 关键字段 |
|------|------|---------|
| `standard` | 标准主表 | id, code, name, version, issuing_org, effective_date, expiry_date, status, type(emission/immunity) |
| `standard_test_method` | 测试方法 | id, standard_id, item_code, item_name, method_description, test_setup, frequency_range, measurement_bandwidth |
| `standard_limit` | 限值表 | id, standard_id, frequency_range_start, frequency_range_end, limit_value, unit, measurement_type(peak/avg/qpeak/qavg), band_width |
| `standard_category` | 产品分类 | id, name, applicable_standards(JSON), product_type(ITE/Audio/Industrial/Medical/Auto) |

### 2.3 关键设计说明

**测试数据关联**：
- 一条测试计划可包含多个测试项目
- 每个测试项目可产生多条测试数据记录（不同工况/不同频率点）
- 测试曲线数据关联到具体测试记录

**CNAS/CMA 合规要点**：
- 所有测试数据必须记录原始值、限值、判定结果、测量不确定度
- 测试环境条件（温度、湿度）必须记录
- 使用的仪器必须记录（关联设备表）
- 测试人员、审核人员、批准人员分离
- 完整的审核链追溯（测试记录 → 报告 → 审核日志）

---

## 3. 模块详细设计

### 3.1 样品管理模块

**业务流程**：
```
收样登记 → 样品入库 → 测试中 → 报告编制 → 报告审核 → 报告签发 → 样品留样 → 样品处置
```

**样品编号规则**：`[年份][月份][类型][流水号]`，例如：`202606-SMP-0001`

**状态流转**：
| 状态 | 说明 | 可流转 |
|------|------|--------|
| pending | 待接收 | 已接收、已拒收 |
| received | 已接收 | 测试中、留样中 |
| testing | 测试中 | 测试完成 |
| completed | 测试完成 | 报告编制 |
| reporting | 报告编制中 | 审核中 |
| reviewed | 审核中 | 已签发、打回 |
| issued | 已签发 | 留样中、处置中 |
| retained | 留样中 | 处置中 |
| disposed | 已处置 | - |
| rejected | 已拒收 | - |

### 3.2 测试项目管理模块

**测试项目分类**：
- **发射类 (Emission)**：辐射骚扰、传导骚扰、谐波电流、电压波动
- **抗扰类 (Immunity)**：ESD、辐射抗扰、EFT、浪涌、传导抗扰、工频磁场、电压暂降

**标准管理**：
- 支持 CISPR、IEC、EN、ISO、GB 系列标准
- 标准版本追踪，支持标准变更历史
- 限值表与标准关联，支持频率段分段限值

**测试数据录入**：
- 支持手动录入（表格形式，批量操作）
- 支持 Excel 批量导入
- 预留仪器自动采集接口（LXI/GPIB/USB）

### 3.3 报告管理模块

**报告模板引擎**：
- **Word 模板**：支持 .docx 模板，字段替换（如 JasperReports、Apache POI）
- **PDF 模板**：支持 PDF 模板，动态内容注入
- **在线表单**：拖拽式布局编辑器，支持图表、表格、照片插入

**审核流程**：
```
编制 → 审核（一级）→ 审核（二级，可选）→ 批准 → 签发
```

**报告版本控制**：
- 每次修改生成新版本
- 版本差异对比
- 修改痕迹保留

### 3.4 标准管理模块

**标准版本管理**：
- 标准全文信息（标准号、名称、版本、发布日期、实施日期）
- 标准状态追踪（现行、废止、修订中）
- CNAS/CMA 标准有效性检查

**限值表管理**：
- 频率段 → 限值 → 测量带宽 → 测量方式（Peak/Avg/QPeak/QAvg）
- 支持不同产品标准的限值切换

---

## 4. 实施阶段规划

### Phase 1: 基础架构 + MVP（8-10 周，2-3 个月）

**优先级 P0**：
1. 项目脚手架搭建（前后端）
2. 用户认证 + RBAC 权限系统
3. 基础数据管理（部门、角色、字典）
4. 客户管理
5. 样品管理（收样、流转、状态跟踪）
6. 测试项目管理（测试计划、测试数据录入）
7. 报告管理（报告生成、三级审核流、Word 模板）
8. 部署（Docker Compose）

### Phase 2: 扩展模块（4-6 周）

**优先级 P1**：
9. 设备管理（台账、校准、使用记录）
10. 人员管理（档案、授权、培训、能力矩阵）
11. 标准管理（标准库、测试方法、限值表）
12. Excel 批量导入导出
13. 报告模板在线编辑器

### Phase 3: 优化与扩展（持续）

**优先级 P2**：
14. 仪器自动采集接口（LXI/GPIB/USB）
15. 电子签章集成
16. 不确定度评估模块
17. 统计分析仪表盘
18. 移动端适配（PWA）
19. AI 辅助分析（测试数据异常检测、限值预测）

---

## 5. 任务清单

### 阶段 1：项目初始化与基础架构（T1-T10）

**T1**: 创建项目目录结构
**T2**: 搭建后端 Spring Boot 3 项目（Maven + Java 21）
**T3**: 配置 PostgreSQL 数据库连接和 MyBatis-Plus
**T4**: 搭建前端 React + TypeScript + Vite + Ant Design Pro 项目
**T5**: 配置 Docker Compose（PostgreSQL + Redis + MinIO）
**T6**: 实现用户实体和 MyBatis-Plus 基础 CRUD
**T7**: 实现 JWT 认证中间件
**T8**: 实现 RBAC 权限管理（用户-角色-菜单-权限）
**T9**: 实现数据权限（部门数据隔离）
**T10**: 实现 Swagger/OpenAPI 文档

### 阶段 2：客户管理模块（T11-T14）

**T11**: 客户管理后端（CRUD + 联系人管理）
**T12**: 客户管理前端（列表、新增、编辑、详情）
**T13**: 合同/委托单管理后端
**T14**: 合同/委托单管理前端

### 阶段 3：样品管理模块（T15-T22）

**T15**: 样品实体设计和数据库表创建
**T16**: 样品管理后端（收样、状态流转、流转日志）
**T17**: 样品管理前端（收样登记、样品列表、详情）
**T18**: 样品照片上传功能
**T19**: 留样管理后端
**T20**: 留样管理前端
**T21**: 样品处置功能
**T22**: 样品编号规则引擎

### 阶段 4：测试管理模块（T23-T30）

**T23**: 测试项目定义后端（分类、标准关联、限值）
**T24**: 测试项目定义前端（列表、新增、编辑）
**T25**: 测试计划管理后端
**T26**: 测试计划管理前端
**T27**: 测试数据录入后端（手动录入 + 批量）
**T28**: 测试数据录入前端（表格编辑、批量操作）
**T29**: Excel 导入导出功能
**T30**: 测试曲线数据管理

### 阶段 5：报告管理模块（T31-T38）

**T31**: 报告实体设计和后端（状态机、审核流）
**T32**: 报告管理前端（列表、详情、审核操作）
**T33**: 报告模板管理后端
**T34**: 报告模板管理前端
**T35**: 报告生成引擎（Word 模板替换）
**T36**: 报告 PDF 导出
**T37**: 报告版本历史
**T38**: 在线报告模板编辑器（拖拽布局）

### 阶段 6：设备管理模块（T39-T44）

**T39**: 设备台账后端
**T40**: 设备台账前端
**T41**: 校准管理后端
**T42**: 校准管理前端
**T43**: 设备使用记录后端
**T44**: 设备使用记录前端

### 阶段 7：人员管理模块（T45-T50）

**T45**: 人员档案后端
**T46**: 人员档案前端
**T47**: 授权上岗管理后端
**T48**: 授权上岗管理前端
**T49**: 培训记录管理
**T50**: 能力矩阵管理

### 阶段 8：标准管理模块（T51-T56）

**T51**: 标准主表后端
**T52**: 标准管理前端
**T53**: 测试方法管理后端
**T54**: 测试方法管理前端
**T55**: 限值表管理后端
**T56**: 限值表管理前端

### 阶段 9: 优化与完善（T57-T60）

**T57**: 全局搜索功能
**T58**: 数据统计仪表盘
**T59**: 日志审计模块
**T60**: 系统配置与参数管理

---

## 附录

### A. EMC 测试项目标准对照表

| 测试项目 | 常用标准 | 频率范围 | 限值单位 |
|---------|---------|---------|---------|
| 辐射骚扰 | CISPR 32 / CISPR 35 | 30MHz-1GHz / 1GHz-6GHz | dBμV/m |
| 传导骚扰 | CISPR 32 / CISPR 16 | 150kHz-30MHz | dBμV |
| 静电放电 | IEC 61000-4-2 | - | kV (接触±8kV, 空气±15kV) |
| 辐射抗扰 | IEC 61000-4-3 | 80MHz-1GHz / 1.4GHz-2GHz | V/m (10V/3V/1V) |
| 电快速瞬变 | IEC 61000-4-4 | 5kHz | kV (±2kV/±4kV) |
| 浪涌 | IEC 61000-4-5 | - | kV (线-线±1kV, 线-地±2kV) |
| 传导抗扰 | IEC 61000-4-6 | 150kHz-80MHz | V (3V/10V) |
| 谐波电流 | IEC 61000-3-2 | 2nd-40th 次谐波 | % |
| 电压波动 | IEC 61000-3-3 | - | Δp (%) |

### B. CNAS 关键要求

- 检测人员需经授权并持证上岗
- 检测设备需定期校准，证书在有效期内
- 测试方法需为现行有效标准
- 原始记录需完整、可追溯
- 报告需经三级审核（编制/审核/批准）
- 需进行测量不确定度评估（如适用）
- 需进行能力验证/比对

### C. 目录结构规划

```
emc-lims/
├── backend/                    # Spring Boot 后端
│   ├── pom.xml
│   ├── src/main/java/com/emclims/
│   │   ├── common/            # 公共模块
│   │   │   ├── config/        # 配置类
│   │   │   ├── security/      # 安全/认证
│   │   │   ├── exception/     # 异常处理
│   │   │   └── response/      # 统一响应
│   │   ├── module/
│   │   │   ├── auth/          # 认证
│   │   │   ├── sample/        # 样品管理
│   │   │   ├── test/          # 测试管理
│   │   │   ├── report/        # 报告管理
│   │   │   ├── equipment/     # 设备管理
│   │   │   ├── personnel/     # 人员管理
│   │   │   ├── customer/      # 客户管理
│   │   │   ├── standard/      # 标准管理
│   │   │   └── system/        # 系统管理
│   │   └── EmcLimsApplication.java
│   └── src/main/resources/
│       ├── application.yml
│       └── mapper/            # MyBatis XML
├── frontend/                   # React 前端
│   ├── package.json
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── components/        # 公共组件
│   │   ├── pages/             # 页面
│   │   │   ├── auth/
│   │   │   ├── sample/
│   │   │   ├── test/
│   │   │   ├── report/
│   │   │   ├── equipment/
│   │   │   ├── personnel/
│   │   │   ├── customer/
│   │   │   ├── standard/
│   │   │   └── system/
│   │   ├── hooks/             # 自定义 Hooks
│   │   ├── store/             # 状态管理
│   │   ├── utils/             # 工具函数
│   │   └── App.tsx
│   └── public/
├── docker/                     # Docker 配置
│   ├── docker-compose.yml
│   ├── postgres/
│   ├── redis/
│   └── nginx/
├── docs/                       # 文档
│   └── plans/
└── README.md
```
