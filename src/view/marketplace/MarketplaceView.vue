<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import {
  IonContent,
  IonGrid,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
  IonSelect,
  IonSelectOption,
  IonRow,
  onIonViewWillEnter,
  IonButton,
  IonModal,
} from '@ionic/vue';

import ApplicationViewport from '@/view/ApplicationViewport.vue';
import ProductCard from '@/component/marketplace/ProductCard.vue';
import { Product } from '@/entity/Product';
import { callSearchProducts } from '@/client/productClient';
import SearchBarWithFilters from '@/component/search/SearchBarWithFilters.vue';

const PAGE_SIZE = 10;

const products = ref<Product[]>([]);
const currentPage = ref(0);
const hasMore = ref(true);
const isLoading = ref(false);

const searchInput = ref('');
const selectedSort = ref<'price,asc' | 'price,desc' | 'quantity,asc' | 'quantity,desc' | 'createdAt,desc'>(
  'createdAt,desc'
);

const showFilterModal = ref(false);

const isDefaultSort = computed(() => selectedSort.value === 'createdAt,desc');

const loadProducts = async (reset = false) => {
  if (isLoading.value || (!hasMore.value && !reset)) return;

  try {
    isLoading.value = true;
    const page = reset ? 0 : currentPage.value;

    const response = await callSearchProducts({
      name: searchInput.value.trim(),
      description: searchInput.value.trim(),
      page,
      size: PAGE_SIZE,
      sort: selectedSort.value,
    });

    if (reset) {
      products.value = response.content;
      currentPage.value = 1;
    } else {
      products.value.push(...response.content);
      currentPage.value++;
    }
    hasMore.value = response.content.length === PAGE_SIZE;
  } catch (error) {
    console.error('Error loading products:', error);
    hasMore.value = false;
  } finally {
    isLoading.value = false;
  }
};

const onInfinite = async (ev: CustomEvent) => {
  await loadProducts();
  await (ev.target as HTMLIonInfiniteScrollElement).complete();
};

onIonViewWillEnter(() => {
  searchInput.value = '';
  selectedSort.value = 'createdAt,desc';
  loadProducts(true);
});

watch([searchInput, selectedSort], () => {
  loadProducts(true);
});

const closeFilterModal = () => {
  (document.activeElement as HTMLElement | null)?.blur();
  showFilterModal.value = false;
};

const handleFilterModalClose = () => {
  showFilterModal.value = false;
};
</script>

<template>
  <ApplicationViewport title="Marketplace">
    <ion-content>
      <SearchBarWithFilters
        v-model="searchInput"
        placeholder="Search products..."
        :isFilterActive="!isDefaultSort"
        @filterClick="showFilterModal = true" />

      <ion-grid>
        <ion-row>
          <ProductCard
            v-for="product in products"
            :key="product.slug"
            :product="product"
            @product-deleted="() => loadProducts(true)" />
        </ion-row>
      </ion-grid>

      <ion-infinite-scroll @ionInfinite="onInfinite" threshold="100px" :disabled="!hasMore">
        <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more products..." />
      </ion-infinite-scroll>
    </ion-content>
    <ion-modal
      :is-open="showFilterModal"
      :breakpoints="[0, 0.3, 0.5]"
      :initial-breakpoint="0.3"
      handle-behavior="cycle"
      @didDismiss="handleFilterModalClose">
      <ion-content class="ion-padding">
        <div class="filter-modal">
          <h2>Search Filters</h2>
          <ion-select v-model="selectedSort">
            <div slot="label">Search Type:</div>
            <ion-select-option value="createdAt,desc">Recently Posted</ion-select-option>
            <ion-select-option value="price,desc">Price ↑</ion-select-option>
            <ion-select-option value="price,asc">Price ↓</ion-select-option>
            <ion-select-option value="quantity,desc">Quantity ↑</ion-select-option>
            <ion-select-option value="quantity,asc">Quantity ↓</ion-select-option>
          </ion-select>
          <ion-button expand="block" @click="closeFilterModal">Apply</ion-button>
        </div>
      </ion-content>
    </ion-modal>
  </ApplicationViewport>
</template>

<style scoped>
.sort-select {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.filter-modal {
  display: flex;
  flex-direction: column;
  padding: 1.5rem;
  border-radius: 12px;
}

ion-button {
  --color: white;
}
</style>
