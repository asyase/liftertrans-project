import axios from 'axios'

export default {
  getSubcontractorsRequest() {
    // Küsime backendilt aktiivsete alltöövõtjate nimekirja
    return axios.get('/api/subcontractors')
  },
}
