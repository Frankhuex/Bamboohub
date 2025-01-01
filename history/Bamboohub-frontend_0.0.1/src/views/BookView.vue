<template>
  <div>
    <ParagraphList :bookId="$route.params.bookId" />
  </div>
</template>

<script setup>
import ParagraphList from '@/components/ParagraphList.vue'
</script>
