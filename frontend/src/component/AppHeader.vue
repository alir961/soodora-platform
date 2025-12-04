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

:deep(.about-btn),
:deep(.actions-button),
:deep(.create-button),
:deep(.create-fab),
:deep(.edit-fab),
:deep(.edit-btn),
:deep(.share-btn),
:deep(.settings-button),
:deep(.action-btn) {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.3);
  background: rgba(59, 130, 246, 0.15);
  color: #60a5fa;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
  padding: 0;
}

:deep(.visit-btn),
:deep(.download-btn) {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.3);
  background: rgba(59, 130, 246, 0.15);
  color: #60a5fa;
  width: auto;
  height: 36px;
  padding: 0 0.75rem;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

:deep(.about-btn) ion-icon,
:deep(.actions-button) ion-icon,
:deep(.create-button) ion-icon,
:deep(.create-fab) ion-icon,
:deep(.edit-fab) ion-icon,
:deep(.edit-btn) ion-icon,
:deep(.share-btn) ion-icon,
:deep(.settings-button) ion-icon,
:deep(.action-btn) ion-icon {
  font-size: 18px;
  color: #fff;
  flex-shrink: 0;
  padding-bottom: 0;
}

:deep(.visit-btn) ion-icon,
:deep(.download-btn) ion-icon {
  font-size: 18px;
  color: #fff;
  flex-shrink: 0;
  padding-bottom: 0;
}

:deep(.visit-btn) span,
:deep(.download-btn) span {
  font-size: 0.875rem;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

:deep(.about-btn):active,
:deep(.actions-button):active,
:deep(.create-button):active,
:deep(.create-fab):active,
:deep(.edit-fab):active,
:deep(.edit-btn):active,
:deep(.share-btn):active,
:deep(.settings-button):active,
:deep(.action-btn):active,
:deep(.visit-btn):active,
:deep(.download-btn):active {
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.25);
  background: linear-gradient(135deg, #1e40af 60%, #1e293b 100%);
  transform: scale(1.05);
}

:deep(.delete-fab),
:deep(.delete-btn),
:deep(.trash-button),
:deep(.back-button) {
  appearance: none;
  border: 1px solid rgba(239, 68, 68, 0.25);
  background: rgba(239, 68, 68, 0.12);
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
  padding: 0;
}

:deep(.delete-fab) ion-icon,
:deep(.delete-btn) ion-icon,
:deep(.trash-button) ion-icon,
:deep(.back-button) ion-icon {
  font-size: 18px;
  color: rgba(239, 68, 68, 1);
  flex-shrink: 0;
  padding-bottom: 0;
}

:deep(.delete-fab):active,
:deep(.delete-btn):active,
:deep(.trash-button):active,
:deep(.back-button):active {
  box-shadow: 0 8px 24px rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.18);
  transform: scale(1.05);
}

:deep(.settings-button.close-mode) {
  border-color: rgba(239, 68, 68, 0.25);
  background: rgba(239, 68, 68, 0.12);
}

:deep(.settings-button.close-mode) ion-icon {
  color: rgba(239, 68, 68, 1);
}

:deep(.settings-button.close-mode):active {
  box-shadow: 0 8px 24px rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.18);
}
</style>
