<script>
import SessionStorageService from '@/services/SessionStorageService.js'
import NavigationService from '@/services/NavigationService.js'

export default {
  name: 'MyJobsView',

  beforeMount() {
    // Leht on ainult DRIVER-ile — teised suuname lehele "õigused puuduvad"
    if (!SessionStorageService.userIsDriver()) {
      NavigationService.navigateToNotAuthorizedView()
    }
  },
}
</script>

<template></template>

<style scoped></style>
