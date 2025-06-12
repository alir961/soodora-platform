<script setup lang="ts">
import { Ref, ref, onMounted } from 'vue';
import { IonGrid, IonRow, onIonViewWillEnter } from '@ionic/vue';
import { retrieveProducts } from '@/client/productClient';
import { Product } from '@/entity/Product';
import ProductCard from '@/component/marketplace/ProductCard.vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { User } from '@/entity/User';
import { useAuthStore } from '@/store/reactiveStorage';

const products: Ref<Product[]> = ref([]);
const userData: Ref<User | null> = ref(null);
const authStore = useAuthStore();

onIonViewWillEnter(async () => {
  await loadProducts();
});

onMounted(() => {
  if (authStore.isAuthenticated) userData.value = authStore.getAuthenticatedUser;
});

const loadProducts = async () => {
  try {
    products.value = await retrieveProducts();
  } catch (error) {
    console.error('Error loading products:', error);
  }
};
</script>

<template>
  <ApplicationViewport title="Marketplace">
    <ion-grid>
      <ion-row>
        <ProductCard v-for="product in products" :key="product.slug" :product="product" />
      </ion-row>
    </ion-grid>
  </ApplicationViewport>
</template>
