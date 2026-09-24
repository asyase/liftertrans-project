import { createRouter, createWebHistory } from 'vue-router'
import HomeView from "@/views/HomeView.vue";
import JobsView from '@/views/JobsView.vue'
import ErrorView from '@/views/ErrorView.vue'
import LoginView from '@/views/LoginView.vue'
import DashboardView from '@/views/DashboardView.vue'
import MyJobsView from '@/views/MyJobsView.vue'

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

    {
      path: '/login',
      name: 'loginRoute',
      component: LoginView,
    },

    {
      path: '/dashbord',
      name: 'dashboardRoute',
      component: DashboardView,
    },

    {
      path: '/my-jobs',
      name: 'myJobsRoute',
      component: MyJobsView,
    },
  ],
})

export default router
