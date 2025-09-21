<script setup lang="ts">
import {
  alertController,
  AlertInput,
  IonAvatar,
  IonButton,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardSubtitle,
  IonCardTitle,
  IonCol,
  IonContent,
  IonIcon,
  IonInput,
  IonItem,
  IonLabel,
  IonModal,
  IonSelect,
  IonSelectOption,
} from '@ionic/vue';
import { Product } from '@/entity/Product';
import { PropType, ref } from 'vue';
import UsernameTag from '@/component/user/UsernameTag.vue';
import customTrashIcon from '@/assets/icons/trash.svg';
import customShareIcon from '@/assets/icons/share.svg';
import { useAuthStore } from '@/store/reactiveStorage';
import { callDeleteProduct, updateProduct } from '@/client/productClient';
import Avatar from '@/component/user/AvatarItem.vue';
import DateDisplay from '@/component/date/DateDisplay.vue';
import { createProductCanvas, downloadCanvas, ExportTheme } from '@/graphics/canvasExport';
import { createCheckoutSession } from '@/client/paymentClient';
import { showToast } from '@/utils';
import { arrowBackOutline, pencil } from 'ionicons/icons';
import FileUpload from '@/component/file/FileUpload.vue';
import { Artist } from '@/entity/Artist';

const props = defineProps({
  product: {
    required: true,
    type: Object as PropType<Product>,
  },
});

const emit = defineEmits(['product-deleted']);
const authStore = useAuthStore();

const showImageModal = ref(false);
const modalImageSrc = ref('');
const modalBreakpoint = ref(0.5);
const addToCartBtnRef = ref<HTMLElement | null>(null);
const productImageHeight = ref(0);
const localProduct = ref<Product>({ ...props.product });

const quantity = ref(1);
const maxQuantity = ref(0);

const editModalOpen = ref(false);
const emptyArtist: Artist = {
  slug: '',
  username: '',
  description: '',
  avatarUrl: '',
};
const editedProduct = ref<Product>({
  slug: '',
  name: '',
  description: '',
  imageUrl: '',
  category: '',
  price: 0,
  quantity: 0,
  vendor: emptyArtist,
  createdAt: new Date(),
});
const selectedFile = ref<File | null>(null);

const onProductImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement;
  productImageHeight.value = img.clientHeight;
};

const openImageModal = (src: string, productQuantity: number | null) => {
  modalImageSrc.value = src;
  maxQuantity.value = productQuantity ?? 1;
  quantity.value = 1;

  const buttonAndPaddingHeight = 180;
  const scaledImageHeight = productImageHeight.value * 1.1;
  const totalHeightPx = scaledImageHeight + buttonAndPaddingHeight;
  const viewportHeight = window.innerHeight;
  modalBreakpoint.value = Math.min(Math.max(totalHeightPx / viewportHeight, 0.2), 1);
  showImageModal.value = true;
};

const closeImageModal = () => {
  showImageModal.value = false;
};

const increaseQuantity = () => {
  if (quantity.value < maxQuantity.value) quantity.value++;
};

const decreaseQuantity = () => {
  if (quantity.value > 1) quantity.value--;
};

const quantityChanged = (event: Event) => {
  const target = event.target as HTMLInputElement;
  let val = parseInt(target.value);
  if (isNaN(val) || val < 1) val = 1;
  else if (val > maxQuantity.value) val = maxQuantity.value;
  quantity.value = val;
};

