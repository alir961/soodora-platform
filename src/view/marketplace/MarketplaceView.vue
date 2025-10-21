<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import {
  IonContent,
  IonGrid,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
  IonRow,
  onIonViewWillEnter,
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
const selectedSort = ref<
  | 'price_amount,asc'
  | 'price_amount,desc'
  | 'price_currency,asc'
  | 'price_currency,desc'
  | 'quantity,asc'
  | 'quantity,desc'
  | 'createdAt,desc'
>('createdAt,desc');

const showFilterModal = ref(false);

const isDefaultSort = computed(() => selectedSort.value === 'createdAt,desc');

const toggleFilters = () => {
  showFilterModal.value = !showFilterModal.value;
};

const applySort = (sort: typeof selectedSort.value) => {
  selectedSort.value = sort;
};

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
    hasMore.value = response.hasMore;
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
</script>

<template>
  <ApplicationViewport title="Marketplace">
    <ion-content>
      <SearchBarWithFilters
        v-model="searchInput"
        placeholder="Search products..."
        :isFilterActive="!isDefaultSort"
        :showFilters="showFilterModal"
        @filterClick="toggleFilters" />

      <transition name="slide-down">
        <div v-if="showFilterModal" class="filter-panel-wrapper">
          <div class="filter-panel">
            <div class="filter-header">
              <h3>Sort Products</h3>
              <button class="clear-btn" @click="applySort('createdAt,desc')">Reset</button>
            </div>
            <div class="filter-options">
              <button
                :class="['filter-chip', { active: selectedSort === 'createdAt,desc' }]"
                @click="applySort('createdAt,desc')">
                Recent
              </button>
              <button
                :class="['filter-chip', { active: selectedSort === 'price_amount,asc' }]"
                @click="applySort('price_amount,asc')">
                Price ↓
              </button>
              <button
                :class="['filter-chip', { active: selectedSort === 'price_amount,desc' }]"
                @click="applySort('price_amount,desc')">
                Price ↑
              </button>
              <button
                :class="['filter-chip', { active: selectedSort === 'quantity,desc' }]"
                @click="applySort('quantity,desc')">
                Quantity ↑
              </button>
              <button
                :class="['filter-chip', { active: selectedSort === 'quantity,asc' }]"
                @click="applySort('quantity,asc')">
                Quantity ↓
              </button>
            </div>
          </div>
        </div>
      </transition>

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
.filter-panel-wrapper {
  width: 90%;
  margin: 0 auto;
  margin-bottom: 0.75rem;
}

.filter-panel {
  background: linear-gradient(to bottom, rgba(18, 22, 30, 0.75), rgba(18, 22, 30, 0.65));
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: 1.25rem 1.5rem;
  border-radius: 16px;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.04);
  position: relative;
}

.filter-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  border-radius: 16px 16px 0 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.filter-header h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.95);
  margin: 0;
  letter-spacing: -0.01em;
}

.clear-btn {
  appearance: none;
  border: 0;
  background: transparent;
  color: #60a5fa;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.clear-btn:active {
  background: rgba(96, 165, 250, 0.12);
  transform: scale(0.96);
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-chip {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.95rem;
  font-weight: 600;
  padding: 10px 18px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.filter-chip:active {
  transform: scale(0.96);
}

.filter-chip.active {
  background: linear-gradient(135deg, rgba(29, 78, 216, 0.9), rgba(37, 99, 235, 0.9));
  border-color: rgba(96, 165, 250, 0.3);
  color: #ffffff;
  box-shadow:
    0 4px 12px rgba(37, 99, 235, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.slide-down-enter-active {
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.slide-down-leave-active {
  transition: all 0.25s cubic-bezier(0.8, 0.2, 0.8, 0.2);
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-12px) scale(0.97);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.98);
}
</style>
