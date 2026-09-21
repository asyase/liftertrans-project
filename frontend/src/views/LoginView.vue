<script>
export default {
  name: 'LoginView',

  data() {
    return {
      email: '',
      password: '',
      errorMessage: '',
      isLoading: false,
    }
  },

  methods: {
    login() {
      this.errorMessage = ''

      if (!this.email) {
        this.errorMessage = 'E-post on kohustuslik'
        return
      }
      if (!this.password) {
        this.errorMessage = 'Parool on kohustuslik'
        return
      }

      this.isLoading = true
      const isAdmin = this.email === 'admin@liftertrans.ee' && this.password === 'admin123'
      const isDriver = this.email === 'mart.tamm@liftertrans.ee' && this.password === 'driver123'
      if (!isAdmin && !isDriver) {
        this.errorMessage = 'Vale e-post või parool'
        this.isLoading = false
      }

      this.isLoading = false
      console.log('Sisselogimine õnnestus')
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

        <button type="button" @click="login" :disabled="isLoading" class="btn btn-danger">
          Logi sisse
        </button>
        <div class="spinner-border text-danger" role="status">
          <span class="visually-hidden">Login sisse...</span>
        </div>
      </div>
    </div>
  </div>
</template>
