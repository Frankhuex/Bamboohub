<template>
  <div class="main-info">
    <h1>小说接龙</h1>

    <div class="buttons" v-if="!creatingBook">
      <button @click="handleCreateBook">新建小说</button>
    </div>
    <!-- 编辑模式: 显示表单 -->
    <form v-else @submit.prevent="submitCreate">
      <br /><br />
      <div>
        <label for="title">标题:</label>
        <input v-model="editedTitle" type="text" id="title" />
      </div>
      <div class="buttons">
        <button type="button" @click="cancelCreate">取消</button>
        <button type="submit">提交</button>
      </div>
    </form>
  </div>
  <div class="books">
    <BookCell
      v-for="bookId in bookIds"
      :key="bookId"
      :bookId="bookId"
      @deletedBook="handleDeleteBook"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { BACKEND_URL } from '@/constants'
import BookCell from './BookCell.vue'

const bookIds = ref([])
const creatingBook = ref(false)
const loading = ref(true)
const editedTitle = ref('')

onMounted(async () => {
  try {
    const response = await axios.get(`${BACKEND_URL}/bookIds`)
    bookIds.value = response.data.data
    loading.value = true
  } catch (e) {
    console.log(e)
  } finally {
    loading.value = false
  }
})

function handleDeleteBook(bookId) {
  bookIds.value = bookIds.value.filter((id) => id !== bookId)
}

function handleCreateBook() {
  editedTitle.value = ''
  creatingBook.value = true
}

async function submitCreate() {
  try {
    const response = await axios.post(`${BACKEND_URL}/book`, {
      title: editedTitle.value,
    })
    let success = response.data.success
    if (success) {
      bookIds.value.push(response.data.data)
      creatingBook.value = false
    }
  } catch (e) {
    console.log(e)
  }
}

function cancelCreate() {
  creatingBook.value = false
}
</script>

<style scoped>
h1 {
  text-align: center;
}

.books {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  padding: 20px;
  width: 100%; /* 确保容器宽度为100% */
}

.buttons {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

button {
  margin: 5px;
}

form {
  display: flex;
  flex-direction: column; /* 纵向排列表单元素 */
  align-items: center; /* 水平居中表单 */

  width: 250px; /* 宽度 */
  height: 350px; /* 高度 */
  background-color: #ccaf70; /* 牛皮纸色 */
  border: 1px solid #000000; /* 黑色边线 */
  border-radius: 5px; /* 圆角矩形 */
  box-shadow: 4px 4px 20px rgba(0, 0, 0, 0.2); /* 更强的阴影 */
  margin: 20px auto; /* 居中 */
  padding: 10px; /* 内边距 */

  position: relative; /* 相对定位，使按钮可以绝对定位 */
}

form .content {
  flex-grow: 1; /* 让内容部分占据剩余的空间 */
}

form .buttons {
  display: flex;
  justify-content: center; /* 水平居中按钮 */
  gap: 10px; /* 按钮之间的间距 */
  margin-top: 20px; /* 增加与表单其他部分的间距 */

  position: absolute; /* 绝对定位 */
  bottom: 20px; /* 距离父容器底部 20px */
  width: 100%; /* 按钮宽度和父容器相同 */
  display: flex; /* 使用 Flexbox 居中按钮 */
  justify-content: center; /* 水平居中按钮 */
}

form input,
form textarea {
  width: 80%; /* 使输入框宽度适应 */
  max-width: 300px; /* 设置最大宽度 */
}

form div {
  margin-bottom: 10px;
}
</style>
