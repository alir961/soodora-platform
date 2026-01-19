<script setup lang="ts">
import { useRouter } from 'vue-router';
import type { QuoteDTO } from '@/entity/Quote';
import { IonItem, IonLabel, IonIcon } from '@ionic/vue';
import DateDisplay from '@/component/date/DateDisplay.vue';
import { documentTextOutline, pencilOutline } from 'ionicons/icons';

const props = defineProps<{
  quote: QuoteDTO;
}>();

const router = useRouter();

const goToQuote = () => {
  router.push(`/quotes/${props.quote.slug}`);
};
</script>

<template>
  <IonItem button detail @click="goToQuote">
    <IonLabel>
      <h2>{{ quote.name }}</h2>
      <h2>{{ quote.clientName }}</h2>
      <p>Total: {{ quote.total.amount }} {{ quote.total.symbol }}</p>
      <IonIcon :icon="documentTextOutline" />
      <DateDisplay :date="quote.createdAtDate" class="date" />
      <IonIcon :icon="pencilOutline" />
      <DateDisplay :date="quote.updatedAt" class="date" />
    </IonLabel>
  </IonItem>
</template>

<style scoped>
.date {
  font-size: 1.2rem;
  font-weight: 400;
  color: var(--ion-color-medium);
  width: auto;
}
</style>
