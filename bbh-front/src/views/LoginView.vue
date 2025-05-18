<template>
  <div class="login-container">
    <h1>登录账号</h1>
    <div class="login">
      <form @submit.prevent="submitLogin">
        <div class="form-group">
          <label for="username">用户名:</label>
          <input v-model="username" type="text" id="title" />
        </div>
        <div class="form-group">
          <label for="password" type="password">密码:</label>
          <input v-model="password" type="password" id="title" />
        </div>

        <div class="buttons">
          <button type="submit">登录</button>
        </div>
      </form>
    </div>

    <div class="buttons">
      <br />
      <button @click="goRegister">新人注册</button>
      <button @click="goTourist">游客访问</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { BACKEND_URL } from '@/constants'
import { useRouter } from 'vue-router'
const username = ref('')
const password = ref('')

const userDTO = ref({
  id: null,
  username: '',
  nickname: '',
  token: '',
})

const router = useRouter()
const submitLogin = async () => {
  // 表单验证
  if (!username.value || !password.value) {
    alert('请填写所有字段！')
    return
  }

  try {
    const response = await axios.post(`${BACKEND_URL}/login`, {
      username: username.value,
      password: password.value,
    })
    if (response.data.success === true) {
      userDTO.value = response.data.data
      console.log(userDTO.value)
      localStorage.setItem('token', userDTO.value.token)
      console.log(localStorage.getItem('token'))
      router.push('/')
    } else {
      alert('登录失败！')
    }
  } catch (e) {
    console.log(e)
  }
}

const goRegister = () => {
  console.log('go register')
  router.push({ name: 'Register' })
}

const goTourist = () => {
  localStorage.removeItem('token')
  router.push('/')
}
</script>

<style scoped>
/* 整个页面居中 */
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0;
}

.login {
  width: 300px; /* 设置表单的固定宽度 */
  padding: 20px;
  background-color: #ccaf70;
  border: 1px solid #000;
  border-radius: 5px;
  box-shadow: 4px 4px 20px rgba(0, 0, 0, 0.2);
}

/* 每一行输入项 */
.form-group {
  display: flex;
  align-items: center; /* 使 label 和 input 垂直居中对齐 */
  margin-bottom: 15px; /* 每一行之间的间距 */
}

/* 标签样式 */
.form-group label {
  width: 120px; /* 设置标签宽度固定 */
  text-align: right; /* 标签文本右对齐 */
  margin-right: 15px; /* 标签与输入框之间的间距 */
}

/* 输入框样式 */
.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 3px;
  box-sizing: border-box;
}

/* 按钮样式 */
.buttons {
  text-align: center;
}

button {
  padding: 10px 20px;
  background-color: #f4a261;
  border: none;
  border-radius: 5px;
  color: white;
  cursor: pointer;
  font-size: 16px;
  margin-left: 10px;
  margin-right: 10px;
}

button:hover {
  background-color: #e76f51;
}
</style>
