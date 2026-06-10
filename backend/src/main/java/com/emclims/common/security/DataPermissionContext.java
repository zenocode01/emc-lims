package com.emclims.common.security;

/**
 * 数据权限上下文
 * 通过 ThreadLocal 存储当前请求用户的数据权限信息
 */
public class DataPermissionContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> DEPT_ID = new ThreadLocal<>();
    private static final ThreadLocal<Integer> DATA_SCOPE = new ThreadLocal<>();

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

    public static void clear() {
        USER_ID.remove();
        DEPT_ID.remove();
        DATA_SCOPE.remove();
    }
}
