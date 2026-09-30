<template>
  <nav class="navbar navbar-expand-lg navbar-dark lt-navbar px-3 mb-4">
    <RouterLink class="navbar-brand" to="/login">LIFTERTRANS</RouterLink>
    <button
      class="navbar-toggler"
      type="button"
      data-bs-toggle="collapse"
      data-bs-target="#navMenu"
    >
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse justify-content-center" id="navMenu">
      <!-- ADMIN-i lingid -->
      <div v-if="isAdmin" class="navbar-nav">
        <RouterLink class="nav-link" to="/dashboard">Esileht</RouterLink>
        <RouterLink class="nav-link" to="/jobs">Tellimused</RouterLink>
        <RouterLink class="nav-link" to="/drivers">Juhid</RouterLink>
      </div>

      <!-- DRIVER-i lingid -->
      <div v-if="isDriver" class="navbar-nav">
        <RouterLink class="nav-link" to="/my-jobs">Minu tööd</RouterLink>
      </div>
    </div>

    <!-- Logi välja nuppu näitame ainult sisselogitud kasutajale -->
    <button
      v-if="isLoggedIn"
      type="button"
      class="btn btn-outline-light btn-sm ms-auto"
      @click="logout"
    >
      Logi välja
    </button>
  </nav>

  <!-- LoginView saadab pärast sisselogimist event-user-logged-in, siis uuendame menüü -->
  <RouterView @event-user-logged-in="updateNavMenu" />
</template>

<script>
export default {
  name: 'App',

  data() {
    return {
      // Loeme rolli kohe sessionStorage-ist, et lehe värskendamisel menüü ei kaoks
      roleName: sessionStorage.getItem('roleName'),
    }
  },

  computed: {
    isLoggedIn() {
      return this.roleName !== null
    },

    isAdmin() {
      return this.roleName === 'ADMIN'
    },

    isDriver() {
      return this.roleName === 'DRIVER'
    },
  },

  methods: {
    updateNavMenu() {
      this.roleName = sessionStorage.getItem('roleName')
    },

    // Kustutame sisselogitud kasutaja andmed, uuendame menüü ja suuname sisselogimise lehele
    logout() {
      sessionStorage.clear()
      this.updateNavMenu()
      this.$router.push('/login')
    },
  },
}
</script>
