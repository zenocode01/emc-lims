package com.emclims.module.customer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emclims.common.response.R;
import com.emclims.common.response.PageResult;
import com.emclims.module.customer.dto.CustomerDTO;
import com.emclims.module.customer.dto.CustomerQueryDTO;
import com.alibaba.excel.EasyExcel;
import com.emclims.module.customer.dto.CustomerQueryDTO;
import com.emclims.module.customer.service.CustomerService;
import com.emclims.module.customer.vo.CustomerExportVO;
import com.emclims.module.customer.vo.CustomerVO;
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
 * 客户管理 Controller
 */
@Tag(name = "客户管理")
@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "分页查询客户列表")
    @GetMapping("/page")
    public R<PageResult<CustomerVO>> page(CustomerQueryDTO queryDTO) {
        Page<CustomerVO> page = customerService.pageCustomers(queryDTO);
        return R.ok(PageResult.of(page));
    }

    @Operation(summary = "获取客户详情")
    @GetMapping("/{id}")
    public R<CustomerVO> detail(@PathVariable Long id) {
        return R.ok(customerService.getCustomerDetail(id));
    }

    @Operation(summary = "新增客户")
    @PostMapping
    public R<Void> create(@Valid @RequestBody CustomerDTO dto) {
        customerService.createCustomer(dto);
        return R.ok();
    }

    @Operation(summary = "更新客户")
    @PutMapping
    public R<Void> update(@Valid @RequestBody CustomerDTO dto) {
        customerService.updateCustomer(dto);
        return R.ok();
    }

    @Operation(summary = "批量删除客户")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        customerService.deleteCustomers(ids);
        return R.ok();
    }

    @Operation(summary = "修改客户状态")
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        customerService.updateStatus(id, status);
        return R.ok();
    }

    @Operation(summary = "导出客户列表")
    @GetMapping("/export")
    public void export(CustomerQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        List<CustomerExportVO> list = customerService.exportCustomers(queryDTO);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("客户列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), CustomerExportVO.class)
                .sheet("客户列表")
                .doWrite(list);
    }
}
