import request from '@/utils/request'

/**
 * 用户登录
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/auth/user-info',
    method: 'get'
  })
}

/**
 * 用户退出登录
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}
