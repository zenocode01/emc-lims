import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { message } from 'antd'
import { getToken, isTokenExpiring, clearTokens } from '../utils/token'

// 请求重试队列
const pendingRequests: Array<{
  config: InternalAxiosRequestConfig
  resolve: (value: any) => void
  reject: (reason?: any) => void
}> = []

// 刷新中标记
let isRefreshing = false
let refreshTokenPromise: Promise<string> | null = null

/**
 * Axios 实例
 */
const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

/**
 * 刷新 Token
 */
async function refreshToken(): Promise<string> {
  const refreshToken = localStorage.getItem('refreshToken')
  if (!refreshToken) {
    clearTokens()
    window.location.href = '/login'
    throw new Error('No refresh token')
  }

  try {
    const response = await axios.post('/api/auth/refresh', {
      refreshToken,
    })
    
    if (response.data.code === 200 || response.data.code === 0) {
      const { accessToken, newRefreshToken, expiresIn } = response.data.data
      localStorage.setItem('token', accessToken)
      if (newRefreshToken) {
        localStorage.setItem('refreshToken', newRefreshToken)
      }
      // 更新过期时间
      const expiry = Date.now() + (expiresIn - 60) * 1000
      localStorage.setItem('tokenExpiry', String(expiry))
      return accessToken
    } else {
      clearTokens()
      window.location.href = '/login'
      throw new Error('Token refresh failed')
    }
  } catch (error) {
    clearTokens()
    window.location.href = '/login'
    throw error
  }
}

/**
 * 请求拦截器
 */
request.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    // 从 localStorage 获取 token
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 */
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const { code, message: msg, data } = response.data

    // 业务逻辑成功
    if (code === 200 || code === 0) {
      return data
    }

    // 业务逻辑失败
    message.error(msg || '请求失败')
    return Promise.reject(new Error(msg || '请求失败'))
  },
  async (error) => {
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          // Token 过期或无效
          const originalRequest = error.config
          
          // 如果已经重试过，直接跳转登录
          if (originalRequest._retry) {
            clearTokens()
            window.location.href = '/login'
            message.error('登录已过期，请重新登录')
            return Promise.reject(error)
          }

          // 如果正在刷新 token，将请求加入队列
          if (isRefreshing) {
            return new Promise((resolve, reject) => {
              pendingRequests.push({
                config: originalRequest,
                resolve,
                reject,
              })
            })
          }

          // 开始刷新 token
          isRefreshing = true
          try {
            const newToken = await refreshToken()
            originalRequest.headers.Authorization = `Bearer ${newToken}`
            originalRequest._retry = true

            // 重试所有挂起的请求
            pendingRequests.forEach(({ config, resolve }) => {
              config.headers.Authorization = `Bearer ${newToken}`
              resolve(request(config))
            })
            pendingRequests.length = 0

            return request(originalRequest)
          } catch (refreshError) {
            pendingRequests.forEach(({ reject }) => reject(refreshError))
            pendingRequests.length = 0
            return Promise.reject(refreshError)
          } finally {
            isRefreshing = false
          }
        case 403:
          message.error('没有权限访问')
          break
        case 404:
          message.error('请求的资源不存在')
          break
        case 500:
          message.error(data?.message || '服务器错误')
          break
        default:
          message.error(data?.message || '请求失败')
      }
    } else {
      message.error('网络异常，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
