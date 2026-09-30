import axios from 'axios'

export default {
  getCustomersRequest() {
    return axios.get('/api/customers')
  },
}
