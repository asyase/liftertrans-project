<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
import JobService from '@/services/JobService.js'

export default {
  name: 'JobDetailView',

  data() {
    return {
      // null = andmed pole veel backendist tulnud
      job: null,

      errorMessage: '',

      errorResponse: {
        message: '',
        errorCode: '',
      },
    }
  },

  computed: {
    // Plokk "Tegelik" näitame ainult lõpetatud tööl
    isCompleted() {
      return this.job.status === 'COMPLETED'
    },

    // Alltöövõtja töö puhul juhti pole, näitame alltöövõtjat
    isSubcontracted() {
      return this.job.executionType === 'SUBCONTRACTED'
    },

    // Ainult kraanatööl on üks töö aadress (pealevõttu ja kohaletoimetamist pole)
    isCraneOnly() {
      return this.job.jobType === 'CRANE_ONLY'
    },
  },

  methods: {
    getJob() {
      // Töö ID võtame URL-ist /jobs/:id
      const jobId = this.$route.params.id

      JobService.getJobRequest(jobId)
        .then((response) => this.handleGetJobResponse(response))
        .catch((error) => this.handleGetJobErrorResponse(error))
    },

    handleGetJobResponse(response) {
      this.job = response.data
    },

    handleGetJobErrorResponse(error) {
      this.errorResponse = error.response.data

      // 404 = sellist tööd pole, näitame backendi teadet
      if (error.response.status === 404) {
        this.errorMessage = this.errorResponse.message
      } else {
        NavigationService.navigateToErrorView()
      }
    },

    formatDateTime(dateTime) {
      // Tühja välja asemel näitame kriipsu
      if (!dateTime) {
        return '—'
      }
      // Ilma sekunditeta, nt 2.10.2026 12:00
      return new Date(dateTime).toLocaleString('et-EE', { dateStyle: 'short', timeStyle: 'short' })
    },

    formatJobType(jobType) {
      // Samad tekstid mis tööde tabelis (JobsTable)
      if (jobType === 'CRANE_ONLY') {
        return 'Kraanatöö'
      }
      if (jobType === 'TRANSPORT_AND_CRANE') {
        return 'Transport + kraanatöö'
      }
      return jobType
    },

    formatVehicle() {
      // Kui autot pole veel määratud, näitame "Määramata"
      if (!this.job.vehicleId) {
        return 'Määramata'
      }
      return this.job.vehicleRegistrationNumber + ' (' + this.job.vehicleName + ')'
    },

    formatExecutionType(executionType) {
      if (executionType === 'INTERNAL') {
        return 'Oma transport'
      }
      if (executionType === 'SUBCONTRACTED') {
        return 'Alltöövõtja'
      }
      return executionType
    },
  },

  beforeMount() {
    // Leht on ainult ADMIN-ile  teised suuname lehele "õigused puuduvad"
    if (!SessionStorageService.userIsAdmin()) {
      NavigationService.navigateToNotAuthorizedView()
      return
    }

    // Lehe avamisel laeme töö andmed
    this.getJob()
  },
}
</script>

<template>
  <div class="container-fluid px-4">
    <div v-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>

    <RouterLink v-if="errorMessage" :to="{ name: 'jobsRoute' }" class="btn btn-outline-secondary">
      Tagasi
    </RouterLink>

    <!-- v-if="job": ootame, kuni andmed on käes, muidu tuleb viga job.jobId peal -->
    <div v-if="job">
      <!-- Pealkiri ja nupud -->
      <div class="row align-items-center mb-3">
        <div class="col">
          <h1>
            Tellimus #{{ job.jobId }}
            <span class="badge bg-secondary fs-6 align-middle">{{ job.status }}</span>
          </h1>
        </div>

        <div class="col text-end">
          <RouterLink :to="{ name: 'jobsRoute' }" class="btn btn-outline-secondary me-2">
            Tagasi
          </RouterLink>

          <!-- TODO: /jobs/:id/edit rada tuleb LIF-66-s -->
          <RouterLink :to="`/jobs/${job.jobId}/edit`" class="btn btn-primary">Muuda</RouterLink>
        </div>
      </div>

      <ul class="nav nav-tabs mb-3">
        <li class="nav-item"><span class="nav-link active">Ülevaade</span></li>
        <li class="nav-item"><span class="nav-link disabled">Kaup</span></li>
        <li class="nav-item"><span class="nav-link disabled">Dokumendid</span></li>
        <li class="nav-item"><span class="nav-link disabled">Staatuse ajalugu</span></li>
      </ul>

      <div class="row g-3">
        <div class="col-md-6">
          <div class="card h-100">
            <div class="card-header">Töö</div>
            <div class="card-body">
              <p><strong>Tüüp:</strong> {{ formatJobType(job.jobType) }}</p>
              <p><strong>Teostamise viis:</strong> {{ formatExecutionType(job.executionType) }}</p>
              <p><strong>Planeeritud algus:</strong> {{ formatDateTime(job.plannedStartTime) }}</p>
              <p><strong>Planeeritud lõpp:</strong> {{ formatDateTime(job.plannedEndTime) }}</p>
              <p><strong>Hinnanguline km:</strong> {{ job.estimatedKm ?? '—' }}</p>
              <p class="mb-0">
                <strong>Hinnangulised tunnid:</strong> {{ job.estimatedHours ?? '—' }}
              </p>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card h-100">
            <div class="card-header">Klient</div>
            <div class="card-body">
              <p><strong>Nimi:</strong> {{ job.customerName }}</p>
              <p><strong>Ettevõte:</strong> {{ job.customerCompanyName ?? '—' }}</p>
              <p><strong>Telefon:</strong> {{ job.customerPhone }}</p>
              <p class="mb-0"><strong>E-post:</strong> {{ job.customerEmail ?? '—' }}</p>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card h-100">
            <div class="card-header">Aadressid</div>
            <div class="card-body">
              <p v-if="isCraneOnly"><strong>Töö aadress:</strong> {{ job.serviceAddress }}</p>
              <template v-else>
                <p><strong>Pealevõtt:</strong> {{ job.pickupAddress }}</p>
                <p><strong>Kohaletoimetamine:</strong> {{ job.deliveryAddress }}</p>
              </template>
              <p class="mb-0">
                <strong>Vastuvõtja:</strong> {{ job.receiverName ?? '—' }}
                {{ job.receiverPhone ?? '' }}
              </p>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card h-100">
            <div class="card-header">Ressursid</div>
            <div class="card-body">
              <p v-if="isSubcontracted">
                <strong>Alltöövõtja:</strong> {{ job.subcontractorName ?? '—' }}
              </p>
              <p v-else><strong>Juht:</strong> {{ job.driverName ?? 'Määramata' }}</p>
              <p class="mb-0"><strong>Auto:</strong> {{ formatVehicle() }}</p>
            </div>
          </div>
        </div>

        <!-- Tegelik: ainult COMPLETED tööl -->
        <div v-if="isCompleted" class="col-md-6">
          <div class="card h-100 border-success">
            <div class="card-header">Tegelik</div>
            <div class="card-body">
              <p><strong>Algus:</strong> {{ formatDateTime(job.actualStartTime) }}</p>
              <p><strong>Lõpp:</strong> {{ formatDateTime(job.actualFinishTime) }}</p>
              <p><strong>Km:</strong> {{ job.actualKm ?? '—' }}</p>
              <p class="mb-0"><strong>Tunnid:</strong> {{ job.actualHours ?? '—' }}</p>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card h-100">
            <div class="card-header">Märkused</div>
            <div class="card-body">{{ job.notes ?? '—' }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
