<template>
  <div class="book-info">
    <h1>{{ bookDTO.title }}</h1>
    <div class="btn-group">
      <button @click="toBookList">返回上页</button>
      <br /><br />
      <button @click="handleShowMemberList">成员列表</button>
    </div>
    <p><br /></p>
  </div>
  <div v-if="showMemberList">
    <MemberList :bookId="$route.params.bookId" @closeWindow="handleCloseMemberList" />
  </div>
  <div class="paragraphs">
    <ParagraphCell
      v-for="id in paraIds"
      :key="id"
      :paraId="id"
      @addedParagraph="handleAddParagraph"
      @deletedParagraph="handleDeleteParagraph"
      @movedUpParagraph="handleMoveUpParagraph"
      @movedDownParagraph="handleMoveDownParagraph"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ParagraphCell from './ParagraphCell.vue'
import axios from 'axios'
import { BACKEND_URL } from '@/constants'
import { useRouter } from 'vue-router'
import MemberList from '@/components/MemberList.vue'

const props = defineProps(['bookId'])

const bookDTO = ref({
  id: null,
  title: '',
  startParaId: null,
})

const paraIds = ref([]) // 用于存储段落 ID 列表

const showMemberList = ref(false)

const loading = ref(false)

const router = useRouter()
const toBookList = () => {
  router.back()
}

onMounted(async () => {
  try {
    loading.value = true
    let token = ''
    if (localStorage.getItem('token')) {
      token = localStorage.getItem('token')
    }
    let response = await axios.get(`${BACKEND_URL}/book/${props.bookId}`, {
      headers: {
        Authorization: token,
      },
    }) // 获取书籍信息
    console.log('Book Info', response.data)
    if (response.data.success === true) {
      bookDTO.value = response.data.data
      console.log('start fetching paraIds')
      console.log(bookDTO.value.startParaId)
      console.log(`${BACKEND_URL}/book/${bookDTO.value.id}/paraIds`)
      // paraIds.value.push(bookDTO.value.startParaId) // 将书籍的起始段落 ID 添加到列表
      response = await axios.get(`${BACKEND_URL}/book/${bookDTO.value.id}/paraIds`, {
        headers: {
          Authorization: token,
        },
      }) // 获取书籍的段落 ID 列表
      console.log(response)
      // console.log(111)
      if (response.data.success === true) {
        paraIds.value = response.data.data
        loading.value = false
      }
    }
  } catch (e) {
    console.log(e)
  }
})

// 处理子组件返回的 nextParaId
// function handleNextParaId(nextParaId) {
//   if (nextParaId && !paraIds.value.includes(nextParaId)) {
//     // paraIds.value.push(nextParaId) // 将新段落 ID 添加到列表
//   }
// }

function handleAddParagraph(data) {
  let index = paraIds.value.indexOf(data.prevParaId)
  paraIds.value.splice(index + 1, 0, data.id)
  //console.log(data.id)
  //console.log(data.prevParaId)
}

function handleDeleteParagraph(paraId) {
  let index = paraIds.value.indexOf(paraId)
  paraIds.value.splice(index, 1)
}

function handleMoveUpParagraph(paraId) {
  let index = paraIds.value.indexOf(paraId)
  if (index > 0) {
    let temp = paraIds.value[index - 1]
    paraIds.value[index - 1] = paraId
    paraIds.value[index] = temp
  }
}

function handleMoveDownParagraph(paraId) {
  let index = paraIds.value.indexOf(paraId)
  if (index < paraIds.value.length - 1) {
    let temp = paraIds.value[index + 1]
    paraIds.value[index + 1] = paraId
    paraIds.value[index] = temp
  }
}

function handleCloseMemberList() {
  showMemberList.value = false
}

function handleShowMemberList() {
  showMemberList.value = true
}
</script>

<style scoped>
.book-info,
.paragraphs {
  /* max-width: 100%; */
  margin: 0 auto;
}

h1 {
  margin: 0 auto;
  text-align: center;
}
</style>
