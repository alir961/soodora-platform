<script setup lang="ts">
import { showToast } from '@/utils';
import { IonIcon, IonContent, onIonViewDidEnter } from '@ionic/vue';
import {
  getLinkHub,
  updateLinkHub,
  createLink,
  updateLink,
  deleteLink,
  deleteLinkHub,
  CreateLinkRequest,
  getLinkHubAnalytics,
} from '@/client/linkHubClient';
import { useRoute, useRouter } from 'vue-router';
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '@/store/reactiveStorage';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import DeleteCard from '@/component/actions/DeleteCard.vue';
import Header from '@/component/AppHeader.vue';
import {
  copyOutline,
  closeOutline,
  linkOutline,
  addCircleOutline,
  chevronForwardOutline,
  arrowBackOutline,
  eyeOutline,
  statsChart,
} from 'ionicons/icons';
import { iconOptions, platformIcons, platformUrlPrefixes } from '@/entity/Link';
import { LinkHubAnalytics } from '@/entity/LinkHub';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const pageSlug = computed(() => route.params.slug as string | undefined);
const isOwner = ref(false);

const linkHubAnalytics = ref<LinkHubAnalytics | null>(null);
const pageData = ref({
  title: '',
  description: '',
});

const originalPageData = ref({
  title: '',
  description: '',
});

type EditableLink = CreateLinkRequest & { slug?: string };
const links = ref<EditableLink[]>([]);
const originalLinks = ref<EditableLink[]>([]);
const editingLinkIndex = ref<number | null>(null);
const deletingLinkIndex = ref<number | null>(null);
const isSubmitting = ref(false);

const deletePageCardRef = ref<InstanceType<typeof DeleteCard> | null>(null);
const deleteLinkCardRef = ref<InstanceType<typeof DeleteCard> | null>(null);

const pageUrl = computed(() => {
  if (!pageSlug.value) return '';
  const baseUrl = import.meta.env.VITE_API_URL?.replace('/api/', '') || window.location.origin;
  return `${baseUrl}/linkhub/${pageSlug.value}`;
});

const isHubInfoValid = computed(() => pageData.value.title.trim() && pageData.value.description.trim());

const hasPageDataChanged = computed(() => {
  return (
    pageData.value.title !== originalPageData.value.title ||
    pageData.value.description !== originalPageData.value.description
  );
});

const hasLinksChanged = computed(() => {
  if (links.value.length !== originalLinks.value.length) return true;
  return links.value.some((link, idx) => {
    const original = originalLinks.value[idx];
    if (!original) return true;
    return (
      link.title !== original.title ||
      link.description !== original.description ||
      link.url !== original.url ||
      link.iconUrl !== original.iconUrl
    );
  });
});

const hasChanges = computed(() => hasPageDataChanged.value || hasLinksChanged.value);

function addLink() {
  links.value.push({ title: '', description: '', url: '', iconUrl: 'other' });
  editingLinkIndex.value = links.value.length - 1;
}

function toggleEditLink(index: number) {
  editingLinkIndex.value = editingLinkIndex.value === index ? null : index;
}

function updateLinkUrl(link: EditableLink, platform: string) {
  link.iconUrl = platform;
  const prefix = platformUrlPrefixes[platform];
  if (!link.url || Object.values(platformUrlPrefixes).some((p) => p && link.url.startsWith(p))) {
    link.url = prefix;
  }
}

function isLinkValid(link: EditableLink) {
  return !!(link.title.trim() && link.description.trim() && link.url.trim());
}

async function loadPage() {
  if (!pageSlug.value) return;
  try {
    const page = await getLinkHub(pageSlug.value);

    linkHubAnalytics.value = await getLinkHubAnalytics(pageSlug.value);
    pageData.value.title = page.title;
    pageData.value.description = page.description;
    originalPageData.value.title = page.title;
    originalPageData.value.description = page.description;

    links.value = (page.links || []).map((link) => ({
      slug: link.slug,
      title: link.title,
      description: link.description,
      url: link.url,
      iconUrl: link.iconUrl,
    }));

    originalLinks.value = JSON.parse(JSON.stringify(links.value));
    isOwner.value = authStore.getAuthenticatedUserSlug === page.owner.slug;
  } catch (err) {
    console.error(err);
    await showToast('Unable to load the Link Hub.', 'danger');
  }
}

