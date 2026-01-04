<script setup lang="ts">
import { PropType } from 'vue';
import { ProductAnalytics } from '@/entity/Product';
import DurationDisplay from '@/component/date/DurationDisplay.vue';

defineProps({
  analytics: {
    required: true,
    type: Object as PropType<ProductAnalytics>,
  },
});
</script>

<template>
  <div class="analytics-modal">
    <h3 class="analytics-title">Products Analytics</h3>
    <p class="analytics-subtitle">Fetched from all your products and sales</p>

    <div class="analytics-grid">
      <div class="analytics-row">
        <span class="analytics-label">Total Quantity</span>
        <span class="analytics-value">{{ analytics.currentQuantity }}</span>
      </div>

      <div class="analytics-row">
        <span class="analytics-label">Sold Quantity</span>
        <span class="analytics-value">{{ analytics.soldQuantity }}</span>
      </div>

      <div class="analytics-row">
        <span class="analytics-label">Total Revenue</span>
        <span class="analytics-value">{{ analytics.totalRevenue + analytics.currency }}</span>
      </div>

      <div class="analytics-row">
        <span class="analytics-label">Sales Count</span>
        <span class="analytics-value">{{ analytics.salesCount }}</span>
      </div>

      <div class="analytics-row duration">
        <span class="analytics-label">Duration Until First Sale</span>
        <span class="analytics-value">
          <DurationDisplay :duration="analytics.durationUntilFirstSale" :sales="analytics.salesCount" />
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.analytics-modal {
  padding: 0.99rem 0.88rem;
  text-align: center;

  & h3 {
    margin: 0 0 0.33rem 0;
    font-size: 1.1rem;
    font-weight: 700;
    color: #ffffff;
    letter-spacing: -0.01em;
  }

  & p {
    margin: 0 0 0.825rem 0;
    font-size: 0.825rem;
    color: rgba(255, 255, 255, 0.6);
    font-weight: 500;
  }
}

.analytics-grid {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;

  & .analytics-row {
    flex: 1;
    background: rgba(255, 255, 255, 0.04);
    padding: 16px;
    border-radius: 12px;
    text-align: center;
    backdrop-filter: blur(4px);

    & .analytics-label {
      display: block;
      font-size: 0.9rem;
      opacity: 0.7;
      margin-bottom: 4px;
    }

    & .analytics-value {
      font-size: 1.8rem;
      font-weight: 700;
    }

    &.duration {
      flex: 2;
    }
  }
}

@media (max-width: 480px) {
  .analytics-modal h3 {
    font-size: 1.045rem;
  }

  .analytics-modal p {
    font-size: 0.77rem;
  }
}
</style>
