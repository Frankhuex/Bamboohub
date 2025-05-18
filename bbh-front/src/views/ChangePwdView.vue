<template>
  <div class="register-container">
    <h1>修改密码</h1>
    <div class="register">
      <form @submit.prevent="submitChangePwd">
        <div class="form-group">
          <label for="oldPwd" type="password">旧密码:</label>
          <input v-model="oldPwd" type="password" id="oldPwd" />
        </div>
        <div class="form-group">
          <label for="password" type="password">新密码:</label>
          <input v-model="newPwd" type="password" id="newPwd" />
        </div>
        <div class="form-group">
          <label for="newPwd2" type="password">再次输新密码:</label>
          <input v-model="newPwd2" type="password" id="newPwd2" />
        </div>
        <div class="buttons">
          <button type="submit">修改密码</button>
        </div>
      </form>
    </div>
    <div class="buttons">
      <br />
      <button @click="router.back()">返回</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { BACKEND_URL } from '@/constants'
import { useRouter } from 'vue-router'
const oldPwd = ref('')
const newPwd = ref('')
const newPwd2 = ref('')

const userDTO = ref({
  id: null,
  username: '',
  nickname: '',
  token: '',
})

const router = useRouter()
const submitChangePwd = async () => {
  // 表单验证
  if (!oldPwd.value || !newPwd.value || !newPwd2.value) {
    alert('请填写所有字段！')
    return
  }

  // 密码验证
  if (newPwd.value !== newPwd2.value) {
    alert('两次输入的密码不一致')
    return
  }

  if (newPwd.value === oldPwd.value) {
    alert('新旧密码不能相同')
    return
  }

  try {
    const response = await axios.post(
      `${BACKEND_URL}/changepwd`,
      {
        oldPassword: oldPwd.value,
        newPassword: newPwd.value,
      },
      {
        headers: {
          Authorization: localStorage.getItem('token'),
        },
      },
    )
    if (response.data.success === true) {
      userDTO.value = response.data.data
      console.log(userDTO.value)
      localStorage.setItem('token', userDTO.value.token)
      console.log(localStorage.getItem('token'))
      alert('密码修改成功！')
      router.push('/')
    } else {
      alert('注册失败！')
    }
  } catch (e) {
    console.log(e)
  }
}
</script>

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
