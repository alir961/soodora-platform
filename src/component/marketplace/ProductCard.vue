<script setup lang="ts">
import {
  alertController,
  AlertInput,
  IonAvatar,
  IonCard,
  IonCol,
  IonIcon,
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
import { closeOutline, pencil } from 'ionicons/icons';
import ImageUploadPreview from '@/component/marketplace/ImageUploadPreview.vue';
import { Artist } from '@/entity/Artist';
import CurrencySelector from '@/component/deviseSelector/CurrencySelector.vue';

const props = defineProps({
  product: {
    required: true,
    type: Object as PropType<Product>,
  },
});

const emit = defineEmits(['product-deleted']);
const authStore = useAuthStore();

const showImageModal = ref(false);
const showPurchaseSheet = ref(false);
const modalImageSrc = ref('');
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
  price: {
    amount: 0,
    currency: 'EUR',
    symbol: '€',
  },
  quantity: 0,
  vendor: emptyArtist,
  createdAt: new Date(),
});
const selectedFile = ref<File | null>(null);

const showImagePreview = ref(false);

const onProductImageLoad = (event: Event) => {
  const img = event.target as HTMLImageElement;
  productImageHeight.value = img.clientHeight;
};

const openImageModal = (src: string, productQuantity: number | null) => {
  modalImageSrc.value = src;
  maxQuantity.value = productQuantity ?? 1;
  quantity.value = 1;
  showPurchaseSheet.value = true;
};

const openImagePreview = (e: Event) => {
  e.stopPropagation();
  showImagePreview.value = true;
};

const closeImagePreview = () => {
  showImagePreview.value = false;
};

