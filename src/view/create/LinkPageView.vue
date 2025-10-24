<script setup lang="ts">
import { showToast } from '@/utils';
import { IonContent, onIonViewDidEnter, IonIcon } from '@ionic/vue';
import {
  createLinkPage,
  getLinkPage,
  updateLinkPage,
  deleteLinkPage,
  createLink,
  updateLink,
  deleteLink,
  getUserLinkPage,
  CreateLinkPageRequest,
  CreateLinkRequest,
} from '@/client/linkPageClient';
import { Link } from '@/entity/Link';
import { copyOutline, closeOutline, linkOutline, addCircleOutline, chevronForwardOutline } from 'ionicons/icons';
import { ref, computed, watch } from 'vue';
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import instagramIcon from '@/assets/icons/instagram.svg';
import xIcon from '@/assets/icons/x.svg';
import youtubeIcon from '@/assets/icons/youtube.svg';
import soundcloudIcon from '@/assets/icons/soundcloud.svg';
import tiktokIcon from '@/assets/icons/tiktok.svg';
import DeleteCard from '@/component/actions/DeleteCard.vue';

const platformIcons: Record<string, string> = {
  instagram: instagramIcon,
  x: xIcon,
  youtube: youtubeIcon,
  soundcloud: soundcloudIcon,
  tiktok: tiktokIcon,
  other: linkOutline,
};

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const pageSlug = computed(() => route.params.slug as string | undefined);
const isCreateMode = computed(() => !pageSlug.value);

const pageData = ref({
  title: '',
  description: '',
});

const checkedOwnership = ref(false);

type EditableLink = CreateLinkRequest & { slug?: string };
const links = ref<EditableLink[]>([]);
const isOwner = ref(false);
const activeNewLinkIndex = ref<number | null>(null);
const editingLinkIndex = ref<number | null>(null);

const iconOptions = [
  { label: 'Instagram', value: 'instagram' },
  { label: 'X', value: 'x' },
  { label: 'YouTube', value: 'youtube' },
  { label: 'SoundCloud', value: 'soundcloud' },
  { label: 'TikTok', value: 'tiktok' },
  { label: 'Other', value: 'other' },
];

const platformUrlPrefixes: Record<string, string> = {
  instagram: 'https://www.instagram.com/',
  x: 'https://x.com/',
  youtube: 'https://www.youtube.com/',
  soundcloud: 'https://soundcloud.com/',
  tiktok: 'https://www.tiktok.com/@',
  other: '',
};

function addLink() {
  const newIndex = links.value.length;
  links.value.push({
    title: '',
    description: '',
    url: '',
    iconUrl: 'other',
  });
  editingLinkIndex.value = newIndex;
  activeNewLinkIndex.value = newIndex;
}

function toggleEditLink(index: number) {
  if (editingLinkIndex.value === index) {
    editingLinkIndex.value = null;
  } else {
    editingLinkIndex.value = index;
  }
}

function updateLinkUrl(link: EditableLink, platform: string) {
  link.iconUrl = platform;

  const currentPrefix = platformUrlPrefixes[platform];
  if (!link.url || Object.values(platformUrlPrefixes).some((prefix) => prefix && link.url.startsWith(prefix))) {
    link.url = currentPrefix;
  }
}

function isLinkValid(link: EditableLink): boolean {
  return !!(link.title.trim() && link.description.trim() && link.url.trim());
}

