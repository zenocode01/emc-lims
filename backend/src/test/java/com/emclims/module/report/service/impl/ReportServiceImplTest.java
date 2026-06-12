package com.emclims.module.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.exception.BusinessException;
import com.emclims.common.numbering.NumberingRuleEngine;
import com.emclims.common.security.SecurityUtils;
import com.emclims.module.customer.entity.Customer;
import com.emclims.module.customer.mapper.CustomerMapper;
import com.emclims.module.report.dto.ReportDTO;
import com.emclims.module.report.dto.ReportQueryDTO;
import com.emclims.module.report.entity.Report;
import com.emclims.module.report.entity.ReportAuditLog;
import com.emclims.module.report.enums.ReportActionEnum;
import com.emclims.module.report.enums.ReportStatusEnum;
import com.emclims.module.report.mapper.ReportAuditLogMapper;
import com.emclims.module.report.vo.ReportAuditLogVO;
import com.emclims.module.report.vo.ReportVO;
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.mapper.SampleMapper;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ReportServiceImpl 报告服务单元测试
 *
 * <p>测试覆盖以下业务方法：
 * <ul>
 *   <li>pageReports - 分页查询报告</li>
 *   <li>getReportDetail - 获取报告详情</li>
 *   <li>createReport - 新建报告</li>
 *   <li>updateReport - 更新报告</li>
 *   <li>submitForReview - 提交审核</li>
 *   <li>approveReport - 审核通过</li>
 *   <li>rejectReport - 审核打回</li>
 *   <li>issueReport - 签发报告</li>
 *   <li>getAuditLogs - 获取审核日志</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportAuditLogMapper auditLogMapper;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SampleMapper sampleMapper;

    @Mock
    private NumberingRuleEngine numberingRuleEngine;

    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl(auditLogMapper, customerMapper, userMapper, sampleMapper, numberingRuleEngine);

        // 模拟 RequestContextHolder，供 SecurityUtils.getCurrentUserId() 使用
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 1L);
        request.setAttribute("username", "admin");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    // ==================== pageReports 测试 ====================

    /**
     * 测试正常分页查询报告
     */
    @Test
    void testPageReports() {
        ReportQueryDTO queryDTO = new ReportQueryDTO();
        queryDTO.setKeyword("报告");
        queryDTO.setCustomerId(50L);
        queryDTO.setCreateTimeEnd(LocalDate.of(2025, 12, 31));
        queryDTO.setStatus("draft");
        queryDTO.setCreateTimeEnd(LocalDate.of(2025, 12, 31));
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Report report = new Report();
        report.setId(1L);
        report.setReportNo("RPT-2025-001");
        report.setSampleId(100L);
        report.setCustomerId(50L);
        report.setStatus(ReportStatusEnum.DRAFT.getValue());
        report.setVersion(1);

        Customer customer = new Customer();
        customer.setId(50L);
        customer.setName("测试客户");

        Sample sample = new Sample();
        sample.setId(100L);
        sample.setSampleNo("SPL-2025-001");

        Page<Report> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(report));

        ReportServiceImpl spy = spy(reportService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));
        doReturn(List.of(customer)).when(customerMapper).selectBatchIds(anyList());

        Page<ReportVO> result = spy.pageReports(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("RPT-2025-001", result.getRecords().get(0).getReportNo());
        assertEquals("测试客户", result.getRecords().get(0).getCustomerName());
        assertEquals("草稿", result.getRecords().get(0).getStatusName());
    }

    /**
     * 测试空结果分页查询
     */
    @Test
    void testPageReportsEmpty() {
        ReportQueryDTO queryDTO = new ReportQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);
        queryDTO.setCreateTimeEnd(LocalDate.of(2025, 12, 31));

        Page<Report> pageResult = new Page<>(1, 10, 0);
        pageResult.setRecords(Collections.emptyList());

        ReportServiceImpl spy = spy(reportService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<ReportVO> result = spy.pageReports(queryDTO);

        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
        assertEquals(0, result.getTotal());
    }

    /**
     * 测试按客户ID筛选分页查询
     */
    @Test
    void testPageReportsByCustomerId() {
        ReportQueryDTO queryDTO = new ReportQueryDTO();
        queryDTO.setCustomerId(50L);
        queryDTO.setCreateTimeEnd(LocalDate.of(2025, 12, 31));
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Report report = new Report();
        report.setId(1L);
        report.setReportNo("RPT-2025-001");
        report.setCustomerId(50L);
        report.setStatus(ReportStatusEnum.DRAFT.getValue());

        Customer customer = new Customer();
        customer.setId(50L);
        customer.setName("指定客户");

        Page<Report> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(report));

        doReturn(List.of(customer)).when(customerMapper).selectBatchIds(anyList());

        ReportServiceImpl spy = spy(reportService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<ReportVO> result = spy.pageReports(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("指定客户", result.getRecords().get(0).getCustomerName());
    }

    /**
     * 测试按状态筛选分页查询
     */
    @Test
    void testPageReportsByStatus() {
        ReportQueryDTO queryDTO = new ReportQueryDTO();
        queryDTO.setStatus(ReportStatusEnum.REVIEW.getValue());
        queryDTO.setCreateTimeEnd(LocalDate.of(2025, 12, 31));
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Report report = new Report();
        report.setId(1L);
        report.setReportNo("RPT-2025-001");
        report.setStatus(ReportStatusEnum.REVIEW.getValue());

        Page<Report> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(report));

        ReportServiceImpl spy = spy(reportService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<ReportVO> result = spy.pageReports(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("审核中", result.getRecords().get(0).getStatusName());
    }

    /**
     * 测试按创建时间范围筛选分页查询
     */
    @Test
    void testPageReportsByDateRange() {
        ReportQueryDTO queryDTO = new ReportQueryDTO();
        queryDTO.setCreateTimeStart(LocalDate.of(2025, 1, 1));
        queryDTO.setCreateTimeEnd(LocalDate.of(2025, 12, 31));
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Report report = new Report();
        report.setId(1L);
        report.setReportNo("RPT-2025-001");
        report.setStatus(ReportStatusEnum.DRAFT.getValue());

        Page<Report> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(report));

        ReportServiceImpl spy = spy(reportService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<ReportVO> result = spy.pageReports(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    // ==================== getReportDetail 测试 ====================

    /**
     * 测试正常获取报告详情
     */
    @Test
    void testGetReportDetail() {
        Report report = new Report();
        report.setId(1L);
        report.setReportNo("RPT-2025-001");
        report.setSampleId(100L);
        report.setCustomerId(50L);
        report.setStatus(ReportStatusEnum.DRAFT.getValue());
        report.setVersion(1);
        report.setReviewerId(10L);
        report.setApproverId(20L);

        Customer customer = new Customer();
        customer.setId(50L);
        customer.setName("测试客户");

        Sample sample = new Sample();
        sample.setId(100L);
        sample.setSampleNo("SPL-2025-001");

        SysUser reviewer = new SysUser();
        reviewer.setId(10L);
        reviewer.setNickname("审核员");

        SysUser approver = new SysUser();
        approver.setId(20L);
        approver.setNickname("批准人");

        doReturn(customer).when(customerMapper).selectById(50L);
        doReturn(sample).when(sampleMapper).selectById(100L);
        doReturn(reviewer).when(userMapper).selectById(10L);
        doReturn(approver).when(userMapper).selectById(20L);

       ReportServiceImpl spy = spy(reportService);
        doReturn(report).when(spy).getById(1L);

        ReportVO vo = spy.getReportDetail(1L);

        assertNotNull(vo);
        assertEquals("RPT-2025-001", vo.getReportNo());
        assertEquals("测试客户", vo.getCustomerName());
        assertEquals("SPL-2025-001", vo.getSampleNo());
        assertEquals("草稿", vo.getStatusName());
        assertEquals("审核员", vo.getReviewerName());
        assertEquals("批准人", vo.getApproverName());
    }

    /**
     * 测试获取不存在的报告
     */
    @Test
    void testGetReportDetailNotFound() {
       ReportServiceImpl spy = spy(reportService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.getReportDetail(999L));
        assertEquals("报告不存在", exception.getMessage());
    }

    /**
     * 测试获取关联数据为 null 的报告详情
     */
    @Test
    void testGetReportDetailWithNullRelations() {
        Report report = new Report();
        report.setId(1L);
        report.setReportNo("RPT-2025-001");
        report.setSampleId(null);
        report.setCustomerId(null);
        report.setStatus(ReportStatusEnum.DRAFT.getValue());
        report.setVersion(1);
        report.setReviewerId(null);
        report.setApproverId(null);

       ReportServiceImpl spy = spy(reportService);
        doReturn(report).when(spy).getById(1L);

        ReportVO vo = spy.getReportDetail(1L);

        assertNotNull(vo);
        assertEquals("RPT-2025-001", vo.getReportNo());
        assertNull(vo.getCustomerName());
        assertNull(vo.getSampleNo());
        assertNull(vo.getReviewerName());
        assertNull(vo.getApproverName());
    }

    // ==================== createReport 测试 ====================

    /**
     * 测试正常新建报告
     */
    @Test
    void testCreateReport() {
        ReportDTO dto = new ReportDTO();
        dto.setSampleId(100L);
        dto.setCustomerId(50L);
        dto.setVersion(1);
        dto.setFileUrl("/reports/001.pdf");
        dto.setRemark("新报告");

        doReturn("RPT-2025-001").when(numberingRuleEngine).generateNumber("REPORT_DEFAULT");
        doReturn(1).when(auditLogMapper).insert(any(ReportAuditLog.class));

       ReportServiceImpl spy = spy(reportService);
        doReturn(true).when(spy).save(any(Report.class));

        assertDoesNotThrow(() -> spy.createReport(dto));
        verify(spy).save(any(Report.class));
        verify(auditLogMapper).insert(any(ReportAuditLog.class));
    }

    /**
     * 测试新建报告 - 版本号自动设置为1
     */
    @Test
    void testCreateReportVersionAutoSet() {
        ReportDTO dto = new ReportDTO();
        dto.setSampleId(100L);
        dto.setCustomerId(50L);

        doReturn("RPT-2025-001").when(numberingRuleEngine).generateNumber("REPORT_DEFAULT");
        doReturn(1).when(auditLogMapper).insert(any(ReportAuditLog.class));

       ReportServiceImpl spy = spy(reportService);
        doReturn(true).when(spy).save(argThat(report ->
                1 == report.getVersion()
                && ReportStatusEnum.DRAFT.getValue().equals(report.getStatus())
        ));

        assertDoesNotThrow(() -> spy.createReport(dto));
        verify(spy).save(argThat(report ->
                1 == report.getVersion()
                && ReportStatusEnum.DRAFT.getValue().equals(report.getStatus())
        ));
    }

    // ==================== updateReport 测试 ====================

    /**
     * 测试正常更新报告（草稿状态）
     */
    @Test
    void testUpdateReport() {
        ReportDTO dto = new ReportDTO();
        dto.setId(1L);
        dto.setSampleId(100L);
        dto.setCustomerId(50L);
        dto.setFileUrl("/reports/001-v2.pdf");

        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setReportNo("RPT-2025-001");
        existingReport.setStatus(ReportStatusEnum.DRAFT.getValue());
        existingReport.setVersion(1);

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Report.class));

        assertDoesNotThrow(() -> spy.updateReport(dto));
        verify(spy).updateById(any(Report.class));
    }

    /**
     * 测试正常更新报告（打回状态）
     */
    @Test
    void testUpdateReportRejected() {
        ReportDTO dto = new ReportDTO();
        dto.setId(1L);
        dto.setSampleId(100L);
        dto.setCustomerId(50L);
        dto.setVersion(2);

        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setReportNo("RPT-2025-001");
        existingReport.setStatus(ReportStatusEnum.REJECTED.getValue());
        existingReport.setVersion(1);

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Report.class));

        assertDoesNotThrow(() -> spy.updateReport(dto));
        verify(spy).updateById(any(Report.class));
    }

    /**
     * 测试更新不存在的报告
     */
    @Test
    void testUpdateReportNotFound() {
        ReportDTO dto = new ReportDTO();
        dto.setId(999L);
        dto.setSampleId(100L);
        dto.setCustomerId(50L);

       ReportServiceImpl spy = spy(reportService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.updateReport(dto));
        assertEquals("报告不存在", exception.getMessage());
        verify(spy, never()).updateById(any(Report.class));
    }

    /**
     * 测试非草稿/打回状态不能编辑
     */
    @Test
    void testUpdateReportWrongStatus() {
        ReportDTO dto = new ReportDTO();
        dto.setId(1L);
        dto.setSampleId(100L);
        dto.setCustomerId(50L);

        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.APPROVED.getValue());
        existingReport.setVersion(1);

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.updateReport(dto));
        assertEquals("只有草稿或打回状态的报告可以编辑", exception.getMessage());
        assertEquals(ReportStatusEnum.APPROVED.getValue(), existingReport.getStatus());
        verify(spy, never()).updateById(any(Report.class));
    }

    /**
     * 测试已签发状态不能编辑
     */
    @Test
    void testUpdateReportIssued() {
        ReportDTO dto = new ReportDTO();
        dto.setId(1L);
        dto.setSampleId(100L);
        dto.setCustomerId(50L);

        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.ISSUED.getValue());
        existingReport.setVersion(1);

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.updateReport(dto));
        assertEquals("只有草稿或打回状态的报告可以编辑", exception.getMessage());
    }

    // ==================== submitForReview 测试 ====================

    /**
     * 测试正常提交审核（草稿状态）
     */
    @Test
    void testSubmitForReview() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.DRAFT.getValue());
        existingReport.setVersion(1);

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Report.class));
        doReturn(1).when(auditLogMapper).insert(any(ReportAuditLog.class));

        assertDoesNotThrow(() -> spy.submitForReview(1L, "请审核"));
        assertEquals(ReportStatusEnum.REVIEW.getValue(), existingReport.getStatus());
        verify(spy).updateById(any(Report.class));
    }

    /**
     * 测试提交审核（打回状态重新提交，版本号+1）
     */
    @Test
    void testSubmitForReviewRejected() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.REJECTED.getValue());
        existingReport.setVersion(2);

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Report.class));
        doReturn(1).when(auditLogMapper).insert(any(ReportAuditLog.class));

        assertDoesNotThrow(() -> spy.submitForReview(1L, "已修改"));
        assertEquals(ReportStatusEnum.REVIEW.getValue(), existingReport.getStatus());
        assertEquals(3, existingReport.getVersion());
        verify(spy).updateById(any(Report.class));
    }

    /**
     * 测试提交审核不存在的报告
     */
    @Test
    void testSubmitForReviewNotFound() {
       ReportServiceImpl spy = spy(reportService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.submitForReview(999L, null));
        assertEquals("报告不存在", exception.getMessage());
    }

    /**
     * 测试非草稿/打回状态不能提交审核
     */
    @Test
    void testSubmitForReviewWrongStatus() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.REVIEW.getValue());

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.submitForReview(1L, null));
        assertEquals("只有草稿或打回状态的报告可以提交审核", exception.getMessage());
        assertEquals(ReportStatusEnum.REVIEW.getValue(), existingReport.getStatus());
    }

    /**
     * 测试提交审核不带评论
     */
    @Test
    void testSubmitForReviewNoComment() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.DRAFT.getValue());
        existingReport.setVersion(1);

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Report.class));
        doReturn(1).when(auditLogMapper).insert(any(ReportAuditLog.class));

        assertDoesNotThrow(() -> spy.submitForReview(1L, null));
        verify(spy).updateById(any(Report.class));
        verify(auditLogMapper).insert(any(ReportAuditLog.class));
    }

    // ==================== approveReport 测试 ====================

    /**
     * 测试正常审核通过
     */
    @Test
    void testApproveReport() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.REVIEW.getValue());
        existingReport.setReviewerId(10L);
        existingReport.setApproverId(null);

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Report.class));
        doReturn(1).when(auditLogMapper).insert(any(ReportAuditLog.class));

        assertDoesNotThrow(() -> spy.approveReport(1L, "审核通过"));
        assertEquals(ReportStatusEnum.APPROVED.getValue(), existingReport.getStatus());
        assertEquals(Long.valueOf(1L), existingReport.getApproverId());
        verify(spy).updateById(any(Report.class));
    }

    /**
     * 测试审核通过不存在的报告
     */
    @Test
    void testApproveReportNotFound() {
       ReportServiceImpl spy = spy(reportService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.approveReport(999L, "通过"));
        assertEquals("报告不存在", exception.getMessage());
    }

    /**
     * 测试非审核中状态不能批准
     */
    @Test
    void testApproveReportWrongStatus() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.DRAFT.getValue());

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.approveReport(1L, "通过"));
        assertEquals("只有审核中状态的报告可以批准", exception.getMessage());
        assertEquals(ReportStatusEnum.DRAFT.getValue(), existingReport.getStatus());
    }

    /**
     * 测试审核通过不带评论
     */
    @Test
    void testApproveReportNoComment() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.REVIEW.getValue());

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Report.class));
        doReturn(1).when(auditLogMapper).insert(any(ReportAuditLog.class));

        assertDoesNotThrow(() -> spy.approveReport(1L, null));
        verify(spy).updateById(any(Report.class));
    }

    // ==================== rejectReport 测试 ====================

    /**
     * 测试正常审核打回
     */
    @Test
    void testRejectReport() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.REVIEW.getValue());
        existingReport.setReviewerId(null);
        existingReport.setApproverId(20L);

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Report.class));
        doReturn(1).when(auditLogMapper).insert(any(ReportAuditLog.class));

        assertDoesNotThrow(() -> spy.rejectReport(1L, "数据不完整"));
        assertEquals(ReportStatusEnum.REJECTED.getValue(), existingReport.getStatus());
        assertEquals(Long.valueOf(1L), existingReport.getReviewerId());
        verify(spy).updateById(any(Report.class));
    }

    /**
     * 测试审核打回不存在的报告
     */
    @Test
    void testRejectReportNotFound() {
       ReportServiceImpl spy = spy(reportService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.rejectReport(999L, "打回"));
        assertEquals("报告不存在", exception.getMessage());
    }

    /**
     * 测试非审核中状态不能打回
     */
    @Test
    void testRejectReportWrongStatus() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.DRAFT.getValue());

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.rejectReport(1L, "打回"));
        assertEquals("只有审核中状态的报告可以打回", exception.getMessage());
        assertEquals(ReportStatusEnum.DRAFT.getValue(), existingReport.getStatus());
    }

    // ==================== issueReport 测试 ====================

    /**
     * 测试正常签发报告
     */
    @Test
    void testIssueReport() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.APPROVED.getValue());
        existingReport.setReviewerId(10L);
        existingReport.setApproverId(20L);
        existingReport.setVersion(1);

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Report.class));
        doReturn(1).when(auditLogMapper).insert(any(ReportAuditLog.class));

        assertDoesNotThrow(() -> spy.issueReport(1L));
        assertEquals(ReportStatusEnum.ISSUED.getValue(), existingReport.getStatus());
        assertNotNull(existingReport.getIssuedDate());
        // 审核人ID已在approve时设置
        verify(spy).updateById(any(Report.class));
    }

    /**
     * 测试签发报告 - 无审核人ID时从批准人ID继承
     */
    @Test
    void testIssueReportInheritApproverAsReviewer() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.APPROVED.getValue());
        existingReport.setReviewerId(null);
        existingReport.setApproverId(20L);
        existingReport.setVersion(1);

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Report.class));
        doReturn(1).when(auditLogMapper).insert(any(ReportAuditLog.class));

        assertDoesNotThrow(() -> spy.issueReport(1L));
        assertEquals(ReportStatusEnum.ISSUED.getValue(), existingReport.getStatus());
        // reviewerId 应该从 approverId 继承
        assertEquals(Long.valueOf(20L), existingReport.getReviewerId());
    }

    /**
     * 测试签发不存在的报告
     */
    @Test
    void testIssueReportNotFound() {
       ReportServiceImpl spy = spy(reportService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.issueReport(999L));
        assertEquals("报告不存在", exception.getMessage());
    }

    /**
     * 测试非已批准状态不能签发
     */
    @Test
    void testIssueReportWrongStatus() {
        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setStatus(ReportStatusEnum.REVIEW.getValue());

       ReportServiceImpl spy = spy(reportService);
        doReturn(existingReport).when(spy).getById(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.issueReport(1L));
        assertEquals("只有已批准状态的报告可以签发", exception.getMessage());
        assertEquals(ReportStatusEnum.REVIEW.getValue(), existingReport.getStatus());
    }

    // ==================== getAuditLogs 测试 ====================

    /**
     * 测试正常获取审核日志
     */
    @Test
    void testGetAuditLogs() {
        Report report = new Report();
        report.setId(1L);
        report.setReportNo("RPT-2025-001");

        ReportAuditLog auditLog1 = new ReportAuditLog();
        auditLog1.setId(1L);
        auditLog1.setReportId(1L);
        auditLog1.setOperatorId(1L);
        auditLog1.setAction(ReportActionEnum.CREATE.getValue());
        auditLog1.setComment("新建报告");
        auditLog1.setAuditTime(LocalDateTime.of(2025, 6, 15, 10, 0));

        ReportAuditLog auditLog2 = new ReportAuditLog();
        auditLog2.setId(2L);
        auditLog2.setReportId(1L);
        auditLog2.setOperatorId(1L);
        auditLog2.setAction(ReportActionEnum.REVIEW.getValue());
        auditLog2.setComment("提交审核");
        auditLog2.setAuditTime(LocalDateTime.of(2025, 6, 15, 14, 30));

        SysUser user = new SysUser();
        user.setId(1L);
        user.setNickname("管理员");

       ReportServiceImpl spy = spy(reportService);
        doReturn(report).when(spy).getById(1L);
        doReturn(List.of(auditLog1, auditLog2)).when(auditLogMapper).selectList(any(LambdaQueryWrapper.class));
        doReturn(List.of(user)).when(userMapper).selectBatchIds(anyList());

        List<ReportAuditLogVO> result = spy.getAuditLogs(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("创建", result.get(0).getActionName());
        assertEquals("管理员", result.get(0).getOperatorName());
        assertEquals("审核", result.get(1).getActionName());
        verify(auditLogMapper).selectList(any(LambdaQueryWrapper.class));
    }

    /**
     * 测试空审核日志列表
     */
    @Test
    void testGetAuditLogsEmpty() {
        Report report = new Report();
        report.setId(1L);
        report.setReportNo("RPT-2025-001");

       ReportServiceImpl spy = spy(reportService);
        doReturn(report).when(spy).getById(1L);
        doReturn(Collections.emptyList()).when(auditLogMapper).selectList(any(LambdaQueryWrapper.class));

        List<ReportAuditLogVO> result = spy.getAuditLogs(1L);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * 测试获取不存在的报告的审核日志
     */
    @Test
    void testGetAuditLogsReportNotFound() {
       ReportServiceImpl spy = spy(reportService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.getAuditLogs(999L));
        assertEquals("报告不存在", exception.getMessage());
    }

    /**
     * 测试审核日志中操作人ID为 null
     */
    @Test
    void testGetAuditLogsNullOperator() {
        Report report = new Report();
        report.setId(1L);
        report.setReportNo("RPT-2025-001");

        ReportAuditLog auditLog = new ReportAuditLog();
        auditLog.setId(1L);
        auditLog.setReportId(1L);
        auditLog.setOperatorId(null);
        auditLog.setAction(ReportActionEnum.CREATE.getValue());
        auditLog.setComment("新建报告");

       ReportServiceImpl spy = spy(reportService);
        doReturn(report).when(spy).getById(1L);
        doReturn(List.of(auditLog)).when(auditLogMapper).selectList(any(LambdaQueryWrapper.class));

        List<ReportAuditLogVO> result = spy.getAuditLogs(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getOperatorName());
    }
}
