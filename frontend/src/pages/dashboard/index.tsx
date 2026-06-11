import { useEffect, useState } from 'react'
import { Card, Col, Row, Statistic, Typography, Table } from 'antd'
import {
  TeamOutlined,
  ExperimentOutlined,
  FileTextOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons'

const { Title, Text } = Typography

interface DashboardData {
  customerCount: number
  testItemCount: number
  pendingReportCount: number
  completedCount: number
}

export default function DashboardPage() {
  const [data, setData] = useState<DashboardData>({
    customerCount: 0,
    testItemCount: 0,
    pendingReportCount: 0,
    completedCount: 0,
  })
  const [loading, setLoading] = useState(true)

  // TODO: 当后端有对应 API 后取消注释
  // useEffect(() => {
  //   const loadDashboard = async () => {
  //     try {
  //       const res = await request.get('/dashboard/stats')
  //       setData(res)
  //     } catch {
  //       // error handled
  //     } finally {
  //       setLoading(false)
  //     }
  //   }
  //   loadDashboard()
  // }, [])

  useEffect(() => {
    setLoading(false)
  }, [])

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '100px' }}>加载中...</div>
  }

  return (
    <div>
      <Title level={4} style={{ marginBottom: 24 }}>工作台</Title>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="客户总数"
              value={data.customerCount}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#1677ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="检测项目"
              value={data.testItemCount}
              prefix={<ExperimentOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="待出报告"
              value={data.pendingReportCount}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="已完成"
              value={data.completedCount}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#13c2c2' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={12}>
          <Card title="快捷操作" size="small">
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 12 }}>
              <a href="/customer">
                <Button block>客户管理</Button>
              </a>
              <a href="/sys/user">
                <Button block>用户管理</Button>
              </a>
              <a href="/sys/role">
                <Button block>角色管理</Button>
              </a>
            </div>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="系统信息" size="small">
            <div>
              <p style={{ color: '#666' }}>
                EMC 电磁兼容实验室信息管理系统 v1.0.0
              </p>
              <p style={{ color: '#999' }}>
                前端：React 18 + Ant Design 5 + TypeScript
              </p>
              <p style={{ color: '#999' }}>
                后端：Spring Boot 3 + MyBatis-Plus + Redis
              </p>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  )
}
