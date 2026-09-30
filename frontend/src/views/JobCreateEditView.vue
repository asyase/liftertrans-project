<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
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
    // Leht on ainult ADMIN-ile — teised suuname lehele "õigused puuduvad"
    if (!SessionStorageService.userIsAdmin()) {
      NavigationService.navigateToNotAuthorizedView()
      return
    }

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

          // Muutmise vaadet (/jobs/:id/edit) veel pole, seega suuname tellimuste nimekirja.
          // Teate anname kaasa URL-i query parameetrina, JobsView loeb selle beforeMount-is välja.
          this.$router.push({
            path: '/jobs',
            query: { successMessage: `Tellimus #${jobId} loodud` },
          })
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
  <div class="container pb-5" style="max-width: 960px">
    <h1 class="mb-4">Lisa uus tellimus</h1>

    <!-- Veateade -->
    <div v-if="errorMessage" class="alert alert-danger" role="alert">
      {{ errorMessage }}
    </div>

    <div class="card lt-section mb-3">
      <div class="card-body">
        <h3 class="card-title mb-3">1. Klient</h3>

        <div class="row g-3">
          <div class="col-12">
            <label for="customerId" class="form-label">Klient</label>
            <select id="customerId" v-model="job.customerId" class="form-select">
              <option :value="null">Vali klient</option>
              <option v-for="customer in customers" :key="customer.id" :value="customer.id">
                {{ customer.companyName }}-{{ customer.name }}
              </option>
            </select>
          </div>

          <div class="col-md-6">
            <label for="receiverName" class="form-label">Vastuvõtja nimi</label>
            <input id="receiverName" v-model="job.receiverName" type="text" class="form-control" />
          </div>

          <div class="col-md-6">
            <label for="receiverPhone" class="form-label">Vastuvõtja telefon</label>
            <input id="receiverPhone" v-model="job.receiverPhone" type="tel" class="form-control" />
          </div>
        </div>
      </div>
    </div>

    <div class="card lt-section mb-3">
      <div class="card-body">
        <h3 class="card-title mb-3">2. Töö</h3>

        <div class="row g-3">
          <div class="col-md-6">
            <label for="jobType" class="form-label">Töö tüüp</label>
            <select id="jobType" v-model="job.jobType" class="form-select">
              <option value="">Vali töö tüüp</option>
              <option v-for="jobType in jobTypes" :key="jobType.value" :value="jobType.value">
                {{ jobType.text }}
              </option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label d-block">Täitmise tüüp</label>
            <div
              v-for="executionType in executionTypes"
              :key="executionType.value"
              class="form-check form-check-inline mt-2"
            >
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
          </div>

          <div v-if="job.executionType === 'INTERNAL'" class="col-md-6">
            <label for="driverId" class="form-label">Juht</label>
            <select id="driverId" v-model="job.driverId" class="form-select">
              <option :value="null">Vali juht</option>
              <option v-for="driver in drivers" :key="driver.driverId" :value="driver.driverId">
                {{ driver.name }}
              </option>
            </select>
          </div>

          <div class="col-md-6">
            <label for="vehicleId" class="form-label">Auto</label>
            <select id="vehicleId" v-model="job.vehicleId" class="form-select">
              <option :value="null">Vali auto</option>
              <option
                v-for="vehicle in filteredVehicles"
                :key="vehicle.vehicleId"
                :value="vehicle.vehicleId"
              >
                {{ vehicle.registrationNumber }} ({{ vehicle.name }})
              </option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <div class="card lt-section mb-3">
      <div class="card-body">
        <h3 class="card-title mb-3">3. Aadressid</h3>

        <p v-if="!job.jobType" class="text-body-secondary mb-0">Vali esmalt töö tüüp.</p>

        <!-- CRANE_ONLY → ainult töö aadress -->
        <div v-if="job.jobType === 'CRANE_ONLY'">
          <label for="serviceAddress" class="form-label">Töö aadress</label>
          <input
            id="serviceAddress"
            v-model="job.serviceAddress"
            type="text"
            class="form-control"
          />
        </div>

        <!-- TRANSPORT_AND_CRANE → pealevõtu ja kohaletoimetamise aadress -->
        <div v-if="job.jobType === 'TRANSPORT_AND_CRANE'" class="row g-3">
          <div class="col-md-6">
            <label for="pickupAddress" class="form-label">Pealevõtu aadress</label>
            <input
              id="pickupAddress"
              v-model="job.pickupAddress"
              type="text"
              class="form-control"
            />
          </div>
          <div class="col-md-6">
            <label for="deliveryAddress" class="form-label">Kohaletoimetamise aadress</label>
            <input
              id="deliveryAddress"
              v-model="job.deliveryAddress"
              type="text"
              class="form-control"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="card lt-section mb-4">
      <div class="card-body">
        <h3 class="card-title mb-3">4. Aeg</h3>

        <div class="row g-3">
          <div class="col-md-6">
            <label for="plannedStartTime" class="form-label">Planeeritud algus</label>
            <input
              id="plannedStartTime"
              v-model="job.plannedStartTime"
              type="datetime-local"
              class="form-control"
            />
          </div>
          <div class="col-md-6">
            <label for="plannedEndTime" class="form-label">Planeeritud lõpp</label>
            <input
              id="plannedEndTime"
              v-model="job.plannedEndTime"
              type="datetime-local"
              class="form-control"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="d-flex gap-2 justify-content-end">
      <button class="btn btn-outline-secondary">Tühista</button>
      <button @click="createJob" :disabled="isLoading" class="btn btn-primary px-4">
        Salvesta
      </button>
    </div>
  </div>
</template>
