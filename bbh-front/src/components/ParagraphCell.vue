<template>
  <div class="paragraph-cell" v-if="success">
    <div class="paragraph" v-if="paraDTO.prevParaId">
      <div v-if="!editing">
        <p class="author" @click="$emit('clickEditAuthor')">
          {{ paraDTO.author }}
        </p>

        <!-- 显示内容 -->
        <article>
          <p class="content">
            {{ paraDTO.content }}
          </p>
        </article>

        <div v-if="canEdit">
          <button @click="editContent">编辑</button>
          <button @click="deleteParagraph">删除</button>
          <button @click="moveUpParagraph">上移</button>
          <button @click="moveDownParagraph">下移</button>
        </div>
      </div>

      <!-- 编辑模式: 显示表单 -->
      <form v-else @submit.prevent="submitEdit">
        <div>
          <label for="author">作者:</label>
          <input v-model="editedAuthor" type="text" id="author" />
        </div>
        <div>
          <label for="content">文本:</label>
          <textarea v-model="editedContent" rows="4" cols="50"></textarea>
        </div>
        <div>
          <button type="button" @click="cancelEdit">取消</button>
          <button type="submit">提交</button>
        </div>
      </form>
    </div>

    <div v-if="canEdit">
      <button class="btn-add-para" v-if="!editing && !addingParagraph" @click="addParagraph">
        +
      </button>
      <!-- 编辑模式: 显示表单 -->
      <form class="paragraph" v-if="addingParagraph" @submit.prevent="submitNewPara">
        <div>
          <label for="author">作者:</label>
          <input v-model="editedAuthor" type="text" id="author" />
        </div>
        <div>
          <label for="content">文本:</label>
          <textarea v-model="editedContent" rows="4" cols="50"></textarea>
        </div>
        <div>
          <button type="button" @click="cancelAddPara">取消</button>
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
//import { useRouter } from 'vue-router'
//const router = useRouter()
const props = defineProps(['paraId'])
const emit = defineEmits([
  'loadedNextParaId',
  'addedParagraph',
  'deletedParagraph',
  'movedUpParagraph',
  'movedDownParagraph',
])

const loading = ref(true)
const paraDTO = ref({
  id: null,
  bookId: -1,
  author: '',
  content: '',
  prevParaId: null,
  nextParaId: null,
})
const success = ref(false)
const errorMsg = ref('')
const editing = ref(false) // 控制是否处于编辑模式
const editedAuthor = ref('') // 存储编辑的 author
const editedContent = ref('') // 存储编辑的 content

const addingParagraph = ref(false)

const roleType = ref('')
const canEdit = ref(false)

// 初始化加载数据
onMounted(async () => {
  try {
    let token = ''
    if (localStorage.getItem('token')) {
      token = localStorage.getItem('token')
    }
    loading.value = true
    let response = await axios.get(`${BACKEND_URL}/paragraph/${props.paraId}`, {
      headers: {
        Authorization: token,
      },
    })
    paraDTO.value = response.data.data
    success.value = response.data.success
    errorMsg.value = response.data.errorMsg
    if (success.value) {
      //console.log(paraDTO.value)
      //console.log(response.data)
      if (response.data.data.nextParaId) {
        emit('loadedNextParaId', response.data.data.nextParaId)
      }
    }

    response = await axios.get(`${BACKEND_URL}/book/${paraDTO.value.bookId}/role`, {
      headers: {
        Authorization: localStorage.getItem('token'),
      },
    })
    if (response.data.success) {
      roleType.value = response.data.data
      canEdit.value =
        roleType.value === 'OWNER' || roleType.value === 'ADMIN' || roleType.value === 'EDITOR'
      console.log(roleType.value)
      loading.value = false
      success.value = true
    } else {
      console.log('Failed to load role:', response.data.errorMsg)
      loading.value = false
      //router.push('/login')
    }
  } catch (e) {
    console.log(e)
    //router.push('/login')
  } finally {
    loading.value = false
  }
})

// 切换到编辑模式
const editContent = () => {
  editedAuthor.value = paraDTO.value.author // 将当前 author 赋给编辑的 author
  editedContent.value = paraDTO.value.content // 将当前 content 赋给编辑的 content
  editing.value = true
}

// 提交编辑
const submitEdit = async () => {
  try {
    // 更新数据
    paraDTO.value.author = editedAuthor.value
    paraDTO.value.content = editedContent.value

    // 调用 API 提交更新的数据
    const response = await axios.put(
      `${BACKEND_URL}/paragraph/${paraDTO.value.id}`,
      {
        author: editedAuthor.value,
        content: editedContent.value,
      },
      {
        headers: {
          Authorization: localStorage.getItem('token'),
        },
      },
    )

    if (response.data.success) {
      console.log('Paragraph updated successfully!')
      console.log(response.data.data)
      editing.value = false
    } else {
      console.log('Failed to update paragraph:', response.data.errorMsg)
      //router.push('/login')
    }
  } catch (error) {
    console.error('Error submitting edit:', error)
  }
}

