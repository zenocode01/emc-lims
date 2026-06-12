package com.emclims.module.report.controller;

import com.emclims.common.response.R;
import com.emclims.module.sys.annotation.RequirePermission;
import com.emclims.common.response.PageResult;
import com.emclims.module.report.dto.ReportTemplateDTO;
import com.emclims.module.report.dto.ReportTemplateQueryDTO;
import com.emclims.module.report.service.ReportTemplateService;
import com.emclims.module.report.vo.ReportTemplateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 报告模板 Controller
 */
@Slf4j
@RestController
@RequestMapping("/report/template")
@RequiredArgsConstructor
@Tag(name = "报告模板", description = "报告模板管理")
public class ReportTemplateController {

    private final ReportTemplateService reportTemplateService;

    /**
     * 分页查询报告模板
     */
    @Operation(summary = "分页查询报告模板")
    @GetMapping("/page")
    @RequirePermission("report:template:list")
    public R<PageResult<ReportTemplateVO>> page(ReportTemplateQueryDTO queryDTO) {
        return R.ok(reportTemplateService.pageTemplates(queryDTO));
    }

    /**
     * 获取报告模板详情
     */
    @Operation(summary = "获取报告模板详情")
    @GetMapping("/{id}")
    @RequirePermission("report:template:detail")
    public R<ReportTemplateVO> detail(@PathVariable Long id) {
        return R.ok(reportTemplateService.getTemplateDetail(id));
    }

    /**
     * 创建报告模板
     */
    @Operation(summary = "创建报告模板")
    @PostMapping
    @RequirePermission("report:template:create")
    public R<Void> create(@Valid @RequestBody ReportTemplateDTO dto) {
        reportTemplateService.createTemplate(dto);
        return R.ok();
    }

    /**
     * 更新报告模板
     */
    @Operation(summary = "更新报告模板")
    @PutMapping
    @RequirePermission("report:template:update")
    public R<Void> update(@Valid @RequestBody ReportTemplateDTO dto) {
        reportTemplateService.updateTemplate(dto);
        return R.ok();
    }

    /**
     * 删除报告模板
     */
    @Operation(summary = "删除报告模板")
    @DeleteMapping("/{id}")
    @RequirePermission("report:template:delete")
    public R<Void> delete(@PathVariable Long id) {
        reportTemplateService.deleteTemplate(id);
        return R.ok();
    }

    /**
     * 批量删除报告模板
     */
    @Operation(summary = "批量删除报告模板")
    @DeleteMapping("/{ids}")
    @RequirePermission("report:template:delete")
    public R<Void> deleteBatch(@PathVariable Long[] ids) {
        reportTemplateService.deleteTemplates(ids);
        return R.ok();
    }

    /**
     * 更新报告模板状态
     */
    @Operation(summary = "更新报告模板状态")
    @PutMapping("/{id}/status")
    @RequirePermission("report:template:update")
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        reportTemplateService.updateTemplateStatus(id, status);
        return R.ok();
    }
}