async function submitUpdate() {
  if (!pageSlug.value || isSubmitting.value) return;

  isSubmitting.value = true;
  try {
    await updateLinkHub(pageSlug.value, {
      title: pageData.value.title,
      description: pageData.value.description,
    });

    for (const link of links.value) {
      if (link.slug) {
        await updateLink(pageSlug.value, link.slug, link);
      } else {
        const createdSlug = await createLink(pageSlug.value, link);
        link.slug = createdSlug;
      }
    }

    // Collapse the editing link and update original data
    editingLinkIndex.value = null;
    originalPageData.value.title = pageData.value.title;
    originalPageData.value.description = pageData.value.description;
    originalLinks.value = JSON.parse(JSON.stringify(links.value));
  } catch (err) {
    console.error(err);
    await showToast('Failed to update Link Hub.', 'danger');
  } finally {
    isSubmitting.value = false;
  }
}

function copyPageUrl() {
  if (!pageSlug.value) return;
  navigator.clipboard.writeText(pageUrl.value);
  showToast('Link Hub URL copied to clipboard !', 'success');
}

async function goToPublicLinkHub() {
  if (!pageSlug.value) return;
  await router.push(`/linkhub/${pageSlug.value}`);
}

function handleBack() {
  router.back();
}

const deletePage = () => deletePageCardRef.value?.open();
const submitLinkDeletion = (index: number) => {
  deletingLinkIndex.value = index;
  deleteLinkCardRef.value?.open();
};

async function handleLinkDeleted() {
  if (deletingLinkIndex.value !== null) {
    links.value.splice(deletingLinkIndex.value, 1);
    deletingLinkIndex.value = null;
  }
}

async function deleteLinkFunction() {
  if (deletingLinkIndex.value !== null && pageSlug.value) {
    const link = links.value[deletingLinkIndex.value];
    if (link.slug) await deleteLink(pageSlug.value, link.slug);
  }
}

async function handlePageDeleted() {
  await router.push('/profile');
}

onIonViewDidEnter(loadPage);
onMounted(loadPage);
</script>

