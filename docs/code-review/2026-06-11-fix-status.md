# 代码审核修复状态对比报告

> 对比：`docs/CODE_REVIEW_REPORT_v2.md`（上次审核） vs 当前代码状态

---

## 已修复问题（4 个）

### ✅ 1. 前端 request.ts 变量名冲突
- **上次状态**: P0 - message 变量从 antd 导入和 response.data 解构冲突
- **当前状态**: ✅ 已修复 - 将 `message: errMsg` 改为仅 `message: msg`
- **位置**: `frontend/src/api/request.ts`

### ✅ 2. Service 层日志缺失
- **上次状态**: 多个 Service 缺少日志记录
- **当前状态**: ✅ 已修复 - 以下 8 个 Service 已添加 `@Slf4j`：
  - `SysUserServiceImpl`（6 个方法）
  - `SysDeptServiceImpl`（5 个方法）
  - `SysMenuServiceImpl`（6 个方法）
  - `CustomerServiceImpl`（7 个方法）
  - `CustomerContactServiceImpl`（5 个方法）
  - `SysRoleServiceImpl`（7 个方法）
  - `SampleServiceImpl`（6 个方法）
  - `AuthServiceImpl`（4 个方法）

---

## 未修复问题（22 个）

### 🔴 P0 致命（3 个）

#### ❶ [BUG] UserForm.tsx 重复加载数据且运行时崩溃
- **上次状态**: 两个 useEffect，sysRoleApi 被覆盖，request 未 import
- **当前状态**: ❌ **未修复**
- **文件**: `frontend/src/pages/sys/user/UserForm.tsx`
- **详情**: 
  - 第 29 行 useEffect 中 `sysRoleApi.all()` 被第 68 行覆盖为空对象 `{ all: () => {} }`
  - 第 76 行 useEffect 中 `request.get()` 未 import
  - 两个 useEffect 都重复加载相同数据
- **影响**: 用户管理页面表单的部门和角色下拉框始终为空

#### ❷ [BUG] 用户列表角色筛选后分页总数错误
- **上次状态**: `userPage.setTotal(userPage.getRecords().size())`
- **当前状态**: ❌ **未修复**
- **文件**: `SysUserServiceImpl.java:74`
- **详情**: 
  ```java
  userPage.setTotal(userPage.getRecords().size());
  ```
- **影响**: 角色筛选后分页错乱

#### ❸ [BUG] AuthOutline 图标不存在
- **上次状态**: 第 3 行 `import { ..., AuthOutline } from '@ant-design/icons'`
- **当前状态**: ❌ **未修复**
- **文件**: `frontend/src/pages/sys/role/index.tsx:3`

### 🔴 P1 严重（5 个）

#### ❹ [SEC] JWT 密钥硬编码默认值
- **上次状态**: `@Value("${jwt.secret:emc-lims-jwt-secret-key-must-be-at-least-256-bits}")`
- **当前状态**: ❌ **未修复**
- **文件**: `JwtUtils.java:22`
- **详情**: 默认密钥公开，生产环境未配置时可直接伪造 Token

#### ❺ [BUG] 删除角色/用户未检查关联引用
- **上次状态**: 未检查 `sys_user_role` 关联
- **当前状态**: ❌ **未修复**
- **文件**: `SysRoleServiceImpl.java:52-58`
- **详情**: 
  - `deleteRole()` 直接 `removeById(id)`，未检查是否有用户关联
  - `deleteUsers()` 虽然删除了角色关联，但未检查业务引用

#### ❻ [BUG] 密码重置接口无需旧密码验证
- **上次状态**: `@PutMapping("/{id}/password")` 无旧密码校验
- **当前状态**: ❌ **未修复**
- **文件**: `SysUserController.java:68`
- **详情**: 登录态即可修改任意用户密码

#### ❼ [BUG] 编号规则引擎并发首次创建序列跳号
- **上次状态**: catch 块中 `seqValue = currentSeq + 1` 可能导致跳号
- **当前状态**: ❌ **未修复**（但测试已通过）
- **文件**: `NumberingRuleEngine.java:73-76`
- **详情**: 
  ```java
  currentSeq = sequenceMapper.selectForUpdate(ruleCode, dateStr);
  sequenceMapper.incrementSeq(ruleCode, dateStr);
  seqValue = currentSeq + 1;
  ```
- **注意**: 测试 `testGenerateNumber_ConcurrentNewDay_DuplicateKeyTriggersRetry` 已通过（verify times(1)），但代码逻辑仍有争议

#### ❽ [ARCH] 数据权限处理器硬编码 dept_id
- **上次状态**: 对无 `dept_id` 的表（customer、sample）会产生 SQL 错误
- **当前状态**: ❌ **未修复**
- **文件**: `EmcDataPermissionHandler.java`
- **详情**: 
  - case 2/3 硬编码 `new Column("dept_id")`
  - customer、sample 表没有 `dept_id` 字段
  - case 3（本部门及子部门）简化为只查当前部门，未实现递归

### 🟡 P2 一般（8 个）

