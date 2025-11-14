<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { showToast } from '@/utils';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import Header from '@/component/AppHeader.vue';
import { createLinkPage, createLink, CreateLinkPageRequest, CreateLinkRequest } from '@/client/linkPageClient';
import { IonIcon, IonContent } from '@ionic/vue';
import { closeOutline, linkOutline, addCircleOutline } from 'ionicons/icons';
import { iconOptions, platformIcons, platformUrlPrefixes } from '@/entity/Link';

type EditableLink = CreateLinkRequest & { slug?: string };
const router = useRouter();
const pageData = ref({ title: '', description: '' });
const links = ref<EditableLink[]>([]);
const editingLinkIndex = ref<number | null>(null);
const isSubmitting = ref(false);

function addLink() {
  links.value.push({ title: '', description: '', url: '', iconUrl: 'other' });
  editingLinkIndex.value = links.value.length - 1;
}

function deleteTempLink(idx: number) {
  links.value.splice(idx, 1);
  if (editingLinkIndex.value === idx) editingLinkIndex.value = null;
}

function updateLinkUrl(link: EditableLink, platform: string) {
  link.iconUrl = platform;
  const prefix = platformUrlPrefixes[platform];
  if (!link.url || Object.values(platformUrlPrefixes).some((p) => p && link.url.startsWith(p))) {
    link.url = prefix;
  }
}

function isLinkValid(link: EditableLink): boolean {
  return !!(link.title.trim() && link.description.trim() && link.url.trim());
}

const areLinksValid = computed(() => links.value.every(isLinkValid));

