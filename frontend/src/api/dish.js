import request from './request'                    // 统一实例，自动加 /api 和 Token

export function getCategories() {                   // 查分类
  return request.get('/categories')                 // 实际请求 /api/categories
}

export function getDishes(params) {                 // 查菜品
  return request.get('/dishes', { params })         // params 会被 axios 拼成 ?categoryId=1&keyword=xx
}
