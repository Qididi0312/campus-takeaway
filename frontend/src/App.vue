<script setup>
import { computed } from 'vue'                              // 计算属性
import { useRoute } from 'vue-router'                       // 当前路由信息
import AppHeader from './components/AppHeader.vue'           // 顶部导航

const route = useRoute()
const showHeader = computed(() => route.meta.hideHeader !== true)   // 登录页隐藏导航
</script>

<template>
  <AppHeader v-if="showHeader" />
  <router-view />
</template>
