<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonRow, IonGrid, IonCol, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent } from '@ionic/vue';
import axios from 'axios';

const products = ref([]);
const apiUrl = import.meta.env.VITE_API_URL;

const fetchProducts = async () => {
  try {
    const response = await axios.get(`${apiUrl}/products`);
    products.value = response.data;
  } catch (error) {
    console.error('Failed to fetch products:', error);
  }
};

onMounted(() => {
  fetchProducts();
});
</script>

<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>MarketPlace</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <ion-title size="large">MarketPlace</ion-title>
        </ion-toolbar>
      </ion-header>

      <ion-grid>
        <ion-row>
          <ion-col size="12" size-md="6" size-lg="4" v-for="product in products" :key="product.id">
            <ion-card>
              <img :src="product.imageUrl" alt="Product Image" />
              <ion-card-header>
                <ion-card-title>{{ product.name }}</ion-card-title>
                <ion-card-subtitle>{{ product.category }}</ion-card-subtitle>
              </ion-card-header>
              <ion-card-content>
                <p>{{ product.description }}</p>
                <p>Price: ${{ product.price.toFixed(2) }}</p>
                <p>Available Quantity: {{ product.quantity }}</p>
              </ion-card-content>
            </ion-card>
          </ion-col>
        </ion-row>
      </ion-grid>

    </ion-content>
  </ion-page>
</template>

<style scoped>
ion-card {
  margin: 10px;
}

ion-card-title {
  font-size: 1.5rem;
}

ion-card-subtitle {
  color: #666;
}

img {
  width: 100%;
  height: auto;
  border-radius: 8px;
}
</style>
