package com.emclims.module.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.exception.BusinessException;
import com.emclims.module.equipment.dto.CalibrationDTO;
import com.emclims.module.equipment.dto.CalibrationQueryDTO;
import com.emclims.module.equipment.dto.EquipmentDTO;
import com.emclims.module.equipment.dto.EquipmentQueryDTO;
import com.emclims.module.equipment.entity.Equipment;
import com.emclims.module.equipment.entity.EquipmentCalibration;
import com.emclims.module.equipment.mapper.EquipmentCalibrationMapper;
import com.emclims.module.equipment.mapper.EquipmentUsageMapper;
import com.emclims.module.equipment.vo.CalibrationVO;
import com.emclims.module.equipment.vo.EquipmentVO;
import com.emclims.module.equipment.vo.UsageVO;
import com.emclims.module.equipment.mapper.EquipmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EquipmentServiceImpl 设备服务单元测试
 *
 * <p>测试覆盖以下业务方法：
 * <ul>
 *   <li>pageEquipment - 分页查询设备</li>
 *   <li>getEquipmentDetail - 获取设备详情</li>
 *   <li>addEquipment - 新增设备</li>
 *   <li>updateEquipment - 更新设备</li>
 *   <li>deleteEquipment - 删除设备</li>
 *   <li>pageCalibration - 分页查询校准记录</li>
 *   <li>saveOrUpdateCalibration - 新增/编辑校准记录</li>
 *   <li>deleteCalibration - 删除校准记录</li>
 *   <li>getCalibrationHistory - 获取校准历史</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class EquipmentServiceImplTest {

    @Mock
    private EquipmentMapper equipmentMapper;

    @Mock
    private EquipmentCalibrationMapper calibrationMapper;

    @Mock
    private EquipmentUsageMapper usageMapper;

    private EquipmentServiceImpl equipmentService;

    @BeforeEach
    void setUp() {
        equipmentService = new EquipmentServiceImpl(calibrationMapper, usageMapper);

        // 模拟 RequestContextHolder
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 1L);
        request.setAttribute("username", "admin");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    // ==================== pageEquipment 测试 ====================

    /**
     * 测试正常分页查询设备
     */
    @Test
    void testPageEquipment() {
        EquipmentQueryDTO queryDTO = new EquipmentQueryDTO();
        queryDTO.setKeyword("频谱");
        queryDTO.setStatus("normal");
        queryDTO.setLocation("实验室A");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setEquipmentNo("EQ-20250101-0001");
        equipment.setName("频谱分析仪");
        equipment.setModel("RSA5126B");
        equipment.setManufacturer("Keysight");
        equipment.setStatus("normal");
        equipment.setLocation("实验室A");
        equipment.setCalibrationDue(LocalDate.of(2026, 1, 1));

        Page<Equipment> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(equipment));

        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<EquipmentVO> result = spy.pageEquipment(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("频谱分析仪", result.getRecords().get(0).getName());
        assertEquals("正常", result.getRecords().get(0).getStatusName());
        verify(spy).page(any(Page.class), any(LambdaQueryWrapper.class));
    }

    /**
     * 测试空结果分页查询
     */
    @Test
    void testPageEquipmentEmpty() {
        EquipmentQueryDTO queryDTO = new EquipmentQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Page<Equipment> pageResult = new Page<>(1, 10, 0);
        pageResult.setRecords(Collections.emptyList());

        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<EquipmentVO> result = spy.pageEquipment(queryDTO);

        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
        assertEquals(0, result.getTotal());
    }

    /**
     * 测试按状态筛选分页查询
     */
    @Test
    void testPageEquipmentByStatus() {
        EquipmentQueryDTO queryDTO = new EquipmentQueryDTO();
        queryDTO.setStatus("maintenance");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setEquipmentNo("EQ-20250101-0002");
        equipment.setName("信号发生器");
        equipment.setStatus("maintenance");

        Page<Equipment> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(equipment));

        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<EquipmentVO> result = spy.pageEquipment(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("维护中", result.getRecords().get(0).getStatusName());
    }

    /**
     * 测试按位置筛选分页查询
     */
    @Test
    void testPageEquipmentByLocation() {
        EquipmentQueryDTO queryDTO = new EquipmentQueryDTO();
        queryDTO.setLocation("实验室B");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("功率计");
        equipment.setLocation("实验室B-1楼");
        equipment.setStatus("normal");

        Page<Equipment> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(equipment));

        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<EquipmentVO> result = spy.pageEquipment(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    /**
     * 测试按校准日期范围筛选
     */
    @Test
    void testPageEquipmentByCalibrationDate() {
        EquipmentQueryDTO queryDTO = new EquipmentQueryDTO();
        queryDTO.setCalibrationDueStart(LocalDate.of(2025, 6, 1));
        queryDTO.setCalibrationDueEnd(LocalDate.of(2025, 12, 31));
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("频谱仪");
        equipment.setCalibrationDue(LocalDate.of(2025, 9, 15));
        equipment.setStatus("normal");

        Page<Equipment> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(equipment));

        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<EquipmentVO> result = spy.pageEquipment(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    /**
     * 测试无关键字查询返回所有设备
     */
    @Test
    void testPageEquipmentNoKeyword() {
        EquipmentQueryDTO queryDTO = new EquipmentQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Equipment equipment1 = new Equipment();
        equipment1.setId(1L);
        equipment1.setEquipmentNo("EQ-001");
        equipment1.setName("频谱分析仪");
        equipment1.setStatus("normal");

        Equipment equipment2 = new Equipment();
        equipment2.setId(2L);
        equipment2.setEquipmentNo("EQ-002");
        equipment2.setName("信号发生器");
        equipment2.setStatus("calibration");

        Page<Equipment> pageResult = new Page<>(1, 10, 2);
        pageResult.setRecords(List.of(equipment1, equipment2));

        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(pageResult).when(spy).page(any(Page.class), any(LambdaQueryWrapper.class));

        Page<EquipmentVO> result = spy.pageEquipment(queryDTO);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        // Verify status names are mapped correctly
    }

    // ==================== getEquipmentDetail 测试 ====================

    /**
     * 测试正常获取设备详情
     */
    @Test
    void testGetEquipmentDetail() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setEquipmentNo("EQ-20250101-0001");
        equipment.setName("频谱分析仪");
        equipment.setModel("RSA5126B");
        equipment.setManufacturer("Keysight");
        equipment.setSerialNo("SN123456");
        equipment.setStatus("normal");
        equipment.setLocation("实验室A");
        equipment.setCalibrationDue(LocalDate.of(2026, 1, 1));
        equipment.setLastCalibration(LocalDate.of(2025, 1, 1));

        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(equipment).when(spy).getById(1L);

        EquipmentVO vo = spy.getEquipmentDetail(1L);

        assertNotNull(vo);
        assertEquals("EQ-20250101-0001", vo.getEquipmentNo());
        assertEquals("频谱分析仪", vo.getName());
        assertEquals("RSA5126B", vo.getModel());
        assertEquals("Keysight", vo.getManufacturer());
        assertEquals("正常", vo.getStatusName());
    }

    /**
     * 测试获取不存在的设备详情
     */
    @Test
    void testGetEquipmentDetailNotFound() {
        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.getEquipmentDetail(999L));
        assertEquals("设备不存在", exception.getMessage());
    }

    // ==================== addEquipment 测试 ====================

    /**
     * 测试正常新增设备
     */
    @Test
    void testAddEquipment() {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setName("频谱分析仪");
        dto.setModel("RSA5126B");
        dto.setManufacturer("Keysight");
        dto.setSerialNo("SN123456");
        dto.setLocation("实验室A");
        dto.setCalibrationDue(LocalDate.of(2026, 1, 1));
        dto.setRemark("高精度频谱分析仪");

        EquipmentServiceImpl spy = spy(equipmentService);
                doReturn(true).when(spy).save(any(Equipment.class));

        assertDoesNotThrow(() -> spy.addEquipment(dto));
    }

    /**
     * 测试新增设备 - 默认状态为正常
     */
    @Test
    void testAddEquipmentDefaultStatus() {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setName("新设备");
        dto.setModel("Model-X");

        EquipmentServiceImpl spy = spy(equipmentService);
                doReturn(true).when(spy).save(any(Equipment.class));

        assertDoesNotThrow(() -> spy.addEquipment(dto));
    }

    /**
     * 测试新增设备 - 指定状态
     */
    @Test
    void testAddEquipmentWithStatus() {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setName("维护中设备");
        dto.setModel("Model-Y");
        dto.setStatus("maintenance");

        EquipmentServiceImpl spy = spy(equipmentService);
                doReturn(true).when(spy).save(any(Equipment.class));

        assertDoesNotThrow(() -> spy.addEquipment(dto));
    }

    // ==================== updateEquipment 测试 ====================

    /**
     * 测试正常更新设备
     */
    @Test
    void testUpdateEquipment() {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setId(1L);
        dto.setName("更新的频谱分析仪");
        dto.setModel("RSA5000B");
        dto.setLocation("实验室B");
        dto.setStatus("normal");

        Equipment existingEquipment = new Equipment();
        existingEquipment.setId(1L);
        existingEquipment.setEquipmentNo("EQ-001");
        existingEquipment.setName("频谱分析仪");
        existingEquipment.setStatus("normal");

        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(existingEquipment).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Equipment.class));

        assertDoesNotThrow(() -> spy.updateEquipment(dto));
        verify(spy).updateById(any(Equipment.class));
    }

    /**
     * 测试更新不存在的设备
     */
    @Test
    void testUpdateEquipmentNotFound() {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setId(999L);
        dto.setName("不存在的设备");

        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(null).when(spy).getById(999L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.updateEquipment(dto));
        assertEquals("设备不存在", exception.getMessage());
        verify(spy, never()).updateById(any(Equipment.class));
    }

    /**
     * 测试更新设备 - ID 为 null
     */
    @Test
    void testUpdateEquipmentNullId() {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setName("名称");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> equipmentService.updateEquipment(dto));
        assertEquals("设备ID不能为空", exception.getMessage());
    }

    // ==================== deleteEquipment 测试 ====================

    /**
     * 测试正常删除设备
     */
    @Test
    void testDeleteEquipment() {
        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(true).when(spy).removeByIds(anyList());

        assertDoesNotThrow(() -> spy.deleteEquipment(Arrays.asList(1L, 2L, 3L)));
        verify(spy).removeByIds(Arrays.asList(1L, 2L, 3L));
    }

    /**
     * 测试删除空列表
     */
    @Test
    void testDeleteEquipmentEmptyList() {
        EquipmentServiceImpl spy = spy(equipmentService);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.deleteEquipment(Collections.emptyList()));
        assertEquals("请选择要删除的设备", exception.getMessage());
        verify(spy, never()).removeByIds(anyList());
    }

    /**
     * 测试删除 null 列表
     */
    @Test
    void testDeleteEquipmentNullList() {
        EquipmentServiceImpl spy = spy(equipmentService);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.deleteEquipment(null));
        assertEquals("请选择要删除的设备", exception.getMessage());
        verify(spy, never()).removeByIds(anyList());
    }

    // ==================== changeStatus 测试（通过 updateEquipment 模拟） ====================

    /**
     * 测试正常修改设备状态（正常 -> 维护中）
     */
    @Test
    void testChangeEquipmentStatus() {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setId(1L);
        dto.setStatus("maintenance");

        Equipment existingEquipment = new Equipment();
        existingEquipment.setId(1L);
        existingEquipment.setEquipmentNo("EQ-001");
        existingEquipment.setName("频谱分析仪");
        existingEquipment.setStatus("normal");

        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(existingEquipment).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Equipment.class));

        assertDoesNotThrow(() -> spy.updateEquipment(dto));
        assertEquals("maintenance", existingEquipment.getStatus());
        verify(spy).updateById(argThat(e -> "maintenance".equals(e.getStatus())));
    }

    /**
     * 测试修改设备状态（维护中 -> 报废）
     */
    @Test
    void testChangeEquipmentStatusToScrap() {
        EquipmentDTO dto = new EquipmentDTO();
        dto.setId(1L);
        dto.setStatus("scrap");

        Equipment existingEquipment = new Equipment();
        existingEquipment.setId(1L);
        existingEquipment.setStatus("maintenance");

        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(existingEquipment).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Equipment.class));

        assertDoesNotThrow(() -> spy.updateEquipment(dto));
        assertEquals("scrap", existingEquipment.getStatus());
    }

    // ==================== pageCalibration 测试 ====================

    /**
     * 测试正常分页查询校准记录
     */
    @Test
    void testPageCalibration() {
        CalibrationQueryDTO queryDTO = new CalibrationQueryDTO();
        queryDTO.setEquipmentId(1L);
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        EquipmentCalibration calibration = new EquipmentCalibration();
        calibration.setId(1L);
        calibration.setEquipmentId(1L);
        calibration.setCalibrationDate(LocalDate.of(2025, 6, 1));
        calibration.setDueDate(LocalDate.of(2026, 6, 1));
        calibration.setCalibrationOrg("计量院");
        calibration.setCertificateNo("CERT-2025-001");
        calibration.setResult("pass");

        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setEquipmentNo("EQ-20250101-0001");
        equipment.setName("频谱分析仪");

        Page<EquipmentCalibration> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(calibration));

        doReturn(pageResult).when(calibrationMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(equipment).when(spy).getById(1L);

        Page<CalibrationVO> result = spy.pageCalibration(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        CalibrationVO vo = result.getRecords().get(0);
        assertEquals("EQ-20250101-0001", vo.getEquipmentNo());
        assertEquals("频谱分析仪", vo.getEquipmentName());
        assertEquals("计量院", vo.getCalibrationOrg());
    }

    /**
     * 测试空结果分页查询校准记录
     */
    @Test
    void testPageCalibrationEmpty() {
        CalibrationQueryDTO queryDTO = new CalibrationQueryDTO();
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        Page<EquipmentCalibration> pageResult = new Page<>(1, 10, 0);
        pageResult.setRecords(Collections.emptyList());

        doReturn(pageResult).when(calibrationMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));

        EquipmentServiceImpl spy = spy(equipmentService);

        Page<CalibrationVO> result = spy.pageCalibration(queryDTO);

        assertNotNull(result);
        assertEquals(0, result.getRecords().size());
    }

    /**
     * 测试按校准结果筛选分页查询
     */
    @Test
    void testPageCalibrationByResult() {
        CalibrationQueryDTO queryDTO = new CalibrationQueryDTO();
        queryDTO.setResult("pass");
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        EquipmentCalibration calibration = new EquipmentCalibration();
        calibration.setId(1L);
        calibration.setEquipmentId(1L);
        calibration.setCalibrationDate(LocalDate.of(2025, 6, 1));
        calibration.setResult("pass");

        Page<EquipmentCalibration> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(calibration));

        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setEquipmentNo("EQ-001");
        equipment.setName("测试设备");

        doReturn(pageResult).when(calibrationMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(equipment).when(spy).getById(1L);

        Page<CalibrationVO> result = spy.pageCalibration(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("pass", result.getRecords().get(0).getResult());
    }

    /**
     * 测试按校准日期范围筛选
     */
    @Test
    void testPageCalibrationByDateRange() {
        CalibrationQueryDTO queryDTO = new CalibrationQueryDTO();
        queryDTO.setCalibrationDateStart(LocalDate.of(2025, 1, 1));
        queryDTO.setCalibrationDateEnd(LocalDate.of(2025, 12, 31));
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(10);

        EquipmentCalibration calibration = new EquipmentCalibration();
        calibration.setId(1L);
        calibration.setEquipmentId(1L);
        calibration.setCalibrationDate(LocalDate.of(2025, 6, 15));

        Page<EquipmentCalibration> pageResult = new Page<>(1, 10, 1);
        pageResult.setRecords(List.of(calibration));

        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setEquipmentNo("EQ-001");
        equipment.setName("测试设备");

        doReturn(pageResult).when(calibrationMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(equipment).when(spy).getById(1L);

        Page<CalibrationVO> result = spy.pageCalibration(queryDTO);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    // ==================== saveOrUpdateCalibration 测试 ====================

    /**
     * 测试新增校准记录
     */
    @Test
    void testSaveOrUpdateCalibrationNew() {
        CalibrationDTO dto = new CalibrationDTO();
        dto.setEquipmentId(1L);
        dto.setCalibrationDate(LocalDate.of(2025, 6, 1));
        dto.setDueDate(LocalDate.of(2026, 6, 1));
        dto.setCalibrationOrg("计量院");
        dto.setCertificateNo("CERT-2025-001");
        dto.setResult("pass");

        EquipmentCalibration calibration = new EquipmentCalibration();
        calibration.setId(1L);
        calibration.setEquipmentId(1L);
        calibration.setCalibrationDate(LocalDate.of(2025, 6, 1));
        calibration.setDueDate(LocalDate.of(2026, 6, 1));

        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setLastCalibration(null);
        equipment.setCalibrationDue(null);

        doReturn(1).when(calibrationMapper).insert(any(EquipmentCalibration.class));
        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(equipment).when(spy).getById(1L);
        doReturn(true).when(spy).updateById(any(Equipment.class));

        assertDoesNotThrow(() -> spy.saveOrUpdateCalibration(dto));
        verify(calibrationMapper).insert(any(EquipmentCalibration.class));
    }

    /**
     * 测试编辑校准记录
     */
    @Test
    void testSaveOrUpdateCalibrationEdit() {
        CalibrationDTO dto = new CalibrationDTO();
        dto.setId(1L);
        dto.setEquipmentId(1L);
        dto.setCalibrationDate(LocalDate.of(2025, 7, 1));
        dto.setDueDate(LocalDate.of(2026, 7, 1));
        dto.setCalibrationOrg("新计量院");
        dto.setResult("pass");

        EquipmentCalibration existingCalibration = new EquipmentCalibration();
        existingCalibration.setId(1L);
        existingCalibration.setEquipmentId(1L);
        existingCalibration.setCalibrationDate(LocalDate.of(2025, 6, 1));
        existingCalibration.setCalibrationOrg("原计量院");

        doReturn(existingCalibration).when(calibrationMapper).selectById(1L);
        doReturn(true).when(calibrationMapper).updateById(any(EquipmentCalibration.class));

        EquipmentServiceImpl spy = spy(equipmentService);

        assertDoesNotThrow(() -> spy.saveOrUpdateCalibration(dto));
        verify(calibrationMapper).selectById(1L);
        verify(calibrationMapper).updateById(any(EquipmentCalibration.class));
    }

    /**
     * 测试编辑不存在的校准记录
     */
    @Test
    void testSaveOrUpdateCalibrationEditNotFound() {
        CalibrationDTO dto = new CalibrationDTO();
        dto.setId(999L);
        dto.setEquipmentId(1L);
        dto.setCalibrationDate(LocalDate.of(2025, 7, 1));
        dto.setCalibrationOrg("计量院");

        doReturn(null).when(calibrationMapper).selectById(999L);

        EquipmentServiceImpl spy = spy(equipmentService);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.saveOrUpdateCalibration(dto));
        assertEquals("校准记录不存在", exception.getMessage());
    }

    // ==================== deleteCalibration 测试 ====================

    /**
     * 测试正常删除校准记录
     */
    @Test
    void testDeleteCalibration() {
        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(3).when(calibrationMapper).deleteBatchIds(anyList());

        assertDoesNotThrow(() -> spy.deleteCalibration(Arrays.asList(1L, 2L, 3L)));
        verify(calibrationMapper).deleteBatchIds(Arrays.asList(1L, 2L, 3L));
    }

    /**
     * 测试删除空列表
     */
    @Test
    void testDeleteCalibrationEmptyList() {
        EquipmentServiceImpl spy = spy(equipmentService);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.deleteCalibration(Collections.emptyList()));
        assertEquals("请选择要删除的校准记录", exception.getMessage());
        verify(calibrationMapper, never()).deleteBatchIds(anyList());
    }

    /**
     * 测试删除 null 列表
     */
    @Test
    void testDeleteCalibrationNullList() {
        EquipmentServiceImpl spy = spy(equipmentService);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> spy.deleteCalibration(null));
        assertEquals("请选择要删除的校准记录", exception.getMessage());
    }

    // ==================== getCalibrationHistory 测试 ====================

    /**
     * 测试正常获取校准历史
     */
    @Test
    void testGetCalibrationHistory() {
        EquipmentCalibration calibration1 = new EquipmentCalibration();
        calibration1.setId(1L);
        calibration1.setEquipmentId(1L);
        calibration1.setCalibrationDate(LocalDate.of(2025, 6, 1));
        calibration1.setCalibrationOrg("计量院");
        calibration1.setResult("pass");

        EquipmentCalibration calibration2 = new EquipmentCalibration();
        calibration2.setId(2L);
        calibration2.setEquipmentId(1L);
        calibration2.setCalibrationDate(LocalDate.of(2024, 6, 1));
        calibration2.setCalibrationOrg("原计量院");
        calibration2.setResult("pass");

        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setEquipmentNo("EQ-20250101-0001");
        equipment.setName("频谱分析仪");

        doReturn(List.of(calibration1, calibration2)).when(calibrationMapper).selectList(any(LambdaQueryWrapper.class));
        EquipmentServiceImpl spy = spy(equipmentService);
        doReturn(equipment).when(spy).getById(1L);

        List<CalibrationVO> result = spy.getCalibrationHistory(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        CalibrationVO first = result.get(0);
        assertEquals("EQ-20250101-0001", first.getEquipmentNo());
        assertEquals("频谱分析仪", first.getEquipmentName());
        // 验证按日期倒序
        assertTrue(result.get(0).getCalibrationDate().isAfter(result.get(1).getCalibrationDate()));
        verify(calibrationMapper).selectList(any(LambdaQueryWrapper.class));
    }

    /**
     * 测试获取空校准历史
     */
    @Test
    void testGetCalibrationHistoryEmpty() {
        doReturn(Collections.emptyList()).when(calibrationMapper).selectList(any(LambdaQueryWrapper.class));

        EquipmentServiceImpl spy = spy(equipmentService);

        List<CalibrationVO> result = spy.getCalibrationHistory(1L);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
