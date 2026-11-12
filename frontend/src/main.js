import { createApp } from 'vue'                    // 创建应用
import { createPinia } from 'pinia'                // 状态管理
import ElementPlus from 'element-plus'             // 组件库
import 'element-plus/dist/index.css'               // 组件库样式，漏了组件就没样子
import './style.css'                               // 项目全局样式
import App from './App.vue'                        // 根组件
import router from './router'                      // 路由

const app = createApp(App)
app.use(createPinia())                             // 先 Pinia，路由守卫里可能要读 store
app.use(router)                                    // 再路由
app.use(ElementPlus)                               // 再组件库
app.mount('#app')                                  // 挂到 index.html 的 #app