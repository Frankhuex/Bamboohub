<!-- <template>
  <div v-if="success" class="paragraph">
    <p class="author" @click="$emit('clickEditAuthor')">
      {{ data.author }}
    </p>

    <article>
      <p class="content" @click="$emit('clickEditContent')">
        {{ data.content }}
      </p>
    </article>

    <slot></slot>
  </div>
  <div v-else>Failed to load...</div>
</template>



<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { defineProps, defineEmits } from 'vue'
import { BACKEND_URL } from '@/constants'

const props = defineProps(['bookId'])
defineEmits(['clickEditAuthor', 'clickEditContent'])

const loading = ref(true)

const data = ref({
  id: null,
  title: '',
  startParaId: null,
})
const success = ref(false)
const errorMsg = ref('')

const paraIds = ref([])

onMounted(async () => {
  try {
    loading.value = true
    const response = await axios.get(`${BACKEND_URL}/book/${props.paraId}`)
    data.value = response.data.data
    success.value = response.data.success
    errorMsg.value = response.data.errorMsg
  } catch (e) {
    console.log(e)
  } finally {
    loading.value = false
  }
})

async function loadPara(paraId) {
  if (!paraId) return
  loading.value=true
  try {
    const response = await axios.get(`${BACKEND_URL}/paragraph/${props.paraId}`)

  }
}
</script> -->

<template>
  <div class="book-info">
    <h1>{{ bookDTO.title }}</h1>
    <button @click="toBookList">回到首页</button>
    <p><br /></p>
  </div>
  <div class="paragraphs">
    <ParagraphCell
      v-for="id in paraIds"
      :key="id"
      :paraId="id"
      @loadedNextParaId="handleNextParaId"
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

const props = defineProps(['bookId'])

const bookDTO = ref({
  id: null,
  title: '',
  startParaId: null,
})

const paraIds = ref([]) // 用于存储段落 ID 列表

const loading = ref(false)

const router = useRouter()
const toBookList = () => {
  router.push({
    name: 'BookList',
  })
}

onMounted(async () => {
  try {
    loading.value = true
    let response = await axios.get(`${BACKEND_URL}/book/${props.bookId}`) // 获取书籍信息
    //console.log(response.data)
    if (response.data.success) {
      bookDTO.value = response.data.data
      //console.log(bookDTO.value)
      paraIds.value.push(bookDTO.value.startParaId) // 将书籍的起始段落 ID 添加到列表
      // //response = await axios.get(`${BACKEND_URL}/book/5/paraIds`) // 获取书籍的段落 ID 列表
      // console.log(response)
      // console.log(111)
      // if (response.data.success) {
      //   paraIds.value = response.data.data
      //   loading.value = false
      // }
    }
  } catch (e) {
    console.log(e)
  }
})

// 处理子组件返回的 nextParaId
function handleNextParaId(nextParaId) {
  if (nextParaId && !paraIds.value.includes(nextParaId)) {
    //console.log('nextParaId:', nextParaId)
    paraIds.value.push(nextParaId) // 将新段落 ID 添加到列表
  }
  //console.log(nextParaId.value)
}

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
</script>

<style scoped>
.book-info,
.paragraphs {
  max-width: 80%;
  margin: 0 auto;
}

h1 {
  margin: 0 auto;
  text-align: center;
}
</style>
