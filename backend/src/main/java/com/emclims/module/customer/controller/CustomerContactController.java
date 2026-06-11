package com.emclims.module.customer.controller;

import com.emclims.common.response.R;
import com.emclims.module.customer.dto.CustomerContactDTO;
import com.emclims.module.customer.service.CustomerContactService;
import com.emclims.module.customer.vo.CustomerContactVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 联系人管理 Controller
 */
@Tag(name = "联系人管理")
@RestController
@RequestMapping("/customer/contact")
public class CustomerContactController {

    private final CustomerContactService contactService;

    public CustomerContactController(CustomerContactService contactService) {
        this.contactService = contactService;
    }

    @Operation(summary = "根据客户ID获取联系人列表")
    @GetMapping("/list/{customerId}")
    public R<List<CustomerContactVO>> listByCustomerId(@PathVariable Long customerId) {
        return R.ok(contactService.listByCustomerId(customerId));
    }

    @Operation(summary = "新增联系人")
    @PostMapping
    public R<Void> create(@Valid @RequestBody CustomerContactDTO dto) {
        contactService.createContact(dto);
        return R.ok();
    }

    @Operation(summary = "更新联系人")
    @PutMapping
    public R<Void> update(@Valid @RequestBody CustomerContactDTO dto) {
        contactService.updateContact(dto);
        return R.ok();
    }

    @Operation(summary = "删除联系人")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        contactService.deleteContact(id);
        return R.ok();
    }

    @Operation(summary = "批量删除联系人")
    @DeleteMapping("/batch")
    public R<Void> deleteBatch(@RequestBody List<Long> ids) {
        contactService.deleteContacts(ids);
        return R.ok();
    }
}
