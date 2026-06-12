package com.emclims.module.statistics.service;

import com.emclims.module.statistics.dto.StatisticsOverviewDTO;

/**
 * 统计数据服务接口
 */
public interface StatisticsService {

    /**
     * 获取统计概览
     */
    StatisticsOverviewDTO getOverview();
}
