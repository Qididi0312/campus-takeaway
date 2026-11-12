<script setup>
import { ref, onMounted } from 'vue'                        // 列表与生命周期
import { useRouter } from 'vue-router'                      // 空状态去点餐
import { ElMessage } from 'element-plus'                    // 操作提示

const router = useRouter()
const orders = ref([])                                      // 本地订单

const statusMap = {                                         // 订单状态与后端口径一致
  0: { text: '待支付', type: 'warning' },
  1: { text: '制作中', type: 'primary' },
  2: { text: '配送中', type: 'info' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已取消', type: 'info' },
}

function load() {
  orders.value = JSON.parse(localStorage.getItem('localOrders') || '[]')
}

function save() {                                           // 状态变更后写回本地
  localStorage.setItem('localOrders', JSON.stringify(orders.value))
}

function pay(order) {                                       // 模拟支付：待支付 → 制作中
  order.status = 1
  save()
  ElMessage.success('支付成功，商家开始制作')
}

function cancel(order) {                                    // 取消：仅待支付/制作中可取消
  order.status = 4
  save()
  ElMessage.success('订单已取消')
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <h2 class="title">我的订单</h2>

    <el-empty v-if="orders.length === 0" description="还没有订单">
      <el-button type="primary" @click="router.push('/')">去点餐</el-button>
    </el-empty>

    <div v-for="order in orders" :key="order.id" class="order-card">
      <div class="head">
        <div>
          <span class="no">订单号 {{ order.orderNo }}</span>
          <span class="time">{{ order.createdAt }}</span>
        </div>
        <!-- :type 根据状态取不同颜色，effect="light" 是浅色底 -->
        <el-tag :type="statusMap[order.status].type" effect="light">{{ statusMap[order.status].text }}</el-tag>
      </div>

      <div class="items">
        <div v-for="(item, idx) in order.items" :key="idx" class="item">
          <span>{{ item.name }} × {{ item.quantity }}</span>
          <span>￥{{ (item.price * item.quantity).toFixed(2) }}</span>
        </div>
      </div>

      <div class="foot">
        <span class="total">合计 ￥{{ order.totalAmount.toFixed(2) }}</span>
        <div class="ops">
          <!-- v-if 按状态决定能点哪些按钮，避免取消已完成的订单 -->
          <el-button v-if="order.status === 0" size="small" @click="cancel(order)">取消订单</el-button>
          <el-button v-if="order.status === 0" type="primary" size="small" @click="pay(order)">模拟支付</el-button>
          <el-button v-if="order.status === 1" size="small" @click="cancel(order)">取消订单</el-button>
          <el-button v-if="order.status === 1" disabled size="small">等待商家配送</el-button>
        </div>
      </div>
    </div>

    <p v-if="orders.length" class="tip">当前为演示版本：订单数据保存在浏览器本地。</p>
  </div>
</template>

<style scoped>
.title {
  margin-bottom: 16px;
}
.order-card {
  background: #fff;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  padding: 16px 20px;
  margin-bottom: 16px;
}
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}
.no {
  font-weight: 600;
  margin-right: 12px;
}
.time {
  color: var(--text-3);
  font-size: 12px;
}
.items {
  padding: 12px 0;
}
.item {
  display: flex;
  justify-content: space-between;
  color: var(--text-2);
  padding: 4px 0;
}
.foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid var(--border);
  padding-top: 12px;
}
.total {
  color: var(--primary);
  font-weight: 700;
  font-size: 16px;
}
.ops {
  display: flex;
  gap: 8px;
}
.tip {
  color: var(--text-3);
  font-size: 12px;
}
</style>
