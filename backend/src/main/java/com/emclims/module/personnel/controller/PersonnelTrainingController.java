package com.emclims.module.personnel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.PageResult;
import com.emclims.common.response.R;
import com.emclims.module.personnel.dto.PersonnelTrainingDTO;
import com.emclims.module.personnel.dto.PersonnelTrainingQueryDTO;
import com.emclims.module.personnel.service.PersonnelTrainingService;
import com.emclims.module.personnel.vo.PersonnelTrainingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 培训记录控制器
 */
@RestController
@RequestMapping("/personnel/training")
@Tag(name = "培训记录", description = "人员培训记录管理接口")
@RequiredArgsConstructor
public class PersonnelTrainingController {

    private final PersonnelTrainingService trainingService;

    /**
     * 分页查询培训记录
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询培训记录")
    public R<PageResult<PersonnelTrainingVO>> page(@Valid PersonnelTrainingQueryDTO queryDTO) {
        Page<PersonnelTrainingVO> page = trainingService.pageTrainings(queryDTO);
        return R.ok(PageResult.of(page));
    }

    /**
     * 获取培训记录详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取培训记录详情")
    public R<PersonnelTrainingVO> detail(@PathVariable Long id) {
        return R.ok(trainingService.getTrainingDetail(id));
    }

    /**
     * 新增培训记录
     */
    @PostMapping
    @Operation(summary = "新增培训记录")
    public R<Void> add(@Valid @RequestBody PersonnelTrainingDTO dto) {
        trainingService.addTraining(dto);
        return R.ok();
    }

    /**
     * 更新培训记录
     */
    @PutMapping
    @Operation(summary = "更新培训记录")
    public R<Void> update(@Valid @RequestBody PersonnelTrainingDTO dto) {
        trainingService.updateTraining(dto);
        return R.ok();
    }

    /**
     * 批量删除培训记录
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除培训记录")
    public R<Void> delete(@RequestBody List<Long> ids) {
        trainingService.deleteTrainings(ids);
        return R.ok();
    }

    /**
     * 根据人员ID查询培训记录列表
     */
    @GetMapping("/personnel/{personnelId}")
    @Operation(summary = "根据人员ID查询培训记录")
    public R<List<PersonnelTrainingVO>> listByPersonnelId(@PathVariable Long personnelId) {
        return R.ok(trainingService.listByPersonnelId(personnelId));
    }
}
