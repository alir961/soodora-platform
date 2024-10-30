<script setup lang="ts">
import {IonButton, IonCard, IonCardContent, IonCardHeader, IonCardSubtitle, IonCardTitle, IonCol} from "@ionic/vue";
import {Product} from "@/entity/Product";
import {PropType, ref} from "vue";

defineProps({
  product: {
    required: true,
    type: Object as PropType<Product>,
  }
});

const focusedProduct = ref(null as Product | null);

const focusProduct = (product: Product) => {
  focusedProduct.value = focusedProduct.value === product ? null : product;
};
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4">
    <ion-card @click="focusProduct(product)" :class="{ focused: focusedProduct === product }">
      <img :src="product.imageUrl" :alt="`Product ${product.name}`"/>
      <ion-card-header>
        <ion-card-title>{{ product.name }}</ion-card-title>
        <ion-card-subtitle>{{ product.category }}</ion-card-subtitle>
      </ion-card-header>
      <ion-card-content>
        <p>{{ product.description }}</p>
        <p>Price: {{ product.price.toFixed(2) }}€</p>
        <p>Available Quantity: {{ product.quantity }}</p>
        <p>Vendor: {{ product.vendor.username }}</p>
        <ion-button v-if="focusedProduct === product" expand="block">Add to Cart</ion-button>
      </ion-card-content>
    </ion-card>
  </ion-col>
</template>

<style scoped>
.focused {
  border: 0.200em solid #3880ff;
  transform: scale(1.05);
}
</style>