import { createRouter, createWebHistory } from 'vue-router'
import JobsView from '@/views/JobsView.vue'
import ErrorView from '@/views/ErrorView.vue'
import NotAuthorizedView from '@/views/NotAuthorizedView.vue'
import LoginView from '@/views/LoginView.vue'
import DashboardView from '@/views/DashboardView.vue'
import MyJobsView from '@/views/MyJobsView.vue'
import JobCreateEditView from '@/views/JobCreateEditView.vue'
import JobDetailView from '@/views/JobDetailView.vue'
import DriversView from '@/views/DriversView.vue'
import DriverCreateEditView from '@/views/DriverCreateEditView.vue'
import DriverDetailView from '@/views/DriverDetailView.vue'
import CustomersView from '@/views/CustomersView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // Rakendus avaneb kohe sisselogimise lehel — eraldi avalehte (/) pole
    {
      path: '/',
      redirect: '/login',
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
      path: '/not-authorized',
      name: 'notAuthorizedRoute',
      component: NotAuthorizedView,
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

    // Ühe töö detailvaade, :id = töö ID (/jobs/new on täpsem rada, see läheb ette)
    {
      path: '/jobs/:id',
      name: 'job-detail',
      component: JobDetailView,
    },

    {
      path: '/drivers',
      name: 'driversRoute',
      component: DriversView,
    },

    {
      path: '/drivers/new',
      name: 'driver-create',
      component: DriverCreateEditView,
    },

    {
      path: '/drivers/:id/edit',
      name: 'driver-edit',
      component: DriverCreateEditView,
    },

    {
      path: '/drivers/:id',
      name: 'driver-detail',
      component: DriverDetailView,
    },

    {
      path: '/customers',
      name: 'customersView',
      component: CustomersView,
    },
  ],
})

export default router
