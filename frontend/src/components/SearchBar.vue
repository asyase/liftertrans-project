<script>
import { PhMagnifyingGlass } from '@phosphor-icons/vue'

export default {
  name: 'SearchBar',

  components: {
    PhMagnifyingGlass,
  },

  props: {
    // v-model otsingusõna jaoks
    modelValue: {
      type: String,
      default: '',
    },
    placeholder: {
      type: String,
      default: 'Otsi',
    },
  },

  emits: ['update:modelValue', 'search'],
}
</script>

<template>
  <!-- Ühtne otsinguriba kõigil lehtedel: Bootstrap input-group + luubi nupp -->
  <div class="input-group lt-search">
    <input
      :value="modelValue"
      type="text"
      class="form-control"
      :placeholder="placeholder"
      @input="$emit('update:modelValue', $event.target.value)"
      @keyup.enter="$emit('search')"
    />
    <button
      type="button"
      class="btn btn-outline-secondary"
      title="Otsi"
      aria-label="Otsi"
      @click="$emit('search')"
    >
      <PhMagnifyingGlass :size="18" />
    </button>
  </div>
</template>

<style scoped>
.lt-search {
  max-width: 360px;
}
</style>
