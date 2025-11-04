<script setup lang="ts">
import { ref, watchEffect, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { searchQuotes } from '@/client/quoteClient';
import type { QuoteDTO, QuoteSearchParams } from '@/entity/Quote';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import { IonContent, IonIcon } from '@ionic/vue';
import { arrowBackOutline, documentTextOutline, personOutline, addCircleOutline } from 'ionicons/icons';
import SearchBarWithFilters from '@/component/search/SearchBarWithFilters.vue';

const router = useRouter();
const route = useRoute();
const quotes = ref<QuoteDTO[]>([]);
const loading = ref(false);
const searchQuery = ref('');
const sortField = ref<'createdAt'>('createdAt');
const sortAsc = ref(false);

const authStore = useAuthStore();

const loadQuotesForUser = async () => {
  const userSlug = authStore.getAuthenticatedUserSlug;
  if (!userSlug) return;

  loading.value = true;
  try {
    const params: QuoteSearchParams = {
      userSlug: userSlug,
      clientName: searchQuery.value,
      page: 0,
      size: 50,
      sort: `${sortField.value},${sortAsc.value ? 'asc' : 'desc'}`,
    };
    const quotePage = await searchQuotes(params);
    quotes.value = quotePage.content;
  } catch (err) {
    quotes.value = [];
  } finally {
    loading.value = false;
  }
};

watchEffect(loadQuotesForUser);

watch(
  () => route.query.t,
  () => {
    loadQuotesForUser();
  }
);

const toggleSort = () => {
  sortAsc.value = !sortAsc.value;
};

const goToQuote = (slug: string) => {
  (document.activeElement as HTMLElement)?.blur();
  router.push(`/quotes/${slug}`);
};

const formatDate = (date: Date | string) => {
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  return dateObj.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  });
};

const quotesCount = computed(() => {
  return `${quotes.value.length} quote${quotes.value.length !== 1 ? 's' : ''}`;
});

const createNewQuote = () => {
  (document.activeElement as HTMLElement)?.blur();
  router.push('/quotes/create');
};

const goBack = () => {
  (document.activeElement as HTMLElement)?.blur();

  // Check if we have valid history to go back to
  const canGoBack = window.history.state?.back;

  if (canGoBack) {
    // Smart back - go to wherever we came from (profile, etc.)
    router.back();
  } else {
    // Default to profile if no valid history
    router.push('/profile/me');
  }
};
</script>

<template>
  <ApplicationViewport title="Quotes">
    <IonContent class="quotes-detail-content">
      <!-- Ambient Glow Background -->
      <div class="ambient-glow">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
      </div>

      <div class="quotes-content">
        <!-- Header with Back Button -->
        <div class="quotes-header">
          <div class="header-content">
            <div class="header-left">
              <button class="back-btn" @click="goBack" tabindex="0">
                <IonIcon :icon="arrowBackOutline" />
              </button>
              <div class="header-text">
                <h1 class="header-title">My Quotes</h1>
                <p class="header-subtitle">{{ quotesCount }}</p>
              </div>
            </div>

            <button class="create-fab" @click="createNewQuote" tabindex="0">
              <IonIcon :icon="addCircleOutline" />
              <span class="create-fab-text">New Quote</span>
            </button>
          </div>
        </div>

        <!-- Search Bar -->
        <SearchBarWithFilters
          v-model="searchQuery"
          placeholder="Search by client name..."
          :debounce="300"
          @filterClick="toggleSort"
          :isFilterActive="sortAsc" />

        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p class="loading-text">Loading quotes...</p>
        </div>

        <!-- Quotes List -->
        <div v-else-if="quotes.length" class="quotes-list">
          <div v-for="quote in quotes" :key="quote.slug" class="quote-card" @click="goToQuote(quote.slug)">
            <!-- Currency Amount - Top Right -->
            <div class="quote-amount">
              <span class="amount-value">{{ quote.total.amount || 0 }}</span>
              <span class="amount-symbol">{{ quote.total.symbol || '$' }}</span>
            </div>

            <!-- Quote Icon & Status Indicator -->
            <div class="quote-icon-wrapper">
              <IonIcon :icon="documentTextOutline" class="quote-icon" />
              <div class="status-indicator"></div>
            </div>

            <!-- Quote Main Info -->
            <div class="quote-main">
              <h3 class="quote-name">{{ quote.name }}</h3>

              <div class="quote-details">
                <div class="detail-item">
                  <IonIcon :icon="personOutline" class="detail-icon" />
                  <span class="detail-text">{{ quote.clientName }}</span>
                </div>
              </div>

              <!-- Quote Footer with Updated Date -->
              <div class="quote-footer">
                <span class="updated-label">Last updated</span>
                <span class="updated-date">{{ formatDate(quote.updatedAt) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else class="empty-state">
          <div class="empty-icon-wrapper">
            <IonIcon :icon="documentTextOutline" class="empty-icon" />
          </div>
          <h2 class="empty-title">No Quotes Found</h2>
          <p class="empty-description">
            {{
              searchQuery
                ? "Try adjusting your search to find what you're looking for."
                : 'Create your first quote to get started!'
            }}
          </p>
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
.quotes-content {
  position: relative;
  z-index: 1;
}

/* Header */
.quotes-header {
  background: rgba(18, 22, 30, 0.85);
  border-bottom: 1px solid rgba(59, 130, 246, 0.15);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.18);
  padding: 1rem 1rem;
  margin-bottom: 0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex: 1;
  min-width: 0;
}

.back-btn {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
  border: none;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 0.75rem;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn ion-icon {
  font-size: 1.5rem;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: scale(1.05);
}

.back-btn:active {
  transform: scale(0.95);
}

.create-fab {
  appearance: none;
  border: none;
  height: 38px;
  padding: 0 1rem 0 0.9rem;
  border-radius: 10px;
  background: linear-gradient(135deg, #253a60e0 30%, #1e40af90 100%);
  box-shadow: 0 4px 16px rgba(30, 41, 59, 0.18);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  flex-shrink: 0;
}

.create-fab ion-icon {
  font-size: 20px;
  padding-bottom: 2px;
  color: #fff;
  flex-shrink: 0;
}

.create-fab-text {
  font-size: 0.85rem;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.create-fab:hover {
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.25);
  background: linear-gradient(135deg, #1e40af 60%, #1e293b 100%);
  transform: translateY(-2px);
}

.create-fab:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(30, 41, 59, 0.12);
}

.header-text {
  flex: 1;
  min-width: 0;
}

.header-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
  margin: 0 0 0.25rem 0;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.header-subtitle {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  line-height: 1;
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  margin: 1.5rem 1rem;
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 1rem;
}

.spinner {
  width: 48px;
  height: 48px;
  border: 3px solid rgba(59, 130, 246, 0.2);
  border-top-color: rgba(59, 130, 246, 0.8);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  margin-top: 1rem;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.6);
}

/* Quotes List */
.quotes-list {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

/* Quote Card */
.quote-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 1rem;
  padding: 1rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  gap: 1rem;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.quote-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(
    90deg,
    rgba(59, 130, 246, 0.8) 0%,
    rgba(96, 165, 250, 0.6) 50%,
    rgba(59, 130, 246, 0.8) 100%
  );
  opacity: 0;
  transition: opacity 0.3s ease;
}

.quote-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.15);
}

