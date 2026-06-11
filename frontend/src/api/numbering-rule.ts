import request from './request';

/** 编号规则 */
export interface NumberingRule {
  id?: number;
  ruleCode: string;
  ruleName: string;
  moduleType: string;
  prefix: string;
  datePattern: string;
  seqLength: number;
  separator: string;
  description?: string;
  status: number;
  createTime?: string;
}

/** 编号规则管理 API */
export const numberingRuleApi = {
  /** 查询所有规则 */
  list: () => request.get<NumberingRule[]>('/sys/numbering-rule'),

  /** 按模块查询 */
  getByModule: (moduleType: string) =>
    request.get<NumberingRule[]>(`/sys/numbering-rule/module/${moduleType}`),

  /** 查询单个 */
  getById: (id: number) =>
    request.get<NumberingRule>(`/sys/numbering-rule/${id}`),

  /** 新增 */
  create: (data: NumberingRule) =>
    request.post('/sys/numbering-rule', data),

  /** 更新 */
  update: (data: NumberingRule) =>
    request.put('/sys/numbering-rule', data),

  /** 删除 */
  delete: (id: number) =>
    request.delete(`/sys/numbering-rule/${id}`),

  /** 启用 */
  enable: (id: number) =>
    request.put(`/sys/numbering-rule/${id}/enable`),

  /** 禁用 */
  disable: (id: number) =>
    request.put(`/sys/numbering-rule/${id}/disable`),

  /** 测试生成编号 */
  generate: (ruleCode: string) =>
    request.post<string>(`/sys/numbering-rule/${ruleCode}/generate`),
};
