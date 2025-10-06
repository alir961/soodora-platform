<script setup lang="ts">
import { IonSearchbar, IonButton, IonIcon } from '@ionic/vue';
import settingsIcon from '@/assets/icons/settings.svg';
import type { SearchbarCustomEvent } from '@ionic/vue';

defineProps({
  modelValue: String,
  placeholder: {
    type: String,
    default: 'Search...',
  },
  isFilterActive: Boolean,
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
      :class="['search-settings-button', { 'active-filter': isFilterActive }]"
      @click="triggerFilter">
      <ion-icon class="settings-icon" :src="settingsIcon" />
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
}

.settings-icon {
  width: 30px;
  height: 30px;
  display: block;
  padding: 4px;
}

.search-settings-button.active-filter {
  --color: var(--ion-color-primary);
}
</style>
