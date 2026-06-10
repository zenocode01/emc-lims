package com.emclims.common.security;

import com.emclims.module.sys.mapper.SysMenuMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证过滤器
 * 拦截请求，验证 Token 并设置用户信息到请求属性
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final SysMenuMapper menuMapper;
    private final DataPermissionLoader dataPermissionLoader;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, SysMenuMapper menuMapper,
                                   DataPermissionLoader dataPermissionLoader) {
        this.jwtUtils = jwtUtils;
        this.menuMapper = menuMapper;
        this.dataPermissionLoader = dataPermissionLoader;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取 Token
        String token = extractToken(request);

        if (token != null && jwtUtils.validateToken(token)) {
            Claims claims = jwtUtils.parseToken(token);
            Long userId = Long.valueOf(claims.getSubject());
            String username = claims.get("username", String.class);

            // 将用户信息放入请求属性，供后续使用
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);

            // 加载用户权限列表，供 PermissionInterceptor 使用
            List<String> permissions = menuMapper.selectPermissionsByUserId(userId);
            request.setAttribute("permissions", permissions);

            // 加载数据权限上下文（部门隔离）
            dataPermissionLoader.load(userId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 请求结束后清理 ThreadLocal
            dataPermissionLoader.clear();
        }
    }

    /**
     * 从请求头中提取 Token
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * 配置需要跳过认证的路径
     * 注意：getRequestURI() 包含 context-path（/api），所以排除路径需加 /api 前缀
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> excludePaths = Arrays.asList(
                "/api/auth/login",
                "/api/auth/refresh",
                "/api/doc.html",
                "/api/webjars/",
                "/api/swagger-resources",
                "/api/v3/api-docs",
                "/api/actuator/"
        );
        String path = request.getRequestURI();
        return excludePaths.stream().anyMatch(path::startsWith);
    }
}
