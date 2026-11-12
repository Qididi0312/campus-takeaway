import { defineStore } from 'pinia'                         // Pinia 的 store 工厂
import { computed, ref, watch } from 'vue'                  // watch 用于同步到本地存储

const STORAGE_KEY = 'cart'                                  // localStorage 键名

export const useCartStore = defineStore('cart', () => {
  const items = ref(JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]'))   // 刷新后恢复购物车

  watch(items, (val) => {                                   // 任何变动都写回本地
    localStorage.setItem(STORAGE_KEY, JSON.stringify(val))
  }, { deep: true })                                        // deep 才能监测数组元素变化

  const totalCount = computed(() => items.value.reduce((sum, i) => sum + i.quantity, 0))   // 总件数
  const totalAmount = computed(() => items.value.reduce((sum, i) => sum + i.price * i.quantity, 0))   // 总金额

  function add(dish) {                                      // 加入购物车
    const found = items.value.find((i) => i.dishId === dish.id)
    if (found) {                                            // 已有就加数量
      if (found.quantity >= dish.stock) return false        // 超过库存不再加
      found.quantity += 1
      return true
    }
    items.value.push({                                      // 没有就新增一条
      dishId: dish.id,
      name: dish.name,
      price: Number(dish.price),
      stock: dish.stock,
      quantity: 1,
    })
    return true
  }

  function updateQty(dishId, qty) {                         // 修改数量
    const found = items.value.find((i) => i.dishId === dishId)
    if (!found) return
    found.quantity = Math.min(Math.max(1, qty), found.stock) // 限制在 1 到库存之间
  }

  function remove(dishId) {                                 // 删除某项
    items.value = items.value.filter((i) => i.dishId !== dishId)
  }

  function clear() {                                        // 清空购物车
    items.value = []
  }

  return { items, totalCount, totalAmount, add, updateQty, remove, clear }
})
