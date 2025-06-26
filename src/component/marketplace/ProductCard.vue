<script setup lang="ts">
import {
  alertController,
  IonButton,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardSubtitle,
  IonCardTitle,
  IonCol,
  IonIcon,
  toastController,
} from '@ionic/vue';
import { Product } from '@/entity/Product';
import { PropType, Ref, ref } from 'vue';
import UsernameTag from '@/component/user/UsernameTag.vue';
import customTrashIcon from '@/assets/icons/trash.svg';
import { useAuthStore } from '@/store/reactiveStorage';
import { callDeleteProduct } from '@/client/productClient';

defineProps({
  product: {
    required: true,
    type: Object as PropType<Product>,
  },
});

const emit = defineEmits(['product-deleted']);

const authStore = useAuthStore();

const focusedProduct: Ref<Product | null> = ref(null);

const focusProduct = (product: Product) => {
  focusedProduct.value = focusedProduct.value === product ? null : product;
};

const askDeleteProduct = async (slug: string) => {
  const alert = await alertController.create({
    header: 'Confirm Product Deletion',
    message: 'Are you sure you want to delete this product?',
    buttons: [
      { text: 'Cancel', role: 'cancel' },
      {
        text: 'Delete',
        handler: async () => {
          try {
            await callDeleteProduct(slug);
            emit('product-deleted', slug);
          } catch (error) {
            const toast = await toastController.create({
              message: 'Failed to delete post.',
              duration: 1500,
              position: 'top',
            });
            await toast.present();
          }
        },
      },
    ],
  });
  await alert.present();
};
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4">
    <ion-card @click="focusProduct(product)" :class="{ focused: focusedProduct === product }">
      <img :src="product.imageUrl" :alt="`Product ${product.name}`" />
      <ion-card-header>
        <ion-card-title>{{ product.name }}</ion-card-title>
        <ion-card-subtitle>{{ product.category }}</ion-card-subtitle>
      </ion-card-header>
      <ion-card-content>
        <p>{{ product.description }}</p>
        <p>Price: {{ product.price !== null ? product.price.toFixed(2) + '€' : 'No price' }}</p>
        <p>Available Quantity: {{ product.quantity !== null ? product.quantity : 'No quantity' }}</p>
        <p>Vendor: <UsernameTag :user="product.vendor" /></p>
        <ion-button v-if="focusedProduct === product" expand="block">Add to Cart</ion-button>
      </ion-card-content>
      <ion-button
        v-if="product.vendor.slug === authStore.getAuthenticatedUserSlug"
        fill="clear"
        size="default"
        @click.stop="askDeleteProduct(product.slug)"
        class="delete-button">
        <ion-icon :src="customTrashIcon" />
      </ion-button>
    </ion-card>
  </ion-col>
</template>

<style scoped>
.focused {
  border: 0.2em solid #3880ff;
  transform: scale(1.05);
}
</style>
