<script>
export default {
  name: 'AppSidebar',

  props: {
    // ADMIN või DRIVER — selle järgi näitame menüü linke
    roleName: String,
  },

  emits: ['event-logout'],

  computed: {
    isAdmin() {
      return this.roleName === 'ADMIN'
    },

    isDriver() {
      return this.roleName === 'DRIVER'
    },
  },

  methods: {
    // Link on aktiivne ka alamlehtedel, nt /jobs/4 peal on "Tellimused" aktiivne
    // (routeri enda router-link-active seda ei tee, sest /jobs ja /jobs/:id on eraldi rajad)
    isActive(path) {
      return this.$route.path.startsWith(path)
    },
  },
}
</script>

<template>
  <aside class="lt-sidebar">
    <RouterLink to="/dashboard" class="lt-sidebar-brand">LIFTERTRANS</RouterLink>

    <nav>
      <!-- ADMIN-i lingid -->
      <!-- TODO: Autod ja Aruanded lisame, kui nende vaated on olemas -->
      <ul v-if="isAdmin">
        <li>
          <RouterLink to="/dashboard" :class="{ active: isActive('/dashboard') }"
            >Töölaud</RouterLink
          >
        </li>
        <li>
          <RouterLink to="/calendar" :class="{ active: isActive('/calendar') }">Kalender</RouterLink>
        </li>
        <li>
          <RouterLink to="/jobs" :class="{ active: isActive('/jobs') }">Tellimused</RouterLink>
        </li>
        <li>
          <RouterLink to="/customers" :class="{ active: isActive('/customers') }"
            >Kliendid</RouterLink
          >
        </li>
        <li>
          <RouterLink to="/drivers" :class="{ active: isActive('/drivers') }">Juhid</RouterLink>
        </li>
      </ul>

      <!-- DRIVER-i lingid -->
      <ul v-if="isDriver">
        <li>
          <RouterLink to="/my-jobs" :class="{ active: isActive('/my-jobs') }">Minu tööd</RouterLink>
        </li>
      </ul>
    </nav>

    <!-- Välja logimise teeb App.vue, siit saadame ainult sündmuse -->
    <button
      type="button"
      class="btn btn-outline-light btn-sm lt-sidebar-logout"
      @click="$emit('event-logout')"
    >
      Logi välja
    </button>
  </aside>
</template>

<style scoped>
.lt-sidebar {
  width: 220px;
  min-height: 100vh;
  padding: 24px 16px;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, var(--lt-red-dark) 0%, var(--lt-red) 100%);

  /* Menüü jääb kerimisel paigale */
  position: sticky;
  top: 0;
  align-self: flex-start;
  flex-shrink: 0;
}

.lt-sidebar-brand {
  font-family: var(--lt-heading-font);
  font-size: 1.6rem;
  font-weight: 700;
  color: #fff;
  text-decoration: none;
  margin-bottom: 24px;
}

.lt-sidebar ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.lt-sidebar li a {
  display: block;
  padding: 8px 12px;
  margin-bottom: 4px;
  border-radius: 6px;
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
}

.lt-sidebar li a:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

/* Aktiivse lehe link (vt isActive) */
.lt-sidebar li a.active {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-weight: 600;
}

.lt-sidebar-logout {
  margin-top: auto;
}

/* Mobiilivaade: külgmenüü muutub ülemiseks ribaks, lingid ja väljalogimine ühte ritta */
@media (max-width: 767.98px) {
  .lt-sidebar {
    width: 100%;
    min-height: auto;
    position: static;
    flex-direction: row;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px 16px;
    padding: 12px 16px;
  }

  .lt-sidebar-brand {
    margin-bottom: 0;
    font-size: 1.3rem;
  }

  .lt-sidebar nav {
    flex-basis: 100%;
    order: 3;
  }

  .lt-sidebar ul {
    display: flex;
    flex-wrap: wrap;
    gap: 4px 8px;
  }

  .lt-sidebar li a {
    margin-bottom: 0;
  }

  .lt-sidebar-logout {
    margin-top: 0;
    margin-left: auto;
  }
}
</style>
