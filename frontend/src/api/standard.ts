import request from './request'

/** 标准查询参数 */
export interface StandardQuery {
  keyword?: string
  type?: string
  status?: string
  effectiveDateStart?: string
  effectiveDateEnd?: string
  pageNum?: number
  pageSize?: number
}

/** 标准 DTO */
export interface StandardDTO {
  id?: number
  code: string
  name: string
  version?: string
  issuingOrg?: string
  effectiveDate?: string
  expiryDate?: string
  status?: string
  type: string
  remark?: string
}

/** 标准 VO */
export interface StandardVO {
  id: number
  code: string
  name: string
  version?: string
  issuingOrg?: string
  effectiveDate?: string
  expiryDate?: string
  status: string
  statusName?: string
  type: string
  typeName?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 标准分类查询参数 */
export interface StandardCategoryQuery {
  keyword?: string
  productType?: string
  pageNum?: number
  pageSize?: number
}

/** 标准分类 DTO */
export interface StandardCategoryDTO {
  id?: number
  name: string
  applicableStandards?: number[]
  productType: string
  remark?: string
}

/** 标准分类 VO */
export interface StandardCategoryVO {
  id: number
  name: string
  applicableStandards?: number[]
  applicableStandardDetails?: StandardCategoryStandardInfo[]
  productType: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 标准分类中的标准信息 */
export interface StandardCategoryStandardInfo {
  id: number
  code: string
  name: string
  version?: string
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

/** 标准 API */
export const standardApi = {
  /** 分页查询标准列表 */
  page: (params: StandardQuery) =>
    request.get<any, PageResult<StandardVO>>('/standard/page', { params }),

  /** 获取标准详情 */
  detail: (id: number) =>
    request.get<any, StandardVO>(`/standard/${id}`),

  /** 新增标准 */
  create: (data: StandardDTO) =>
    request.post<any, void>('/standard', data),

  /** 更新标准 */
  update: (data: StandardDTO) =>
    request.put<any, void>('/standard', data),

  /** 批量删除标准 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/standard', { data: ids }),

  /** 导出标准列表 */
  export: (params: StandardQuery) =>
    request.get<any, Blob>('/standard/export', { params, responseType: 'blob' }),
}

/** 标准分类 API */
export const standardCategoryApi = {
  /** 分页查询标准分类列表 */
  page: (params: StandardCategoryQuery) =>
    request.get<any, PageResult<StandardCategoryVO>>('/standard/category/page', { params }),

  /** 获取标准分类详情 */
  detail: (id: number) =>
    request.get<any, StandardCategoryVO>(`/standard/category/${id}`),

  /** 新增标准分类 */
  create: (data: StandardCategoryDTO) =>
    request.post<any, void>('/standard/category', data),

  /** 更新标准分类 */
  update: (data: StandardCategoryDTO) =>
    request.put<any, void>('/standard/category', data),

  /** 批量删除标准分类 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/standard/category', { data: ids }),
}
