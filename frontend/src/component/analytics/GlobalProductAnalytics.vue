<script setup lang="ts">
import { PropType } from 'vue';
import { ProductAnalytics } from '@/entity/Product';
import DurationDisplay from '@/component/date/DurationDisplay.vue';
import { IonIcon } from '@ionic/vue';
import {
  storefrontOutline,
  cubeOutline,
  cartOutline,
  cashOutline,
  receiptOutline,
  timeOutline,
  trendingUpOutline,
} from 'ionicons/icons';

defineProps({
  analytics: {
    required: true,
    type: Object as PropType<ProductAnalytics>,
  },
});
</script>

<template>
  <div class="analytics-section">
    <!-- Section Header -->
    <div class="section-header">
      <div class="header-icon green">
        <IonIcon :icon="storefrontOutline" />
      </div>
      <div class="header-text">
        <h3>Products Analytics</h3>
        <p>Sales from your marketplace</p>
      </div>
    </div>

    <!-- Revenue Highlight -->
    <div class="revenue-highlight">
      <div class="revenue-main">
        <IonIcon :icon="cashOutline" class="revenue-icon" />
        <div class="revenue-info">
          <span class="revenue-label">Total Revenue</span>
          <span class="revenue-value"
            >{{ analytics.totalRevenue }}<span class="currency">{{ analytics.currency }}</span></span
          >
        </div>
      </div>
      <div class="sales-badge">
        <IonIcon :icon="trendingUpOutline" />
        {{ analytics.salesCount }} sales
      </div>
    </div>

    <!-- Inventory Stats -->
    <div class="stats-group">
      <h4 class="group-title">
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
        <div class="stat-card">
          <div class="stat-icon sales">
            <IonIcon :icon="receiptOutline" />
          </div>
          <span class="stat-value">{{ analytics.salesCount }}</span>
          <span class="stat-label">Orders</span>
        </div>
      </div>
    </div>

    <!-- Time to First Sale -->
    <div class="stats-group time-group">
      <h4 class="group-title">
        <IonIcon :icon="timeOutline" />
        Time to First Sale
      </h4>
      <div class="time-display">
        <DurationDisplay :duration="analytics.durationUntilFirstSale" :sales="analytics.salesCount" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.analytics-section {
  margin: 0 1rem 1rem;
  background: linear-gradient(165deg, rgba(15, 35, 30, 0.6) 0%, rgba(8, 20, 18, 0.4) 100%);
  border: 1px solid rgba(34, 197, 94, 0.15);
  border-radius: 18px;
  overflow: hidden;
}

/* Section Header */
.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 1rem;
  background: rgba(34, 197, 94, 0.05);
  border-bottom: 1px solid rgba(34, 197, 94, 0.1);
}

.header-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  color: white;
}

.header-icon.green {
  background: linear-gradient(135deg, #15803d, #22c55e);
}

.header-icon ion-icon {
  font-size: 20px;
}

.header-text h3 {
  margin: 0;
  font-size: 1rem;
  font-weight: 700;
  color: #ffffff;
}

.header-text p {
  margin: 2px 0 0;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
}

/* Revenue Highlight */
.revenue-highlight {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem;
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.1) 0%, rgba(34, 197, 94, 0.03) 100%);
  border-bottom: 1px solid rgba(34, 197, 94, 0.08);
}

.revenue-main {
  display: flex;
  align-items: center;
  gap: 12px;
}

.revenue-icon {
  font-size: 28px;
  color: #4ade80;
}

.revenue-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.revenue-label {
  font-size: 0.7rem;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
}

.revenue-value {
  font-size: 1.5rem;
  font-weight: 800;
  color: #4ade80;
}

.currency {
  font-size: 0.9rem;
  font-weight: 600;
  margin-left: 2px;
}

.sales-badge {
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

.sales-badge ion-icon {
  font-size: 14px;
}

/* Stats Group */
.stats-group {
  padding: 1rem;
}

.group-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 0.8rem;
  font-size: 0.75rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.group-title ion-icon {
  font-size: 14px;
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
  padding: 12px 8px;
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
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
}

.stat-icon ion-icon {
  font-size: 17px;
}

.stat-icon.stock {
  background: rgba(59, 130, 246, 0.12);
  color: #60a5fa;
}

.stat-icon.sold {
  background: rgba(34, 197, 94, 0.12);
  color: #4ade80;
}

.stat-icon.sales {
  background: rgba(168, 85, 247, 0.12);
  color: #a855f7;
}

.stat-value {
  font-size: 1.3rem;
  font-weight: 700;
  color: #ffffff;
}

.stat-label {
  font-size: 0.65rem;
  color: rgba(255, 255, 255, 0.45);
  text-transform: uppercase;
  letter-spacing: 0.03em;
}

/* Time Group */
.time-group {
  padding-top: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
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
</style>
