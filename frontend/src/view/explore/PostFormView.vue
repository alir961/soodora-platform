<script setup lang="ts">
import { ref, computed } from 'vue';
import { IonContent, IonIcon } from '@ionic/vue';
import { useRouter } from 'vue-router';
import { closeOutline, textOutline, musicalNoteOutline, documentTextOutline } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import Header from '@/component/AppHeader.vue';
import ImageUploadPreview from '@/component/marketplace/ImageUploadPreview.vue';
import { callCreatePost, CreatePostRequest } from '@/client/postClient';
import { showToast } from '@/utils';

const router = useRouter();

type PostType = 'visual' | 'track';

const postType = ref<PostType>('visual');

const postData = ref<CreatePostRequest>({
  title: '',
  content: '',
  imageUrl: '',
  audioTitle: '',
  audioUrl: '',
  createdAt: new Date(),
  likeCount: 0,
  repostCount: 0,
  commentCount: 0,
});

const currentImageUrl = ref<string>('');
const selectedFile = ref<File | null>(null);
const isLoading = ref(false);

const isValidAudioUrl = (url: string) => /\.mp3$/i.test(url);

const onFileSelected = (file: File | null) => {
  selectedFile.value = file;
};

const closeForm = () => {
  router.back();
};

const switchPostType = (type: PostType) => {
  if (postType.value === type) return;

  postType.value = type;

  if (type === 'visual') {
    postData.value.audioTitle = '';
    postData.value.audioUrl = '';
  } else {
    postData.value.title = '';
    postData.value.content = '';
  }
};

const isSubmitDisabled = computed(() => {
  if (isLoading.value) return true;

  if (postType.value === 'visual') {
    if (!postData.value.title.trim() || !postData.value.content.trim()) return true;
  } else {
    if (!postData.value.audioTitle.trim() || !postData.value.audioUrl.trim()) return true;
    if (!isValidAudioUrl(postData.value.audioUrl)) return true;
  }

  return false;
});

const submitForm = async () => {
  if (isSubmitDisabled.value) return;

  isLoading.value = true;

  try {
    const formData = new FormData();

    const dataToSend = {
      ...postData.value,
      title: postType.value === 'visual' ? postData.value.title : '',
      content: postType.value === 'visual' ? postData.value.content : '',
      audioTitle: postType.value === 'track' ? postData.value.audioTitle : '',
      audioUrl: postType.value === 'track' ? postData.value.audioUrl : '',
    };

    formData.append('data', new Blob([JSON.stringify(dataToSend)], { type: 'application/json' }));

    if (selectedFile.value) {
      formData.append('file', selectedFile.value);
    }

    await callCreatePost(formData);
    postData.value.title = '';
    postData.value.content = '';
    postData.value.audioTitle = '';
    postData.value.audioUrl = '';

    await router.push('/explore');
  } catch (error: unknown) {
    const message = 'Failed to create post. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <ApplicationViewport title="Create Post">
    <ion-content class="form-content-wrapper">
      <Header
        title="Create Post"
        :subtitle="postType === 'visual' ? postData.title || 'New post' : postData.audioTitle || 'New track'"
        :icon="documentTextOutline">
        <template #actions>
          <button class="back-btn" @click="closeForm">
            <ion-icon :icon="closeOutline" />
          </button>
        </template>
      </Header>

      <div v-if="isLoading" class="loading">
        <p>Creating post...</p>
      </div>

      <div v-else class="form-content">
        <div class="post-type-selector">
          <button :class="['type-btn', { active: postType === 'visual' }]" @click="switchPostType('visual')">
            <ion-icon :icon="textOutline" />
            <span>Visual Post</span>
          </button>
          <button :class="['type-btn', { active: postType === 'track' }]" @click="switchPostType('track')">
            <ion-icon :icon="musicalNoteOutline" />
            <span>Track Post</span>
          </button>
        </div>

        <transition name="fade" mode="out-in">
          <div v-if="postType === 'visual'" key="visual-fields" class="form-section">
            <div class="section-header">
              <ion-icon :icon="textOutline" class="section-icon" />
              <h3>Share Your Moment</h3>
            </div>

            <div class="form-field">
              <label>Title <span class="required">*</span></label>
              <input type="text" v-model="postData.title" placeholder="Give your post a captivating title" required />
            </div>

            <div class="form-field">
              <label>Content <span class="required">*</span></label>
              <textarea v-model="postData.content" placeholder="What's on your mind?" rows="6" required></textarea>
            </div>

            <div class="form-field">
              <label>Cover Image <span class="optional">(Optional)</span></label>
              <ImageUploadPreview :current-image-url="currentImageUrl" @file-selected="onFileSelected" />
            </div>
          </div>

          <div v-else key="track-fields" class="form-section">
            <div class="section-header">
              <ion-icon :icon="musicalNoteOutline" class="section-icon" />
              <h3>Drop Your Track</h3>
            </div>

            <div class="form-field">
              <label>Track Title <span class="required">*</span></label>
              <input type="text" v-model="postData.audioTitle" placeholder="Name your masterpiece" required />
            </div>

            <div class="form-field">
              <label>Audio URL <span class="required">*</span></label>
              <input type="url" v-model="postData.audioUrl" placeholder="https://example.com/track.mp3" required />
              <p v-if="postData.audioUrl && !isValidAudioUrl(postData.audioUrl)" class="error-msg">
                Only .mp3 URLs are accepted
              </p>
            </div>

            <div class="form-field">
              <label>Album Art <span class="optional">(Optional - Default cover will be used)</span></label>
              <ImageUploadPreview :current-image-url="currentImageUrl" @file-selected="onFileSelected" />
            </div>
          </div>
        </transition>

        <button class="submit-btn" @click="submitForm" :disabled="isSubmitDisabled">Create Post</button>
      </div>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
}

/* Form header styles removed - now using Header component */

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

.post-type-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding: 8px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 14px;
}

.type-btn {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.04);
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.95rem;
  font-weight: 600;
  padding: 14px 20px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.type-btn ion-icon {
  font-size: 20px;
}

.type-btn:active {
  transform: scale(0.98);
}

.type-btn.active {
  background: linear-gradient(135deg, rgba(29, 78, 216, 0.25), rgba(37, 99, 235, 0.25));
  border-color: rgba(59, 130, 246, 0.4);
  color: #60a5fa;
  box-shadow:
    0 4px 16px rgba(37, 99, 235, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
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
.form-field textarea {
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
  min-height: 120px;
}

.form-field input::placeholder,
.form-field textarea::placeholder {
  color: #7f7f7f;
}

.form-field input:hover,
.form-field textarea:hover {
  border-color: rgba(59, 130, 246, 0.35);
  background: rgba(13, 27, 42, 0.6);
}

.form-field input:focus,
.form-field textarea:focus {
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow:
    0 0 0 6px rgba(59, 130, 246, 0.1),
    0 8px 24px rgba(0, 0, 0, 0.25);
  background: rgba(13, 27, 42, 0.7);
}

.error-msg {
  color: #f87171;
  font-size: 0.85rem;
  margin: 0;
  padding-left: 2px;
  display: flex;
  align-items: center;
  gap: 4px;
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

.fade-enter-active,
.fade-leave-active {
  transition:
    opacity 0.2s ease,
    transform 0.2s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
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
  .form-field textarea {
    font-size: 16px;
  }

  .type-btn {
    font-size: 0.9rem;
    padding: 12px 16px;
  }

  .type-btn span {
    display: none;
  }

  .type-btn ion-icon {
    font-size: 24px;
  }
}
</style>
