<template>
  <div class="d-flex flex-column flex-md-row">
    <!-- Külgmenüü on kõigil lehtedel, aga ainult sisselogitud kasutajale (login lehel pole) -->
    <AppSidebar v-if="isLoggedIn" :role-name="roleName" @event-logout="logout" />

    <main class="flex-grow-1 py-4 lt-main">
      <!-- LoginView saadab pärast sisselogimist event-user-logged-in, siis uuendame menüü -->
      <RouterView @event-user-logged-in="updateNavMenu" />
    </main>
  </div>
</template>

<script>
import AppSidebar from '@/components/AppSidebar.vue'

export default {
  name: 'App',

  components: {
    AppSidebar,
  },

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

<style>
/* Sisu võtab ülejäänud laiuse; min-width: 0, et laiad tabelid ei lükkaks lehte laiemaks */
.lt-main {
  min-width: 0;
}
</style>