#### ❾ [BUG] PermissionInterceptor 的 ALL/OR 逻辑完全相同
- **上次状态**: 两分支逻辑一致，ALL 模式未实现
- **当前状态**: ❌ **未修复**
- **文件**: `PermissionInterceptor.java:49-54`
- **详情**: 
  ```java
  if (permission.mode() == ALL) {
      hasPermission = userPermissions.contains(requiredPermission);
  } else {
      hasPermission = userPermissions.contains(requiredPermission);
  }
  ```

#### ⓫ [SEC] CORS 配置过于宽松
- **上次状态**: `allowedOriginPatterns("*")` + `allowCredentials(true)`
- **当前状态**: ❌ **未修复**
- **文件**: `SecurityConfig.java:70`

#### ⓫ [ARCH] PermissionInterceptor 可能未注册到拦截器链
- **上次状态**: 缺少 `WebMvcConfigurer` 配置
- **当前状态**: ❌ **未修复**
- **详情**: 需要在 `WebMvcConfigurer` 中注册 `PermissionInterceptor`

#### ⓬ [FE] 前端缺少全局 token 过期处理
- **上次状态**: ProtectedRoute 仅页面加载时检查 localStorage
- **当前状态**: ⚠️ **部分修复**
- **详情**: 
  - ✅ `request.ts` 401 响应拦截器已实现（清除 token + 跳转）
  - ❌ ProtectedRoute 组件仍然只在页面加载时检查

#### ⓭ [QLTY] 重复的查询条件代码
- **上次状态**: CustomerServiceImpl 中 pageCustomers() 和 exportCustomers() 重复
- **当前状态**: ❌ **未修复**
- **文件**: `CustomerServiceImpl.java`
- **详情**: 两个方法的 wrapper 构建代码完全相同

#### ⓮ [FE] 前端 try-catch 空块无用户反馈
- **上次状态**: API 调用 catch 为空
- **当前状态**: ❌ **未修复**
- **详情**: 虽然 request.ts 有全局 error 提示，但前端 API 调用层 catch 块仍为空注释

#### ⓯ [FE] 批量删除 API 请求体格式不一致
- **上次状态**: `{ data: ids }` 作为 Axios data 发送
- **当前状态**: ❌ **未修复**
- **文件**: `frontend/src/api/sys.ts:174`

#### ⓰ [ARCH] @Transactional 缺失
- **上次状态**: 多处 Service 缺少事务注解
- **当前状态**: ❌ **未修复**
- **详情**: 
  - `CustomerServiceImpl.deleteCustomers()` 无事务
  - `SysUserServiceImpl.deleteUsers()` 无事务
  - `SysUserServiceImpl.updateUser()` 无事务

### 🟢 ENH 建议（1 个）

#### ⓫ [QLTY] 日志使用不当
- **上次状态**: 业务异常用 `log.error` 记录
- **当前状态**: ❌ **未修复**（但已添加日志）
- **详情**: `GlobalExceptionHandler` 中 `BusinessException` 使用 `log.error` 级别

---

## 新增发现（本次审核）

### ⚠️ 新增问题

#### 1. 测试通过但逻辑存疑
- `NumberingRuleEngineTest.testGenerateNumber_ConcurrentNewDay_DuplicateKeyTriggersRetry` 通过，但测试验证的是 `incrementSeq` 调用 1 次，而非序列值正确性
- 如果 catch 块中的 `seqValue = currentSeq + 1` 逻辑有误，测试可能未覆盖

#### 2. BaseEntity 序列化警告
- Maven 编译输出警告：`Sample.java` 和 `SampleRetention.java` 缺少 `serialVersionUID`

#### 3. 数据权限递归未实现
- `EmcDataPermissionHandler` case 3 注释说明"简化处理"，但实际场景中"本部门及子部门"是常见需求
- 如需完整递归，应使用递归 CTE 或应用层计算子部门列表

#### 4. 前端路由权限控制缺失
- 当前路由是静态配置的，没有根据用户角色动态控制
- `ProtectedRoute` 仅检查 token 存在性，不检查角色/权限

---

## 修复率统计

| 等级 | 总数 | 已修复 | 未修复 | 修复率 |
|------|------|--------|--------|--------|
| P0 致命 | 3 | 0 | 3 | 0% |
| P1 严重 | 5 | 0 | 5 | 0% |
| P2 一般 | 8 | 0 | 8 | 0% |
| ENH 建议 | 10+ | 1 | 9+ | ~10% |
| **总计** | **26** | **1** | **25** | **~4%** |

> 注：日志添加算作 1 个改进项的修复，其余 25 个问题均未修复

---

## 优先修复建议

### 立即修复（影响核心功能）

1. **UserForm.tsx 运行时崩溃** - 部门和角色选择器不可用
2. **AuthOutline 图标** - 编译/运行时可能报错
3. **分页总数错误** - 角色筛选后分页错乱

### 本周修复（影响安全/数据一致性）

4. **PermissionInterceptor ALL 模式** - 权限校验不准确
5. **删除角色/用户关联检查** - 数据一致性风险
6. **密码重置无旧密码验证** - 安全漏洞
7. **数据权限对 customer/sample 表的 SQL 错误** - 可能运行时崩溃

### 本月修复（代码质量）

8. **CORS 配置收紧**
9. **重复查询代码抽取**
10. **@Transactional 补充**
11. **前端路由权限控制**
12. **PermissionInterceptor 注册**
