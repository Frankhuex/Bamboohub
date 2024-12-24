<template>
  <div v-if="success">
    <div class="book">
      <div class="book-info" v-if="!editing">
        <h1 class="title">
          {{ bookDTO.title }}
        </h1>
        <div class="buttons">
          <button @click="enterBook">进入小说</button>
          <button @click="editBook">编辑信息</button>
          <button @click="deleteBook">删除</button>
        </div>

        <p><br /></p>
      </div>

      <!-- 编辑模式: 显示表单 -->
      <form v-else @submit.prevent="submitEdit">
        <br /><br />
        <div>
          <label for="title">标题:</label>
          <input v-model="editedTitle" type="text" id="title" />
        </div>
        <div class="buttons">
          <button type="button" @click="cancelEdit">取消</button>
          <button type="submit">提交</button>
        </div>
      </form>
    </div>

    <slot></slot>
  </div>
  <div v-else-if="loading">Loading...</div>
  <div v-else>Failed to load...</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { BACKEND_URL } from '@/constants'
import { useRouter } from 'vue-router'

const props = defineProps(['bookId'])
const emit = defineEmits(['deletedBook'])

const bookDTO = ref({
  id: null,
  title: '',
  startParaId: null,
})

const loading = ref(true)

const success = ref(false)
const editing = ref(false) // 控制是否处于编辑模式
const editedTitle = ref('') // 存储编辑的title

onMounted(async () => {
  try {
    // 获取数据
    const response = await axios.get(`${BACKEND_URL}/book/${props.bookId}`)

    if (response.data.success) {
      console.log('Book loaded successfully!')
      bookDTO.value = response.data.data
      loading.value = false
      success.value = true
    } else {
      console.log('Failed to load book:', response.data.errorMsg)
      loading.value = false
    }
  } catch (error) {
    console.error('Error loading book:', error)
    loading.value = false
  }
})

const router = useRouter()
const enterBook = () => {
  router.push({
    name: 'BookContent',
    params: {
      bookId: bookDTO.value.id,
    },
  })
}

// 切换到编辑模式
const editBook = () => {
  editedTitle.value = bookDTO.value.title
  editing.value = true
}

// 提交编辑
const submitEdit = async () => {
  try {
    // 更新数据
    bookDTO.value.title = editedTitle.value

    // 调用 API 提交更新的数据
    const response = await axios.put(`${BACKEND_URL}/book/${bookDTO.value.id}`, {
      title: editedTitle.value,
    })

    if (response.data.success) {
      console.log('Book updated successfully!')
      console.log(response.data.data)
      editing.value = false
    } else {
      console.log('Failed to update book:', response.data.errorMsg)
    }
  } catch (error) {
    console.error('Error submitting edit:', error)
  }
}

// 取消编辑
const cancelEdit = () => {
  editing.value = false // 退出编辑模式
}

const deleteBook = async () => {
  const isConfirmed = window.confirm(`你确定要删除《${bookDTO.value.title}》这本书吗？`)
  if (isConfirmed) {
    try {
      // 调用 API 删除段落
      const response = await axios.delete(`${BACKEND_URL}/book/${bookDTO.value.id}`)

      if (response.data.success) {
        console.log('Book deleted successfully!')
        console.log(response.data.data)
        emit('deletedBook', bookDTO.value.id)
      } else {
        console.log('Failed to delete book:', response.data.errorMsg)
      }
    } catch (error) {
      console.error('Error deleting book:', error)
    }
  }
}
</script>

<style scoped>
button {
  margin: 5px;
}

form {
  display: flex;
  flex-direction: column; /* 纵向排列表单元素 */
  align-items: center; /* 水平居中表单 */
  height: 100%; /* 确保表单占满父容器的高度 */
}

form .content {
  flex-grow: 1; /* 让内容部分占据剩余的空间 */
}

form .buttons {
  display: flex;
  justify-content: center; /* 水平居中按钮 */
  gap: 10px; /* 按钮之间的间距 */
  margin-top: 20px; /* 增加与表单其他部分的间距 */
}

form input,
form textarea {
  width: 80%; /* 使输入框宽度适应 */
  max-width: 300px; /* 设置最大宽度 */
}

form div {
  margin-bottom: 10px;
}

.book {
  width: 250px; /* 宽度 */
  height: 350px; /* 高度 */
  background-color: #ccaf70; /* 牛皮纸色 */
  border: 1px solid #000000; /* 黑色边线 */
  border-radius: 5px; /* 圆角矩形 */
  box-shadow: 4px 4px 20px rgba(0, 0, 0, 0.2); /* 更强的阴影 */
  margin: 20px auto; /* 居中 */
  padding: 10px; /* 内边距 */

  display: flex; /* 使用 Flexbox 布局 */
  flex-direction: column; /* 子元素纵向排列 */
  position: relative; /* 相对定位，使按钮可以绝对定位 */
}

.book-info {
  display: flex;
  flex-direction: column; /* 纵向排列 */
  justify-content: flex-start; /* 内容顶部对齐 */
  flex-grow: 1; /* 让 book-info 区域占据剩余空间 */
  align-items: center; /* 水平居中 */
}

.title {
  position: absolute;
  top: 100px;
  align-items: center;
}

.buttons {
  position: absolute; /* 绝对定位 */
  bottom: 20px; /* 距离父容器底部 20px */
  width: 100%; /* 按钮宽度和父容器相同 */
  display: flex; /* 使用 Flexbox 居中按钮 */
  justify-content: center; /* 水平居中按钮 */
}
</style>
