import { createRouter, createWebHistory } from 'vue-router'
import HomeView from "@/views/HomeView.vue";
import JobsView from '@/views/JobsView.vue'
import ErrorView from '@/views/ErrorView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'homeRoute',
      component: HomeView,
    },

    {
      path: '/jobs',
      name: 'jobsRoute',
      component: JobsView,
    },

    {
      path: '/error',
      name: 'errorRoute',
      component: ErrorView,
    },

  ],
})

export default router
