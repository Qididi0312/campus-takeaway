<script setup>
import { useRouter } from 'vue-router'                      // 跳转用
import { ElMessage } from 'element-plus'                    // 退出提示
import { useUserStore } from '../stores/user'                // 登录状态
import { useCartStore } from '../stores/cart'                // 购物车数量

const router = useRouter()
const user = useUserStore()
const cart = useCartStore()

function goCart() {                                         // 查看登录状态，根据登录状态跳转
  router.push(user.isLogin ? '/cart' : '/login')
}

function handleLogout() {
  user.logout()                                             // 清空状态和本地存储
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<template>
  <header class="header">
    <div class="header-inner">
      <div class="brand" @click="router.push(user.isAdmin ? '/admin' : '/')">   <!-- 点 logo 回各自首页 -->
        <span class="logo">智膳</span>
        <span class="slogan">校园外卖</span>
      </div>

      <!-- 管理员和普通用户两套导航：管理员不参与点餐 -->
      <nav class="nav">
        <template v-if="user.isAdmin">
          <router-link to="/admin" class="nav-item" active-class="active">管理后台</router-link>
        </template>
        <template v-else>
          <router-link to="/" class="nav-item" active-class="active">点餐</router-link>   <!-- active-class 自动高亮当前页 -->
          <router-link to="/orders" class="nav-item" active-class="active">我的订单</router-link>
        </template>
      </nav>

      <div class="right">
        <!-- 购物车只对普通用户展示 -->
        <el-badge v-if="!user.isAdmin" :value="cart.totalCount" :hidden="cart.totalCount === 0" class="cart-badge">   <!-- 角标显示件数 -->
          <el-button text circle @click="goCart">🛒</el-button>
        </el-badge>
        <template v-if="user.isLogin">
          <span class="user">{{ user.username }}</span>
          <el-button text @click="handleLogout">退出</el-button>
        </template>
        <el-button v-else type="primary" plain size="small" @click="router.push('/login')">登录</el-button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.header {
  position: sticky;                                         /* 吸顶，滚动时导航始终可见 */
  top: 0;
  z-index: 100;
  background: #fff;
  border-bottom: 1px solid var(--border);
}
.header-inner {
  max-width: 1120px;                                        /* 与页面内容同宽 */
  margin: 0 auto;
  height: 60px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 32px;
}
.brand {
  display: flex;
  align-items: baseline;
  gap: 6px;
  cursor: pointer;
}
.logo {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary);
  letter-spacing: 1px;
}
.slogan {
  font-size: 12px;
  color: var(--text-3);
}
.nav {
  display: flex;
  gap: 24px;
  flex: 1;                                                  /* 占中间，把右侧推到底 */
}
.nav-item {
  color: var(--text-2);
  text-decoration: none;
  font-size: 15px;
  padding: 4px 0;
  border-bottom: 2px solid transparent;                     /* 预留高亮位置，避免跳动 */
}
.nav-item.active {
  color: var(--primary);
  border-bottom-color: var(--primary);                      /* 当前页下划线 */
  font-weight: 600;
}
.right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.user {
  color: var(--text-2);
  max-width: 120px;
  overflow: hidden;                                         /* 名字过长省略 */
  text-overflow: ellipsis;
  white-space: nowrap;
}
.cart-badge {
  margin-right: 8px;
}
</style>
