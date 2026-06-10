package com.emclims.common.config;

import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.emclims.common.security.DataPermissionContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;

/**
 * 数据权限处理器
 * 根据用户角色的 data_scope 自动添加部门数据过滤条件
 *
 * data_scope 含义：
 * 1 - 全部数据
 * 2 - 本部门数据
 * 3 - 本部门及子部门数据
 * 4 - 仅本人数据
 */
public class DataPermissionHandler extends DataPermissionInterceptor {

    @Override
    public Expression getWhere(String tableName, Expression whereExpression) {
        Integer dataScope = DataPermissionContext.getDataScope();
        Long deptId = DataPermissionContext.getDeptId();
        Long userId = DataPermissionContext.getUserId();

        if (dataScope == null || deptId == null) {
            return whereExpression;
        }

        // 1 - 全部数据，不需要过滤
        if (dataScope == 1) {
            return whereExpression;
        }

        Expression deptExpression;
        switch (dataScope) {
            case 2:
                // 2 - 本部门数据
                deptExpression = new EqualsTo(new Column("dept_id"),
                        new net.sf.jsqlparser.expression.LongExpression(deptId));
                break;
            case 3:
                // 3 - 本部门及子部门数据（简化处理：后续可使用 CTE 递归）
                deptExpression = new EqualsTo(new Column("dept_id"),
                        new net.sf.jsqlparser.expression.LongExpression(deptId));
                break;
            case 4:
                // 4 - 仅本人数据（创建人 + 所属部门）
                deptExpression = new AndExpression(
                        new EqualsTo(new Column("create_by"),
                                new net.sf.jsqlparser.expression.LongExpression(userId)),
                        new EqualsTo(new Column("dept_id"),
                                new net.sf.jsqlparser.expression.LongExpression(deptId))
                );
                break;
            default:
                return whereExpression;
        }

        if (whereExpression == null) {
            return deptExpression;
        }
        return new AndExpression(whereExpression, deptExpression);
    }
}
