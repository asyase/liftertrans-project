import axios from 'axios'

export default {
  getDriversRequest() {
    return axios.get('/api/drivers')
  },

  // küsime backilt 1 juhi andmed
  getDriverRequest(driverId) {
    return axios.get('/api/drivers/' + driverId)
  },
}
