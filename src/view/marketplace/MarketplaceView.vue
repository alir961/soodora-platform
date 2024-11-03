<script setup lang="ts">
import { Reactive, reactive, Ref, ref } from 'vue';
import {
  IonButton,
  IonContent,
  IonGrid,
  IonHeader,
  IonIcon,
  IonInput,
  IonItem,
  IonModal,
  IonRow,
  IonTitle,
  IonToolbar,
  onIonViewWillEnter,
} from '@ionic/vue';
import { add } from 'ionicons/icons';
import { createProduct, CreateProductRequest, retrieveProducts } from '@/client/productClient';
import { Product } from '@/entity/Product';
import { getAuthenticatedUser } from '@/utils';
import ProductCard from '@/component/marketplace/ProductCard.vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const products: Ref<Product[]> = ref([]);
const showModal: Ref<boolean> = ref(false);
const newProduct: Reactive<CreateProductRequest> = reactive({
  name: '',
  description: '',
  imageUrl: '',
  category: '',
  price: 0,
  quantity: 1,
});

const loadProducts = async () => {
  try {
    products.value = await retrieveProducts();
  } catch (error) {
    console.error('Error loading products:', error);
  }
};

const submitProduct = async () => {
  try {
    await createProduct(newProduct);
    await loadProducts();
    showModal.value = false;
  } catch (error) {
    console.error('Failed to create product:', error);
  }
};

onIonViewWillEnter(async () => {
  await getAuthenticatedUser();
  await loadProducts();
});
</script>

<template>
  <ApplicationViewport title="Marketplace" authenticated>
    <template #toolbar>
      <div id="toolbar-button">
        <ion-button size="small" @click="showModal = true" :inert="showModal">
          <ion-icon :icon="add"></ion-icon>
        </ion-button>
      </div>
    </template>

    <ion-grid>
      <ion-row>
        <ProductCard v-for="product in products" :key="product.slug" :product="product" />
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
          <form @submit.prevent="submitProduct">
            <ion-item>
              <ion-input
                label="Name"
                label-placement="floating"
                placeholder="Enter name"
                v-model="newProduct.name"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Description"
                label-placement="floating"
                placeholder="Enter description"
                v-model="newProduct.description"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Image URL"
                label-placement="floating"
                placeholder="Enter link to image"
                v-model="newProduct.imageUrl"
                clearInput></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Category"
                label-placement="floating"
                placeholder="Enter commas spaced categories"
                v-model="newProduct.category"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Price"
                label-placement="floating"
                placeholder="Enter price (€)"
                type="number"
                v-model="newProduct.price"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Stock Number"
                label-placement="floating"
                placeholder="Enter stock number"
                type="number"
                v-model="newProduct.quantity"></ion-input>
            </ion-item>
            <ion-button expand="block" type="submit">Create Product</ion-button>
          </form>
        </ion-content>
      </div>
    </ion-modal>
  </ApplicationViewport>
</template>

<style scoped>
ion-card-title {
  font-size: 1.5rem;
}

ion-card-subtitle {
  color: #666;
}

#toolbar-button {
  width: 25%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;

  & ion-button {
    --border-radius: 15px;

    height: 70%;
    width: 50%;
  }
}

img {
  width: 100%;
  height: auto;
  border-radius: 0.5em;
}

.modal-content {
  height: 100%;

  & ion-button {
    margin: 20px;
  }
}
</style>
