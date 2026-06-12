package com.emclims.module.test.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.R;
import com.emclims.common.response.PageResult;
import com.emclims.module.test.dto.TestItemDTO;
import com.emclims.module.test.dto.TestItemQueryDTO;
import com.emclims.module.test.service.TestItemService;
import com.emclims.module.test.vo.TestItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

/**
 * 测试项目控制器
 */
@RestController
@RequestMapping("/test-item")
@RequiredArgsConstructor
@Tag(name = "测试项目", description = "测试项目定义管理")
public class TestItemController {

    private final TestItemService testItemService;

    @GetMapping("/page")
    @Operation(summary = "分页查询测试项目")
    public R<PageResult<TestItemVO>> page(TestItemQueryDTO queryDTO) {
        Page<TestItemVO> page = testItemService.pageTestItems(queryDTO);
        return R.ok(PageResult.of(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取测试项目详情")
    public R<TestItemVO> detail(@PathVariable Long id) {
        return R.ok(testItemService.getTestItemDetail(id));
    }

    @PostMapping
    @Operation(summary = "新增测试项目")
    public R<Void> create(@Valid @RequestBody TestItemDTO dto) {
        testItemService.createTestItem(dto);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "更新测试项目")
    public R<Void> update(@Valid @RequestBody TestItemDTO dto) {
        testItemService.updateTestItem(dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除测试项目")
    public R<Void> delete(@PathVariable Long id) {
        testItemService.deleteTestItem(id);
        return R.ok();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "修改测试项目状态")
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        testItemService.updateTestItemStatus(id, status);
        return R.ok();
    }
}
