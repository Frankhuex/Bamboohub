//import ParagraphList from '@/components/ParagraphList.vue'
//import { name } from '@vue/eslint-config-prettier/skip-formatting'
import { createRouter, createWebHashHistory } from 'vue-router'
// import ParagraphList from '../components/ParagraphList.vue'

import RegisterView from '../views/RegisterView.vue'
import LoginView from '../views/LoginView.vue'
import BookListView from '../views/BookListView.vue'
import BookView from '../views/BookView.vue'

const routes = [
  {
    path: '/register',
    component: RegisterView,
    name: 'Register',
  },
  {
    path: '/login',
    component: LoginView,
    name: 'Login',
  },
  {
    path: '/',
    redirect: '/public',
  },
  {
    path: '/public', // 对应 public 路由
    component: BookListView,
    name: 'BookListPublic',
  },
  {
    path: '/private', // 对应 private 路由
    component: BookListView,
    name: 'BookListPrivate',
  },
  {
    path: '/book/:bookId',
    component: BookView,
    name: 'BookContent',
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
