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
}
