package com.emclims.common.security;

import com.emclims.common.exception.BusinessException;
import com.emclims.module.sys.annotation.RequirePermission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PermissionInterceptor 权限拦截器单元测试
 */
@ExtendWith(MockitoExtension.class)
class PermissionInterceptorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private PermissionInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new PermissionInterceptor();
    }

    @Test
    void testNonHandlerMethod() throws Exception {
        boolean result = interceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }

    @Test
    void testNoAnnotation() throws Exception {
        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getMethodAnnotation(RequirePermission.class)).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertTrue(result);
    }

    @Test
    void testHasPermission() throws Exception {
        RequirePermission annotation = mock(RequirePermission.class);
        when(annotation.value()).thenReturn("sys:user:list");
        when(annotation.mode()).thenReturn(RequirePermission.PermissionMode.ANY);

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getMethodAnnotation(RequirePermission.class)).thenReturn(annotation);

        when(request.getAttribute("permissions")).thenReturn(List.of("sys:user:list", "sys:role:list"));

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertTrue(result);
    }

    @Test
    void testNoPermission() {
        RequirePermission annotation = mock(RequirePermission.class);
        when(annotation.value()).thenReturn("sys:user:delete");
        when(annotation.mode()).thenReturn(RequirePermission.PermissionMode.ANY);

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getMethodAnnotation(RequirePermission.class)).thenReturn(annotation);

        when(request.getAttribute("permissions")).thenReturn(List.of("sys:user:list"));

        assertThrows(BusinessException.class,
                () -> interceptor.preHandle(request, response, handlerMethod));
    }

    @Test
    void testEmptyPermissions() {
        RequirePermission annotation = mock(RequirePermission.class);
        lenient().when(annotation.value()).thenReturn("sys:user:list");
        lenient().when(annotation.mode()).thenReturn(RequirePermission.PermissionMode.ANY);

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getMethodAnnotation(RequirePermission.class)).thenReturn(annotation);

        when(request.getAttribute("permissions")).thenReturn(List.of());

        assertThrows(BusinessException.class,
                () -> interceptor.preHandle(request, response, handlerMethod));
    }

    @Test
    void testNullPermissions() {
        RequirePermission annotation = mock(RequirePermission.class);
        lenient().when(annotation.value()).thenReturn("sys:user:list");
        lenient().when(annotation.mode()).thenReturn(RequirePermission.PermissionMode.ANY);

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getMethodAnnotation(RequirePermission.class)).thenReturn(annotation);

        when(request.getAttribute("permissions")).thenReturn(null);

        assertThrows(BusinessException.class,
                () -> interceptor.preHandle(request, response, handlerMethod));
    }
}
