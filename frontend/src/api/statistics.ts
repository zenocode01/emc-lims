import request from './request'

/** 统计概览 VO */
export interface StatisticsOverviewVO {
  customerTotal: number
  sampleTotal: number
  samplePending: number
  sampleTesting: number
  sampleCompleted: number
  sampleThisMonth: number
  testPlanTotal: number
  testPlanTesting: number
  testPlanCompleted: number
  reportTotal: number
  reportReviewing: number
  reportIssued: number
  reportIssuedThisMonth: number
  equipmentTotal: number
  equipmentNormal: number
  equipmentCalibrating: number
  personnelTotal: number
  personnelValid: number
  equipmentCalibrationDue: number
  personnelAuthExpiring: number
  sampleTrend7Days: number[]
  reportTrend7Days: number[]
  sampleCategoryDistribution: { name: string; value: number }[]
  testCategoryDistribution: { name: string; value: number }[]
}

/** 统计 API */
export const statisticsApi = {
  /** 获取统计概览 */
  getOverview: () =>
    request.get<any, StatisticsOverviewVO>('/api/statistics/overview'),
}
