package com.emclims.module.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.R;
import com.emclims.common.response.PageResult;
import com.emclims.common.security.SecurityUtils;
import com.emclims.module.sys.dto.SysUserDTO;
import com.emclims.module.sys.dto.SysUserQueryDTO;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.service.SysUserService;
import com.emclims.module.sys.vo.SysUserExportVO;
import com.emclims.module.sys.vo.SysUserVO;
import com.alibaba.excel.EasyExcel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 用户管理 Controller
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    private final SysUserService userService;

    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public R<PageResult<SysUserVO>> page(SysUserQueryDTO queryDTO) {
        Page<SysUserVO> page = userService.pageUsers(queryDTO);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public R<SysUserVO> detail(@PathVariable Long id) {
        return R.ok(userService.getUserDetail(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public R<Void> create(@Valid @RequestBody SysUserDTO dto) {
        userService.createUser(dto);
        return R.ok();
    }

    @Operation(summary = "更新用户")
    @PutMapping
    public R<Void> update(@Valid @RequestBody SysUserDTO dto) {
        userService.updateUser(dto);
        return R.ok();
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        userService.deleteUsers(ids);
        return R.ok();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/{id}/password")
    public R<Void> resetPassword(@PathVariable Long id, @RequestParam String oldPassword, @RequestParam String newPassword) {
        userService.resetPassword(id, oldPassword, newPassword);
        return R.ok();
    }

    @Operation(summary = "修改状态")
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return R.ok();
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/current")
    public R<SysUserVO> getCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return R.ok(userService.getUserDetail(currentUserId));
    }

    @Operation(summary = "导出用户列表")
    @GetMapping("/export")
    public void export(SysUserQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        List<SysUserExportVO> list = userService.exportUsers(queryDTO);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户列表_" + System.currentTimeMillis(), "UTF-8")
                .replaceAll("\\+", "%20");
        EasyExcel.write(response.getOutputStream(), SysUserExportVO.class).sheet("用户列表").doWrite(list);
    }

    @Operation(summary = "获取所有用户列表")
    @GetMapping("/all")
    public R<List<SysUserVO>> all() {
        return R.ok(userService.listUsers());
    }
}
