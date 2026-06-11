package com.emclims.module.auth.controller;

import com.emclims.module.auth.service.AuthService;
import com.emclims.module.sys.dto.LoginRequest;
import com.emclims.module.sys.dto.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 认证控制器单元测试
 */
class AuthControllerTest {

    private MockMvc mockMvc;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
    }

    @Test
    void testLogin() throws Exception {
        LoginResponse response = new LoginResponse();
        response.setToken("test-token");
        response.setNickname("管理员");
        response.setUsername("admin");
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("test-token"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void testLogout() throws Exception {
        doNothing().when(authService).logout();

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(authService).logout();
    }

    @Test
    void testInfo() throws Exception {
        LoginResponse response = new LoginResponse();
        response.setUsername("admin");
        response.setNickname("管理员");
        response.setPermissions(List.of("user:read", "user:write"));
        when(authService.getCurrentUserInfo()).thenReturn(response);

        mockMvc.perform(get("/auth/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"));

        verify(authService).getCurrentUserInfo();
    }

    @Test
    void testRefreshToken() throws Exception {
        LoginResponse response = new LoginResponse();
        response.setToken("new-token");
        when(authService.refreshToken("old-token")).thenReturn(response);

        mockMvc.perform(post("/auth/refresh")
                        .param("token", "old-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("new-token"));

        verify(authService).refreshToken("old-token");
    }
}
