// Sisselogitud kasutaja andmed on sessionStorage-is (salvestab LoginView).
// Siin on ühes kohas kontrollid, et vaated ja App.vue ei peaks võtmete nimesid ise teadma.
export default {
  userIsLoggedIn() {
    return sessionStorage.getItem('userId') !== null
  },

  userIsAdmin() {
    return sessionStorage.getItem('roleName') === 'ADMIN'
  },

  userIsDriver() {
    return sessionStorage.getItem('roleName') === 'DRIVER'
  },
}
