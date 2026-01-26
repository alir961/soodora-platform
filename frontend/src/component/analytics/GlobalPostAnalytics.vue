<script setup lang="ts">
import { PropType, computed } from 'vue';
import { PostAnalytics } from '@/entity/Post';
import { IonIcon } from '@ionic/vue';
import { heartOutline, chatbubbleOutline, repeatOutline, sparklesOutline, peopleOutline } from 'ionicons/icons';

const props = defineProps({
  analytics: {
    required: true,
    type: Object as PropType<PostAnalytics>,
  },
});

const subscriberTotal = computed(() => {
  return props.analytics.subscribedLikes + props.analytics.subscribedComments + props.analytics.subscribedReposts;
});
</script>

<template>
  <div class="analytics-section">
    <!-- Section Header -->
    <div class="section-header">
      <div class="header-icon blue">
        <IonIcon :icon="sparklesOutline" />
      </div>
      <div class="header-text">
        <h3>Posts Analytics</h3>
        <p>Engagement from all your posts</p>
      </div>
    </div>

    <!-- All Users Stats -->
    <div class="stats-group">
      <h4 class="group-title">
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
    <div class="stats-group subscriber-group">
      <h4 class="group-title">
        <IonIcon :icon="sparklesOutline" />
        Subscribers
        <span class="subscriber-badge">{{ subscriberTotal }}</span>
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
</template>

<style scoped>
.analytics-section {
  margin: 0 1rem 1rem;
  background: linear-gradient(165deg, rgba(15, 30, 55, 0.6) 0%, rgba(8, 18, 35, 0.4) 100%);
  border: 1px solid rgba(59, 130, 246, 0.15);
  border-radius: 18px;
  overflow: hidden;
}

/* Section Header */
.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 1rem;
  background: rgba(59, 130, 246, 0.05);
  border-bottom: 1px solid rgba(59, 130, 246, 0.1);
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

.header-icon.blue {
  background: linear-gradient(135deg, #1d4ed8, #3b82f6);
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
  color: #60a5fa;
}

.subscriber-badge {
  margin-left: auto;
  background: rgba(59, 130, 246, 0.2);
  color: #60a5fa;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 0.7rem;
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
  padding: 12px 8px;
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

/* Subscriber Group */
.subscriber-group {
  padding-top: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  margin-top: 0;
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
</style>
