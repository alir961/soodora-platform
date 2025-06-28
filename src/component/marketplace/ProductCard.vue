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
  IonModal,
  IonContent,
  IonAvatar,
  toastController,
  AlertInput,
} from '@ionic/vue';
import { Product } from '@/entity/Product';
import { PropType } from 'vue';
import UsernameTag from '@/component/user/UsernameTag.vue';
import customTrashIcon from '@/assets/icons/trash.svg';
import customShareIcon from '@/assets/icons/share.svg';
import { useAuthStore } from '@/store/reactiveStorage';
import { callDeleteProduct } from '@/client/productClient';
import { ref } from 'vue';
import Avatar from '@/component/user/AvatarItem.vue';
import DateDisplay from '@/component/date/DateDisplay.vue';
import { createProductCanvas, downloadCanvas, ExportTheme } from '@/graphics/canvasExport';

defineProps({
  product: {
    required: true,
    type: Object as PropType<Product>,
  },
});

const emit = defineEmits(['product-deleted']);

const authStore = useAuthStore();

const showImageModal = ref(false);
const modalImageSrc = ref('');

const openImageModal = (src: string) => {
  modalImageSrc.value = src;
  showImageModal.value = true;
};

const closeImageModal = () => {
  showImageModal.value = false;
};

const showComingSoonToast = async () => {
  const toast = await toastController.create({
    message: 'This feature is coming soon!',
    duration: 1500,
    color: 'primary',
    position: 'top',
  });
  await toast.present();
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

const exportThemes = [
  {
    label: 'Black',
    type: 'radio',
    value: 'black',
  },
  {
    label: 'White',
    type: 'radio',
    value: 'white',
  },
  {
    label: 'Transparent',
    type: 'radio',
    value: 'transparent',
  },
] as AlertInput[];

const exportProduct = async (product: Product) => {
  const alert = await alertController.create({
    header: 'Select theme for export',
    inputs: exportThemes,
    buttons: [
      {
        text: 'Cancel',
        role: 'cancel',
      },
      {
        text: 'Export',
        handler: async (selectedTheme: string) => {
          if (!selectedTheme) return;
          try {
            const canvas = await createProductCanvas(product, selectedTheme as ExportTheme);

            downloadCanvas(canvas);
          } catch (err) {
            console.error('Export failed:', err);
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
    <ion-card @click="openImageModal(product.imageUrl)">
      <div class="product-image-wrapper">
        <img :src="product.imageUrl" :alt="`Product ${product.name}`" />
      </div>
      <div class="product-meta">
        <div class="vendor-info">
          <ion-avatar>
            <Avatar :user="product.vendor" />
          </ion-avatar>
          <UsernameTag :user="product.vendor" />
        </div>
        <ion-button
          v-if="product.vendor.slug === authStore.getAuthenticatedUserSlug"
          fill="clear"
          size="default"
          color="light"
          @click.stop="askDeleteProduct(product.slug)"
          class="delete-button">
          <ion-icon :src="customTrashIcon" />
        </ion-button>
        <ion-button fill="clear" size="default" @click.stop="exportProduct(product)" class="delete-button">
          <ion-icon :src="customShareIcon" />
        </ion-button>
      </div>
      <ion-card-header>
        <ion-card-title class="product-title">{{ product.name }}</ion-card-title>
        <div class="category-date-row">
          <ion-card-subtitle>{{ product.category }}</ion-card-subtitle>
          <ion-card-subtitle><DateDisplay :date="product.createdAt" /></ion-card-subtitle>
        </div>
      </ion-card-header>
      <ion-card-content>
        <p class="product-description">{{ product.description }}</p>
        <div class="price-quantity-row">
          <h3 class="product-price">
            {{ product.price !== null ? product.price.toFixed(2) + '€' : 'No price' }}
          </h3>
          <p class="product-quantity">In Stock: {{ product.quantity !== null ? product.quantity : 'N/A' }}</p>
        </div>
      </ion-card-content>
    </ion-card>
    <ion-modal
      :is-open="showImageModal"
      @did-dismiss="closeImageModal"
      backdrop-dismiss="true"
      :breakpoints="[0, 0.4, 0.5]"
      :initial-breakpoint="0.5"
      handle-behavior="cycle">
      <ion-content class="ion-padding modal-content">
        <img :src="modalImageSrc" alt="Fullscreen product image" class="modal-image" />
        <ion-button expand="block" @click="showComingSoonToast" class="buy-button">Add to Cart</ion-button>
      </ion-content>
    </ion-modal>
  </ion-col>
</template>

<style scoped>
ion-card-header {
  padding-bottom: 0;
  padding-top: 0;
}

ion-card-subtitle {
  font-size: 1rem;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-image-wrapper {
  width: 100%;
  max-height: 250px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.product-image-wrapper img {
  width: 100%;
  height: auto;
  object-fit: cover;
}

.product-description {
  margin-top: 0.5rem;
  font-size: 0.95rem;
}

.product-meta {
  color: #666;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

.vendor-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-left: 1rem;
}

.product-price {
  font-weight: bold;
  font-size: 1.3rem;
  margin-bottom: 0.25rem;
}

.product-quantity {
  font-size: 1rem;
}

.product-title {
  font-size: 1.35rem;
}

.modal-content {
  border-radius: 12px;
  overflow: hidden;
}

.modal-image {
  border-radius: 15px;
}

.buy-button {
  margin-top: 1rem;
  --background: var(--ion-color-primary);
  --color: white;
}

.price-quantity-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-top: 0.5rem;
}

.vendor-info ion-avatar {
  width: 32px;
  height: 32px;
}

.category-date-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}
</style>