async function submitCreate() {
  if (
    !pageData.value.title.trim() ||
    !pageData.value.description.trim() ||
    !areLinksValid.value ||
    isSubmitting.value
  ) {
    return;
  }

  isSubmitting.value = true;
  try {
    const pageCommand: CreateLinkPageRequest = {
      title: pageData.value.title,
      description: pageData.value.description,
    };
    const createdSlug = await createLinkPage(pageCommand);

    for (const link of links.value) {
      await createLink(createdSlug, link);
    }

    await router.replace(`/linkhub/edit/${createdSlug}`);
    pageData.value.title = '';
    pageData.value.description = '';
  } catch (error: unknown) {
    const message = 'Unable to create Link Hub. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
    console.error(error);
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<template>
  <ApplicationViewport title="Create Link Hub">
    <ion-content class="form-content-wrapper">
      <Header title="Create Link Hub" :subtitle="pageData.title || 'New hub'" :icon="linkOutline">
        <template #actions>
          <button class="back-btn" @click="router.back()">
            <ion-icon :icon="closeOutline" />
          </button>
        </template>
      </Header>

      <div class="form-content">
        <div class="form-section">
          <div class="section-header">
            <div class="header-left">
              <ion-icon :icon="linkOutline" class="section-icon" />
              <h3>Your Link Hub</h3>
            </div>
          </div>

          <div class="form-field">
            <label>Page Title <span class="required">*</span></label>
            <input type="text" v-model="pageData.title" placeholder="Give your link hub a name" required />
          </div>

          <div class="form-field">
            <label>Description <span class="required">*</span></label>
            <textarea
              v-model="pageData.description"
              placeholder="Describe what visitors will find here"
              rows="4"
              required></textarea>
          </div>
        </div>

        <div class="form-section">
          <div class="section-header">
            <div class="header-left">
              <ion-icon :icon="linkOutline" class="section-icon" />
              <h3>Links</h3>
            </div>
            <button class="add-link-btn" @click="addLink">
              <ion-icon :icon="addCircleOutline" />
              <span>Add Link</span>
            </button>
          </div>

          <div v-if="links.length === 0" class="empty-links">
            <p>No links yet. Add your first link!</p>
          </div>

          <div v-else class="links-grid">
            <div v-for="(link, idx) in links" :key="idx" class="link-wrapper">
              <div class="link-card expanded">
                <div class="form-field">
                  <label>Link Title <span class="required">*</span></label>
                  <input type="text" v-model="link.title" placeholder="e.g., My Instagram" />
                </div>

                <div class="form-field">
                  <label>Description <span class="required">*</span></label>
                  <input type="text" v-model="link.description" placeholder="Brief description" />
                </div>

                <div class="form-field">
                  <label>Platform</label>
                  <div class="platform-selector">
                    <button
                      v-for="option in iconOptions"
                      :key="option.value"
                      type="button"
                      class="platform-option"
                      :class="{ active: link.iconUrl === option.value }"
                      @click="updateLinkUrl(link, option.value)"
                      :title="option.label">
                      <ion-icon v-if="platformIcons[option.value]" :src="platformIcons[option.value]" />
                      <span v-else>{{ option.label.charAt(0) }}</span>
                    </button>
                  </div>
                </div>

                <div class="form-field">
                  <label>URL <span class="required">*</span></label>
                  <div class="url-input-wrapper">
                    <div class="url-prefix">
                      <ion-icon
                        v-if="link.iconUrl !== 'other' && platformIcons[link.iconUrl]"
                        :src="platformIcons[link.iconUrl]"
                        class="url-prefix-icon" />
                      <ion-icon v-else :icon="linkOutline" class="url-prefix-icon" />
                    </div>
                    <input
                      type="url"
                      v-model="link.url"
                      :placeholder="link.iconUrl === 'other' ? 'https://...' : platformUrlPrefixes[link.iconUrl]" />
                  </div>
                </div>

                <button class="delete-link-btn" @click="deleteTempLink(idx)">Delete Link</button>
              </div>
            </div>
          </div>
        </div>

        <button
          class="submit-btn"
          @click="submitCreate"
          :disabled="!pageData.title.trim() || !pageData.description.trim() || !areLinksValid || isSubmitting">
          {{ isSubmitting ? 'Creating...' : 'Create Link Hub' }}
        </button>
      </div>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
}

.back-btn {
  appearance: none;
  border: none;
  height: 38px;
  width: 38px;
  padding: 0;
  border-radius: 10px;
  background: rgba(239, 68, 68, 0.12);
  border: 1px solid rgba(239, 68, 68, 0.25);
  box-shadow: 0 4px 16px rgba(239, 68, 68, 0.2);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.back-btn ion-icon {
  font-size: 22px;
  padding-bottom: 0;
  color: rgba(239, 68, 68, 1);
  flex-shrink: 0;
}

.back-btn:hover {
  box-shadow: 0 8px 24px rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.18);
}

.back-btn:active {
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.15);
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
  justify-content: space-between;
  gap: 10px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
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

.add-link-btn {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.3);
  background: rgba(59, 130, 246, 0.15);
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
  flex-shrink: 0;
}

.add-link-btn ion-icon {
  font-size: 18px;
}

.add-link-btn:hover {
  background: rgba(59, 130, 246, 0.25);
  border-color: rgba(59, 130, 246, 0.4);
  box-shadow: 0 0 12px rgba(59, 130, 246, 0.2);
}

.add-link-btn:active {
  transform: scale(0.98);
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
  min-height: 100px;
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

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  margin: 0;
  font-size: 0.9rem;
  color: #9aa3ad;
}

.copy-url-btn ion-icon {
  font-size: 18px;
}

.add-link-btn {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.3);
  background: rgba(59, 130, 246, 0.15);
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
  flex-shrink: 0;
}

.add-link-btn ion-icon {
  font-size: 18px;
}

.add-link-btn:hover {
  background: rgba(59, 130, 246, 0.25);
  border-color: rgba(59, 130, 246, 0.4);
  box-shadow: 0 0 12px rgba(59, 130, 246, 0.2);
}

.add-link-btn:active {
  transform: scale(0.98);
}

