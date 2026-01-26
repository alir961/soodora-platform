<script setup lang="ts">
import { IonIcon } from '@ionic/vue';
import { closeOutline } from 'ionicons/icons';

defineProps<{
  imageUrl: string;
  alt?: string;
}>();

const emit = defineEmits<{
  close: [];
}>();

const close = () => {
  emit('close');
};
</script>

<template>
  <Teleport to="body">
    <Transition name="image-preview-fade">
      <div v-if="imageUrl" class="image-preview-overlay" @click="close">
        <div class="image-preview-container" @click.stop>
          <button class="preview-close" @click="close">
            <ion-icon :icon="closeOutline" />
          </button>
          <img :src="imageUrl" :alt="alt || 'Image preview'" class="preview-image" />
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.image-preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.92);
  backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 99999;
  padding: 1rem;
}

.image-preview-container {
  position: relative;
  max-width: 95vw;
  max-height: 90vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-image {
  max-width: 100%;
  max-height: 85vh;
  border-radius: 12px;
  object-fit: contain;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.preview-close {
  position: absolute;
  top: -16px;
  right: -16px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(18, 22, 30, 0.95);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4);
  z-index: 1;
}

.preview-close:hover {
  background: rgba(239, 68, 68, 0.95);
  border-color: rgba(239, 68, 68, 0.4);
  transform: scale(1.08) rotate(90deg);
}

.preview-close:active {
  transform: scale(0.95) rotate(90deg);
}

.preview-close ion-icon {
  font-size: 24px;
  color: white;
}

/* Animation */
.image-preview-fade-enter-active,
.image-preview-fade-leave-active {
  transition: opacity 0.25s ease;
}

.image-preview-fade-enter-from,
.image-preview-fade-leave-to {
  opacity: 0;
}
</style>
