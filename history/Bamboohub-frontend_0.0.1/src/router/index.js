//import ParagraphList from '@/components/ParagraphList.vue'
//import { name } from '@vue/eslint-config-prettier/skip-formatting'
import { createRouter, createWebHashHistory } from 'vue-router'
// import ParagraphList from '../components/ParagraphList.vue'
const routes = [
  {
    path: '/',
    component: () => import('../views/BookListView.vue'),
    name: 'BookList',
  },
  {
    path: '/book/:bookId',
    component: () => import('../views/BookView.vue'),
    name: 'BookContent',
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
