package com.emclims.module.sys.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色菜单授权 DTO
 */
@Data
public class RoleMenuDTO {

    private Long roleId;

    private List<Long> menuIds;
}
