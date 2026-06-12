package com.emclims.common.security;

import java.util.List;

/**
 * 数据权限上下文
 * 通过 ThreadLocal 存储当前请求用户的数据权限信息
 */
public class DataPermissionContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> DEPT_ID = new ThreadLocal<>();
    private static final ThreadLocal<Integer> DATA_SCOPE = new ThreadLocal<>();
    private static final ThreadLocal<List<Long>> SUB_DEPT_IDS = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setDeptId(Long deptId) {
        DEPT_ID.set(deptId);
    }

    public static Long getDeptId() {
        return DEPT_ID.get();
    }

    public static void setDataScope(Integer dataScope) {
        DATA_SCOPE.set(dataScope);
    }

    public static Integer getDataScope() {
        return DATA_SCOPE.get();
    }

    public static void setSubDeptIds(List<Long> subDeptIds) {
        SUB_DEPT_IDS.set(subDeptIds);
    }

    public static List<Long> getSubDeptIds() {
        return SUB_DEPT_IDS.get();
    }

    public static void clear() {
        USER_ID.remove();
        DEPT_ID.remove();
        DATA_SCOPE.remove();
        SUB_DEPT_IDS.remove();
    }
}
