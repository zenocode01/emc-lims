package com.emclims.common.config;

import com.emclims.common.security.DataPermissionContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EmcDataPermissionHandler 数据权限处理器单元测试
 * 验证各 data_scope 下 SQL WHERE 条件表达式的生成逻辑
 */
class EmcDataPermissionHandlerTest {

    private EmcDataPermissionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new EmcDataPermissionHandler();
        DataPermissionContext.clear();
    }

    @AfterEach
    void tearDown() {
        DataPermissionContext.clear();
    }

    @Test
    void testDataScopeAllReturnsOriginalExpression() {
        // dataScope=1（全部数据）：返回原始表达式，不做过滤
        DataPermissionContext.setDataScope(1);
        DataPermissionContext.setDeptId(10L);
        DataPermissionContext.setUserId(1L);

        Expression original = new StringValue("original_expr");
        Expression result = handler.getSqlSegment(original, "sys_user");

        assertSame(original, result, "dataScope=1 应直接返回原始表达式");
    }

    @Test
    void testDataScopeNullReturnsOriginalExpression() {
        // dataScope 为 null：返回原始表达式
        // deptId 和 userId 有值但不影响
        DataPermissionContext.setDeptId(10L);
        DataPermissionContext.setUserId(1L);

        Expression original = new StringValue("original_expr");
        Expression result = handler.getSqlSegment(original, "sys_user");

        assertSame(original, result, "dataScope=null 应直接返回原始表达式");
    }

    @Test
    void testDeptIdNullReturnsOriginalExpression() {
        // deptId 为 null：返回原始表达式
        DataPermissionContext.setDataScope(2);
        DataPermissionContext.setUserId(1L);

        Expression original = new StringValue("original_expr");
        Expression result = handler.getSqlSegment(original, "sys_user");

        assertSame(original, result, "deptId=null 应直接返回原始表达式");
    }

    @Test
    void testDataScopeDeptOnly() {
        // dataScope=2（本部门数据）：添加 dept_id = ? 条件
        DataPermissionContext.setDataScope(2);
        DataPermissionContext.setDeptId(10L);
        DataPermissionContext.setUserId(1L);

        Expression result = handler.getSqlSegment(null, "sys_user");

        assertNotNull(result, "dataScope=2 应生成过滤条件");
        assertTrue(result instanceof EqualsTo, "dataScope=2 应生成 EqualsTo 表达式");
        assertEquals("dept_id", ((EqualsTo) result).getLeftExpression().toString(),
                "左侧应为 dept_id 列");
    }

    @Test
    void testDataScopeDeptAndSubDept() {
        // dataScope=3（本部门及子部门）：暂时等同于 dept_id = ?
        DataPermissionContext.setDataScope(3);
        DataPermissionContext.setDeptId(20L);
        DataPermissionContext.setUserId(1L);

        Expression result = handler.getSqlSegment(null, "sys_user");

        assertNotNull(result, "dataScope=3 应生成过滤条件");
        assertTrue(result instanceof EqualsTo, "dataScope=3 应生成 EqualsTo 表达式");
        assertEquals("dept_id", ((EqualsTo) result).getLeftExpression().toString());
    }

    @Test
    void testDataScopeSelf() {
        // dataScope=4（仅本人数据）：添加 create_by = ? AND dept_id = ? 条件
        DataPermissionContext.setDataScope(4);
        DataPermissionContext.setDeptId(10L);
        DataPermissionContext.setUserId(1L);

        Expression result = handler.getSqlSegment(null, "sys_user");

        assertNotNull(result, "dataScope=4 应生成过滤条件");
        assertTrue(result instanceof AndExpression, "dataScope=4 应生成 AndExpression");

        AndExpression andExpr = (AndExpression) result;
        String andStr = andExpr.toString();
        assertTrue(andStr.contains("create_by"), "应包含 create_by 条件");
        assertTrue(andStr.contains("dept_id"), "应包含 dept_id 条件");
    }

    @Test
    void testDataScopeSelfWithOriginalExpression() {
        // dataScope=4 且已有 whereExpression：应合并（AND 连接）
        DataPermissionContext.setDataScope(4);
        DataPermissionContext.setDeptId(10L);
        DataPermissionContext.setUserId(1L);

        Expression original = new StringValue("status = 1");
        Expression result = handler.getSqlSegment(original, "sys_user");

        assertNotNull(result, "应生成合并后的表达式");
        assertTrue(result instanceof AndExpression, "应生成 AndExpression 合并表达式");
    }

    @Test
    void testDataScopeInvalid() {
        // 无效的 dataScope 值：返回原始表达式
        DataPermissionContext.setDataScope(99);
        DataPermissionContext.setDeptId(10L);
        DataPermissionContext.setUserId(1L);

        Expression original = new StringValue("original_expr");
        Expression result = handler.getSqlSegment(original, "sys_user");

        assertSame(original, result, "无效 dataScope 应直接返回原始表达式");
    }

    @Test
    void testDefaultScopeNotSet() {
        // 未设置任何上下文：返回原始表达式
        Expression original = new StringValue("status = 1 AND name LIKE '%test%'");
        Expression result = handler.getSqlSegment(original, "sys_user");

        assertSame(original, result, "未设置上下文时应直接返回原始表达式");
    }
}
