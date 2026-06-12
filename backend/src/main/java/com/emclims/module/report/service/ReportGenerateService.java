package com.emclims.module.report.service;

import com.emclims.module.report.vo.ReportTemplateVO;

import java.io.InputStream;
import java.util.Map;

/**
 * 报告生成服务接口
 */
public interface ReportGenerateService {

    /**
     * 根据模板生成报告（Word 格式）
     * @param template 报告模板
     * @param data 报告数据
     * @return Word 文档输入流
     */
    InputStream generateWordReport(ReportTemplateVO template, Map<String, Object> data);

    /**
     * 根据模板生成报告（PDF 格式）
     * @param template 报告模板
     * @param data 报告数据
     * @return PDF 文档输入流
     */
    InputStream generatePdfReport(ReportTemplateVO template, Map<String, Object> data);

    /**
     * 生成报告数据
     * @param reportId 报告 ID
     * @return 报告数据
     */
    Map<String, Object> generateReportData(Long reportId);
}
