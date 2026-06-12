import { lazy } from 'react'

/**
 * 页面组件懒加载映射
 * 根据 component 路径动态导入对应的页面组件
 */
export const pageComponentMap: Record<string, () => Promise<{ default: React.FC }>> = {
  // 登录
  'login/index': () => import('../pages/login'),
  // Dashboard
  'dashboard/index': () => import('../pages/dashboard'),
  // 系统管理
  'sys/user/index': () => import('../pages/sys/user'),
  'sys/role/index': () => import('../pages/sys/role'),
  'sys/menu/index': () => import('../pages/sys/menu'),
  'sys/dept/index': () => import('../pages/sys/dept'),
  // 业务管理
  'customer/index': () => import('../pages/customer'),
  'sample/index': () => import('../pages/sample'),
  'test-plan/index': () => import('../pages/test-plan'),
  'report/index': () => import('../pages/report'),
  'equipment/index': () => import('../pages/equipment'),
  'personnel/index': () => import('../pages/personnel'),
  'standard/index': () => import('../pages/standard'),
  // 编号规则
  'numbering-rule/index': () => import('../pages/numbering-rule'),
}

/**
 * 根据 component 路径获取页面组件
 * @param componentPath component 路径，如 'sys/user/index'
 * @returns 懒加载组件，如果找不到则返回默认组件
 */
export function getPageComponent(componentPath: string): React.FC {
  const loader = pageComponentMap[componentPath]
  if (loader) {
    const LazyComponent = lazy(loader)
    return () => (
      <React.Suspense fallback={<div style={{ textAlign: 'center', padding: '40px' }}>加载中...</div>}>
        <LazyComponent />
      </React.Suspense>
    )
  }
  // 默认返回 Dashboard
  const LazyDashboard = lazy(() => import('../pages/dashboard'))
  return () => (
    <React.Suspense fallback={<div style={{ textAlign: 'center', padding: '40px' }}>加载中...</div>}>
      <LazyDashboard />
    </React.Suspense>
  )
}

/**
 * 静态路由配置（无需权限）
 */
export const staticRoutes = [
  {
    path: '/login',
    component: lazy(() => import('../pages/login')),
  },
]

/**
 * 获取所有静态路由路径（用于权限检查）
 */
export function getStaticRoutePaths(): string[] {
  return staticRoutes.map(route => route.path)
}
