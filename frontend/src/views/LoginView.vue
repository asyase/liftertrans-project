<script>
import AuthService from '@/services/AuthService.js'

export default {
  name: 'LoginView',

  // Vormi andmed ja olek
  data() {
    return {
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
        return this.$router.push('/dashboard')
      }

      if (user.roleName === 'DRIVER') {
        return this.$router.push('/my-jobs')
      }

      this.errorMessage='Tundmatu kasutajaroll'
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
          <input v-model="email" type="email" class="form-control" placeholder="E-post" />
          <label>Kasutajanimi / e-post</label>
        </div>

        <div class="form-floating mb-3">
          <input v-model="password" type="password" class="form-control" placeholder="Parool" />
          <label>Parool</label>
        </div>

        <button type="button" @click="login" class="btn btn-danger">Logi sisse</button>
      </div>
    </div>
  </div>
</template>
