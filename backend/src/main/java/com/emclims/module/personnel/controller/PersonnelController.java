package com.emclims.module.personnel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.PageResult;
import com.emclims.common.response.R;
import com.emclims.module.personnel.dto.PersonnelDTO;
import com.alibaba.excel.EasyExcel;
import com.emclims.module.personnel.dto.PersonnelQueryDTO;
import com.emclims.module.personnel.service.PersonnelService;
import com.emclims.module.personnel.vo.PersonnelExportVO;
import com.emclims.module.personnel.vo.PersonnelVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 人员管理控制器
 */
@RestController
@RequestMapping("/personnel")
@Tag(name = "人员管理", description = "人员档案管理接口")
@RequiredArgsConstructor
public class PersonnelController {

    private final PersonnelService personnelService;

    /**
     * 分页查询人员档案
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询人员档案")
    public R<PageResult<PersonnelVO>> page(@Valid PersonnelQueryDTO queryDTO) {
        Page<PersonnelVO> page = personnelService.pagePersonnel(queryDTO);
        return R.ok(PageResult.of(page));
    }

    /**
     * 获取人员档案详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取人员档案详情")
    public R<PersonnelVO> detail(@PathVariable Long id) {
        return R.ok(personnelService.getPersonnelDetail(id));
    }

    /**
     * 新增人员档案
     */
    @PostMapping
    @Operation(summary = "新增人员档案")
    public R<Void> add(@Valid @RequestBody PersonnelDTO dto) {
        personnelService.addPersonnel(dto);
        return R.ok();
    }

    /**
     * 更新人员档案
     */
    @PutMapping
    @Operation(summary = "更新人员档案")
    public R<Void> update(@Valid @RequestBody PersonnelDTO dto) {
        personnelService.updatePersonnel(dto);
        return R.ok();
    }

    /**
     * 批量删除人员档案
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除人员档案")
    public R<Void> delete(@RequestBody List<Long> ids) {
        personnelService.deletePersonnel(ids);
        return R.ok();
    }

    /**
     * 导出人员档案列表
     */
    @GetMapping("/export")
    @Operation(summary = "导出人员档案列表")
    public void export(PersonnelQueryDTO queryDTO, HttpServletResponse response) throws java.io.IOException {
        List<PersonnelExportVO> list = personnelService.exportPersonnel(queryDTO);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("人员档案列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), PersonnelExportVO.class)
                .sheet("人员档案列表")
                .doWrite(list);
    }
}
