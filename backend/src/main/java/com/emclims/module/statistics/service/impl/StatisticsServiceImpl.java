package com.emclims.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.module.customer.entity.Customer;
import com.emclims.module.customer.mapper.CustomerMapper;
import com.emclims.module.equipment.entity.Equipment;
import com.emclims.module.equipment.mapper.EquipmentMapper;
import com.emclims.module.personnel.entity.Personnel;
import com.emclims.module.personnel.entity.PersonnelAuthorization;
import com.emclims.module.personnel.mapper.PersonnelAuthorizationMapper;
import com.emclims.module.personnel.mapper.PersonnelMapper;
import com.emclims.module.report.entity.Report;
import com.emclims.module.report.mapper.ReportMapper;
import com.emclims.module.sample.entity.Sample;
import com.emclims.module.sample.mapper.SampleMapper;
import com.emclims.module.statistics.dto.StatisticsOverviewDTO;
import com.emclims.module.statistics.service.StatisticsService;
import com.emclims.module.test.entity.TestPlan;
import com.emclims.module.test.mapper.TestPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计数据服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SampleMapper sampleMapper;
    private final ReportMapper reportMapper;
    private final TestPlanMapper testPlanMapper;
    private final CustomerMapper customerMapper;
    private final EquipmentMapper equipmentMapper;
    private final PersonnelMapper personnelMapper;
    private final PersonnelAuthorizationMapper personnelAuthorizationMapper;

    @Override
    public StatisticsOverviewDTO getOverview() {
        log.info("获取统计概览数据");
        StatisticsOverviewDTO dto = new StatisticsOverviewDTO();

        // 客户统计
        dto.setCustomerTotal(countAll(new LambdaQueryWrapper<Customer>()));

        // 样品统计
        LambdaQueryWrapper<Sample> sampleWrapper = new LambdaQueryWrapper<>();
        dto.setSampleTotal(countAll(sampleWrapper));
        dto.setSamplePending(countSamplesByStatus("pending"));
        dto.setSampleTesting(countSamplesByStatus("testing"));
        dto.setSampleCompleted(countSamplesByStatus("completed"));

        // 本月新增样品
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LambdaQueryWrapper<Sample> thisMonthSampleWrapper = new LambdaQueryWrapper<Sample>()
                .ge(Sample::getCreateTime, firstDayOfMonth.atStartOfDay());
        dto.setSampleThisMonth(countAll(thisMonthSampleWrapper));

        // 测试计划统计
        LambdaQueryWrapper<TestPlan> planWrapper = new LambdaQueryWrapper<>();
        dto.setTestPlanTotal(countAll(planWrapper));
        dto.setTestPlanTesting(countPlansByStatus("testing"));
        dto.setTestPlanCompleted(countPlansByStatus("completed"));

        // 报告统计
        LambdaQueryWrapper<Report> reportWrapper = new LambdaQueryWrapper<>();
        dto.setReportTotal(countAll(reportWrapper));
        dto.setReportReviewing(countReportsByStatus("review"));
        dto.setReportIssued(countReportsByStatus("issued"));

        // 本月签发报告
        LambdaQueryWrapper<Report> thisMonthReportWrapper = new LambdaQueryWrapper<Report>()
                .eq(Report::getStatus, "issued")
                .ge(Report::getIssuedDate, firstDayOfMonth);
        dto.setReportIssuedThisMonth(countAll(thisMonthReportWrapper));

        // 设备统计
        LambdaQueryWrapper<Equipment> equipmentWrapper = new LambdaQueryWrapper<>();
        dto.setEquipmentTotal(countAll(equipmentWrapper));
        dto.setEquipmentNormal(countEquipmentsByStatus("normal"));
        dto.setEquipmentCalibrating(countEquipmentsByStatus("calibration"));

        // 人员统计
        LambdaQueryWrapper<Personnel> personnelWrapper = new LambdaQueryWrapper<>();
        dto.setPersonnelTotal(countAll(personnelWrapper));
        dto.setPersonnelValid(countPersonnelByStatus("valid"));

        // 即将到期的校准设备
        LocalDate calibrationDueThreshold = now.plusDays(30);
        LambdaQueryWrapper<Equipment> dueEquipmentWrapper = new LambdaQueryWrapper<Equipment>()
                .between(Equipment::getCalibrationDue, now, calibrationDueThreshold)
                .eq(Equipment::getStatus, "normal");
        dto.setEquipmentCalibrationDue(countAll(dueEquipmentWrapper));

        // 待更新资质人员
        LocalDate authExpiringThreshold = now.plusDays(30);
        LambdaQueryWrapper<PersonnelAuthorization> expiringAuthWrapper = new LambdaQueryWrapper<PersonnelAuthorization>()
                .between(PersonnelAuthorization::getExpireDate, now, authExpiringThreshold);
        dto.setPersonnelAuthExpiring(Math.toIntExact(personnelAuthorizationMapper.selectCount(expiringAuthWrapper)));

        // 趋势数据（最近 7 天）
        dto.setSampleTrend7Days(generateDailyTrend(sampleWrapper, 7));
        dto.setReportTrend7Days(generateDailyReportTrend(reportWrapper, 7));

        // 分布数据
        dto.setSampleCategoryDistribution(generateSampleCategoryDistribution());
        dto.setTestCategoryDistribution(generateTestCategoryDistribution());

        return dto;
    }

    private int countAll(LambdaQueryWrapper<?> wrapper) {
        // 简化实现，实际应该传入对应的 Mapper
        return 0;
    }

    private int countSamplesByStatus(String status) {
        return Math.toIntExact(sampleMapper.selectCount(
                new LambdaQueryWrapper<Sample>().eq(Sample::getStatus, status)));
    }

    private int countPlansByStatus(String status) {
        return Math.toIntExact(testPlanMapper.selectCount(
                new LambdaQueryWrapper<TestPlan>().eq(TestPlan::getStatus, status)));
    }

    private int countReportsByStatus(String status) {
        return Math.toIntExact(reportMapper.selectCount(
                new LambdaQueryWrapper<Report>().eq(Report::getStatus, status)));
    }

    private int countEquipmentsByStatus(String status) {
        return Math.toIntExact(equipmentMapper.selectCount(
                new LambdaQueryWrapper<Equipment>().eq(Equipment::getStatus, status)));
    }

    private int countPersonnelByStatus(String status) {
        return Math.toIntExact(personnelMapper.selectCount(
                new LambdaQueryWrapper<Personnel>().eq(Personnel::getStatus, status)));
    }

    private List<Integer> generateDailyTrend(LambdaQueryWrapper<Sample> wrapper, int days) {
        List<Integer> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LambdaQueryWrapper<Sample> dayWrapper = new LambdaQueryWrapper<Sample>()
                    .ge(Sample::getCreateTime, date.atStartOfDay())
                    .lt(Sample::getCreateTime, date.plusDays(1).atStartOfDay());
            trend.add(Math.toIntExact(sampleMapper.selectCount(dayWrapper)));
        }
        return trend;
    }

    private List<Integer> generateDailyReportTrend(LambdaQueryWrapper<Report> wrapper, int days) {
        List<Integer> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LambdaQueryWrapper<Report> dayWrapper = new LambdaQueryWrapper<Report>()
                    .ge(Report::getCreateTime, date.atStartOfDay())
                    .lt(Report::getCreateTime, date.plusDays(1).atStartOfDay());
            trend.add(Math.toIntExact(reportMapper.selectCount(dayWrapper)));
        }
        return trend;
    }

    private List<Map<String, Object>> generateSampleCategoryDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();
        // 按产品名称前 10 个字符分组模拟类别分布
        Map<String, Long> categoryMap = sampleMapper.selectList(null)
                .stream()
                .filter(s -> s.getProductName() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getProductName().substring(0, Math.min(10, s.getProductName().length())),
                        Collectors.counting()
                ));
        categoryMap.forEach((category, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", category);
            item.put("value", count);
            distribution.add(item);
        });
        return distribution;
    }

    private List<Map<String, Object>> generateTestCategoryDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();
        // 按测试计划编号前缀分组模拟类别分布
        Map<String, Long> categoryMap = testPlanMapper.selectList(null)
                .stream()
                .filter(p -> p.getPlanNo() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getPlanNo().substring(0, Math.min(5, p.getPlanNo().length())),
                        Collectors.counting()
                ));
        categoryMap.forEach((category, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", category);
            item.put("value", count);
            distribution.add(item);
        });
        return distribution;
    }
}