async function submitCreate() {
  try {
    const pageCommand: CreateLinkPageRequest = {
      title: pageData.value.title,
      description: pageData.value.description,
    };
    const createdLinkPageSlug = await createLinkPage(pageCommand);

    for (const link of links.value) {
      await createLink(createdLinkPageSlug, link);
    }

    await showToast('Succeed to create Link Hub.');
    await router.push(`/linkpages/${createdLinkPageSlug}`);
  } catch (error: unknown) {
    const message = 'Unable to create Link Hub. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
}

const pageUrl = computed(() => {
  if (!pageSlug.value) return '';
  return `${window.location.origin}/linkpages/${pageSlug.value}`;
});

async function loadPage() {
  if (!pageSlug.value || isCreateMode.value) {
    return;
  }
  try {
    const page = await getLinkPage(pageSlug.value);
    pageData.value.title = page.title;
    pageData.value.description = page.description;
    links.value = (page.links || []).map((link: Link) => ({
      slug: link.slug,
      title: link.title,
      description: link.description,
      url: link.url,
      iconUrl: link.iconUrl,
    }));

    isOwner.value = authStore.getAuthenticatedUserSlug === page.owner.slug;
  } catch (error: unknown) {
    const message = 'Unable to load the Link Hub.' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
    console.error('Failed to load hub:', error);
  }
}

async function submitUpdate() {
  if (!pageSlug.value) return;
  try {
    await updateLinkPage(pageSlug.value, {
      title: pageData.value.title,
      description: pageData.value.description,
    });

    for (const link of links.value) {
      if ((link as Link).slug) {
        await updateLink(pageSlug.value, (link as Link).slug, link);
      } else {
        await createLink(pageSlug.value, link);
      }
    }

    await showToast('Succeed to update Link Hub.');
  } catch (error: unknown) {
    const message = 'Unable to update the Link Hub.' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
}

const deletingLinkIndex = ref<number | null>(null);

const deletePageCardRef = ref<InstanceType<typeof DeleteCard> | null>(null);
const deleteLinkCardRef = ref<InstanceType<typeof DeleteCard> | null>(null);

const deletePage = () => {
  deletePageCardRef.value?.open();
};

const handlePageDeleted = async () => {
  await router.push('/linkpages');
};

const submitLinkDeletion = (index: number) => {
  deletingLinkIndex.value = index;
  deleteLinkCardRef.value?.open();
};

const handleLinkDeleted = async () => {
  if (deletingLinkIndex.value !== null) {
    links.value.splice(deletingLinkIndex.value, 1);
    deletingLinkIndex.value = null;
  }
};

const deleteLinkFunction = async () => {
  if (deletingLinkIndex.value !== null) {
    const link = links.value[deletingLinkIndex.value];
    if (pageSlug.value && link.slug) {
      await deleteLink(pageSlug.value, link.slug);
    }
  }
};

async function checkIfUserHasLinkPage() {
  try {
    const userSlug = authStore.getAuthenticatedUserSlug;

    if (!userSlug) {
      isOwner.value = false;
      checkedOwnership.value = true;
      return;
    }

    if (!pageSlug.value) {
      const userPage = await getUserLinkPage(userSlug);

      if (userPage) {
        await router.push(`/linkpages/${userPage.slug}`);
        checkedOwnership.value = true;
        return;
      }
      isOwner.value = false;
    } else {
      const page = await getLinkPage(pageSlug.value);
      isOwner.value = userSlug === page.owner.slug;
    }
    checkedOwnership.value = true;
  } catch (error) {
    console.error('Error checking ownership:', error);
    isOwner.value = false;
    checkedOwnership.value = true;
  }
}

function copyPageUrl() {
  if (pageSlug.value) {
    const url = `${window.location.origin}/linkpages/${pageSlug.value}`;
    navigator.clipboard.writeText(url);
    showToast('URL copied to clipboard!', 'success');
  }
}

onIonViewDidEnter(async () => {
  await checkIfUserHasLinkPage();
  await loadPage();
});

onMounted(async () => {
  await checkIfUserHasLinkPage();
  await loadPage();
});

watch(
  () => route.fullPath,
  (newPath) => {
    if (newPath === '/linkpages' && isCreateMode.value) {
      checkIfUserHasLinkPage();
    }
  },
  { immediate: true }
);

watch(
  () => route.params.slug,
  (newSlug) => {
    if (!newSlug) {
      resetData();
    }
  }
);

function resetData() {
  pageData.value.title = '';
  pageData.value.description = '';
  links.value = [];
  isOwner.value = false;
  editingLinkIndex.value = null;
}

const isHubInfoValid = computed(() => pageData.value.title.trim() && pageData.value.description.trim());
</script>

<template>
  <ApplicationViewport title="Link Page">
    <ion-content class="form-content-wrapper">
      <div v-if="isCreateMode">
        <div class="form-header">
          <h2>Create Link Hub</h2>
          <button class="close-btn" @click="$router.back()">
            <ion-icon :icon="closeOutline" />
            Close
          </button>
        </div>

        <div class="form-content">
          <div class="form-section">
            <div class="section-header">
              <ion-icon :icon="linkOutline" class="section-icon" />
              <h3>Your Link Hub</h3>
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

          <button
            class="submit-btn"
            @click="submitCreate"
            :disabled="!pageData.title.trim() || !pageData.description.trim()">
            Create Link Hub
          </button>
        </div>
      </div>

      <div v-else>
        <div v-if="!checkedOwnership" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Loading...</p>
        </div>

        <div v-else>
          <div v-if="isOwner">
            <div class="form-header">
              <h2>Edit Link Hub</h2>
              <button class="close-btn" @click="$router.back()">
                <ion-icon :icon="closeOutline" />
                Close
              </button>
            </div>

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
                  <textarea
                    v-model="pageData.description"
                    placeholder="Link Hub description"
                    rows="4"
                    required></textarea>
                </div>
              </div>

              <div class="form-section">
                <div class="section-header">
                  <div class="header-left">
                    <ion-icon :icon="linkOutline" class="section-icon" />
                    <h3>Your Links</h3>
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
                            :placeholder="
                              link.iconUrl === 'other' ? 'https://...' : platformUrlPrefixes[link.iconUrl]
                            " />
                        </div>
                      </div>

                      <div class="link-actions">
                        <button class="save-link-btn" @click="submitUpdate" :disabled="!isLinkValid(link)">
                          {{ link.slug ? 'Save Changes' : 'Create Link' }}
                        </button>
                        <button class="delete-link-btn" @click="submitLinkDeletion(idx)" title="Delete link">
                          Delete Link
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="action-buttons">
                <button class="submit-btn" @click="submitUpdate" :disabled="!isHubInfoValid">Save Changes</button>
                <button class="danger-btn" @click="deletePage">Delete Link Hub</button>
              </div>
            </div>
          </div>

          <div v-else class="public-view">
            <div class="public-header">
              <h1>{{ pageData.title }}</h1>
              <p class="public-description">{{ pageData.description }}</p>
            </div>

            <div class="public-links">
              <a
                v-for="(link, idx) in links"
                :key="idx"
                :href="link.url.startsWith('http') ? link.url : 'https://' + link.url"
                target="_blank"
                rel="noopener noreferrer"
                class="public-link-card">
                <div class="public-link-icon">
                  <ion-icon v-if="platformIcons[link.iconUrl]" :src="platformIcons[link.iconUrl]" />
                  <ion-icon v-else :icon="linkOutline" />
                </div>
                <div class="public-link-info">
                  <h3>{{ link.title }}</h3>
                  <p v-if="link.description">{{ link.description }}</p>
                </div>
                <ion-icon :icon="chevronForwardOutline" class="public-link-arrow" />
              </a>
            </div>
          </div>
        </div>
      </div>
    </ion-content>

    <DeleteCard
      item-type="link hub"
      :item-slug="pageSlug || 'page'"
      :delete-function="deleteLinkPage"
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

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 1rem;
  gap: 1rem;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: #60a5fa;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
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

.copy-url-btn:hover {
  background: rgba(59, 130, 246, 0.25);
  border-color: rgba(59, 130, 246, 0.4);
  transform: scale(1.05);
}

.copy-url-btn:active {
  transform: scale(0.95);
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

.public-view {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
}

.public-header {
  text-align: center;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.public-header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 0.75rem 0;
  text-shadow: 0 0 12px rgba(59, 130, 246, 0.3);
}

.public-description {
  font-size: 1.1rem;
  color: #9aa3ad;
  margin: 0;
  line-height: 1.6;
}

.public-links {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.public-link-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.25rem;
  background: linear-gradient(135deg, rgba(13, 27, 42, 0.6), rgba(9, 18, 30, 0.5));
  border: 1px solid rgba(59, 130, 246, 0.15);
  border-radius: 16px;
  text-decoration: none;
  transition: all 0.25s ease;
  cursor: pointer;
}

.public-link-card:hover {
  background: linear-gradient(135deg, rgba(13, 27, 42, 0.8), rgba(9, 18, 30, 0.7));
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.public-link-card:active {
  transform: translateY(0);
}

.public-link-icon {
  width: 56px;
  height: 56px;
  flex-shrink: 0;
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.public-link-icon ion-icon {
  font-size: 28px;
  color: #60a5fa;
}

.public-link-info {
  flex: 1;
  min-width: 0;
}

.public-link-info h3 {
  margin: 0 0 4px 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #ffffff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.public-link-info p {
  margin: 0;
  font-size: 0.9rem;
  color: #9aa3ad;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.public-link-arrow {
  font-size: 20px;
  color: #60a5fa;
  flex-shrink: 0;
  transition: transform 0.2s ease;
}

.public-link-card:hover .public-link-arrow {
  transform: translateX(4px);
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

  .public-view {
    padding: 1.5rem 1rem;
  }

  .public-header h1 {
    font-size: 1.75rem;
  }

  .public-description {
    font-size: 1rem;
  }

  .public-link-icon {
    width: 48px;
    height: 48px;
  }

  .public-link-icon ion-icon {
    font-size: 24px;
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
</style>
