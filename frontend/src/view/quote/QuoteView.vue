<script setup lang="ts">
import { ref, watchEffect } from 'vue';
import { searchQuotes } from '@/client/quoteClient';
import type { QuoteDTO, QuoteSearchParams } from '@/entity/Quote';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import { IonButton, IonContent, IonHeader, IonIcon, IonList, IonLoading, IonTitle, IonToolbar } from '@ionic/vue';
import { arrowBackOutline } from 'ionicons/icons';
import QuoteItem from '@/component/quote/QuoteItem.vue';
import SearchBarWithFilters from '@/component/search/SearchBarWithFilters.vue';
import { showToast } from '@/utils';

const quotes = ref<QuoteDTO[]>([]);
const loading = ref(false);
const searchQuery = ref('');
const sortField = ref<'createdAt'>('createdAt');
const sortAsc = ref(true);

const authStore = useAuthStore();

const loadQuotesForUser = async () => {
  const userSlug = authStore.getAuthenticatedUserSlug;
  if (!userSlug) return;

  loading.value = true;
  try {
    const params: QuoteSearchParams = {
      clientName: searchQuery.value,
      page: 0,
      size: 50,
      sort: `${sortField.value},${sortAsc.value ? 'asc' : 'desc'}`,
    };
    const quotePage = await searchQuotes(params);
    quotes.value = quotePage.content;
  } catch (err) {
    quotes.value = [];
    const message = (err as Error)?.message ?? 'Unable to load quotes';
    await showToast(message, 'danger');
  } finally {
    loading.value = false;
  }
};

watchEffect(loadQuotesForUser);

const toggleSort = () => {
  sortAsc.value = !sortAsc.value;
  loadQuotesForUser();
};
</script>

<template>
  <ApplicationViewport title="Quotes">
    <IonHeader>
      <IonToolbar>
        <IonButton class="inline-back-button" slot="start" fill="clear" @click="$router.back()">
          <IonIcon :icon="arrowBackOutline" /> Back
        </IonButton>
        <IonTitle>My Quotes</IonTitle>
      </IonToolbar>

      <SearchBarWithFilters
        v-model="searchQuery"
        placeholder="Search by client name..."
        :debounce="300"
        @filterClick="toggleSort"
        :isFilterActive="sortAsc" />
    </IonHeader>

    <IonContent class="ion-padding">
      <IonLoading :is-open="loading" message="Loading quotes..." />

      <IonList v-if="!loading && quotes.length">
        <QuoteItem v-for="q in quotes" :key="q.slug" :quote="q" />
      </IonList>

      <div v-if="!loading && !quotes.length" class="ion-text-center ion-padding">
        <p>No quotes found.</p>
      </div>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
.inline-back-button {
  display: flex;
  align-items: center;
}
</style>