// 取消编辑
const cancelEdit = () => {
  editing.value = false // 退出编辑模式
}

const addParagraph = () => {
  editedAuthor.value = '' // 清空编辑的 author
  editedContent.value = '' // 清空编辑的 content
  addingParagraph.value = true
}

const submitNewPara = async () => {
  try {
    // 调用 API 新增段落
    const response = await axios.post(
      `${BACKEND_URL}/paragraph`,
      {
        author: editedAuthor.value,
        content: editedContent.value,
        prevParaId: paraDTO.value.id,
      },
      {
        headers: {
          Authorization: localStorage.getItem('token'),
        },
      },
    )

    if (response.data.success) {
      console.log('Paragraph added successfully!')
      console.log(response.data.data)
      addingParagraph.value = false
      emit('addedParagraph', {
        id: response.data.data,
        prevParaId: paraDTO.value.id,
      })
    } else {
      console.log('Failed to add paragraph:', response.data.errorMsg)
      //router.push('/login')
    }
  } catch (error) {
    console.error('Error submitting new paragraph:', error)
  }
}

const cancelAddPara = () => {
  addingParagraph.value = false
}

const deleteParagraph = async () => {
  const isConfirmed = window.confirm(`你确定要删除${paraDTO.value.author}写的这一段吗？`)
  if (isConfirmed) {
    try {
      // 调用 API 删除段落
      const response = await axios.delete(`${BACKEND_URL}/paragraph/${paraDTO.value.id}`, {
        headers: {
          Authorization: localStorage.getItem('token'),
        },
      })

      if (response.data.success) {
        console.log('Paragraph deleted successfully!')
        console.log(response.data.data)
        emit('deletedParagraph', paraDTO.value.id)
      } else {
        console.log('Failed to delete paragraph:', response.data.errorMsg)
        //router.push('/login')
      }
    } catch (error) {
      console.error('Error deleting paragraph:', error)
    }
  }
}

const moveUpParagraph = async () => {
  try {
    const response = await axios.post(
      `${BACKEND_URL}/paragraph/${paraDTO.value.id}/move-up`,
      {},
      {
        headers: {
          Authorization: localStorage.getItem('token'),
        },
      },
    )
    if (response.data.success) {
      console.log('Paragraph moved up successfully!')
      emit('movedUpParagraph', paraDTO.value.id)
    } else {
      //router.push('/login')
    }
  } catch (error) {
    console.error('Error moving up paragraph:', error)
  }
}

const moveDownParagraph = async () => {
  try {
    const response = await axios.post(
      `${BACKEND_URL}/paragraph/${paraDTO.value.id}/move-down`,
      {},
      {
        headers: {
          Authorization: localStorage.getItem('token'),
        },
      },
    )
    if (response.data.success) {
      console.log('Paragraph moved down successfully!')
      emit('movedDownParagraph', paraDTO.value.id)
    } else {
      //router.push('/login')
    }
  } catch (error) {
    console.error('Error moving down paragraph:', error)
  }
}
</script>

<style scoped>
button {
  margin: 5px;
}

form textarea,
form input {
  width: 100%;
}

form div {
  margin-bottom: 10px;
}

form label {
  color: black;
}

.paragraph {
  background-color: #fdf2c0; /* 纸黄色 */
  border: 1px solid black; /* 黑色边线 */
  border-radius: 15px; /* 圆角矩形 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 阴影效果 */
  padding: 20px; /* 内部填充（margin） */

  /* 以下是居中设置 */
  width: 800px; /* 设置宽度，可根据需要调整 */
  max-width: 100%; /* 最大宽度，避免过宽 */
  margin: 20px auto; /* 水平居中，20px 的上下外边距 */
}

.content {
  color: black;
  white-space: pre-wrap;
}

.author {
  color: grey;
  white-space: pre-wrap;
}

.btn-add-para {
  display: block; /* 使按钮成为块级元素 */
  width: 50px; /* 设置固定宽度 */
  height: 50px; /* 设置固定高度 */
  border-radius: 50%; /* 圆形按钮 */
  background-color: #fdf2c0; /* 背景色 */
  color: black; /* 文字颜色 */
  border: none; /* 无边框 */
  font-size: 24px; /* 字体大小 */
  text-align: center; /* 水平居中 */
  line-height: 50px; /* 垂直居中 */
  cursor: pointer; /* 悬停时显示手指形状 */
  transition: background-color 0.3s; /* 背景颜色变化的过渡效果 */
  margin: 0 auto; /* 左右自动填充，使按钮水平居中 */
}

.btn-add-para:hover {
  background-color: #fdfaca; /* 悬停时按钮背景颜色变化 */
}

/* .paragraph-cell {
  display: flex;
  justify-content: center; 
  align-items: center;
} */
</style>
