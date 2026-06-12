package com.emclims.module.report.service.impl;

import com.emclims.common.exception.BusinessException;
import com.emclims.module.audit.service.OperationLogService;
import com.emclims.module.customer.entity.Customer;
import com.emclims.module.customer.mapper.CustomerMapper;
import com.emclims.module.report.entity.Report;
import com.emclims.module.report.mapper.ReportMapper;
import com.emclims.module.report.vo.ReportTemplateVO;
import com.emclims.module.test.entity.TestPlan;
import com.emclims.module.test.entity.TestRecord;
import com.emclims.module.test.mapper.TestPlanMapper;
import com.emclims.module.test.mapper.TestRecordMapper;
import com.emclims.module.report.service.ReportGenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报告生成服务实现
 * 支持 Word 模板替换和 PDF 导出
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportGenerateServiceImpl implements ReportGenerateService {

    private final ReportMapper reportMapper;
    private final TestPlanMapper testPlanMapper;
    private final TestRecordMapper testRecordMapper;
    private final CustomerMapper customerMapper;

    @Override
    public InputStream generateWordReport(ReportTemplateVO template, Map<String, Object> data) {
        log.info("生成 Word 报告，模板：{}", template.getTemplateName());

        // 将模板内容（JSON）转换为 Map
        Map<String, Object> templateData = parseTemplateContent(template);

        // 合并数据
        Map<String, Object> mergedData = new HashMap<>();
        mergedData.putAll(templateData);
        mergedData.putAll(data);

        // 生成 Word 文档（简化版，实际应使用 docx4j 或 Apache POI）
        String wordContent = generateWordContent(mergedData);
        return new ByteArrayInputStream(wordContent.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public InputStream generatePdfReport(ReportTemplateVO template, Map<String, Object> data) {
        log.info("生成 PDF 报告，模板：{}", template.getTemplateName());

        // 先生成 Word，再转换为 PDF
        // 实际项目中可使用 Apache PDFBox 或 iText 进行 PDF 生成
        InputStream wordStream = generateWordReport(template, data);

        // TODO: 集成 PDF 转换（Word -> PDF）
        // 可以使用 Apache POI + Apache PDFBox 或第三方服务

        return wordStream;
    }

    @Override
    public Map<String, Object> generateReportData(Long reportId) {
        log.info("生成报告数据，报告 ID: {}", reportId);

        Map<String, Object> data = new HashMap<>();

        // 查询报告信息
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        data.put("report", report);
        data.put("reportNo", report.getReportNo());
        data.put("version", report.getVersion());
        data.put("status", report.getStatus());
        data.put("issuedDate", report.getIssuedDate());

        // 查询样品信息
        Long sampleId = report.getSampleId();
        if (sampleId != null) {
            // TODO: 查询样品详情
            // Sample sample = sampleMapper.selectById(sampleId);
            // data.put("sample", sample);
        }

        // 查询客户信息
        Long customerId = report.getCustomerId();
        if (customerId != null) {
            Customer customer = customerMapper.selectById(customerId);
            if (customer != null) {
                data.put("customer", customer);
                data.put("customerName", customer.getName());
                data.put("customerContact", customer.getContact());
                data.put("customerPhone", customer.getPhone());
                data.put("customerAddress", customer.getAddress());
            }
        }

        // 查询测试计划
        List<TestPlan> testPlans = testPlanMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TestPlan>()
                .eq(TestPlan::getSampleId, sampleId)
        );
        data.put("testPlans", testPlans);

        // 查询测试记录（通过测试计划ID关联）
        if (!testPlans.isEmpty()) {
            List<Long> planIds = testPlans.stream().map(TestPlan::getId).collect(java.util.stream.Collectors.toList());
            List<TestRecord> testRecords = testRecordMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TestRecord>()
                    .in(TestRecord::getTestPlanId, planIds)
            );
            data.put("testRecords", testRecords);
        }

        // 生成日期
        data.put("generateDate", java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        return data;
    }

    /**
     * 解析模板内容（JSON 格式）
     */
    private Map<String, Object> parseTemplateContent(ReportTemplateVO template) {
        String templateContent = template.getTemplateContent();
        if (templateContent == null || templateContent.isEmpty()) {
            return new HashMap<>();
        }
        // TODO: 使用 Jackson 或 Gson 解析 JSON
        // ObjectMapper mapper = new ObjectMapper();
        // return mapper.readValue(templateContent, Map.class);
        return new HashMap<>();
    }

    /**
     * 生成 Word 内容（简化版）
     */
    private String generateWordContent(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='UTF-8'></head><body>");
        sb.append("<h1>测试报告</h1>");
        sb.append("<p>报告编号：").append(data.get("reportNo")).append("</p>");
        sb.append("<p>版本：v").append(data.get("version")).append("</p>");
        sb.append("<p>签发日期：").append(data.get("issuedDate")).append("</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
