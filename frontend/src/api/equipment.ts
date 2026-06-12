import request from './request'

/** 设备查询参数 */
export interface EquipmentQuery {
  keyword?: string
  status?: string
  location?: string
  calibrationDueStart?: string
  calibrationDueEnd?: string
  pageNum?: number
  pageSize?: number
}

/** 设备 DTO */
export interface EquipmentDTO {
  id?: number
  name: string
  model?: string
  manufacturer?: string
  serialNo?: string
  location?: string
  status?: string
  calibrationDue?: string
  lastCalibration?: string
  remark?: string
}

/** 设备 VO */
export interface EquipmentVO {
  id: number
  equipmentNo: string
  name: string
  model?: string
  manufacturer?: string
  serialNo?: string
  location?: string
  status?: string
  statusName?: string
  calibrationDue?: string
  lastCalibration?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 校准记录查询参数 */
export interface CalibrationQuery {
  equipmentId?: number
  calibrationDateStart?: string
  calibrationDateEnd?: string
  result?: string
  pageNum?: number
  pageSize?: number
}

/** 校准记录 DTO */
export interface CalibrationDTO {
  id?: number
  equipmentId: number
  calibrationDate: string
  dueDate?: string
  calibrationOrg: string
  certificateNo?: string
  result?: string
  attachment?: string
}

/** 校准记录 VO */
export interface CalibrationVO {
  id: number
  equipmentId: number
  equipmentNo?: string
  equipmentName?: string
  calibrationDate: string
  dueDate?: string
  calibrationOrg: string
  certificateNo?: string
  result?: string
  attachment?: string
  createTime?: string
}

/** 使用记录查询参数 */
export interface UsageQuery {
  equipmentId?: number
  userId?: number
  startTimeStart?: string
  startTimeEnd?: string
  status?: string
  pageNum?: number
  pageSize?: number
}

/** 使用记录 DTO */
export interface UsageDTO {
  id?: number
  equipmentId: number
  testPlanId?: number
  userId: number
  startTime: string
  endTime?: string
  status?: string
  remark?: string
}

/** 使用记录 VO */
export interface UsageVO {
  id: number
  equipmentId: number
  equipmentNo?: string
  equipmentName?: string
  testPlanId?: number
  userId?: number
  userName?: string
  startTime: string
  endTime?: string
  status?: string
  statusName?: string
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

/** 统一响应 */
export interface R<T> {
  code: number
  data: T
  message: string
}

/** 设备 API */
export const equipmentApi = {
  /** 分页查询设备列表 */
  page: (params: EquipmentQuery) =>
    request.get<any, PageResult<EquipmentVO>>('/equipment/page', { params }),

  /** 获取设备详情 */
  detail: (id: number) =>
    request.get<any, EquipmentVO>(`/equipment/${id}`),

  /** 新增设备 */
  create: (data: EquipmentDTO) =>
    request.post<any, void>('/equipment', data),

  /** 更新设备 */
  update: (data: EquipmentDTO) =>
    request.put<any, void>('/equipment', data),

  /** 批量删除设备 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/equipment/' + ids.join(',')),

  /** 导出设备列表 */
  export: (params: EquipmentQuery) =>
    request.get<any, Blob>('/equipment/export', { params, responseType: 'blob' }),
}

/** 校准记录 API */
export const calibrationApi = {
  /** 分页查询校准记录 */
  page: (params: CalibrationQuery) =>
    request.get<any, PageResult<CalibrationVO>>('/equipment/calibration/page', { params }),

  /** 新增/编辑校准记录 */
  saveOrUpdate: (data: CalibrationDTO) =>
    request.post<any, void>('/equipment/calibration', data),

  /** 批量删除校准记录 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/equipment/calibration/' + ids.join(',')),

  /** 获取设备校准历史 */
  history: (equipmentId: number) =>
    request.get<any, CalibrationVO[]>(`/equipment/calibration/history/${equipmentId}`),
}

/** 使用记录 API */
export const usageApi = {
  /** 分页查询使用记录 */
  page: (params: UsageQuery) =>
    request.get<any, PageResult<UsageVO>>('/equipment/usage/page', { params }),

  /** 新增使用记录 */
  create: (data: UsageDTO) =>
    request.post<any, void>('/equipment/usage', data),

  /** 更新使用记录 */
  update: (data: UsageDTO) =>
    request.put<any, void>('/equipment/usage', data),

  /** 批量删除使用记录 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/equipment/usage/' + ids.join(',')),
}
