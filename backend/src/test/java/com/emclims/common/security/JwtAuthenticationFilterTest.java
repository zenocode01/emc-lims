package com.emclims.common.security;

import com.emclims.module.sys.mapper.SysMenuMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * JwtAuthenticationFilter 认证过滤器单元测试
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private SysMenuMapper menuMapper;

    @Mock
    private DataPermissionLoader dataPermissionLoader;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtUtils, menuMapper, dataPermissionLoader, redisTemplate);
    }

    // === extractToken 测试 ===

    @Test
    void testExtractTokenWithBearer() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer test-token-123");

        java.lang.reflect.Method method = JwtAuthenticationFilter.class
                .getDeclaredMethod("extractToken", HttpServletRequest.class);
        method.setAccessible(true);
        String token = (String) method.invoke(filter, request);

        assertEquals("test-token-123", token);
    }

    @Test
    void testExtractTokenWithoutBearer() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("test-token-123");

        java.lang.reflect.Method method = JwtAuthenticationFilter.class
                .getDeclaredMethod("extractToken", HttpServletRequest.class);
        method.setAccessible(true);
        String token = (String) method.invoke(filter, request);

        assertNull(token);
    }

    @Test
    void testExtractTokenNullHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        java.lang.reflect.Method method = JwtAuthenticationFilter.class
                .getDeclaredMethod("extractToken", HttpServletRequest.class);
        method.setAccessible(true);
        String token = (String) method.invoke(filter, request);

        assertNull(token);
    }

    // === shouldNotFilter 测试 ===

    @Test
    void testShouldNotFilterLogin() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void testShouldNotFilterRefresh() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/auth/refresh");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void testShouldNotFilterDocHtml() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/doc.html");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void testShouldNotFilterWebjars() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/webjars/jquery/jquery.min.js");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void testShouldNotFilterSwaggerResources() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/swagger-resources");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void testShouldNotFilterApiDocs() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v3/api-docs");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void testShouldNotFilterActuator() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/actuator/health");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void testShouldFilter() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/user/page");

        assertFalse(filter.shouldNotFilter(request));
    }

    // === doFilterInternal 测试 ===

    @Test
    void testDoFilterInternalValidToken() throws ServletException, IOException {
        HttpServletRequest request = spy(new MockHttpServletRequest());
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtils.validateToken("valid-token")).thenReturn(true);

        io.jsonwebtoken.Claims claims = mock(io.jsonwebtoken.Claims.class);
        when(claims.getSubject()).thenReturn("1");
        when(claims.get("username", String.class)).thenReturn("admin");
        when(jwtUtils.parseToken("valid-token")).thenReturn(claims);
        when(menuMapper.selectPermissionsByUserId(1L)).thenReturn(Arrays.asList("user:read", "user:write"));
        when(redisTemplate.opsForValue()).thenReturn(mock(org.springframework.data.redis.core.ValueOperations.class));

        filter.doFilter(request, response, filterChain);

        // 验证请求属性
        assertEquals(1L, request.getAttribute("userId"));
        assertEquals("admin", request.getAttribute("username"));
        assertNotNull(request.getAttribute("permissions"));

        verify(dataPermissionLoader).load(1L);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalInvalidToken() throws ServletException, IOException {
        HttpServletRequest request = spy(new MockHttpServletRequest());
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtUtils.validateToken("invalid-token")).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(request.getAttribute("userId"));
        assertNull(request.getAttribute("username"));
        assertNull(request.getAttribute("permissions"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalNoToken() throws ServletException, IOException {
        HttpServletRequest request = spy(new MockHttpServletRequest());
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertNull(request.getAttribute("userId"));
        assertNull(request.getAttribute("username"));
        assertNull(request.getAttribute("permissions"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithEmptyPermissions() throws ServletException, IOException {
        HttpServletRequest request = spy(new MockHttpServletRequest());
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtils.validateToken("valid-token")).thenReturn(true);

        io.jsonwebtoken.Claims claims = mock(io.jsonwebtoken.Claims.class);
        when(claims.getSubject()).thenReturn("1");
        when(claims.get("username", String.class)).thenReturn("admin");
        when(jwtUtils.parseToken("valid-token")).thenReturn(claims);
        when(menuMapper.selectPermissionsByUserId(1L)).thenReturn(Collections.emptyList());
        when(redisTemplate.opsForValue()).thenReturn(mock(org.springframework.data.redis.core.ValueOperations.class));

        filter.doFilter(request, response, filterChain);

        assertNotNull(request.getAttribute("permissions"));
        assertEquals(0, ((List<?>) request.getAttribute("permissions")).size());
        verify(filterChain).doFilter(request, response);
    }
}
