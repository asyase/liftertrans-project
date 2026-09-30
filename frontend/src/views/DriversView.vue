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

    goToAddDriver() {
      this.$router.push('/drivers/new')
    },

    goToViewDriver(driverId) {
      this.$router.push('/drivers/' + driverId)
    },

    goToEditDriver(driverId) {
      this.$router.push('/drivers/' + driverId + '/edit')
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
        <button class="btn btn-danger" @click="goToAddDriver">+ Lisa juht</button>
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
            <button class="btn btn-danger me-2" @click="goToViewDriver(driver.driverId)">
              Vaata
            </button>

            <button class="btn btn-outline-secondary me-2" @click="goToEditDriver(driver.driverId)">
              Muuda
            </button>

            <button class="btn btn-dark" @click="deleteDriver(driver.driverId)">Kustuta</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
