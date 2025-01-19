<template>
  <div class="member-list">
    <!-- 关闭按钮 -->
    <button class="close-btn" @click="closeWindow">×</button>

    <!-- 显示加载状态 -->
    <div v-if="loading">加载中...</div>

    <!-- 显示角色表格 -->
    <div v-else>
      <div v-if="showForm === IDLE">
        <div v-if="roles">
          <h1>参与成员</h1>
          <!-- 合并角色信息为一列 -->
          <div class="table-container">
            <table>
              <thead>
                <tr>
                  <th>用户名</th>
                  <th>昵称</th>
                  <th>权限</th>
                  <th v-if="canAdmin">操作</th>
                </tr>
              </thead>
              <tbody>
                <!-- 书主 -->
                <tr v-if="roles.owner">
                  <td>{{ roles.owner.username }}</td>
                  <td>{{ roles.owner.nickname }}</td>
                  <td>书主</td>
                  <td v-if="canAdmin"></td>
                </tr>

                <!-- 管理员 -->
                <tr v-for="(admin, index) in roles.admins" :key="index">
                  <td>{{ admin.username }}</td>
                  <td>{{ admin.nickname }}</td>
                  <td>管理员</td>
                  <td v-if="ownRoleType === 'OWNER'">
                    <button class="edit-btn" @click="showEditRoleForm(admin.username, 'ADMIN')">
                      编辑
                    </button>
                    <button class="kick-btn" @click="kickMember(admin.username)">踢出</button>
                  </td>
                  <td v-else-if="canAdmin"></td>
                </tr>

                <!-- 编辑者 -->
                <tr v-for="(editor, index) in roles.editors" :key="index">
                  <td>{{ editor.username }}</td>
                  <td>{{ editor.nickname }}</td>
                  <td>编辑者</td>
                  <td v-if="ownRoleType === 'OWNER' || ownRoleType === 'ADMIN'">
                    <button class="edit-btn" @click="showEditRoleForm(editor.username, 'EDITOR')">
                      编辑
                    </button>
                    <button class="kick-btn" @click="kickMember(editor.username)">踢出</button>
                  </td>
                  <td v-else-if="canAdmin"></td>
                </tr>

                <!-- 查看者 -->
                <tr v-for="(viewer, index) in roles.viewers" :key="index">
                  <td>{{ viewer.username }}</td>
                  <td>{{ viewer.nickname }}</td>
                  <td>查看者</td>
                  <td v-if="ownRoleType === 'OWNER' || ownRoleType === 'ADMIN'">
                    <button class="edit-btn" @click="showEditRoleForm(viewer.username, 'VIEWER')">
                      编辑
                    </button>
                    <button class="kick-btn" @click="kickMember(viewer.username)">踢出</button>
                  </td>
                  <td v-else-if="canAdmin"></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div v-else>
          <p>没有角色信息。</p>
        </div>

        <!-- 邀请用户按钮 -->
        <button
          v-if="ownRoleType === 'OWNER' || ownRoleType === 'ADMIN'"
          class="btn"
          @click="showInviteForm"
        >
          邀请用户
        </button>
      </div>

      <!-- 邀请表单 -->
      <div v-else-if="showForm === INVITING">
        <h1>邀请用户</h1>
        <form @submit.prevent="submitRoleForm">
          <label for="username">用户名:</label>
          <input type="text" id="username" v-model="roleRequest.username" required />

          <label for="roleType">权限:</label>
          <div>
            <input type="radio" id="viewer" value="VIEWER" v-model="roleRequest.roleType" />
            <label for="viewer">查看者</label>

            <input type="radio" id="editor" value="EDITOR" v-model="roleRequest.roleType" />
            <label for="editor">编辑者</label>

            <input type="radio" id="admin" value="ADMIN" v-model="roleRequest.roleType" />
            <label for="admin">管理员</label>
          </div>

          <button type="submit">提交</button>
          <button @click="cancelForm">取消</button>
        </form>
      </div>

      <!-- 编辑权限表单 -->
      <div v-else-if="showForm === EDITINGROLE">
        <h1>编辑权限</h1>
        <form @submit.prevent="submitRoleForm">
          <label for="username">用户名: {{ roleRequest.username }}</label>

          <label for="roleType">权限:</label>
          <div>
            <input type="radio" id="viewer" value="VIEWER" v-model="roleRequest.roleType" />
            <label for="viewer">查看者</label>

            <input type="radio" id="editor" value="EDITOR" v-model="roleRequest.roleType" />
            <label for="editor">编辑者</label>

            <input type="radio" id="admin" value="ADMIN" v-model="roleRequest.roleType" />
            <label for="admin">管理员</label>
          </div>

          <button type="submit">提交</button>
          <button @click="cancelForm">取消</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { BACKEND_URL } from '@/constants'

// 接受 bookId 作为 props
const props = defineProps({
  bookId: {
    type: Number,
    required: true,
  },
})
const emit = defineEmits(['closeWindow'])

const roles = ref(null) // 存储角色数据
const loading = ref(true) // 加载状态

const IDLE = 0
const INVITING = 1
const EDITINGROLE = 2
const showForm = ref(IDLE) // 控制显示角色表单

const roleRequest = ref({
  username: '',
  bookId: props.bookId,
  roleType: '', // 初始化角色为空
}) // 表单数据

