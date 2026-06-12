package com.emclims.module.standard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.PageResult;
import com.emclims.common.response.R;
import com.emclims.module.standard.dto.StandardCategoryDTO;
import com.emclims.module.standard.dto.StandardCategoryQueryDTO;
import com.emclims.module.standard.service.StandardCategoryService;
import com.emclims.module.standard.vo.StandardCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标准分类管理 Controller
 */
@RestController
@RequestMapping("/standard/category")
@Tag(name = "标准分类管理", description = "标准分类管理接口")
@RequiredArgsConstructor
public class StandardCategoryController {

    private final StandardCategoryService categoryService;

    /**
     * 分页查询标准分类
     */
    @Operation(summary = "分页查询标准分类")
    @GetMapping("/page")
    public R<PageResult<StandardCategoryVO>> page(StandardCategoryQueryDTO queryDTO) {
        Page<StandardCategoryVO> page = categoryService.pageCategories(queryDTO);
        return R.ok(PageResult.of(page));
    }

    /**
     * 获取标准分类详情
     */
    @Operation(summary = "获取标准分类详情")
    @GetMapping("/{id}")
    public R<StandardCategoryVO> detail(@PathVariable Long id) {
        return R.ok(categoryService.getCategoryDetail(id));
    }

    /**
     * 新增标准分类
     */
    @Operation(summary = "新增标准分类")
    @PostMapping
    public R<Void> add(@Valid @RequestBody StandardCategoryDTO dto) {
        categoryService.addCategory(dto);
        return R.ok();
    }

    /**
     * 更新标准分类
     */
    @Operation(summary = "更新标准分类")
    @PutMapping
    public R<Void> update(@Valid @RequestBody StandardCategoryDTO dto) {
        categoryService.updateCategory(dto);
        return R.ok();
    }

    /**
     * 批量删除标准分类
     */
    @Operation(summary = "批量删除标准分类")
    @DeleteMapping
    public R<Void> delete(@RequestBody List<Long> ids) {
        categoryService.deleteCategories(ids);
        return R.ok();
    }
}
