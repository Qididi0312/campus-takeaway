import { createRouter, createWebHistory } from 'vue-router'    // 路由工厂 + History 模式
import Login from '../pages/Login.vue'                         // 登录页
import Dishlist from '../pages/Dishlist.vue'                   // 菜品列表
import Cart from '../pages/Cart.vue'                           // 购物车
import Orders from '../pages/Orders.vue'                       // 订单列表
import Admin from '../pages/Admin.vue'                         // 管理后台

// 管理员不参与点餐，这些页面禁止管理员进入
const DINER_PATHS = ['/', '/cart', '/orders']

const routes = [
  { path: '/login', name: 'login', component: Login, meta: { hideHeader: true } },   // 登录页不显示导航
  { path: '/', name: 'dish-list', component: Dishlist },                             // 首页可匿名浏览
  { path: '/cart', name: 'cart', component: Cart, meta: { requiresAuth: true } },    // 购物车需登录
  { path: '/orders', name: 'orders', component: Orders, meta: { requiresAuth: true } }, // 订单需登录
  { path: '/admin', name: 'admin', component: Admin, meta: { requiresAdmin: true } }, // 管理后台仅管理员
]

const router = createRouter({
  history: createWebHistory(),                             // 地址栏无 #
  routes,
})

router.beforeEach((to) => {                                // 全局前置守卫
  // 守卫在组件外执行，拿不到 store 实例，所以直接读本地存储
  const token = localStorage.getItem('token')
  const role = Number(localStorage.getItem('role') || 0)

  // 一、管理后台：必须已登录且是管理员
  if (to.meta.requiresAdmin) {
    if (!token) return { path: '/login', query: { redirect: to.fullPath } }   // 没登录先去登录
    if (role !== 1) return { path: '/' }                                     // 普通用户不能访问管理后台
  }

  // 二、管理员自动回管理后台
  if (role === 1 && DINER_PATHS.includes(to.path)) {
    return { path: '/admin' }
  }

  // 三、普通用户访问需要登录的页面
  if (to.meta.requiresAuth && !token) {
    return { path: '/login', query: { redirect: to.fullPath } }   // 记住原目标，登录后跳回
  }

  // 四、已登录状态不能去登录页
  if (to.path === '/login' && token) {
    return { path: role === 1 ? '/admin' : '/' }
  }

  return true                                              // 放行
})

export default router