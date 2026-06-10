package com.emclims.module.auth.controller;

import com.emclims.common.response.R;
import com.emclims.module.sys.dto.LoginRequest;
import com.emclims.module.sys.dto.LoginResponse;
import com.emclims.module.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证 Controller
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return R.ok(authService.login(request));
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public R<LoginResponse> info() {
        return R.ok(authService.getCurrentUserInfo());
    }

    @Operation(summary = "刷新 Token")
    @PostMapping("/refresh")
    public R<LoginResponse> refresh(@RequestParam String token) {
        return R.ok(authService.refreshToken(token));
    }
}
