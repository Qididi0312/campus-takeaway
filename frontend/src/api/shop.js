import request from './request'                    // 统一实例，自动加 /api 和 Token

/**
 * 查询店铺是否营业（公开接口，未登录也能查）
 * 返回 { status: 1, open: true, text: '营业中' }
 */
export function getShopStatus() {
  return request.get('/shop/status')
}
