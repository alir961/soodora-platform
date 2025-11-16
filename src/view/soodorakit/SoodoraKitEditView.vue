<script setup lang="ts">
import { IonContent, IonIcon, IonInput, IonTextarea } from '@ionic/vue';
import {
  addCircleOutline,
  arrowBackOutline,
  cloudDownloadOutline,
  createOutline,
  documentTextOutline,
  imageOutline,
  musicalNoteOutline,
  saveOutline,
  statsChart,
  trashOutline,
  videocamOutline,
} from 'ionicons/icons';
import Header from '@/component/AppHeader.vue';
import { onMounted, ref, ref as vueRef, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  deleteSoodoraKit,
  deleteSoodoraKitItem,
  exportSoodoraKitPdf,
  getSoodoraKitArtistSlug,
  getMediaKitAnalyticsBySlug,
  getSoodoraKitBySlug,
  updateSoodoraKit,
} from '@/client/soodoraKitClient';
import { useAuthStore } from '@/store/reactiveStorage';
import type { MediaKitAnalytics, SoodoraKitDTO, SoodoraKitItemDTO } from '@/entity/SoodoraKit';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { showToast } from '@/utils';
import DeleteCard from '@/component/actions/DeleteCard.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const deleteCardRef = vueRef();
const deleteItemCardRefs = vueRef<Record<string, InstanceType<typeof DeleteCard>>>({});

const slug = ref<string | undefined>(route.params.slug as string | undefined);
const soodoraKit = ref<SoodoraKitDTO | null>(null);
const soodoraKitItems = ref<SoodoraKitItemDTO[]>([]);
const mediaKitAnalytics = ref<MediaKitAnalytics | null>(null);
const isLoading = ref(false);
const isSaving = ref(false);
const justSaved = ref(false);

const getItemTypeIcon = (type: string) => {
  switch (type) {
    case 'Image':
      return imageOutline;
    case 'Video':
      return videocamOutline;
    case 'Audio':
      return musicalNoteOutline;
    case 'Text':
      return documentTextOutline;
    default:
      return documentTextOutline;
  }
};

