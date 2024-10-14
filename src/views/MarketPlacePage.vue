<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonRow, IonGrid, IonCol, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent, IonButton, IonIcon, IonModal, IonInput, IonItem } from '@ionic/vue';
import { add } from 'ionicons/icons';
import axios from 'axios';
import {getToken} from "@/utils/localStorage";

const products = ref([]);
const vendorMap = ref({});
const focusedProduct = ref(null);
const showModal = ref(false);
const newProduct = ref({ name: '', description: '', imageUrl: '', category: '', price: 0, quantity: 0 });
const apiUrl = import.meta.env.VITE_API_URL;

const fetchVendors = async () => {
  const token = await getToken();
  const vendorSlugs = products.value
      .map(product => product.userSlug)
      .filter((userSlug, index, self) => self.indexOf(userSlug) === index);

  const vendorPromises = vendorSlugs.map(userSlug =>
      axios.get(`${apiUrl}/users/${userSlug}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }).then(response => {
        vendorMap.value[userSlug] = response.data;
      }).catch(error => {
        console.error(`Failed to fetch vendor info for userSlug ${userSlug}:`, error);
      })
  );
  await Promise.all(vendorPromises);
};

const fetchProducts = async () => {
  try {
    const response = await axios.get(`${apiUrl}/products`);
    products.value = response.data;
    await fetchVendors();
  } catch (error) {
    console.error('Failed to fetch products:', error);
  }
};

const createProduct = async () => {
  const token = await getToken();
  try {
    await axios.post(`${apiUrl}/products`, newProduct.value, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
    });
    await fetchProducts();
    showModal.value = false;
  } catch (error) {
    console.error('Failed to create product:', error.response.data.reason);
  }
};

const focusProduct = (product) => {
  focusedProduct.value = focusedProduct.value === product ? null : product;
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
        <ion-button slot="end" @click="showModal = true" :inert="showModal">
          <ion-icon :icon="add"></ion-icon>
        </ion-button>
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
            <ion-card @click="focusProduct(product)" :class="{ focused: focusedProduct === product }">
              <img :src="product.imageUrl" alt="Product Image" />
              <ion-card-header>
                <ion-card-title>{{ product.name }}</ion-card-title>
                <ion-card-subtitle>{{ product.category }}</ion-card-subtitle>
              </ion-card-header>
              <ion-card-content>
                <p>{{ product.description }}</p>
                <p>Price: {{ product.price.toFixed(2) }}€</p>
                <p>Available Quantity: {{ product.quantity }}</p>
                <p>Vendor: {{ vendorMap[product.userSlug]?.username }}</p> <!-- Update this line -->
                <ion-button v-if="focusedProduct === product" expand="block">Add to Cart</ion-button>
              </ion-card-content>
            </ion-card>
          </ion-col>
        </ion-row>
      </ion-grid>

      <ion-modal :isOpen="showModal">
        <div class="modal-content">
          <ion-header>
            <ion-toolbar>
              <ion-title>Create Product</ion-title>
              <ion-button slot="end" @click="showModal = false">Close</ion-button>
            </ion-toolbar>
          </ion-header>
          <ion-content>
            <form @submit.prevent="createProduct">
              <ion-item>
                <ion-input placeholder="Name" v-model="newProduct.name"></ion-input>
              </ion-item>
              <ion-item>
                <ion-input placeholder="Description" v-model="newProduct.description"></ion-input>
              </ion-item>
              <ion-item>
                <ion-input placeholder="Image Url" v-model="newProduct.imageUrl"></ion-input>
              </ion-item>
              <ion-item>
                <ion-input placeholder="Category" v-model="newProduct.category"></ion-input>
              </ion-item>
              <ion-item>
                <ion-input placeholder="Price €" type="number" v-model="newProduct.price"></ion-input>
              </ion-item>
              <ion-item>
                <ion-input placeholder="Stock Number" type="number" v-model="newProduct.quantity"></ion-input>
              </ion-item>
              <ion-button expand="block" type="submit">Create Product</ion-button>
            </form>
          </ion-content>
        </div>
      </ion-modal>

    </ion-content>
  </ion-page>
</template>

<style scoped>
ion-card {
  margin: 2%;
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
  border-radius: 0.5em;
}

.focused {
  border: 0.200em solid #3880ff;
  transform: scale(1.05);
}

.modal-content {
  height: 100%;
}
</style>
