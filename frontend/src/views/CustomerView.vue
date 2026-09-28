<script>
// import axios from 'axios'
</script>

<template>
  <div class="app-layout">
    <aside class="sidebar">
      <h2 class="brand">LIFTERTRANS</h2>
      <nav>
        <ul>
          <li><a href="#">Esileht</a></li>
          <li><a href="#">Kalender</a></li>
          <li><a href="#">Tellimused</a></li>
          <li class="active"><a href="/api/customers">Kliendid</a></li>
          <li><a href="#">Autod</a></li>
          <li><a href="#">juhid</a></li>
          <li><a href="#">Aruanded</a></li>
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
            <tr v-for="(customer, index) in customers" :key="customer.customerId"></tr>
            <td>{{ index + 1 }}</td>
            <td>{{ customer.name }}</td>
            <td>{{ customer.companyName }}</td>
            <td>{{ customer.companyRegistrationNumber }}</td>
            <td>{{ customer.vatNumber }}</td>
            <td>{{ customer.email }}</td>
            <td>{{ customer.invoiceEmail }}</td>
            <td>{{ customer.phone }}</td>


          </tbody>
        </table>
      </div>
    </main>
  </div>
</template>

<style></style>
