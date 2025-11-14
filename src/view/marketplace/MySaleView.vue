<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getMySales } from '@/client/saleClient';
import { Sale } from '@/entity/Sale';
import { IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonContent } from '@ionic/vue';
import { closeOutline, personOutline, pricetagOutline, pricetagsOutline, cardOutline } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import Header from '@/component/AppHeader.vue';
import { showToast } from '@/utils';

const router = useRouter();

const sales = ref<Sale[]>([]);
const page = ref(0);
const size = 10;
const hasMore = ref(true);

const fetchSales = async (pageNumber: number) => {
  try {
    const newSales = await getMySales(pageNumber, size, 'quantitySold');

    newSales.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

    if (newSales.length < size) {
      hasMore.value = false;
    }
    sales.value.push(...newSales);
  } catch (error: unknown) {
    const message = 'Failed to load your sales. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
    hasMore.value = false;
  }
};

const loadMore = async (event: CustomEvent<void>) => {
  page.value++;
  await fetchSales(page.value);
  await (event.target as HTMLIonInfiniteScrollElement).complete();

  if (!hasMore.value) {
    (event.target as HTMLIonInfiniteScrollElement).disabled = true;
  }
};

const goBack = () => {
  router.back();
};

const showImagePreview = ref(false);
const previewImageUrl = ref('');

const openImagePreview = (imageUrl: string) => {
  previewImageUrl.value = imageUrl;
  showImagePreview.value = true;
};

const closeImagePreview = () => {
  showImagePreview.value = false;
  previewImageUrl.value = '';
};

onMounted(async () => {
  sales.value = [];
  page.value = 0;
  hasMore.value = true;
  await fetchSales(page.value);
});
</script>

<template>
  <ApplicationViewport title="My Sales">
    <ion-content class="sales-content-wrapper">
      <Header title="My Sales" subtitle="Track your product sales" :icon="pricetagsOutline">
        <template #actions>
          <button class="back-btn" @click="goBack">
            <ion-icon :icon="closeOutline" />
          </button>
        </template>
      </Header>
      <div class="sales-content">
        <div v-if="sales.length === 0 && !hasMore" class="empty-state">
          <div class="empty-icon">
            <ion-icon :icon="pricetagsOutline" />
          </div>
          <p class="empty-text">No sales yet</p>
          <p class="empty-subtext">Your sales will appear here once customers start purchasing</p>
        </div>

        <div v-else class="sales-list">
          <div v-for="sale in sales" :key="sale.slug" class="sale-card">
            <div class="card-top-row">
              <div class="sale-image-wrapper" @click="sale.product.imageUrl && openImagePreview(sale.product.imageUrl)">
                <img
                  v-if="sale.product.imageUrl"
                  :src="sale.product.imageUrl"
                  :alt="sale.product.name"
                  class="sale-image" />
                <div v-else class="sale-image-placeholder">
                  <ion-icon :icon="closeOutline" />
                </div>
              </div>

              <div class="card-main-info">
                <h3 class="product-name">{{ sale.product.name }}</h3>
              </div>

              <div class="card-right-col">
                <span class="sale-date">
                  {{ new Date(sale.createdAt).toLocaleDateString() }}
                </span>
                <div class="sale-badge">
                  <span class="badge-qty">{{ sale.quantitySold }}</span>
                  <span class="badge-label">sold</span>
                </div>
              </div>
            </div>

            <div class="card-middle-row">
              <div class="buyer-info-compact">
                <div class="buyer-row">
                  <ion-icon :icon="personOutline" class="info-icon"></ion-icon>
                  <span class="buyer-name">{{ sale.buyer.username }}</span>
                </div>
              </div>
            </div>

            <div class="card-bottom-row">
              <div class="price-item">
                <ion-icon :icon="pricetagOutline" class="price-icon"></ion-icon>
                <span class="price-label">Unit:</span>
                <span class="price-value">
                  {{
                    sale.product.price !== null
                      ? sale.product.price.symbol + sale.product.price.amount.toFixed(2)
                      : 'N/A'
                  }}
                </span>
              </div>

              <div class="price-divider"></div>

              <div class="price-item total">
                <ion-icon :icon="cardOutline" class="price-icon"></ion-icon>
                <span class="price-label">Total:</span>
                <span class="price-value highlight">
                  {{ sale.product.price !== null ? sale.product.price.symbol + sale.amount.amount.toFixed(2) : 'N/A' }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <ion-infinite-scroll @ionInfinite="loadMore" threshold="100px" :disabled="!hasMore">
          <ion-infinite-scroll-content
            loading-spinner="crescent"
            loading-text="Loading more sales..."></ion-infinite-scroll-content>
        </ion-infinite-scroll>
      </div>
    </ion-content>

    <transition name="preview-fade">
      <div v-if="showImagePreview" class="image-preview-overlay" @click="closeImagePreview">
        <div class="image-preview-container" @click.stop>
          <button class="preview-close" @click="closeImagePreview">✕</button>
          <img :src="previewImageUrl" alt="Sale product" />
        </div>
      </div>
    </transition>
  </ApplicationViewport>
</template>

<style scoped>
.sales-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
  background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
  overflow-y: auto;
  max-height: calc(100vh - 64px);
}

