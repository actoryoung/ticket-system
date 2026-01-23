import { defineStore } from 'pinia'
import { login, getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null')
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ADMIN',
    isHandler: (state) => ['ADMIN', 'HANDLER'].includes(state.user?.role)
  },

  actions: {
    async login(credentials) {
      try {
        const response = await login(credentials)
        if (response.code === 200) {
          this.token = response.data.token
          this.user = response.data.userInfo
          localStorage.setItem('token', this.token)
          localStorage.setItem('user', JSON.stringify(this.user))
          return { success: true }
        }
        return { success: false, message: response.message }
      } catch (error) {
        return { success: false, message: error.message || '登录失败' }
      }
    },

    async fetchUserInfo() {
      try {
        const response = await getUserInfo()
        if (response.code === 200) {
          this.user = response.data
          localStorage.setItem('user', JSON.stringify(this.user))
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    },

    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
