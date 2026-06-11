import { useEffect, useState } from 'react'
import { Card, Col, Row, Statistic, Typography, Table, Button, Tag, Descriptions, Empty, Spin, Space } from 'antd'
import {
  TeamOutlined,
  ExperimentOutlined,
  FileTextOutlined,
  CheckCircleOutlined,
  UserOutlined,
  CalendarOutlined,
  BellOutlined,
  ClockCircleOutlined,
  CheckOutlined,
  PlusOutlined,
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'

const { Title, Text } = Typography

interface DashboardData {
  customerCount: number
  testItemCount: number
  pendingReportCount: number
  completedCount: number
}

interface Activity {
  id: number
  title: string
  status: 'pending' | 'processing' | 'completed'
  date: string
}

export default function DashboardPage() {
  const navigate = useNavigate()
  const [data, setData] = useState<DashboardData>({
    customerCount: 0,
    testItemCount: 0,
    pendingReportCount: 0,
    completedCount: 0,
  })
  const [loading, setLoading] = useState(true)
  const [activities, setActivities] = useState<Activity[]>([])

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
    // 模拟数据
    setActivities([
      { id: 1, title: '新增客户：华为技术有限公司', status: 'completed', date: '2026-06-11' },
      { id: 2, title: '测试项目：EMC 辐射发射测试', status: 'processing', date: '2026-06-10' },
      { id: 3, title: '检测报告：静电放电测试报告', status: 'pending', date: '2026-06-09' },
      { id: 4, title: '新增编号规则：EMC-2026', status: 'completed', date: '2026-06-08' },
    ])
    setLoading(false)
  }, [])

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px' }}>
        <Spin />
      </div>
    )
  }

  const statusMap = {
    pending: { text: '待处理', color: 'orange' },
    processing: { text: '进行中', color: 'blue' },
    completed: { text: '已完成', color: 'green' },
  }

  const activityColumns = [
    {
      title: '活动',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const { text, color } = statusMap[status as keyof typeof statusMap]
        return <Tag color={color}>{text}</Tag>
      },
    },
    {
      title: '日期',
      dataIndex: 'date',
      key: 'date',
    },
  ]

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <Title level={4} style={{ margin: 0 }}>工作台</Title>
        <Space>
          <Button icon={<CalendarOutlined />}>今日: {new Date().toLocaleDateString()}</Button>
        </Space>
      </div>

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
        <Col xs={24} lg={16}>
          <Card title="最近活动" size="small">
            <Table
              columns={activityColumns}
              dataSource={activities}
              rowKey="id"
              pagination={false}
              locale={{ emptyText: <Empty description="暂无活动" /> }}
            />
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card title="快捷操作" size="small">
            <Space direction="vertical" style={{ width: '100%' }}>
              <Button
                block
                icon={<PlusOutlined />}
                onClick={() => navigate('/customer')}
              >
                新增客户
              </Button>
              <Button
                block
                icon={<TeamOutlined />}
                onClick={() => navigate('/customer')}
              >
                客户管理
              </Button>
              <Button
                block
                icon={<UserOutlined />}
                onClick={() => navigate('/sys/user')}
              >
                用户管理
              </Button>
              <Button
                block
                icon={<FileTextOutlined />}
              >
                检测报告
              </Button>
            </Space>
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={12}>
          <Card title="系统信息" size="small">
            <Descriptions bordered column={1} size="small">
              <Descriptions.Item label="系统名称">EMC 电磁兼容实验室信息管理系统</Descriptions.Item>
              <Descriptions.Item label="系统版本">v1.0.0</Descriptions.Item>
              <Descriptions.Item label="前端技术">React 18 + Ant Design 5 + TypeScript</Descriptions.Item>
              <Descriptions.Item label="后端技术">Spring Boot 3 + MyBatis-Plus + Redis</Descriptions.Item>
              <Descriptions.Item label="运行环境">开发模式</Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>
      </Row>
    </div>
  )
}