<template>
  <ApplicationViewport title="Link Page">
    <ion-content class="form-content-wrapper">
      <div v-if="isOwner">
        <Header
          title="Edit Link Hub"
          :subtitle="pageData.title || 'Untitled'"
          :icon="arrowBackOutline"
          :on-icon-click="handleBack">
          <template #actions>
            <button class="visit-btn" @click="goToPublicLinkHub">
              <ion-icon :icon="eyeOutline" />
              <span>Visit</span>
            </button>
          </template>
        </Header>

        <div class="form-content">
          <div class="url-section">
            <div class="url-display">
              <span class="url-label">Your Link Hub URL:</span>
              <div class="url-box">
                <span class="url-text">{{ pageUrl }}</span>
                <button class="copy-url-btn" @click="copyPageUrl" title="Copy URL">
                  <ion-icon :icon="copyOutline" />
                </button>
              </div>
            </div>
          </div>

          <div class="form-section">
            <div class="section-header">
              <ion-icon :icon="linkOutline" class="section-icon" />
              <h3>Hub Information</h3>
            </div>

            <div class="form-field">
              <label>Hub Title <span class="required">*</span></label>
              <input type="text" v-model="pageData.title" placeholder="Link Hub title" required />
            </div>

            <div class="form-field">
              <label>Description <span class="required">*</span></label>
              <textarea v-model="pageData.description" placeholder="Link Hub description" rows="4" required></textarea>
            </div>
          </div>

          <div class="form-section">
            <div class="section-header">
              <div class="header-left">
                <ion-icon :icon="linkOutline" class="section-icon" />
                <h3>Your Link Hub</h3>
              </div>
              <button class="add-link-btn" @click="addLink">
                <ion-icon :icon="addCircleOutline" />
                <span>Add Link</span>
              </button>
            </div>

            <div v-if="links.length === 0" class="empty-links">
              <p>No links yet. Add your first link to get started!</p>
            </div>

            <div v-else class="links-grid">
              <div v-for="(link, idx) in links" :key="idx" class="link-wrapper">
                <div v-if="editingLinkIndex !== idx" class="link-collapsed-bar" @click="toggleEditLink(idx)">
                  <div class="collapsed-icon">
                    <ion-icon v-if="platformIcons[link.iconUrl]" :src="platformIcons[link.iconUrl]" />
                    <ion-icon v-else :icon="linkOutline" />
                  </div>
                  <div class="collapsed-info">
                    <span class="collapsed-title">{{ link.title || 'Untitled Link' }}</span>
                    <span class="collapsed-url">{{ link.url || 'No URL set' }}</span>
                  </div>
                  <ion-icon :icon="chevronForwardOutline" class="collapsed-arrow" />
                </div>

                <div v-else class="link-card expanded">
                  <div class="link-card-top-row">
                    <div class="link-card-mode-wrapper">
                      <span class="link-card-mode">
                        {{ link.slug ? 'Update Link' : 'Create Link' }}
                      </span>
                    </div>
                    <button class="cancel-link-btn" @click="toggleEditLink(idx)">
                      <ion-icon :icon="closeOutline" />
                      Close
                    </button>
                  </div>

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

                  <div class="link-actions">
                    <button class="save-link-btn" @click="submitUpdate" :disabled="!isLinkValid(link) || isSubmitting">
                      {{ isSubmitting ? 'Saving...' : link.slug ? 'Save Changes' : 'Create Link' }}
                    </button>
                    <button class="delete-link-btn" @click="submitLinkDeletion(idx)" title="Delete link">
                      Delete Link
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="analytics-section" v-if="linkHubAnalytics">
            <div class="section-header">
              <div class="header-left">
                <ion-icon :icon="statsChart" class="section-icon" />
                <h3>Analytics</h3>
              </div>
            </div>

            <div class="analytics-grid">
              <div class="analytics-box">
                <span class="analytics-label">Total Views</span>
                <span class="analytics-value">{{ linkHubAnalytics.viewCount }}</span>
              </div>
            </div>
          </div>

          <div class="action-buttons">
            <button class="submit-btn" @click="submitUpdate" :disabled="!isHubInfoValid || !hasChanges || isSubmitting">
              {{ isSubmitting ? 'Saving...' : 'Save Changes' }}
            </button>
            <button class="danger-btn" @click="deletePage">Delete Link Hub</button>
          </div>
        </div>
      </div>
    </ion-content>

    <DeleteCard
      item-type="link hub"
      :item-slug="pageSlug || 'page'"
      :delete-function="deleteLinkHub"
      @deleted="handlePageDeleted"
      ref="deletePageCardRef" />

    <DeleteCard
      item-type="link"
      :item-slug="deletingLinkIndex !== null ? (links[deletingLinkIndex]?.slug ?? 'new-link') : 'new-link'"
      :delete-function="deleteLinkFunction"
      @deleted="handleLinkDeleted"
      ref="deleteLinkCardRef" />
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
}