const goToCheckout = async (product: Product) => {
  if (product.price == null) {
    console.error('Product has no price');
    return;
  }

  const authenticatedUserSlug = authStore.getAuthenticatedUserSlug;
  if (!authenticatedUserSlug) {
    console.error('Artist slug is null');
    return;
  }

  try {
    const response = await createCheckoutSession({
      name: product.name,
      amount: Math.round(product.price * 100),
      imageUrl: product.imageUrl,
      productSlug: product.slug,
      vendorSlug: product.vendor.slug,
      buyerSlug: authenticatedUserSlug,
      quantity: quantity.value,
    });
    window.location.href = response.url;
  } catch (error: unknown) {
    const message = 'Checkout Failed. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

const askDeleteProduct = async (slug: string) => {
  const alert = await alertController.create({
    header: 'Confirm Product Deletion',
    message: 'Are you sure you want to delete this product?',
    buttons: [
      { text: 'Cancel', role: 'cancel' },
      {
        text: 'Delete',
        role: 'destructive',
        handler: async () => {
          try {
            await callDeleteProduct(slug);
            emit('product-deleted', slug);
            await showToast('Succeed to delete your product');
          } catch (error: unknown) {
            const message = 'Failed to delete your product. ' + (error instanceof Error ? error.message : '');
            await showToast(message, 'danger');
          }
        },
      },
    ],
  });
  await alert.present();
};

const exportThemes = [
  { label: 'Black', type: 'radio', value: 'black' },
  { label: 'White', type: 'radio', value: 'white' },
  { label: 'Transparent', type: 'radio', value: 'transparent' },
] as AlertInput[];

const exportProduct = async (product: Product) => {
  const alert = await alertController.create({
    header: 'Select theme for export',
    inputs: exportThemes,
    buttons: [
      { text: 'Cancel', role: 'cancel' },
      {
        text: 'Export',
        handler: async (selectedTheme: string) => {
          if (!selectedTheme) return;
          try {
            const canvas = await createProductCanvas(product, selectedTheme as ExportTheme);
            downloadCanvas(canvas);
            await showToast('Succeed to export product');
          } catch (error: unknown) {
            const message = 'Failed to export product. ' + (error instanceof Error ? error.message : '');
            await showToast(message, 'danger');
          }
        },
      },
    ],
  });
  await alert.present();
};

const openEditModal = () => {
  editedProduct.value = { ...localProduct.value };
  editModalOpen.value = true;
};

const submitEdit = async () => {
  const formData = new FormData();
  formData.append(
    'data',
    new Blob(
      [
        JSON.stringify({
          name: editedProduct.value.name,
          description: editedProduct.value.description,
          category: editedProduct.value.category,
          price: editedProduct.value.price,
          quantity: editedProduct.value.quantity,
        }),
      ],
      { type: 'application/json' }
    )
  );
  if (selectedFile.value) formData.append('file', selectedFile.value);

  try {
    localProduct.value = await updateProduct(localProduct.value.slug, formData);
    editModalOpen.value = false;
    selectedFile.value = null;
    await showToast('Product updated successfully');
  } catch (error: unknown) {
    const message = 'Failed to update your product. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

const onFileSelected = (file: File | null) => {
  selectedFile.value = file;
};
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4">
    <ion-card @click="openImageModal(localProduct.imageUrl, localProduct.quantity)">
      <div class="product-image-wrapper">
        <img :src="localProduct.imageUrl" :alt="`Product ${localProduct.name}`" @load="onProductImageLoad" />
      </div>
      <div class="product-meta">
        <div class="vendor-info">
          <ion-avatar>
            <Avatar :user="localProduct.vendor" @click.stop />
          </ion-avatar>
          <UsernameTag :user="localProduct.vendor" @click.stop />
        </div>
        <div class="product-buttons">
          <ion-button
            fill="clear"
            size="default"
            color="light"
            class="delete-button"
            @click.stop="exportProduct(localProduct)">
            <ion-icon :src="customShareIcon" />
          </ion-button>
          <ion-button
            v-if="localProduct.vendor.slug === authStore.getAuthenticatedUserSlug"
            fill="clear"
            size="default"
            color="light"
            class="edit-button"
            @click.stop="openEditModal">
            <ion-icon :src="pencil" />
          </ion-button>
          <ion-button
            v-if="localProduct.vendor.slug === authStore.getAuthenticatedUserSlug"
            fill="clear"
            size="default"
            color="light"
            class="delete-button"
            @click.stop="askDeleteProduct(localProduct.slug)">
            <ion-icon :src="customTrashIcon" />
          </ion-button>
        </div>
      </div>
      <ion-card-header>
        <ion-card-title class="product-title">{{ localProduct.name }}</ion-card-title>
        <div class="category-date-row">
          <ion-card-subtitle>{{ localProduct.category }}</ion-card-subtitle>
          <DateDisplay :date="localProduct.createdAt" class="product-date" />
        </div>
      </ion-card-header>
      <ion-card-content>
        <p class="product-description">{{ localProduct.description }}</p>
        <div class="price-quantity-row">
          <h3 class="product-price">
            {{ localProduct.price !== null ? localProduct.price.toFixed(2) + '€' : 'No price' }}
          </h3>
          <p class="product-quantity">In Stock: {{ localProduct.quantity !== null ? localProduct.quantity : 'N/A' }}</p>
        </div>
      </ion-card-content>
    </ion-card>

    <ion-modal
      :is-open="showImageModal"
      @did-dismiss="closeImageModal"
      backdrop-dismiss="true"
      :breakpoints="[0, modalBreakpoint]"
      :initial-breakpoint="modalBreakpoint"
      handle-behavior="cycle">
      <ion-content class="ion-padding modal-content">
        <ion-item lines="none" class="quantity-selector">
          <ion-button size="small" @click="decreaseQuantity" :disabled="quantity <= 1">-</ion-button>
          <ion-input
            type="number"
            min="1"
            :max="maxQuantity"
            v-model.number="quantity"
            @ionChange="quantityChanged"
            style="max-width: 60px; text-align: center">
          </ion-input>
          <ion-button size="small" @click="increaseQuantity" :disabled="quantity >= maxQuantity">+</ion-button>
          <ion-label class="quantity-label"> Quantité: {{ quantity }} / {{ maxQuantity }} en stock </ion-label>
        </ion-item>
        <ion-button
          ref="addToCartBtnRef"
          expand="block"
          @click="goToCheckout(localProduct)"
          class="buy-button"
          :disabled="quantity < 1 || quantity > maxQuantity">
          Buy
        </ion-button>
      </ion-content>
    </ion-modal>

    <ion-modal :is-open="editModalOpen" @did-dismiss="editModalOpen = false">
      <ion-content class="ion-padding">
        <div class="modal-header">
          <h2>Edit Product</h2>
          <IonButton class="inline-back-button" slot="start" fill="clear" @click="$router.back()">
            <IonIcon :icon="arrowBackOutline" />
            Back
          </IonButton>
        </div>

        <ion-item>
          <IonLabel position="stacked">Name</IonLabel>
          <IonInput v-model="editedProduct.name" />
        </ion-item>
        <ion-item>
          <IonLabel position="stacked">Description</IonLabel>
          <IonInput v-model="editedProduct.description" />
        </ion-item>
        <ion-item>
          <ion-label position="stacked">Category</ion-label>
          <ion-select interface="action-sheet" placeholder="Select a category" v-model="editedProduct.category">
            <ion-select-option value="Vinyl">Vinyl</ion-select-option>
            <ion-select-option value="CD">CD</ion-select-option>
            <ion-select-option value="Clothing">Clothing</ion-select-option>
            <ion-select-option value="Concert Tickets">Concert Tickets</ion-select-option>
            <ion-select-option value="Digital Downloads">Digital Downloads</ion-select-option>
            <ion-select-option value="Accessories">Accessories</ion-select-option>
            <ion-select-option value="Posters">Posters</ion-select-option>
          </ion-select>
        </ion-item>
        <ion-item>
          <IonLabel position="stacked">Price</IonLabel>
          <IonInput type="number" v-model.number="editedProduct.price" />
        </ion-item>
        <ion-item>
          <IonLabel position="stacked">Quantity</IonLabel>
          <IonInput type="number" v-model.number="editedProduct.quantity" />
        </ion-item>
        <ion-item>
          <FileUpload fieldLabel="Select Image" label="Choose File" accept="image/*" @file-selected="onFileSelected" />
        </ion-item>

        <ion-button expand="block" @click="submitEdit">Save</ion-button>
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

.product-date {
  font-size: 1.2rem;
  font-weight: 400;
  color: var(--ion-color-medium);
  margin-left: auto;
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

.quantity-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.quantity-label {
  font-weight: bold;
  margin-left: 8px;
}
</style>
