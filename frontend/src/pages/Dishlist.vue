<script setup>
import { ref, onMounted } from 'vue'                        // 响应式与生命周期
import { useRouter } from 'vue-router'                      // 去购物车
import { ElMessage } from 'element-plus'                    // 加购提示
import { getCategories, getDishes } from '../api/dish'       // 接口
import { getShopStatus } from '../api/shop'                  // 营业状态
import { useCartStore } from '../stores/cart'                // 购物车状态
import { useUserStore } from '../stores/user'                // 登录状态

const router = useRouter()
const cart = useCartStore()
const user = useUserStore()

const categories = ref([])                                  // 分类
const dishes = ref([])                                      // 菜品
const activeCategory = ref(0)                               // 0 = 全部
const keyword = ref('')                                     // 搜索词
const loading = ref(false)                                  // 加载遮罩
const shopOpen = ref(true)                                  // 店铺是否营业，休息时禁止下单

async function loadShopStatus() {
  try {
    const data = await getShopStatus()
    shopOpen.value = data.open                              // 后端返回 open: true/false
  } catch (e) {
    shopOpen.value = true                                   // 查询失败时不影响浏览，按营业处理
  }
}

async function loadCategories() {
  try {
    categories.value = await getCategories()
  } catch (e) {
    categories.value = []                                   // 失败给空数组，页面不崩
  }
}

async function loadDishes() {
  loading.value = true
  try {
    const params = {}
    if (activeCategory.value) params.categoryId = activeCategory.value
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    dishes.value = await getDishes(params)
  } catch (e) {
    dishes.value = []
  } finally {
    loading.value = false
  }
}

function selectCategory(id) {
  activeCategory.value = id
  loadDishes()
}

function handleAdd(dish) {
  if (!shopOpen.value) {                                    // 店铺休息时不能下单
    ElMessage.warning('店铺休息中，暂不接单')
    return
  }
  if (!user.isLogin) {                                      // 未登录先引导登录
    ElMessage.warning('请先登录再点餐')
    router.push({ path: '/login', query: { redirect: '/' } })
    return
  }
  const ok = cart.add(dish)                                 // store 会校验库存
  if (ok) ElMessage.success(`已加入：${dish.name}`)
  else ElMessage.warning('已达到库存上限')
}

onMounted(() => {
  loadShopStatus()                                          // 先查营业状态，再渲染列表
  loadCategories()
  loadDishes()
})
</script>

