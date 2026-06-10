package com.emclims.module.sys.vo;

import lombok.Data;

import java.util.List;

/**
 * 菜单视图对象
 */
@Data
public class SysMenuVO {

    private Long id;

    private String menuName;

    private String menuType;

    private String path;

    private String component;

    private String permission;

    private Long parentId;

    private Integer sort;

    private String icon;

    private Integer isHidden;

    private Integer status;

    private List<SysMenuVO> children;
}
