<script setup>
import { ref, onMounted } from 'vue'                              // 响应式与生命周期
import { ElMessage } from 'element-plus'                          // 操作提示
import { getAdminDishes, updateDishStatus, updateShopStatus } from '../api/admin'   // 管理端接口
import { getShopStatus } from '../api/shop'                        // 营业状态

const dishes = ref([])                                             // 全部菜品
const loading = ref(false)                                         // 表格加载遮罩
const shopOpen = ref(true)                                         // 当前是否营业
const shopLoading = ref(false)                                     // 营业开关请求中，防止连点

/**
 * 加载菜品列表
 * 管理员 /api/admin/dishes，status = 0
 * 用户端 /api/dishes
 */
async function loadDishes() {
  loading.value = true
  try {
    dishes.value = await getAdminDishes()
  } catch (e) {
    dishes.value = []                                              // 失败给空数组，页面不崩
  } finally {
    loading.value = false
  }
}

/** 加载店铺营业状态 */
async function loadShopStatus() {
  try {
    const data = await getShopStatus()
    shopOpen.value = data.open                                    // 后端返回 open: true/false
  } catch (e) {
    shopOpen.value = false                                        // 查不到时按休息处理更安全
  }
}

/**
 * 上架 / 下架
 * el-switch 的 @change 会把新值传进来，binding 取反就是目标状态
 * 这里用 try/catch 是为了失败时把开关拨回去，避免界面和后端不一致
 */
async function handleDishSwitch(dish, value) {
  const status = value ? 1 : 0
  try {
    const msg = await updateDishStatus(dish.id, status)            // 后端返回中文提示
    dish.status = status                                           // 成功才改本地状态
    ElMessage.success(msg)
  } catch (e) {
    dish.status = status === 1 ? 0 : 1                             // 回滚，让开关复位
  }
}

/** 营业 / 休息切换 */
async function handleShopSwitch(value) {
  const status = value ? 1 : 0
  shopLoading.value = true
  try {
    const msg = await updateShopStatus(status)
    shopOpen.value = status === 1
    ElMessage.success(msg)
  } catch (e) {
    shopOpen.value = status !== 1                                  // 失败复位
  } finally {
    shopLoading.value = false
  }
}

onMounted(() => {
  loadDishes()
  loadShopStatus()
})
</script>

<template>
  <div class="admin-page">
    <div class="page-head">
      <div>
        <h1>管理后台</h1>
        <p class="sub">控制店铺营业状态与菜品上架情况</p>
      </div>
    </div>

    <!-- 营业状态卡片 -->
    <section class="shop-card" :class="{ closed: !shopOpen }">
      <div class="shop-info">
        <div class="shop-title">
          <span class="dot" :class="{ off: !shopOpen }"></span>
          <span>{{ shopOpen ? '营业中' : '休息中' }}</span>
        </div>
        <p class="shop-tip">
          {{ shopOpen ? '用户当前可以正常浏览并下单' : '用户仍可浏览菜品，但无法加入购物车下单' }}
        </p>
      </div>
      <!-- v-model 绑 shopOpen，@change 里发请求存到后端 -->
      <el-switch
        v-model="shopOpen"
        :loading="shopLoading"
        size="large"
        active-text="营业"
        inactive-text="休息"
        @change="handleShopSwitch"
      />
    </section>

    <!-- 菜品上下架 -->
    <section class="panel">
      <div class="panel-head">
        <h2>菜品管理</h2>
        <span class="count">共 {{ dishes.length }} 个菜品</span>
      </div>

      <el-table v-loading="loading" :data="dishes" stripe style="width: 100%">
        <el-table-column prop="name" label="菜品" min-width="160" />
        <el-table-column label="价格" width="110">
          <template #default="{ row }">
            ￥{{ Number(row.price).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="90" />
        <el-table-column label="状态" width="150">
          <template #default="{ row }">
            <!-- :model-value 只读绑定当前状态，变化时调 handleDishSwitch 发请求 -->
            <el-switch
              :model-value="row.status === 1"
              active-text="上架"
              inactive-text="下架"
              @change="(v) => handleDishSwitch(row, v)"
            />
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<style scoped>
.admin-page {
  max-width: 1120px;                          /* 与导航栏同宽，视觉对齐 */
  margin: 0 auto;
  padding: 28px 20px 60px;
}
.page-head h1 {
  margin: 0;
  font-size: 26px;
}
.sub {
  margin: 6px 0 0;
  color: var(--text-3);
  font-size: 14px;
}

/* 营业状态卡片：营业时橙色，休息时灰色，颜色本身就是状态提示 */
.shop-card {
  margin-top: 20px;
  padding: 22px 26px;
  border-radius: var(--radius);
  background: linear-gradient(135deg, #fff1e8, #ffe3d2);
  border: 1px solid #ffd0b5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}
.shop-card.closed {
  background: #f4f5f7;
  border-color: var(--border);
}
.shop-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 700;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #22c55e;                        /* 绿色小圆点表示正在营业 */
}
.dot.off {
  background: #9aa0a6;
}
.shop-tip {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--text-3);
}

.panel {
  margin-top: 20px;
  padding: 20px 22px 6px;
  background: var(--card);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
}
.panel-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 12px;
}
.panel-head h2 {
  margin: 0;
  font-size: 17px;
}
.count {
  font-size: 13px;
  color: var(--text-3);
}

@media (max-width: 720px) {
  .shop-card {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
