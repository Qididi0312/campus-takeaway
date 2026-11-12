import { defineStore } from 'pinia'                         // Pinia 的 store 工厂
import { computed, ref } from 'vue'                         // 响应式 API

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')     // 初始值从本地读，刷新不丢登录态
  const username = ref(localStorage.getItem('username') || '')
  const role = ref(Number(localStorage.getItem('role') || 0))

  const isLogin = computed(() => !!token.value)             // 有没有登录
  const isAdmin = computed(() => role.value === 1)          // 是否管理员

  function setLogin(data) {                                 // 登录成功后写入
    token.value = data.token
    username.value = data.username
    role.value = data.role
    localStorage.setItem('token', data.token)               // 持久化，刷新后仍在
    localStorage.setItem('username', data.username)
    localStorage.setItem('role', String(data.role))
  }

  function logout() {                                       // 退出登录
    token.value = ''
    username.value = ''
    role.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
  }

  return { token, username, role, isLogin, isAdmin, setLogin, logout }
})
