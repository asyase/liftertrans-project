<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
import DriverService from '@/services/DriverService.js'
import axios from "axios";

export default {
  name: 'DriversView',

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

      errorMessage: '',

      errorResponse: {
        message: '',
        errorCode: '',
      },
    }
  },

  methods: {
    getDrivers() {
      DriverService.getDriversRequest()

        .then((response) => this.handleGetDrivers(response))
        .catch((error) => this.handleGetDriversErrorResponse(error))
    },

    handleGetDrivers(response) {
      this.drivers = response.data
    },
    handleGetDriversErrorResponse(error) {
      this.errorMessage = 'Juhtide laadimine ebaõnnestus.'
    },

    deleteDriver(driverId) {

      // kinnitusaken
      if (confirm('Kas oled kindel, et soovid selle juhi kustutada?')) {

      // saadame backendile kustutamise päringu
      axios.delete('/api/drivers/' + driverId)
        .then(() => {

          // värskendame tabelit
          this.getDrivers()
        })

          .catch(error => {
            this.errorMessage = 'Kustutamine ebaõnnestus'
          })
      }
    }
  },
}
</script>

<template>
  <div class="container-fluid px-4">
    <div v-if="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>

    <h1>Juhid</h1>

    <div class="row">
      <div class="col-md-4">
        <input v-model="searchText" type="text" class="form-control" placeholder="Otsi juhti" />
      </div>
      <div class="col-auto">
        <RouterLink :to="{ name: 'driver-create' }" class="btn btn-danger">+ Lisa juht</RouterLink>
      </div>
    </div>
    <table class="table table-hover">
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
        <tr v-for="driver in drivers" :key="driver.driverId">
          <td>{{ driver.name }}</td>
          <td>{{ driver.phone }}</td>
          <td>{{ driver.email }}</td>
          <td>{{ driver.active ? 'Aktiivne' : 'Mitteaktiivne' }}</td>
          <td>
            <RouterLink
              :to="{ name: 'driver-detail', params: { id: driver.driverId } }"
              class="btn btn-danger me-2"
            >
              Vaata
            </RouterLink>

            <RouterLink
              :to="{ name: 'driver-edit', params: { id: driver.driverId } }"
              class="btn btn-outline-secondary me-2"
            >
              Muuda
            </RouterLink>

            <button class="btn btn-dark" @click="deleteDriver(driver.driverId)">Kustuta</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
