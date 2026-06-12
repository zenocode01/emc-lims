import request from './request'

/** 菜单项（动态菜单用） */
export interface MenuNode {
  id: number
  menuName: string
  menuType: number
  path?: string
  component?: string
  permission?: string
  parentId: number
  sort?: number
  icon?: string
  isHidden?: number
  status?: number
  children?: MenuNode[]
}

/** 当前用户信息 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar?: string
  roles: string[]
  permissions: string[]
  menus: MenuNode[]
}

/** 菜单 API */
export const menuApi = {
  /** 获取当前用户菜单树 */
  getUserMenus: () =>
    request.get<any, MenuNode[]>('/sys/menu/current-user/tree'),

  /** 获取当前用户权限码 */
  getUserPermissions: () =>
    request.get<any, string[]>('/sys/menu/current-user/permissions'),

  /** 获取当前用户信息 */
  getUserInfo: () =>
    request.get<any, UserInfo>('/sys/user/current'),
}

/**
 * 根据菜单路径映射组件
 */
export function mapComponent(componentPath: string) {
  const componentMap: Record<string, () => Promise<any>> = {
    // 系统管理
    'sys/dept/index': () => import('../pages/sys/dept'),
    'sys/menu/index': () => import('../pages/sys/menu'),
    'sys/role/index': () => import('../pages/sys/role'),
    'sys/user/index': () => import('../pages/sys/user'),
    // 业务管理
    'customer/index': () => import('../pages/customer'),
    'sample/index': () => import('../pages/sample'),
    // Dashboard
    'dashboard/index': () => import('../pages/dashboard'),
    // 登录
    'login/index': () => import('../pages/login'),
    // 编号规则
    'numbering-rule/index': () => import('../pages/numbering-rule'),
  }
  return componentMap[componentPath]
}

/**
 * 将菜单树扁平化为路由配置
 */
export function menuToRoutes(menus: MenuNode[], basicRoute: React.ComponentType = () => null) {
  const routes: any[] = []

  const traverse = (menuList: MenuNode[]) => {
    for (const menu of menuList) {
      if (menu.menuType === 1 && !menu.isHidden) {
        // 菜单类型，需要路由
        const component = mapComponent(menu.component || 'dashboard/index')
        routes.push({
          path: menu.path || `/${menu.id}`,
          element: <component />,
          children: [],
        })
      }
      if (menu.children && menu.children.length > 0) {
        traverse(menu.children)
      }
    }
  }

  traverse(menus)
  return routes
}

/**
 * 递归构建菜单树
 */
export function buildMenuTree(menus: MenuNode[]): MenuNode[] {
  const menuMap = new Map<number, MenuNode>()
  const roots: MenuNode[] = []

  for (const menu of menus) {
    menuMap.set(menu.id, { ...menu, children: [] })
  }

  for (const menu of menus) {
    const node = menuMap.get(menu.id)
    if (!node) continue

    if (menu.parentId === 0 || menu.parentId === null) {
      roots.push(node)
    } else {
      const parent = menuMap.get(menu.parentId)
      if (parent) {
        parent.children?.push(node)
      } else {
        roots.push(node)
      }
    }
  }

  return roots
}
