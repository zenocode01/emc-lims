# Code Review Report - 2026-06-11

## 概述
对最近一次提交（T22 样品编号规则引擎）进行代码审查，发现以下问题。

## 发现问题

### 1. [严重] 并发创建新日期计数器时存在唯一约束冲突

- **文件**: `backend/src/main/java/com/emclims/common/numbering/NumberingRuleEngine.java:68`
- **描述**: `insert` 操作在 `SELECT ... FOR UPDATE` 行锁之前执行。两个并发线程同时看到 `selectOne` 返回 null，均执行 insert，第二个线程会触发 `uk_rule_date` 唯一约束异常。
- **影响**: 高并发下编号规则为新的业务日期首次生成编号时，约一半请求会失败。

### 2. [中等] selectForUpdate 返回值丢弃导致不必要的第三次查询

- **文件**: `backend/src/main/java/com/emclims/common/numbering/NumberingRuleEngine.java:78`
- **描述**: `selectForUpdate` 返回的 `current_seq` 值被直接丢弃，随后又发起第三次 `selectOne` 重新读取。新值 = selectForUpdate 返回值 + 1，完全可通过捕获返回值计算。
- **影响**: 每次编号生成浪费一次数据库往返查询。

### 3. [中等] enableRule/disableRule 对不存在的 ID 静默成功

- **文件**: `backend/src/main/java/com/emclims/module/sys/service/impl/NumberingRuleServiceImpl.java:37`
- **描述**: `getById` 返回 null 时方法静默返回，Controller 将结果包装为 `R.ok()`，前端显示成功但实际未做任何更改。
- **影响**: 用户操作虚假成功，与系统其他模块行为不一致。

### 4. [轻微] getRulesByModule 未过滤已禁用规则

- **文件**: `backend/src/main/java/com/emclims/module/sys/service/impl/NumberingRuleServiceImpl.java:20`
- **描述**: 查询未添加 `status = 1` 过滤条件，返回结果包含已禁用的规则。
- **影响**: 前端用户可能选中禁用规则，触发编号生成时收到错误提示，体验不佳。

### 5. [轻微] self-invocation 绕过 @Transactional AOP 代理

- **文件**: `backend/src/main/java/com/emclims/common/numbering/NumberingRuleEngine.java:46`
- **描述**: `generateNumber(String)` 通过 `this.generateNumber(...)` 自调用绕过 Spring AOP 代理，`@Transactional` 不生效。但由于自增 UPDATE 是原子的，实际不会导致数据损坏，仅 `SELECT FOR UPDATE` 行锁失效。
- **影响**: 行锁失效，在高并发下锁无实际作用，但原子 UPDATE 仍能保证序列号正确递增。

## 修复建议
1. 将 insert 移到 `SELECT FOR UPDATE` 保护范围之后，使用唯一约束异常处理并发插入冲突
2. 捕获 `selectForUpdate` 返回值 +1 替代第三次查询
3. 对不存在的 ID 抛出 `BusinessException`
4. 添加 `eq(SysNumberingRule::getStatus, 1)` 过滤条件
