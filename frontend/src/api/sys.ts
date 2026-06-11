import request from './request'

/** 分页结果 */
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

/** 用户 VO */
export interface SysUserVO {
  id: number
  username: string
  nickname: string
  phone: string
  email: string
  sex: number
  avatar?: string
  status: number
  deptId?: number
  deptName?: string
  roleId?: number
  roleName?: string
  roleCode?: string
  birthday?: string
  post?: string
  employeeCode?: string
  createTime?: string
}

/** 用户 DTO */
export interface SysUserDTO {
  id?: number
  phone: string
  deptId?: number
  password?: string
  nickname?: string
  email?: string
  sex?: number
  avatar?: string
  status?: number
  birthday?: string
  post?: string
  employeeCode?: string
  roleIds?: number[]
}

/** 用户查询参数 */
export interface SysUserQuery {
  keyword?: string
  deptId?: number
  roleId?: number
  status?: number
  createTimeStart?: string
  createTimeEnd?: string
  pageNum?: number
  pageSize?: number
}

/** 角色 VO */
export interface SysRoleVO {
  id: number
  roleName: string
  roleCode: string
  roleDesc?: string
  dataScope?: number
  dataScopeName?: string
  status: number
  sort?: number
  createTime?: string
}

/** 角色 DTO */
export interface SysRoleDTO {
  id?: number
  roleName: string
  roleCode: string
  roleDesc?: string
  dataScope?: number
  status?: number
  sort?: number
  menuIds?: number[]
}

/** 角色查询参数 */
export interface SysRoleQuery {
  keyword?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

/** 菜单 VO */
export interface SysMenuVO {
  id: number
  menuName: string
  menuType: number
  path?: string
  component?: string
  permission?: string
  parentId: number
  sort?: number
  icon?: string
  isHidden?: number
  status?: number
  children?: SysMenuVO[]
}

/** 菜单 DTO */
export interface SysMenuDTO {
  id?: number
  menuName: string
  menuType: number
  path?: string
  component?: string
  permission?: string
  parentId?: number
  sort?: number
  icon?: string
  isHidden?: number
  status?: number
}

/** 部门 VO */
export interface SysDeptVO {
  id: number
  name: string
  code: string
  deptType?: number
  parentId: number
  leader?: string
  phone?: string
  email?: string
  status: number
  sort?: number
  children?: SysDeptVO[]
}

/** 部门 DTO */
export interface SysDeptDTO {
  id?: number
  name: string
  code: string
  deptType?: number
  parentId?: number
  leader?: string
  phone?: string
  email?: string
  status?: number
  sort?: number
}

// ==================== 用户 API ====================
export const sysUserApi = {
  /** 分页查询用户列表 */
  page: (params: SysUserQuery) =>
    request.get<any, PageResult<SysUserVO>>('/sys/user/page', { params }),

  /** 获取用户详情 */
  detail: (id: number) =>
    request.get<any, SysUserVO>(`/sys/user/${id}`),

  /** 新增用户 */
  create: (data: SysUserDTO) =>
    request.post<any, void>('/sys/user', data),

  /** 更新用户 */
  update: (data: SysUserDTO) =>
    request.put<any, void>('/sys/user', data),

  /** 批量删除用户 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/sys/user/batch', { data: ids }),

  /** 修改用户状态 */
  updateStatus: (id: number, status: number) =>
    request.put<any, void>(`/sys/user/${id}/status`, null, { params: { status } }),

  /** 获取所有用户（下拉框） */
  all: () =>
    request.get<any, SysUserVO[]>('/sys/user/all'),
}

// ==================== 角色 API ====================
export const sysRoleApi = {
  /** 分页查询角色列表 */
  page: (params: SysRoleQuery) =>
    request.get<any, PageResult<SysRoleVO>>('/sys/role/page', { params }),

  /** 获取角色详情 */
  detail: (id: number) =>
    request.get<any, SysRoleVO>(`/sys/role/${id}`),

  /** 新增角色 */
  create: (data: SysRoleDTO) =>
    request.post<any, void>('/sys/role', data),

  /** 更新角色 */
  update: (data: SysRoleDTO) =>
    request.put<any, void>('/sys/role', data),

  /** 批量删除角色 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/sys/role/batch', { data: ids }),

  /** 修改角色状态 */
  updateStatus: (id: number, status: number) =>
    request.put<any, void>(`/sys/role/${id}/status`, null, { params: { status } }),

  /** 获取所有角色（下拉框） */
  all: () =>
    request.get<any, SysRoleVO[]>('/sys/role/all'),

  /** 根据角色ID获取菜单ID列表 */
  getMenuIds: (roleId: number) =>
    request.get<any, number[]>(`/sys/role/menu/${roleId}`),

  /** 更新角色菜单授权 */
  updateMenus: (roleId: number, menuIds: number[]) =>
    request.put<any, void>(`/sys/role/menu/${roleId}`, null, { params: { menuIds } }),
}

// ==================== 菜单 API ====================
export const sysMenuApi = {
  /** 获取菜单树 */
  tree: () =>
    request.get<any, SysMenuVO[]>('/sys/menu/tree'),

  /** 获取菜单详情 */
  detail: (id: number) =>
    request.get<any, SysMenuVO>(`/sys/menu/${id}`),

  /** 新增菜单 */
  create: (data: SysMenuDTO) =>
    request.post<any, void>('/sys/menu', data),

  /** 更新菜单 */
  update: (data: SysMenuDTO) =>
    request.put<any, void>('/sys/menu', data),

  /** 删除菜单 */
  delete: (id: number) =>
    request.delete<any, void>(`/sys/menu/${id}`),
}

// ==================== 部门 API ====================
export const sysDeptApi = {
  /** 获取部门树 */
  tree: () =>
    request.get<any, SysDeptVO[]>('/sys/dept/tree'),

  /** 获取部门详情 */
  detail: (id: number) =>
    request.get<any, SysDeptVO>(`/sys/dept/${id}`),

  /** 新增部门 */
  create: (data: SysDeptDTO) =>
    request.post<any, void>('/sys/dept', data),

  /** 更新部门 */
  update: (data: SysDeptDTO) =>
    request.put<any, void>('/sys/dept', data),

  /** 删除部门 */
  delete: (id: number) =>
    request.delete<any, void>(`/sys/dept/${id}`),

  /** 获取所有部门（下拉框） */
  all: () =>
    request.get<any, SysDeptVO[]>('/sys/dept/all'),
}
