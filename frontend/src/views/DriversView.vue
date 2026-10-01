<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
import DriverService from '@/services/DriverService.js'
import axios from 'axios'
import SearchBar from '@/components/SearchBar.vue'

export default {
  name: 'DriversView',
  components: { SearchBar },

  // Lehe avamisel laadime tööd backendist
  beforeMount() {
    // Leht on ainult ADMIN-ile — teised suuname lehele "õigused puuduvad"
    if (!SessionStorageService.userIsAdmin()) {
      NavigationService.navigateToNotAuthorizedView()
      return
    }

    this.getDrivers()
  },

  data() {
    return {
      drivers: [],
      searchText: '',
      appliedSearchText: '',

      errorMessage: '',
      successMessage: '',

      errorResponse: {
        message: '',
        errorCode: '',
      },
    }
  },

  computed: {
    filteredDrivers() {
      const search = this.appliedSearchText.trim().toLowerCase()

      if (!search) {
        return this.drivers
      }

      return this.drivers.filter((driver) => driver.name.toLowerCase().includes(search))
    },
  },

  methods: {
    getDrivers() {
      DriverService.getDriversRequest()

        .then((response) => this.handleGetDrivers(response))
        .catch(() => this.handleGetDriversErrorResponse())
    },

    handleGetDrivers(response) {
      this.drivers = response.data
    },
    handleGetDriversErrorResponse() {
      this.errorMessage = 'Juhtide laadimine ebaõnnestus.'
    },

    deleteDriver(driverId) {
      // kinnitusaken
      if (confirm('Kas oled kindel, et soovid selle juhi kustutada?')) {
        // saadame backendile kustutamise päringu
        axios
          .delete('/api/drivers/' + driverId)
          .then(() => {
            this.successMessage = 'Juht on kustutatud.'
            // värskendame tabelit
            this.getDrivers()
          })

          .catch((error) => {
            // Näitame backendi tegelikku veateadet (nt "juhiga on seotud tellimusi"),
            // kui seda pole, siis üldist teadet
            this.errorMessage = error.response?.data?.message ?? 'Kustutamine ebaõnnestus'
          })
      }
    },

    applyDriversSearch() {
      this.appliedSearchText = this.searchText
    },
  },
}
</script>

<template>
  <div class="container-fluid px-4">
    <div v-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>
    <div v-if="successMessage" class="alert alert-success">
      {{ successMessage}}
    </div>

    <h1 class="mb-4">Juhid</h1>

    <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
      <SearchBar
        v-model="searchText"
        placeholder="Otsi juhti"
        @search="applyDriversSearch"
      />

      <RouterLink :to="{ name: 'driver-create' }" class="btn btn-primary">+ Lisa juht</RouterLink>
    </div>

    <div class="table-responsive">
      <table class="table table-hover align-middle">
        <thead>
          <tr>
            <th scope="col">Nimi</th>
            <th scope="col">Telefon</th>
            <th scope="col">E-post</th>
            <th scope="col">Staatus</th>
            <th scope="col">Tegevused</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="driver in filteredDrivers" :key="driver.driverId">
            <td>{{ driver.name }}</td>
            <td>{{ driver.phone }}</td>
            <td>{{ driver.email }}</td>
            <td>{{ driver.active ? 'Aktiivne' : 'Mitteaktiivne' }}</td>
            <td class="text-nowrap">
              <RouterLink
                :to="{ name: 'driver-detail', params: { id: driver.driverId } }"
                class="btn btn-sm btn-outline-primary me-2"
              >
                Vaata
              </RouterLink>

              <RouterLink
                :to="{ name: 'driver-edit', params: { id: driver.driverId } }"
                class="btn btn-sm btn-outline-secondary me-2"
              >
                Muuda
              </RouterLink>

              <button
                class="btn btn-sm btn-outline-dark"
                @click="deleteDriver(driver.driverId)"
              >
                Kustuta
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
