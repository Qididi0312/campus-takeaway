import request from './request'                    // 统一实例：自动加 /api 前缀和 Authorization 头

/**
 * 管理端菜品列表
 */
export function getAdminDishes() {
  return request.get('/admin/dishes')
}

/**
 * 上架 / 下架单个菜品
 * status = 1 上架，status = 0 下架
 */
export function updateDishStatus(id, status) {
  return request.put(`/admin/dishes/${id}/status`, { status })
}

/**
 * 切换店铺营业状态
 * status = 1 营业，status = 0 休息
 */
export function updateShopStatus(status) {
  return request.put('/admin/shop/status', { status })
}
