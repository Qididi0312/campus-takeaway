<script setup>
import { computed, reactive, ref } from 'vue'              // 表单对象与实例
import { useRoute, useRouter } from 'vue-router'           // 读取 redirect 参数、跳转
import { ElMessage } from 'element-plus'                   // 提示
import { login, register } from '../api/au'                // 登录 / 注册接口
import { useUserStore } from '../stores/user'              // 登录状态

const router = useRouter()
const route = useRoute()
const user = useUserStore()

const mode = ref('login')                                  // 'login' 登录 / 'register' 注册
const formRef = ref(null)                                  // 表单实例，用来调 validate
const loading = ref(false)                                 // 提交中状态

// 一个表单同时承载登录和注册，注册时多用到 confirm 和 phone
const form = reactive({
  username: '',
  password: '',
  confirm: '',
  phone: '',
})

const isRegister = computed(() => mode.value === 'register')

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度 3-20 位', trigger: 'blur' },   // 与后端校验保持一致
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },                  // 与后端校验保持一致
  ],
  confirm: [
    {
      // 只在注册模式下校验，登录模式没有这个输入框
      validator: (rule, value, callback) => {
        if (!isRegister.value) return callback()
        if (!value) return callback(new Error('请再次输入密码'))
        if (value !== form.password) return callback(new Error('两次输入的密码不一致'))
        callback()
      },
      trigger: 'blur',
    },
  ],
}

/** 登录和注册共用一个提交入口，按 mode 分流 */
async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    if (isRegister.value) {
      // 注册接口只认 username / password / phone，多余字段不传
      await register({ username: form.username, password: form.password, phone: form.phone })
      ElMessage.success('注册成功，请登录')
      switchMode('login')                                  // 注册完自动切回登录，用户名保留
    } else {
      const data = await login({ username: form.username, password: form.password })
      user.setLogin(data)                                  // 写入 store 与本地存储（含 role）
      ElMessage.success('登录成功')
      // 管理员不参与点餐，登录后直接进管理后台
      if (data.role === 1) router.push('/admin')
      else router.push(route.query.redirect || '/')        // 普通用户有原目标就跳回去
    }
  } catch (e) {
    // 拦截器已统一提示，这里不重复
  } finally {
    loading.value = false
  }
}

/** 切换登录 / 注册：清空校验状态，密码不残留 */
function switchMode(target) {
  mode.value = target
  form.password = ''
  form.confirm = ''
  formRef.value?.clearValidate()                           // 清掉上一种模式留下的红色错误提示
}
</script>

<template>
  <div class="login-page">
    <div class="login-box">
      <aside class="brand-side">
        <div class="brand-title">智膳</div>
        <p class="brand-sub">校园外卖点餐系统</p>
        <ul class="points">
          <li>30 分钟送达宿舍楼下</li>
          <li>分类浏览与快速搜索</li>
          <li>下单、支付、订单跟踪一体化</li>
        </ul>
      </aside>

      <main class="form-side">
        <h2>{{ isRegister ? '创建账号' : '欢迎回来' }}</h2>
        <p class="hint">{{ isRegister ? '注册后即可点餐与查看订单' : '登录后即可点餐与查看订单' }}</p>

        <!-- ref 拿到表单实例才能调 validate；:model 绑数据；:rules 绑校验规则 -->
        <el-form ref="formRef" :model="form" :rules="rules" size="large">
          <!-- prop 必须是 form 里的字段名，校验才知道管哪个输入框 -->
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" clearable />
          </el-form-item>
          <el-form-item prop="password">
            <!-- show-password 显示小眼睛可切明文；@keyup.enter 回车直接提交 -->
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              show-password
              @keyup.enter="handleSubmit"
            />
          </el-form-item>

          <!-- 注册才需要的两个输入框 -->
          <template v-if="isRegister">
            <el-form-item prop="confirm">
              <el-input
                v-model="form.confirm"
                type="password"
                placeholder="确认密码"
                show-password
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input v-model="form.phone" placeholder="手机号（选填）" clearable />
            </el-form-item>
          </template>

          <el-button type="primary" class="submit" :loading="loading" @click="handleSubmit">
            {{ isRegister ? '注册' : '登录' }}
          </el-button>
        </el-form>

        <!-- 登录 / 注册切换 -->
        <p class="switch-line">
          <template v-if="isRegister">
            已有账号？<el-link type="primary" :underline="false" @click="switchMode('login')">返回登录</el-link>
          </template>
          <template v-else>
            还没有账号？<el-link type="primary" :underline="false" @click="switchMode('register')">立即注册</el-link>
          </template>
        </p>
      </main>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fff4ec 0%, #f6f7f9 45%, #eef4fb 100%);
  padding: 20px;
}
.login-box {
  display: flex;
  width: 860px;
  max-width: 100%;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 18px 48px rgba(31, 35, 41, 0.12);
  overflow: hidden;
}
.brand-side {
  width: 360px;
  padding: 44px 36px;
  color: #fff;
  background: linear-gradient(160deg, #ff8a4c, #ff6b35 60%, #e8551f);
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.brand-title {
  font-size: 44px;
  font-weight: 700;
  letter-spacing: 4px;
}
.brand-sub {
  margin-top: 8px;
  font-size: 15px;
  opacity: 0.9;
}
.points {
  margin: 32px 0 0;
  padding: 0;
  list-style: none;
  font-size: 13px;
  opacity: 0.92;
}
.points li {
  padding: 6px 0 6px 18px;
  position: relative;
}
.points li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 14px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.85);
}
.form-side {
  flex: 1;
  padding: 48px 44px;
}
.hint {
  color: var(--text-3);
  margin: 6px 0 26px;
}
.submit {
  width: 100%;
  margin-top: 4px;
}
.switch-line {
  margin-top: 18px;
  font-size: 13px;
  color: var(--text-3);
  text-align: center;
}
@media (max-width: 720px) {
  .brand-side {
    display: none;
  }
  .form-side {
    padding: 32px 24px;
  }
}
</style>
