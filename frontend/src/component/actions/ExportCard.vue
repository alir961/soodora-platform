<script setup lang="ts">
import { PropType, ref } from 'vue';
import { IonIcon } from '@ionic/vue';
import { shareOutline } from 'ionicons/icons';
import { Post } from '@/entity/Post';
import { Product } from '@/entity/Product';
import { createPostCanvas, createProductCanvas, downloadCanvas, ExportTheme } from '@/graphics/canvasExport';
import { showToast } from '@/utils';

const props = defineProps({
  item: {
    required: true,
    type: Object as PropType<Post | Product>,
  },
  type: {
    required: true,
    type: String as PropType<'post' | 'product'>,
  },
});

const showExportModal = ref(false);
const selectedTheme = ref<ExportTheme>('black');
const previewDataUrl = ref<string>('');
const isGeneratingPreview = ref(false);
const showFullscreenPreview = ref(false);

const open = () => {
  showExportModal.value = true;
  selectedTheme.value = 'black';
  generatePreview('black');
};

const closeModal = () => {
  showExportModal.value = false;
  selectedTheme.value = 'black';
  previewDataUrl.value = '';
};

const selectTheme = (theme: ExportTheme) => {
  selectedTheme.value = theme;
  generatePreview(theme);
};

const generatePreview = async (theme: ExportTheme) => {
  isGeneratingPreview.value = true;
  try {
    const canvas =
      props.type === 'post'
        ? await createPostCanvas(props.item as Post, theme)
        : await createProductCanvas(props.item as Product, theme);

    previewDataUrl.value = canvas.toDataURL();
  } catch (error) {
    console.error('Failed to generate preview:', error);
  } finally {
    isGeneratingPreview.value = false;
  }
};

