<template>
  <div>
    <NavBar />
  </div>
  <br /><br />

  <div>
    <ParagraphList :bookId="$route.params.bookId" />
  </div>
</template>

<script setup>
import ParagraphList from '@/components/ParagraphList.vue'
import NavBar from '@/components/NavBar.vue'
</script>
