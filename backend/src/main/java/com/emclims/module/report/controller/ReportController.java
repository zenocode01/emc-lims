package com.emclims.module.report.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.R;
import com.emclims.common.response.PageResult;
import com.emclims.module.report.dto.ReportDTO;
import com.emclims.module.report.dto.ReportQueryDTO;
import com.emclims.module.report.dto.ReportAuditDTO;
import com.emclims.module.report.service.ReportService;
import com.emclims.module.report.vo.ReportAuditLogVO;
import com.emclims.module.report.vo.ReportExportVO;
import com.emclims.module.report.vo.ReportVO;
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
 * 报告管理 Controller
 */
@Tag(name = "报告管理")
@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "分页查询报告列表")
    @GetMapping("/page")
    public R<PageResult<ReportVO>> page(ReportQueryDTO queryDTO) {
        Page<ReportVO> page = reportService.pageReports(queryDTO);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取报告详情")
    @GetMapping("/{id}")
    public R<ReportVO> detail(@PathVariable Long id) {
        return R.ok(reportService.getReportDetail(id));
    }

    @Operation(summary = "新建报告")
    @PostMapping
    public R<Void> create(@Valid @RequestBody ReportDTO dto) {
        reportService.createReport(dto);
        return R.ok();
    }

    @Operation(summary = "更新报告信息")
    @PutMapping
    public R<Void> update(@Valid @RequestBody ReportDTO dto) {
        reportService.updateReport(dto);
        return R.ok();
    }

    @Operation(summary = "删除报告")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        reportService.deleteReport(id);
        return R.ok();
    }

    @Operation(summary = "提交审核")
    @PostMapping("/{id}/submit")
    public R<Void> submit(@PathVariable Long id, @RequestBody(required = false) ReportAuditDTO auditDTO) {
        String comment = auditDTO != null ? auditDTO.getComment() : null;
        reportService.submitForReview(id, comment);
        return R.ok();
    }

    @Operation(summary = "审核通过")
    @PostMapping("/{id}/approve")
    public R<Void> approve(@PathVariable Long id, @RequestBody(required = false) ReportAuditDTO auditDTO) {
        String comment = auditDTO != null ? auditDTO.getComment() : null;
        reportService.approveReport(id, comment);
        return R.ok();
    }

    @Operation(summary = "审核打回")
    @PostMapping("/{id}/reject")
    public R<Void> reject(@PathVariable Long id, @RequestBody(required = false) ReportAuditDTO auditDTO) {
        String comment = auditDTO != null ? auditDTO.getComment() : null;
        reportService.rejectReport(id, comment);
        return R.ok();
    }

    @Operation(summary = "签发报告")
    @PostMapping("/{id}/issue")
    public R<Void> issue(@PathVariable Long id) {
        reportService.issueReport(id);
        return R.ok();
    }

    @Operation(summary = "获取报告审核日志")
    @GetMapping("/{id}/audit-logs")
    public R<List<ReportAuditLogVO>> auditLogs(@PathVariable Long id) {
        return R.ok(reportService.getAuditLogs(id));
    }

    @Operation(summary = "导出报告列表")
    @GetMapping("/export")
    public void export(ReportQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        List<ReportExportVO> list = reportService.exportReports(queryDTO);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("报告列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), ReportExportVO.class)
                .sheet("报告列表")
                .doWrite(list);
    }
}