const closePurchaseSheet = () => {
  showPurchaseSheet.value = false;
  quantity.value = 1;
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
      amount: product.price.amount,
      currency: product.price.currency,
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

const closeEditModal = () => {
  editModalOpen.value = false;
  selectedFile.value = null;
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
          price: editedProduct.value.price.amount,
          currency: editedProduct.value.price.currency,
          quantity: editedProduct.value.quantity,
        }),
      ],
      { type: 'application/json' }
    )
  );
  if (selectedFile.value) formData.append('file', selectedFile.value);

  try {
    localProduct.value = await updateProduct(localProduct.value.slug, formData);
    closeEditModal();
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
    <ion-card class="product-card">
      <div class="card-image-wrapper" @click="openImagePreview">
        <img :src="localProduct.imageUrl" :alt="`Product ${localProduct.name}`" @load="onProductImageLoad" />
        
        <!-- Action buttons overlay -->
        <div class="card-actions" @click.stop>
          <button class="action-icon export" @click="exportProduct(localProduct)" title="Export">
            <ion-icon :src="customShareIcon" />
          </button>
          <button 
            v-if="localProduct.vendor.slug === authStore.getAuthenticatedUserSlug"
            class="action-icon edit" 
            @click="openEditModal"
            title="Edit">
            <ion-icon :src="pencil" />
          </button>
          <button 
            v-if="localProduct.vendor.slug === authStore.getAuthenticatedUserSlug"
            class="action-icon delete" 
            @click="askDeleteProduct(localProduct.slug)"
            title="Delete">
            <ion-icon :src="customTrashIcon" />
          </button>
        </div>

        <!-- Price badge -->
        <div class="price-badge">
          <span class="price-amount">{{ localProduct.price?.amount.toFixed(2) }}</span>
          <span class="price-currency">{{ localProduct.price?.symbol }}</span>
        </div>
      </div>

      <div class="card-content">
        <div class="product-header">
          <h3 class="product-name">{{ localProduct.name }}</h3>
          <span class="product-category">{{ localProduct.category }}</span>
        </div>

        <p class="product-description">{{ localProduct.description }}</p>

        <div class="card-footer">
          <div class="vendor-chip" @click.stop>
            <ion-avatar class="vendor-avatar">
              <Avatar :user="localProduct.vendor" />
            </ion-avatar>
            <UsernameTag :user="localProduct.vendor" class="vendor-name" />
          </div>
          
          <div class="stock-info">
            <span class="stock-label">Stock:</span>
            <span class="stock-value">{{ localProduct.quantity ?? 'N/A' }}</span>
          </div>
        </div>

        <button 
          class="buy-button"
          @click="openImageModal(localProduct.imageUrl, localProduct.quantity)">
          Buy Now
        </button>
      </div>
    </ion-card>

    <!-- Image Preview Modal -->
    <transition name="preview-fade">
      <div v-if="showImagePreview" class="image-preview-overlay" @click="closeImagePreview">
        <div class="image-preview-container" @click.stop>
          <button class="preview-close" @click="closeImagePreview">✕</button>
          <img :src="localProduct.imageUrl" :alt="localProduct.name" />
        </div>
      </div>
    </transition>

    <!-- Purchase Bottom Sheet -->
    <transition name="sheet-overlay">
      <div v-if="showPurchaseSheet" class="purchase-overlay" @click="closePurchaseSheet">
        <div class="purchase-sheet" @click.stop>
          <div class="sheet-handle"></div>
          
          <div class="product-preview">
            <img :src="localProduct.imageUrl" :alt="localProduct.name" class="preview-image" />
            <div class="preview-info">
              <h3>{{ localProduct.name }}</h3>
              <p class="preview-vendor">by @{{ localProduct.vendor.username }}</p>
              <p class="preview-price">
                {{ localProduct.price?.amount.toFixed(2) }} {{ localProduct.price?.symbol }}
              </p>
            </div>
          </div>

          <div class="quantity-section">
            <label>Quantity</label>
            <div class="quantity-controls">
              <button 
                class="qty-btn" 
                @click="decreaseQuantity" 
                :disabled="quantity <= 1">
                −
              </button>
              <div class="qty-display">
                <span class="qty-number">{{ quantity }}</span>
                <span class="qty-max">/ {{ maxQuantity }} in stock</span>
              </div>
              <button 
                class="qty-btn" 
                @click="increaseQuantity" 
                :disabled="quantity >= maxQuantity">
                +
              </button>
            </div>
          </div>

          <div class="purchase-summary">
            <div class="summary-row">
              <span>Subtotal</span>
              <span class="summary-value">
                {{ (localProduct.price!.amount * quantity).toFixed(2) }} {{ localProduct.price?.symbol }}
              </span>
            </div>
          </div>

          <button 
            class="checkout-btn"
            @click="goToCheckout(localProduct)"
            :disabled="quantity < 1 || quantity > maxQuantity">
            Proceed to Checkout
          </button>

          <button class="cancel-btn" @click="closePurchaseSheet">
            Cancel
          </button>
        </div>
      </div>
    </transition>

    <!-- Edit Product Modal -->
    <transition name="fade">
      <div v-if="editModalOpen" class="edit-modal-overlay" @click="closeEditModal">
        <div class="edit-modal-container" @click.stop>
          <div class="edit-modal-header">
            <h2>Edit Product</h2>
            <button class="edit-close-btn" @click="closeEditModal">
              <ion-icon :icon="closeOutline" />
              Close
            </button>
          </div>

          <div class="edit-modal-content">
            <div class="edit-form-field">
              <label>Product Name</label>
              <input type="text" v-model="editedProduct.name" placeholder="Enter product name" />
            </div>

            <div class="edit-form-field">
              <label>Description</label>
              <textarea v-model="editedProduct.description" placeholder="Describe your product" rows="4"></textarea>
            </div>

            <div class="edit-form-field">
              <label>Category</label>
              <select v-model="editedProduct.category">
                <option value="">Select a category</option>
                <option value="Vinyl">Vinyl</option>
                <option value="CD">CD</option>
                <option value="Clothing">Clothing</option>
                <option value="Concert Tickets">Concert Tickets</option>
                <option value="Digital Downloads">Digital Downloads</option>
                <option value="Accessories">Accessories</option>
                <option value="Posters">Posters</option>
              </select>
            </div>

            <div class="edit-form-row">
              <div class="edit-form-field">
                <label>Price</label>
                <input type="number" v-model.number="editedProduct.price.amount" placeholder="0.00" step="0.01" />
              </div>
              <div class="edit-form-field">
                <label>Currency</label>
                <CurrencySelector v-model="editedProduct.price.currency" />
              </div>
            </div>

            <div class="edit-form-field">
              <label>Quantity</label>
              <input type="number" v-model.number="editedProduct.quantity" placeholder="0" min="0" />
            </div>

            <div class="edit-form-field">
              <label>Product Image</label>
              <ImageUploadPreview 
                :current-image-url="editedProduct.imageUrl"
                @file-selected="onFileSelected" />
            </div>

            <button class="edit-save-btn" @click="submitEdit">Save Changes</button>
          </div>
        </div>
      </div>
    </transition>
  </ion-col>
</template>

<style scoped>
/* Product Card */
.product-card {
  margin: 0 0 16px 0;
  border-radius: 16px;
  overflow: hidden;
  background: linear-gradient(
    to bottom,
    rgba(18, 22, 30, 0.85),
    rgba(18, 22, 30, 0.75)
  );
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
  transition: all 0.2s ease;
  width: 100%;
  height: auto;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.35);
  border-color: rgba(255, 255, 255, 0.12);
}

