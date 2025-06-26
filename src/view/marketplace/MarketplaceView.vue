<script setup lang="ts">
import { ref, watch } from 'vue';
import {
  IonContent,
  IonGrid,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
  IonItem,
  IonLabel,
  IonSearchbar,
  IonSelect,
  IonSelectOption,
  IonRow,
  onIonViewWillEnter,
} from '@ionic/vue';

import ApplicationViewport from '@/view/ApplicationViewport.vue';
import ProductCard from '@/component/marketplace/ProductCard.vue';
import { Product } from '@/entity/Product';
import { callSearchProducts } from '@/client/productClient';

const PAGE_SIZE = 10;

const products = ref<Product[]>([]);
const currentPage = ref(0);
const hasMore = ref(true);
const isLoading = ref(false);

const searchInput = ref('');
const selectedSort = ref<'price,asc' | 'price,desc' | 'quantity,asc' | 'quantity,desc'>('price,asc');

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
  loadProducts(true);
});

watch([searchInput, selectedSort], () => {
  loadProducts(true);
});
</script>

<template>
  <ApplicationViewport title="Marketplace">
    <ion-content class="ion-padding">
      <ion-searchbar v-model="searchInput" :debounce="300" placeholder="Search products..." />

      <ion-item lines="none" class="sort-select">
        <ion-label>Sort by</ion-label>
        <ion-select v-model="selectedSort" interface="popover">
          <ion-select-option value="price,asc">Price ↑</ion-select-option>
          <ion-select-option value="price,desc">Price ↓</ion-select-option>
          <ion-select-option value="quantity,asc">Quantity ↑</ion-select-option>
          <ion-select-option value="quantity,desc">Quantity ↓</ion-select-option>
        </ion-select>
      </ion-item>

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
  </ApplicationViewport>
</template>

<style scoped>
.sort-select {
  margin: 1rem 0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}
</style>
