package com.emclims.module.report.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.report.dto.ReportDTO;
import com.emclims.module.report.dto.ReportQueryDTO;
import com.emclims.module.report.entity.Report;
import com.emclims.module.report.vo.ReportAuditLogVO;
import com.emclims.module.report.vo.ReportExportVO;
import com.emclims.module.report.vo.ReportVO;

import java.util.List;

/**
 * 报告 Service
 */
public interface ReportService extends IService<Report> {

    /**
     * 分页查询报告列表
     */
    Page<ReportVO> pageReports(ReportQueryDTO queryDTO);

    /**
     * 根据ID获取报告详情
     */
    ReportVO getReportDetail(Long id);

    /**
     * 新建报告（草稿状态）
     */
    void createReport(ReportDTO dto);

    /**
     * 更新报告信息
     */
    void updateReport(ReportDTO dto);

    /**
     * 提交审核（草稿 → 审核中）
     */
    void submitForReview(Long reportId, String comment);

    /**
     * 审核通过（审核中 → 已批准）
     */
    void approveReport(Long reportId, String comment);

    /**
     * 审核打回（审核中 → 已打回）
     */
    void rejectReport(Long reportId, String comment);

    /**
     * 签发报告（已批准 → 已签发）
     */
    void issueReport(Long reportId);

    /**
     * 获取报告审核日志
     */
    List<ReportAuditLogVO> getAuditLogs(Long reportId);

    /**
     * 导出报告列表
     */
    List<ReportExportVO> exportReports(ReportQueryDTO queryDTO);

    /**
     * 删除报告
     */
    void deleteReport(Long id);
}
