package com.emclims.module.sample.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.R;
import com.emclims.common.response.PageResult;
import com.emclims.module.sample.dto.SampleDTO;
import com.emclims.module.sample.dto.SampleQueryDTO;
import com.emclims.module.sample.dto.SampleStatusDTO;
import com.emclims.module.sample.service.SampleService;
import com.emclims.module.sample.vo.SampleExportVO;
import com.emclims.module.sample.vo.SampleLogVO;
import com.emclims.module.sample.vo.SampleVO;
import com.alibaba.excel.EasyExcel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 样品管理 Controller
 */
@Tag(name = "样品管理")
@RestController
@RequestMapping("/sample")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @Operation(summary = "分页查询样品列表")
    @GetMapping("/page")
    public R<PageResult<SampleVO>> page(SampleQueryDTO queryDTO) {
        Page<SampleVO> page = sampleService.pageSamples(queryDTO);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取样品详情")
    @GetMapping("/{id}")
    public R<SampleVO> detail(@PathVariable Long id) {
        return R.ok(sampleService.getSampleDetail(id));
    }

    @Operation(summary = "收样登记")
    @PostMapping
    public R<Void> receive(@Valid @RequestBody SampleDTO dto) {
        sampleService.receiveSample(dto);
        return R.ok();
    }

    @Operation(summary = "更新样品信息")
    @PutMapping
    public R<Void> update(@Valid @RequestBody SampleDTO dto) {
        sampleService.updateSample(dto);
        return R.ok();
    }

    @Operation(summary = "批量删除样品")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        sampleService.deleteSamples(ids);
        return R.ok();
    }

    @Operation(summary = "变更样品状态")
    @PutMapping("/status")
    public R<Void> changeStatus(@Valid @RequestBody SampleStatusDTO dto) {
        sampleService.changeStatus(dto);
        return R.ok();
    }

    @Operation(summary = "获取样品流转日志")
    @GetMapping("/{id}/logs")
    public R<List<SampleLogVO>> logs(@PathVariable Long id) {
        return R.ok(sampleService.getSampleLogs(id));
    }

    @Operation(summary = "导出样品列表")
    @GetMapping("/export")
    public void export(SampleQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        List<SampleExportVO> list = sampleService.exportSamples(queryDTO);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("样品列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), SampleExportVO.class)
                .sheet("样品列表")
                .doWrite(list);
    }
}