const ownRoleType = ref('')
const hasLogined = ref(false)
const canAdmin = ref(false)
// 获取角色数据
onMounted(async () => {
  try {
    let token = ''
    if (localStorage.getItem('token')) {
      token = localStorage.getItem('token') // 获取 token
      hasLogined.value = true
    }
    let response = await axios.get(`${BACKEND_URL}/book/${props.bookId}/roles`, {
      headers: {
        Authorization: token,
      },
    })
    roles.value = response.data.data

    response = await axios.get(`${BACKEND_URL}/book/${props.bookId}/role`, {
      headers: {
        Authorization: token,
      },
    })
    ownRoleType.value = response.data.data
    if (ownRoleType.value === 'ADMIN' || ownRoleType.value === 'OWNER') {
      canAdmin.value = true
    }
  } catch (error) {
    console.error('Error fetching roles:', error)
  } finally {
    loading.value = false
  }
})

// 切换到邀请用户表单
const showInviteForm = () => {
  showForm.value = INVITING
}

const showEditRoleForm = (username, roleType) => {
  showForm.value = EDITINGROLE
  roleRequest.value.username = username
  roleRequest.value.roleType = roleType
}
// 取消邀请，恢复显示角色表格
const cancelForm = () => {
  showForm.value = IDLE
  roleRequest.value.username = ''
  roleRequest.value.roleType = ''
}

// 提交表单
const submitRoleForm = async () => {
  try {
    const token = localStorage.getItem('token')

    const response = await axios.put(`${BACKEND_URL}/role`, roleRequest.value, {
      headers: {
        Authorization: token,
      },
    })
    if (response.data.success === true) {
      console.log('Role updated:', response.data)
      // 提交成功后，重新加载角色数据并关闭表单
      await loadRoles()
      showForm.value = IDLE
    } else {
      alert('用户名不存在。')
    }
  } catch (error) {
    alert('操作失败。')
    console.error('Error updating role:', error)
  }
}

const kickMember = async (username) => {
  try {
    const isConfirmed = window.confirm(`你确定要踢出用户“ ${username}”吗？`)
    if (!isConfirmed) {
      return
    }
    const token = localStorage.getItem('token')
    const response = await axios.delete(`${BACKEND_URL}/book/${props.bookId}/role/${username}`, {
      headers: {
        Authorization: token,
      },
    })
    if (response.data.success === true) {
      console.log('Member kicked:', response.data)
      // 踢出成功后，重新加载角色数据
      await loadRoles()
    } else {
      alert('踢出失败。')
    }
  } catch (error) {
    alert('踢出失败。')
    console.error('Error kicking member:', error)
  }
}

// 关闭悬浮窗
const closeWindow = () => {
  emit('closeWindow')
  console.log('Close window clicked')
}

// 重新加载角色数据
const loadRoles = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await axios.get(`${BACKEND_URL}/book/${props.bookId}/roles`, {
      headers: {
        Authorization: token,
      },
    })
    roles.value = response.data.data
  } catch (error) {
    console.error('Error fetching roles:', error)
  }
}
</script>

<style scoped>
.member-list1 {
  display: flex;
  flex-direction: column;
  align-items: center; /* 水平居中 */
  text-align: left; /* 文本左对齐 */
  position: fixed; /* 固定定位 */
  top: 50%; /* 距离页面顶部 50% */
  left: 50%; /* 距离页面左侧 50% */
  transform: translate(-50%, -50%); /* 通过 translate 将组件中心点移到页面中心 */
  z-index: 1000; /* 确保它在其他元素之上 */
  background-color: rgba(255, 255, 255, 0.8); /* 半透明白色背景 */
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.2); /* 添加阴影，使组件更突出 */
  padding: 20px; /* 添加内边距 */
  width: 350px; /* 设置组件宽度 */
  border-radius: 8px; /* 圆角边框 */
  max-height: 80vh; /* 最大高度为页面高度的80% */
}

.member-list {
  display: flex;
  flex-direction: column; /* 子元素纵向排列 */
  align-items: center;
  text-align: left;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
  background-color: rgba(255, 255, 255, 0.8);
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.2);
  padding: 20px;
  width: 90%;
  max-width: 450px;
  border-radius: 8px;
  max-height: 90vh;
  overflow: hidden;
}

.close-btn {
  position: absolute; /* 绝对定位 */
  top: 10px; /* 距离顶部 10px */
  right: 10px; /* 距离右边 10px */
  background: none; /* 去掉按钮背景 */
  border: none; /* 去掉按钮边框 */
  font-size: 24px; /* 字体大小 */
  color: black; /* 字体颜色为黑色 */
  cursor: pointer; /* 鼠标悬停时显示为指针 */
}

button {
  margin-top: 10px;
  padding: 8px 10px;
  margin: 2px;
  background-color: #007bff;
  color: black; /* 字体颜色为黑色 */
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}

form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

label {
  margin-bottom: 5px;
  color: black; /* 字体颜色为黑色 */
}

input[type='text'],
input[type='radio'] {
  padding: 8px;
  margin-bottom: 10px;
}

/* .table-container {
  max-height: 400px;
  overflow-y: auto;
  margin-top: 20px;
} */
.table-container {
  flex: 1; /* 占据剩余的可用空间 */
  width: 100%;
  overflow-y: auto; /* 表格在容器内滚动 */
  max-height: calc(100vh - 200px);
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
  border: 2px solid black;
  table-layout: fixed;
}

table th,
table td {
  padding: 8px;
  text-align: left;
  border: 1px solid black;
  background-color: transparent;
  color: black; /* 字体颜色为黑色 */
}

table th {
  background-color: transparent;
}

table tbody tr:nth-child(even) {
  background-color: transparent;
}

table tbody tr:hover {
  background-color: transparent;
}

h1 {
  color: black; /* 字体颜色为黑色 */
  text-align: center; /* 文本居中 */
}
h3 {
  margin-top: 20px;
  font-size: 18px;
  text-decoration: underline;
  color: black; /* 字体颜色为黑色 */
}
</style>
