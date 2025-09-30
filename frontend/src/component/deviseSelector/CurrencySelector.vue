<script setup lang="ts">
import { defineProps, defineEmits, ref, watch } from 'vue';
import { IonSelect, IonSelectOption, IonItem, IonLabel } from '@ionic/vue';
import { Currency } from '@/entity/Money';

const props = defineProps<{
  modelValue: Currency;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: Currency): void;
}>();

const localCurrency = ref<Currency>(props.modelValue || 'EUR');

watch(localCurrency, (newVal) => emit('update:modelValue', newVal));
</script>

<template>
  <ion-item>
    <ion-label>Currency</ion-label>
    <ion-select v-model="localCurrency" interface="action-sheet" placeholder="Select a currency">
      <ion-select-option value="EUR">€</ion-select-option>
      <ion-select-option value="USD">$</ion-select-option>
      <ion-select-option value="CAD">C$</ion-select-option>
      <ion-select-option value="GBP">£</ion-select-option>
    </ion-select>
  </ion-item>
</template>
