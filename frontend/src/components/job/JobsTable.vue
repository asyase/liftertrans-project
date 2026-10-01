<script>
export default {
  name: 'JobsTable',

  props: {
    jobs: {},
  },

  methods: {
    formatDate(dateTime) {
      // Kui kuupäeva ei ole, kuvame "-"
      if (!dateTime) {
        return '-'
      }

      return new Date(dateTime).toLocaleDateString('et-EE')
    },

    getDriverOrSubcontractor(job) {
      // Alltöövõtja olemasolul kuvame teda, muidu juhti
      if (job.subcontractorName) {
        return job.subcontractorName
      }

      return job.driverName || '-'
    },

    getPickupOrServiceAddress(job) {
      // Kraanatööl kasutatakse töö aadressi
      if (job.jobType === 'CRANE_ONLY') {
        return job.serviceAddress || '-'
      }

      return job.pickupAddress || '-'
    },

    getDeliveryAddress(job) {
      // Kraanatööl kohaletoimetamise aadress puudub
      if (job.jobType === 'CRANE_ONLY') {
        return '-'
      }

      return job.deliveryAddress || '-'
    },

    formatJobType(jobType) {
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
  },
}
</script>

<template>
  <table class="table table-hover">
    <thead>
      <tr>
        <th scope="col">Nr</th>
        <th scope="col">Kuupäev</th>
        <th scope="col">Klient</th>
        <th scope="col">Töö tüüp</th>
        <th scope="col">Pealevõtt / töö aadress</th>
        <th scope="col">Kohaletoimetamine</th>
        <th scope="col">Auto</th>
        <th scope="col">Juht / alltöövõtja</th>
        <th scope="col">Staatus</th>
        <th scope="col">Tegevused</th>
      </tr>
    </thead>

    <tbody>
      <!-- Nr on lihtsalt rea järjekorranumber (index algab 0-st, seepärast + 1), mitte töö ID -->
      <tr v-for="(job, index) in jobs" :key="job.jobId">
        <td>{{ index + 1 }}</td>

        <td>
          {{ formatDate(job.plannedStartTime) }}
        </td>

        <td>
          {{ job.customerName }}
        </td>

        <td>
          {{ formatJobType(job.jobType) }}
        </td>

        <td>
          {{ getPickupOrServiceAddress(job) }}
        </td>

        <td>
          {{ getDeliveryAddress(job) }}
        </td>

        <td>
          {{ job.vehicleRegistrationNumber || '-' }}
        </td>

        <td>
          {{ getDriverOrSubcontractor(job) }}
        </td>

        <td>
          {{ job.status }}
        </td>

        <td>
          <!-- Üleminek teisele lehele → RouterLink (saab avada ka uues vahekaardis) -->
          <!-- TODO: kui /jobs/:id/edit rada on routeris olemas, kasuta ka Muuda juures nime: { name: 'job-edit', params: { id } } -->
          <RouterLink
            :to="{ name: 'job-detail', params: { id: job.jobId } }"
            class="btn btn-sm btn-outline-primary me-2"
          >
            Vaata
          </RouterLink>

          <RouterLink :to="`/jobs/${job.jobId}/edit`" class="btn btn-sm btn-outline-secondary">
            Muuda
          </RouterLink>
        </td>
      </tr>
    </tbody>
  </table>
</template>
