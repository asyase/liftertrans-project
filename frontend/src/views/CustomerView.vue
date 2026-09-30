<script>

import { ref, onMounted } from 'vue'
// import { useRouter } from 'vue-router'
import axios from 'axios' // 1. Impordi axios

// const router = useRouter()
const customers = ref([])
const searchQuery = ref('')

export default {
  name: 'CustomerView'
}

const fetchCustomers = async () => {
  try {
    const response = await axios.get('/api/customers', {
      params: searchQuery.value ? { search: searchQuery.value } : {},
    })

    customers.value = response.data
  } catch (error) {
    console.error('Viga klientide laadimisel:', error)
  }
}

// Kustutamise kinnitamine ja API teostus
/* const confirmDelete = async (customer) => {
  if (confirm(`Kas oled kindel, et soovid kliendi "${customer.name}" kustutada?`)) {
    try {
      await axios.delete(`/api/customers/${customer.customerId}`)
      // Värskenda nimekirja pärast edukat kustutamist
      fetchCustomers()
    } catch (error) {
      // Axiose vea vastus asub error.response sees
      if (error.response) {
        alert(`Viga kustutamisel: ${error.response.data.message}`)
      } else {
        console.error('Viga kustutamisel:', error)
      }
    }
  }
}*/

onMounted(() => {
  fetchCustomers()
})
</script>

<template>
  <div class="app-layout">
    <aside class="sidebar">
      <h2 class="brand">LIFTERTRANS</h2>
      <nav>
        <ul>
          <li><router-link to="/">Esileht</router-link></li>
          <li><router-link to="/calendar">Kalender</router-link></li>
          <li><router-link to="/orders">Tellimused</router-link></li>
          <li class="active"><router-link to="/customers">Kliendid</router-link></li>
          <li><router-link to="/vechicles">Autod</router-link></li>
          <li><router-link to="/drivers">juhid</router-link></li>
          <li><router-link to="/reports">Aruanded</router-link></li>
        </ul>
      </nav>
      <div class="logout">
        <a href="#">Logi välja</a>
      </div>
    </aside>

    <main class="main-content">
      <header class="content-header">
        <h1>Kliendid</h1>
        <div class="actions">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Otsi (nimi või ettevõte)..."
            @input="fetchCustomers"
            class="search-input"
          />

          <router-link to="/customers/new" class="btn btn-primary"> + Lisa klient </router-link>
        </div>
      </header>

      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Nimi</th>
              <th>Ettevõte</th>
              <th>Registrikood</th>
              <th>KMKR</th>
              <th>E-post</th>
              <th>Arve e-post</th>
              <th>Telefon</th>
              <th>Tegevused</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="(customer, index) in customers" :key="customer.customerId">
              <td>{{ index + 1 }}</td>
              <td>{{ customer.name }}</td>
              <td>{{ customer.companyName }}</td>
              <td>{{ customer.companyRegistrationNumber }}</td>
              <td>{{ customer.vatNumber }}</td>
              <td>{{ customer.email }}</td>
              <td>{{ customer.invoiceEmail }}</td>
              <td>{{ customer.phone }}</td>
              <td class="actions-cell">
                <router-link :to="`/customers/${customer.customerId}`">vaata</router-link>
                <router-link :to="`/customers/${customer.customerId}/edit`">Muuda</router-link>
                <button @click="confirmDelete(customer)" class="btn-link text-danger">
                  Kustuta
                </button>
              </td>
            </tr>
            <tr v-if="customers.length === 0">
              <td colspan="9" class="text-center">Kliente ei leitud.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </main>
  </div>
</template>

<style>
.app-layout {
  display: flex;
  min-height: 100vh;
  font-family: sans-serif;
}

.sidebar {
  width: 200px;
  border-right: 1px solid #ccc;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar li {
  margin-bottom: 12px;
}

.sidebar li.active a {
  font-weight: bold;
  color: #000;
}

.sidebar .logout {
  margin-top: auto;
}

.main-content {
  flex: 1;
  padding: 20px 40px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.actions {
  display: flex;
  gap: 12px;
}

.search-input {
  padding: 6px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.btn-primary {
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  padding: 6px 12px;
  text-decoration: none;
  color: #000;
  border-radius: 4px;
}

table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

th, td {
  border: 1px solid #ccc;
  padding: 8px 12px;
  font-size: 14px;
}

th {
  background-color: #f9f9f9;
}

.actions-cell a, .btn-link {
  margin-right: 8px;
  color: #0066cc;
  text-decoration: underline;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
}

.text-danger {
  color: #cc0000;
}

.text-center {
  text-align: center;
}
</style>
