package com.emclims.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * 安全工具类
 * 从当前请求中获取用户身份信息
 */
public class SecurityUtils {

    /**
     * 获取当前请求对象
     */
    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (attrs != null) {
            return attrs.getRequest();
        }
        return null;
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            Object userId = request.getAttribute("userId");
            return userId instanceof Long ? (Long) userId : null;
        }
        return null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Object username = getRequest().getAttribute("username");
        return username != null ? username.toString() : null;
    }

    /**
     * 获取当前登录用户权限列表
     */
    @SuppressWarnings("unchecked")
    public static List<String> getCurrentPermissions() {
        Object permissions = getRequest().getAttribute("permissions");
        return permissions instanceof List ? (List<String>) permissions : null;
    }

    /**
     * 判断是否已登录
     */
    public static boolean isAuthenticated() {
        return getCurrentUserId() != null;
    }
}
