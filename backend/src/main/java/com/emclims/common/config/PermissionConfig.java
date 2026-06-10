package com.emclims.common.config;

import com.emclims.common.security.JwtAuthenticationFilter;
import com.emclims.common.security.PermissionInterceptor;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 权限配置
 */
@Configuration
public class PermissionConfig implements WebMvcConfigurer {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PermissionInterceptor permissionInterceptor;

    public PermissionConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                           PermissionInterceptor permissionInterceptor) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.permissionInterceptor = permissionInterceptor;
    }

    /**
     * 注册权限拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/refresh");
    }
}
