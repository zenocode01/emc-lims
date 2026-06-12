package com.emclims.module.report.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.common.numbering.NumberingRuleEngine;
import com.emclims.common.security.SecurityUtils;
import com.emclims.common.util.ConvertUtils;
import com.emclims.module.customer.entity.Customer;
import com.emclims.module.customer.mapper.CustomerMapper;
import com.emclims.module.report.dto.ReportDTO;
import com.emclims.module.report.dto.ReportQueryDTO;
import com.emclims.module.report.entity.Report;
import com.emclims.module.report.entity.ReportAuditLog;
import com.emclims.module.report.enums.ReportActionEnum;
import com.emclims.module.report.enums.ReportStatusEnum;
import com.emclims.module.report.mapper.ReportAuditLogMapper;
import com.emclims.module.report.mapper.ReportMapper;
import com.emclims.module.report.service.ReportService;
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.mapper.SampleMapper;
import com.emclims.module.report.vo.ReportAuditLogVO;
import com.emclims.module.report.vo.ReportExportVO;
import com.emclims.module.report.vo.ReportVO;
import com.emclims.module.sys.entity.SysUser;
import com.emclims.module.sys.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报告 Service 实现
 */
@Slf4j
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    private final ReportAuditLogMapper auditLogMapper;
    private final CustomerMapper customerMapper;
    private final SysUserMapper userMapper;
    private final SampleMapper sampleMapper;
    private final NumberingRuleEngine numberingRuleEngine;

    public ReportServiceImpl(ReportAuditLogMapper auditLogMapper,
                             CustomerMapper customerMapper,
                             SysUserMapper userMapper,
                             SampleMapper sampleMapper,
                             NumberingRuleEngine numberingRuleEngine) {
        this.auditLogMapper = auditLogMapper;
        this.customerMapper = customerMapper;
        this.userMapper = userMapper;
        this.sampleMapper = sampleMapper;
        this.numberingRuleEngine = numberingRuleEngine;
    }

    @Override
    public Page<ReportVO> pageReports(ReportQueryDTO queryDTO) {
        log.debug("查询报告列表，关键字: {}, 客户ID: {}, 状态: {}",
                queryDTO.getKeyword(), queryDTO.getCustomerId(), queryDTO.getStatus());
        Page<Report> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()), Report::getReportNo, queryDTO.getKeyword())
                .eq(queryDTO.getCustomerId() != null, Report::getCustomerId, queryDTO.getCustomerId())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()), Report::getStatus, queryDTO.getStatus())
                .ge(queryDTO.getCreateTimeStart() != null, Report::getCreateTime, queryDTO.getCreateTimeStart())
                .le(queryDTO.getCreateTimeEnd() != null, Report::getCreateTime, queryDTO.getCreateTimeEnd().plusDays(1))
                .orderByDesc(Report::getCreateTime);

        Page<Report> reportPage = this.page(page, wrapper);

        // 批量查询客户、样品、用户，避免 N+1 问题
        List<Long> customerIds = reportPage.getRecords().stream()
                .map(Report::getCustomerId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        List<Long> sampleIds = reportPage.getRecords().stream()
                .map(Report::getSampleId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        // 收集所有可能的用户ID（审核人、批准人）
        List<Long> userIds = reportPage.getRecords().stream()
                .flatMap(r -> {
                    List<Long> ids = new java.util.ArrayList<>();
                    if (r.getReviewerId() != null) ids.add(r.getReviewerId());
                    if (r.getApproverId() != null) ids.add(r.getApproverId());
                    return ids.stream();
                })
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Customer> customerMap = customerIds.isEmpty()
                ? Collections.emptyMap()
                : customerMapper.selectBatchIds(customerIds).stream()
                .collect(Collectors.toMap(Customer::getId, c -> c));
        Map<Long, Sample> sampleMap = sampleIds.isEmpty()
                ? Collections.emptyMap()
                : sampleMapper.selectBatchIds(sampleIds).stream()
                .collect(Collectors.toMap(Sample::getId, s -> s));
        Map<Long, SysUser> userMap = userIds.isEmpty()
                ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        Page<ReportVO> result = ConvertUtils.toPage(reportPage, r -> convertToVO(r, customerMap, sampleMap, userMap));
        return result;
    }

    @Override
    public ReportVO getReportDetail(Long id) {
        log.debug("获取报告详情，报告ID: {}", id);
        Report report = this.getById(id);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        // 详情查询需要获取所有关联数据
        Customer customer = report.getCustomerId() != null
                ? customerMapper.selectById(report.getCustomerId()) : null;
        Sample sample = report.getSampleId() != null
                ? sampleMapper.selectById(report.getSampleId()) : null;
        SysUser reviewer = report.getReviewerId() != null
                ? userMapper.selectById(report.getReviewerId()) : null;
        SysUser approver = report.getApproverId() != null
                ? userMapper.selectById(report.getApproverId()) : null;

        return convertToVO(report,
                customer != null ? Collections.singletonMap(customer.getId(), customer) : Collections.emptyMap(),
                sample != null ? Collections.singletonMap(sample.getId(), sample) : Collections.emptyMap(),
                buildUserMap(reviewer, approver));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReport(ReportDTO dto) {
        log.info("新建报告，样品ID: {}, 客户ID: {}", dto.getSampleId(), dto.getCustomerId());

        // 生成报告编号
        String reportNo = numberingRuleEngine.generateNumber("REPORT_DEFAULT");

        Report report = new Report();
        BeanUtils.copyProperties(dto, report);
        report.setReportNo(reportNo);
        report.setStatus(ReportStatusEnum.DRAFT.getValue());
        report.setVersion(1);
        this.save(report);

        // 记录创建日志
        createAuditLog(report.getId(), ReportActionEnum.CREATE.getValue(), "新建报告");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReport(ReportDTO dto) {
        log.info("更新报告信息，报告ID: {}", dto.getId());
        Report report = this.getById(dto.getId());
        if (report == null) {
            throw new BusinessException("报告不存在");
        }

        // 只有草稿或打回状态才能编辑
        if (!ReportStatusEnum.DRAFT.getValue().equals(report.getStatus())
                && !ReportStatusEnum.REJECTED.getValue().equals(report.getStatus())) {
            throw new BusinessException("只有草稿或打回状态的报告可以编辑");
        }

        BeanUtils.copyProperties(dto, report);
        this.updateById(report);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitForReview(Long reportId, String comment) {
        log.info("提交审核，报告ID: {}", reportId);
        Report report = this.getById(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }

        // 只有草稿或打回状态才能提交审核
        if (!ReportStatusEnum.DRAFT.getValue().equals(report.getStatus())
                && !ReportStatusEnum.REJECTED.getValue().equals(report.getStatus())) {
            throw new BusinessException("只有草稿或打回状态的报告可以提交审核");
        }

        // 如果从打回状态重新提交，版本号+1
        if (ReportStatusEnum.REJECTED.getValue().equals(report.getStatus())) {
            report.setVersion(report.getVersion() + 1);
        }

        report.setStatus(ReportStatusEnum.REVIEW.getValue());
        this.updateById(report);

        createAuditLog(reportId, ReportActionEnum.REVIEW.getValue(),
                StrUtil.isNotBlank(comment) ? comment : "提交审核");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveReport(Long reportId, String comment) {
        log.info("审核通过，报告ID: {}", reportId);
        Report report = this.getById(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }

        if (!ReportStatusEnum.REVIEW.getValue().equals(report.getStatus())) {
            throw new BusinessException("只有审核中状态的报告可以批准");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        report.setStatus(ReportStatusEnum.APPROVED.getValue());
        report.setApproverId(currentUserId);
        this.updateById(report);

        createAuditLog(reportId, ReportActionEnum.APPROVE.getValue(),
                StrUtil.isNotBlank(comment) ? comment : "审核通过");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectReport(Long reportId, String comment) {
        log.info("审核打回，报告ID: {}", reportId);
        Report report = this.getById(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }

        if (!ReportStatusEnum.REVIEW.getValue().equals(report.getStatus())) {
            throw new BusinessException("只有审核中状态的报告可以打回");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        report.setStatus(ReportStatusEnum.REJECTED.getValue());
        report.setReviewerId(currentUserId);
        this.updateById(report);

        createAuditLog(reportId, ReportActionEnum.REJECT.getValue(),
                StrUtil.isNotBlank(comment) ? comment : "审核不通过，打回修改");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void issueReport(Long reportId) {
        log.info("签发报告，报告ID: {}", reportId);
        Report report = this.getById(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }

        if (!ReportStatusEnum.APPROVED.getValue().equals(report.getStatus())) {
            throw new BusinessException("只有已批准状态的报告可以签发");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        report.setStatus(ReportStatusEnum.ISSUED.getValue());
        report.setIssuedDate(LocalDate.now());
        // 审核人ID已在approve时设置，这里不再覆盖
        if (report.getReviewerId() == null && report.getApproverId() != null) {
            report.setReviewerId(report.getApproverId());
        }
        this.updateById(report);

        createAuditLog(reportId, ReportActionEnum.REVIEW.getValue(), "报告签发");
    }

    @Override
    public List<ReportAuditLogVO> getAuditLogs(Long reportId) {
        log.debug("获取报告审核日志，报告ID: {}", reportId);

        // 验证报告是否存在
        Report report = this.getById(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }

        LambdaQueryWrapper<ReportAuditLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportAuditLog::getReportId, reportId)
                .orderByDesc(ReportAuditLog::getAuditTime);

        List<ReportAuditLog> logs = auditLogMapper.selectList(wrapper);

        // 批量查询操作人，避免 N+1
        List<Long> operatorIds = logs.stream()
                .map(ReportAuditLog::getOperatorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, SysUser> userMap = operatorIds.isEmpty()
                ? Collections.emptyMap()
                : userMapper.selectBatchIds(operatorIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        return logs.stream().map(log -> {
            ReportAuditLogVO vo = new ReportAuditLogVO();
            BeanUtils.copyProperties(log, vo);
            vo.setActionName(ReportActionEnum.fromValue(log.getAction()).getLabel());

            if (log.getOperatorId() != null && userMap.containsKey(log.getOperatorId())) {
                vo.setOperatorName(userMap.get(log.getOperatorId()).getNickname());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 创建审核日志
     */
    private void createAuditLog(Long reportId, String action, String comment) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        ReportAuditLog auditLog = new ReportAuditLog();
        auditLog.setReportId(reportId);
        auditLog.setOperatorId(currentUserId);
        auditLog.setAction(action);
        auditLog.setComment(comment);
        auditLog.setAuditTime(LocalDateTime.now());
        auditLogMapper.insert(auditLog);

        log.info("记录审核日志，报告ID: {}, 操作: {}, 操作人: {}", reportId, action, currentUserId);
    }

    /**
     * 构建用户 Map（仅包含非 null 用户）
     */
    private Map<Long, SysUser> buildUserMap(SysUser... users) {
        return java.util.Arrays.stream(users)
                .filter(u -> u != null)
                .collect(Collectors.toMap(SysUser::getId, u -> u));
    }

    /**
     * 转换为 VO（批量查询版）
     */
    private ReportVO convertToVO(Report report,
                                  Map<Long, Customer> customerMap,
                                  Map<Long, Sample> sampleMap,
                                  Map<Long, SysUser> userMap) {
        ReportVO vo = new ReportVO();
        BeanUtils.copyProperties(report, vo);
        vo.setStatusName(ReportStatusEnum.fromValue(report.getStatus()).getLabel());

        if (report.getCustomerId() != null && customerMap != null) {
            Customer customer = customerMap.get(report.getCustomerId());
            if (customer != null) {
                vo.setCustomerName(customer.getName());
            }
        }
        if (report.getSampleId() != null && sampleMap != null) {
            Sample sample = sampleMap.get(report.getSampleId());
            if (sample != null) {
                vo.setSampleNo(sample.getSampleNo());
            }
        }
        if (report.getReviewerId() != null && userMap != null) {
            SysUser reviewer = userMap.get(report.getReviewerId());
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getNickname());
            }
        }
        if (report.getApproverId() != null && userMap != null) {
            SysUser approver = userMap.get(report.getApproverId());
            if (approver != null) {
                vo.setApproverName(approver.getNickname());
            }
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReport(Long id) {
        log.info("删除报告，报告ID: {}", id);

        Report report = this.getById(id);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }

        this.removeById(id);
        log.info("删除报告成功，报告ID: {}", id);
    }

    @Override
    public List<ReportExportVO> exportReports(ReportQueryDTO queryDTO) {
        log.debug("导出报告列表，关键字: {}, 客户ID: {}, 状态: {}",
                queryDTO.getKeyword(), queryDTO.getCustomerId(), queryDTO.getStatus());

        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(queryDTO.getKeyword()), Report::getReportNo, queryDTO.getKeyword())
                .eq(queryDTO.getCustomerId() != null, Report::getCustomerId, queryDTO.getCustomerId())
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()), Report::getStatus, queryDTO.getStatus())
                .ge(queryDTO.getCreateTimeStart() != null, Report::getCreateTime, queryDTO.getCreateTimeStart())
                .le(queryDTO.getCreateTimeEnd() != null, Report::getCreateTime, queryDTO.getCreateTimeEnd().plusDays(1))
                .orderByDesc(Report::getCreateTime);

        List<Report> reports = this.list(wrapper);

        // 批量查询关联数据
        List<Long> sampleIds = reports.stream()
                .map(Report::getSampleId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        List<Long> customerIds = reports.stream()
                .map(Report::getCustomerId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        List<Long> userIds = reports.stream()
                .flatMap(r -> {
                    List<Long> ids = new java.util.ArrayList<>();
                    if (r.getReviewerId() != null) ids.add(r.getReviewerId());
                    if (r.getApproverId() != null) ids.add(r.getApproverId());
                    return ids.stream();
                })
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Sample> sampleMap = sampleIds.isEmpty()
                ? Collections.emptyMap()
                : sampleMapper.selectBatchIds(sampleIds).stream()
                .collect(Collectors.toMap(Sample::getId, s -> s));
        Map<Long, Customer> customerMap = customerIds.isEmpty()
                ? Collections.emptyMap()
                : customerMapper.selectBatchIds(customerIds).stream()
                .collect(Collectors.toMap(Customer::getId, c -> c));
        Map<Long, SysUser> userMap = userIds.isEmpty()
                ? Collections.emptyMap()
                : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        return ConvertUtils.toList(reports, r -> convertToExportVO(r, sampleMap, customerMap, userMap));
    }

    /**
     * 转换为导出 VO（批量查询版）
     */
    private ReportExportVO convertToExportVO(Report report,
                                             Map<Long, Sample> sampleMap,
                                             Map<Long, Customer> customerMap,
                                             Map<Long, SysUser> userMap) {
        ReportExportVO vo = new ReportExportVO();
        vo.setReportNo(report.getReportNo());
        vo.setVersion(report.getVersion());
        vo.setIssuedDate(report.getIssuedDate());
        vo.setCreateTime(report.getCreateTime());
        vo.setStatusName(ReportStatusEnum.fromValue(report.getStatus()).getLabel());

        if (report.getSampleId() != null && sampleMap != null) {
            Sample sample = sampleMap.get(report.getSampleId());
            if (sample != null) {
                vo.setSampleNo(sample.getSampleNo());
            }
        }
        if (report.getCustomerId() != null && customerMap != null) {
            Customer customer = customerMap.get(report.getCustomerId());
            if (customer != null) {
                vo.setCustomerName(customer.getName());
            }
        }
        if (report.getReviewerId() != null && userMap != null) {
            SysUser reviewer = userMap.get(report.getReviewerId());
            if (reviewer != null) {
                vo.setReviewerName(reviewer.getNickname());
            }
        }
        if (report.getApproverId() != null && userMap != null) {
            SysUser approver = userMap.get(report.getApproverId());
            if (approver != null) {
                vo.setApproverName(approver.getNickname());
            }
        }
        return vo;
    }
}
