<script setup lang="ts">
import { ref as vueRef, computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { QuoteArticleDTO, QuoteDTO } from '@/entity/Quote';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import {
  addCircleOutline,
  arrowBackOutline,
  createOutline,
  documentTextOutline,
  downloadOutline,
  trashOutline,
  cubeOutline,
} from 'ionicons/icons';
import { IonContent, IonIcon } from '@ionic/vue';
import DeleteCard from '@/component/actions/DeleteCard.vue';
import { showToast } from '@/utils';
import { useAuthStore } from '@/store/reactiveStorage';
import {
  deleteQuote as deleteQuoteApi,
  deleteQuoteArticle,
  exportQuotePdf as downloadQuotePdf,
  getQuote,
} from '@/client/quoteClient';
import Header from '@/component/AppHeader.vue';

const deleteCardRef = vueRef();
const deleteItemCardRefs = vueRef<Record<string, InstanceType<typeof DeleteCard>>>({});

const showDeleteCard = ref(false);
const showDeleteItemSlug = ref<string | null>(null);

const route = useRoute();
const router = useRouter();

const quote = ref<QuoteDTO | null>(null);

const authStore = useAuthStore();
const isOwner = computed(() => {
  return quote.value?.artist.slug === authStore.getAuthenticatedUserSlug;
});

const loadQuote = async (slug: string) => {
  try {
    const data = await getQuote(slug);
    quote.value = data;

    if (!quote.value.quoteArticles) quote.value.quoteArticles = [];
  } catch (e) {
    await showToast('Failed to load quote', 'danger');
  }
};

const openEditModal = () => {
  (document.activeElement as HTMLElement)?.blur();
  router.push(`/quotes/${quote.value?.slug}/edit`);
};

const goBackToQuotes = () => {
  (document.activeElement as HTMLElement)?.blur();
  router.push(`/quotes/me?t=${Date.now()}`);
};

const handleDeleteQuote = async () => {
  if (!quote.value) return;
  try {
    await deleteQuoteApi(quote.value.slug);
    await router.push(`/quotes/me?t=${Date.now()}`);
  } catch {
    await showToast('Failed to delete quote', 'danger');
  }
};

const openArticleModal = (article?: QuoteArticleDTO) => {
  if (article) {
    router.push(`/quotes/${quote.value?.slug}/items/${article.slug}/edit`);
  } else {
    router.push(`/quotes/${quote.value?.slug}/items/new`);
  }
};

const handleDeleteArticle = async (articleSlug: string) => {
  if (!quote.value) return;
  try {
    await deleteQuoteArticle(articleSlug);
    const index = quote.value.quoteArticles.findIndex((a) => a.slug === articleSlug);
    if (index !== -1) quote.value.quoteArticles.splice(index, 1);
  } catch {
    await showToast('Failed to delete article', 'danger');
  }
};

const downloadPdf = async () => {
  if (!quote.value) return;
  try {
    const blob = await downloadQuotePdf(quote.value.slug);
    const url = window.URL.createObjectURL(new Blob([blob]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `quote-${quote.value.slug}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  } catch {
    await showToast('Failed to download PDF', 'danger');
  }
};

watch(
  () => [route.params.slug, route.query.t],
  ([newSlug]) => {
    if (
      typeof newSlug === 'string' &&
      route.path.startsWith('/quotes/') &&
      !route.path.includes('/edit') &&
      !route.path.includes('/items/')
    ) {
      loadQuote(newSlug);
    }
  },
  { immediate: true }
);

const calculateSubtotal = () => {
  if (!quote.value?.quoteArticles) return 0;
  return quote.value.quoteArticles
    .reduce((sum, article) => sum + article.quantity * article.unitPrice.amount, 0)
    .toFixed(2);
};
</script>

<template>
  <ApplicationViewport>
    <IonContent class="quote-details-content">
      <!-- Ambient Glow Background -->
      <div class="ambient-glow">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
      </div>

      <div class="quote-details-wrapper">
        <!-- Header -->
        <Header
          v-if="quote"
          :title="quote.name"
          subtitle="Quote Details"
          :icon="arrowBackOutline"
          :on-icon-click="goBackToQuotes">
          <template #actions>
            <div class="header-actions" v-if="isOwner">
              <button class="action-btn edit-btn" @click="openEditModal">
                <IonIcon :icon="createOutline" />
                <span class="action-btn-text">Edit</span>
              </button>
              <button class="action-btn download-btn" @click="downloadPdf">
                <IonIcon :icon="downloadOutline" />
              </button>
              <button class="action-btn delete-btn" @click="deleteCardRef?.open()">
                <IonIcon :icon="trashOutline" />
              </button>
              <DeleteCard
                ref="deleteCardRef"
                :itemType="'quote'"
                :itemSlug="quote.slug"
                :deleteFunction="handleDeleteQuote"
                @deleted="showDeleteCard = false" />
            </div>
          </template>
        </Header>

        <!-- Customer Info Card -->
        <div class="info-card" v-if="quote">
          <div class="info-header">
            <div class="info-icon-wrapper">
              <IonIcon :icon="documentTextOutline" class="info-icon" />
            </div>
            <h2 class="info-title">Customer Information</h2>
          </div>

          <div class="info-grid">
            <div class="info-item" v-if="quote.clientEmail">
              <span class="info-label">Email</span>
              <span class="info-value">{{ quote.clientEmail }}</span>
            </div>
            <div class="info-item" v-if="quote.clientPhone">
              <span class="info-label">Phone</span>
              <span class="info-value">{{ quote.clientPhone }}</span>
            </div>
            <div class="info-item full-width" v-if="quote.clientAddress">
              <span class="info-label">Address</span>
              <span class="info-value">{{ quote.clientAddress }}</span>
            </div>
            <div class="info-item full-width" v-if="quote.description">
              <span class="info-label">Notes</span>
              <span class="info-value">{{ quote.description }}</span>
            </div>
          </div>
        </div>

        <!-- Items Section -->
        <div class="articles-section" v-if="quote">
          <div class="articles-header">
            <div class="articles-title-with-icon">
              <IonIcon :icon="cubeOutline" class="articles-title-icon" />
              <h2 class="articles-title">Items</h2>
            </div>
            <button class="add-article-btn" @click="openArticleModal()" v-if="isOwner">
              <IonIcon :icon="addCircleOutline" />
              <span>Add Item</span>
            </button>
          </div>

          <!-- Items List -->
          <div v-if="quote.quoteArticles?.length" class="articles-list">
            <div v-for="article in quote.quoteArticles" :key="article.slug" class="article-card">
              <div class="article-content">
                <div class="article-header">
                  <h3 class="article-description">{{ article.description }}</h3>
                  <div class="article-actions" v-if="isOwner">
                    <button class="article-action-btn edit" @click="openArticleModal(article)">
                      <IonIcon :icon="createOutline" />
                    </button>
                    <button class="article-action-btn delete" @click="deleteItemCardRefs[article.slug]?.open()">
                      <IonIcon :icon="trashOutline" />
                    </button>
                    <DeleteCard
                      :ref="
                        (el) => {
                          if (el) deleteItemCardRefs[article.slug] = el as InstanceType<typeof DeleteCard>;
                        }
                      "
                      :itemType="'item'"
                      :itemSlug="article.slug"
                      :deleteFunction="handleDeleteArticle"
                      @deleted="showDeleteItemSlug = null" />
                  </div>
                </div>

                <div class="article-info-row">
                  <div class="article-details">
                    <span class="article-detail-item">
                      <span class="detail-label">Qty:</span>
                      <span class="detail-value">{{ article.quantity }}</span>
                    </span>
                    <span class="article-divider">•</span>
                    <span class="article-detail-item">
                      <span class="detail-label">Unit Price:</span>
                      <span class="detail-value">{{ article.unitPrice.amount }} {{ article.unitPrice.symbol }}</span>
                    </span>
                    <span class="article-divider">•</span>
                    <span class="article-detail-item">
                      <span class="detail-label">VAT:</span>
                      <span class="detail-value">{{ article.vatPercent }}%</span>
                    </span>
                  </div>

                  <div class="article-total-row">
                    <span class="total-label">Total</span>
                    <span class="total-value-group">
                      <span class="total-value">{{ (article.quantity * article.unitPrice.amount).toFixed(2) }}</span>
                      <span class="total-currency">{{ article.unitPrice.symbol }}</span>
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Total Summary -->
            <div class="total-summary">
              <div class="summary-row subtotal">
                <span class="summary-label">Subtotal <span class="tax-note">(before tax)</span></span>
                <span class="summary-value">{{ calculateSubtotal() }} {{ quote.total.symbol }}</span>
              </div>
              <div class="summary-row grand-total">
                <span class="summary-label">Total</span>
                <span class="summary-value">{{ quote.total.amount }} {{ quote.total.symbol }}</span>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="empty-articles">
            <div class="empty-icon-wrapper">
              <IonIcon :icon="documentTextOutline" class="empty-icon" />
            </div>
            <h3 class="empty-title">No Items Yet</h3>
            <p class="empty-description">Add items to build your quote</p>
          </div>
        </div>
      </div>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
/* Ambient Background */
.ambient-glow {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 1;
  animation: glowFloat 10s ease-in-out infinite;
}

.glow-orb.orb-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.25) 0%, rgba(59, 130, 246, 0.08) 50%, transparent 100%);
  top: -150px;
  left: -150px;
}

.glow-orb.orb-2 {
  width: 450px;
  height: 450px;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.2) 0%, rgba(96, 165, 250, 0.06) 50%, transparent 100%);
  bottom: -100px;
  right: -100px;
}

.glow-orb.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.18) 0%, rgba(37, 99, 235, 0.05) 50%, transparent 100%);
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
}

@keyframes glowFloat {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

/* Content */
.quote-details-wrapper {
  position: relative;
  z-index: 1;
}

/* Header Actions */
.header-actions {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  flex-shrink: 0;
}

.delete-btn {
  color: rgba(239, 68, 68, 1);
}

.download-btn:hover,
.delete-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: scale(1.05);
}

.download-btn:active,
.delete-btn:active {
  transform: scale(0.95);
}

/* Info Card */
.info-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 1rem;
  padding: 1.25rem;
  margin: 1.2rem 1rem 1.2rem 1rem;
}

.info-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.25rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.info-icon-wrapper {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(37, 99, 235, 0.1));
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.info-icon {
  font-size: 1.5rem;
  color: rgba(59, 130, 246, 1);
}

.info-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: white;
  margin: 0;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.4;
  word-break: break-word;
}

/* Items Section */
.articles-section {
  margin: 0 1rem 1.5rem 1rem;
}

.articles-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.articles-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: white;
  margin: 0;
}

.articles-title-with-icon {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}

.articles-title-icon {
  font-size: 1.3rem;
  color: rgba(59, 130, 246, 1);
  flex-shrink: 0;
}

.add-article-btn {
  appearance: none;
  border: none;
  height: 36px;
  padding: 0 1rem;
  border-radius: 10px;
  background: linear-gradient(135deg, #253a60e0 30%, #1e40af90 100%);
  box-shadow: 0 4px 16px rgba(30, 41, 59, 0.18);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #fff;
  font-size: 0.85rem;
  font-weight: 600;
}

.add-article-btn ion-icon {
  font-size: 20px;
}

.add-article-btn:hover {
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.25);
  transform: translateY(-2px);
}

.add-article-btn:active {
  transform: translateY(0);
}

/* Items List */
.articles-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.article-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 1rem;
  padding: 1.25rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.article-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.15);
}

.article-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.article-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

.article-description {
  font-size: 1.05rem;
  font-weight: 600;
  color: white;
  margin: 0;
  line-height: 1.4;
  flex: 1;
  min-width: 0;
}

.article-actions {
  display: flex;
  gap: 0.5rem;
  flex-shrink: 0;
}

.article-action-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: white;
}

.article-action-btn ion-icon {
  font-size: 16px;
}

.article-action-btn.edit:hover {
  background: rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.3);
  color: rgba(59, 130, 246, 1);
}

.article-action-btn.delete {
  color: rgba(239, 68, 68, 1);
}

.article-action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.15);
  border-color: rgba(239, 68, 68, 0.3);
}

.article-info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 1rem;
  padding-top: 0.75rem;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.article-details {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
  font-size: 0.85rem;
  flex: 1;
  min-width: 0;
}

.article-detail-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.detail-label {
  color: rgba(255, 255, 255, 0.5);
}

.detail-value {
  color: rgba(255, 255, 255, 0.8);
  font-weight: 600;
}

.article-divider {
  color: rgba(255, 255, 255, 0.3);
}

.article-total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.total-label {
  font-size: 0.7rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.total-value-group {
  display: flex;
  align-items: baseline;
  gap: 0.375rem;
}

.total-value {
  font-size: 1.25rem;
  font-weight: 700;
  color: rgba(59, 130, 246, 1);
  font-variant-numeric: tabular-nums;
  line-height: 1;
}

.total-currency {
  font-size: 1.25rem;
  font-weight: 700;
  color: rgba(59, 130, 246, 1);
  text-transform: uppercase;
  line-height: 1;
}

/* Total Summary */
.total-summary {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 1rem;
  padding: 1.25rem;
  margin-top: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-row.subtotal {
  padding-bottom: 0.75rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.summary-row.grand-total {
  padding-top: 0.25rem;
}

.summary-label {
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.tax-note {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 400;
  font-style: italic;
}

.summary-row.grand-total .summary-label {
  font-size: 1.1rem;
  font-weight: 700;
  color: white;
}

.summary-value {
  font-size: 1.1rem;
  font-weight: 700;
  color: rgba(59, 130, 246, 1);
  font-variant-numeric: tabular-nums;
}

.summary-row.grand-total .summary-value {
  font-size: 1.5rem;
}

/* Empty State */
.empty-articles {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 1rem;
  padding: 3rem 2rem;
  text-align: center;
}

.empty-icon-wrapper {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(37, 99, 235, 0.1));
  border-radius: 1rem;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.empty-icon {
  font-size: 2.5rem;
  color: rgba(59, 130, 246, 0.8);
}

.empty-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: white;
  margin: 0 0 0.5rem 0;
}

.empty-description {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

/* Responsive */
@media (max-width: 640px) {
  .action-btn-text {
    display: none;
  }

  .edit-btn {
    width: 38px;
    padding: 0;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .article-info-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }

  .article-total {
    width: 100%;
    justify-content: flex-start;
  }

  .total-value {
    font-size: 1.4rem;
  }

  .total-currency {
    font-size: 1.4rem;
  }
}
</style>
