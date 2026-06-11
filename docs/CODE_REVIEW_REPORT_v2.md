# EMC LIMS 代码审查报告

> 审查日期：2026-06-11 | 审查范围：全量代码（后端 Spring Boot 3 + 前端 React 18）

---

## 目录

1. [严重缺陷](#1-严重缺陷)
2. [安全问题](#2-安全问题)
3. [代码质量问题](#3-代码质量问题)
4. [前端问题](#4-前端问题)
5. [设计问题](#5-设计问题)
6. [测试覆盖](#6-测试覆盖)
7. [总结与建议](#7-总结与建议)

---

## 1. 严重缺陷

### 1.1 [BUG] `UserForm.tsx` 重复加载数据且运行时崩溃

**文件：** [UserForm.tsx](frontend/src/pages/sys/user/UserForm.tsx:29)

组件中存在两个重复的 `useEffect` 数据加载逻辑（第 29-53 行和第 76-96 行），且**都有运行时错误**：

- 第一个 `useEffect`（第 29 行）：引用 `sysRoleApi.all()`，但在使用时 `sysRoleApi` 被后面第 68 行重新定义为空对象 `{ all: () => {} }`，不会返回任何数据。
- 第二个 `useEffect`（第 76 行）：直接调用 `request.get()`，但 `request` 在该文件中**没有 import**（第 3 行只 import 了 `sysUserApi` 和 `sysDeptApi`）。

**影响：** 用户管理页面打开表单时，部门和角色下拉框始终为空，导致无法正常选择部门和角色。

### 1.2 [BUG] 用户列表角色筛选后分页总数错误

**文件：** [SysUserServiceImpl.java:74](backend/src/main/java/com/emclims/module/sys/service/impl/SysUserServiceImpl.java)

```java
// 第 74 行：错误地将当前页过滤后的记录数赋给 total
userPage.setTotal(userPage.getRecords().size());
```

按角色筛选后，`total` 被设置为**当前页过滤后的记录数**而非全表符合条件的总记录数，导致前端分页显示错乱（例如第 2 页无数据）。

**建议：** 应在应用层做角色过滤后重新计算全量总条数，或者在查询层面使用 EXISTS 子查询。

### 1.3 [BUG] `PermissionInterceptor` 的 ALL/OR 逻辑完全相同

**文件：** [PermissionInterceptor.java:49-54](backend/src/main/java/com/emclims/common/security/PermissionInterceptor.java)

```java
if (permission.mode() == RequirePermission.PermissionMode.ALL) {
    hasPermission = userPermissions.contains(requiredPermission);
} else {
    hasPermission = userPermissions.contains(requiredPermission);
}
```

两个分支的校验逻辑完全相同，`ALL` 模式（需要所有权限）未实现真正的权限集合检查。当 `@RequirePermission` 需要多个权限标识时，无法正确校验。

### 1.4 [BUG] 编号规则引擎并发竞争条件

**文件：** [NumberingRuleEngine.java:63-83](backend/src/main/java/com/emclims/common/numbering/NumberingRuleEngine.java)

并发首次创建序列计数器时，若 `currentSeq == null`，在 `insert` 和 `incrementSeq` 之间存在竞态窗口：

1. 线程 A 查询为 null → 执行 `insert`
2. 线程 B 查询为 null → `insert` 因唯一约束失败 → 进入 catch 块
3. catch 块中重新 `selectForUpdate` 获取到线程 A 插入的值，然后执行 `incrementSeq` + 设置 `seqValue = currentSeq + 1`
4. 但这里 `currentSeq` 是 catch 块中重新查询的值，**先 increment 再 +1 会导致序列跳号**

### 1.5 [BUG] 删除角色/用户时未检查关联引用

**文件：** [SysRoleServiceImpl.java:52-58](backend/src/main/java/com/emclims/module/sys/service/impl/SysRoleServiceImpl.java), [SysUserServiceImpl.java:183-189](backend/src/main/java/com/emclims/module/sys/service/impl/SysUserServiceImpl.java)

删除角色和用户时未检查是否被其他实体引用：

- 删除角色：未检查 `sys_user_role` 中是否存在关联用户，删除后关联数据成孤儿
- 删除用户：虽然删除了 `sys_user_role` 关联，但未检查 `sample.receive_by`、`sample.tester_id` 等业务引用

---

## 2. 安全问题

### 2.1 [SEC] JWT 密钥硬编码默认值

**文件：** [JwtUtils.java:22](backend/src/main/java/com/emclims/common/security/JwtUtils.java)

```java
@Value("${jwt.secret:emc-lims-jwt-secret-key-must-be-at-least-256-bits}")
private String secret;
```

默认密钥是一个硬编码的公开字符串，若生产环境未配置 `jwt.secret`，攻击者可直接伪造 JWT Token。

**建议：** 移除默认值，要求必须在配置文件中显式设置，或使用随机生成密钥，并通过 Kubernetes Secret / 环境变量注入。

### 2.2 [SEC] 配置文件敏感信息缺失

**文件：** N/A

项目根目录缺少 `application.yml` / `application-prod.yml` 的示例模板，数据库密码等敏感信息的配置方式不透明。同时 `.gitignore` 中未显式排除 `application-local.yml` 等配置。

### 2.3 [SEC] 密码重置接口无需旧密码验证

**文件：** [SysUserController.java:68](backend/src/main/java/com/emclims/module/sys/controller/SysUserController.java)

```java
@PutMapping("/{id}/password")
public R<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
    userService.resetPassword(id, newPassword);
    return R.ok();
}
```

密码重置接口只校验了登录态，不需要旧密码验证即可任意修改他人密码。一旦 JWT 泄露，攻击者可修改任意用户密码。

**建议：** 增加旧密码校验，或限制只能由管理员操作（配合 `@RequirePermission`）。

### 2.4 [SEC] CORS 配置过于宽松

**文件：** [SecurityConfig.java:70](backend/src/main/java/com/emclims/common/config/SecurityConfig.java)

```java
config.setAllowedOriginPatterns(Arrays.asList("*"));
config.setAllowCredentials(true);
```

`allowCredentials(true)` 与 `allowedOriginPatterns("*")` 的组合允许任意来源携带凭证访问 API。生产环境应限制为具体域名。

---

## 3. 代码质量问题

### 3.1 [QLTY] `Git pull` 请求错误导致序号跳号

**文件：** [NumberingRuleEngine.java:73-76](backend/src/main/java/com/emclims/common/numbering/NumberingRuleEngine.java)

在 `catch (DuplicateKeyException)` 块中：

```java
currentSeq = sequenceMapper.selectForUpdate(ruleCode, dateStr);  // 重新查询
sequenceMapper.incrementSeq(ruleCode, dateStr);                   // 递增
seqValue = currentSeq + 1;                                        // 返回 currentSeq + 1
```

此时 `currentSeq` 是递增后的值（因为读取 FOR UPDATE 获取的是最新已递增的值），`seqValue = currentSeq + 1` 会进一步加 1，导致跳号。正确做法应使用 `sequenceMapper.incrementSeq` 的返回值或直接获取新的值。

### 3.2 [QLTY] 代理内 `this` 调用导致事务失效

多处 Service 实现直接调用 `this.save()`、`this.updateById()`、`this.getById()` 等 `ServiceImpl` 继承方法。当从类内部其他方法调用 `@Transactional` 方法时，由于 AOP 代理限制，事务**可能不生效**。

**建议：** 在需要事务保证的方法上添加 `@Transactional(rollbackFor = Exception.class)`，并确保跨方法调用时通过代理（自注入或 `AopContext.currentProxy()`）。

### 3.3 [QLTY] 重复的条件查询代码

`CustomerServiceImpl` 的 `pageCustomers()` 和 `exportCustomers()` 中有完全相同的查询条件构造代码（第 31-38 行和第 117-123 行）。应抽取为私有方法复用。

### 3.4 [QLTY] `BeanUtils.copyProperties` 使用不当

多处使用 `BeanUtils.copyProperties(dto, user, "password")` 忽略特定字段。这种方式是编译期不安全的，字段名使用字符串字面量，重构时不会报错。

**建议：** 使用 MapStruct 等编译期工具进行对象映射。

### 3.5 [QLTY] `LambdaQueryWrapper` 的 `or()` 条件逻辑隐患

**文件：** [SysUserServiceImpl.java:54-56](backend/src/main/java/com/emclims/module/sys/service/impl/SysUserServiceImpl.java), [CustomerServiceImpl.java:32-34](backend/src/main/java/com/emclims/module/sys/service/impl/CustomerServiceImpl.java)

```java
wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()), SysUser::getPhone, queryDTO.getKeyword())
       .or().like(StrUtil.isNotBlank(queryDTO.getKeyword()), SysUser::getNickname, queryDTO.getKeyword())
       .or().like(StrUtil.isNotBlank(queryDTO.getKeyword()), SysUser::getEmployeeCode, queryDTO.getKeyword())
```

当 `keyword` 为空字符串时，三个条件的 `condition` 参数均为 `false`，但这些条件不会被添加到 SQL。在 MyBatis-Plus 中空条件不会影响后续条件，所以这是**当前是正确的**。但后续增加条件时容易踩坑——如果有人在这段代码之后直接 `.eq()` 而不是 `.and()`，SQL 语义就会变化。

**建议：** 使用 `nested()` 或 `and(w -> ...)` 明确包裹 OR 条件组：

```java
wrapper.and(w -> w.like(..., SysUser::getPhone, ...)
                  .or().like(..., SysUser::getNickname, ...)
                  .or().like(..., SysUser::getEmployeeCode, ...))
       .eq(...)
```

### 3.6 [QLTY] 日志使用不当

多个 Service 中使用 `log.error("业务异常: {}", e.getMessage())` 记录业务异常。业务异常通过 `BusinessException` 抛出后已被 `GlobalExceptionHandler` 捕获，此时 `e.getMessage()` 记录到 error 日志属于正常流程，造成日志污染。

**建议：** 业务异常使用 `log.warn` 级别，真正的系统异常才用 `log.error`。

---

## 4. 前端问题

### 4.1 [FE] `UserForm.tsx` 同时存在两个重复的数据加载逻辑（☆ 严重）

上文已在 1.1 中详细描述。需立即修复，删除重复且错误的代码。

### 4.2 [FE] `RolePage.tsx` 引用了不存在的图标

**文件：** [RolePage.tsx:3](frontend/src/pages/sys/role/index.tsx)

```typescript
import { ..., AuthOutline } from '@ant-design/icons'
```

`AuthOutline` 不存在于 `@ant-design/icons` 库中，会导致编译错误或运行时警告。应替换为 `AuditOutlined` 或 `SafetyOutlined`。

### 4.3 [FE] 菜单管理页面 Tree 组件数据格式不兼容

**文件：** [MenuPage.tsx](frontend/src/pages/sys/menu/index.tsx)

```tsx
<Tree defaultExpandAll treeData={renderTree(menuTree)} />
```

`renderTree()` 返回的是 `Tree.TreeNode` 元素的数组，但 Ant Design 5 的 `<Tree>` 组件自 v5 起推荐使用 `treeData` 属性传入标准数据结构，而不是 JSX 子节点。当前方式虽可运行但不符合 Ant Design 5 的最佳实践，且可能导致事件处理（如 `onSelect`）行为异常。

### 4.4 [FE] 前端缺少全局 token 过期处理

当前 `ProtectedRoute`（[App.tsx:138](frontend/src/App.tsx)）仅在页面加载时检查 `localStorage.getItem('token')`。当请求返回 401 时，前端没有统一的拦截逻辑清除 token 并跳转到登录页。

### 4.5 [FE] 所有 try-catch 块为空处理

前端所有 API 调用的 catch 块都是空注释 `// error handled by interceptor`，但实际代码中**没有注册任何请求拦截器**来处理全局错误提示。用户操作失败时没有任何反馈。

### 4.6 [FE] 批量删除 API 请求体格式不一致

**文件：** [sys.ts:174](frontend/src/api/sys.ts)

```typescript
deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/sys/user/batch', { data: ids }),
```

使用了 `{ data: ids }` 作为 Axios 的 `data` 字段发送请求体。Axios 的 `delete` 方法第二个参数是 `config`，将数组直接作为 `data` 发送时，部分 Axios 版本会将数组序列化为 JSON 数组格式 `[1,2,3]` 而非对象 `{ids: [1,2,3]}`。后端接收时需要特殊处理或改为 POST 请求。

---

## 5. 设计问题

### 5.1 [ARCH] 数据权限处理器硬编码 `dept_id` 和 `create_by` 字段

**文件：** [EmcDataPermissionHandler.java:48-63](backend/src/main/java/com/emclims/common/config/EmcDataPermissionHandler.java)

处理器假设所有业务表都包含 `dept_id` 和 `create_by` 字段。但 `customer`、`sample` 等表虽然有 `create_by`，却没有 `dept_id` 字段。当数据权限为"本部门"或"本部门及子部门"时，在这些表上执行查询会在运行时产生 SQL 错误。

**建议：** 为需要数据权限的业务表添加 `dept_id` 字段，或者在处理器中根据表名动态决定过滤策略。

### 5.2 [ARCH] 数据权限过滤器未排除特定 Mapper

**文件：** [EmcDataPermissionHandler.java:80-89](backend/src/main/java/com/emclims/common/config/EmcDataPermissionHandler.java)

`isAssociationTable` 方法通过 Mapper 接口全限定名的小写形式（下划线已移除）进行字符串包含匹配：

```java
String lower = tableName.toLowerCase().replace("_", "");
return lower.contains("userrole") || ...;
```

MyBatis-Plus 传递的 `tableName` 参数行为不确定，在某些版本中可能是 SQL 表名而非 Mapper 类名。当前方法不可靠。

### 5.3 [ARCH] 菜单和部门树构建逻辑重复

**文件：** [SysDeptServiceImpl.java](backend/src/main/java/com/emclims/module/sys/service/impl/SysDeptServiceImpl.java), [SysMenuServiceImpl.java](backend/src/main/java/com/emclims/module/sys/service/impl/SysMenuServiceImpl.java)

`buildTree()` 和 `buildMenuTree()` 方法逻辑完全相同（递归构建树、排序），应抽取为公共工具方法。

### 5.4 [ARCH] `PermissionInterceptor` 未注册到拦截器链

在 `SecurityConfig` 中只注册了 `JwtAuthenticationFilter`，但 `PermissionInterceptor` 虽然定义了 `preHandle` 方法，缺少 `WebMvcConfigurer` 配置将其注册到 Spring MVC 的拦截器链中。部分版本或配置可能依赖 `WebMvcConfigurer` 才能生效。

### 5.5 [ARCH] `@Transactional` 缺失或放置位置不当

- `CustomerServiceImpl.deleteCustomers()`：批量删除客户无事务保护
- `SysUserServiceImpl.deleteUsers()`：虽然遍历删除了角色关联，但没有 `@Transactional` 注解
- `SysUserServiceImpl.updateUser()`：更新用户和角色关联没有 `@Transactional`

---

## 6. 测试覆盖

### 6.1 [TEST] 测试覆盖不足

从 git 历史看存在测试文件，但关键模块缺乏覆盖：

- 未覆盖 `PermissionInterceptor` 的权限校验逻辑
- 未覆盖 `NumberingRuleEngine` 的并发安全（行锁 + 重复键异常路径）
- 未覆盖 `EmcDataPermissionHandler` 的数据过滤 SQL 生成
- 未覆盖 `AuthServiceImpl` 的登录/刷新/登出完整流程
- 未覆盖前端组件

### 6.2 [TEST] 测试数据清理

H2 内存数据库的测试数据初始化脚本（`schema.sql`）需要与产品数据库（`init.sql`）保持同步，新增表或字段时容易遗漏。

---

## 7. 总结与建议

### 7.1 致命优先（P0 - 立即修复）

| # | 描述 | 文件 |
|---|------|------|
| 1 | `UserForm.tsx` 运行时崩溃，部门和角色选择器不可用 | `frontend/src/pages/sys/user/UserForm.tsx` |
| 2 | 用户列表角色筛选后分页总数错误 | `SysUserServiceImpl.java:74` |
| 3 | `AuthOutline` 图标不存在导致编译错误 | `frontend/src/pages/sys/role/index.tsx:3` |

### 7.2 严重（P1 - 本周修复）

| # | 描述 | 文件 |
|---|------|------|
| 1 | 密码重置无旧密码校验，可任意修改他人密码 | `SysUserController.java:68` |
| 2 | JWT 密钥硬编码默认值 | `JwtUtils.java:22` |
| 3 | 删除角色时未检查关联引用 | `SysRoleServiceImpl.java:52-58` |
| 4 | 数据权限处理器对无 `dept_id` 的表执行时产生 SQL 错误 | `EmcDataPermissionHandler.java` |
| 5 | 编号规则引擎并发首次创建时可能的跳号 | `NumberingRuleEngine.java:73-76` |

### 7.3 一般（P2 - 本月修复）

| # | 描述 |
|---|------|
| 1 | CORS 配置过于宽松 |
| 2 | `PermissionInterceptor` 的 ALL 模式未正确实现 |
| 3 | `PermissionInterceptor` 可能未注册到拦截器链 |
| 4 | 前端缺少全局 401 处理和错误提示 |
| 5 | 重复的查询条件代码（`CustomerServiceImpl`） |
| 6 | try-catch 空块无用户反馈 |
| 7 | 批量删除 API 请求体格式不一致 |
| 8 | `@Transactional` 缺失多处 |

### 7.4 建议（ENH - 迭代规划）

| # | 描述 |
|---|------|
| 1 | 使用 MapStruct 替代 `BeanUtils.copyProperties` |
| 2 | 抽取公共树构建工具类（部门/菜单） |
| 3 | OR 条件组使用 `nested()` 或 `and()` 包裹以提高可维护性 |
| 4 | 业务异常使用 `log.warn` 级别 |
| 5 | Ant Design 5 菜单树使用标准 `treeData` API |
| 6 | 增加关键模块的单元测试覆盖 |
| 7 | 添加生产环境敏感信息配置规范 |

---

> 本报告共发现 **26 个问题**：P0（致命）3 个、P1（严重）5 个、P2（一般）8 个、ENH（建议）10 个。
