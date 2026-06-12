package com.emclims.module.personnel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.PageResult;
import com.emclims.common.response.R;
import com.emclims.module.personnel.dto.CompetencyMatrixDTO;
import com.emclims.module.personnel.dto.CompetencyMatrixQueryDTO;
import com.emclims.module.personnel.service.CompetencyMatrixService;
import com.emclims.module.personnel.vo.CompetencyMatrixVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 能力矩阵控制器
 */
@RestController
@RequestMapping("/personnel/competency")
@Tag(name = "能力矩阵", description = "人员能力矩阵管理接口")
@RequiredArgsConstructor
public class CompetencyMatrixController {

    private final CompetencyMatrixService competencyService;

    /**
     * 分页查询能力矩阵
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询能力矩阵")
    public R<PageResult<CompetencyMatrixVO>> page(@Valid CompetencyMatrixQueryDTO queryDTO) {
        Page<CompetencyMatrixVO> page = competencyService.pageCompetencyMatrix(queryDTO);
        return R.ok(PageResult.of(page));
    }

    /**
     * 获取能力矩阵详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取能力矩阵详情")
    public R<CompetencyMatrixVO> detail(@PathVariable Long id) {
        return R.ok(competencyService.getCompetencyDetail(id));
    }

    /**
     * 新增能力矩阵记录
     */
    @PostMapping
    @Operation(summary = "新增能力矩阵记录")
    public R<Void> add(@Valid @RequestBody CompetencyMatrixDTO dto) {
        competencyService.addCompetencyMatrix(dto);
        return R.ok();
    }

    /**
     * 更新能力矩阵记录
     */
    @PutMapping
    @Operation(summary = "更新能力矩阵记录")
    public R<Void> update(@Valid @RequestBody CompetencyMatrixDTO dto) {
        competencyService.updateCompetencyMatrix(dto);
        return R.ok();
    }

    /**
     * 批量删除能力矩阵记录
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除能力矩阵记录")
    public R<Void> delete(@RequestBody List<Long> ids) {
        competencyService.deleteCompetencyMatrices(ids);
        return R.ok();
    }

    /**
     * 根据人员ID查询能力矩阵列表
     */
    @GetMapping("/personnel/{personnelId}")
    @Operation(summary = "根据人员ID查询能力矩阵")
    public R<List<CompetencyMatrixVO>> listByPersonnelId(@PathVariable Long personnelId) {
        return R.ok(competencyService.listByPersonnelId(personnelId));
    }

    /**
     * 根据人员和测试项目查询能力矩阵
     */
    @GetMapping("/personnel/{personnelId}/type/{testItemType}")
    @Operation(summary = "根据人员和测试项目查询能力矩阵")
    public R<CompetencyMatrixVO> getByPersonnelAndType(
            @PathVariable Long personnelId,
            @PathVariable String testItemType) {
        return R.ok(competencyService.getByPersonnelAndItemType(personnelId, testItemType));
    }
}