<template>
  <div class="page-container">
    <section class="hero">
      <div>
        <h1>今天想吃点什么？</h1>
        <p class="sub">校园外卖 · 下单后 30 分钟送达宿舍楼下</p>
      </div>
      <div class="hero-emoji">🍜</div>
    </section>

    <!-- 店铺休息时给用户明确提示，避免点了加购却没反应 -->
    <div v-if="!shopOpen" class="closed-tip">
      店铺休息中，暂时无法下单，可以先看看菜单
    </div>

    <div class="filters">
      <div class="cats">
        <!-- :class 是对象语法：条件为真才加 active 类，用来高亮当前分类 -->
        <button class="cat" :class="{ active: activeCategory === 0 }" @click="selectCategory(0)">全部</button>
        <!-- v-for 遍历分类；:key 用唯一 id 帮助 Vue 复用节点 -->
        <button
          v-for="c in categories"
          :key="c.id"
          class="cat"
          :class="{ active: activeCategory === c.id }"
          @click="selectCategory(c.id)"
        >{{ c.name }}</button>
      </div>

      <!-- clearable 带清除按钮；@keyup.enter 回车触发搜索 -->
      <el-input
        v-model="keyword"
        placeholder="搜索菜品"
        class="search"
        clearable
        @keyup.enter="loadDishes"
      >
        <!-- #append 是具名插槽，把按钮塞进输入框右侧 -->
        <template #append>
          <el-button @click="loadDishes">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- v-loading 是 Element Plus 的遮罩指令，绑定 loading 后自动显示/隐藏 -->
    <div v-loading="loading" class="grid-wrap">
      <!-- v-if 条件渲染：不在加载且没有数据时才显示空状态 -->
      <el-empty v-if="!loading && dishes.length === 0" description="没有找到菜品" />
      <div class="grid">
        <article v-for="dish in dishes" :key="dish.id" class="dish-card">
          <!-- 所有菜品统一使用 public/images/dish.png -->
          <div class="thumb">
            <img src="/images/dish.png" :alt="dish.name" />
          </div>
          <div class="info">
            <h3>{{ dish.name }}</h3>
            <p class="desc">{{ dish.description }}</p>
            <div class="meta">
              <span class="price">￥{{ Number(dish.price).toFixed(2) }}</span>
              <el-tag v-if="dish.spicy > 0" size="small" type="danger" effect="plain">辣度 {{ dish.spicy }}</el-tag>
              <span class="stock">库存 {{ dish.stock }}</span>
            </div>
            <div class="actions">
              <!-- :disabled 传布尔值：库存为 0 或店铺休息时按钮变灰不可点 -->
              <el-button type="primary" :disabled="dish.stock <= 0 || !shopOpen" @click="handleAdd(dish)">
                {{ !shopOpen ? '休息中' : (dish.stock > 0 ? '加入购物车' : '今日售罄') }}
              </el-button>
            </div>
          </div>
        </article>
      </div>
    </div>

    <!-- 购物车有东西才显示底部悬浮结算栏 -->
    <div v-if="cart.totalCount > 0" class="cart-bar">
      <span class="count">已选 {{ cart.totalCount }} 件</span>
      <span class="amount">￥{{ cart.totalAmount.toFixed(2) }}</span>
      <el-button type="primary" round @click="router.push('/cart')">去结算</el-button>
    </div>
  </div>
</template>

<style scoped>
.hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(120deg, #fff1e8, #ffe0cc);
  border-radius: var(--radius);
  padding: 28px 32px;
  margin-bottom: 24px;
}
.sub {
  color: #a35a2a;
  margin-top: 6px;
}
.hero-emoji {
  font-size: 64px;
  line-height: 1;
}
/* 休息提示条：用灰色弱化，不抢菜品卡的视觉 */
.closed-tip {
  margin-bottom: 20px;
  padding: 12px 18px;
  border-radius: var(--radius);
  background: #f4f5f7;
  border: 1px solid var(--border);
  color: var(--text-2);
  font-size: 14px;
}
.filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}
.cats {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.cat {
  border: 1px solid var(--border);
  background: #fff;
  color: var(--text-2);
  padding: 6px 16px;
  border-radius: 999px;
  cursor: pointer;
  font-size: 14px;
}
.cat.active {
  background: var(--primary);
  border-color: var(--primary);
  color: #fff;
}
.search {
  width: 260px;
}
.grid-wrap {
  min-height: 240px;
}
.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.dish-card {
  background: var(--card);
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
  transition: transform 0.2s, box-shadow 0.2s;
}
.dish-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(31, 35, 41, 0.1);
}
.thumb {
  height: 110px;
}
.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.info {
  padding: 14px 16px 16px;
}
.desc {
  color: var(--text-3);
  font-size: 13px;
  height: 40px;
  overflow: hidden;
  margin: 4px 0 8px;
}
.meta {
  display: flex;
  align-items: center;
  gap: 8px;
}
.price {
  color: var(--primary);
  font-size: 18px;
  font-weight: 700;
}
.stock {
  color: var(--text-3);
  font-size: 12px;
}
.actions {
  margin-top: 12px;
}
.actions .el-button {
  width: 100%;
}
.cart-bar {
  position: fixed;
  left: 50%;
  bottom: 24px;
  transform: translateX(-50%);
  background: #fff;
  border-radius: 999px;
  box-shadow: 0 8px 28px rgba(31, 35, 41, 0.18);
  padding: 10px 12px 10px 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  z-index: 50;
}
.count {
  color: var(--text-2);
}
.amount {
  color: var(--primary);
  font-weight: 700;
  font-size: 18px;
}
@media (max-width: 900px) {
  .grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 640px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .search {
    width: 100%;
  }
}
</style>
