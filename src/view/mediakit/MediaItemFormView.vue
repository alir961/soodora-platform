<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import ImageUploadPreview from '@/component/marketplace/ImageUploadPreview.vue';
import { IonIcon } from '@ionic/vue';
import { closeOutline, imageOutline } from 'ionicons/icons';
import { showToast } from '@/utils';
import { addMediaKitItem, getMediaKitBySlug, updateMediaKitItem } from '@/client/mediaKitClient';
import { useAuthStore } from '@/store/reactiveStorage';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const IMAGE = 'Image';

const isEditMode = computed(() => !!route.params.itemSlug);
const headerTitle = computed(() => (isEditMode.value ? 'Edit Media Item' : 'Add Media Item'));
const submitButtonText = computed(() => (isEditMode.value ? 'Save Changes' : 'Add Item'));

const mediaKitSlug = computed(() => route.params.slug as string);
const itemSlug = computed(() => route.params.itemSlug as string);

const itemData = ref<{ type: string; title: string; content: string }>({
  type: '',
  title: '',
  content: '',
});

const selectedFile = ref<File | null>(null);
const currentImageUrl = ref<string>('');
const isLoading = ref(false);

onMounted(async () => {
  if (isEditMode.value) {
    isLoading.value = true;
    try {
      const mediaKit = await getMediaKitBySlug(mediaKitSlug.value);
      const item = mediaKit.items?.find((i) => i.slug === itemSlug.value);

      if (item) {
        itemData.value = {
          type: item.type,
          title: item.title,
          content: item.content || '',
        };
        if (item.type === IMAGE && item.content) {
          currentImageUrl.value = item.content;
        }
      } else {
        throw new Error('Item not found');
      }
    } catch (error) {
      console.error('Failed to load item:', error);
      await showToast('Failed to load item', 'danger');
      router.back();
    } finally {
      isLoading.value = false;
    }
  }
});

const onFileSelected = (file: File | null) => {
  selectedFile.value = file;
  if (file) itemData.value.content = '';
};

const closeForm = () => {
  router.back();
};

const isSubmitDisabled = computed(() => {
  if (isLoading.value) return true;
  if (!itemData.value.type || !itemData.value.title.trim()) return true;
  if (itemData.value.type === IMAGE && !selectedFile.value && !currentImageUrl.value) return true;
  if (itemData.value.type !== IMAGE && !itemData.value.content.trim()) return true;
  return false;
});

const submitForm = async () => {
  try {
    if (isSubmitDisabled.value) return;

    isLoading.value = true;

    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(itemData.value)], { type: 'application/json' }));

    if (itemData.value.type === IMAGE && selectedFile.value) {
      formData.append('file', selectedFile.value);
    }

    if (isEditMode.value) {
      await updateMediaKitItem(itemSlug.value, formData);
    } else {
      const artistSlug = authStore.getAuthenticatedUserSlug;
      if (!artistSlug) {
        throw new Error('User not authenticated');
      }
      await addMediaKitItem(artistSlug, formData);
    }

    await router.push(`/mediakits/${mediaKitSlug.value}?t=${Date.now()}`);
  } catch (error: unknown) {
    const action = isEditMode.value ? 'update' : 'add';
    const message = `Failed to ${action} item. ` + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <ApplicationViewport :title="headerTitle">
    <div class="form-header">
      <h2>{{ headerTitle }}</h2>
      <button class="close-btn" @click="closeForm">
        <IonIcon :icon="closeOutline" />
        Close
      </button>
    </div>

    <div class="form-content-wrapper">
      <div v-if="isLoading" class="loading">
        <p>Loading item...</p>
      </div>

      <div v-else class="form-content">
        <div class="form-section">
          <div class="section-header">
            <IonIcon :icon="imageOutline" class="section-icon" />
            <h3>Item Details</h3>
          </div>

          <div class="form-field">
            <label>Type <span class="required">*</span></label>
            <select v-model="itemData.type" required>
              <option value="" disabled>Select type</option>
              <option value="Image">Image</option>
              <option value="Video">Video</option>
              <option value="Audio">Audio</option>
              <option value="Text">Text</option>
            </select>
          </div>

          <div class="form-field">
            <label>Title <span class="required">*</span></label>
            <input type="text" v-model="itemData.title" placeholder="Enter item title" required />
          </div>

          <div v-if="itemData.type === IMAGE" class="form-field">
            <label>Image <span class="required">*</span></label>
            <ImageUploadPreview :current-image-url="currentImageUrl" @file-selected="onFileSelected" />
          </div>

          <div v-if="itemData.type && itemData.type !== IMAGE" class="form-field">
            <label>Content <span class="required">*</span></label>
            <textarea
              v-model="itemData.content"
              placeholder="Enter content or description..."
              rows="5"
              required></textarea>
          </div>
        </div>

        <button class="submit-btn" @click="submitForm" :disabled="isSubmitDisabled">
          {{ isLoading ? (isEditMode ? 'Updating...' : 'Adding...') : submitButtonText }}
        </button>
      </div>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
  background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
  overflow-y: auto;
  min-height: 100vh;
  padding-bottom: 2rem;
}

