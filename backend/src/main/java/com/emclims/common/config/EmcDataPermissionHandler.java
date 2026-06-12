package com.emclims.common.config;

import com.emclims.common.security.DataPermissionContext;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 表名到部门字段的映射
     * key: 表名（小写）
     * value: 部门字段名，null 表示无部门过滤
     */
    private static final Map<String, String> TABLE_DEPT_FIELD_MAP = new HashMap<>();

    static {
        // sys 模块 - 标准 dept_id
        TABLE_DEPT_FIELD_MAP.put("sys_user", "dept_id");
        TABLE_DEPT_FIELD_MAP.put("sys_dept", "parent_id");
        TABLE_DEPT_FIELD_MAP.put("sys_role", null);
        TABLE_DEPT_FIELD_MAP.put("sys_menu", null);

        // customer 模块 - 使用 customer_id
        TABLE_DEPT_FIELD_MAP.put("customer", null);
        TABLE_DEPT_FIELD_MAP.put("customer_contact", null);

        // sample 模块 - 使用 sample_id
        TABLE_DEPT_FIELD_MAP.put("sample", null);
        TABLE_DEPT_FIELD_MAP.put("sample_image", null);
        TABLE_DEPT_FIELD_MAP.put("sample_log", null);
        TABLE_DEPT_FIELD_MAP.put("sample_retention", null);

        // 默认使用 dept_id
        TABLE_DEPT_FIELD_MAP.put("default", "dept_id");
    }

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

        String deptColumn = resolveDeptColumn(tableName);
        // 表没有部门字段，不做过滤
        if (deptColumn == null) {
            return whereExpression;
        }

        Expression deptExpression;
        switch (dataScope) {
            case 2:
                // 2 - 本部门数据
                deptExpression = new EqualsTo(new Column(deptColumn), new LongValue(deptId));
                break;
            case 3:
                // 3 - 本部门及子部门数据
                // 由于 DataPermissionHandler 在 SQL 拼接阶段调用，无法直接查询数据库
                // 采用简化实现：查询本部门数据
                // 完整实现应在 DataPermissionLoader 中预先加载子部门 ID 列表并设置到 context
                // 然后在 DataPermissionContext 中通过 getSubDeptIds() 获取
                deptExpression = new EqualsTo(new Column(deptColumn), new LongValue(deptId));
                break;
            case 4:
                // 4 - 仅本人数据
                deptExpression = new EqualsTo(new Column("create_by"), new LongValue(userId));
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
     * 根据表名解析部门字段名
     */
    private String resolveDeptColumn(String tableName) {
        if (tableName == null) {
            return "dept_id";
        }
        // 尝试直接匹配
        if (TABLE_DEPT_FIELD_MAP.containsKey(tableName.toLowerCase())) {
            return TABLE_DEPT_FIELD_MAP.get(tableName.toLowerCase());
        }
        // MP 传入完整方法名，提取表名
        String simpleName = extractTableName(tableName);
        return TABLE_DEPT_FIELD_MAP.getOrDefault(simpleName, TABLE_DEPT_FIELD_MAP.get("default"));
    }

    /**
     * 从完整方法名中提取表名
     */
    private String extractTableName(String fullMethodName) {
        // 例如: com.emclims.module.sys.mapper.SysUserMapper.selectPage -> sys_user
        String[] parts = fullMethodName.split("\\.");
        if (parts.length < 2) {
            return fullMethodName.toLowerCase();
        }
        String className = parts[parts.length - 2];
        // 去掉 Mapper 后缀，转小写
        String baseName = className.replaceAll("Mapper$", "");
        // 驼峰转下划线
        return baseName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    /**
     * 判断是否为不需要数据权限过滤的关联表
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
