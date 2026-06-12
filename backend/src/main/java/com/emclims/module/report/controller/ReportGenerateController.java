package com.emclims.module.report.controller;

import com.emclims.common.response.R;
import com.emclims.module.report.vo.ReportTemplateVO;
import com.emclims.module.report.service.ReportGenerateService;
import com.emclims.module.report.service.ReportTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 报告生成 Controller
 */
@Slf4j
@RestController
@RequestMapping("/report/generate")
@RequiredArgsConstructor
@Tag(name = "报告生成", description = "报告生成与导出")
public class ReportGenerateController {

    private final ReportGenerateService reportGenerateService;
    private final ReportTemplateService reportTemplateService;

    /**
     * 根据模板生成报告
     */
    @Operation(summary = "根据模板生成报告")
    @PostMapping("/{reportId}/{templateId}/word")
    public void generateWordReport(
            @PathVariable Long reportId,
            @PathVariable Long templateId,
            HttpServletResponse response
    ) throws IOException {
        log.info("生成 Word 报告，报告 ID: {}, 模板 ID: {}", reportId, templateId);

        ReportTemplateVO templateVO = reportTemplateService.getTemplateDetail(templateId);
        if (templateVO == null) {
            throw new RuntimeException("模板不存在");
        }

        Map<String, Object> data = reportGenerateService.generateReportData(reportId);
        InputStream inputStream = reportGenerateService.generateWordReport(templateVO, data);

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_" + reportId + ".docx");

        inputStream.transferTo(response.getOutputStream());
        inputStream.close();
    }

    /**
     * 根据模板生成 PDF 报告
     */
    @Operation(summary = "根据模板生成 PDF 报告")
    @PostMapping("/{reportId}/{templateId}/pdf")
    public void generatePdfReport(
            @PathVariable Long reportId,
            @PathVariable Long templateId,
            HttpServletResponse response
    ) throws IOException {
        log.info("生成 PDF 报告，报告 ID: {}, 模板 ID: {}", reportId, templateId);

        ReportTemplateVO templateVO = reportTemplateService.getTemplateDetail(templateId);
        if (templateVO == null) {
            throw new RuntimeException("模板不存在");
        }

        Map<String, Object> data = reportGenerateService.generateReportData(reportId);
        InputStream inputStream = reportGenerateService.generatePdfReport(templateVO, data);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_" + reportId + ".pdf");

        inputStream.transferTo(response.getOutputStream());
        inputStream.close();
    }

    /**
     * 获取报告数据（用于预览）
     */
    @Operation(summary = "获取报告数据")
    @GetMapping("/{reportId}/preview")
    public R<Map<String, Object>> getReportPreview(@PathVariable Long reportId) {
        Map<String, Object> data = reportGenerateService.generateReportData(reportId);
        return R.ok(data);
    }
}
