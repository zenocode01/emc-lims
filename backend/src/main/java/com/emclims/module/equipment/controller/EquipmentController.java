package com.emclims.module.equipment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.PageResult;
import com.emclims.common.response.R;
import com.emclims.module.equipment.dto.CalibrationDTO;
import com.emclims.module.equipment.dto.CalibrationQueryDTO;
import com.emclims.module.equipment.dto.EquipmentDTO;
import com.emclims.module.equipment.dto.EquipmentQueryDTO;
import com.emclims.module.equipment.dto.UsageDTO;
import com.emclims.module.equipment.dto.UsageQueryDTO;
import com.emclims.module.equipment.service.EquipmentService;
import com.alibaba.excel.EasyExcel;
import com.emclims.module.equipment.vo.CalibrationVO;
import com.emclims.module.equipment.vo.EquipmentExportVO;
import com.emclims.module.equipment.vo.EquipmentVO;
import com.emclims.module.equipment.vo.UsageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 设备管理控制器
 * 提供设备台账、校准记录、使用记录的 CRUD 接口
 */
@Tag(name = "设备管理")
@RestController
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    // ==================== 设备台账接口 ====================

    /**
     * 分页查询设备
     */
    @Operation(summary = "分页查询设备")
    @GetMapping("/page")
    public R<PageResult<EquipmentVO>> page(EquipmentQueryDTO queryDTO) {
        Page<EquipmentVO> page = equipmentService.pageEquipment(queryDTO);
        return R.ok(PageResult.of(page));
    }

    /**
     * 获取设备详情
     */
    @Operation(summary = "获取设备详情")
    @GetMapping("/{id}")
    public R<EquipmentVO> detail(@Parameter(description = "设备ID") @PathVariable Long id) {
        return R.ok(equipmentService.getEquipmentDetail(id));
    }

    /**
     * 新增设备
     */
    @Operation(summary = "新增设备")
    @PostMapping
    public R<Void> add(@Valid @RequestBody EquipmentDTO dto) {
        equipmentService.addEquipment(dto);
        return R.ok();
    }

    /**
     * 更新设备
     */
    @Operation(summary = "更新设备")
    @PutMapping
    public R<Void> update(@Valid @RequestBody EquipmentDTO dto) {
        equipmentService.updateEquipment(dto);
        return R.ok();
    }

    /**
     * 删除设备
     */
    @Operation(summary = "删除设备")
    @DeleteMapping("/{ids}")
    public R<Void> delete(@Parameter(description = "设备ID列表，逗号分隔") @PathVariable List<Long> ids) {
        equipmentService.deleteEquipment(ids);
        return R.ok();
    }

    // ==================== 校准记录接口 ====================

    /**
     * 分页查询校准记录
     */
    @Operation(summary = "分页查询校准记录")
    @GetMapping("/calibration/page")
    public R<PageResult<CalibrationVO>> pageCalibration(CalibrationQueryDTO queryDTO) {
        Page<CalibrationVO> page = equipmentService.pageCalibration(queryDTO);
        return R.ok(PageResult.of(page));
    }

    /**
     * 新增/编辑校准记录
     */
    @Operation(summary = "新增/编辑校准记录")
    @PostMapping("/calibration")
    public R<Void> saveCalibration(@Valid @RequestBody CalibrationDTO dto) {
        equipmentService.saveOrUpdateCalibration(dto);
        return R.ok();
    }

    /**
     * 删除校准记录
     */
    @Operation(summary = "删除校准记录")
    @DeleteMapping("/calibration/{ids}")
    public R<Void> deleteCalibration(@Parameter(description = "校准记录ID列表，逗号分隔") @PathVariable List<Long> ids) {
        equipmentService.deleteCalibration(ids);
        return R.ok();
    }

    /**
     * 获取设备校准历史
     */
    @Operation(summary = "获取设备校准历史")
    @GetMapping("/calibration/history/{equipmentId}")
    public R<List<CalibrationVO>> getCalibrationHistory(
            @Parameter(description = "设备ID") @PathVariable Long equipmentId) {
        return R.ok(equipmentService.getCalibrationHistory(equipmentId));
    }

    // ==================== 使用记录接口 ====================

    /**
     * 分页查询使用记录
     */
    @Operation(summary = "分页查询使用记录")
    @GetMapping("/usage/page")
    public R<PageResult<UsageVO>> pageUsage(UsageQueryDTO queryDTO) {
        Page<UsageVO> page = equipmentService.pageUsage(queryDTO);
        return R.ok(PageResult.of(page));
    }

    /**
     * 新增使用记录
     */
    @Operation(summary = "新增使用记录")
    @PostMapping("/usage")
    public R<Void> addUsage(@Valid @RequestBody UsageDTO dto) {
        equipmentService.addUsage(dto);
        return R.ok();
    }

    /**
     * 更新使用记录
     */
    @Operation(summary = "更新使用记录")
    @PutMapping("/usage")
    public R<Void> updateUsage(@Valid @RequestBody UsageDTO dto) {
        equipmentService.updateUsage(dto);
        return R.ok();
    }

    /**
     * 删除使用记录
     */
    @Operation(summary = "删除使用记录")
    @DeleteMapping("/usage/{ids}")
    public R<Void> deleteUsage(@Parameter(description = "使用记录ID列表，逗号分隔") @PathVariable List<Long> ids) {
        equipmentService.deleteUsage(ids);
        return R.ok();
    }

    /**
     * 导出设备列表
     */
    @Operation(summary = "导出设备列表")
    @GetMapping("/export")
    public void export(EquipmentQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        List<EquipmentExportVO> list = equipmentService.exportEquipments(queryDTO);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("设备列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), EquipmentExportVO.class)
                .sheet("设备列表")
                .doWrite(list);
    }
}