const confirmExport = async () => {
  if (!selectedTheme.value) return;

  try {
    const canvas =
      props.type === 'post'
        ? await createPostCanvas(props.item as Post, selectedTheme.value)
        : await createProductCanvas(props.item as Product, selectedTheme.value);

    downloadCanvas(canvas);
    await showToast(`Succeed to share ${props.type}.`);
    closeModal();
  } catch (error: unknown) {
    const message = `Failed to share ${props.type}. ` + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

defineExpose({
  open,
});

const openFullscreenPreview = () => {
  showFullscreenPreview.value = true;
};

const closeFullscreenPreview = () => {
  showFullscreenPreview.value = false;
};
</script>

<template>
  <Teleport to="body">
    <transition name="modal-fade">
      <div v-if="showExportModal" class="export-modal-overlay" @click="closeModal">
        <div class="export-modal" @click.stop>
          <button class="close-btn" @click="closeModal">✕</button>

          <div class="modal-content">
            <h3>Share {{ type }}</h3>

            <div class="theme-selector">
              <button class="theme-btn" :class="{ active: selectedTheme === 'black' }" @click="selectTheme('black')">
                Black
              </button>
              <button class="theme-btn" :class="{ active: selectedTheme === 'white' }" @click="selectTheme('white')">
                White
              </button>
              <button
                class="theme-btn"
                :class="{ active: selectedTheme === 'transparent' }"
                @click="selectTheme('transparent')">
                Clear
              </button>
            </div>

            <div class="preview-container" @click="openFullscreenPreview">
              <div v-if="isGeneratingPreview" class="preview-loading">
                <div class="loading-spinner"></div>
              </div>
              <img v-else-if="previewDataUrl" :src="previewDataUrl" :alt="`${type} preview`" class="preview-image" />
            </div>

            <p class="preview-hint" v-if="!isGeneratingPreview && previewDataUrl">Tap to view full size</p>

            <button class="share-btn" :disabled="!selectedTheme || isGeneratingPreview" @click="confirmExport">
              <ion-icon :icon="shareOutline"></ion-icon>
              <span>Download & Share</span>
            </button>
          </div>
        </div>
      </div>
    </transition>

    <transition name="preview-fade">
      <div v-if="showFullscreenPreview" class="fullscreen-overlay" @click="closeFullscreenPreview">
        <div class="fullscreen-content">
          <button class="fullscreen-close" @click="closeFullscreenPreview">✕</button>
          <img
            v-if="previewDataUrl"
            :src="previewDataUrl"
            :alt="`${type} fullscreen preview`"
            class="fullscreen-image"
            @click.stop />
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<style scoped>
.export-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.export-modal {
  position: relative;
  background: linear-gradient(145deg, rgba(24, 30, 42, 0.98), rgba(18, 22, 30, 0.96));
  border-radius: 18px;
  width: 100%;
  max-width: 280px;
  backdrop-filter: blur(24px) saturate(180%);
  -webkit-backdrop-filter: blur(24px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow:
    0 24px 48px rgba(0, 0, 0, 0.7),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.close-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  appearance: none;
  border: 0;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.6);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 13px;
  font-weight: 300;
  transition: all 0.2s ease;
  z-index: 1;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  transform: rotate(90deg);
}

.close-btn:active {
  transform: rotate(90deg) scale(0.9);
}

.modal-content {
  padding: 0.75rem;
}

.modal-content h3 {
  margin: 0 0 0.65rem 0;
  font-size: 1.15rem;
  font-weight: 700;
  color: #ffffff;
  text-align: center;
  letter-spacing: -0.02em;
}

.theme-selector {
  display: flex;
  gap: 0.35rem;
  margin-bottom: 0.65rem;
  background: rgba(0, 0, 0, 0.3);
  padding: 0.3rem;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.theme-btn {
  flex: 1;
  appearance: none;
  border: 0;
  background: transparent;
  color: rgba(255, 255, 255, 0.5);
  padding: 0.4rem 0.4rem;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.theme-btn:hover {
  color: rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.05);
}

.theme-btn.active {
  background: linear-gradient(135deg, #2563eb, #60a5fa);
  color: #ffffff;
  box-shadow:
    0 2px 8px rgba(37, 99, 235, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.theme-btn:active {
  transform: scale(0.96);
}

.preview-container {
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 0.4rem;
  background: rgba(96, 165, 250, 0.08);
  border: 2px solid rgba(96, 165, 250, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  height: 160px;
  cursor: pointer;
  transition: all 0.25s ease;
  position: relative;
}

.preview-container::after {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(96, 165, 250, 0.05);
  opacity: 0;
  transition: opacity 0.25s ease;
  pointer-events: none;
}

.preview-container:hover {
  border-color: rgba(96, 165, 250, 0.3);
  transform: scale(1.02);
}

.preview-container:hover::after {
  opacity: 1;
}

.preview-container:active {
  transform: scale(0.98);
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.preview-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.loading-spinner {
  width: 28px;
  height: 28px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: #60a5fa;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.preview-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.3rem;
  margin: 0 0 0.65rem 0;
  font-size: 0.7rem;
  color: rgba(96, 165, 250, 0.8);
  font-weight: 500;
  text-align: center;
}

.preview-hint ion-icon {
  font-size: 15px;
}

.share-btn {
  width: 100%;
  height: 40px;
  border: 0;
  border-radius: 9px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 50%, #60a5fa);
  box-shadow:
    0 5px 15px rgba(37, 99, 235, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;
}

.share-btn ion-icon {
  padding-bottom: 4px;
  font-size: 15px;
}

.share-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none !important;
}

.share-btn:not(:disabled):hover {
  box-shadow:
    0 7px 20px rgba(37, 99, 235, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.share-btn:not(:disabled):active {
  transform: translateY(0);
  box-shadow:
    0 3px 10px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.fullscreen-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(96, 165, 250, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 10001;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  cursor: zoom-out;
}

.fullscreen-content {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.fullscreen-close {
  position: absolute;
  top: -16px;
  right: -16px;
  appearance: none;
  border: 0;
  background: rgba(18, 22, 30, 0.95);
  color: #ffffff;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 22px;
  font-weight: 300;
  transition: all 0.2s ease;
  z-index: 1;
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.5);
}

.fullscreen-close:hover {
  background: rgba(239, 68, 68, 0.95);
  border-color: rgba(239, 68, 68, 0.4);
  transform: scale(1.08) rotate(90deg);
}

.fullscreen-close:active {
  transform: scale(0.95) rotate(90deg);
}

.fullscreen-image {
  max-width: 90vw;
  max-height: 90vh;
  object-fit: contain;
  border-radius: 12px;
  box-shadow: 0 24px 72px rgba(0, 0, 0, 0.6);
  cursor: default;
  display: block;
}

.modal-fade-enter-active {
  animation: modalFadeIn 0.2s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active {
  animation: modalFadeIn 0.18s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

.modal-fade-enter-active .export-modal {
  animation: modalScale 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active .export-modal {
  animation: modalScale 0.22s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
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
    transform: scale(0.9) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.preview-fade-enter-active {
  animation: previewFadeIn 0.25s ease;
}

.preview-fade-leave-active {
  animation: previewFadeIn 0.2s ease reverse;
}

@keyframes previewFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@media (max-width: 480px) {
  .export-modal {
    max-width: calc(100% - 2rem);
  }

  .modal-content h3 {
    font-size: 1.1rem;
  }

  .theme-btn {
    font-size: 0.7rem;
    padding: 0.35rem 0.35rem;
  }

  .preview-container {
    height: 140px;
  }

  .preview-hint {
    font-size: 0.65rem;
  }

  .preview-hint ion-icon {
    font-size: 14px;
  }

  .fullscreen-overlay {
    padding: 1rem;
  }

  .fullscreen-close {
    top: -14px;
    right: -14px;
    width: 40px;
    height: 40px;
    font-size: 20px;
  }
}
</style>
