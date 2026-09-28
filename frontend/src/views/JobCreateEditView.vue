<script>
import JobService from '@/services/JobService.js'
import CustomerService from '@/services/CustomerService.js'
import VehicleService from '@/services/VehicleService.js'
import DriverService from '@/services/DriverService.js'

export default {
  name: 'JobCreateEditView',
  computed: {
    filteredVehicles() {
      // Tellimusele saab valida ainult kasutuses oleva auto (mitte nt IN_SERVICE)
      const activeVehicles = this.vehicles.filter((vehicle) => vehicle.status === 'ACTIVE')

      // Oma ressursiga: ainult oma autod (alltöövõtjat pole)
      if (this.job.executionType === 'INTERNAL') {
        return activeVehicles.filter((vehicle) => vehicle.subcontractorId === null)
      }

      // Alltöövõtja: ainult valitud alltöövõtja autod (kuni alltöövõtja pole valitud, pole ka autosid)
      if (this.job.subcontractorId === null) {
        return []
      }
      return activeVehicles.filter(
        (vehicle) => vehicle.subcontractorId === this.job.subcontractorId,
      )
    },
  },

  watch: {
    // Teostamise viisi vahetamisel tühjendame eelmise viisi valikud,
    // muidu jääks nt peidetud juht alles ja backend lükkaks töö tagasi
    'job.executionType'() {
      this.job.driverId = null
      this.job.vehicleId = null
      this.job.subcontractorId = null
    },
  },

  data() {
    return {
      job: {
        customerId: null,
        jobType: '',
        executionType: 'INTERNAL',

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
    this.getDrivers()
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
      DriverService.getDriversRequest()
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
          this.errorMessage = 'Autode laadimine ebaõnnestus'
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

      // Backend ootab aegu Instant-ina (ISO, nt "2026-09-28T07:00:00.000Z"),
      // datetime-local annab aga ajavööndita "2026-09-28T10:00"
      const jobRequest = {
        ...this.job,
        plannedStartTime: this.toIsoString(this.job.plannedStartTime),
        plannedEndTime: this.toIsoString(this.job.plannedEndTime),
      }

      JobService.postJobRequest(jobRequest)
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

    toIsoString(dateTimeLocal) {
      // Tühi väli → null (backend annab plannedStartTime puhul selge veateate)
      if (!dateTimeLocal) {
        return null
      }
      return new Date(dateTimeLocal).toISOString()
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
    <!-- VIGA OLI: div-i sees ei olnud midagi (<div ...></div>), "input" ja type="radio" olid
         kirjutatud div-i atribuutideks. Raadionupp on eraldi <input /> tag div-i SEES. -->
    <!-- VIGA OLI: :value oli div-il, aga see peab olema input-il (div-il pole väärtust). -->
    <div
      v-for="executionType in executionTypes"
      :key="executionType.value"
      class="form-check form-check-inline"
    >
      <!-- v-model on kõigil nuppudel sama → Vue teab, et need on üks grupp (valida saab ühe) -->
      <!-- VIGA OLI: v-model="job.executionType" puudus nupul, seega valik ei jõudnud job-i -->
      <input
        type="radio"
        class="form-check-input"
        :id="executionType.value"
        :value="executionType.value"
        v-model="job.executionType"
      />
      <label class="form-check-label" :for="executionType.value">
        {{ executionType.text }}
      </label>
    </div>

    <div v-if="job.executionType === 'INTERNAL'">
      <label>Juht</label>

      <select v-model="job.driverId">
        <option :value="null">Vali juht</option>
        <option v-for="driver in drivers" :key="driver.driverId" :value="driver.driverId">
          {{ driver.name }}
        </option>
      </select>
    </div>

    <label>Auto</label>

    <select v-model="job.vehicleId">
      <option :value="null">Vali auto</option>

      <!-- filteredVehicles, mitte vehicles: näitame ainult valitud teostamise viisile sobivaid autosid -->
      <option
        v-for="vehicle in filteredVehicles"
        :key="vehicle.vehicleId"
        :value="vehicle.vehicleId"
      >
        {{ vehicle.registrationNumber }} ({{ vehicle.name }})
      </option>
    </select>

    <h3>3. Aadressid</h3>

    <!-- CRANE_ONLY → ainult töö aadress -->
    <div v-if="job.jobType === 'CRANE_ONLY'">
      <label>Töö aadress</label>
      <input v-model="job.serviceAddress" type="text" />
    </div>

    <!-- TRANSPORT_AND_CRANE → pealevõtu ja kohaletoimetamise aadress -->
    <div v-if="job.jobType === 'TRANSPORT_AND_CRANE'">
      <label>Pealevõtu aadress</label>
      <input v-model="job.pickupAddress" type="text" />

      <label>Kohaletoimetamise aadress</label>
      <input v-model="job.deliveryAddress" type="text" />
    </div>

    <h3>4. Aeg</h3>

    <label>Planeeritud algus</label>
    <input v-model="job.plannedStartTime" type="datetime-local" />

    <label>Planeeritud lõpp</label>
    <input v-model="job.plannedEndTime" type="datetime-local" />

    <button @click="createJob" :disabled="isLoading" class="btn btn-success">Salvesta</button>

    <button class="btn btn-secondary">Tühista</button>
  </div>
</template>
