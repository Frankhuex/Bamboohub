<template>
  <div class="register-container">
    <h1>注册账号</h1>
    <div class="register">
      <form @submit.prevent="submitRegister">
        <div class="form-group">
          <label for="username">创建用户名:</label>
          <input v-model="username" type="text" id="username" />
        </div>
        <div class="form-group">
          <label for="password" type="password">创建密码:</label>
          <input v-model="password" type="password" id="password" />
        </div>
        <div class="form-group">
          <label for="password2" type="password">再次输密码:</label>
          <input v-model="password2" type="password" id="password2" />
        </div>
        <div class="form-group">
          <label for="nickname">昵称:</label>
          <input v-model="nickname" type="text" id="nickname" />
        </div>

        <div class="buttons">
          <button type="submit">注册</button>
        </div>
      </form>
    </div>
    <div class="buttons">
      <br />
      <button @click="goLogin">用户登录</button>
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
const password2 = ref('')
const nickname = ref('')

const userDTO = ref({
  id: null,
  username: '',
  nickname: '',
  token: '',
})

const router = useRouter()
const submitRegister = async () => {
  // 表单验证
  if (!username.value || !password.value || !nickname.value) {
    alert('请填写所有字段！')
    return
  }

  // 密码验证
  if (password.value !== password2.value) {
    alert('两次输入的密码不一致')
    return
  }

  try {
    const response = await axios.post(`${BACKEND_URL}/register`, {
      username: username.value,
      password: password.value,
      nickname: nickname.value,
    })
    if (response.data.success === true) {
      userDTO.value = response.data.data
      console.log(userDTO.value)
      localStorage.setItem('token', userDTO.value.token)
      console.log(localStorage.getItem('token'))
      router.push('/')
    } else {
      alert('注册失败！')
    }
  } catch (e) {
    console.log(e)
  }
}

const goLogin = () => {
  router.push({
    name: 'Login',
  })
}

const goTourist = () => {
  localStorage.removeItem('token')
  router.push('/')
}
</script>

<!-- <style scoped>
/* 整个页面居中 */
.register-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh; /* 使其垂直居中 */
}

.register {
  width: 400px; /* 设置表单的固定宽度 */
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
</style> -->

<style scoped>
/* 整个页面居中 */
.register-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0;
}

.register {
  width: 350px; /* 设置表单的固定宽度 */
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
