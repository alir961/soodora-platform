<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { createPlaylist, updatePlaylistBySlug, searchPlaylists } from '@/client/playlistClient';
import { showToast } from '@/utils';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import Header from '@/component/AppHeader.vue';
import ImageUploadPreview from '@/component/marketplace/ImageUploadPreview.vue';
import { IonIcon, IonContent } from '@ionic/vue';
import { closeOutline, musicalNotesOutline } from 'ionicons/icons';

const router = useRouter();
const route = useRoute();

const isEditMode = computed(() => !!route.params.slug);
const headerTitle = computed(() => (isEditMode.value ? 'Edit Playlist' : 'Create Playlist'));
const submitButtonText = computed(() => (isEditMode.value ? 'Save Changes' : 'Create Playlist'));

const playlistData = ref<{ name: string; description: string; privacy: string }>({
  name: '',
  description: '',
  privacy: 'PUBLIC',
});

const playlistCoverFile = ref<File | null>(null);
const currentImageUrl = ref<string>('');
const isLoading = ref(false);

onMounted(async () => {
  if (isEditMode.value) {
    isLoading.value = true;
    const playlistSlug = route.params.slug as string;

    try {
      const response = await searchPlaylists(undefined, undefined, undefined, 0, 100, 'createdAt,desc');
      const playlist = response.content.find((p) => p.slug === playlistSlug);

      if (playlist) {
        playlistData.value = {
          name: playlist.name,
          description: playlist.description,
          privacy: playlist.privacy,
        };
        currentImageUrl.value = playlist.imageUrl;
      } else {
        throw new Error('Playlist not found');
      }
    } catch (error) {
      console.error('Failed to load playlist:', error);
      await showToast('Failed to load playlist', 'danger');
      router.back();
    } finally {
      isLoading.value = false;
    }
  }
});

const onPlaylistCoverSelected = (file: File | null) => {
  playlistCoverFile.value = file;
};

const closeForm = () => {
  router.back();
};

const isSubmitDisabled = computed(() => {
  if (isLoading.value) return true;
  if (!playlistData.value.name.trim() || !playlistData.value.description.trim()) return true;
  return false;
});

const submitForm = async () => {
  try {
    if (isSubmitDisabled.value) return;

    isLoading.value = true;

    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(playlistData.value)], { type: 'application/json' }));

    if (playlistCoverFile.value) {
      formData.append('file', playlistCoverFile.value, playlistCoverFile.value.name);
    }

    if (isEditMode.value) {
      const playlistSlug = route.params.slug as string;
      await updatePlaylistBySlug(playlistSlug, formData);
      await router.push(`/playlists/${playlistSlug}?t=${Date.now()}`);
    } else {
      await createPlaylist(formData);
      await router.push(`/playlists/me?t=${Date.now()}`);
    }
  } catch (error: unknown) {
    const action = isEditMode.value ? 'update' : 'create';
    const message = `Failed to ${action} playlist. ` + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <ApplicationViewport :title="headerTitle">
    <IonContent class="form-content-wrapper">
      <Header :title="headerTitle" :subtitle="playlistData.name || 'New playlist'" :icon="musicalNotesOutline">
        <template #actions>
          <button class="back-btn" @click="closeForm">
            <ion-icon :icon="closeOutline" />
          </button>
        </template>
      </Header>

      <div v-if="isLoading" class="loading">
        <p>Loading playlist...</p>
      </div>

      <div v-else class="form-content">
        <div class="form-section">
          <div class="section-header">
            <div style="display: flex; align-items: center; flex: 1; gap: 10px">
              <IonIcon :icon="musicalNotesOutline" class="section-icon" />
              <h3>Playlist Details</h3>
            </div>
          </div>

          <div class="form-field">
            <label>Name <span class="required">*</span></label>
            <input type="text" v-model="playlistData.name" placeholder="Give your playlist a name" required />
          </div>

          <div class="form-field">
            <label>Description <span class="required">*</span></label>
            <textarea
              v-model="playlistData.description"
              placeholder="Describe your playlist"
              rows="4"
              required></textarea>
          </div>

          <div class="form-field">
            <label>Privacy <span class="required">*</span></label>
            <select v-model="playlistData.privacy">
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

        <button class="submit-btn" @click="submitForm" :disabled="isSubmitDisabled">
          {{ submitButtonText }}
        </button>
      </div>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
}

/* Form header styles removed - now using Header component */

.section-icon {
  font-size: 24px;
  color: #60a5fa;
  flex-shrink: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.delete-icon-btn {
  appearance: none;
  border: 1px solid rgba(220, 38, 38, 0.3);
  background: rgba(220, 38, 38, 0.08);
  color: #ef4444;
  height: 38px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  padding: 0 0.5rem;
}

.delete-icon-btn ion-icon {
  font-size: 1.2rem;
  margin-right: 0.35rem;
}

.delete-label {
  font-size: 0.95rem;
  font-weight: 500;
  color: #ef4444;
  margin-left: 0.1rem;
  white-space: nowrap;
}

.delete-icon-btn:hover:not(:disabled) {
  background: rgba(220, 38, 38, 0.15);
  border-color: rgba(220, 38, 38, 0.5);
  box-shadow: 0 0 12px rgba(220, 38, 38, 0.2);
}

.delete-icon-btn:active:not(:disabled) {
  transform: scale(0.95);
}

.delete-icon-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.back-btn {
  appearance: none;
  border: 1px solid rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  font-size: 0.9rem;
  font-weight: 600;
  padding: 8px 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
}

.back-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  box-shadow: 0 0 12px rgba(239, 68, 68, 0.2);
}

.back-btn:active {
  transform: scale(0.95);
}

.back-btn ion-icon {
  font-size: 22px;
}

.loading {
  text-align: center;
  padding: 3rem 1.5rem;
  color: #9aa3ad;
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
  justify-content: space-between;
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
