import router from '@/router/index.js'

export default {
  navigateToErrorView() {
    router.push('/error')
  },

  navigateToNotAuthorizedView() {
    router.push('/not-authorized')
  },
}