async function fetchSoodoraKit(slugToLoad: string) {
  isLoading.value = true;
  try {
    const fetchMediaKit = await getSoodoraKitBySlug(slugToLoad);
    const analytics = await getMediaKitAnalyticsBySlug(slugToLoad);

    soodoraKit.value = fetchMediaKit;
    soodoraKitItems.value = fetchMediaKit.items || [];
    mediaKitAnalytics.value = analytics;
  } catch (error: unknown) {
    const message = 'Failed to load SoodoraKit. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
}

const goBack = () => {
  (document.activeElement as HTMLElement)?.blur();
  router.push('/profile');
};

const saveSoodoraKit = async () => {
  if (!soodoraKit.value) return;
  isSaving.value = true;
  justSaved.value = false;
  try {
    await updateSoodoraKit(soodoraKit.value.slug, soodoraKit.value);
    justSaved.value = true;
    setTimeout(() => {
      justSaved.value = false;
    }, 2000);
  } catch (error: unknown) {
    const message = 'Failed to save SoodoraKit. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isSaving.value = false;
  }
};

const downloadPdfHandler = async () => {
  if (!soodoraKit.value) return;
  try {
    const blob = await exportSoodoraKitPdf(soodoraKit.value.slug);
    const url = URL.createObjectURL(blob);
    const link = Object.assign(document.createElement('a'), {
      href: url,
      download: `soodorakit_${soodoraKit.value.slug}.pdf`,
    });
    link.click();
    URL.revokeObjectURL(url);
  } catch (error: unknown) {
    const message = 'Failed to download PDF. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

const openAddItemModal = () => {
  (document.activeElement as HTMLElement)?.blur();
  router.push(`/soodorakits/${soodoraKit.value?.slug}/items/new`);
};

const openEditItemModal = (item: SoodoraKitItemDTO) => {
  (document.activeElement as HTMLElement)?.blur();
  router.push(`/soodorakits/${soodoraKit.value?.slug}/items/${item.slug}/edit`);
};

onMounted(async () => {
  if (slug.value) {
    await fetchSoodoraKit(slug.value);
  } else {
    const artistSlug = authStore.getAuthenticatedUserSlug;
    try {
      const existing = artistSlug ? await getSoodoraKitArtistSlug(artistSlug) : null;
      if (existing) await router.replace(`/soodorakits/${existing.slug}`);
    } catch (error: unknown) {
      const message = 'Redirection to Soodora Kit failed. ' + (error instanceof Error ? error.message : '');
      await showToast(message, 'danger');
    }
  }
});

watch(
  () => route.params.slug,
  async (newSlug) => {
    // Only fetch if we're actually on the soodorakit edit route
    if (route.path.startsWith('/soodorakits/') && route.name === 'SoodoraKitEdit') {
      slug.value = newSlug as string | undefined;
      if (slug.value) await fetchSoodoraKit(slug.value);
    }
  }
);

watch(
  () => route.query.t,
  async (newTimestamp) => {
    // Refresh items when returning from item form (timestamp query param changes)
    if (newTimestamp && slug.value && route.name === 'SoodoraKitEdit') {
      await fetchSoodoraKit(slug.value);
    }
  }
);
</script>

<template>
  <ApplicationViewport title="Soodora Kit">
    <IonContent class="soodorakit-content">
      <!-- Ambient Glow Background -->
      <div class="ambient-glow">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
      </div>

      <div v-if="isLoading" class="loading-container">
        <div class="loading-spinner"></div>
        <p class="loading-text">Loading your media kit...</p>
      </div>

      <div v-else-if="soodoraKit" class="soodorakit-wrapper">
        <!-- Header with Back Button and Actions -->
        <Header
          title="Soodora Kit"
          :subtitle="soodoraKit.title || 'Soodora Media Kit'"
          :icon="arrowBackOutline"
          :on-icon-click="goBack">
          <template #actions>
            <div class="header-actions">
              <button class="action-btn download-btn" @click="downloadPdfHandler">
                <IonIcon :icon="cloudDownloadOutline" />
                <span class="action-btn-text">PDF</span>
              </button>
              <button class="action-btn delete-btn" @click="deleteCardRef?.open()">
                <IonIcon :icon="trashOutline" />
              </button>
              <DeleteCard
                ref="deleteCardRef"
                item-type="link hub"
                :item-slug="soodoraKit.slug"
                :delete-function="deleteSoodoraKit"
                @deleted="router.push('/profile')" />
            </div>
          </template>
        </Header>

        <!-- Info Card -->
        <div class="info-card">
          <div class="info-header">
            <div class="info-icon-wrapper">
              <IonIcon :icon="documentTextOutline" class="info-icon" />
            </div>
            <h2 class="info-title">Soodora Kit Information</h2>
            <button class="save-btn" :class="{ saved: justSaved }" @click="saveSoodoraKit" :disabled="isSaving">
              <IonIcon :icon="saveOutline" />
              <span>{{ isSaving ? 'Saving...' : justSaved ? 'Saved!' : 'Save' }}</span>
            </button>
          </div>

          <!-- Basic Information Section -->
          <div class="info-section">
            <h3 class="section-title">Basic Information</h3>
            <div class="info-grid">
              <div class="info-item full-width">
                <span class="info-label">Title</span>
                <IonInput v-model="soodoraKit.title" placeholder="Enter soodora kit title" class="custom-input" />
              </div>
              <div class="info-item full-width">
                <span class="info-label">Short Bio</span>
                <IonTextarea
                  v-model="soodoraKit.shortBio"
                  :rows="3"
                  placeholder="Brief description..."
                  class="custom-textarea" />
              </div>
              <div class="info-item full-width">
                <span class="info-label">Long Bio</span>
                <IonTextarea
                  v-model="soodoraKit.longBio"
                  :rows="5"
                  placeholder="Detailed biography..."
                  class="custom-textarea" />
              </div>
            </div>
          </div>

          <!-- Contact Information Section -->
          <div class="info-section">
            <h3 class="section-title">Contact Information</h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">Email</span>
                <IonInput
                  v-model="soodoraKit.contactEmail"
                  type="email"
                  placeholder="contact@example.com"
                  class="custom-input" />
              </div>
              <div class="info-item">
                <span class="info-label">Phone</span>
                <IonInput
                  v-model="soodoraKit.contactPhone"
                  type="tel"
                  placeholder="+1 (555) 000-0000"
                  class="custom-input" />
              </div>
            </div>
          </div>
        </div>

        <!-- Media Items Section -->
        <div class="items-section">
          <div class="items-header">
            <div class="items-title-with-icon">
              <IonIcon :icon="imageOutline" class="items-title-icon" />
              <h2 class="items-title">Media Items</h2>
            </div>
            <button class="add-item-btn" @click="openAddItemModal" tabindex="0">
              <IonIcon :icon="addCircleOutline" />
              <span>Add Item</span>
            </button>
          </div>

          <!-- Items List -->
          <div v-if="soodoraKitItems.length" class="items-list">
            <div v-for="item in soodoraKitItems" :key="item.slug" class="item-card">
              <div class="item-content">
                <div class="item-header">
                  <div class="item-left">
                    <div class="item-icon-wrapper">
                      <IonIcon :icon="getItemTypeIcon(item.type)" class="item-type-icon" />
                    </div>
                    <div class="item-info">
                      <h3 class="item-title">{{ item.title }}</h3>
                      <span class="item-type-badge">{{ item.type }}</span>
                    </div>
                  </div>
                  <div class="item-actions">
                    <button class="item-action-btn edit" @click="openEditItemModal(item)">
                      <IonIcon :icon="createOutline" />
                    </button>
                    <button class="item-action-btn delete" @click="deleteItemCardRefs[item.slug]?.open()">
                      <IonIcon :icon="trashOutline" />
                    </button>
                    <DeleteCard
                      :ref="
                        (el) => {
                          if (el) deleteItemCardRefs[item.slug] = el as InstanceType<typeof DeleteCard>;
                        }
                      "
                      item-type="item"
                      :item-slug="item.slug"
                      :delete-function="deleteSoodoraKitItem"
                      @deleted="soodoraKitItems = soodoraKitItems.filter((i) => i.slug !== item.slug)" />
                  </div>
                </div>

                <p v-if="item.content" class="item-description">{{ item.content }}</p>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div v-else class="empty-items">
            <div class="empty-icon-wrapper">
              <IonIcon :icon="imageOutline" class="empty-icon" />
            </div>
            <h3 class="empty-title">No Media Items Yet</h3>
            <p class="empty-description">Add images, videos, audio or text content</p>
          </div>
        </div>

        <div class="analytics-container" v-if="mediaKitAnalytics">
          <IonCard class="analytics-card">
            <IonCardHeader class="section-header">
              <div class="section-header-left">
                <IonIcon :icon="statsChart" class="section-header-icon" />
                <IonCardTitle class="section-header-title">Analytics</IonCardTitle>
              </div>
              <IonCardSubtitle class="section-header-subtitle">Soodora Kit Performance</IonCardSubtitle>
            </IonCardHeader>

            <IonCardContent>
              <div class="analytics-grid">
                <div class="analytics-box">
                  <span class="analytics-label">Total Downloads</span>
                  <span class="analytics-value">{{ mediaKitAnalytics.downloadCount }}</span>
                </div>

                <div class="analytics-box">
                  <span class="analytics-label">Follower Downloads</span>
                  <span class="analytics-value">{{ mediaKitAnalytics.subscriberDownloadCount }}</span>
                </div>
              </div>
            </IonCardContent>
          </IonCard>
        </div>
      </div>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
/* Ambient Background */
.ambient-glow {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 1;
  animation: glowFloat 10s ease-in-out infinite;
}

.glow-orb.orb-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.25) 0%, rgba(59, 130, 246, 0.08) 50%, transparent 100%);
  top: -150px;
  left: -150px;
}

