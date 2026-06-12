import { useEffect, useState } from 'react'
import { Row, Col, Card, Statistic, Spin, Alert, Descriptions, Table, Tag } from 'antd'
import {
  TeamOutlined,
  InboxOutlined,
  FileTextOutlined,
  ToolOutlined,
  UserOutlined,
  RiseOutlined,
  DownloadOutlined,
  WarningOutlined,
} from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import { statisticsApi, type StatisticsOverviewVO } from '../../api/statistics'

/**
 * 数据统计仪表盘页面
 */
export default function StatisticsPage() {
  const [data, setData] = useState<StatisticsOverviewVO | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    loadStatistics()
  }, [])

  const loadStatistics = async () => {
    try {
      const res = await statisticsApi.getOverview()
      setData(res)
      setError(null)
    } catch (err) {
      setError('加载统计数据失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <Spin size="large" tip="加载中..." />
      </div>
    )
  }

  if (error) {
    return <Alert message={error} type="error" showIcon />
  }

  if (!data) {
    return <Alert message="暂无数据" type="info" showIcon />
  }

  return (
    <div>
      {/* 顶部统计卡片 */}
      <Row gutter={[16, 16]}>
        {/* 客户统计 */}
        <Col span={6}>
          <Card hoverable>
            <Statistic
              title="客户总数"
              value={data.customerTotal}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>

        {/* 样品统计 */}
        <Col span={6}>
          <Card hoverable>
            <Statistic
              title="样品总数"
              value={data.sampleTotal}
              prefix={<InboxOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
            <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
              待测：{data.samplePending} | 测试中：{data.sampleTesting} | 已完成：{data.sampleCompleted}
            </div>
          </Card>
        </Col>

        {/* 报告统计 */}
        <Col span={6}>
          <Card hoverable>
            <Statistic
              title="报告总数"
              value={data.reportTotal}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
            <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
              待审核：{data.reportReviewing} | 已签发：{data.reportIssued}
            </div>
          </Card>
        </Col>

        {/* 设备统计 */}
        <Col span={6}>
          <Card hoverable>
            <Statistic
              title="设备总数"
              value={data.equipmentTotal}
              prefix={<ToolOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
            <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
              正常：{data.equipmentNormal} | 校准中：{data.equipmentCalibrating}
            </div>
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        {/* 人员统计 */}
        <Col span={8}>
          <Card hoverable>
            <Statistic
              title="人员总数"
              value={data.personnelTotal}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#13c2c2' }}
            />
            <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
              授权有效：{data.personnelValid}
            </div>
          </Card>
        </Col>

        {/* 本月统计 */}
        <Col span={8}>
          <Card hoverable>
            <Statistic
              title="本月新增样品"
              value={data.sampleThisMonth}
              prefix={<RiseOutlined />}
              valueStyle={{ color: '#eb2f96' }}
            />
            <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
              本月签发报告：{data.reportIssuedThisMonth}
            </div>
          </Card>
        </Col>

        {/* 预警统计 */}
        <Col span={8}>
          <Card hoverable>
            <Statistic
              title="即将到期校准"
              value={data.equipmentCalibrationDue}
              prefix={<WarningOutlined />}
              valueStyle={{ color: '#ff4d4f' }}
            />
            <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
              资质即将到期：{data.personnelAuthExpiring}
            </div>
          </Card>
        </Col>
      </Row>

      {/* 趋势图表区域 */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col span={12}>
          <Card title="最近 7 天样品接收趋势">
            <div style={{ height: 200, display: 'flex', alignItems: 'end', gap: 8, padding: '20px 0' }}>
              {data.sampleTrend7Days.map((count, index) => {
                const maxCount = Math.max(...data.sampleTrend7Days, 1)
                const height = Math.max((count / maxCount) * 150, 4)
                const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                return (
                  <div key={index} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                    <div style={{ fontSize: 12, marginBottom: 4 }}>{count}</div>
                    <div
                      style={{
                        width: '100%',
                        height: `${height}px`,
                        background: 'linear-gradient(180deg, #1890ff, #69c0ff)',
                        borderRadius: '4px 4px 0 0',
                      }}
                    />
                    <div style={{ fontSize: 12, marginTop: 4 }}>{days[index]}</div>
                  </div>
                )
              })}
            </div>
          </Card>
        </Col>

        <Col span={12}>
          <Card title="最近 7 天报告签发趋势">
            <div style={{ height: 200, display: 'flex', alignItems: 'end', gap: 8, padding: '20px 0' }}>
              {data.reportTrend7Days.map((count, index) => {
                const maxCount = Math.max(...data.reportTrend7Days, 1)
                const height = Math.max((count / maxCount) * 150, 4)
                const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                return (
                  <div key={index} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                    <div style={{ fontSize: 12, marginBottom: 4 }}>{count}</div>
                    <div
                      style={{
                        width: '100%',
                        height: `${height}px`,
                        background: 'linear-gradient(180deg, #52c41a, #95de64)',
                        borderRadius: '4px 4px 0 0',
                      }}
                    />
                    <div style={{ fontSize: 12, marginTop: 4 }}>{days[index]}</div>
                  </div>
                )
              })}
            </div>
          </Card>
        </Col>
      </Row>

      {/* 分布图表区域 */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col span={12}>
          <Card title="样品类别分布">
            <Table
              dataSource={data.sampleCategoryDistribution}
              columns={[
                { title: '类别', dataIndex: 'name', key: 'name' },
                { title: '数量', dataIndex: 'value', key: 'value' },
              ]}
              pagination={false}
              size="small"
            />
          </Card>
        </Col>

        <Col span={12}>
          <Card title="测试类别分布">
            <Table
              dataSource={data.testCategoryDistribution}
              columns={[
                { title: '类别', dataIndex: 'name', key: 'name' },
                { title: '数量', dataIndex: 'value', key: 'value' },
              ]}
              pagination={false}
              size="small"
            />
          </Card>
        </Col>
      </Row>

      {/* 底部操作按钮 */}
      <div style={{ marginTop: 16, textAlign: 'center' }}>
        <button
          onClick={loadStatistics}
          style={{
            padding: '8px 16px',
            background: '#1890ff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
          }}
        >
          刷新数据
        </button>
      </div>
    </div>
  )
}
