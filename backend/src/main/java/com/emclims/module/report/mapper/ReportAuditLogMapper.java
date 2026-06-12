package com.emclims.module.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emclims.module.report.entity.ReportAuditLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报告审核日志 Mapper
 */
@Mapper
public interface ReportAuditLogMapper extends BaseMapper<ReportAuditLog> {
}
