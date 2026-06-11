import request from './request'

/** 客户查询参数 */
export interface CustomerQuery {
  keyword?: string
  type?: number
  industry?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

/** 客户 DTO */
export interface CustomerDTO {
  id?: number
  name: string
  type?: number
  industry?: string
  address?: string
  phone?: string
  email?: string
  contact?: string
  status?: number
  remark?: string
}

/** 客户 VO */
export interface CustomerVO {
  id: number
  name: string
  type?: number
  typeName?: string
  industry?: string
  address?: string
  phone?: string
  email?: string
  contact?: string
  status?: number
  remark?: string
  createBy?: number
  createTime?: string
  updateBy?: number
  updateTime?: string
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

/** 统一响应 */
export interface R<T> {
  code: number
  data: T
  message: string
}

/** 客户 API */
export const customerApi = {
  /** 分页查询客户列表 */
  page: (params: CustomerQuery) =>
    request.get<any, PageResult<CustomerVO>>('/customer/page', { params }),

  /** 获取客户详情 */
  detail: (id: number) =>
    request.get<any, CustomerVO>(`/customer/${id}`),

  /** 新增客户 */
  create: (data: CustomerDTO) =>
    request.post<any, void>('/customer', data),

  /** 更新客户 */
  update: (data: CustomerDTO) =>
    request.put<any, void>('/customer', data),

  /** 批量删除客户 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/customer/batch', { data: ids }),

  /** 修改客户状态 */
  updateStatus: (id: number, status: number) =>
    request.put<any, void>(`/customer/${id}/status`, null, { params: { status } }),
}

/** 联系人 VO */
export interface CustomerContactVO {
  id: number
  customerId: number
  name: string
  phone?: string
  email?: string
  position?: string
  isPrimary?: number
  remark?: string
  createTime?: string
}

/** 联系人 DTO */
export interface CustomerContactDTO {
  id?: number
  customerId: number
  name: string
  phone?: string
  email?: string
  position?: string
  isPrimary?: number
  remark?: string
}

/** 联系人 API */
export const customerContactApi = {
  /** 根据客户ID获取联系人列表 */
  listByCustomerId: (customerId: number) =>
    request.get<any, CustomerContactVO[]>(`/customer/contact/list/${customerId}`),

  /** 新增联系人 */
  create: (data: CustomerContactDTO) =>
    request.post<any, void>('/customer/contact', data),

  /** 更新联系人 */
  update: (data: CustomerContactDTO) =>
    request.put<any, void>('/customer/contact', data),

  /** 删除联系人 */
  delete: (id: number) =>
    request.delete<any, void>(`/customer/contact/${id}`),

  /** 批量删除联系人 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/customer/contact/batch', { data: ids }),
}
