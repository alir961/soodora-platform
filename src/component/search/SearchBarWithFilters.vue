<script setup lang="ts">
import { IonSearchbar, IonButton, IonIcon } from '@ionic/vue';
import settingsIcon from '@/assets/icons/settings.svg';
import { closeOutline } from 'ionicons/icons';
import type { SearchbarCustomEvent } from '@ionic/vue';

defineProps({
  modelValue: String,
  placeholder: {
    type: String,
    default: 'Search...',
  },
  isFilterActive: Boolean,
  showFilters: Boolean,
  debounce: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits(['update:modelValue', 'filterClick']);

const updateValue = (event: SearchbarCustomEvent) => {
  const value = event.detail.value ?? '';
  emit('update:modelValue', value);
};

const triggerFilter = () => {
  emit('filterClick');
};
</script>

<template>
  <div class="search-bar-row">
    <ion-searchbar
      class="search-bar"
      :placeholder="placeholder"
      :debounce="debounce"
      :value="modelValue"
      @ionInput="updateValue" />
    <ion-button
      fill="clear"
      :class="['search-settings-button', { 'active-filter': isFilterActive, 'filter-open': showFilters }]"
      @click="triggerFilter">
      <ion-icon v-if="showFilters" class="close-icon" :icon="closeOutline" />
      <ion-icon v-else class="settings-icon" :src="settingsIcon" />
    </ion-button>
  </div>
</template>

<style scoped>
.search-bar-row {
  display: flex;
  align-items: center;
  padding: 0.5rem 1.5em 0rem 1.5em;
}

.search-bar {
  padding: 0;
  flex: 1;
}

.search-settings-button {
  --padding-start: 0;
  --padding-end: 0;
  --padding-top: 0;
  --padding-bottom: 0;
  --color: white;
  transition: transform 0.2s ease;
}

.settings-icon,
.close-icon {
  width: 30px;
  height: 30px;
  display: block;
  padding: 4px;
  transition: transform 0.2s ease;
}

.search-settings-button.active-filter:not(.filter-open) {
  --color: var(--ion-color-primary);
}

.search-settings-button.filter-open {
  --color: #ff4444; /* Red color for close icon */
}

.search-settings-button:active {
  transform: scale(0.92);
}
</style>
