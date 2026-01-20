<script setup lang="ts">
import { PropType, ref } from 'vue';
import { ProductAnalytics } from '@/entity/Product';
import DurationDisplay from '@/component/date/DurationDisplay.vue';
import { IonIcon } from '@ionic/vue';
import {
  closeOutline,
  storefrontOutline,
  cubeOutline,
  cartOutline,
  timeOutline,
  trendingUpOutline,
} from 'ionicons/icons';

defineProps({
  analytics: {
    required: true,
    type: Object as PropType<ProductAnalytics>,
  },
});

const displayed = ref(false);

const openModal = () => {
  displayed.value = true;
};

const closeModal = () => {
  displayed.value = false;
};

defineExpose({
  open: openModal,
});
</script>

<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="displayed" class="analytics-modal-overlay" @click="closeModal">
        <div class="analytics-modal" @click.stop>
          <!-- Header -->
          <div class="modal-header">
            <div class="header-content">
              <div class="header-icon">
                <IonIcon :icon="storefrontOutline" />
              </div>
              <div class="header-text">
                <h3>Product Analytics</h3>
                <p>Sales Performance</p>
              </div>
            </div>
            <button class="close-btn" @click="closeModal">
              <IonIcon :icon="closeOutline" />
            </button>
          </div>

          <!-- Revenue Banner -->
          <div class="revenue-banner">
            <div class="revenue-main">
              <span class="revenue-label">Total Revenue</span>
              <span class="revenue-value"
                >{{ analytics.totalRevenue }}<span class="currency">{{ analytics.currency }}</span></span
              >
            </div>
            <div class="revenue-badge">
              <IonIcon :icon="trendingUpOutline" />
              {{ analytics.salesCount }} sales
            </div>
          </div>

          <!-- Inventory Stats -->
          <div class="stats-section">
            <h4 class="section-title">
              <IonIcon :icon="cubeOutline" />
              Inventory
            </h4>
            <div class="stats-row">
              <div class="stat-card">
                <div class="stat-icon stock">
                  <IonIcon :icon="cubeOutline" />
                </div>
                <span class="stat-value">{{ analytics.currentQuantity }}</span>
                <span class="stat-label">In Stock</span>
              </div>
              <div class="stat-card">
                <div class="stat-icon sold">
                  <IonIcon :icon="cartOutline" />
                </div>
                <span class="stat-value">{{ analytics.soldQuantity }}</span>
                <span class="stat-label">Sold</span>
              </div>
            </div>
          </div>

          <!-- Time to First Sale -->
          <div class="stats-section time-section">
            <h4 class="section-title">
              <IonIcon :icon="timeOutline" />
              Time to First Sale
            </h4>
            <div class="time-display">
              <DurationDisplay :duration="analytics.durationUntilFirstSale" :sales="analytics.salesCount" />
            </div>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<style scoped>
.analytics-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.analytics-modal {
  background: linear-gradient(165deg, rgba(15, 30, 55, 0.98) 0%, rgba(8, 18, 35, 0.98) 100%);
  border-radius: 20px;
  width: 92%;
  max-width: 340px;
  border: 1px solid rgba(34, 197, 94, 0.2);
  box-shadow:
    0 25px 50px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(255, 255, 255, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
  overflow: hidden;
}

/* Header */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.1rem 1rem;
  border-bottom: 1px solid rgba(34, 197, 94, 0.15);
  background: rgba(34, 197, 94, 0.05);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #15803d, #22c55e);
  border-radius: 12px;
  color: white;
}

.header-icon ion-icon {
  font-size: 20px;
}

.header-text h3 {
  margin: 0;
  font-size: 1.05rem;
  font-weight: 700;
  color: #ffffff;
}

.header-text p {
  margin: 2px 0 0;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
}

.close-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.close-btn ion-icon {
  font-size: 18px;
}

/* Revenue Banner */
.revenue-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.15) 0%, rgba(34, 197, 94, 0.05) 100%);
  border-bottom: 1px solid rgba(34, 197, 94, 0.1);
}

.revenue-main {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.revenue-label {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 500;
}

.revenue-value {
  font-size: 1.6rem;
  font-weight: 800;
  color: #4ade80;
}

.currency {
  font-size: 0.9rem;
  font-weight: 600;
  color: #4ade80;
  margin-left: 2px;
}

.revenue-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(34, 197, 94, 0.15);
  border: 1px solid rgba(34, 197, 94, 0.25);
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  color: #4ade80;
}

.revenue-badge ion-icon {
  font-size: 14px;
}

/* Stats Section */
.stats-section {
  padding: 1rem;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 0.9rem;
  font-size: 0.8rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.section-title ion-icon {
  font-size: 15px;
  color: #4ade80;
}

.stats-row {
  display: flex;
  gap: 10px;
}

.stat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 14px 8px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 14px;
  transition: all 0.2s ease;
}

.stat-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(34, 197, 94, 0.2);
}

.stat-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
}

.stat-icon ion-icon {
  font-size: 18px;
}

.stat-icon.stock {
  background: rgba(59, 130, 246, 0.12);
  color: #60a5fa;
}

.stat-icon.sold {
  background: rgba(34, 197, 94, 0.12);
  color: #4ade80;
}

.stat-value {
  font-size: 1.4rem;
  font-weight: 700;
  color: #ffffff;
}

.stat-label {
  font-size: 0.7rem;
  color: rgba(255, 255, 255, 0.45);
  text-transform: uppercase;
  letter-spacing: 0.03em;
}

/* Time Section */
.time-section {
  padding-top: 0;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  margin-top: 0;
  padding: 0.9rem 1rem 1.1rem;
}

.time-display {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.85);
}

/* Animation */
.modal-fade-enter-active {
  animation: overlayFadeIn 0.2s ease;
}

.modal-fade-leave-active {
  animation: overlayFadeIn 0.15s ease reverse;
}

.modal-fade-enter-active .analytics-modal {
  animation: modalScale 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active .analytics-modal {
  animation: modalScale 0.15s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

@keyframes overlayFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes modalScale {
  from {
    opacity: 0;
    transform: scale(0.92) translateY(12px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@media (max-width: 480px) {
  .analytics-modal {
    max-width: calc(100% - 2rem);
  }
}
</style>
