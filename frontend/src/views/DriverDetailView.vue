<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
import JobService from '@/services/JobService.js'
import DriverService from '@/services/DriverService.js'
export default {
  name: 'DriverDetailView',

  data() {
    return {
      //null =  andmed pole veel backendist tulnud
      driver: null,

      jobs: [],

      errorMessage: '',

      errorResponse: {
        message: '',
        errorCode: '',
      },
    }
  },

  methods: {
    getDriver() {
      // Driver id võtame URL-ist /drivers/:id
      const driverId = this.$route.params.id

      //kutsume servicelt juhi id, then - kui päring õnnestub ja response on serverist tulnud vastus.
      //catch kui päring ebaõnnestub
      DriverService.getDriverRequest(driverId)
        .then((response) => this.handleGetDriverResponse(response))
        .catch((error) => this.handleGetDriverErrorResponse(error))
    },

    //õnnestunud vastuse abimeetod
    handleGetDriverResponse(response) {
      this.driver = response.data
    },

    //ebaõnnestunud vastuse abimeetod
    handleGetDriverErrorResponse(error) {
      this.errorResponse = error.response.data

      // 404 = sellist juhti pole, näitame backendi teadet
      if (error.response.status === 404) {
        this.errorMessage = this.errorResponse.message
      } else {
        NavigationService.navigateToErrorView()
      }
    },
  },

  beforeMount() {
    // Leht on ainult ADMIN-ile — teised suuname lehele "õigused puuduvad"
    if (!SessionStorageService.userIsAdmin()) {
      NavigationService.navigateToNotAuthorizedView()
      return
    }

    //lehe avamisel lame ühe driver andmed
    this.getDriver()
  },
}
</script>
