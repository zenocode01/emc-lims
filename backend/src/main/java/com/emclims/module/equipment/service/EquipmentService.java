package com.emclims.module.equipment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.emclims.module.equipment.dto.CalibrationDTO;
import com.emclims.module.equipment.dto.CalibrationQueryDTO;
import com.emclims.module.equipment.dto.EquipmentDTO;
import com.emclims.module.equipment.dto.EquipmentQueryDTO;
import com.emclims.module.equipment.dto.UsageDTO;
import com.emclims.module.equipment.dto.UsageQueryDTO;
import com.emclims.module.equipment.entity.Equipment;
import com.emclims.module.equipment.vo.CalibrationVO;
import com.emclims.module.equipment.vo.EquipmentExportVO;
import com.emclims.module.equipment.vo.EquipmentVO;
import com.emclims.module.equipment.vo.UsageVO;

import java.util.List;

/**
 * 设备 Service
 */
public interface EquipmentService extends IService<Equipment> {

    /**
     * 分页查询设备
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<EquipmentVO> pageEquipment(EquipmentQueryDTO queryDTO);

    /**
     * 获取设备详情
     *
     * @param id 设备ID
     * @return 设备详情
     */
    EquipmentVO getEquipmentDetail(Long id);

    /**
     * 新增设备
     *
     * @param dto 设备信息
     */
    void addEquipment(EquipmentDTO dto);

    /**
     * 更新设备
     *
     * @param dto 设备信息
     */
    void updateEquipment(EquipmentDTO dto);

    /**
     * 删除设备（批量）
     *
     * @param ids 设备ID列表
     */
    void deleteEquipment(List<Long> ids);

    /**
     * 分页查询校准记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<CalibrationVO> pageCalibration(CalibrationQueryDTO queryDTO);

    /**
     * 新增/编辑校准记录
     *
     * @param dto 校准记录信息
     */
    void saveOrUpdateCalibration(CalibrationDTO dto);

    /**
     * 删除校准记录
     *
     * @param ids 校准记录ID列表
     */
    void deleteCalibration(List<Long> ids);

    /**
     * 获取设备校准历史
     *
     * @param equipmentId 设备ID
     * @return 校准记录列表
     */
    List<CalibrationVO> getCalibrationHistory(Long equipmentId);

    /**
     * 分页查询使用记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<UsageVO> pageUsage(UsageQueryDTO queryDTO);

    /**
     * 新增使用记录
     *
     * @param dto 使用记录信息
     */
    void addUsage(UsageDTO dto);

    /**
     * 更新使用记录
     *
     * @param dto 使用记录信息
     */
    void updateUsage(UsageDTO dto);

    /**
     * 删除使用记录
     *
     * @param ids 使用记录ID列表
     */
    void deleteUsage(List<Long> ids);

    /**
     * 导出设备列表
     *
     * @param queryDTO 查询条件
     * @return 设备导出列表
     */
    List<EquipmentExportVO> exportEquipments(EquipmentQueryDTO queryDTO);
}
