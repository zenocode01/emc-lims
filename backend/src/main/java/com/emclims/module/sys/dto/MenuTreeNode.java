package com.emclims.module.sys.dto;

import lombok.Data;

import java.util.List;

/**
 * 菜单树节点 DTO
 */
@Data
public class MenuTreeNode {

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

    private List<MenuTreeNode> children;
}
