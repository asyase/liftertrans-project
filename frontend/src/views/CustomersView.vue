<script>
import axios from 'axios'
import CustomerService from '@/services/CustomerService.js'
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'

export default {
  name: 'CustomersView',

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
      customers: [],
      searchText: '',

      errorMessage: '',

      errorResponse: {
        message: '',
        errorCode: '',
      },
    }
  },

  computed: {
    filteredCustomers() {
      if (!this.searchText) {
        return this.customers
      }

      const search = this.searchText.trim().toLowerCase()
      return this.customers.filter(
        (customer) => customer.companyName && customer.companyName.toLowerCase().includes(search),
      )
    },
  },

  methods: {
    getCustomers() {
      CustomerService.getCustomersRequest()
        .then((response) => this.handleGetCustomers(response))
        .catch((error) => this.handleGetCustomersErrorResponse(error))
    },

    handleGetCustomers(response) {
      this.customers = response.data
      console.log('Kliendid', this.customers)
    },

    handleGetCustomersErrorResponse() {
      this.errorMessage = 'Klientide laadimine ebaõnnestus.'
    },

    // goToAddCustomer() {
    //   this.$router.push('/customers/new')
    // },
    //
    // goToViewCustomer(customerId) {
    //   this.$router.push('/customers/' + customerId)
    // },
    //
    // goToEditCustomer(customerId) {
    //   this.$router.push('/customers/' + customerId + '/edit')
    // },

    deleteCustomer(customerId) {
      // kinnitusaken
      if (confirm('Kas oled kindel, et soovid selle kliendi kustutada?')) {
        // saadame backendile kustutamise päringu
        axios
          .delete('/api/customers/' + customerId)
          .then(() => {
            this.getCustomers()
          }) // värskendame tabelit
          .catch((error) => {
            console.error('Kustutamise viga:', error)
            this.errorMessage = 'Kustutamine ebaõnnestus'
          })
      }
    },
  },
}
</script>

<template>
  <!-- Külgmenüü on nüüd App.vue-s (AppSidebar), siin ainult lehe sisu -->
  <div>
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
            <tr v-for="(customer, index) in filteredCustomers" :key="customer.customerId">
              <td>{{ index + 1 }}</td>
              <td>{{ customer.name }}</td>
              <td>{{ customer.companyName }}</td>
              <td>{{ customer.companyRegistrationNumber }}</td>
              <td>{{ customer.vatNumber }}</td>
              <td>{{ customer.email }}</td>
              <td>{{ customer.invoiceEmail }}</td>
              <td>{{ customer.phone }}</td>
              <td>
                <router-link :to="`/customers/${customer.customerId}`" class="btn btn-danger me-2"
                  >Vaata
                </router-link>
                <router-link
                  :to="`/customers/${customer.customerId}/edit`"
                  class="btn btn-outline-secondary me-2"
                  >Muuda
                </router-link>
                <button @click="deleteCustomer(customer.customerId)" class="btn btn-dark">Kustuta</button>
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

<style scoped>
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

th,
td {
  border: 1px solid #ccc;
  padding: 8px 12px;
  font-size: 14px;
}

th {
  background-color: #f9f9f9;
}

.actions-cell a,
.text-center {
  text-align: center;
}
</style>
