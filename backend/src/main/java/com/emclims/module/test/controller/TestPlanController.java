package com.emclims.module.test.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.R;
import com.emclims.common.response.PageResult;
import com.emclims.module.test.dto.TestPlanDTO;
import com.emclims.module.test.service.TestPlanService;
import com.emclims.module.test.vo.TestPlanExportVO;
import com.emclims.module.test.vo.TestPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 测试计划控制器
 */
@RestController
@RequestMapping("/test-plan")
@RequiredArgsConstructor
@Tag(name = "测试计划", description = "测试计划管理")
public class TestPlanController {

    private final TestPlanService testPlanService;

    @GetMapping("/page")
    @Operation(summary = "分页查询测试计划")
    public R<PageResult<TestPlanVO>> page(@RequestParam Long sampleId,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<TestPlanVO> page = testPlanService.pageTestPlans(sampleId, status, pageNum, pageSize);
        return R.ok(PageResult.of(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取测试计划详情")
    public R<TestPlanVO> detail(@PathVariable Long id) {
        return R.ok(testPlanService.getTestPlanDetail(id));
    }

    @PostMapping
    @Operation(summary = "创建测试计划")
    public R<Void> create(@Valid @RequestBody TestPlanDTO dto) {
        testPlanService.createTestPlan(dto);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "更新测试计划")
    public R<Void> update(@Valid @RequestBody TestPlanDTO dto) {
        testPlanService.updateTestPlan(dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除测试计划")
    public R<Void> delete(@PathVariable Long id) {
        testPlanService.deleteTestPlan(id);
        return R.ok();
    }

    @PutMapping("/{id}/start")
    @Operation(summary = "开始测试")
    public R<Void> start(@PathVariable Long id) {
        testPlanService.startTest(id);
        return R.ok();
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "完成测试")
    public R<Void> complete(@PathVariable Long id) {
        testPlanService.completeTest(id);
        return R.ok();
    }

    @Operation(summary = "导出测试计划列表")
    @GetMapping("/export")
    public void export(@RequestParam Long sampleId,
                       @RequestParam(required = false) String status,
                       HttpServletResponse response) throws java.io.IOException {
        java.util.List<TestPlanExportVO> list = testPlanService.exportTestPlans(sampleId, status);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("测试计划列表_" + System.currentTimeMillis(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), TestPlanExportVO.class)
                .sheet("测试计划列表")
                .doWrite(list);
    }
}
