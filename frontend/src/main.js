import { createApp } from 'vue'
import { createPinia } from 'pinia'
import axios from 'axios'

import App from './App.vue'
import router from './router'

// Bootstrap
import 'bootstrap/dist/css/bootstrap.min.css'

// Oma stiilid peale Bootstrapi, et brändi värvid kirjutaksid Bootstrapi omad üle
import './assets/main.css'
import 'bootstrap/dist/js/bootstrap.js'

// Lisaimpordid
// leafleti css kujindused
import "leaflet/dist/leaflet.css";

const app = createApp(App)

app.use(createPinia())
app.use(router)

// Axios globaalselt kättesaadavaks
app.config.globalProperties.$axios = axios

app.mount('#app')