.empty-links {
  text-align: center;
  padding: 3rem 1.5rem;
  background: rgba(255, 255, 255, 0.02);
  border: 2px dashed rgba(255, 255, 255, 0.1);
  border-radius: 12px;
}

.empty-links p {
  margin: 0;
  font-size: 0.95rem;
  color: #9aa3ad;
}

.links-grid {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.link-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.collapsed-icon ion-icon {
  font-size: 20px;
  color: #60a5fa;
}

.link-card {
  position: relative;
  padding: 1.25rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  transition: all 0.2s ease;
}

.link-card.expanded {
  animation: expandCard 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes expandCard {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.link-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.15);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.platform-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.platform-option {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.05);
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.platform-option ion-icon {
  font-size: 24px;
  color: #9aa3ad;
}

.platform-option span {
  font-size: 1.1rem;
  font-weight: 700;
  color: #9aa3ad;
}

.platform-option:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.2);
}

.platform-option.active {
  background: rgba(59, 130, 246, 0.2);
  border-color: rgba(59, 130, 246, 0.4);
}

.platform-option.active ion-icon,
.platform-option.active span {
  color: #60a5fa;
}

.url-input-wrapper {
  display: flex;
  align-items: center;
  border: 1px solid rgba(59, 130, 246, 0.2);
  background: rgba(13, 27, 42, 0.4);
  border-radius: 12px;
  overflow: hidden;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;
}

.url-input-wrapper:hover {
  border-color: rgba(59, 130, 246, 0.35);
  background: rgba(13, 27, 42, 0.6);
}

.url-input-wrapper:focus-within {
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow:
    0 0 0 6px rgba(59, 130, 246, 0.1),
    0 8px 24px rgba(0, 0, 0, 0.25);
  background: rgba(13, 27, 42, 0.7);
}

.url-prefix {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 12px;
  background: rgba(59, 130, 246, 0.08);
  border-right: 1px solid rgba(59, 130, 246, 0.2);
  flex-shrink: 0;
  height: 44px;
}

.url-prefix-icon {
  font-size: 18px;
  color: #60a5fa;
}

.url-input-wrapper input {
  flex: 1;
  appearance: none;
  outline: none;
  border: 0;
  background: transparent;
  color: #fff;
  padding: 12px 14px;
  font-size: 15px;
  font-family: inherit;
}

.url-input-wrapper input::placeholder {
  color: #7f7f7f;
}

.delete-link-btn {
  position: static;
  align-self: center;
  display: flex;
  align-items: center;
  height: 35px;
  width: 100%;
  justify-content: center;
  appearance: none;
  border: 1px solid rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.1);
  color: #f87171;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
  font-weight: 600;
}

.delete-link-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  box-shadow: 0 0 12px rgba(239, 68, 68, 0.2);
}

.delete-link-btn:active {
  transform: scale(0.95);
}

.public-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 0.75rem 0;
  text-shadow: 0 0 12px rgba(59, 130, 246, 0.3);
}

@media (max-width: 768px) {
  .form-header {
    padding: 1rem;
  }

  .back-btn {
    width: 42px;
    height: 42px;
  }

  .back-btn ion-icon {
    font-size: 1.4rem;
  }

  .header-title {
    font-size: 1.25rem;
  }

  .header-subtitle {
    font-size: 0.8rem;
  }

  .form-content {
    padding: 1.25rem;
    margin-top: 0.75rem;
    margin-bottom: 0.75rem;
  }

  .add-link-btn span {
    display: inline;
  }

  .link-card {
    padding: 1rem;
  }

  .platform-option {
    width: 44px;
    height: 44px;
  }

  .public-header h1 {
    font-size: 1.75rem;
  }

  .public-link-icon ion-icon {
    font-size: 24px;
  }

  .collapsed-icon ion-icon {
    font-size: 18px;
  }

  .url-prefix {
    padding: 0 10px;
  }
}
</style>
