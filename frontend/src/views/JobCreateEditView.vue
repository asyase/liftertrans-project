<script>
import JobService from '@/services/JobService.js'
import CustomerService from '@/services/CustomerService.js'
import VehicleService from '@/services/VehicleService.js'

export default {
  name: 'JobCreateEditView',
  computed: {},

  data() {
    return {
      job: {
        customerId: null,
        jobType: '',
        executionType: '',

        vehicleId: null,
        driverId: null,
        subcontractorId: null,

        pickupAddress: '',
        deliveryAddress: '',
        serviceAddress: '',

        receiverName: '',
        receiverPhone: '',

        plannedStartTime: '',
        plannedEndTime: '',

        estimatedKm: null,
        estimatedHours: null,

        notes: '',
      },
      errorMessage: '',
      isLoading: false,

      // Täidetakse backendist (beforeMount)
      customers: [],
      jobTypes: [],
      executionTypes: [],
      drivers: [],
      vehicles: [],
    }
  },
  beforeMount() {
    this.getCustomers()
    this.getJobTypes()
    this.getExecutionTypes()
    //  this.getDrivers()
    this.getVehicles()
  },
  methods: {
    getCustomers() {
      CustomerService.getCustomersRequest()
        .then((response) => {
          this.customers = response.data
        })
        .catch(() => {
          this.errorMessage = 'Klientide laadimine ebaõnnestus'
        })
    },
    getDrivers() {
      JobService.getDriversRequest()
        .then((response) => {
          this.drivers = response.data
        })
        .catch(() => {
          this.errorMessage = 'Juhtide laadimine ebaõnnestus'
        })
    },
    getJobTypes() {
      JobService.getJobTypesRequest()
        .then((response) => {
          this.jobTypes = response.data
        })
        .catch(() => {
          this.errorMessage = 'Töö tüüpide laadimine ebaõnnestus'
        })
    },
    getVehicles() {
      VehicleService.getVehiclesRequest()
        .then((response) => {
          this.vehicles = response.data
        })
        .catch(() => {
          this.errorMessage = 'not ok'
        })
    },

    getExecutionTypes() {
      JobService.getExecutionTypesRequest()
        .then((response) => {
          this.executionTypes = response.data
        })
        .catch(() => {
          this.errorMessage = 'Teostamise viiside laadimine ebaõnnestus'
        })
    },

    createJob() {
      // Kustutame eelmise veateate
      this.errorMessage = ''

      // Alustame laadimist
      this.isLoading = true

      // vaja saata this.job backi
      JobService.postJobRequest(this.job)
        .then((response) => {
          const jobId = response.data.jobId

          this.$router.push(`/jobs/${jobId}/edit`)
        })
        .catch((error) => {
          this.errorMessage = error.response?.data?.message || 'Töö loomine ebaõnnestus'
        })
        .finally(() => {
          this.isLoading = false
        })
    },
  },
}
</script>
<template>
  <div class="container">
    <h1>Lisa uus tellimus</h1>

    <!-- Veateade -->
    <div v-if="errorMessage">
      {{ errorMessage }}
    </div>

    <h3>1. Klient</h3>

    <label>Klient</label>
    <select v-model="job.customerId">
      <option :value="null">Vali klient</option>
      <option v-for="customer in customers" :key="customer.id" :value="customer.id">
        {{ customer.companyName }}-{{ customer.name }}
      </option>
    </select>
    <label>Vastuvõtja nimi</label>
    <input v-model="job.receiverName" type="text" />

    <label>Vastuvõtja telefon</label>
    <input v-model="job.receiverPhone" type="tel" />

    <h3>2. Töö</h3>
    <select v-model="job.jobType">
      <option value="">Vali töö tüüp</option>
      <option v-for="jobType in jobTypes" :key="jobType.value" :value="jobType.value">
        {{ jobType.text }}
      </option>
    </select>

    <label>Täitmise tüüp</label>

    <select v-model="job.executionType">
      <option value="">Vali täitmise tüüp</option>
      <option
        v-for="executionType in executionTypes"
        :key="executionType.value"
        :value="executionType.value"
      >
        {{ executionType.text }}
      </option>
    </select>

    <label>Täitmise tüüp</label>

    <select v-model="job.executionType">
      <option value="">Vali täitmise tüüp</option>
      <option
        v-for="executionType in executionTypes"
        :key="executionType.value"
        :value="executionType.value"
      >
        {{ executionType.text }}
      </option>
    </select>

    <label>Juht</label>

    <select v-model="job.driverId">
      <option :value="null">Vali juht</option>

      <option v-for="driver in drivers" :key="driver.driverId" :value="driver.driverId">
        {{ driver.name }}
      </option>
    </select>

    <label>Auto</label>

    <select v-model="job.vehicleId">
      <option :value="null">Vali auto</option>

      <option v-for="vehicle in vehicles" :key="vehicle.vehicleId" :value="vehicle.vehicleId">
        {{ vehicle.name }}
      </option>
    </select>

    <button @click="createJob" :disabled="isLoading" class="btn btn-success">Salvesta</button>

    <button class="btn btn-secondary">Tühista</button>
  </div>
</template>
