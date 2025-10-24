<script setup lang="ts">
import { IonAvatar, IonCard, IonCol, IonIcon } from '@ionic/vue';
import { Product } from '@/entity/Product';
import { PropType, ref } from 'vue';
import UsernameTag from '@/component/user/UsernameTag.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import { callDeleteProduct } from '@/client/productClient';
import Avatar from '@/component/user/AvatarItem.vue';
import { createCheckoutSession } from '@/client/paymentClient';
import { showToast } from '@/utils';
import { pencil } from 'ionicons/icons';
import { useRouter } from 'vue-router';
import customShareIcon from '@/assets/icons/share.svg';
import customTrashIcon from '@/assets/icons/trash.svg';
import DateDisplay from '@/component/date/DateDisplay.vue';
import ExportCard from '@/component/actions/ExportCard.vue';
import DeleteCard from '@/component/actions/DeleteCard.vue';

const props = defineProps({
  product: {
    required: true,
    type: Object as PropType<Product>,
  },
});

const emit = defineEmits(['product-deleted']);
const authStore = useAuthStore();
const router = useRouter();

const showPurchaseSheet = ref(false);
const showImagePreview = ref(false);
const modalImageSrc = ref('');

const localProduct = ref<Product>({ ...props.product });

const quantity = ref(1);
const maxQuantity = ref(0);
const exportCardRef = ref<InstanceType<typeof ExportCard> | null>(null);
const deleteCardRef = ref<InstanceType<typeof DeleteCard> | null>(null);

const openExportModal = () => {
  exportCardRef.value?.open();
};

const openDeleteConfirm = () => {
  deleteCardRef.value?.open();
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

const openEditModal = () => {
  router.push(`/marketplace/product/${localProduct.value.slug}/edit`);
};

const handleProductDeleted = (slug: string) => {
  emit('product-deleted', slug);
};
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4" class="product-col">
    <ion-card class="product-card">
      <div class="card-image-wrapper" @click="openImagePreview">
        <img :src="localProduct.imageUrl" :alt="`Product ${localProduct.name}`" />

        <div class="card-actions" @click.stop>
          <button class="action-icon export" @click="openExportModal" title="Export">
            <ion-icon :src="customShareIcon" />
          </button>

          <template v-if="localProduct.vendor.slug === authStore.getAuthenticatedUserSlug">
            <button class="action-icon edit" @click="openEditModal" title="Edit">
              <ion-icon :src="pencil" />
            </button>

            <button class="action-icon delete" @click="openDeleteConfirm" title="Delete">
              <ion-icon :src="customTrashIcon" />
            </button>
          </template>
        </div>

        <div class="price-badge">
          <span class="price-amount">{{ localProduct.price?.amount.toFixed(2) }}</span>
          <span class="price-currency">{{ localProduct.price?.symbol }}</span>
        </div>
      </div>

      <div class="card-content">
        <div class="product-header">
          <div class="header-top">
            <h3 class="product-name">{{ localProduct.name }}</h3>
            <DateDisplay :date="localProduct.createdAt" class="product-date" />
          </div>
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

        <button class="buy-button" @click="openImageModal(localProduct.imageUrl, localProduct.quantity)">
          Buy Now
        </button>
      </div>
    </ion-card>

    <transition name="preview-fade">
      <div v-if="showImagePreview" class="image-preview-overlay" @click="closeImagePreview">
        <div class="image-preview-container" @click.stop>
          <button class="preview-close" @click="closeImagePreview">✕</button>
          <img :src="localProduct.imageUrl" :alt="localProduct.name" />
        </div>
      </div>
    </transition>

    <Teleport to="body">
      <transition name="sheet-slide">
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
                <button class="qty-btn" @click="decreaseQuantity" :disabled="quantity <= 1">−</button>
                <div class="qty-display">
                  <span class="qty-number">{{ quantity }}</span>
                  <span class="qty-max">/ {{ maxQuantity }} in stock</span>
                </div>
                <button class="qty-btn" @click="increaseQuantity" :disabled="quantity >= maxQuantity">+</button>
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

            <button class="cancel-btn" @click="closePurchaseSheet">Cancel</button>
          </div>
        </div>
      </transition>
    </Teleport>

    <ExportCard :item="localProduct" type="product" ref="exportCardRef" />

    <DeleteCard
      item-type="product"
      :item-slug="localProduct.slug"
      :delete-function="callDeleteProduct"
      @deleted="handleProductDeleted"
      ref="deleteCardRef" />
  </ion-col>
</template>

<style scoped>
.product-col {
  padding: 0 1rem;
}

.product-card {
  margin: 0 0 16px 0;
  border-radius: 16px;
  overflow: hidden;
  background: linear-gradient(to bottom, rgba(22, 28, 38, 0.92), rgba(18, 22, 30, 0.88));
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.04);
  transition: all 0.2s ease;
  width: 100%;
  height: auto;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-2px);
  box-shadow:
    0 12px 32px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.16);
}

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
  color: #f87171;
}

.action-icon.delete ion-icon {
  color: #f87171;
}

.action-icon:active {
  transform: scale(0.95);
}

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

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.product-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  min-width: 0;
}

.product-date {
  font-size: 0.75rem;
  color: #9aa3ad;
  font-weight: 500;
  white-space: nowrap;
  flex-shrink: 0;
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
  background: linear-gradient(to bottom, rgba(18, 22, 30, 0.98), rgba(18, 22, 30, 0.95));
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.sheet-slide-enter-active {
  transition: opacity 0.3s ease;
}

.sheet-slide-leave-active {
  transition: opacity 0.25s ease;
}

.sheet-slide-enter-from,
.sheet-slide-leave-to {
  opacity: 0;
}

.sheet-slide-enter-active .purchase-sheet {
  animation: slideUp 0.35s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.sheet-slide-leave-active .purchase-sheet {
  animation: slideDown 0.3s cubic-bezier(0.8, 0.2, 0.8, 0.2);
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

@keyframes slideDown {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(100%);
  }
}

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
