import { createRouter, createWebHistory } from 'vue-router'
import HomeView from "@/views/HomeView.vue";
import JobsView from '@/views/JobsView.vue'
import ErrorView from '@/views/ErrorView.vue'
import LoginView from '@/views/LoginView.vue'
import DashboardView from '@/views/DashboardView.vue'
import MyJobsView from '@/views/MyJobsView.vue'
import JobCreateEditView from '@/views/JobCreateEditView.vue'

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
      path: '/dashboard',
      name: 'dashboardRoute',
      component: DashboardView,
    },

    {
      path: '/my-jobs',
      name: 'myJobsRoute',
      component: MyJobsView,
    },
    {
      path: '/jobs/new',
      name: 'job-create',
      component: JobCreateEditView,
    },
  ],
})

export default router
