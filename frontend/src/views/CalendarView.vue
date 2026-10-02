<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
import JobService from '@/services/JobService.js'

// Eestikeelsed sildid
const STATUS_LABELS = {
  DRAFT: 'Mustand',
  PLANNED: 'Planeeritud',
  IN_PROGRESS: 'Töös',
  COMPLETED: 'Lõpetatud',
  CANCELLED: 'Tühistatud',
}

const STATUS_CLASSES = {
  DRAFT: 'draft',
  PLANNED: 'planned',
  IN_PROGRESS: 'progress',
  COMPLETED: 'done',
  CANCELLED: 'cancel',
}

const JOB_TYPE_LABELS = {
  CRANE_ONLY: 'Kraanatöö',
  TRANSPORT_AND_CRANE: 'Transport + kraanatöö',
  TRANSPORT: 'Transport',
}

const WEEKDAYS = ['E', 'T', 'K', 'N', 'R', 'L', 'P']
const MONTHS = [
  'Jaanuar',
  'Veebruar',
  'Märts',
  'Aprill',
  'Mai',
  'Juuni',
  'Juuli',
  'August',
  'September',
  'Oktoober',
  'November',
  'Detsember',
]

export default {
  name: 'CalendarView',

  beforeMount() {
    // Leht on ainult ADMIN-ile — teised suuname lehele "õigused puuduvad"
    if (!SessionStorageService.userIsAdmin()) {
      NavigationService.navigateToNotAuthorizedView()
      return
    }

    this.getJobs()
  },

  data() {
    const today = new Date()

    return {
      jobs: [],
      loading: false,
      errorMessage: '',
      weekdays: WEEKDAYS,
      viewYear: today.getFullYear(),
      viewMonth: today.getMonth(),
      selectedKey: this.dateKey(today),
    }
  },

  computed: {
    monthTitle() {
      return MONTHS[this.viewMonth] + ' ' + this.viewYear
    },

    // Tööd rühmitatud päeva järgi: { 'YYYY-MM-DD': [job, ...] }
    jobsByDate() {
      const map = {}
      for (const job of this.jobs) {
        if (!job.plannedStartTime) {
          continue
        }

        const key = this.dateKey(new Date(job.plannedStartTime))
        if (!map[key]) {
          map[key] = []
        }
        map[key].push(job)
      }

      return map
    },

    // Kuu päevade ruudustik, nädal algab esmaspäevast. Kuu algusesse lisame tühjad lahtrid.
    calendarCells() {
      const first = new Date(this.viewYear, this.viewMonth, 1)
      const offset = (first.getDay() + 6) % 7
      const daysInMonth = new Date(this.viewYear, this.viewMonth + 1, 0).getDate()

      const cells = []
      for (let i = 0; i < offset; i++) {
        cells.push(null)
      }

      for (let day = 1; day <= daysInMonth; day++) {
        const date = new Date(this.viewYear, this.viewMonth, day)
        const key = this.dateKey(date)
        cells.push({
          day,
          key,
          jobs: this.jobsByDate[key] || [],
          isToday: key === this.dateKey(new Date()),
        })
      }

      return cells
    },

    selectedJobs() {
      const jobs = this.jobsByDate[this.selectedKey] || []

      return [...jobs].sort((a, b) => new Date(a.plannedStartTime) - new Date(b.plannedStartTime))
    },

    selectedLabel() {
      // 'YYYY-MM-DD' → '2. oktoober 2026'
      const [year, month, day] = this.selectedKey.split('-').map(Number)

      return Number(day) + '. ' + MONTHS[month - 1].toLowerCase() + ' ' + year
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

    // Kohaliku kuupäeva võti 'YYYY-MM-DD'
    dateKey(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')

      return year + '-' + month + '-' + day
    },

    selectDay(cell) {
      if (cell) {
        this.selectedKey = cell.key
      }
    },

    previousMonth() {
      if (this.viewMonth === 0) {
        this.viewMonth = 11
        this.viewYear--
      } else {
        this.viewMonth--
      }
    },

    nextMonth() {
      if (this.viewMonth === 11) {
        this.viewMonth = 0
        this.viewYear++
      } else {
        this.viewMonth++
      }
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

    statusClass(status) {
      return STATUS_CLASSES[status] ?? 'draft'
    },

    driverOrSubcontractor(job) {
      return job.subcontractorName || job.driverName || '-'
    },
  },
}
</script>

<template>
  <div class="container-fluid px-4">
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-2">
      <h1 class="mb-0">Kalender</h1>
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
    <div v-else class="row g-4">
      <!-- Kuukalender -->
      <div class="col-12 col-lg-7">
        <div class="card">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <button class="btn btn-outline-secondary btn-sm" @click="previousMonth">‹</button>
              <h2 class="h5 mb-0 text-capitalize">{{ monthTitle }}</h2>
              <button class="btn btn-outline-secondary btn-sm" @click="nextMonth">›</button>
            </div>

            <div class="calendar-grid">
              <div v-for="weekday in weekdays" :key="weekday" class="calendar-dow">
                {{ weekday }}
              </div>

              <div
                v-for="(cell, index) in calendarCells"
                :key="index"
                class="calendar-day"
                :class="{
                  empty: !cell,
                  today: cell && cell.isToday,
                  selected: cell && cell.key === selectedKey,
                }"
                @click="selectDay(cell)"
              >
                <template v-if="cell">
                  <span class="calendar-num">{{ cell.day }}</span>
                  <div v-if="cell.jobs.length" class="calendar-dots">
                    <span
                      v-for="job in cell.jobs.slice(0, 5)"
                      :key="job.jobId"
                      class="calendar-dot"
                      :class="statusClass(job.status)"
                    ></span>
                  </div>
                </template>
              </div>
            </div>

            <!-- Staatuste legend -->
            <div class="calendar-legend mt-3">
              <span><i class="calendar-dot planned"></i>Planeeritud</span>
              <span><i class="calendar-dot progress"></i>Töös</span>
              <span><i class="calendar-dot done"></i>Lõpetatud</span>
              <span><i class="calendar-dot draft"></i>Mustand</span>
              <span><i class="calendar-dot cancel"></i>Tühistatud</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Valitud päeva tööd -->
      <div class="col-12 col-lg-5">
        <div class="card">
          <div class="card-body">
            <h2 class="h5 mb-1">Päeva tööd</h2>
            <p class="text-secondary mb-3">{{ selectedLabel }} · {{ selectedJobs.length }} tööd</p>

            <div v-if="selectedJobs.length" class="table-responsive">
              <table class="table table-hover align-middle mb-0">
                <thead>
                  <tr>
                    <th scope="col">Kellaaeg</th>
                    <th scope="col">Klient</th>
                    <th scope="col">Juht</th>
                    <th scope="col">Staatus</th>
                    <th scope="col"></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="job in selectedJobs" :key="job.jobId">
                    <td>{{ formatTime(job.plannedStartTime) }}</td>
                    <td>
                      <div>{{ job.customerName }}</div>
                      <small class="text-secondary">{{ jobTypeLabel(job.jobType) }}</small>
                    </td>
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

            <p v-else class="text-secondary mb-0">Sel päeval pole töid.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
}

