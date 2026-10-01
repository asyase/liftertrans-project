<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
import JobService from '@/services/JobService.js'

// Staatuse ja töö tüübi eestikeelsed sildid
const STATUS_LABELS = {
  DRAFT: 'Mustand',
  PLANNED: 'Planeeritud',
  IN_PROGRESS: 'Töös',
  COMPLETED: 'Lõpetatud',
  CANCELLED: 'Tühistatud',
}

const JOB_TYPE_LABELS = {
  CRANE_ONLY: 'Kraanatöö',
  TRANSPORT_AND_CRANE: 'Transport + kraanatöö',
  TRANSPORT: 'Transport',
}

export default {
  name: 'DashboardView',

  beforeMount() {
    // Leht on ainult ADMIN-ile — teised suuname lehele "õigused puuduvad"
    if (!SessionStorageService.userIsAdmin()) {
      NavigationService.navigateToNotAuthorizedView()
      return
    }

    this.getJobs()
  },

  data() {
    return {
      jobs: [],
      loading: false,
      errorMessage: '',
    }
  },

  computed: {
    // Tänased tööd — plaanitud algus on tänasel kuupäeval
    todayJobs() {
      return this.jobs.filter((job) => this.isToday(job.plannedStartTime))
    },

    // Loendurid käivad tänaste tööde kohta
    todayCount() {
      return this.todayJobs.length
    },

    plannedCount() {
      return this.countTodayByStatus('PLANNED')
    },

    inProgressCount() {
      return this.countTodayByStatus('IN_PROGRESS')
    },

    completedCount() {
      return this.countTodayByStatus('COMPLETED')
    },
  },

  methods: {
    getJobs() {
      this.loading = true
      this.errorMessage = ''

      JobService.getJobsRequest({})
        .then((response) => this.handleGetJobs(response))
        .catch(() => this.handleGetJobsError())
        .finally(() => {
          this.loading = false
        })
    },

    handleGetJobs(response) {
      this.jobs = response.data
    },

    handleGetJobsError() {
      this.errorMessage = 'Tööde laadimine ebaõnnestus.'
    },

    countTodayByStatus(status) {
      return this.todayJobs.filter((job) => job.status === status).length
    },

    isToday(dateTime) {
      if (!dateTime) {
        return false
      }

      return new Date(dateTime).toDateString() === new Date().toDateString()
    },

    formatTime(dateTime) {
      if (!dateTime) {
        return '-'
      }

      return new Date(dateTime).toLocaleTimeString('et-EE', {
        hour: '2-digit',
        minute: '2-digit',
      })
    },

    jobTypeLabel(jobType) {
      return JOB_TYPE_LABELS[jobType] ?? jobType
    },

    statusLabel(status) {
      return STATUS_LABELS[status] ?? status
    },

    // Alltöövõtja olemasolul kuvame teda, muidu juhti
    driverOrSubcontractor(job) {
      return job.subcontractorName || job.driverName || '-'
    },

    goToCreateOrder() {
      this.$router.push('/jobs/new')
    },

    goToCalendar() {
      this.$router.push('/calendar')
    },
  },
}
</script>

<template>
  <div class="container-fluid px-4">
    <!-- Päis koos tegevusnuppudega -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2">
      <h1 class="mb-0">Töölaud</h1>

      <div class="d-flex gap-2">
        <button class="btn btn-danger" @click="goToCreateOrder">+ Lisa uus tellimus</button>
        <button class="btn btn-outline-secondary" @click="goToCalendar">Vaata kogu kalendrit</button>
      </div>
    </div>

    <!-- Veateade -->
    <div v-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>

    <!-- Laadimine -->
    <div v-else-if="loading" class="text-center py-5 text-secondary">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Laadimine…</span>
      </div>
      <div class="mt-2">Laadimine…</div>
    </div>

    <!-- Sisu -->
    <template v-else>
      <!-- Ülevaate kaardid + uue tellimuse nupp -->
      <div class="row g-3 mb-4 align-items-stretch">
        <div class="col-6 col-md-3">
          <div class="card lt-section h-100">
            <div class="card-body">
              <div class="text-secondary">Tänased tööd</div>
              <div class="display-6 fw-bold">{{ todayCount }}</div>
            </div>
          </div>
        </div>

        <div class="col-6 col-md-3">
          <div class="card lt-section h-100">
            <div class="card-body">
              <div class="text-secondary">Planeeritud</div>
              <div class="display-6 fw-bold">{{ plannedCount }}</div>
            </div>
          </div>
        </div>

        <div class="col-6 col-md-3">
          <div class="card lt-section h-100">
            <div class="card-body">
              <div class="text-secondary">Töös</div>
              <div class="display-6 fw-bold">{{ inProgressCount }}</div>
            </div>
          </div>
        </div>

        <div class="col-6 col-md-3">
          <div class="card lt-section h-100">
            <div class="card-body">
              <div class="text-secondary">Lõpetatud</div>
              <div class="display-6 fw-bold">{{ completedCount }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Tänased tööd eraldi plokis -->
      <h2 class="h5 mb-3">Tänased tööd</h2>

      <div v-if="todayJobs.length > 0" class="table-responsive">
        <table class="table table-hover align-middle">
          <thead>
            <tr>
              <th scope="col">Kellaaeg</th>
              <th scope="col">Klient</th>
              <th scope="col">Töö tüüp</th>
              <th scope="col">Auto</th>
              <th scope="col">Juht</th>
              <th scope="col">Staatus</th>
              <th scope="col">Tegevus</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="job in todayJobs" :key="job.jobId">
              <td>{{ formatTime(job.plannedStartTime) }}</td>
              <td>{{ job.customerName }}</td>
              <td>{{ jobTypeLabel(job.jobType) }}</td>
              <td>{{ job.vehicleRegistrationNumber || '-' }}</td>
              <td>{{ driverOrSubcontractor(job) }}</td>
              <td>{{ statusLabel(job.status) }}</td>
              <td class="text-nowrap">
                <RouterLink
                  :to="{ name: 'job-detail', params: { id: job.jobId } }"
                  class="btn btn-sm btn-outline-primary me-2"
                >
                  Vaata
                </RouterLink>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <p v-else class="text-secondary">Tänaseks pole ühtegi tööd planeeritud.</p>
    </template>
  </div>
</template>

<style scoped></style>
