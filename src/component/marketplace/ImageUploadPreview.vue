<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { IonIcon } from '@ionic/vue';
import { cloudUploadOutline, imageOutline } from 'ionicons/icons';

const props = defineProps({
  currentImageUrl: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['file-selected']);

const selectedFile = ref<File | null>(null);
const previewUrl = ref<string>(props.currentImageUrl || '');
const fileInputRef = ref<HTMLInputElement | null>(null);
const showImagePreview = ref(false);

const hasImage = computed(() => !!previewUrl.value);

watch(() => props.currentImageUrl, (newUrl) => {
  if (newUrl && !selectedFile.value) {
    previewUrl.value = newUrl;
  }
});

const handleFileChange = (event: Event) => {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  
  if (file) {
    selectedFile.value = file;
    const reader = new FileReader();
    reader.onload = (e) => {
      previewUrl.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
    emit('file-selected', file);
  }
};

const triggerFileInput = () => {
  fileInputRef.value?.click();
};

const openImagePreview = () => {
  showImagePreview.value = true;
};

const closeImagePreview = () => {
  showImagePreview.value = false;
};
</script>

<template>
  <div class="image-upload-preview">
    <input
      ref="fileInputRef"
      type="file"
      accept="image/*"
      @change="handleFileChange"
      class="hidden-input"
    />
    
    <div v-if="hasImage" class="preview-container">
      <img 
        :src="previewUrl" 
        alt="Product preview" 
        class="preview-image" 
        @click="openImagePreview" />
      <button type="button" class="change-btn" @click="triggerFileInput">
        <ion-icon :icon="cloudUploadOutline" />
        Change Image
      </button>
    </div>
    
    <div v-else class="upload-placeholder" @click="triggerFileInput">
      <div class="placeholder-content">
        <ion-icon :icon="imageOutline" class="placeholder-icon" />
        <span class="placeholder-text">Choose Image</span>
        <span class="placeholder-hint">Click to upload</span>
      </div>
    </div>

    <!-- Fullscreen Image Preview -->
    <transition name="preview-fade">
      <div v-if="showImagePreview" class="image-preview-overlay" @click="closeImagePreview">
        <div class="image-preview-container" @click.stop>
          <button class="preview-close" @click="closeImagePreview">✕</button>
          <img :src="previewUrl" alt="Product preview fullscreen" />
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.image-upload-preview {
  width: 100%;
}

.hidden-input {
  display: none;
}

.preview-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: rgba(13, 27, 42, 0.3);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 12px;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.preview-image {
  width: 140px;
  height: 140px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid rgba(59, 130, 246, 0.3);
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  cursor: zoom-in;
  transition: all 0.2s ease;
}

.preview-image:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.4);
  border-color: rgba(59, 130, 246, 0.5);
}

.preview-image:active {
  transform: scale(0.98);
}

.change-btn {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.3);
  background: linear-gradient(135deg, rgba(29, 78, 216, 0.2), rgba(37, 99, 235, 0.2));
  color: #60a5fa;
  font-size: 0.9rem;
  font-weight: 600;
  padding: 12px 20px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  flex: 1;
  justify-content: center;
}

.change-btn ion-icon {
  font-size: 1.2rem;
}

.change-btn:hover {
  background: linear-gradient(135deg, rgba(29, 78, 216, 0.3), rgba(37, 99, 235, 0.3));
  border-color: rgba(59, 130, 246, 0.5);
  box-shadow: 0 0 16px rgba(59, 130, 246, 0.2);
  transform: translateY(-1px);
}

.change-btn:active {
  transform: translateY(0);
}

.upload-placeholder {
  padding: 2rem;
  border: 2px dashed rgba(59, 130, 246, 0.3);
  border-radius: 12px;
  background: rgba(13, 27, 42, 0.2);
  cursor: pointer;
  transition: all 0.2s ease;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.upload-placeholder:hover {
  border-color: rgba(59, 130, 246, 0.5);
  background: rgba(13, 27, 42, 0.35);
  box-shadow: 0 0 20px rgba(59, 130, 246, 0.15);
}

.upload-placeholder:active {
  transform: scale(0.99);
}

.placeholder-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.placeholder-icon {
  font-size: 3rem;
  color: #60a5fa;
  opacity: 0.7;
}

.placeholder-text {
  font-size: 1rem;
  font-weight: 600;
  color: #60a5fa;
}

.placeholder-hint {
  font-size: 0.85rem;
  color: #9aa3ad;
}

/* Fullscreen Image Preview */
.image-preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.92);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  z-index: 10002;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.image-preview-container {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview-container img {
  max-width: 100%;
  max-height: 90vh;
  object-fit: contain;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.preview-close {
  position: absolute;
  top: -16px;
  right: -16px;
  appearance: none;
  border: 0;
  background: rgba(18, 22, 30, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #ffffff;
  font-size: 20px;
  font-weight: 300;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
  transition: all 0.2s ease;
  z-index: 1;
}

.preview-close:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  transform: scale(1.05);
}

.preview-close:active {
  transform: scale(0.95);
}

/* Preview fade animation */
.preview-fade-enter-active {
  animation: previewFadeIn 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.preview-fade-leave-active {
  animation: previewFadeIn 0.2s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

.preview-fade-enter-active .image-preview-container {
  animation: previewZoomIn 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.preview-fade-leave-active .image-preview-container {
  animation: previewZoomIn 0.25s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

@keyframes previewFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes previewZoomIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@media (max-width: 480px) {
  .preview-container {
    flex-direction: column;
    text-align: center;
  }

  .preview-image {
    width: 120px;
    height: 120px;
  }

  .change-btn {
    width: 100%;
  }

  .image-preview-overlay {
    padding: 1rem;
  }

  .preview-close {
    top: -12px;
    right: -12px;
    width: 36px;
    height: 36px;
    font-size: 18px;
  }
}
</style>
