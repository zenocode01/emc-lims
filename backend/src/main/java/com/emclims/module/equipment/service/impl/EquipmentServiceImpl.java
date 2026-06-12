package com.emclims.module.equipment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.emclims.common.exception.BusinessException;
import com.emclims.common.security.SecurityUtils;
import com.emclims.module.equipment.dto.CalibrationDTO;
import com.emclims.module.equipment.dto.CalibrationQueryDTO;
import com.emclims.module.equipment.dto.EquipmentDTO;
import com.emclims.module.equipment.dto.EquipmentQueryDTO;
import com.emclims.module.equipment.dto.UsageDTO;
import com.emclims.module.equipment.dto.UsageQueryDTO;
import com.emclims.module.equipment.entity.Equipment;
import com.emclims.module.equipment.entity.EquipmentCalibration;
import com.emclims.module.equipment.entity.EquipmentUsage;
import com.emclims.module.equipment.mapper.EquipmentCalibrationMapper;
import com.emclims.module.equipment.mapper.EquipmentMapper;
import com.emclims.module.equipment.mapper.EquipmentUsageMapper;
import com.emclims.module.equipment.service.EquipmentService;
import com.emclims.module.equipment.vo.CalibrationVO;
import com.emclims.module.equipment.vo.EquipmentExportVO;
import com.emclims.module.equipment.vo.EquipmentVO;
import com.emclims.module.equipment.vo.UsageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 设备 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String EQUIPMENT_NO_PREFIX = "EQ";

    private final EquipmentCalibrationMapper calibrationMapper;
    private final EquipmentUsageMapper usageMapper;

    /**
     * 设备状态名称映射
     */
    private static final java.util.Map<String, String> STATUS_NAME_MAP = java.util.Map.of(
            "normal", "正常",
            "maintenance", "维护中",
            "calibration", "校准中",
            "scrap", "报废"
    );

    /**
     * 使用记录状态名称映射
     */
    private static final java.util.Map<String, String> USAGE_STATUS_NAME_MAP = java.util.Map.of(
            "in_use", "使用中",
            "completed", "已完成"
    );

    @Override
    public Page<EquipmentVO> pageEquipment(EquipmentQueryDTO queryDTO) {
        LambdaQueryWrapper<Equipment> wrapper = buildQueryWrapper(queryDTO);

        Page<Equipment> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Equipment> resultPage = this.page(page, wrapper);

        // 转换为 VO
        List<EquipmentVO> voList = resultPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        Page<EquipmentVO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    private LambdaQueryWrapper<Equipment> buildQueryWrapper(EquipmentQueryDTO queryDTO) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();

        // 关键字搜索：设备编号/名称
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(Equipment::getEquipmentNo, queryDTO.getKeyword())
                    .or()
                    .like(Equipment::getName, queryDTO.getKeyword())
            );
        }

        // 状态筛选
        if (StrUtil.isNotBlank(queryDTO.getStatus())) {
            wrapper.eq(Equipment::getStatus, queryDTO.getStatus());
        }

        // 位置筛选
        if (StrUtil.isNotBlank(queryDTO.getLocation())) {
            wrapper.like(Equipment::getLocation, queryDTO.getLocation());
        }

        // 校准日期范围筛选
        if (queryDTO.getCalibrationDueStart() != null) {
            wrapper.ge(Equipment::getCalibrationDue, queryDTO.getCalibrationDueStart());
        }
        if (queryDTO.getCalibrationDueEnd() != null) {
            wrapper.le(Equipment::getCalibrationDue, queryDTO.getCalibrationDueEnd());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(Equipment::getCreateTime);

        return wrapper;
    }

    @Override
    public EquipmentVO getEquipmentDetail(Long id) {
        Equipment equipment = this.getById(id);
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        return convertToVO(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEquipment(EquipmentDTO dto) {
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(dto, equipment);

        // 生成设备编号
        String equipmentNo = generateEquipmentNo();
        equipment.setEquipmentNo(equipmentNo);

        // 默认状态为正常
        if (StrUtil.isBlank(equipment.getStatus())) {
            equipment.setStatus("normal");
        }

        this.save(equipment);
        log.info("新增设备成功，设备ID: {}, 设备编号: {}", equipment.getId(), equipmentNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEquipment(EquipmentDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("设备ID不能为空");
        }

        Equipment equipment = this.getById(dto.getId());
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }

        BeanUtils.copyProperties(dto, equipment, "equipmentNo", "createTime");
        this.updateById(equipment);
        log.info("更新设备成功，设备ID: {}", equipment.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEquipment(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的设备");
        }
        this.removeByIds(ids);
        log.info("删除设备成功，设备ID列表: {}", ids);
    }

    @Override
    public Page<CalibrationVO> pageCalibration(CalibrationQueryDTO queryDTO) {
        LambdaQueryWrapper<EquipmentCalibration> wrapper = new LambdaQueryWrapper<>();

        // 设备ID筛选
        if (queryDTO.getEquipmentId() != null) {
            wrapper.eq(EquipmentCalibration::getEquipmentId, queryDTO.getEquipmentId());
        }

        // 校准日期范围筛选
        if (queryDTO.getCalibrationDateStart() != null) {
            wrapper.ge(EquipmentCalibration::getCalibrationDate, queryDTO.getCalibrationDateStart());
        }
        if (queryDTO.getCalibrationDateEnd() != null) {
            wrapper.le(EquipmentCalibration::getCalibrationDate, queryDTO.getCalibrationDateEnd());
        }

        // 校准结果筛选
        if (StrUtil.isNotBlank(queryDTO.getResult())) {
            wrapper.eq(EquipmentCalibration::getResult, queryDTO.getResult());
        }

        // 按校准日期倒序
        wrapper.orderByDesc(EquipmentCalibration::getCalibrationDate);

        Page<EquipmentCalibration> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<EquipmentCalibration> resultPage = calibrationMapper.selectPage(page, wrapper);

        // 批量查询设备信息，避免 N+1
        List<Long> equipmentIds = resultPage.getRecords().stream()
                .map(EquipmentCalibration::getEquipmentId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Equipment> equipmentMap = equipmentIds.isEmpty()
                ? Map.of()
                : this.listByIds(equipmentIds).stream()
                .collect(Collectors.toMap(Equipment::getId, Function.identity()));

        // 转换为 VO
        List<CalibrationVO> voList = resultPage.getRecords().stream()
                .map(calibration -> convertCalibrationToVO(calibration, equipmentMap))
                .collect(Collectors.toList());
        Page<CalibrationVO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateCalibration(CalibrationDTO dto) {
        EquipmentCalibration calibration = new EquipmentCalibration();
        BeanUtils.copyProperties(dto, calibration);

        if (calibration.getId() != null) {
            // 编辑
            EquipmentCalibration existing = calibrationMapper.selectById(calibration.getId());
            if (existing == null) {
                throw new BusinessException("校准记录不存在");
            }
            calibrationMapper.updateById(calibration);
            log.info("更新校准记录成功，记录ID: {}", calibration.getId());
        } else {
            // 新增
            calibrationMapper.insert(calibration);

            // 同步更新设备的校准信息
            Equipment equipment = this.getById(calibration.getEquipmentId());
            if (equipment != null) {
                equipment.setLastCalibration(calibration.getCalibrationDate());
                equipment.setCalibrationDue(calibration.getDueDate());
                this.updateById(equipment);
            }

            log.info("新增校准记录成功，记录ID: {}, 设备ID: {}", calibration.getId(), calibration.getEquipmentId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCalibration(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的校准记录");
        }
        calibrationMapper.deleteBatchIds(ids);
        log.info("删除校准记录成功，记录ID列表: {}", ids);
    }

    @Override
    public List<CalibrationVO> getCalibrationHistory(Long equipmentId) {
        LambdaQueryWrapper<EquipmentCalibration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EquipmentCalibration::getEquipmentId, equipmentId)
                .orderByDesc(EquipmentCalibration::getCalibrationDate);

        List<EquipmentCalibration> calibrationList = calibrationMapper.selectList(wrapper);
        // 单条查询场景，直接查询设备信息
        Map<Long, Equipment> equipmentMap = calibrationList.isEmpty()
                ? Map.of()
                : this.listByIds(calibrationList.stream().map(EquipmentCalibration::getEquipmentId).distinct().collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Equipment::getId, Function.identity()));
        return calibrationList.stream()
                .map(calibration -> convertCalibrationToVO(calibration, equipmentMap))
                .collect(Collectors.toList());
    }

    @Override
    public Page<UsageVO> pageUsage(UsageQueryDTO queryDTO) {
        LambdaQueryWrapper<EquipmentUsage> wrapper = new LambdaQueryWrapper<>();

        // 设备ID筛选
        if (queryDTO.getEquipmentId() != null) {
            wrapper.eq(EquipmentUsage::getEquipmentId, queryDTO.getEquipmentId());
        }

        // 使用人ID筛选
        if (queryDTO.getUserId() != null) {
            wrapper.eq(EquipmentUsage::getUserId, queryDTO.getUserId());
        }

        // 开始时间范围筛选
        if (queryDTO.getStartTimeStart() != null) {
            wrapper.ge(EquipmentUsage::getStartTime, queryDTO.getStartTimeStart());
        }
        if (queryDTO.getStartTimeEnd() != null) {
            wrapper.le(EquipmentUsage::getStartTime, queryDTO.getStartTimeEnd());
        }

        // 状态筛选
        if (StrUtil.isNotBlank(queryDTO.getStatus())) {
            wrapper.eq(EquipmentUsage::getStatus, queryDTO.getStatus());
        }

        // 按开始时间倒序
        wrapper.orderByDesc(EquipmentUsage::getStartTime);

        Page<EquipmentUsage> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<EquipmentUsage> resultPage = usageMapper.selectPage(page, wrapper);

        // 批量查询设备信息，避免 N+1
        List<Long> usageEquipmentIds = resultPage.getRecords().stream()
                .map(EquipmentUsage::getEquipmentId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Equipment> usageEquipmentMap = usageEquipmentIds.isEmpty()
                ? Map.of()
                : this.listByIds(usageEquipmentIds).stream()
                .collect(Collectors.toMap(Equipment::getId, Function.identity()));

        // 转换为 VO
        List<UsageVO> voList = resultPage.getRecords().stream()
                .map(usage -> convertUsageToVO(usage, usageEquipmentMap))
                .collect(Collectors.toList());
        Page<UsageVO> result = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUsage(UsageDTO dto) {
        EquipmentUsage usage = new EquipmentUsage();
        BeanUtils.copyProperties(dto, usage);

        // 默认状态为使用中
        if (StrUtil.isBlank(usage.getStatus())) {
            usage.setStatus("in_use");
        }

        usageMapper.insert(usage);
        log.info("新增使用记录成功，记录ID: {}, 设备ID: {}", usage.getId(), usage.getEquipmentId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUsage(UsageDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("使用记录ID不能为空");
        }

        EquipmentUsage existing = usageMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("使用记录不存在");
        }

        EquipmentUsage usage = new EquipmentUsage();
        BeanUtils.copyProperties(dto, usage);
        usageMapper.updateById(usage);
        log.info("更新使用记录成功，记录ID: {}", usage.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUsage(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的使用记录");
        }
        usageMapper.deleteBatchIds(ids);
        log.info("删除使用记录成功，记录ID列表: {}", ids);
    }

    /**
     * 将设备实体转换为 VO
     */
    private EquipmentVO convertToVO(Equipment equipment) {
        EquipmentVO vo = new EquipmentVO();
        BeanUtils.copyProperties(equipment, vo);
        vo.setStatusName(STATUS_NAME_MAP.getOrDefault(equipment.getStatus(), equipment.getStatus()));
        return vo;
    }

    /**
     * 将校准记录实体转换为 VO
     */
    private CalibrationVO convertCalibrationToVO(EquipmentCalibration calibration, Map<Long, Equipment> equipmentMap) {
        CalibrationVO vo = new CalibrationVO();
        BeanUtils.copyProperties(calibration, vo);

        // 从批量查询结果中获取设备信息
        if (equipmentMap != null && equipmentMap.containsKey(calibration.getEquipmentId())) {
            Equipment equipment = equipmentMap.get(calibration.getEquipmentId());
            vo.setEquipmentNo(equipment.getEquipmentNo());
            vo.setEquipmentName(equipment.getName());
        }

        return vo;
    }

    /**
     * 将使用记录实体转换为 VO
     */
    private UsageVO convertUsageToVO(EquipmentUsage usage, Map<Long, Equipment> equipmentMap) {
        UsageVO vo = new UsageVO();
        BeanUtils.copyProperties(usage, vo);
        vo.setStatusName(USAGE_STATUS_NAME_MAP.getOrDefault(usage.getStatus(), usage.getStatus()));

        // 从批量查询结果中获取设备信息
        if (equipmentMap != null && equipmentMap.containsKey(usage.getEquipmentId())) {
            Equipment equipment = equipmentMap.get(usage.getEquipmentId());
            vo.setEquipmentNo(equipment.getEquipmentNo());
            vo.setEquipmentName(equipment.getName());
        }

        return vo;
    }

    /**
     * 生成设备编号
     * 格式：EQ + yyyyMMdd + 4位序号
     *
     * @return 设备编号
     */
    private String generateEquipmentNo() {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String prefix = EQUIPMENT_NO_PREFIX + dateStr;

        // 查询当天最大的设备编号
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(Equipment::getEquipmentNo, prefix)
                .orderByDesc(Equipment::getEquipmentNo)
                .last("LIMIT 1");

        Equipment lastEquipment = this.getOne(wrapper, false);
        int seq = 1;
        if (lastEquipment != null && StrUtil.isNotBlank(lastEquipment.getEquipmentNo())) {
            String lastNo = lastEquipment.getEquipmentNo();
            if (lastNo.length() > prefix.length()) {
                try {
                    seq = Integer.parseInt(lastNo.substring(prefix.length())) + 1;
                } catch (NumberFormatException e) {
                    log.warn("解析设备编号序号失败: {}", lastNo);
                }
            }
        }

        return String.format("%s%04d", prefix, seq);
    }

    @Override
    public List<EquipmentExportVO> exportEquipments(EquipmentQueryDTO queryDTO) {
        LambdaQueryWrapper<Equipment> wrapper = buildQueryWrapper(queryDTO);
        List<Equipment> equipments = this.list(wrapper);
        return equipments.stream()
                .map(this::convertToExportVO)
                .collect(Collectors.toList());
    }

    private EquipmentExportVO convertToExportVO(Equipment equipment) {
        EquipmentExportVO vo = new EquipmentExportVO();
        BeanUtils.copyProperties(equipment, vo);
        vo.setStatusName(STATUS_NAME_MAP.getOrDefault(equipment.getStatus(), equipment.getStatus()));
        return vo;
    }
}
