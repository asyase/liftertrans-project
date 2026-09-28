import axios from 'axios'

export default {
  getDriversRequest() {
    return axios.get('/api/drivers')
  },
}
