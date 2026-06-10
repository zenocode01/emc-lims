package com.emclims.module.sys.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户查询 DTO
 */
@Data
public class SysUserQueryDTO {

    /** 搜索关键字（手机号/昵称/工号） */
    private String keyword;

    /** 部门ID */
    private Long deptId;

    /** 角色ID */
    private Long roleId;

    /** 状态 */
    private Integer status;

    /** 创建时间-开始 */
    private LocalDateTime createTimeStart;

    /** 创建时间-结束 */
    private LocalDateTime createTimeEnd;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