.glow-orb.orb-2 {
  width: 450px;
  height: 450px;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.2) 0%, rgba(96, 165, 250, 0.06) 50%, transparent 100%);
  bottom: -100px;
  right: -100px;
}

.glow-orb.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.18) 0%, rgba(37, 99, 235, 0.05) 50%, transparent 100%);
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
}

@keyframes glowFloat {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(20px, 20px) scale(1.05);
  }
}

/* Content */
.soodorakit-content {
  --background: transparent;
  position: relative;
}

.soodorakit-wrapper {
  position: relative;
  z-index: 1;
  padding-bottom: 100px;
}

/* Loading State */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 1rem;
  position: relative;
  z-index: 1;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(59, 130, 246, 0.2);
  border-top-color: rgba(59, 130, 246, 1);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  color: rgba(255, 255, 255, 0.7);
  font-size: 1rem;
}

/* Header */
/* Header styles removed - now using Header component */

.header-actions {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  flex-shrink: 0;
}

.action-btn {
  appearance: none;
  border: none;
  height: 38px;
  padding: 0 1rem;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: white;
  font-size: 0.875rem;
  font-weight: 600;
}

.action-btn ion-icon {
  font-size: 20px;
}

.action-btn-text {
  white-space: nowrap;
}

.download-btn {
  background: linear-gradient(135deg, #253a60e0 30%, #1e40af90 100%);
  border-color: rgba(59, 130, 246, 0.3);
  color: #fff;
  box-shadow: 0 4px 16px rgba(30, 41, 59, 0.18);
}

.download-btn:hover {
  background: linear-gradient(135deg, #1e40af 60%, #1e293b 100%);
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.25);
  transform: translateY(-2px);
}

.download-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(30, 41, 59, 0.12);
}

