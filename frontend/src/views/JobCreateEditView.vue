<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
import JobService from '@/services/JobService.js'
import CustomerService from '@/services/CustomerService.js'
import VehicleService from '@/services/VehicleService.js'
import SubcontractorService from '@/services/SubcontractorService.js'
import DriverService from '@/services/DriverService.js'

export default {
  name: 'JobCreateEditView',
  computed: {
    plannedHours() {
      // Planeeritud töötunnid = lõpp - algus. Kui üks aeg puudub või lõpp pole pärast algust, siis null
      if (!this.job.plannedStartTime || !this.job.plannedEndTime) {
        return null
      }
      const milliseconds = new Date(this.job.plannedEndTime) - new Date(this.job.plannedStartTime)
      if (milliseconds <= 0) {
        return null
      }
      // Ümardame ühe komakohani, nt 4 või 2.5
      return Math.round((milliseconds / 3600000) * 10) / 10
    },

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

    // Teise alltöövõtja valimisel eelmise alltöövõtja auto enam ei sobi
    'job.subcontractorId'() {
      this.job.vehicleId = null
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
      subcontractors: [],
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
    this.getSubcontractors()
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

    getSubcontractors() {
      SubcontractorService.getSubcontractorsRequest()
        .then((response) => {
          this.subcontractors = response.data
        })
        .catch(() => {
          this.errorMessage = 'Alltöövõtjate laadimine ebaõnnestus'
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
        // Planeeritud töötunnid arvutatakse algus- ja lõpuajast (kasutaja ise ei sisesta)
        estimatedHours: this.plannedHours,
        // Tühjaks kustutatud numbriväli annab '' — saadame siis null
        estimatedKm: this.job.estimatedKm === '' ? null : this.job.estimatedKm,
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
              <option
                v-for="customer in customers"
                :key="customer.customerId"
                :value="customer.customerId"
              >
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

          <!-- Alltöövõtja valik: selle järgi näitame Auto rippmenüüs ainult tema autosid -->
          <div v-if="job.executionType === 'SUBCONTRACTED'" class="col-md-6">
            <label for="subcontractorId" class="form-label">Alltöövõtja</label>
            <select id="subcontractorId" v-model="job.subcontractorId" class="form-select">
              <option :value="null">Vali alltöövõtja</option>
              <option
                v-for="subcontractor in subcontractors"
                :key="subcontractor.subcontractorId"
                :value="subcontractor.subcontractorId"
              >
                {{ subcontractor.companyName }}
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

        <!-- TRANSPORT ja TRANSPORT_AND_CRANE → pealevõtu ja kohaletoimetamise aadress -->
        <div
          v-if="job.jobType === 'TRANSPORT' || job.jobType === 'TRANSPORT_AND_CRANE'"
          class="row g-3"
        >
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

        <!-- Kilometraaž sisestatakse käsitsi (kõigil töö tüüpidel, ka kraanatööl sõit objektile) -->
        <div v-if="job.jobType" class="row g-3 mt-0">
          <div class="col-md-6">
            <label for="estimatedKm" class="form-label">Planeeritud kilometraaž</label>
            <div class="input-group">
              <input
                id="estimatedKm"
                v-model.number="job.estimatedKm"
                type="number"
                min="0"
                step="0.1"
                class="form-control"
              />
              <span class="input-group-text">km</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card lt-section mb-4">
      <div class="card-body">
        <h3 class="card-title mb-3">4. Planeeritud aeg</h3>

        <div class="row g-3">
          <div class="col-md-6">
            <label for="plannedStartTime" class="form-label">Planeeritud algusaeg</label>
            <input
              id="plannedStartTime"
              v-model="job.plannedStartTime"
              type="datetime-local"
              class="form-control"
            />
          </div>
          <div class="col-md-6">
            <label for="plannedEndTime" class="form-label">Planeeritud lõpuaeg</label>
            <input
              id="plannedEndTime"
              v-model="job.plannedEndTime"
              type="datetime-local"
              class="form-control"
            />
          </div>
          <div class="col-md-6">
            <label for="plannedHours" class="form-label">Planeeritud töötunnid</label>
            <div class="input-group">
              <input
                id="plannedHours"
                :value="plannedHours ?? '—'"
                type="text"
                class="form-control"
                readonly
              />
              <span class="input-group-text">h (arvutatud)</span>
            </div>
          </div>
        </div>

        <p class="text-body-secondary small mt-3 mb-0">
          Tegelikku algus- ja lõpuaega siin ei sisestata.<br />
          Need salvestatakse automaatselt, kui juht töö alustab ja lõpetab.
        </p>
      </div>
    </div>

    <div class="d-flex gap-2 justify-content-end">
      <RouterLink :to="{ name: 'jobsRoute' }" class="btn btn-outline-secondary">Tühista</RouterLink>
      <button @click="createJob" :disabled="isLoading" class="btn btn-primary px-4">
        Salvesta
      </button>
    </div>
  </div>
</template>
