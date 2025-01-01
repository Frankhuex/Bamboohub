<template>
  <div class="navbar">
    <div class="navbar-left">
      <span class="site-name">竹里馆 Bamboohub</span>
    </div>
    <div class="navbar-center">
      <!-- 显示当前选择按钮，点击展开下拉菜单 -->
      <div class="nav-btn" @click="toggleDropdown">
        {{ currentSelection }}
        <span class="arrow">&#x25BC;</span>
        <!-- 向下箭头 -->
      </div>

      <!-- 下拉菜单 -->
      <div v-if="dropdownVisible" class="dropdown">
        <span class="dropdown-item" @click="getPublic">公共图书</span>
        <span class="dropdown-item" @click="getPrivate">我的图书</span>
      </div>
    </div>

    <div class="navbar-right">
      <div class="nav-btn" @click="toggleUserDropdown">
        {{ userDTO.nickname }}
        <span class="arrow">&#x25BC;</span>
        <!-- 向下箭头 -->
      </div>

      <!-- 用户下拉菜单 -->
      <div v-if="userDropdownVisible" class="dropdown">
        <span class="dropdown-item" @click="logout">登出</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { BACKEND_URL } from '@/constants'
import axios from 'axios'

const userDTO = ref({
  id: null,
  username: '',
  nickname: '',
  token: '',
})

const dropdownVisible = ref(false) // 控制图书下拉菜单显示/隐藏
const userDropdownVisible = ref(false) // 控制用户下拉菜单显示/隐藏
const currentSelection = ref('公共图书') // 当前选择的图书状态

onMounted(async () => {
  // 获取用户信息
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      router.push({
        name: 'Login',
      })
      return
    }
    let response = await axios.get(`${BACKEND_URL}/user/info`, {
      headers: {
        Authorization: localStorage.getItem('token'),
      },
    })
    console.log(response.data)
    if (response.data.success) {
      userDTO.value = response.data.data
    }
  } catch (e) {
    console.log(e)
    router.push({
      name: 'Login',
    })
  }
})

const router = useRouter()

const getPublic = () => {
  currentSelection.value = '公共图书'
  router.push({
    name: 'BookListPublic',
  })
  dropdownVisible.value = false // 点击后收起下拉菜单
}

const getPrivate = () => {
  currentSelection.value = '我的图书'
  router.push({
    name: 'BookListPrivate',
  })
  dropdownVisible.value = false // 点击后收起下拉菜单
}

const logout = () => {
  localStorage.removeItem('token')
  router.push({
    name: 'Login',
  })
  userDropdownVisible.value = false // 点击后收起用户下拉菜单
}

const toggleDropdown = () => {
  dropdownVisible.value = !dropdownVisible.value
}

const toggleUserDropdown = () => {
  userDropdownVisible.value = !userDropdownVisible.value
}
</script>

<style scoped>
/* 修改 navbar 样式 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px; /* 设置左右内边距 */
  height: 70px; /* 设置固定的高度 */
  background-color: #333; /* 设置背景色 */
  color: white; /* 设置文字颜色 */
  position: fixed; /* 固定定位在页面顶部 */
  top: 0; /* 从页面顶部开始 */
  left: 0; /* 距离左侧 0 */
  right: 0; /* 距离右侧 0 */
  z-index: 1000; /* 确保上边栏显示在其他元素上方 */
}

.navbar-left {
  font-size: 20px;
}

.navbar-center {
  display: flex;
  align-items: center;
  position: relative; /* 让下拉菜单相对于该容器定位 */
}

.nav-btn {
  margin: 0 15px;
  padding: 10px 20px; /* 设置内边距，调整按钮的大小 */
  cursor: pointer;
  background-color: #515151;
  transition:
    background-color 0.3s ease,
    color 0.3s ease;
  border-radius: 5px; /* 可选，给按钮添加圆角 */
  display: flex;
  justify-content: center;
  align-items: center;
}

.nav-btn:hover {
  background-color: #acaba8; /* 鼠标悬停时的背景色 */
  color: white; /* 鼠标悬停时的文字颜色 */
}

.arrow {
  margin-left: 5px;
}

.dropdown {
  position: absolute;
  top: 100%; /* 下拉菜单显示在按钮下方 */
  right: 0; /* 让下拉菜单靠右显示 */
  min-width: 150px; /* 设置下拉菜单的最小宽度 */
  background-color: #515151;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
  z-index: 100;
  display: flex;
  flex-direction: column; /* 垂直排列菜单项 */
  padding: 0 10px; /* 给菜单添加左右内边距 */
}

.dropdown-item {
  padding: 10px 20px;
  cursor: pointer;
  color: white;
  text-align: center;
  border-bottom: 1px solid #ccc;
  display: block;
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background-color: #acaba8; /* 鼠标悬停时的背景色 */
}

/* 确保右侧的按钮不会占满整行 */
.navbar-right {
  display: flex;
  justify-content: flex-end; /* 保持右对齐 */
  align-items: center; /* 垂直居中 */
}

.navbar-right .nav-btn {
  min-width: auto; /* 让按钮宽度自适应 */
  width: auto; /* 让按钮宽度根据内容自适应 */
  padding: 10px 20px; /* 控制按钮大小 */
  display: inline-block; /* 防止被拉伸 */
}

/* 确保用户名和登出按钮不影响其他布局 */
.nickname,
.nav-btn {
  margin: 0 10px; /* 给按钮适当的左右间距 */
}

.dropdown-item {
  min-width: auto; /* 去掉100%的宽度约束 */
  width: auto; /* 自动适应内容宽度 */
}
</style>
