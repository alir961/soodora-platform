<script setup lang="ts">
import { PropType } from 'vue';
import { PostAnalytics } from '@/entity/Post';

defineProps({
  analytics: {
    required: true,
    type: Object as PropType<PostAnalytics>,
  },
});
</script>

<template>
  <div class="analytics-modal">
    <h3 class="analytics-title">Posts Analytics</h3>
    <p class="analytics-subtitle">Fetched from all your posts</p>

    <div class="analytics-grid">
      <div class="analytics-row">
        <span class="analytics-label">Likes</span>
        <span class="analytics-value">{{ analytics.likes }}</span>
      </div>

      <div class="analytics-row">
        <span class="analytics-label">Comments</span>
        <span class="analytics-value">{{ analytics.comments }}</span>
      </div>

      <div class="analytics-row">
        <span class="analytics-label">Reposts</span>
        <span class="analytics-value">{{ analytics.reposts }}</span>
      </div>

      <div class="analytics-row">
        <span class="analytics-label">Subscriber Likes</span>
        <span class="analytics-value">{{ analytics.subscribedLikes }}</span>
      </div>

      <div class="analytics-row">
        <span class="analytics-label">Subscriber Comments</span>
        <span class="analytics-value">{{ analytics.subscribedComments }}</span>
      </div>

      <div class="analytics-row">
        <span class="analytics-label">Subscriber Reposts</span>
        <span class="analytics-value">{{ analytics.subscribedReposts }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.analytics-modal {
  padding: 0.99rem 0.88rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.175);
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
