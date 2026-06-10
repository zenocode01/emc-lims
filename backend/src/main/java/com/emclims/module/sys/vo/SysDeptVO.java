package com.emclims.module.sys.vo;

import lombok.Data;

import java.util.List;

/**
 * 部门视图对象
 */
@Data
public class SysDeptVO {

    private Long id;

    private String deptName;

    private String deptCode;

    private Integer deptType;

    private Long parentId;

    private String parentName;

    private Long leader;

    private String leaderName;

    private String phone;

    private String email;

    private Integer sort;

    private Integer status;

    private List<SysDeptVO> children;
}
