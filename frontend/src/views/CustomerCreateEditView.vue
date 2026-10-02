<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'

export default {
  name: 'CustomerCreateEditView',

  beforeMount() {
    // Leht on ainult ADMIN-ile — teised suuname lehele "õigused puuduvad"
    if (!SessionStorageService.userIsAdmin()) {
      NavigationService.navigateToNotAuthorizedView()
      return
    }

    this.getCustomers()
  },

  data() {
    return {
      customer: {
        customer: [],
        errorMessage: '',
        errorResponse: {
          message: '',
          errorCode: '',
        }
      }
    },
  }
  methods: {
    login() {
      // Kustuta eelmine veateade
      this.errorMessage = ''

      // Kontrolli, kas väljad on täidetud
      if (!this.name || !this.companyName ) {
        this.errorMessage = 'Täida kõik väljad'
        return
      }

      // Valmista backendile saadetavad andmed
      const authRequestDto = {
        email: this.email,
        password: this.password,
      }
    },
  },
}
</script>

<template></template>

<style scoped></style>
