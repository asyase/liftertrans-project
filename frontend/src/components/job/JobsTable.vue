<script>
export default {
  name: 'JobsTable',

  props: {
    jobs: {},
  },

  emits: ['event-job-view-click', 'event-job-edit-click'],

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

      return jobType
    },
  },
}
</script>

<template>
  <table class="table table-hover">
    <thead>
      <tr>
        <th scope="col">ID</th>
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
      <!-- VIGA OLI: kasutasin job.id, aga backend saadab välja nimega jobId → ID veerg oli tühi
           ja nupud Vaata/Muuda saatsid undefined. -->
      <tr v-for="job in jobs" :key="job.jobId">
        <td>{{ job.jobId }}</td>

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
          <button
            @click="$emit('event-job-view-click', job.jobId)"
            class="btn btn-sm btn-outline-primary me-2"
          >
            Vaata
          </button>

          <button
            @click="$emit('event-job-edit-click', job.jobId)"
            class="btn btn-sm btn-outline-secondary"
          >
            Muuda
          </button>
        </td>
      </tr>
    </tbody>
  </table>
</template>
