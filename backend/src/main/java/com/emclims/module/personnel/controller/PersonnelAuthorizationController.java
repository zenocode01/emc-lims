package com.emclims.module.personnel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.PageResult;
import com.emclims.common.response.R;
import com.emclims.module.personnel.dto.PersonnelAuthorizationDTO;
import com.emclims.module.personnel.dto.PersonnelAuthorizationQueryDTO;
import com.emclims.module.personnel.service.PersonnelAuthorizationService;
import com.emclims.module.personnel.vo.PersonnelAuthorizationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 授权上岗记录控制器
 */
@RestController
@RequestMapping("/personnel/authorization")
@Tag(name = "授权上岗记录", description = "人员授权上岗管理接口")
@RequiredArgsConstructor
public class PersonnelAuthorizationController {

    private final PersonnelAuthorizationService authorizationService;

    /**
     * 分页查询授权上岗记录
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询授权上岗记录")
    public R<PageResult<PersonnelAuthorizationVO>> page(@Valid PersonnelAuthorizationQueryDTO queryDTO) {
        Page<PersonnelAuthorizationVO> page = authorizationService.pageAuthorizations(queryDTO);
        return R.ok(PageResult.of(page));
    }

    /**
     * 获取授权上岗记录详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取授权上岗记录详情")
    public R<PersonnelAuthorizationVO> detail(@PathVariable Long id) {
        return R.ok(authorizationService.getAuthorizationDetail(id));
    }

    /**
     * 新增授权上岗记录
     */
    @PostMapping
    @Operation(summary = "新增授权上岗记录")
    public R<Void> add(@Valid @RequestBody PersonnelAuthorizationDTO dto) {
        authorizationService.addAuthorization(dto);
        return R.ok();
    }

    /**
     * 更新授权上岗记录
     */
    @PutMapping
    @Operation(summary = "更新授权上岗记录")
    public R<Void> update(@Valid @RequestBody PersonnelAuthorizationDTO dto) {
        authorizationService.updateAuthorization(dto);
        return R.ok();
    }

    /**
     * 批量删除授权上岗记录
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除授权上岗记录")
    public R<Void> delete(@RequestBody List<Long> ids) {
        authorizationService.deleteAuthorizations(ids);
        return R.ok();
    }

    /**
     * 根据人员ID查询授权记录列表
     */
    @GetMapping("/personnel/{personnelId}")
    @Operation(summary = "根据人员ID查询授权记录")
    public R<List<PersonnelAuthorizationVO>> listByPersonnelId(@PathVariable Long personnelId) {
        return R.ok(authorizationService.listByPersonnelId(personnelId));
    }
}
