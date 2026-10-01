<script>
import CustomerService from '@/services/CustomerService.js'
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
import { PhMagnifyingGlass } from '@phosphor-icons/vue'

export default {
  name: 'CustomersView',

  components: {
    PhMagnifyingGlass,
  },

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
      searchText: '', // mida kasutaja otsinguväljale kirjutab
      errorMessage: '',
      errorResponse: {
        message: '',
        errorCode: '',
      },
    }
  },

  watch: {
    // Kui otsinguväli tühjendatakse, laeme kohe kõik kliendid
    searchText(value) {
      if (!value) {
        this.getCustomers()
      }
    },
  },

  methods: {
    // Käivitub luubi nupu või Enter-klahviga
    search() {
      this.getCustomers()
    },

    getCustomers() {
      this.errorMessage = ''
      // Saadame otsingusõna backendile: GET /api/customers?search=...
      CustomerService.getCustomersRequest(this.searchText.trim())
          .then((response) => this.handleGetCustomers(response))
          .catch((error) => this.handleGetCustomersErrorResponse(error))
    },

    handleGetCustomers(response) {
      this.customers = response.data
    },

    handleGetCustomersErrorResponse(error) {
      // Kui backend saatis ApiError vastuse, näitame selle teadet
      if (error.response && error.response.data && error.response.data.message) {
        this.errorResponse = error.response.data
        this.errorMessage = this.errorResponse.message
      } else {
        this.errorMessage = 'Klientide laadimine ebaõnnestus.'
      }
    },

    deleteCustomer(customerId) {
      if (confirm('Kas oled kindel, et soovid selle kliendi kustutada?')) {
        CustomerService.deleteCustomerRequest(customerId)
            .then(() => this.getCustomers()) // värskendame tabelit
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
  <div>
    <main class="main-content">
      <header class="content-header">
        <h1>Kliendid</h1>
        <div class="actions">
          <div class="search-wrapper">
            <input
                v-model="searchText"
                type="text"
                placeholder="Otsi (nimi või ettevõte)..."
                class="search-input"
                @keyup.enter="search"
            />
            <button type="button" class="search-btn" @click="search" title="Otsi">
              <PhMagnifyingGlass :size="18" />
            </button>
          </div>

          <router-link to="/customers/new" class="btn btn-primary"> + Lisa klient </router-link>
        </div>
      </header>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

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
            <td>
              <router-link
                  :to="`/customers/${customer.customerId}`"
                  class="btn btn-outline-primary me-2"
              >
                Vaata
              </router-link>
              <router-link
                  :to="`/customers/${customer.customerId}/edit`"
                  class="btn btn-outline-secondary me-2"
              >
                Muuda
              </router-link>
              <button @click="deleteCustomer(customer.customerId)" class="btn btn-outline-dark">
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

/* Otsinguväli + luubi nupp ühe tervikuna */
.search-wrapper {
  display: flex;
  align-items: stretch;
}

.search-input {
  padding: 6px 12px;
  border: 1px solid #ccc;
  border-right: none;
  border-radius: 4px 0 0 4px;
}

.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 10px;
  border: 1px solid #ccc;
  border-radius: 0 4px 4px 0;
  background-color: #f0f0f0;
  cursor: pointer;
}

.search-btn:hover {
  background-color: #e0e0e0;
}

.error-message {
  color: #c0392b;
  margin-bottom: 12px;
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

.text-center {
  text-align: center;
}
</style>