.calendar-dow {
  text-align: center;
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--bs-secondary-color, #6c757d);
  font-weight: 600;
  padding-bottom: 4px;
}

.calendar-day {
  aspect-ratio: 1 / 1;
  border: 1px solid var(--bs-border-color, #dee2e6);
  border-radius: 8px;
  padding: 6px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  cursor: pointer;
  font-variant-numeric: tabular-nums;
}

.calendar-day:hover {
  border-color: var(--lt-red, #b3201f);
}

.calendar-day.empty {
  border-color: transparent;
  cursor: default;
}

.calendar-day.today .calendar-num {
  background: var(--lt-red, #b3201f);
  color: #fff;
  border-radius: 50%;
  width: 1.6em;
  height: 1.6em;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.calendar-day.selected {
  border-color: var(--lt-red, #b3201f);
  box-shadow: 0 0 0 2px rgba(179, 32, 31, 0.2);
}

.calendar-num {
  font-weight: 600;
  font-size: 0.9rem;
}

.calendar-dots {
  display: flex;
  flex-wrap: wrap;
  gap: 3px;
  margin-top: auto;
}

.calendar-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  display: inline-block;
}

.calendar-dot.planned {
  background: #1d6fb8;
}

.calendar-dot.progress {
  background: #bd7100;
}

.calendar-dot.done {
  background: #2e7d46;
}

.calendar-dot.draft {
  background: #6c757d;
}

.calendar-dot.cancel {
  background: #b3201f;
}

.calendar-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 16px;
}

.calendar-legend span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 0.78rem;
  color: var(--bs-secondary-color, #6c757d);
}
</style>
