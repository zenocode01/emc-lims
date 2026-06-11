package com.emclims.module.customer.dto;

import lombok.Data;

/**
 * 客户查询 DTO
 */
@Data
public class CustomerQueryDTO {

    /** 搜索关键字（名称/联系人/电话） */
    private String keyword;

    /** 客户类型 */
    private Integer type;

    /** 行业 */
    private String industry;

    /** 状态 */
    private Integer status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
