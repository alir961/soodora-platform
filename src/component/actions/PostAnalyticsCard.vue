<script setup lang="ts">
import { PropType, ref, computed } from 'vue';
import { PostAnalytics } from '@/entity/Post';
import { IonIcon } from '@ionic/vue';
import {
  heartOutline,
  chatbubbleOutline,
  repeatOutline,
  closeOutline,
  sparklesOutline,
  peopleOutline,
} from 'ionicons/icons';

const props = defineProps({
  analytics: {
    required: true,
    type: Object as PropType<PostAnalytics>,
  },
});

const displayed = ref(false);

const openModal = () => {
  displayed.value = true;
};

const closeModal = () => {
  displayed.value = false;
};

const totalEngagement = computed(() => {
  return props.analytics.likes + props.analytics.comments + props.analytics.reposts;
});

const subscriberEngagement = computed(() => {
  return props.analytics.subscribedLikes + props.analytics.subscribedComments + props.analytics.subscribedReposts;
});

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
                <IonIcon :icon="sparklesOutline" />
              </div>
              <div class="header-text">
                <h3>Post Analytics</h3>
                <p>Performance Overview</p>
              </div>
            </div>
            <button class="close-btn" @click="closeModal">
              <IonIcon :icon="closeOutline" />
            </button>
          </div>

          <!-- Total Engagement Banner -->
          <div class="engagement-banner">
            <span class="engagement-label">Total Engagement</span>
            <span class="engagement-value">{{ totalEngagement }}</span>
          </div>

          <!-- Main Stats -->
          <div class="stats-section">
            <h4 class="section-title">
              <IonIcon :icon="peopleOutline" />
              All Users
            </h4>
            <div class="stats-row">
              <div class="stat-card">
                <div class="stat-icon likes">
                  <IonIcon :icon="heartOutline" />
                </div>
                <span class="stat-value">{{ analytics.likes }}</span>
                <span class="stat-label">Likes</span>
              </div>
              <div class="stat-card">
                <div class="stat-icon comments">
                  <IonIcon :icon="chatbubbleOutline" />
                </div>
                <span class="stat-value">{{ analytics.comments }}</span>
                <span class="stat-label">Comments</span>
              </div>
              <div class="stat-card">
                <div class="stat-icon reposts">
                  <IonIcon :icon="repeatOutline" />
                </div>
                <span class="stat-value">{{ analytics.reposts }}</span>
                <span class="stat-label">Reposts</span>
              </div>
            </div>
          </div>

          <!-- Subscriber Stats -->
          <div class="stats-section subscriber-section">
            <h4 class="section-title">
              <IonIcon :icon="sparklesOutline" />
              Subscribers
              <span class="subscriber-badge">{{ subscriberEngagement }}</span>
            </h4>
            <div class="stats-row compact">
              <div class="stat-item">
                <IonIcon :icon="heartOutline" class="mini-icon likes" />
                <span>{{ analytics.subscribedLikes }}</span>
              </div>
              <div class="stat-item">
                <IonIcon :icon="chatbubbleOutline" class="mini-icon comments" />
                <span>{{ analytics.subscribedComments }}</span>
              </div>
              <div class="stat-item">
                <IonIcon :icon="repeatOutline" class="mini-icon reposts" />
                <span>{{ analytics.subscribedReposts }}</span>
              </div>
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
  border: 1px solid rgba(59, 130, 246, 0.2);
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
  border-bottom: 1px solid rgba(59, 130, 246, 0.15);
  background: rgba(59, 130, 246, 0.05);
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
  background: linear-gradient(135deg, #1d4ed8, #3b82f6);
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

/* Engagement Banner */
.engagement-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.9rem 1rem;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15) 0%, rgba(59, 130, 246, 0.05) 100%);
  border-bottom: 1px solid rgba(59, 130, 246, 0.1);
}

.engagement-label {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.engagement-value {
  font-size: 1.5rem;
  font-weight: 800;
  color: #60a5fa;
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
  color: #60a5fa;
}

.subscriber-badge {
  margin-left: auto;
  background: rgba(59, 130, 246, 0.2);
  color: #60a5fa;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 700;
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
  border-color: rgba(59, 130, 246, 0.2);
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

.stat-icon.likes {
  background: rgba(239, 68, 68, 0.12);
  color: #f87171;
}

.stat-icon.comments {
  background: rgba(59, 130, 246, 0.12);
  color: #60a5fa;
}

.stat-icon.reposts {
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

/* Subscriber Section */
.subscriber-section {
  padding-top: 0;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  margin-top: 0;
  padding: 0.9rem 1rem 1.1rem;
}

.stats-row.compact {
  gap: 8px;
}

.stat-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 10px;
}

.stat-item span {
  font-size: 1rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.85);
}

.mini-icon {
  font-size: 16px;
}

.mini-icon.likes {
  color: #f87171;
}

.mini-icon.comments {
  color: #60a5fa;
}

.mini-icon.reposts {
  color: #4ade80;
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
