<script>
import AuthService from '@/services/AuthService.js'
import liftertransLogo from '@/assets/liftertrans-logo.png'

export default {
  name: 'LoginView',

  // Anname App.vue-le teada, et kasutaja logis sisse (menüü tuleb uuendada)
  emits: ['event-user-logged-in'],

  // Vormi andmed ja olek
  data() {
    return {
      logoUrl: liftertransLogo,
      email: '',
      password: '',
      errorMessage: '',
    }
  },

  methods: {
    login() {
      // Kustuta eelmine veateade
      this.errorMessage = ''

      // Kontrolli, kas väljad on täidetud
      if (!this.email || !this.password) {
        this.errorMessage = 'Täida kõik väljad'
        return
      }

      // Valmista backendile saadetavad andmed
      const authRequestDto = {
        email: this.email,
        password: this.password,
      }

      // Saada login-päring backendile
      AuthService.postLoginRequest(authRequestDto)
        .then((response) => this.handleLoginResponse(response)) // Edukas vastus
        .catch((error) => this.handleLoginError(error)) // Veateade
    },

    // Töötle edukat sisselogimist
    handleLoginResponse(response) {
      // Võta backendist kasutaja andmed
      const user = response.data

      console.log('Sisselogimine ok')
      console.log('Roll', user.roleName)

      if (user.roleName === 'ADMIN') {
        this.saveLoggedInUser(user)
        return this.$router.push('/dashboard')
      }

      if (user.roleName === 'DRIVER') {
        this.saveLoggedInUser(user)
        return this.$router.push('/my-jobs')
      }

      this.errorMessage = 'Tundmatu kasutajaroll'
    },

    // Salvestame sisselogitud kasutaja andmed brauserisse (sessionStorage kaob vahelehe sulgemisel)
    saveLoggedInUser(user) {
      sessionStorage.setItem('userId', user.userId)
      sessionStorage.setItem('roleName', user.roleName)
      // ADMIN-il driverId puudub (null) — salvestame siis tühja stringi, mitte "null"
      sessionStorage.setItem('driverId', user.driverId ?? '')

      // App.vue kuulab seda sündmust ja näitab menüüs õige rolli lingid
      this.$emit('event-user-logged-in')
    },

    // Töötle sisselogimise viga
    handleLoginError(error) {
      // Näita backendi veateadet või üldist veateadet
      this.errorMessage = error.response?.data?.message || 'Sisselogimine ebaõnnestus'
    },
  },
}
</script>

<template>
  <div class="container text-center">
    <!-- LIFTERTRANS logo -->
    <div class="mb-4 mt-4">
      <img :src="logoUrl" alt="LIFTERTRANS" class="lt-login-logo" />
    </div>

    <div class="row justify-content-center">
      <div class="col col-6">
        <div v-if="errorMessage" class="alert alert-danger mb-3" role="alert">
          {{ errorMessage }}
        </div>
      </div>
    </div>

    <div class="row justify-content-center">
      <div class="col col-3">
        <h2>Logi sisse</h2>

        <div class="form-floating mb-3">
          <input v-model="email" @keyup.enter="login" type="email" class="form-control" placeholder="E-post" />
          <label>Kasutajanimi / e-post</label>
        </div>

        <div class="form-floating mb-3">
          <input v-model="password" @keyup.enter="login" type="password" class="form-control" placeholder="Parool" />
          <label>Parool</label>
        </div>

        <button type="button" @click="login" class="btn btn-danger">Logi sisse</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* LIFTERTRANS logo: piiratud laius, et see ei oleks liiga suur ja mahuks mobiilis */
.lt-login-logo {
  width: 100%;
  max-width: 340px;
  height: auto;
}
</style>