.delete-btn {
  color: rgba(239, 68, 68, 1);
  width: 38px;
  padding: 0;
  justify-content: center;
}

.delete-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: scale(1.05);
}

.delete-btn:active {
  transform: scale(0.95);
}

/* Info Card */
.info-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 1rem;
  padding: 1.25rem;
  margin: 1.2rem 1rem 1.2rem 1rem;
}

.info-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.25rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  flex-wrap: wrap;
}

.info-icon-wrapper {
  width: 44px;
  height: 44px;
  flex-shrink: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(37, 99, 235, 0.1));
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.info-icon {
  font-size: 1.5rem;
  color: rgba(59, 130, 246, 1);
}

.info-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: white;
  margin: 0;
  flex: 1;
}

.save-btn {
  appearance: none;
  border: none;
  height: 36px;
  padding: 0 1rem;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  font-weight: 600;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  color: #fff;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.25);
  margin-left: auto;
}

.save-btn ion-icon {
  font-size: 18px;
}

.save-btn:hover:not(:disabled) {
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.35);
  transform: translateY(-1px);
}

.save-btn:active:not(:disabled) {
  transform: translateY(0);
}

.save-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  background: linear-gradient(135deg, #374151, #4b5563);
  box-shadow: none;
}

.save-btn.saved {
  background: linear-gradient(135deg, #059669, #10b981 55%, #34d399);
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.25);
}

.save-btn.saved:hover {
  background: linear-gradient(135deg, #059669, #10b981 55%, #34d399);
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.35);
}

.info-section {
  margin-bottom: 2rem;
}

