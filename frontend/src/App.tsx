import { Routes, Route, Navigate } from 'react-router-dom'
import { useEffect, useState } from 'react'

/**
 * EMC LIMS 主应用组件
 * 路由配置：
 * - /login -> 登录页
 * - / -> 重定向到首页
 * - 后续扩展各模块页面
 */

// 简单路由守卫（后续用 React Router Outlet + 布局组件完善）
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const token = localStorage.getItem('token')
  if (!token) {
    return <Navigate to="/login" replace />
  }
  return <>{children}</>
}

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
    <Routes>
      {/* 登录页 */}
      <Route path="/login" element={<LoginPage />} />

      {/* 首页（占位，后续开发） */}
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />

      {/* 404 */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

// 登录页（占位）
function LoginPage() {
  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    }}>
      <div style={{
        background: 'white',
        padding: '40px',
        borderRadius: '8px',
        boxShadow: '0 4px 20px rgba(0,0,0,0.15)',
        width: '400px',
      }}>
        <h1 style={{ textAlign: 'center', marginBottom: '30px' }}>EMC LIMS</h1>
        <p style={{ textAlign: 'center', color: '#666' }}>电磁兼容实验室信息管理系统</p>
        <div style={{ textAlign: 'center', marginTop: '20px', padding: '40px', background: '#f5f5f5', borderRadius: '4px' }}>
          登录页面开发中...
        </div>
      </div>
    </div>
  )
}

// 首页（占位）
function DashboardPage() {
  return (
    <div style={{ padding: '24px' }}>
      <h1>EMC LIMS 首页</h1>
      <p style={{ color: '#666' }}>首页内容开发中...</p>
    </div>
  )
}

export default App
