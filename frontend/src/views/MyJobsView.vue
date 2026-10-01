<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
import JobService from '@/services/JobService.js'
import { PhClock, PhFlagCheckered, PhMapPin, PhPlay, PhUser } from '@phosphor-icons/vue'

export default {
  name: 'MyJobsView',

  components: { PhClock, PhFlagCheckered, PhMapPin, PhPlay, PhUser },

  beforeMount() {
    // Leht on ainult DRIVER-ile — teised suuname lehele "õigused puuduvad"
    if (!SessionStorageService.userIsDriver()) {
      NavigationService.navigateToNotAuthorizedView()
      return
    }

    this.getDriverJobs()
  },

  data() {
    return {
      jobs: [],
      errorMessage: '',
    }
  },

  methods: {
    getDriverJobs() {
      // Sisselogitud juhi ID (salvestas LoginView)
      const driverId = SessionStorageService.getDriverId()

      JobService.getDriverJobsRequest(driverId)
        .then((response) => {
          this.jobs = response.data
        })
        .catch(() => {
          this.errorMessage = 'Tööde laadimine ebaõnnestus'
        })
    },

    startJob(jobId) {
      JobService.startDriverJobRequest(SessionStorageService.getDriverId(), jobId)
        .then(() => this.getDriverJobs())
        .catch((error) => this.handleJobStatusError(error))
    },

    finishJob(jobId) {
      JobService.finishDriverJobRequest(SessionStorageService.getDriverId(), jobId)
        .then(() => this.getDriverJobs())
        .catch((error) => this.handleJobStatusError(error))
    },

    handleJobStatusError(error) {
      // Backend saadab põhjuse (nt vale staatus või võõras töö) message väljas
      this.errorMessage = error.response?.data?.message || 'Töö staatuse muutmine ebaõnnestus'
    },

    formatDate(dateTime) {
      // Nädalapäev ja kuupäev, nt "reede, 3. oktoober"
      return new Date(dateTime).toLocaleDateString('et-EE', {
        weekday: 'long',
        day: 'numeric',
        month: 'long',
      })
    },

    formatTime(dateTime) {
      // Ainult kellaaeg, nt 12:00
      return new Date(dateTime).toLocaleTimeString('et-EE', { timeStyle: 'short' })
    },

    formatJobType(jobType) {
      // Samad tekstid mis tööde tabelis (JobsTable)
      if (jobType === 'CRANE_ONLY') {
        return 'Kraanatöö'
      }
      if (jobType === 'TRANSPORT_AND_CRANE') {
        return 'Transport + kraanatöö'
      }
      if (jobType === 'TRANSPORT') {
        return 'Transport'
      }
      return jobType
    },

    formatStatus(status) {
      if (status === 'PLANNED') {
        return 'Planeeritud'
      }
      if (status === 'IN_PROGRESS') {
        return 'Töös'
      }
      return status
    },

    getStatusBadgeClass(status) {
      // Töös olev töö on kollane, et see eristuks planeeritud töödest
      if (status === 'IN_PROGRESS') {
        return 'text-bg-warning'
      }
      return 'text-bg-primary'
    },
  },
}
</script>

<template>
  <div class="container-fluid px-4">
    <div v-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>

    <h1>Minu tööd</h1>

    <p v-if="jobs.length === 0" class="text-body-secondary">Aktiivseid töid ei ole.</p>

    <!-- Telefonis üks kaart reas, laiemal ekraanil kaks või kolm -->
    <div class="row g-3">
      <div v-for="job in jobs" :key="job.jobId" class="col-12 col-md-6 col-xl-4">
        <div
          class="card h-100"
          :class="{ 'border-warning border-2': job.status === 'IN_PROGRESS' }"
        >
          <div class="card-header d-flex justify-content-between align-items-center">
            <span class="d-flex align-items-center gap-2">
              <PhClock :size="20" />
              <strong class="fs-5">{{ formatTime(job.plannedStartTime) }}</strong>
              <span class="text-body-secondary">{{ formatDate(job.plannedStartTime) }}</span>
            </span>
            <span class="badge" :class="getStatusBadgeClass(job.status)">
              {{ formatStatus(job.status) }}
            </span>
          </div>

          <div class="card-body">
            <h5 class="card-title d-flex align-items-center gap-2">
              <PhUser :size="20" />
              {{ job.customerName }}
            </h5>
            <p class="card-subtitle text-body-secondary mb-3">{{ formatJobType(job.jobType) }}</p>

            <!-- CRANE_ONLY → ainult töö aadress, muidu pealevõtt ja kohaletoimetamine -->
            <div v-if="job.jobType === 'CRANE_ONLY'" class="d-flex gap-2">
              <PhMapPin :size="20" class="flex-shrink-0 mt-1" />
              <div>
                <div class="small text-body-secondary">Töö aadress</div>
                <div>{{ job.serviceAddress }}</div>
              </div>
            </div>
            <template v-else>
              <div class="d-flex gap-2 mb-2">
                <PhMapPin :size="20" class="flex-shrink-0 mt-1" />
                <div>
                  <div class="small text-body-secondary">Pealevõtt</div>
                  <div>{{ job.pickupAddress }}</div>
                </div>
              </div>
              <div class="d-flex gap-2">
                <PhFlagCheckered :size="20" class="flex-shrink-0 mt-1" />
                <div>
                  <div class="small text-body-secondary">Kohaletoimetamine</div>
                  <div>{{ job.deliveryAddress }}</div>
                </div>
              </div>
            </template>
          </div>

          <!-- Suur nupp kogu kaardi laiuses, et telefonis oleks lihtne vajutada -->
          <div class="card-footer bg-transparent border-0 pt-0 pb-3">
            <button
              v-if="job.status === 'PLANNED'"
              class="btn btn-primary w-100 d-flex justify-content-center align-items-center gap-2"
              @click="startJob(job.jobId)"
            >
              <PhPlay :size="18" weight="fill" />
              Alusta tööd
            </button>
            <button
              v-if="job.status === 'IN_PROGRESS'"
              class="btn btn-success w-100 d-flex justify-content-center align-items-center gap-2"
              @click="finishJob(job.jobId)"
            >
              <PhFlagCheckered :size="18" weight="fill" />
              Lõpeta töö
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
