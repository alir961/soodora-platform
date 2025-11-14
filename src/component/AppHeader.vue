<script setup lang="ts">
import { IonIcon } from '@ionic/vue';

defineProps<{
  title?: string;
  subtitle?: string;
  icon?: string;
  onIconClick?: () => void;
}>();
</script>

<template>
  <div class="profile-header">
    <div class="header-content">
      <div class="header-left">
        <button
          v-if="icon"
          class="header-icon-wrapper"
          :class="{ clickable: onIconClick }"
          @click="onIconClick"
          :disabled="!onIconClick">
          <ion-icon :icon="icon" class="header-icon" />
        </button>
        <div v-if="title" class="header-text">
          <h1 class="header-title">{{ title }}</h1>
          <p v-if="subtitle" class="header-subtitle">{{ subtitle }}</p>
        </div>
      </div>
      <div class="header-actions">
        <slot name="actions" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-header {
  position: sticky;
  top: 0;
  z-index: 10;
  background: rgba(18, 22, 30, 0.85);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
  padding: 1rem;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  max-width: 1400px;
  margin: 0 auto;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex: 1;
  min-width: 0;
}

.header-icon-wrapper {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(37, 99, 235, 0.15));
  border: 1px solid rgba(59, 130, 246, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.2);
  padding: 0;
  cursor: default;
  transition: all 0.3s ease;
}

.header-icon-wrapper.clickable {
  cursor: pointer;
}

.header-icon-wrapper.clickable:hover {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.3), rgba(37, 99, 235, 0.25));
  border-color: rgba(59, 130, 246, 0.4);
  box-shadow: 0 4px 20px rgba(59, 130, 246, 0.3);
}

.header-icon-wrapper:disabled {
  cursor: default;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(37, 99, 235, 0.15));
  border-color: rgba(59, 130, 246, 0.3);
}

.header-icon {
  font-size: 22px;
  color: #60a5fa;
}

.header-text {
  flex: 20px;
  min-width: 0;
}

.header-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 2px 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-subtitle {
  font-size: 0.8rem;
  color: #9aa3ad;
  margin: 0;
  font-weight: 500;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
</style>
