import request from './request'

/** 测试计划查询参数 */
export interface TestPlanQuery {
  sampleId?: number
  status?: string
  pageNum?: number
  pageSize?: number
}

/** 测试计划 DTO */
export interface TestPlanDTO {
  id?: number
  sampleId: number
  customerId?: number
  testItemIds?: number[]
  testItems?: string
  planDate?: string
  dueDate?: string
  remark?: string
}

/** 测试计划 VO */
export interface TestPlanVO {
  id: number
  planNo: string
  sampleId: number
  sampleNo?: string
  productName?: string
  customerId?: number
  customerName?: string
  testItems?: any[]
  status: string
  statusName?: string
  planDate?: string
  dueDate?: string
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

/** 测试计划 API */
export const testPlanApi = {
  /** 分页查询测试计划 */
  page: (params: TestPlanQuery) =>
    request.get<any, PageResult<TestPlanVO>>('/test-plan/page', { params }),

  /** 获取测试计划详情 */
  detail: (id: number) =>
    request.get<any, TestPlanVO>(`/test-plan/${id}`),

  /** 创建测试计划 */
  create: (data: TestPlanDTO) =>
    request.post<any, void>('/test-plan', data),

  /** 更新测试计划 */
  update: (data: TestPlanDTO) =>
    request.put<any, void>('/test-plan', data),

  /** 删除测试计划 */
  delete: (id: number) =>
    request.delete<any, void>(`/test-plan/${id}`),

  /** 开始测试 */
  start: (id: number) =>
    request.put<any, void>(`/test-plan/${id}/start`),

  /** 完成测试 */
  complete: (id: number) =>
    request.put<any, void>(`/test-plan/${id}/complete`),
}

/** 测试计划状态选项 */
export const TEST_PLAN_STATUS_OPTIONS = [
  { label: '草稿', value: 'draft' },
  { label: '测试中', value: 'testing' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' },
]

/** 测试计划状态颜色映射 */
export const TEST_PLAN_STATUS_COLOR: Record<string, string> = {
  draft: 'default',
  testing: 'processing',
  completed: 'success',
  cancelled: 'default',
}
