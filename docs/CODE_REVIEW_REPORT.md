# 代码审查报告

## 概览

**项目：** EMC LIMS 电磁兼容实验室信息管理系统
**审查范围：** `1f01c00..HEAD`（2 个 commit：`96d3f80` + `bf00059`）
**审查时间：** 2026-06-11
**审查人：** MiniMax-M3

---

## 提交清单

| Commit | 类型 | 说明 |
|--------|------|------|
| `96d3f80` | feat | 系统管理模块完善（SecurityConfig + 多角色 + 数据权限 + 前端 4 个系统管理页面 + Dashboard 优化） |
| `bf00059` | fix | 修复多角色支持的测试和 DataPermissionHandler |

**统计：** 23 文件变更，2076 行新增，59 行删除

---

## 审查结论

**总体评价：** ⚠️ **有条件通过（Conditionally Approved）**

整体架构清晰，完成了多角色支持、数据权限过滤、CORS 配置、前端 4 个系统管理页面（用户/角色/菜单/部门）等核心功能。代码风格统一，注释充分。但存在 **2 个关键问题**、**5 个重要问题** 和 **5 个次要问题** 需要关注。

**测试状态：** 232/233 通过 ✅（1 个失败与本次提交无关，是 `1f01c00` 引入的并发测试问题）

---

## 关键问题（Critical - 必须修复）

### 🔴 C1. N+1 查询问题 - 用户列表性能

**文件：** `backend/src/main/java/com/emclims/module/sys/service/impl/SysUserServiceImpl.java:86-99`

**问题描述：**
`pageUsers()` 方法对分页结果中每个用户都执行一次 `deptMapper.selectById()` 和 `roleMapper.selectById()`。当一页 20 条记录时，会产生 40 次额外查询。

**影响：** 高并发下数据库压力巨大，响应时间随页大小线性增长。

**建议：**
```java
// 批量查询优化
List<Long> deptIds = records.stream()
    .map(SysUser::getDeptId)
    .filter(Objects::nonNull)
    .distinct()
    .collect(Collectors.toList());
Map<Long, String> deptMap = deptMapper.selectBatchIds(deptIds).stream()
    .collect(Collectors.toMap(SysDept::getId, SysDept::getDeptName));
```

---

### 🔴 C2. dataScope=3 子部门递归未实现

**文件：** `backend/src/main/java/com/emclims/common/config/EmcDataPermissionHandler.java:50-54`

**问题描述：**
代码注释承认 "完整递归子部门查询" 未实现，仅简化为查询当前部门，与 `dataScope=3` 的语义（"本部门及子部门数据"）不符。

**影响：** 配置为 `dataScope=3` 的角色实际上只能看到本部门数据，与配置预期不符，可能导致数据权限漏洞。

**建议：**
方案 A：在 `DataPermissionLoader` 加载数据权限时，一次性查出用户部门的所有子部门 ID 列表（通过递归 SQL 或物化路径），存入 `DataPermissionContext`，在 Handler 中使用 `IN` 条件。

方案 B：使用 PostgreSQL 递归 CTE。

**短期缓解：** 明确标记为已知限制，更新文档。

---

## 重要问题（Important - 应该修复）

### 🟠 I1. pageUsers 按角色筛选性能问题

**文件：** `backend/src/main/java/com/emclims/module/sys/service/impl/SysUserServiceImpl.java:71-83`

**问题描述：**
当前实现先分页查询所有用户，再在内存中按角色过滤，导致：
1. 分页数据不准确（total 是过滤前，过滤后可能远小于 total）
2. 当角色用户极少时，分页几乎遍历全表

**建议：**
```java
// 使用子查询
if (queryDTO.getRoleId() != null) {
    wrapper.in(SysUser::getId,
        Wrappers.lambdaQuery(SysUserRole.class)
            .select(SysUserRole::getUserId)
            .eq(SysUserRole::getRoleId, queryDTO.getRoleId()));
}
```

---

### 🟠 I2. 缺少事务管理

**文件：** `backend/src/main/java/com/emclims/module/sys/service/impl/SysUserServiceImpl.java:128, 154, 167, 178`

**问题描述：**
`createUser`、`updateUser`、`deleteUsers`、`resetPassword`、`updateStatus` 都没有 `@Transactional` 注解。如果用户保存成功但角色关联失败，会产生数据不一致。

**建议：**
```java
@Transactional(rollbackFor = Exception.class)
@Override
public void createUser(SysUserDTO dto) {
    // ... 现有逻辑
}
```

---

### 🟠 I3. 缺少 roleIds 有效性验证

**文件：** `backend/src/main/java/com/emclims/module/sys/service/impl/SysUserServiceImpl.java:128, 154`

**问题描述：**
`createUser` 和 `updateUser` 中没有验证 `dto.getRoleIds()` 中的角色 ID 是否真实存在。

**影响：** 可能会插入无效的 `sys_user_role` 关联记录。

**建议：**
```java
if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
    List<SysRole> existingRoles = roleMapper.selectBatchIds(dto.getRoleIds());
    if (existingRoles.size() != dto.getRoleIds().size()) {
        throw new BusinessException("包含无效的角色ID");
    }
}
```

---

### 🟠 I4. SysUser 实体冗余字段

**文件：** `backend/src/main/java/com/emclims/module/sys/entity/SysUser.java`

**问题描述：**
`SysUser` 中仍保留 `roleId` 字段，但已使用 `sys_user_role` 关联表，会造成：
1. 概念混淆（新旧两套机制并存）
2. 数据不一致风险（两个地方都设置但只读其中一个）

**建议：** 移除 `SysUser.roleId` 字段，所有角色操作通过 `userRoleMapper` 进行。

