import { useEffect, useState, useCallback, ReactNode } from 'react'
import { Navigate, useRoutes, type RouteObject } from 'react-router-dom'
import { menuApi, menuToRoutes, mapComponent, buildMenuTree, MenuNode } from '../api/menu'
import { getPageComponent } from './routes'
import { Spin } from 'antd'

/**
 * 静态路由配置（无需权限即可访问）
 */
const staticRoutes: RouteObject[] = [
  {
    path: '/login',
    element: <import('../pages/login').then(m => <m.default />) />,
  },
]

/**
 * 检查用户是否拥有指定权限
 */
export function hasPermission(permission: string, userPermissions: string[]): boolean {
  return userPermissions.includes(permission)
}

/**
 * 将后端菜单树转换为 React Router 路由配置
 */
export function buildRoutesFromMenus(menus: MenuNode[]): RouteObject[] {
  const routes: RouteObject[] = []

  const traverse = (menuList: MenuNode[], parentPath = '') => {
    for (const menu of menuList) {
      if (menu.menuType === 1 && !menu.isHidden) {
        // 菜单类型，需要路由
        const path = menu.path || `/${menu.id}`
        const fullPath = parentPath ? `${parentPath}${path}` : path
        
        // 根据 component 字段动态导入组件
        const component = mapComponent(menu.component || 'dashboard/index')
        
        routes.push({
          path: fullPath,
          element: component ? <component /> : <Navigate to="/" replace />,
        })
      }
      // 递归处理子菜单
      if (menu.children && menu.children.length > 0) {
        traverse(menu.children, parentPath)
      }
    }
  }

  traverse(menus)
  return routes
}

/**
 * 权限路由配置提供者
 * 从后端加载菜单和权限，渲染动态路由
 */
export function PermissionRouter({ children }: { children: ReactNode }) {
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [permissions, setPermissions] = useState<string[]>([])
  const [menus, setMenus] = useState<MenuNode[]>([])

  const loadPermissions = useCallback(async () => {
    try {
      setLoading(true)
      const [permRes, menuRes] = await Promise.all([
        menuApi.getUserPermissions(),
        menuApi.getUserMenus(),
      ])
      setPermissions(permRes)
      setMenus(menuRes)
      setError(null)
    } catch (err) {
      setError('加载权限失败')
      console.error('Load permissions error:', err)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadPermissions()
  }, [loadPermissions])

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" tip="加载中..." />
      </div>
    )
  }

  if (error) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <div style={{ textAlign: 'center' }}>
          <p style={{ color: '#ff4d4f', fontSize: 16 }}>{error}</p>
          <button onClick={loadPermissions} style={{ padding: '8px 16px' }}>
            重试
          </button>
        </div>
      </div>
    )
  }

  // 构建动态路由
  const dynamicRoutes = buildRoutesFromMenus(menus)

  // 合并静态路由和动态路由
  const allRoutes: RouteObject[] = [
    ...staticRoutes,
    ...dynamicRoutes,
    {
      path: '*',
      element: <Navigate to="/" replace />,
    },
  ]

  return useRoutes(allRoutes)
}

/**
 * 检查路由是否需要权限
 */
export function checkRoutePermission(
  routePath: string,
  permissions: string[]
): boolean {
  // 静态路由不需要权限
  if (routePath === '/login') {
    return true
  }
  // 动态路由需要用户有对应权限
  return permissions.length > 0
}
