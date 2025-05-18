<template>
  <div>
    <NavBar />
  </div>
  <br />
  <div>
    <div v-if="isPublic">
      <BookList :isPublic="true" />
    </div>
    <div v-else>
      <BookList :isPublic="false" />
    </div>
  </div>
</template>

<script setup>
import { ref, watchEffect } from 'vue'
import BookList from '@/components/BookList.vue'
import NavBar from '@/components/NavBar.vue'
import { useRoute } from 'vue-router'
const isPublic = ref(true)
const route = useRoute()

// 当路由变化时，更新 isPublic
watchEffect(() => {
  isPublic.value = route.path === '/public'
})

// const getPublic = () => {
//   isPublic.value = true
//   console.log(isPublic.value)
// }

// const getPrivate = () => {
//   isPublic.value = false
//   console.log(isPublic.value)
// }
</script>

<style scoped></style>
