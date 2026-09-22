<script>
import JobsTable from '@/components/job/JobsTable.vue'
import JobService from '@/services/JobService.js'
import NavigationService from '@/services/NavigationService.js'

export default {
  name: 'JobsView',

  components: {
    JobsTable,
  },

  beforeMount() {
    // Lehe avamisel laadime tööd backendist
    this.getJobs()
  },

  data() {
    return {
      jobs: [],

      errorMessage: '',

      filters: {
        date: '',
        vehicleId: '',
        driverId: '',
        status: '',
      },

      errorResponse: {
        message: '',
        errorCode: '',
      },
    }
  },

  methods: {
    getJobs() {
      // Enne uut päringut eemaldame vana veateate
      this.errorMessage = ''

      JobService.getJobsRequest(this.filters)
        .then((response) => this.handleGetJobsResponse(response))
        .catch((error) => this.handleGetJobsErrorResponse(error))
    },

    handleGetJobsResponse(response) {
      // Backendist saadud tööd salvestame jobs massiivi
      this.jobs = response.data
    },

    handleGetJobsErrorResponse(error) {
      this.errorResponse = error.response.data

      if (error.response.status === 400) {
        this.errorMessage = this.errorResponse.message
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    setStatusFilter(status) {
      // Kiirfiltri vajutamisel muudame staatust
      this.filters.status = status

      // Laadime tööd uuesti valitud staatusega
      this.getJobs()
    },

    clearStatusFilter() {
      this.filters.status = ''
      this.getJobs()
    },

    handleFilterChange() {
      // Dropdowni või kuupäeva muutmisel laadime andmed uuesti
      this.getJobs()
    },

    handleJobViewClick(jobId) {
      NavigationService.navigateToJobDetailView(jobId)
    },

    handleJobEditClick(jobId) {
      NavigationService.navigateToJobEditView(jobId)
    },

    handleAddJobClick() {
      NavigationService.navigateToJobCreateView()
    },
  },
}
</script>

<template>
  <div class="container-fluid px-4">
    <div class="row align-items-center mb-3">
      <div class="col">
        <h1>Tellimused</h1>
      </div>

      <div class="col text-end">
        <button @click="handleAddJobClick" class="btn btn-primary">+ Lisa uus tellimus</button>
      </div>
    </div>

    <!-- Kiirfiltrid -->
    <div class="row mb-3">
      <div class="col">
        <button @click="setStatusFilter('PLANNED')" class="btn btn-outline-secondary me-2">
          PLANNED
        </button>

        <button @click="setStatusFilter('IN_PROGRESS')" class="btn btn-outline-secondary me-2">
          IN_PROGRESS
        </button>

        <button @click="setStatusFilter('COMPLETED')" class="btn btn-outline-secondary me-2">
          COMPLETED
        </button>

        <button @click="clearStatusFilter" class="btn btn-outline-secondary">Kõik</button>
      </div>
    </div>

    <!-- Filtrid -->
    <div class="row mb-4">
      <div class="col col-2">
        <input
          v-model="filters.date"
          @change="handleFilterChange"
          type="date"
          class="form-control"
        />
      </div>

      <div class="col col-2">
        <select v-model="filters.status" @change="handleFilterChange" class="form-select">
          <option value="">Staatus</option>
          <option value="DRAFT">DRAFT</option>
          <option value="PLANNED">PLANNED</option>
          <option value="IN_PROGRESS">IN_PROGRESS</option>
          <option value="COMPLETED">COMPLETED</option>
          <option value="CANCELLED">CANCELLED</option>
        </select>
      </div>
    </div>

    <!-- Veateade -->
    <div v-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>

    <!-- Tööde tabel -->
    <JobsTable
      :jobs="jobs"
      @event-job-view-click="handleJobViewClick"
      @event-job-edit-click="handleJobEditClick"
    />
  </div>
</template>
