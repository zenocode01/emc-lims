package com.emclims.module.sys.controller;

import com.emclims.common.numbering.NumberingRuleEngine;
import com.emclims.common.response.R;
import com.emclims.module.sys.entity.SysNumberingRule;
import com.emclims.module.sys.service.NumberingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 编号规则管理 Controller
 */
@Tag(name = "编号规则管理")
@RestController
@RequestMapping("/sys/numbering-rule")
@RequiredArgsConstructor
public class NumberingRuleController {

    private final NumberingRuleService numberingRuleService;
    private final NumberingRuleEngine numberingRuleEngine;

    @Operation(summary = "查询所有编号规则")
    @GetMapping
    public R<List<SysNumberingRule>> list() {
        return R.ok(numberingRuleService.list());
    }

    @Operation(summary = "根据模块类型查询规则")
    @GetMapping("/module/{moduleType}")
    public R<List<SysNumberingRule>> getByModule(@PathVariable String moduleType) {
        return R.ok(numberingRuleService.getRulesByModule(moduleType));
    }

    @Operation(summary = "查询单个规则")
    @GetMapping("/{id}")
    public R<SysNumberingRule> getById(@PathVariable Long id) {
        return R.ok(numberingRuleService.getById(id));
    }

    @Operation(summary = "新增编号规则")
    @PostMapping
    public R<Void> create(@RequestBody SysNumberingRule rule) {
        numberingRuleService.save(rule);
        return R.ok();
    }

    @Operation(summary = "更新编号规则")
    @PutMapping
    public R<Void> update(@RequestBody SysNumberingRule rule) {
        numberingRuleService.updateById(rule);
        return R.ok();
    }

    @Operation(summary = "删除编号规则")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        numberingRuleService.removeById(id);
        return R.ok();
    }

    @Operation(summary = "启用规则")
    @PutMapping("/{id}/enable")
    public R<Void> enable(@PathVariable Long id) {
        numberingRuleService.enableRule(id);
        return R.ok();
    }

    @Operation(summary = "禁用规则")
    @PutMapping("/{id}/disable")
    public R<Void> disable(@PathVariable Long id) {
        numberingRuleService.disableRule(id);
        return R.ok();
    }

    @Operation(summary = "测试生成编号")
    @PostMapping("/{ruleCode}/generate")
    public R<String> generate(@PathVariable String ruleCode) {
        String number = numberingRuleEngine.generateNumber(ruleCode);
        return R.ok(number);
    }
}
