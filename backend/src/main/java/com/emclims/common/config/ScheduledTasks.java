package com.emclims.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.emclims.module.equipment.entity.Equipment;
import com.emclims.module.equipment.mapper.EquipmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 定时任务配置
 * 负责设备校准提醒等定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final EquipmentMapper equipmentMapper;

    /**
     * 设备校准提醒定时任务
     * 每天 9:00 检查即将到期和已逾期的设备校准
     * 校准到期前 30 天、15 天、7 天、1 天会发送提醒
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkEquipmentCalibration() {
        log.info("开始执行设备校准提醒检查...");

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);
        LocalDate thirtyDaysLater = today.plusDays(30);

        // 查询校准日期在 30 天内或已逾期的设备
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .le(Equipment::getCalibrationDue, thirtyDaysLater)  // 校准日期在 30 天内
                .or()
                .le(Equipment::getCalibrationDue, today)             // 已过期
        )
        .isNotNull(Equipment::getCalibrationDue)
        .eq(Equipment::getStatus, "normal");  // 只检查正常状态的设备

        List<Equipment> equipmentList = equipmentMapper.selectList(wrapper);

        if (equipmentList.isEmpty()) {
            log.info("没有需要提醒的设备校准");
            return;
        }

        for (Equipment equipment : equipmentList) {
            LocalDate dueDate = equipment.getCalibrationDue();
            long daysUntilDue = LocalDate.now().until(dueDate).getDays();
            String reminderLevel;
            
            if (daysUntilDue < 0) {
                reminderLevel = "已逾期";
            } else if (daysUntilDue <= 7) {
                reminderLevel = "紧急";
            } else if (daysUntilDue <= 15) {
                reminderLevel = "重要";
            } else {
                reminderLevel = "一般";
            }

            log.info("设备校准提醒 - 设备编号：{}，名称：{}，校准到期日：{}，剩余天数：{}，提醒级别：{}",
                    equipment.getEquipmentNo(),
                    equipment.getName(),
                    dueDate,
                    daysUntilDue,
                    reminderLevel);

            // TODO: 集成消息推送（邮件/短信/站内信）
            // messageService.sendCalibrationReminder(equipment, dueDate, daysUntilDue, reminderLevel);
        }

        log.info("设备校准提醒检查完成，共检查 {} 台设备", equipmentList.size());
    }

    /**
     * 每日 8:00 生成设备校准报告摘要
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void generateDailyCalibrationSummary() {
        log.info("开始生成每日设备校准报告摘要...");

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // 查询今天到期的校准设备
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Equipment::getCalibrationDue, today)
               .eq(Equipment::getStatus, "normal");

        long count = equipmentMapper.selectCount(wrapper);
        log.info("今日设备校准到期数量：{} 台", count);

        // TODO: 发送邮件给设备管理员
        // if (count > 0) {
        //     emailService.sendCalibrationSummaryEmail(count, today);
        // }
    }
}
