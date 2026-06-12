package com.emclims.module.standard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.PageResult;
import com.emclims.common.response.R;
import com.emclims.module.standard.dto.StandardDTO;
import com.emclims.module.standard.dto.StandardQueryDTO;
import com.emclims.module.standard.service.StandardService;
import com.emclims.module.standard.vo.StandardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标准管理 Controller
 */
@RestController
@RequestMapping("/standard")
@Tag(name = "标准管理", description = "标准管理接口")
@RequiredArgsConstructor
public class StandardController {

    private final StandardService standardService;

    /**
     * 分页查询标准
     */
    @Operation(summary = "分页查询标准")
    @GetMapping("/page")
    public R<PageResult<StandardVO>> page(StandardQueryDTO queryDTO) {
        Page<StandardVO> page = standardService.pageStandards(queryDTO);
        return R.ok(PageResult.of(page));
    }

    /**
     * 获取标准详情
     */
    @Operation(summary = "获取标准详情")
    @GetMapping("/{id}")
    public R<StandardVO> detail(@PathVariable Long id) {
        return R.ok(standardService.getStandardDetail(id));
    }

    /**
     * 新增标准
     */
    @Operation(summary = "新增标准")
    @PostMapping
    public R<Void> add(@Valid @RequestBody StandardDTO dto) {
        standardService.addStandard(dto);
        return R.ok();
    }

    /**
     * 更新标准
     */
    @Operation(summary = "更新标准")
    @PutMapping
    public R<Void> update(@Valid @RequestBody StandardDTO dto) {
        standardService.updateStandard(dto);
        return R.ok();
    }

    /**
     * 批量删除标准
     */
    @Operation(summary = "批量删除标准")
    @DeleteMapping
    public R<Void> delete(@RequestBody List<Long> ids) {
        standardService.deleteStandards(ids);
        return R.ok();
    }

    /**
     * 导出标准列表
     */
    @Operation(summary = "导出标准列表")
    @GetMapping("/export")
    public void export(StandardQueryDTO queryDTO, HttpServletResponse response) throws java.io.IOException {
        List<com.emclims.module.standard.vo.StandardExportVO> list = standardService.exportStandards(queryDTO);
        com.alibaba.excel.EasyExcel.write(response.getOutputStream(), com.emclims.module.standard.vo.StandardExportVO.class)
                .sheet("标准列表")
                .doWrite(list);
    }
}
