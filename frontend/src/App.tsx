import { Routes, Route, Navigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { ConfigProvider, Layout, Menu, theme } from 'antd'
import {
  TeamOutlined,
  HomeOutlined,
  SettingOutlined,
  UserOutlined,
  MenuUnfoldOutlined,
  ApartmentOutlined,
  BarChartOutlined,
  FileTextOutlined, ExperimentOutlined, FileDoneOutlined, ToolOutlined, ProfileOutlined, ReadOutlined,
} from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import CustomerPage from './pages/customer'
import LoginPage from './pages/login'
import DashboardPage from './pages/dashboard'
import NumberingRulePage from './pages/numbering-rule'
import StatisticsPage from './pages/statistics'
import SamplePage from './pages/sample'
import ReportPage from './pages/report'
import TestPlanPage from './pages/test-plan'
import UserPage from './pages/sys/user'
import RolePage from './pages/sys/role'
import MenuPage from './pages/sys/menu'
import DeptPage from './pages/sys/dept'
import { menuApi, MenuNode } from './api/menu'
import type { MenuProps } from 'antd'

const { Header, Sider, Content } = Layout

/**
 * 图标映射
 */
const iconMap: Record<string, React.ReactNode> = {
  'HomeOutlined': <HomeOutlined />,
  'TeamOutlined': <TeamOutlined />,
  'FileTextOutlined': <FileTextOutlined />,
  'ExperimentOutlined': <ExperimentOutlined />,
  'FileDoneOutlined': <FileDoneOutlined />,
  'ToolOutlined': <ToolOutlined />,
  'ProfileOutlined': <ProfileOutlined />,
  'ReadOutlined': <ReadOutlined />,
  'SettingOutlined': <SettingOutlined />,
  'UserOutlined': <UserOutlined />,
  'MenuUnfoldOutlined': <MenuUnfoldOutlined />,
  'ApartmentOutlined': <ApartmentOutlined />,
}

/**
 * 将后端菜单树转换为 Ant Design Menu 格式
 */
function convertMenuNodesToItems(nodes: MenuNode[]): MenuProps['items'] {
  if (!nodes || nodes.length === 0) return []
  
  return nodes.map(node => ({
    key: node.path || `/${node.id}`,
    label: node.menuName,
    icon: node.icon ? iconMap[node.icon] : undefined,
    children: node.children && node.children.length > 0 ? convertMenuNodesToItems(node.children) : undefined,
  }))
}

/**
 * 布局组件
 */
function AppLayout({ children }: { children: React.ReactNode }) {
  const navigate = useNavigate()
  const location = useLocation()
  const { token } = theme.useToken()
  
  const [menuItems, setMenuItems] = useState<MenuProps['items']>([])

  useEffect(() => {
    // 从后端加载动态菜单
    menuApi.getUserMenus().then(menus => {
      const items = convertMenuNodesToItems(menus)
      // 添加首页菜单项
      setMenuItems([
        { key: '/', icon: <HomeOutlined />, label: '首页' },
        ...(items.length > 0 ? [{ type: 'group' as const, label: '业务管理', children: items }] : []),
      ])
    }).catch(() => {
      // 加载失败时使用默认菜单
      setMenuItems([
        { key: '/', icon: <HomeOutlined />, label: '首页' },
        { type: 'group' as const, label: '业务管理', children: [
          { key: '/customer', icon: <TeamOutlined />, label: '客户管理' },
          { key: '/sample', icon: <FileTextOutlined />, label: '样品管理' },
          { key: '/test-plan', icon: <ExperimentOutlined />, label: '测试计划' },
          { key: '/report', icon: <FileDoneOutlined />, label: '报告管理' },
          { key: '/equipment', icon: <ToolOutlined />, label: '设备管理' },
          { key: '/personnel', icon: <ProfileOutlined />, label: '人员管理' },
          { key: '/standard', icon: <ReadOutlined />, label: '标准管理' },
          { key: '/numbering-rule', icon: <SettingOutlined />, label: '编号规则' },
          { key: '/statistics', icon: <BarChartOutlined />, label: '数据统计' },
        ]},
        { type: 'group' as const, label: '系统管理', children: [
          { key: '/sys/user', icon: <UserOutlined />, label: '用户管理' },
          { key: '/sys/role', icon: <SettingOutlined />, label: '角色管理' },
          { key: '/sys/menu', icon: <MenuUnfoldOutlined />, label: '菜单管理' },
          { key: '/sys/dept', icon: <ApartmentOutlined />, label: '部门管理' },
        ]},
      ])
    })
  }, [])

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        width={220}
        style={{
          background: token.colorBgContainer,
          borderRight: `1px solid ${token.colorBorderSecondary}`,
        }}
      >
        <div
          style={{
            height: 64,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            borderBottom: `1px solid ${token.colorBorderSecondary}`,
            fontWeight: 600,
            fontSize: 18,
            color: token.colorPrimary,
          }}
        >
          EMC LIMS
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
          style={{ borderInlineEnd: 'none' }}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            background: token.colorBgContainer,
            padding: '0 24px',
            borderBottom: `1px solid ${token.colorBorderSecondary}`,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'flex-end',
          }}
        >
          <span style={{ color: token.colorTextSecondary }}>EMC 电磁兼容实验室信息管理系统</span>
        </Header>
        <Content
          style={{
            margin: 24,
            padding: 24,
            background: token.colorBgContainer,
            borderRadius: token.borderRadius,
          }}
        >
          {children}
        </Content>
      </Layout>
    </Layout>
  )
}

