/**
 * Token 管理工具
 * 负责 Token 的存储、获取、过期判断和刷新
 */

const TOKEN_KEY = 'token'
const REFRESH_TOKEN_KEY = 'refreshToken'
const TOKEN_EXPIRY_KEY = 'tokenExpiry'

/**
 * 保存 Token 和过期时间
 */
export function saveToken(accessToken: string, refreshToken: string, expiresIn: number): void {
  localStorage.setItem(TOKEN_KEY, accessToken)
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
  // 过期时间 = 当前时间 + 过期秒数（减去 60 秒提前刷新）
  const expiry = Date.now() + (expiresIn - 60) * 1000
  localStorage.setItem(TOKEN_EXPIRY_KEY, String(expiry))
}

/**
 * 获取访问令牌
 */
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 获取刷新令牌
 */
export function getRefreshToken(): string | null {
  return localStorage.getItem(REFRESH_TOKEN_KEY)
}

/**
 * 检查 Token 是否即将过期（5 分钟内）
 */
export function isTokenExpiring(): boolean {
  const expiry = localStorage.getItem(TOKEN_EXPIRY_KEY)
  if (!expiry) return true
  return Date.now() >= Number(expiry)
}

/**
 * 检查 Token 是否已过期
 */
export function isTokenExpired(): boolean {
  const expiry = localStorage.getItem(TOKEN_EXPIRY_KEY)
  if (!expiry) return true
  return Date.now() >= Number(expiry) + 60000 // 预留 1 分钟缓冲
}

/**
 * 清除所有 Token
 */
export function clearTokens(): void {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
  localStorage.removeItem(TOKEN_EXPIRY_KEY)
}

/**
 * 检查用户是否已登录
 */
export function isLoggedIn(): boolean {
  return !!getToken() && !isTokenExpired()
}
