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
} from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import CustomerPage from './pages/customer'
import LoginPage from './pages/login'
import DashboardPage from './pages/dashboard'
import NumberingRulePage from './pages/numbering-rule'
import SamplePage from './pages/sample'
import UserPage from './pages/sys/user'
import RolePage from './pages/sys/role'
import MenuPage from './pages/sys/menu'
import DeptPage from './pages/sys/dept'
import { FileTextOutlined, ExperimentOutlined, FileDoneOutlined, ToolOutlined, ProfileOutlined, ReadOutlined } from '@ant-design/icons'

const { Header, Sider, Content } = Layout

/**
 * 布局组件
 */
function AppLayout({ children }: { children: React.ReactNode }) {
  const navigate = useNavigate()
  const location = useLocation()
  const { token } = theme.useToken()

  const menuItems = [
    {
      key: '/',
      icon: <HomeOutlined />,
      label: '首页',
    },
    {
      type: 'group' as const,
      label: '业务管理',
      icon: <ApartmentOutlined />,
      children: [
        {
          key: '/customer',
          icon: <TeamOutlined />,
          label: '客户管理',
        },
        {
          key: '/sample',
          icon: <FileTextOutlined />,
          label: '样品管理',
        },
        {
          key: '/test-plan',
          icon: <ExperimentOutlined />,
          label: '测试计划',
        },
        {
          key: '/report',
          icon: <FileDoneOutlined />,
          label: '报告管理',
        },
        {
          key: '/equipment',
          icon: <ToolOutlined />,
          label: '设备管理',
        },
        {
          key: '/personnel',
          icon: <ProfileOutlined />,
          label: '人员管理',
        },
        {
          key: '/standard',
          icon: <ReadOutlined />,
          label: '标准管理',
        },
        {
          key: '/numbering-rule',
          icon: <SettingOutlined />,
          label: '编号规则',
        },
      ],
    },
    {
      type: 'group' as const,
      label: '系统管理',
      icon: <ApartmentOutlined />,
      children: [
        {
          key: '/sys/user',
          icon: <UserOutlined />,
          label: '用户管理',
        },
        {
          key: '/sys/role',
          icon: <SettingOutlined />,
          label: '角色管理',
        },
        {
          key: '/sys/menu',
          icon: <MenuUnfoldOutlined />,
          label: '菜单管理',
        },
        {
          key: '/sys/dept',
          icon: <ApartmentOutlined />,
          label: '部门管理',
        },
      ],
    },
  ]

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
        {/* 预留页面 */}
        <Route
          path="/test-plan"
          element={
            <ProtectedRoute>
              <AppLayout>
                <div style={{ textAlign: 'center', padding: '60px', color: '#999' }}>测试计划页面开发中...</div>
              </AppLayout>
            </ProtectedRoute>
          }
        />
        <Route
          path="/report"
          element={
            <ProtectedRoute>
              <AppLayout>
                <div style={{ textAlign: 'center', padding: '60px', color: '#999' }}>报告管理页面开发中...</div>
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
