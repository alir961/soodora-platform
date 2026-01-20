<script setup lang="ts">
import { onIonViewWillEnter, IonContent, IonIcon } from '@ionic/vue';
import { computed, onMounted, ref } from 'vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { callComputePostsAnalytics, callComputeProductsAnalytics } from '@/client/analyticsClient';
import { PostAnalytics } from '@/entity/Post';
import { ProductAnalytics } from '@/entity/Product';
import GlobalPostAnalytics from '@/component/analytics/GlobalPostAnalytics.vue';
import GlobalProductAnalytics from '@/component/analytics/GlobalProductAnalytics.vue';
import AppHeader from '@/component/AppHeader.vue';
import { arrowBackOutline, statsChartOutline } from 'ionicons/icons';
import { useRouter } from 'vue-router';

const router = useRouter();

const postAnalytics = ref<PostAnalytics | null>(null);
const productAnalytics = ref<ProductAnalytics | null>(null);

const fetchAnalytics = async () => {
  try {
    postAnalytics.value = await callComputePostsAnalytics();
    productAnalytics.value = await callComputeProductsAnalytics();
  } catch (error) {
    console.error('Failed to fetch analytics:', error);
  }
};

const goBack = () => {
  router.back();
};

const totalEngagement = computed(() => {
  if (!postAnalytics.value) return 0;
  return postAnalytics.value.likes + postAnalytics.value.comments + postAnalytics.value.reposts;
});

const isLoading = computed(() => !postAnalytics.value && !productAnalytics.value);

onIonViewWillEnter(async () => await fetchAnalytics());

onMounted(async () => await fetchAnalytics());
</script>

<template>
  <ApplicationViewport>
    <IonContent class="analytics-content">
      <div class="ambient-glow">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
      </div>

      <div class="analytics-wrapper">
        <AppHeader
          title="Analytics"
          subtitle="Your performance overview"
          :icon="arrowBackOutline"
          :on-icon-click="goBack" />

        <!-- Loading State -->
        <div v-if="isLoading" class="loading-state">
          <div class="spinner"></div>
          <p>Loading analytics...</p>
        </div>

        <!-- Analytics Content -->
        <div v-else class="analytics-sections">
          <!-- Summary Banner -->
          <div class="summary-banner">
            <div class="summary-icon">
              <IonIcon :icon="statsChartOutline" />
            </div>
            <div class="summary-content">
              <span class="summary-label">Total Post Engagement</span>
              <span class="summary-value">{{ totalEngagement }}</span>
            </div>
            <div v-if="productAnalytics" class="summary-revenue">
              <span class="revenue-label">Revenue</span>
              <span class="revenue-value">{{ productAnalytics.totalRevenue }}{{ productAnalytics.currency }}</span>
            </div>
          </div>

          <!-- Post Analytics Section -->
          <GlobalPostAnalytics v-if="postAnalytics" :analytics="postAnalytics" />

          <!-- Product Analytics Section -->
          <GlobalProductAnalytics v-if="productAnalytics" :analytics="productAnalytics" />
        </div>
      </div>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
.analytics-content {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
}

.ambient-glow {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, transparent 70%);
  top: -100px;
  left: -100px;
}

.orb-2 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(34, 197, 94, 0.15) 0%, transparent 70%);
  bottom: 100px;
  right: -50px;
}

.orb-3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.12) 0%, transparent 70%);
  top: 50%;
  left: 30%;
}

.analytics-wrapper {
  position: relative;
  z-index: 1;
  padding-bottom: 2rem;
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 1.5rem;
  gap: 1rem;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid rgba(59, 130, 246, 0.2);
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.9rem;
  margin: 0;
}

/* Summary Banner */
.summary-banner {
  display: flex;
  align-items: center;
  gap: 14px;
  margin: 1rem 1rem 1rem;
  padding: 1rem 1.1rem;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.12) 0%, rgba(34, 197, 94, 0.08) 100%);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 16px;
}

.summary-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1d4ed8, #3b82f6);
  border-radius: 12px;
  color: white;
}

.summary-icon ion-icon {
  font-size: 22px;
}

.summary-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.summary-label {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 500;
}

.summary-value {
  font-size: 1.5rem;
  font-weight: 800;
  color: #60a5fa;
}

.summary-revenue {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.revenue-label {
  font-size: 0.7rem;
  color: rgba(255, 255, 255, 0.4);
  text-transform: uppercase;
}

.revenue-value {
  font-size: 1.2rem;
  font-weight: 700;
  color: #4ade80;
}

.analytics-sections {
  display: flex;
  flex-direction: column;
  gap: 0;
}
</style>
