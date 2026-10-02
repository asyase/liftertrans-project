<script>
import CustomerService from '@/services/CustomerService.js'
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'
import SearchBar from '@/components/SearchBar.vue'

export default {
  name: 'CustomersView',

  components: {
    SearchBar,
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
              // Näitame backendi tegelikku veateadet (nt "kliendiga on seotud töid"),
              // kui seda pole, siis üldist teadet
              this.errorMessage = error.response?.data?.message ?? 'Kustutamine ebaõnnestus'
            })
      }
    },
  },
}
</script>

<template>
  <div class="container-fluid px-4">
    <h1 class="mb-4">Kliendid</h1>

    <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
      <SearchBar v-model="searchText" placeholder="Otsi (nimi või ettevõte)" @search="search" />

      <RouterLink to="/customers/new" class="btn btn-primary">+ Lisa klient</RouterLink>
    </div>

    <div v-if="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

    <div class="table-responsive">
      <table class="table table-hover align-middle">
        <thead>
          <tr>
            <th scope="col">#</th>
            <th scope="col">Nimi</th>
            <th scope="col">Ettevõte</th>
            <th scope="col">Registrikood</th>
            <th scope="col">KMKR</th>
            <th scope="col">E-post</th>
            <th scope="col">Arve e-post</th>
            <th scope="col">Telefon</th>
            <th scope="col">Tegevused</th>
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
            <td class="text-nowrap">
              <RouterLink
                :to="`/customers/${customer.customerId}`"
                class="btn btn-sm btn-outline-primary me-2"
              >
                Vaata
              </RouterLink>
              <RouterLink
                :to="`/customers/${customer.customerId}/edit`"
                class="btn btn-sm btn-outline-secondary me-2"
              >
                Muuda
              </RouterLink>
              <button class="btn btn-sm btn-outline-dark" @click="deleteCustomer(customer.customerId)">
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
  </div>
</template>
