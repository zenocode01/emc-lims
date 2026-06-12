import request from './request'

/** 人员查询参数 */
export interface PersonnelQuery {
  keyword?: string
  education?: string
  title?: string
  status?: string
  hireDateStart?: string
  hireDateEnd?: string
  pageNum?: number
  pageSize?: number
}

/** 人员 DTO */
export interface PersonnelDTO {
  id?: number
  userId: number
  name: string
  idCard?: string
  education?: string
  major?: string
  title?: string
  hireDate?: string
  status?: string
  remark?: string
}

/** 人员 VO */
export interface PersonnelVO {
  id: number
  userId: number
  userName: string
  name: string
  idCard?: string
  education?: string
  major?: string
  title?: string
  hireDate?: string
  status?: string
  statusName?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 资质授权查询参数 */
export interface PersonnelAuthorizationQuery {
  personnelId?: number
  authorizationItem?: string
  authorizationDateStart?: string
  authorizationDateEnd?: string
  expireDateStart?: string
  expireDateEnd?: string
  status?: string
  pageNum?: number
  pageSize?: number
}

/** 资质授权 DTO */
export interface PersonnelAuthorizationDTO {
  id?: number
  personnelId: number
  authorizationDate: string
  expireDate: string
  authorizerId: number
  authorizationItem: string
  remark?: string
}

/** 资质授权 VO */
export interface PersonnelAuthorizationVO {
  id: number
  personnelId: number
  personnelName: string
  authorizationDate: string
  expireDate: string
  authorizerId: number
  authorizerName: string
  authorizationItem: string
  status: string
  statusName?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 培训记录查询参数 */
export interface PersonnelTrainingQuery {
  personnelId?: number
  course?: string
  trainer?: string
  trainDateStart?: string
  trainDateEnd?: string
  result?: string
  pageNum?: number
  pageSize?: number
}

/** 培训记录 DTO */
export interface PersonnelTrainingDTO {
  id?: number
  personnelId: number
  course: string
  trainer?: string
  trainDate: string
  duration?: number
  result?: string
  certificate?: string
  remark?: string
}

/** 培训记录 VO */
export interface PersonnelTrainingVO {
  id: number
  personnelId: number
  personnelName: string
  course: string
  trainer?: string
  trainDate: string
  duration?: number
  result: string
  resultName?: string
  certificate?: string
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 能力矩阵查询参数 */
export interface CompetencyMatrixQuery {
  personnelId?: number
  testItemType?: string
  assessmentDateStart?: string
  assessmentDateEnd?: string
  minScore?: number
  maxScore?: number
  pageNum?: number
  pageSize?: number
}

/** 能力矩阵 DTO */
export interface CompetencyMatrixDTO {
  id?: number
  personnelId: number
  testItemType: string
  assessmentDate: string
  score: number
  assessorId: number
  remark?: string
}

/** 能力矩阵 VO */
export interface CompetencyMatrixVO {
  id: number
  personnelId: number
  personnelName: string
  testItemType: string
  assessmentDate: string
  score: number
  assessorId: number
  assessorName: string
  remark?: string
  createTime?: string
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

/** 人员 API */
export const personnelApi = {
  /** 分页查询人员列表 */
  page: (params: PersonnelQuery) =>
    request.get<any, PageResult<PersonnelVO>>('/personnel/page', { params }),

  /** 获取人员详情 */
  detail: (id: number) =>
    request.get<any, PersonnelVO>(`/personnel/${id}`),

  /** 新增人员 */
  create: (data: PersonnelDTO) =>
    request.post<any, void>('/personnel', data),

  /** 更新人员 */
  update: (data: PersonnelDTO) =>
    request.put<any, void>('/personnel', data),

  /** 批量删除人员 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/personnel/batch', { data: ids }),

  /** 导出人员档案 */
  export: (params: PersonnelQuery) => {
    // 导出通过新窗口打开，后端返回 Excel 文件流
    const query = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        query.append(key, String(value))
      }
    })
    window.open(`/api/personnel/export?${query.toString()}`, '_blank')
  },
}

/** 资质授权 API */
export const personnelAuthorizationApi = {
  /** 分页查询授权记录 */
  page: (params: PersonnelAuthorizationQuery) =>
    request.get<any, PageResult<PersonnelAuthorizationVO>>('/personnel/authorization/page', { params }),

  /** 获取授权记录详情 */
  detail: (id: number) =>
    request.get<any, PersonnelAuthorizationVO>(`/personnel/authorization/${id}`),

  /** 新增授权记录 */
  create: (data: PersonnelAuthorizationDTO) =>
    request.post<any, void>('/personnel/authorization', data),

  /** 更新授权记录 */
  update: (data: PersonnelAuthorizationDTO) =>
    request.put<any, void>('/personnel/authorization', data),

  /** 批量删除授权记录 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/personnel/authorization/batch', { data: ids }),

  /** 根据人员ID查询授权记录列表 */
  listByPersonnelId: (personnelId: number) =>
    request.get<any, PersonnelAuthorizationVO[]>(`/personnel/authorization/personnel/${personnelId}`),
}

/** 培训记录 API */
export const personnelTrainingApi = {
  /** 分页查询培训记录 */
  page: (params: PersonnelTrainingQuery) =>
    request.get<any, PageResult<PersonnelTrainingVO>>('/personnel/training/page', { params }),

  /** 获取培训记录详情 */
  detail: (id: number) =>
    request.get<any, PersonnelTrainingVO>(`/personnel/training/${id}`),

  /** 新增培训记录 */
  create: (data: PersonnelTrainingDTO) =>
    request.post<any, void>('/personnel/training', data),

  /** 更新培训记录 */
  update: (data: PersonnelTrainingDTO) =>
    request.put<any, void>('/personnel/training', data),

  /** 批量删除培训记录 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/personnel/training/batch', { data: ids }),

  /** 根据人员ID查询培训记录列表 */
  listByPersonnelId: (personnelId: number) =>
    request.get<any, PersonnelTrainingVO[]>(`/personnel/training/personnel/${personnelId}`),
}

/** 能力矩阵 API */
export const competencyMatrixApi = {
  /** 分页查询能力矩阵 */
  page: (params: CompetencyMatrixQuery) =>
    request.get<any, PageResult<CompetencyMatrixVO>>('/personnel/competency/page', { params }),

  /** 获取能力矩阵详情 */
  detail: (id: number) =>
    request.get<any, CompetencyMatrixVO>(`/personnel/competency/${id}`),

  /** 新增能力矩阵记录 */
  create: (data: CompetencyMatrixDTO) =>
    request.post<any, void>('/personnel/competency', data),

  /** 更新能力矩阵记录 */
  update: (data: CompetencyMatrixDTO) =>
    request.put<any, void>('/personnel/competency', data),

  /** 批量删除能力矩阵记录 */
  deleteBatch: (ids: number[]) =>
    request.delete<any, void>('/personnel/competency/batch', { data: ids }),

  /** 根据人员ID查询能力矩阵列表 */
  listByPersonnelId: (personnelId: number) =>
    request.get<any, CompetencyMatrixVO[]>(`/personnel/competency/personnel/${personnelId}`),

  /** 根据人员和测试项目类型查询能力矩阵 */
  getByPersonnelAndType: (personnelId: number, testItemType: string) =>
    request.get<any, CompetencyMatrixVO>(`/personnel/competency/personnel/${personnelId}/type/${testItemType}`),
}
