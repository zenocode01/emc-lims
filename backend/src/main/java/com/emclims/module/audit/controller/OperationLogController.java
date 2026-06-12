package com.emclims.module.audit.controller;

import com.emclims.common.response.R;
import com.emclims.common.response.PageResult;
import com.emclims.module.sys.annotation.RequirePermission;
import com.emclims.module.audit.dto.OperationLogQueryDTO;
import com.emclims.module.audit.service.OperationLogService;
import com.emclims.module.audit.vo.OperationLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 操作审计日志 Controller
 */
@Slf4j
@RestController
@RequestMapping("/audit/log")
@RequiredArgsConstructor
@Tag(name = "操作审计日志", description = "操作审计日志管理")
public class OperationLogController {

    private final OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     */
    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    @RequirePermission("audit:log:list")
    public R<PageResult<OperationLogVO>> page(OperationLogQueryDTO queryDTO) {
        return R.ok(operationLogService.pageLogs(queryDTO));
    }

    /**
     * 获取操作日志详情
     */
    @Operation(summary = "获取操作日志详情")
    @GetMapping("/{id}")
    @RequirePermission("audit:log:detail")
    public R<OperationLogVO> detail(@PathVariable Long id) {
        return R.ok(operationLogService.getLogDetail(id));
    }

    /**
     * 批量删除操作日志
     */
    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/{ids}")
    @RequirePermission("audit:log:delete")
    public R<Void> delete(@PathVariable Long[] ids) {
        operationLogService.deleteLogs(ids);
        return R.ok();
    }
}
