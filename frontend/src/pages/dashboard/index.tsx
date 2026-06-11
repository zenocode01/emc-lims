import { Card, Col, Row, Statistic, Typography } from 'antd'
import { TeamOutlined, ExperimentOutlined, FileTextOutlined, CheckCircleOutlined } from '@ant-design/icons'

const { Title } = Typography

export default function DashboardPage() {
  return (
    <div>
      <Title level={4} style={{ marginBottom: 24 }}>工作台</Title>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="客户总数"
              value={0}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#1677ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="检测项目"
              value={0}
              prefix={<ExperimentOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="待出报告"
              value={0}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#faad14' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="已完成"
              value={0}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#13c2c2' }}
            />
          </Card>
        </Col>
      </Row>

      <Card style={{ marginTop: 24 }}>
        <Title level={5}>快捷操作</Title>
        <p style={{ color: '#666' }}>
          欢迎使用 EMC 电磁兼容实验室信息管理系统。
          请从左侧菜单选择功能模块开始使用。
        </p>
      </Card>
    </div>
  )
}
