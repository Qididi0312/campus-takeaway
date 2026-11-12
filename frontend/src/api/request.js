import axios from 'axios'                          // 引入 axios
import { ElMessage } from 'element-plus'           // 网络错误时统一提示，避免每个页面都写

const request = axios.create({
  baseURL: '/api',                                 // 所有请求自动加 /api，走下面的代理
  timeout: 10000,                                  // 10 秒超时，避免一直转圈
})

request.interceptors.request.use((config) => {     // 请求拦截器：发出去之前
  const token = localStorage.getItem('token')      // 从本地取 Token
  if (token) config.headers.Authorization = token  // 有就带上，后端按这个头认人
  return config                                    // 必须 return，否则请求发不出去
})

request.interceptors.response.use(
  (res) => {
    const { code, message, data } = res.data       // 后端固定结构 { code, message, data }
    if (code === 0) return data                    // 把data交给页面
    ElMessage.error(message || '请求失败')          // 业务失败统一弹提示
    return Promise.reject(new Error(message))      // 再抛出去，页面需要时还能 catch
  },
  (err) => {
    ElMessage.error(err.message || '网络异常')       // 超时、断网、5xx 等网络错误
    return Promise.reject(err)
  }
)

export default request