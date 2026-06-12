package com.emclims.module.test.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.R;
import com.emclims.common.response.PageResult;
import com.emclims.module.test.dto.TestRecordDTO;
import com.emclims.module.test.dto.TestRecordQueryDTO;
import com.emclims.module.test.service.TestRecordService;
import com.emclims.module.test.vo.TestRecordExportVO;
import com.emclims.module.test.vo.TestRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 测试记录控制器
 */
@RestController
@RequestMapping("/test-record")
@RequiredArgsConstructor
@Tag(name = "测试记录", description = "测试数据记录管理")
public class TestRecordController {

    private final TestRecordService testRecordService;

    @GetMapping("/page")
    @Operation(summary = "分页查询测试记录")
    public R<PageResult<TestRecordVO>> page(TestRecordQueryDTO queryDTO) {
        Page<TestRecordVO> page = testRecordService.pageTestRecords(queryDTO);
        return R.ok(PageResult.of(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取测试记录详情")
    public R<TestRecordVO> detail(@PathVariable Long id) {
        return R.ok(testRecordService.getTestRecordDetail(id));
    }

    @PostMapping
    @Operation(summary = "新增测试记录")
    public R<Void> create(@Valid @RequestBody TestRecordDTO dto) {
        testRecordService.createTestRecord(dto);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "更新测试记录")
    public R<Void> update(@Valid @RequestBody TestRecordDTO dto) {
        testRecordService.updateTestRecord(dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除测试记录")
    public R<Void> delete(@PathVariable Long id) {
        testRecordService.deleteTestRecord(id);
        return R.ok();
    }

    @Operation(summary = "导出测试记录列表")
    @GetMapping("/export")
    public void export(TestRecordQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        List<TestRecordExportVO> list = testRecordService.exportTestRecords(queryDTO);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("测试记录列表_" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
        EasyExcel.write(response.getOutputStream(), TestRecordExportVO.class).sheet("测试记录列表").doWrite(list);
    }
}
