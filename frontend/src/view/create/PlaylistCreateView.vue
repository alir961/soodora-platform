<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { createPlaylist } from '@/client/playlistClient';
import { showToast } from '@/utils';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import ImageUploadPreview from '@/component/marketplace/ImageUploadPreview.vue';
import { IonIcon, IonContent } from '@ionic/vue';
import { closeOutline, musicalNotesOutline } from 'ionicons/icons';

const router = useRouter();

const newPlaylist = ref<{ name: string; description: string; privacy: string }>({
  name: '',
  description: '',
  privacy: 'PUBLIC',
});

const playlistCoverFile = ref<File | null>(null);
const currentImageUrl = ref<string>('');

const onPlaylistCoverSelected = (file: File | null) => {
  playlistCoverFile.value = file;
  if (file) {
    currentImageUrl.value = URL.createObjectURL(file);
  } else {
    currentImageUrl.value = '';
  }
};

const closeForm = () => {
  router.back();
};

const isLoading = ref(false);

const isSubmitDisabled = computed(() => {
  if (isLoading.value) return true;
  if (!newPlaylist.value.name.trim() || !newPlaylist.value.description.trim()) return true;
  return false;
});

const callCreatePlaylist = async () => {
  try {
    if (isSubmitDisabled.value) return;

    isLoading.value = true;

    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(newPlaylist.value)], { type: 'application/json' }));

    if (playlistCoverFile.value) {
      formData.append('file', playlistCoverFile.value, playlistCoverFile.value.name);
    }

    const createdPlaylist = await createPlaylist(formData);
    await showToast('Playlist created!');
    await router.push({ name: 'PlaylistDetailsView', params: { slug: createdPlaylist.slug } });
  } catch (error: unknown) {
    const message = 'Failed to create playlist. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <ApplicationViewport title="Create Playlist">
    <div class="form-header">
      <h2>Create Playlist</h2>
      <button class="close-btn" @click="closeForm">
        <IonIcon :icon="closeOutline" />
        Close
      </button>
    </div>

    <IonContent class="form-content-wrapper">
      <div class="form-content">
        <div class="form-section">
          <div class="section-header">
            <IonIcon :icon="musicalNotesOutline" class="section-icon" />
            <h3>Playlist Details</h3>
          </div>

          <div class="form-field">
            <label>Name <span class="required">*</span></label>
            <input type="text" v-model="newPlaylist.name" placeholder="Give your playlist a name" required />
          </div>

          <div class="form-field">
            <label>Description <span class="required">*</span></label>
            <textarea
              v-model="newPlaylist.description"
              placeholder="Describe your playlist"
              rows="4"
              required></textarea>
          </div>

          <div class="form-field">
            <label>Privacy <span class="required">*</span></label>
            <select v-model="newPlaylist.privacy">
              <option value="PUBLIC">Public</option>
              <option value="PRIVATE">Private</option>
              <option value="UNLISTED">Unlisted</option>
            </select>
          </div>

          <div class="form-field">
            <label>Cover Image <span class="optional">(Optional)</span></label>
            <ImageUploadPreview :current-image-url="currentImageUrl" @file-selected="onPlaylistCoverSelected" />
          </div>
        </div>

        <button class="submit-btn" @click="callCreatePlaylist" :disabled="isSubmitDisabled">
          {{ isLoading ? 'Creating...' : 'Create Playlist' }}
        </button>
      </div>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
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

.optional {
  color: #9aa3ad;
  font-size: 0.8rem;
  font-weight: 500;
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
  min-height: 100px;
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
