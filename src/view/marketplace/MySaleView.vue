<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getMySales } from '@/client/saleClient';
import { Sale } from '@/entity/Sale';
import {
  toastController,
  IonButton,
  IonIcon,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
  IonContent,
  IonHeader,
  IonToolbar,
  IonTitle,
} from '@ionic/vue';
import { arrowBackOutline } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const router = useRouter();

const sales = ref<Sale[]>([]);
const page = ref(0);
const size = 10;
const hasMore = ref(true);

const fetchSales = async (pageNumber: number) => {
  try {
    const newSales = await getMySales(pageNumber, size, 'quantitySold');
    if (newSales.length < size) {
      hasMore.value = false;
    }
    sales.value.push(...newSales);
  } catch (error) {
    const toast = await toastController.create({
      message: 'Error loading sales',
      duration: 2000,
      color: 'danger',
    });
    await toast.present();
    hasMore.value = false;
  }
};

const loadMore = async (event: CustomEvent<void>) => {
  page.value++;
  await fetchSales(page.value);
  (event.target as HTMLIonInfiniteScrollElement).complete();

  if (!hasMore.value) {
    (event.target as HTMLIonInfiniteScrollElement).disabled = true;
  }
};

const goBack = () => {
  router.back();
};

onMounted(async () => {
  sales.value = [];
  page.value = 0;
  hasMore.value = true;
  await fetchSales(page.value);
});
</script>

<template>
  <ApplicationViewport title="My Sales">
    <ion-header>
      <ion-toolbar>
        <ion-button fill="clear" @click="goBack" aria-label="Back">
          <ion-icon :icon="arrowBackOutline" slot="icon-only" />
        </ion-button>
        <ion-title>My Sales</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <div v-if="sales.length === 0 && !hasMore" class="no-sales">No sales found</div>

      <div v-for="sale in sales" :key="sale.slug" class="sale-card">
        <img v-if="sale.product.imageUrl" :src="sale.product.imageUrl" alt="Product image" class="sale-image" />
        <div class="sale-info">
          <div class="sale-header">
            <h2>{{ sale.product.name }}</h2>
            <span class="quantity">Sold: {{ sale.quantitySold }}</span>
          </div>
          <div class="sale-date">{{ new Date(sale.createdAt).toLocaleString() }}</div>
          <div class="sale-price">
            Unit price:
            {{ sale.product.price !== null ? sale.product.price.toFixed(2) + '€' : 'No price' }}
          </div>
          <div class="sale-total">
            Total:
            {{ sale.product.price !== null ? sale.amount.toFixed(2) + '€' : 'No total' }}
          </div>
        </div>
      </div>

      <ion-infinite-scroll @ionInfinite="loadMore" threshold="100px" :disabled="!hasMore">
        <ion-infinite-scroll-content loadingText="Loading more sales..."></ion-infinite-scroll-content>
      </ion-infinite-scroll>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
.sale-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.sale-header h2 {
  font-weight: 700;
  font-size: 1.2rem;
  color: var(--ion-color-primary);
  margin: 0;
  word-break: break-word;
}

.quantity {
  font-weight: 600;
  color: var(--ion-color-secondary);
  font-size: 1rem;
  white-space: nowrap;
}

.sale-date {
  font-size: 0.9rem;
  color: var(--ion-color-medium);
}

.no-sales {
  text-align: center;
  color: var(--ion-color-medium);
  margin-top: 40px;
  font-size: 1.1rem;
  font-weight: 600;
}

.sale-price,
.sale-total {
  font-weight: 600;
  color: var(--ion-color-secondary);
  margin-top: 4px;
  font-size: 1rem;
}

.sale-card {
  display: flex;
  gap: 16px;
  background: var(--ion-background-color, #fff);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgb(0 0 0 / 0.1);
  padding: 16px 20px;
  transition: box-shadow 0.3s ease;
  cursor: default;
}

.sale-card:hover {
  box-shadow: 0 6px 20px rgb(0 0 0 / 0.15);
}

.sale-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.sale-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
</style>