/* Form header styles removed - now using Header component */

.back-btn {
  appearance: none;
  border: 1px solid rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  font-size: 0.9rem;
  font-weight: 600;
  padding: 8px 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
}

.back-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  box-shadow: 0 0 12px rgba(239, 68, 68, 0.2);
}

.back-btn:active {
  transform: scale(0.95);
}

.back-btn ion-icon {
  font-size: 22px;
}

.sales-content {
  padding: 1.5rem;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 3rem;
}

.empty-state {
  text-align: center;
  padding: 4rem 1.5rem;
  background: linear-gradient(to bottom, rgba(13, 27, 42, 0.6), rgba(9, 18, 30, 0.5));
  border-radius: 16px;
  margin-top: 2rem;
}

.empty-icon {
  width: 100px;
  height: 100px;
  margin: 0 auto 1.5rem;
  background: rgba(59, 130, 246, 0.15);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px dashed rgba(59, 130, 246, 0.3);
}

.empty-icon ion-icon {
  font-size: 50px;
  color: #60a5fa;
}

.empty-text {
  margin: 0 0 0.5rem 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #ffffff;
}

.empty-subtext {
  margin: 0;
  font-size: 1rem;
  color: #9aa3ad;
  max-width: 400px;
  margin: 0 auto;
}

.sales-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 3rem;
}

.sale-card {
  background: linear-gradient(145deg, rgba(30, 35, 45, 0.85), rgba(22, 26, 35, 0.9));
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.25s ease;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  padding: 0.875rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.sale-card:hover {
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.12);
  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.4),
    0 0 0 1px rgba(96, 165, 250, 0.15);
}

.card-top-row {
  display: flex;
  align-items: flex-start;
  gap: 0.875rem;
}

.sale-image-wrapper {
  position: relative;
  width: 60px;
  height: 60px;
  flex-shrink: 0;
  background: rgba(0, 0, 0, 0.3);
  overflow: hidden;
  cursor: zoom-in;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.sale-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.sale-card:hover .sale-image {
  transform: scale(1.08);
}

.sale-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(59, 130, 246, 0.1);
}

.sale-image-placeholder ion-icon {
  font-size: 28px;
  color: #60a5fa;
  opacity: 0.5;
}

.card-main-info {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
}

.product-name {
  font-size: 1rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  line-height: 1.3;
}

.card-right-col {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.5rem;
  flex-shrink: 0;
}

.sale-badge {
  background: rgba(18, 22, 30, 0.95);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(59, 130, 246, 0.4);
  border-radius: 8px;
  padding: 4px 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  flex-shrink: 0;
}

.badge-qty {
  font-size: 1rem;
  font-weight: 700;
  color: #60a5fa;
  line-height: 1;
}

.badge-label {
  font-size: 0.65rem;
  font-weight: 600;
  color: #9aa3ad;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  line-height: 1;
}

.sale-date {
  font-size: 0.7rem;
  font-weight: 600;
  color: #9aa3ad;
  white-space: nowrap;
  flex-shrink: 0;
  display: flex;
  align-items: center;
}

.card-middle-row {
  padding: 0.625rem;
  background: rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 8px;
}

.buyer-info-compact {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.buyer-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.info-icon {
  font-size: 0.95rem;
  color: #60a5fa;
  flex-shrink: 0;
}

.buyer-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #ffffff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  min-width: 0;
}

.buyer-email {
  font-size: 0.8rem;
  color: #9aa3ad;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  min-width: 0;
}

.card-bottom-row {
  display: flex;
  gap: 0.875rem;
  align-items: center;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 8px;
  padding: 0.625rem;
}

.price-item {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  flex: 1;
}

.price-icon {
  font-size: 1rem;
  color: #60a5fa;
  flex-shrink: 0;
}

.price-label {
  font-size: 0.8rem;
  font-weight: 600;
  color: #9aa3ad;
  flex-shrink: 0;
}

.price-value {
  font-size: 0.95rem;
  font-weight: 700;
  color: #cfcfcf;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-value.highlight {
  font-size: 1.05rem;
  color: #60a5fa;
}

.price-divider {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
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

@media (max-width: 768px) {
  .form-header {
    padding: 1rem 1.25rem;
  }

  .form-header h2 {
    font-size: 1.2rem;
  }

  .sales-content {
    padding: 1rem;
    padding-bottom: 3rem;
  }

  .sales-list {
    gap: 0.625rem;
  }

  .sale-card {
    padding: 0.75rem;
    gap: 0.625rem;
  }

  .card-top-row {
    gap: 0.75rem;
  }

  .product-name {
    font-size: 0.95rem;
  }

  .buyer-row {
    gap: 0.4rem;
  }

  .buyer-name {
    font-size: 0.85rem;
  }

  .buyer-email {
    font-size: 0.75rem;
  }

  .card-bottom-row {
    padding: 0.5rem;
    gap: 0.75rem;
  }

  .price-label {
    font-size: 0.75rem;
  }

  .price-value {
    font-size: 0.9rem;
  }

  .price-value.highlight {
    font-size: 1rem;
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
}
</style>
