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

/** 报告模板查询参数 */
export interface ReportTemplateQuery {
  keyword?: string
  templateType?: string
  productCategory?: string
  status?: number
  pageNum?: number
  pageSize?: number
}

/** 报告模板 DTO */
export interface ReportTemplateDTO {
  id?: number
  templateName: string
  templateCode: string
  templateType?: string
  productCategory?: string
  templateContent?: string
  previewUrl?: string
  status: number
  remark?: string
}

/** 报告模板 VO */
export interface ReportTemplateVO {
  id: number
  templateName: string
  templateCode: string
  templateType?: string
  templateTypeName?: string
  productCategory?: string
  status: number
  statusName?: string
  templateContent?: string
  previewUrl?: string
  remark?: string
  createTime?: string
  updateTime?: string
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

/** 报告模板 API */
export const reportTemplateApi = {
  /** 分页查询报告模板列表 */
  page: (params: ReportTemplateQuery) =>
    request.get<any, PageResult<ReportTemplateVO>>('/report/template/page', { params }),

  /** 获取报告模板详情 */
  detail: (id: number) =>
    request.get<any, ReportTemplateVO>(`/report/template/${id}`),

  /** 创建报告模板 */
  create: (data: ReportTemplateDTO) =>
    request.post<any, void>('/report/template', data),

  /** 更新报告模板 */
  update: (data: ReportTemplateDTO) =>
    request.put<any, void>('/report/template', data),

  /** 删除报告模板 */
  delete: (id: number) =>
    request.delete<any, void>(`/report/template/${id}`),

  /** 批量删除报告模板 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>(`/report/template/${ids.join(',')}`),

  /** 更新报告模板状态 */
  updateStatus: (id: number, status: number) =>
    request.put<any, void>(`/report/template/${id}/status`, null, { params: { status } }),
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
