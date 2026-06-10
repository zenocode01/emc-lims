package com.emclims.module.sys.controller;

import com.emclims.common.response.R;
import com.emclims.module.sys.dto.MenuTreeNode;
import com.emclims.module.sys.entity.SysMenu;
import com.emclims.module.sys.service.SysMenuService;
import com.emclims.module.sys.vo.SysMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理 Controller
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    private final SysMenuService menuService;

    public SysMenuController(SysMenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(summary = "获取菜单树（管理员）")
    @GetMapping("/tree")
    public R<List<SysMenuVO>> tree() {
        return R.ok(menuService.getMenuTree());
    }

    @Operation(summary = "获取菜单树（角色授权用）")
    @GetMapping("/tree-by-role")
    public R<List<MenuTreeNode>> treeByRole(@RequestParam Long roleId) {
        return R.ok(menuService.getMenuTreeByRoleId(roleId));
    }

    @Operation(summary = "获取当前用户菜单树")
    @GetMapping("/current-user/tree")
    public R<List<MenuTreeNode>> currentUserTree() {
        // 从 SecurityContext 中获取当前用户ID
        // 简化处理：先从请求头或参数获取
        Long userId = getCurrentUserId();
        return R.ok(menuService.getMenuTreeByUserId(userId));
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public R<SysMenu> detail(@PathVariable Long id) {
        return R.ok(menuService.getById(id));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    public R<Void> create(@RequestBody SysMenu menu) {
        menuService.createMenu(menu);
        return R.ok();
    }

    @Operation(summary = "更新菜单")
    @PutMapping
    public R<Void> update(@RequestBody SysMenu menu) {
        menuService.updateMenu(menu);
        return R.ok();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return R.ok();
    }

    private Long getCurrentUserId() {
        // TODO: 从 SecurityContext 获取
        return 1L;
    }
}
