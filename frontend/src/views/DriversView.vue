<script>
import DriverService from '@/services/DriverService.js'

export default {
  name: 'DriversView',

  // Lehe avamisel laadime tööd backendist
  beforeMount() {
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
        <button class="btn btn-danger">+ Lisa juht</button>
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
          <td></td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
