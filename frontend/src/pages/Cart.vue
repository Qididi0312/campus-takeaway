<script setup>
import { useRouter } from 'vue-router'                      // 下单后跳订单页
import { ElMessage, ElMessageBox } from 'element-plus'      // 确认框与提示
import { useCartStore } from '../stores/cart'                // 购物车状态

const cart = useCartStore()
const router = useRouter()

function changeQty(item, val) {                             // el-input-number 变化时同步 store
  cart.updateQty(item.dishId, val)
}

async function submitOrder() {
  if (cart.items.length === 0) return
  // ElMessageBox.confirm 返回 Promise：确认走 then，取消走 catch
  try {
    await ElMessageBox.confirm(
      `共 ${cart.totalCount} 件，合计 ￥${cart.totalAmount.toFixed(2)}，确认提交订单？`,
      '提交订单',
      { confirmButtonText: '提交', cancelButtonText: '再想想' },
    )
  } catch (e) {
    return                                                  // 用户取消，直接结束
  }
  const orders = JSON.parse(localStorage.getItem('localOrders') || '[]')   // 本地订单列表
  orders.unshift({
    id: Date.now(),
    orderNo: 'ZS' + Date.now(),
    createdAt: new Date().toLocaleString(),
    status: 0,                                              // 0 = 待支付，与后端状态定义一致
    totalAmount: cart.totalAmount,
    items: cart.items.map((i) => ({ name: i.name, price: i.price, quantity: i.quantity })),   // 保存快照
  })
  localStorage.setItem('localOrders', JSON.stringify(orders))
  cart.clear()                                              // 下单后清空购物车
  ElMessage.success('订单已提交，请完成支付')
  router.push('/orders')
}
</script>

<template>
  <div class="page-container">
    <h2 class="title">购物车</h2>

    <el-empty v-if="cart.items.length === 0" description="购物车还是空的">
      <el-button type="primary" @click="router.push('/')">去点餐</el-button>
    </el-empty>

    <template v-else>
      <div class="list">
        <div v-for="item in cart.items" :key="item.dishId" class="row">
          <div class="name">
            <h3>{{ item.name }}</h3>
            <span class="unit">￥{{ item.price.toFixed(2) }}</span>
          </div>

          <!-- :model-value 单向绑定，避免直接改 store 数据；@change 触发时再写回 -->
          <el-input-number
            :model-value="item.quantity"
            :min="1"
            :max="item.stock"
            size="small"
            @change="(val) => changeQty(item, val)"
          />

          <span class="subtotal">￥{{ (item.price * item.quantity).toFixed(2) }}</span>

          <el-button text type="danger" @click="cart.remove(item.dishId)">删除</el-button>
        </div>
      </div>

      <div class="summary">
        <div class="summary-line">
          <span>共 {{ cart.totalCount }} 件</span>
          <span class="total">合计 ￥{{ cart.totalAmount.toFixed(2) }}</span>
        </div>
        <div class="ops">
          <el-button @click="cart.clear()">清空</el-button>
          <el-button type="primary" @click="submitOrder">提交订单</el-button>
        </div>
      </div>

      <p class="tip">当前为演示版本：订单保存在浏览器本地，后端订单接口接通后会替换为真实下单。</p>
    </template>
  </div>
</template>

<style scoped>
.title {
  margin-bottom: 16px;
}
.list {
  background: #fff;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  padding: 8px 20px;
}
.row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--border);
}
.row:last-child {
  border-bottom: none;
}
.name {
  flex: 1;
}
.unit {
  color: var(--text-3);
  font-size: 12px;
}
.subtotal {
  width: 90px;
  text-align: right;
  color: var(--primary);
  font-weight: 600;
}
.summary {
  margin-top: 16px;
  background: #fff;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}
.summary-line {
  display: flex;
  gap: 16px;
  align-items: baseline;
}
.total {
  color: var(--primary);
  font-size: 20px;
  font-weight: 700;
}
.tip {
  margin-top: 12px;
  color: var(--text-3);
  font-size: 12px;
}
</style>
