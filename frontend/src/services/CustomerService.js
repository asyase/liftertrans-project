import axios from 'axios'

export default {
  getCustomersRequest(search) {
    // Küsime backendilt klientide nimekirja (search on valikuline)
    return axios.get('/api/customers', {
      params: {
        search: search || null,
      },
    })
  },

  deleteCustomerRequest(customerId) {
    // Kustutame kliendi ID järgi
    return axios.delete('/api/customers/' + customerId)
  },
}