.quote-card:hover::before {
  opacity: 1;
}

.quote-card:active {
  transform: translateY(0);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.1);
}

/* Quote Icon */
.quote-icon-wrapper {
  position: relative;
  width: 52px;
  height: 52px;
  flex-shrink: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(37, 99, 235, 0.1));
  border-radius: 0.875rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.quote-icon {
  font-size: 1.75rem;
  color: rgba(59, 130, 246, 1);
}

.status-indicator {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 12px;
  height: 12px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  border: 2px solid rgba(18, 22, 30, 0.9);
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(0.95);
  }
}

/* Quote Main Content */
.quote-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.quote-header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 0.75rem;
}

.quote-name {
  font-size: 1.05rem;
  font-weight: 600;
  color: white;
  margin: 0;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  min-width: 0;
}

.quote-amount {
  position: absolute;
  top: 1rem;
  right: 1rem;
  display: flex;
  align-items: baseline;
  gap: 0.25rem;
  flex-shrink: 0;
  padding: 0.375rem 0.75rem;
  background: rgba(59, 130, 246, 0.12);
  border: 1px solid rgba(59, 130, 246, 0.25);
  border-radius: 0.5rem;
  backdrop-filter: blur(10px);
}

.amount-value {
  font-size: 1rem;
  font-weight: 700;
  color: rgba(59, 130, 246, 1);
  font-variant-numeric: tabular-nums;
}

.amount-symbol {
  font-size: 0.75rem;
  font-weight: 600;
  color: rgba(59, 130, 246, 0.8);
  text-transform: uppercase;
}

.quote-details {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  max-width: calc(100% - 60px);
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  flex-shrink: 0;
}

.detail-item:first-child {
  min-width: 0;
  flex-shrink: 1;
  overflow: hidden;
}

.detail-icon {
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.5);
  flex-shrink: 0;
}

.detail-text {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.7);
  white-space: nowrap;
}

.detail-item:first-child .detail-text {
  overflow: hidden;
  text-overflow: ellipsis;
  min-width: 0;
}

.quote-footer {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.updated-label {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
  flex-shrink: 0;
}

.updated-date {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.7);
  white-space: nowrap;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 1rem;
  margin: 1.5rem 1rem;
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
  margin: 0 0 0.75rem 0;
}

.empty-description {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  line-height: 1.5;
}

/* Responsive */
@media (max-width: 640px) {
  .header-title {
    font-size: 1.3rem;
  }

  .back-btn {
    width: 42px;
    height: 42px;
  }

  .back-btn ion-icon {
    font-size: 1.3rem;
  }

  .create-fab {
    padding: 0;
    width: 38px;
  }

  .create-fab ion-icon {
    font-size: 22px;
    padding-bottom: 0;
  }

  .create-fab-text {
    display: none;
  }

  .quote-card {
    padding: 0.875rem;
  }

  .quote-icon-wrapper {
    width: 48px;
    height: 48px;
  }

  .quote-icon {
    font-size: 1.5rem;
  }

  .quote-name {
    font-size: 0.95rem;
  }

  .quote-amount {
    top: 0.875rem;
    right: 0.875rem;
    padding: 0.25rem 0.625rem;
  }

  .amount-value {
    font-size: 0.9rem;
  }

  .detail-text {
    font-size: 0.8rem;
  }
}
</style>
