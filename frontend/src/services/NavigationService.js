import router from '@/router/index.js'

export default {
  navigateToJobCreateView() {
    router.push('/jobs/new')
  },

  navigateToJobDetailView(jobId) {
    router.push(`/jobs/${jobId}`)
  },

  navigateToJobEditView(jobId) {
    router.push(`/jobs/${jobId}/edit`)
  },

  navigateToErrorView() {
    router.push('/error')
  },
}
