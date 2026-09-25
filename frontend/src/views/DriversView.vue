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
    }
  },

  methods: {
    getDrivers() {

      DriverService.getDriversRequest(this.drivers)
        .then((response) => this.handleGetDrivers(responce))
        .catch()
    }
  }


}
</script>
