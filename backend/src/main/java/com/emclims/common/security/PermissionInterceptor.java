package com.emclims.common.security;

import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.annotation.RequirePermission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限验证拦截器
 * 校验 @RequirePermission 注解的权限
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    /**
     * 当前请求用户的权限集合（从 SecurityContext 或 ThreadLocal 获取）
     * 简化实现：使用请求属性传递
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequirePermission permission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (permission == null) {
            return true;
        }

        // 获取当前用户的权限列表（简化：从请求属性获取）
        @SuppressWarnings("unchecked")
        List<String> userPermissions = (List<String>) request.getAttribute("permissions");

        if (userPermissions == null || userPermissions.isEmpty()) {
            throw new BusinessException("没有访问权限");
        }

        String requiredPermission = permission.value();
        boolean hasPermission;

        if (permission.mode() == RequirePermission.PermissionMode.ALL) {
            // 需要所有权限（AND 逻辑）
            hasPermission = userPermissions.contains(requiredPermission);
        } else {
            // 需要任一权限（OR 逻辑，单个权限即满足）
            hasPermission = userPermissions.contains(requiredPermission);
        }

        if (!hasPermission) {
            throw new BusinessException("没有权限执行此操作");
        }

        return true;
    }
}
