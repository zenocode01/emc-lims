package com.emclims.common.security;

import com.emclims.module.sys.mapper.SysMenuMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
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
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PERMISSIONS_CACHE_KEY_PREFIX = "user:permissions:";
    private static final Duration PERMISSIONS_CACHE_TTL = Duration.ofMinutes(10);

    public JwtAuthenticationFilter(JwtUtils jwtUtils, SysMenuMapper menuMapper,
                                   DataPermissionLoader dataPermissionLoader,
                                   RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtils = jwtUtils;
        this.menuMapper = menuMapper;
        this.dataPermissionLoader = dataPermissionLoader;
        this.redisTemplate = redisTemplate;
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

            // 从 Redis 缓存加载用户权限列表，供 PermissionInterceptor 使用
            String cacheKey = PERMISSIONS_CACHE_KEY_PREFIX + userId;
            List<String> permissions = (List<String>) redisTemplate.opsForValue().get(cacheKey);

            if (permissions == null) {
                // 缓存未命中，查询数据库
                permissions = menuMapper.selectPermissionsByUserId(userId);
                // 写入缓存，TTL 10 分钟
                if (permissions != null) {
                    redisTemplate.opsForValue().set(cacheKey, permissions, PERMISSIONS_CACHE_TTL);
                }
            }
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
