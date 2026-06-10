package com.emclims.module.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.emclims.common.response.R;
import com.emclims.module.sys.dto.RoleMenuDTO;
import com.emclims.module.sys.entity.SysRole;
import com.emclims.module.sys.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/sys/role")
public class SysRoleController {

    private final SysRoleService roleService;

    public SysRoleController(SysRoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "分页查询角色列表")
    @GetMapping("/page")
    public R<Page<SysRole>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysRole> page = roleService.page(new PageDTO<>(pageNum, pageSize));
        return R.ok(page);
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public R<SysRole> detail(@PathVariable Long id) {
        return R.ok(roleService.getRoleDetail(id));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public R<Void> create(@RequestBody SysRole role) {
        roleService.createRole(role);
        return R.ok();
    }

    @Operation(summary = "更新角色")
    @PutMapping
    public R<Void> update(@RequestBody SysRole role) {
        roleService.updateRole(role);
        return R.ok();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return R.ok();
    }

    @Operation(summary = "批量删除角色")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        roleService.deleteRoles(ids);
        return R.ok();
    }

    @Operation(summary = "更新状态")
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        roleService.updateRoleStatus(id, status);
        return R.ok();
    }

    @Operation(summary = "角色授权菜单")
    @PostMapping("/grant-menus")
    public R<Void> grantMenus(@RequestBody RoleMenuDTO dto) {
        roleService.grantMenus(dto);
        return R.ok();
    }

    @Operation(summary = "获取角色的菜单ID列表")
    @GetMapping("/{roleId}/menu-ids")
    public R<List<Long>> getMenuIds(@PathVariable Long roleId) {
        return R.ok(roleService.getMenuIdsByRoleId(roleId));
    }
}
