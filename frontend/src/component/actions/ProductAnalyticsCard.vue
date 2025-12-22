<script setup lang="ts">
import { PropType, ref } from 'vue';
import { ProductAnalytics } from '@/entity/Product';
import DurationDisplay from '@/component/date/DurationDisplay.vue';

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
          <h3 class="analytics-title">Analytics</h3>
          <p class="analytics-subtitle">Performance</p>

          <div class="analytics-grid">
            <div class="analytics-row">
              <span class="analytics-label">Current Quantity</span>
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

            <div class="analytics-row">
              <span class="analytics-label">Duration Until First Sale</span>
              <span class="analytics-value">
                <DurationDisplay :duration="analytics.durationUntilFirstSale" :sales="analytics.salesCount" />
              </span>
            </div>
          </div>

          <button class="cancel-button" @click="closeModal">Cancel</button>
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
  background: transparent;
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.analytics-modal {
  background: linear-gradient(145deg, rgb(6, 22, 57), rgb(10, 18, 35));
  border-radius: 16px;
  width: 80%;
  max-width: 300px;
  padding: 0.99rem 0.88rem;
  border: 1px solid rgba(255, 255, 255, 0.175);
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.8),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
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
  }
}

.cancel-button {
  height: 40px;
  border-radius: 8px;
  font-size: 0.88rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 1.1rem;
  width: 99px;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.1);
  margin: 1.2rem auto 0 auto;

  &:hover:not(:disabled) {
    background: rgba(255, 255, 255, 0.1);
    color: #ffffff;
  }

  &:active:not(:disabled) {
    transform: scale(0.97);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.modal-fade-enter-active .analytics-modal {
  animation: modalScale 0.2s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active .analytics-modal {
  animation: modalScale 0.15s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

@keyframes modalFadeIn {
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
    transform: scale(0.92) translateY(9px);
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

  .analytics-modal h3 {
    font-size: 1.045rem;
  }

  .analytics-modal p {
    font-size: 0.77rem;
  }

  .cancel-button {
    height: 35px;
    font-size: 0.825rem;
    min-width: 70px;
  }
}
</style>