.back-btn,
.visit-btn {
  appearance: none;
  border: none;
  height: 38px;
  padding: 0;
  border-radius: 10px;
  background: linear-gradient(135deg, #253a60e0 30%, #1e40af90 100%);
  box-shadow: 0 4px 16px rgba(30, 41, 59, 0.18);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #fff;
}

.back-btn {
  width: 38px;
}

.visit-btn {
  padding: 0 1rem;
  gap: 0.5rem;
  font-size: 0.9rem;
  font-weight: 500;
}

.back-btn ion-icon,
.visit-btn ion-icon {
  font-size: 22px;
  padding-bottom: 0;
  color: #fff;
  flex-shrink: 0;
}

.back-btn:hover,
.visit-btn:hover {
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.25);
  background: linear-gradient(135deg, #1e40af 60%, #1e293b 100%);
  transform: translateY(-2px);
}

.back-btn:active,
.visit-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(30, 41, 59, 0.12);
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
  justify-content: flex-start;
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

.url-section {
  padding: 1.25rem;
  background: rgba(59, 130, 246, 0.08);
  border: 1px solid rgba(59, 130, 246, 0.2);
  border-radius: 14px;
}

.url-display {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.url-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #9aa3ad;
}

.url-box {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  background: rgba(13, 27, 42, 0.6);
  border: 1px solid rgba(59, 130, 246, 0.25);
  border-radius: 10px;
}

.url-text {
  flex: 1;
  font-size: 0.9rem;
  color: #60a5fa;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: 'Courier New', monospace;
}

.copy-url-btn {
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
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.copy-url-btn ion-icon {
  font-size: 18px;
}

.copy-url-btn:active {
  background: rgba(59, 130, 246, 0.25);
  border-color: rgba(59, 130, 246, 0.4);
  transform: scale(1.05);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
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

.link-collapsed-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.link-collapsed-bar:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateX(2px);
}

.link-collapsed-bar:active {
  transform: scale(0.98);
}

.collapsed-icon {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.collapsed-icon ion-icon {
  font-size: 20px;
  color: #60a5fa;
}

.collapsed-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.collapsed-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #ffffff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.collapsed-url {
  font-size: 0.8rem;
  color: #9aa3ad;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: 'Courier New', monospace;
}

.collapsed-arrow {
  font-size: 18px;
  color: #60a5fa;
  flex-shrink: 0;
  transition: transform 0.2s ease;
}

.link-collapsed-bar:hover .collapsed-arrow {
  transform: translateX(3px);
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

.link-card-top-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.link-card-mode-wrapper {
  display: flex;
  align-items: center;
  height: 40px;
}

.link-card-mode {
  font-size: 1.2rem;
  font-weight: 600;
  color: #ffffff;
  white-space: nowrap;
  display: flex;
  align-items: center;
  height: 40px;
}

.cancel-link-btn {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.05);
  color: #9aa3ad;
  width: auto;
  height: 30px;
  padding: 0 12px;
  border-radius: 10px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cancel-link-btn ion-icon {
  font-size: 15px;
  margin-right: 6px;
}

.cancel-link-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.2);
  color: #ffffff;
}

.cancel-link-btn:active {
  transform: scale(0.98);
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

.link-actions {
  display: flex;
  gap: 10px;
  margin-top: 8px;
}

.save-link-btn {
  height: 35px;
  border: 0;
  border-radius: 10px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 700;
  transition: all 0.2s ease;
}

.save-link-btn {
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  color: #ffffff;
  box-shadow:
    0 8px 20px rgba(37, 99, 235, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
}

.save-link-btn:hover:not(:disabled) {
  box-shadow:
    0 10px 24px rgba(37, 99, 235, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.16);
  transform: translateY(-1px);
}

.save-link-btn:active:not(:disabled) {
  transform: translateY(0);
}

.save-link-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: linear-gradient(135deg, #374151, #4b5563);
  box-shadow: none;
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

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 0.5rem;
}

.danger-btn {
  width: 100%;
  height: 48px;
  border: 1px solid rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.1);
  color: #f87171;
  font-weight: 700;
  font-size: 1rem;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.danger-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  box-shadow: 0 0 16px rgba(239, 68, 68, 0.3);
}

.danger-btn:active {
  transform: scale(0.98);
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

  .url-section {
    padding: 1rem;
  }

  .url-box {
    padding: 10px 12px;
  }

  .copy-url-btn {
    width: 32px;
    height: 32px;
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

  .link-collapsed-bar {
    padding: 12px 14px;
  }

  .collapsed-icon {
    width: 36px;
    height: 36px;
  }

  .collapsed-icon ion-icon {
    font-size: 18px;
  }

  .collapsed-title {
    font-size: 0.9rem;
  }

  .collapsed-url {
    font-size: 0.75rem;
  }

  .url-prefix {
    padding: 0 10px;
  }

  .link-actions {
    flex-direction: column;
  }
}

.analytics-grid {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.analytics-box {
  flex: 1;
  min-width: 150px;
  background: rgba(255, 255, 255, 0.04);
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  backdrop-filter: blur(4px);
  margin-top: 12px;
}

.analytics-label {
  display: block;
  font-size: 0.9rem;
  opacity: 0.7;
  margin-bottom: 4px;
}

.analytics-value {
  font-size: 1.8rem;
  font-weight: 700;
}
</style>
