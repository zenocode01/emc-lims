import request from './request'

/** 样品查询参数 */
export interface SampleQuery {
  keyword?: string
  customerId?: number
  status?: string
  receiveDateStart?: string
  receiveDateEnd?: string
  pageNum?: number
  pageSize?: number
}

/** 样品 DTO */
export interface SampleDTO {
  id?: number
  customerId: number
  contractId?: number
  productName: string
  model?: string
  manufacturer?: string
  batchNo?: string
  receiveDate: string
  sampleCount?: number
  testStandards?: string
  testRequirements?: string
  testerId?: number
  remark?: string
}

/** 样品 VO */
export interface SampleVO {
  id: number
  sampleNo: string
  customerId: number
  customerName?: string
  contractId?: number
  productName: string
  model?: string
  manufacturer?: string
  batchNo?: string
  receiveDate: string
  sampleCount?: number
  status: string
  statusName?: string
  testStandards?: string
  testRequirements?: string
  testerId?: number
  testerName?: string
  receiveById?: number
  receiveByName?: string
  createBy?: number
  createTime?: string
  updateBy?: number
  updateTime?: string
}

/** 样品状态变更 DTO */
export interface SampleStatusDTO {
  sampleId: number
  toStatus: string
  remark?: string
}

/** 样品流转日志 VO */
export interface SampleLogVO {
  id: number
  sampleId: number
  fromStatus?: string
  fromStatusName?: string
  toStatus: string
  toStatusName: string
  remark?: string
  operatorId?: number
  operatorName?: string
  createTime?: string
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

/** 样品 API */
export const sampleApi = {
  /** 分页查询样品列表 */
  page: (params: SampleQuery) =>
    request.get<any, PageResult<SampleVO>>('/sample/page', { params }),

  /** 获取样品详情 */
  detail: (id: number) =>
    request.get<any, SampleVO>(`/sample/${id}`),

  /** 收样登记（新增样品） */
  create: (data: SampleDTO) =>
    request.post<any, void>('/sample', data),

  /** 更新样品信息 */
  update: (data: SampleDTO) =>
    request.put<any, void>('/sample', data),

  /** 批量删除样品 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/sample/batch', { data: ids }),

  /** 变更样品状态 */
  changeStatus: (data: SampleStatusDTO) =>
    request.put<any, void>('/sample/status', data),

  /** 获取样品流转日志 */
  logs: (id: number) =>
    request.get<any, SampleLogVO[]>(`/sample/${id}/logs`),
}

/** 样品状态选项 */
export const SAMPLE_STATUS_OPTIONS = [
  { label: '待收样', value: 'pending' },
  { label: '已收样', value: 'received' },
  { label: '测试中', value: 'testing' },
  { label: '测试完成', value: 'completed' },
  { label: '留样中', value: 'retained' },
  { label: '已处置', value: 'disposed' },
  { label: '已归还', value: 'returned' },
]

/** 样品状态颜色映射 */
export const SAMPLE_STATUS_COLOR: Record<string, string> = {
  pending: 'default',
  received: 'processing',
  testing: 'processing',
  completed: 'success',
  retained: 'warning',
  disposed: 'default',
  returned: 'default',
}
