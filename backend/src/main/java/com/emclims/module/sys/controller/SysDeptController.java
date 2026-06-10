package com.emclims.module.sys.controller;

import com.emclims.common.response.R;
import com.emclims.module.sys.entity.SysDept;
import com.emclims.module.sys.service.SysDeptService;
import com.emclims.module.sys.vo.SysDeptVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理 Controller
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/sys/dept")
public class SysDeptController {

    private final SysDeptService deptService;

    public SysDeptController(SysDeptService deptService) {
        this.deptService = deptService;
    }

    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    public R<List<SysDeptVO>> tree() {
        return R.ok(deptService.getDeptTree());
    }

    @Operation(summary = "获取部门详情")
    @GetMapping("/{id}")
    public R<SysDeptVO> detail(@PathVariable Long id) {
        return R.ok(deptService.getDeptDetail(id));
    }

    @Operation(summary = "新增部门")
    @PostMapping
    public R<Void> create(@RequestBody SysDept dept) {
        deptService.createDept(dept);
        return R.ok();
    }

    @Operation(summary = "更新部门")
    @PutMapping
    public R<Void> update(@RequestBody SysDept dept) {
        deptService.updateDept(dept);
        return R.ok();
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        deptService.deleteDept(id);
        return R.ok();
    }
}