.info-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 1rem 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-size: 0.85rem;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.custom-input,
.custom-textarea {
  --background: rgba(255, 255, 255, 0.05);
  --color: rgba(255, 255, 255, 0.9);
  --placeholder-color: rgba(255, 255, 255, 0.4);
  --border-radius: 12px;
  --padding-start: 14px;
  --padding-end: 14px;
  --padding-top: 12px;
  --padding-bottom: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 0.95rem;
  line-height: 1.4;
  border-radius: 12px;
}

.custom-textarea {
  min-height: 80px;
  border-radius: 12px;
}

/* Items Section */
.items-section {
  margin: 0 1rem 1.5rem 1rem;
}

.items-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.items-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: white;
  margin: 0;
}

.items-title-with-icon {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}

.items-title-icon {
  font-size: 1.3rem;
  color: rgba(59, 130, 246, 1);
  flex-shrink: 0;
}

.add-item-btn {
  appearance: none;
  border: none;
  height: 36px;
  padding: 0 1rem;
  border-radius: 10px;
  background: linear-gradient(135deg, #253a60e0 30%, #1e40af90 100%);
  box-shadow: 0 4px 16px rgba(30, 41, 59, 0.18);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #fff;
  font-size: 0.85rem;
  font-weight: 600;
}

.add-item-btn ion-icon {
  font-size: 20px;
}

.add-item-btn:hover {
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.25);
  transform: translateY(-2px);
}

/* Items List */
.items-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.item-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 1rem;
  padding: 1.25rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.item-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.15);
}

.item-content {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 0.875rem;
  flex: 1;
  min-width: 0;
}

.item-icon-wrapper {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(37, 99, 235, 0.1));
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.item-type-icon {
  font-size: 24px;
  color: rgba(59, 130, 246, 1);
}

.item-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.item-title {
  font-size: 1.05rem;
  font-weight: 600;
  color: white;
  margin: 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-type-badge {
  display: inline-block;
  font-size: 0.7rem;
  font-weight: 600;
  color: rgba(59, 130, 246, 1);
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.25);
  padding: 0.125rem 0.5rem;
  border-radius: 0.375rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  width: fit-content;
}

.item-description {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.5;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-actions {
  display: flex;
  gap: 0.5rem;
  flex-shrink: 0;
}

.item-action-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: white;
}

.item-action-btn ion-icon {
  font-size: 16px;
}

.item-action-btn.edit:hover {
  background: rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.3);
  color: rgba(59, 130, 246, 1);
}

.item-action-btn.delete {
  color: rgba(239, 68, 68, 1);
}

.item-action-btn.delete:hover {
  background: rgba(239, 68, 68, 0.15);
  border-color: rgba(239, 68, 68, 0.3);
}

/* Empty State */
.empty-items {
  background: rgba(255, 255, 255, 0.02);
  border: 1px dashed rgba(255, 255, 255, 0.1);
  border-radius: 1rem;
  padding: 3rem 1.5rem;
  text-align: center;
}

.empty-icon-wrapper {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  font-size: 40px;
  color: rgba(255, 255, 255, 0.3);
}

.empty-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 0.5rem 0;
}

.empty-description {
  font-size: 0.875rem;
  color: rgba(255, 255, 255, 0.4);
  margin: 0;
  line-height: 1.5;
}

.analytics-card {
  margin-top: 20px;
  border-radius: 16px;
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

.analytics-container {
  margin-left: 1rem;
  margin-right: 1rem;
}

.section-header {
  display: flex;
  flex-direction: column;
  margin-top: 24px;
  margin-bottom: 1rem;
  padding: 0;
}

.section-header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-header-icon {
  font-size: 20px;
  color: var(--ion-color-primary);
}

.section-header-title {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0;
  color: #fff;
}

.section-header-subtitle {
  font-size: 14px;
  opacity: 0.6;
  margin-top: 4px;
}

/* Responsive */
@media (max-width: 640px) {
  .header-title {
    font-size: 1.3rem;
  }

  .back-btn {
    width: 42px;
    height: 42px;
  }

  .back-btn ion-icon {
    font-size: 1.3rem;
  }
}
</style>
