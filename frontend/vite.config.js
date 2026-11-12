import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {                        // 前端请求 /api/** 全部转发给后端
        target: 'http://localhost:8080', // 后端端口
        changeOrigin: true,            // 把 Host 头改成后端地址，避免部分后端校验失败
      },
    },
  },
})
