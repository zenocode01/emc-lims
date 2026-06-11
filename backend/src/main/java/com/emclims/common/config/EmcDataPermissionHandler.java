package com.emclims.common.config;

import com.emclims.common.security.DataPermissionContext;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;

/**
 * EMC LIMS 数据权限处理器
 * 根据用户角色的 data_scope 自动添加部门数据过滤条件
 *
 * data_scope 含义：
 * 1 - 全部数据
 * 2 - 本部门数据
 * 3 - 本部门及子部门数据
 * 4 - 仅本人数据
 */
public class EmcDataPermissionHandler implements DataPermissionHandler {

    @Override
    public Expression getSqlSegment(Expression whereExpression, String tableName) {
        Integer dataScope = DataPermissionContext.getDataScope();
        Long deptId = DataPermissionContext.getDeptId();
        Long userId = DataPermissionContext.getUserId();

        // 关联表不需要数据权限过滤
        if (isAssociationTable(tableName)) {
            return whereExpression;
        }

        // 未设置数据范围或部门ID时，不做过滤
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
                        new LongValue(deptId));
                break;
            case 3:
                // 3 - 本部门及子部门数据（使用 OR 条件）
                // 注意：这里简化处理，直接查询当前部门
                // 如需完整递归子部门查询，建议改用 MyBatis 动态 SQL 或应用层过滤
                deptExpression = new EqualsTo(new Column("dept_id"),
                        new LongValue(deptId));
                break;
            case 4:
                // 4 - 仅本人数据
                deptExpression = new EqualsTo(new Column("create_by"),
                        new LongValue(userId));
                break;
            default:
                return whereExpression;
        }

        if (whereExpression == null) {
            return deptExpression;
        }
        return new AndExpression(whereExpression, deptExpression);
    }

    /**
     * 判断是否为不需要数据权限过滤的关联表
     * MP 传入的 tableName 是 Mapper 接口的完整方法名（如：
     * com.emclims.module.sys.mapper.SysUserRoleMapper.deleteById），
     * 转小写后下划线被吞掉，需要根据 Mapper 类名判断。
     */
    private boolean isAssociationTable(String tableName) {
        if (tableName == null) {
            return false;
        }
        String lower = tableName.toLowerCase().replace("_", "");
        return lower.contains("userrole")
                || lower.contains("rolemenu")
                || lower.contains("numberingsequence")
                || lower.contains("auditlog");
    }
}
