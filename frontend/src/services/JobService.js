import axios from 'axios'

export default {
  getJobsRequest(filters) {
    // Saadame backendile ainult valitud filtrid
    return axios.get('/api/jobs', {
      params: {
        date: filters.date || null,
        vehicleId: filters.vehicleId || null,
        driverId: filters.driverId || null,
        status: filters.status || null,
      },
    })
  },

  getJobTypesRequest() {
    // Küsime backendilt töö tüüpide nimekirja
    return axios.get('/api/jobs/types')
  },

  getExecutionTypesRequest() {
    // Küsime backendilt teostamise viiside nimekirja
    return axios.get('/api/jobs/execution-types')
  },

  postJobRequest(jobCreateRequestDto) {
    // Saadame uue töö andmed backendile
    return axios.post('/api/jobs', jobCreateRequestDto)
  },
}
