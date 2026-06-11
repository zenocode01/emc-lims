import request from './request'

/**
 * 登录
 */
export function login(username: string, password: string) {
  return request.post('/auth/login', { username, password })
}

/**
 * 刷新 Token
 */
export function refreshToken() {
  return request.post('/auth/refresh')
}