.form-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid rgba(59, 130, 246, 0.15);
  background: linear-gradient(to bottom, rgba(13, 27, 42, 0.98), rgba(13, 27, 42, 0.85));
  backdrop-filter: blur(16px) saturate(180%);
  -webkit-backdrop-filter: blur(16px) saturate(180%);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
}

.form-header h2 {
  font-size: 1.4rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  text-shadow: 0 0 8px rgba(59, 130, 246, 0.3);
}

.close-btn {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.2);
  background: rgba(59, 130, 246, 0.08);
  color: #60a5fa;
  font-size: 0.9rem;
  font-weight: 600;
  padding: 8px 14px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.close-btn:hover {
  background: rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.35);
  box-shadow: 0 0 12px rgba(59, 130, 246, 0.2);
}

.close-btn:active {
  transform: scale(0.98);
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50vh;
  color: rgba(255, 255, 255, 0.7);
  font-size: 1rem;
}

.form-content {
  padding: 1.5rem;
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  background: linear-gradient(to bottom, rgba(13, 27, 42, 0.6), rgba(9, 18, 30, 0.5));
  border-radius: 16px;
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.section-icon {
  font-size: 24px;
  color: #60a5fa;
}

.section-header h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  letter-spacing: 0.01em;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-field label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #cfcfcf;
  letter-spacing: 0.01em;
  display: flex;
  align-items: center;
  gap: 6px;
}

.required {
  color: #f87171;
  font-size: 1rem;
}

.form-field input,
.form-field textarea,
.form-field select {
  appearance: none;
  outline: none;
  border: 1px solid rgba(59, 130, 246, 0.2);
  background: rgba(13, 27, 42, 0.4);
  color: #fff;
  padding: 12px 14px;
  border-radius: 12px;
  font-size: 15px;
  font-family: inherit;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;
}

.form-field textarea {
  resize: vertical;
  min-height: 80px;
}

.form-field input::placeholder,
.form-field textarea::placeholder {
  color: #7f7f7f;
}

.form-field input:hover,
.form-field textarea:hover,
.form-field select:hover {
  border-color: rgba(59, 130, 246, 0.35);
  background: rgba(13, 27, 42, 0.6);
}

.form-field input:focus,
.form-field textarea:focus,
.form-field select:focus {
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow:
    0 0 0 6px rgba(59, 130, 246, 0.1),
    0 8px 24px rgba(0, 0, 0, 0.25);
  background: rgba(13, 27, 42, 0.7);
}

.form-field select {
  cursor: pointer;
  background-image: url("data:image/svg+xml,%3Csvg width='12' height='8' viewBox='0 0 12 8' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M1 1.5L6 6.5L11 1.5' stroke='white' stroke-opacity='0.6' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
  padding-right: 2.5rem;
}

.submit-btn {
  width: 100%;
  height: 48px;
  border: 0;
  border-radius: 12px;
  cursor: pointer;
  color: #fff;
  font-weight: 700;
  font-size: 1rem;
  letter-spacing: 0.01em;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  box-shadow:
    0 10px 30px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
  transition: all 0.2s ease;
  margin-top: 0.5rem;
}

.submit-btn:hover:not(:disabled) {
  box-shadow:
    0 12px 32px rgba(37, 99, 235, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.16);
  transform: translateY(-1px);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow:
    0 8px 24px rgba(37, 99, 235, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  background: linear-gradient(135deg, #374151, #4b5563);
  box-shadow: none;
}

@media (max-width: 768px) {
  .form-header {
    padding: 1rem 1.25rem;
  }

  .form-header h2 {
    font-size: 1.2rem;
  }

  .form-content {
    padding: 1.25rem;
    margin-top: 0.75rem;
    margin-bottom: 0.75rem;
  }

  .form-field input,
  .form-field textarea,
  .form-field select {
    font-size: 16px;
  }
}
</style>