// 简单路由守卫
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const token = localStorage.getItem('token')
  if (!token) {
    return <Navigate to="/login" replace />
  }
  return <>{children}</>
}

/**
 * EMC LIMS 主应用
 */
function App() {
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // TODO: 验证 token 有效性
    setLoading(false)
  }, [])

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '100px' }}>加载中...</div>
  }

  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#1677ff',
        },
      }}
    >
      <Routes>
        {/* 登录页 */}
        <Route path="/login" element={<LoginPage />} />

        {/* 受保护的路由 */}
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <AppLayout>
                <DashboardPage />
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/customer"
          element={
            <ProtectedRoute>
              <AppLayout>
                <CustomerPage />
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/sample"
          element={
            <ProtectedRoute>
              <AppLayout>
                <SamplePage />
              </AppLayout>
            </ProtectedRoute>
          }
        />
        {/* 测试管理页面 */}
        <Route
          path="/test-plan"
          element={
            <ProtectedRoute>
              <AppLayout>
                <TestPlanPage />
              </AppLayout>
            </ProtectedRoute>
          }
        />
        {/* 报告管理页面 */}
        <Route
          path="/report"
          element={
            <ProtectedRoute>
              <AppLayout>
                <ReportPage />
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/equipment"
          element={
            <ProtectedRoute>
              <AppLayout>
                <div style={{ textAlign: 'center', padding: '60px', color: '#999' }}>设备管理页面开发中...</div>
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/personnel"
          element={
            <ProtectedRoute>
              <AppLayout>
                <div style={{ textAlign: 'center', padding: '60px', color: '#999' }}>人员管理页面开发中...</div>
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/standard"
          element={
            <ProtectedRoute>
              <AppLayout>
                <div style={{ textAlign: 'center', padding: '60px', color: '#999' }}>标准管理页面开发中...</div>
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/numbering-rule"
          element={
            <ProtectedRoute>
              <AppLayout>
                <NumberingRulePage />
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/statistics"
          element={
            <ProtectedRoute>
              <AppLayout>
                <StatisticsPage />
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/sys/user"
          element={
            <ProtectedRoute>
              <AppLayout>
                <UserPage />
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/sys/role"
          element={
            <ProtectedRoute>
              <AppLayout>
                <RolePage />
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/sys/menu"
          element={
            <ProtectedRoute>
              <AppLayout>
                <MenuPage />
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/sys/dept"
          element={
            <ProtectedRoute>
              <AppLayout>
                <DeptPage />
              </AppLayout>
            </ProtectedRoute>
          }
        />

        {/* 404 */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </ConfigProvider>
  )
}

export default App