---

### 🟠 I5. DataPermissionLoader 变量名误导

**文件：** `backend/src/main/java/com/emclims/common/security/DataPermissionLoader.java:43-55`

**问题描述：**
变量名 `maxDataScope` 实际存储的是最小值（权限最大），与名称含义相反。

**建议：**
```java
// 重命名为 minDataScope
Integer minDataScope = null;
for (Long roleId : roleIds) {
    SysRole role = roleMapper.selectById(roleId);
    if (role != null && role.getDataScope() != null) {
        if (minDataScope == null || role.getDataScope() < minDataScope) {
            minDataScope = role.getDataScope();
        }
    }
}
```

---

## 次要问题（Minor - 锦上添花）

### 🟡 M1. 未使用的 import

**文件：** `backend/src/main/java/com/emclims/common/config/EmcDataPermissionHandler.java:6`

```java
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;  // 未使用
```

---

### 🟡 M2. SysUserVO 应支持多角色

**文件：** `backend/src/main/java/com/emclims/module/sys/vo/SysUserVO.java`

当前 VO 只有 `roleId`、`roleName`、`roleCode` 三个单数字段。应该扩展为 `List<SysRoleVO> roles` 或 `List<Long> roleIds`，以充分发挥多角色架构优势。

---

### 🟡 M3. 删除用户时无日志记录

**文件：** `backend/src/main/java/com/emclims/module/sys/service/impl/SysUserServiceImpl.java:167`

`deleteUsers` 直接物理删除，无审计日志。建议记录操作日志（操作人、删除对象、时间）。

---

### 🟡 M4. SysUserRole 主键策略未明确

**文件：** `backend/src/main/java/com/emclims/module/sys/entity/SysUserRole.java`

`@TableId` 未指定 `IdType` 和具体列名。建议明确：
```java
@TableId(value = "id", type = IdType.ASSIGN_ID)
private Long id;
```

---

### 🟡 M5. 测试 schema.sql 缺少初始化数据

**文件：** `backend/src/test/resources/schema.sql`

仅添加了表结构，没有 INSERT 初始数据，部分测试用例可能仍需手动准备数据。

---

## 安全审查

### ✅ 已正确处理

1. **密码加密：** 使用 `passwordEncoder.encode()` 存储 BCrypt 哈希 ✅
2. **JWT 过滤：** 排除白名单路径（`/api/auth/login`、`/api/auth/refresh`、Swagger 路径）✅
3. **CORS 配置：** 使用 `allowedOriginPatterns("*")` 配合 `credentials: true` ✅
4. **数据权限隔离：** 通过 `DataPermissionContext` ThreadLocal 隔离 ✅

### ⚠️ 建议加强

1. **角色变更不刷新 Token：** 用户角色变更后，已签发的 JWT 仍带旧权限。建议在角色变更时吊销旧 Token。
2. **缺少密码强度校验：** `createUser` 时未对密码复杂度做校验。
3. **缺少操作日志：** 关键操作（用户/角色 CRUD）无审计日志。

---

## 前端审查

### ✅ 已正确处理

1. **路由守卫：** `ProtectedRoute` 检查 `localStorage.token` ✅
2. **Token 存储：** 登录成功后存储到 `localStorage` ✅
3. **API 错误处理：** `request.ts` 统一拦截 401/403 ✅
4. **组件复用：** 4 个表单组件（UserForm/RoleForm/MenuForm/DeptForm）结构一致 ✅
5. **图标使用：** Ant Design Icons 语义化 ✅

### ⚠️ 建议加强

1. **Token 过期处理：** 当前缺少主动检查 token 过期并自动登出的逻辑。
2. **表单验证：** 角色编码的正则验证仅前端，后端应同步验证。
3. **菜单树懒加载：** 每次打开角色授权都全量加载菜单树，建议大菜单时改为懒加载。

---

## 测试覆盖

### 现状

| 维度 | 覆盖情况 |
|------|---------|
| 后端单测 | 232/233 通过（1 个预存失败：并发测试） |
| 前端构建 | ⚠️ WSL UNC 路径问题阻塞本地验证 |
| 集成测试 | 缺失 |
| E2E 测试 | 缺失 |

### 建议

1. 为 `SysUserRoleMapper` 的 4 个方法添加单测
2. 为 `DataPermissionLoader` 添加多角色边界场景测试（空角色列表、角色冲突）
3. 为 `EmcDataPermissionHandler.dataScope=3` 添加测试用例
4. 在 CI 中加入前后端构建验证

---

## 优先级修复建议

### P0 - 立即修复（生产前）

1. **C1** N+1 查询问题
2. **C2** dataScope=3 子部门递归实现或文档化限制
3. **I2** 添加 `@Transactional`

### P1 - 近期修复（下一迭代）

4. **I1** pageUsers 角色筛选优化
5. **I3** roleIds 有效性验证
6. **I4** 移除 `SysUser.roleId` 冗余字段

### P2 - 持续优化

7. **I5** 重命名误导变量
8. **M1-M5** 次要问题
9. 安全加强：Token 吊销、密码强度、操作日志

---

## 总结

本次提交成功完成了从"单角色架构"到"多角色架构"的演进，并补齐了安全配置（CORS、JWT Filter）、数据权限过滤、4 个核心系统管理页面。**架构设计合理，关注点分离清晰**，是高质量的演进性重构。

但仍存在 **2 个关键性能/正确性问题** 需要在生产化前修复：
1. 用户列表的 N+1 查询
2. dataScope=3 子部门递归缺失

修复后建议进行性能压测（特别是用户管理页面的列表查询），验证优化效果。

**审查人：** MiniMax-M3
**审查日期：** 2026-06-11
