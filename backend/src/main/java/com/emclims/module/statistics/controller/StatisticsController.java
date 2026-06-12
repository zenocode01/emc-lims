package com.emclims.module.statistics.controller;

import com.emclims.common.response.R;
import com.emclims.module.sys.annotation.RequirePermission;
import com.emclims.module.statistics.dto.StatisticsOverviewDTO;
import com.emclims.module.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计数据 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "数据统计", description = "数据统计与仪表盘")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 获取统计概览
     */
    @Operation(summary = "获取统计概览")
    @GetMapping("/overview")
    @RequirePermission("statistics:overview")
    public R<StatisticsOverviewDTO> getOverview() {
        StatisticsOverviewDTO dto = statisticsService.getOverview();
        return R.ok(dto);
    }
}
