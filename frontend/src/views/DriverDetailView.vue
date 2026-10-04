<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
import JobService from '@/services/JobService.js'
export default {
  name: 'DriverDetailView',

  data() {
    return {
      //null =  andmed pole veel backendist tulnud
      driver: null,

      jobs: [],
    }



  },

  beforeMount() {
    // Leht on ainult ADMIN-ile — teised suuname lehele "õigused puuduvad"
    if (!SessionStorageService.userIsAdmin()) {
      NavigationService.navigateToNotAuthorizedView()
    }
  },
}
</script>