/* Image Section */
.card-image-wrapper {
  position: relative;
  width: 100%;
  height: 220px;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.3);
  cursor: zoom-in;
}

.card-image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.card-image-wrapper:hover img {
  transform: scale(1.05);
}

/* Action Buttons Overlay */
.card-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  z-index: 2;
}

.action-icon {
  appearance: none;
  border: 0;
  background: rgba(18, 22, 30, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.action-icon ion-icon {
  font-size: 18px;
  color: #ffffff;
}

.action-icon.export {
  background: rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.3);
}

.action-icon.export ion-icon {
  color: #60a5fa;
}

.action-icon.edit {
  background: rgba(139, 92, 246, 0.15);
  border-color: rgba(139, 92, 246, 0.3);
}

.action-icon.edit ion-icon {
  color: #a78bfa;
}

.action-icon.delete {
  background: rgba(239, 68, 68, 0.15);
  border-color: rgba(239, 68, 68, 0.3);
}

.action-icon.delete ion-icon {
  color: #f87171;
}

.action-icon:active {
  transform: scale(0.95);
}

/* Price Badge */
.price-badge {
  position: absolute;
  bottom: 12px;
  left: 12px;
  background: rgba(18, 22, 30, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 10px;
  padding: 8px 14px;
  display: flex;
  align-items: baseline;
  gap: 4px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.price-amount {
  font-size: 1.4rem;
  font-weight: 700;
  color: #60a5fa;
  line-height: 1;
}

.price-currency {
  font-size: 1rem;
  font-weight: 600;
  color: #93c5fd;
}

/* Card Content */
.card-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex: 1;
}

.product-header {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.product-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-category {
  font-size: 0.85rem;
  color: #9aa3ad;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 600;
}

.product-description {
  font-size: 0.95rem;
  color: #cfcfcf;
  line-height: 1.5;
  margin: 0;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Card Footer */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.vendor-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  padding: 6px 12px 6px 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.vendor-chip:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.12);
}

.vendor-avatar {
  width: 28px;
  height: 28px;
  flex-shrink: 0;
}

.vendor-name {
  font-size: 0.9rem;
  flex-shrink: 1;
  min-width: 0;
}

.stock-info {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  padding: 6px 10px;
}

.stock-label {
  font-size: 0.8rem;
  color: #9aa3ad;
  font-weight: 600;
}

.stock-value {
  font-size: 0.9rem;
  color: #ffffff;
  font-weight: 700;
}

/* Buy Button */
.buy-button {
  appearance: none;
  border: 0;
  width: 100%;
  height: 44px;
  border-radius: 12px;
  cursor: pointer;
  color: #fff;
  font-weight: 700;
  font-size: 1rem;
  letter-spacing: 0.01em;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  box-shadow: 
    0 8px 20px rgba(37, 99, 235, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
  transition: all 0.2s ease;
  margin-top: 8px;
}

.buy-button:hover {
  box-shadow: 
    0 10px 24px rgba(37, 99, 235, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.16);
  transform: translateY(-1px);
}

.buy-button:active {
  transform: translateY(0);
  box-shadow: 
    0 6px 16px rgba(37, 99, 235, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
}

/* Image Preview Modal */
.image-preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.92);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.image-preview-container {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview-container img {
  max-width: 100%;
  max-height: 90vh;
  object-fit: contain;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.preview-close {
  position: absolute;
  top: -16px;
  right: -16px;
  appearance: none;
  border: 0;
  background: rgba(18, 22, 30, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #ffffff;
  font-size: 20px;
  font-weight: 300;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  transition: all 0.2s ease;
  z-index: 1;
}

.preview-close:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  transform: scale(1.05);
}

.preview-close:active {
  transform: scale(0.95);
}

/* Preview fade animation */
.preview-fade-enter-active {
  animation: previewFadeIn 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.preview-fade-leave-active {
  animation: previewFadeIn 0.2s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

.preview-fade-enter-active .image-preview-container {
  animation: previewZoomIn 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.preview-fade-leave-active .image-preview-container {
  animation: previewZoomIn 0.25s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

@keyframes previewFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes previewZoomIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* Purchase Bottom Sheet Styles */
.purchase-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  z-index: 9999;
  display: flex;
  align-items: flex-end;
}

.purchase-sheet {
  background: linear-gradient(
    to bottom,
    rgba(18, 22, 30, 0.98),
    rgba(18, 22, 30, 0.95)
  );
  border-radius: 24px 24px 0 0;
  padding: 1.5rem;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-bottom: none;
  box-shadow: 0 -8px 32px rgba(0, 0, 0, 0.4);
}

.sheet-handle {
  width: 40px;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  margin: 0 auto 1.5rem auto;
}

.product-preview {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.preview-image {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  object-fit: cover;
  border: 1px solid rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
}

.preview-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
  min-width: 0;
}

.preview-info h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.preview-vendor {
  font-size: 0.9rem;
  color: #9aa3ad;
  margin: 0;
}

.preview-price {
  font-size: 1.2rem;
  font-weight: 700;
  color: #60a5fa;
  margin: 0;
}

.quantity-section {
  margin-bottom: 1.5rem;
}

.quantity-section label {
  display: block;
  font-size: 0.9rem;
  font-weight: 600;
  color: #cfcfcf;
  margin-bottom: 0.75rem;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 0.75rem 1rem;
}

.qty-btn {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  font-size: 1.25rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.qty-btn:active:not(:disabled) {
  transform: scale(0.95);
  background: rgba(255, 255, 255, 0.12);
}

.qty-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.qty-display {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.qty-number {
  font-size: 1.5rem;
  font-weight: 700;
  color: #ffffff;
  line-height: 1;
}

.qty-max {
  font-size: 0.75rem;
  color: #9aa3ad;
  line-height: 1;
}

.purchase-summary {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  padding: 1rem;
  margin-bottom: 1.5rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1rem;
  color: #cfcfcf;
}

.summary-value {
  font-size: 1.25rem;
  font-weight: 700;
  color: #ffffff;
}

.checkout-btn {
  width: 100%;
  height: 52px;
  border: 0;
  border-radius: 12px;
  cursor: pointer;
  color: #fff;
  font-weight: 700;
  font-size: 1.05rem;
  letter-spacing: 0.01em;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  box-shadow: 
    0 10px 30px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
  transition: all 0.2s ease;
  margin-bottom: 0.75rem;
}

.checkout-btn:active:not(:disabled) {
  transform: translateY(1px);
  box-shadow: 
    0 6px 20px rgba(37, 99, 235, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
}

.checkout-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.cancel-btn {
  width: 100%;
  height: 48px;
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: transparent;
  color: #9aa3ad;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-btn:active {
  background: rgba(255, 255, 255, 0.04);
  transform: scale(0.98);
}

/* Edit Product Modal - Fullscreen */
.edit-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.9);
  z-index: 999;
  display: flex;
  justify-content: center;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  padding: 0 1rem 2rem;
}

.edit-modal-container {
  min-height: 100vh;
  background: linear-gradient(
    to bottom,
    rgba(13, 27, 42, 0.95),
    rgba(9, 18, 30, 0.92)
  );
  display: flex;
  flex-direction: column;
  width: 100%;
}

.edit-modal-header {
  position: sticky;
  top: 0;
  background: linear-gradient(
    to bottom,
    rgba(13, 27, 42, 0.98),
    rgba(13, 27, 42, 0.95)
  );
  backdrop-filter: blur(16px) saturate(180%);
  -webkit-backdrop-filter: blur(16px) saturate(180%);
  border-bottom: 1px solid rgba(59, 130, 246, 0.15);
  padding: 1.25rem 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 10;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
}

.edit-modal-header h2 {
  font-size: 1.4rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  text-shadow: 0 0 8px rgba(59, 130, 246, 0.3);
}

.edit-close-btn {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.2);
  background: rgba(59, 130, 246, 0.08);
  color: #60a5fa;
  font-size: 0.9rem;
  font-weight: 600;
  padding: 8px 14px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.edit-close-btn:hover {
  background: rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.35);
  box-shadow: 0 0 12px rgba(59, 130, 246, 0.2);
}

.edit-close-btn:active {
  transform: scale(0.98);
}

.edit-modal-content {
  flex: 1;
  padding: 1.5rem;
  max-width: 600px;
  width: 100%;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.edit-form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.edit-form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.edit-form-field label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #cfcfcf;
  letter-spacing: 0.01em;
}

.edit-form-field input,
.edit-form-field textarea,
.edit-form-field select {
  appearance: none;
  outline: none;
  border: 1px solid rgba(59, 130, 246, 0.2);
  background: rgba(13, 27, 42, 0.4);
  color: #fff;
  padding: 12px 14px;
  border-radius: 12px;
  font-size: 15px;
  font-family: inherit;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;
}

.edit-form-field textarea {
  resize: vertical;
  min-height: 100px;
}

.edit-form-field input::placeholder,
.edit-form-field textarea::placeholder {
  color: #7f7f7f;
}

.edit-form-field input:hover,
.edit-form-field textarea:hover,
.edit-form-field select:hover {
  border-color: rgba(59, 130, 246, 0.35);
  background: rgba(13, 27, 42, 0.6);
}

.edit-form-field input:focus,
.edit-form-field textarea:focus,
.edit-form-field select:focus {
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow:
    0 0 0 6px rgba(59, 130, 246, 0.1),
    0 8px 24px rgba(0, 0, 0, 0.25);
  background: rgba(13, 27, 42, 0.7);
}

.edit-save-btn {
  width: 100%;
  height: 48px;
  border: 0;
  border-radius: 12px;
  cursor: pointer;
  color: #fff;
  font-weight: 700;
  font-size: 1rem;
  letter-spacing: 0.01em;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  box-shadow:
    0 10px 30px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
  transition: all 0.2s ease;
  margin-top: 0.5rem;
}

.edit-save-btn:hover {
  box-shadow:
    0 12px 32px rgba(37, 99, 235, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.16);
  transform: translateY(-1px);
}

.edit-save-btn:active {
  transform: translateY(0);
  box-shadow:
    0 8px 24px rgba(37, 99, 235, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
}

/* Fade transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .card-image-wrapper {
    height: 200px;
  }
  
  .action-icon {
    width: 32px;
    height: 32px;
  }
  
  .action-icon ion-icon {
    font-size: 16px;
  }
  
  .price-badge {
    padding: 6px 12px;
  }
  
  .price-amount {
    font-size: 1.2rem;
  }

  .image-preview-overlay {
    padding: 1rem;
  }

  .preview-close {
    top: -12px;
    right: -12px;
    width: 36px;
    height: 36px;
    font-size: 18px;
  }

  .purchase-sheet {
    padding: 1.25rem;
  }
  
  .preview-image {
    width: 70px;
    height: 70px;
  }

  .edit-form-row {
    grid-template-columns: 1fr;
  }

  .edit-modal-header {
    padding: 1rem 1.25rem;
  }

  .edit-modal-header h2 {
    font-size: 1.2rem;
  }

  .edit-modal-content {
    padding: 1.25rem;
  }

  .edit-form-field input,
  .edit-form-field textarea,
  .edit-form-field select {
    font-size: 16px;
  }

  .edit-modal-container {
    padding-top: calc(env(safe-area-inset-top) + 60px);
  }

  .edit-modal-header {
    top: calc(env(safe-area-inset-top) + 60px);
    padding: 1rem 1.25rem;
  }
}
</style>
