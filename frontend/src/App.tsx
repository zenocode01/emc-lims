import { Routes, Route, Navigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { ConfigProvider, Layout, Menu, theme } from 'antd'
import { TeamOutlined, HomeOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import CustomerPage from './pages/customer'
import LoginPage from './pages/login'
import DashboardPage from './pages/dashboard'

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
      key: '/customer',
      icon: <TeamOutlined />,
      label: '客户管理',
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

        {/* 404 */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </ConfigProvider>
  )
}



export default App
