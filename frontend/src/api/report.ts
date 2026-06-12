import request from './request'

/** 报告查询参数 */
export interface ReportQuery {
  sampleId?: number
  customerId?: number
  status?: string
  startDate?: string
  endDate?: string
  pageNum?: number
  pageSize?: number
}

/** 报告 DTO */
export interface ReportDTO {
  id?: number
  sampleId: number
  customerId?: number
  remark?: string
}

/** 报告审核 DTO */
export interface ReportAuditDTO {
  comment?: string
}

/** 报告 VO */
export interface ReportVO {
  id: number
  reportNo: string
  sampleId: number
  sampleNo?: string
  productName?: string
  customerId?: number
  customerName?: string
  status: string
  statusName?: string
  version: number
  reviewerId?: number
  reviewerName?: string
  approverId?: number
  approverName?: string
  issuedDate?: string
  fileUrl?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 报告审核日志 VO */
export interface ReportAuditLogVO {
  id: number
  reportId: number
  operatorId: number
  operatorName?: string
  action: string
  actionName?: string
  comment?: string
  auditTime?: string
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

/** 报告 API */
export const reportApi = {
  /** 分页查询报告列表 */
  page: (params: ReportQuery) =>
    request.get<any, PageResult<ReportVO>>('/report/page', { params }),

  /** 获取报告详情 */
  detail: (id: number) =>
    request.get<any, ReportVO>(`/report/${id}`),

  /** 新建报告 */
  create: (data: ReportDTO) =>
    request.post<any, void>('/report', data),

  /** 更新报告 */
  update: (data: ReportDTO) =>
    request.put<any, void>('/report', data),

  /** 提交审核 */
  submit: (id: number, comment?: string) =>
    request.post<any, void>(`/report/${id}/submit`, comment ? { comment } : null),

  /** 审核通过 */
  approve: (id: number, comment?: string) =>
    request.post<any, void>(`/report/${id}/approve`, comment ? { comment } : null),

  /** 审核打回 */
  reject: (id: number, comment?: string) =>
    request.post<any, void>(`/report/${id}/reject`, comment ? { comment } : null),

  /** 签发报告 */
  issue: (id: number) =>
    request.post<any, void>(`/report/${id}/issue`),

  /** 获取审核日志 */
  auditLogs: (id: number) =>
    request.get<any, ReportAuditLogVO[]>(`/report/${id}/audit-logs`),

  /** 导出报告 */
  export: (params: ReportQuery) =>
    request.get<any, Blob>('/report/export', { params, responseType: 'blob' }),
}

/** 报告状态选项 */
export const REPORT_STATUS_OPTIONS = [
  { label: '草稿', value: 'draft' },
  { label: '审核中', value: 'review' },
  { label: '已批准', value: 'approved' },
  { label: '已签发', value: 'issued' },
  { label: '已打回', value: 'rejected' },
]

/** 报告状态颜色映射 */
export const REPORT_STATUS_COLOR: Record<string, string> = {
  draft: 'default',
  review: 'processing',
  approved: 'warning',
  issued: 'success',
  rejected: 'error',
}

/** 审核操作选项 */
export const REPORT_ACTION_OPTIONS = [
  { label: '创建', value: 'create' },
  { label: '审核', value: 'review' },
  { label: '批准', value: 'approve' },
  { label: '打回', value: 'reject' },
]
