<script setup lang="ts">
import { ref, watch } from 'vue';
import { IonIcon } from '@ionic/vue';
import { musicalNotesOutline, checkmarkCircle, addCircleOutline } from 'ionicons/icons';
import { showToast } from '@/utils';
import { useAuthStore } from '@/store/reactiveStorage';
import { usePlayerStore } from '@/store/playerStore';
import {
  searchPlaylists,
  addPostToPlaylist,
  removePostFromPlaylist,
  fetchPlaylistBySlug,
} from '@/client/playlistClient';
import router from '@/router';

const props = defineProps<{
  postSlug: string;
  audioTitle?: string | null;
  audioUrl?: string | null;
  currentPlaylistSlug?: string | null;
  imageUrl?: string | null;
}>();

const authStore = useAuthStore();
const player = usePlayerStore();
const showModal = ref(false);
const savedPlaylistsSlugs = ref<string[]>([]);
interface Playlist {
  slug: string;
  name: string;
  imageUrl?: string | null;
  postCount?: number;
}
const allPlaylists = ref<Playlist[]>([]);
const isLoading = ref(false);
const isSavingChanges = ref(false);
const selectedPlaylists = ref<Set<string>>(new Set());

const checkSaved = async (postSlug: string) => {
  const userSlug = authStore.getAuthenticatedUserSlug;
  if (!userSlug) return;

  const defaultPage = { page: 0, size: 20 };
  savedPlaylistsSlugs.value = [];

  try {
    const playlists = await searchPlaylists(userSlug);

    for (const pl of playlists.content) {
      const fullPlaylist = await fetchPlaylistBySlug(pl.slug, defaultPage);
      if (fullPlaylist.posts.content.some((p) => p.slug === postSlug)) {
        savedPlaylistsSlugs.value.push(pl.slug);
      }
    }
  } catch (error) {
    console.error('Failed to check saved playlists', error);
  }
};

const loadPlaylists = async () => {
  isLoading.value = true;
  try {
    const userSlug = authStore.getAuthenticatedUserSlug;
    if (!userSlug) return;

    const response = await searchPlaylists(userSlug);
    const playlistsWithCounts: Playlist[] = [];
    const defaultPage = { page: 0, size: 20 };
    for (const pl of response.content) {
      try {
        const playlist = await fetchPlaylistBySlug(pl.slug, defaultPage);
        const postCount = playlist?.posts?.page?.totalElements ?? playlist?.posts?.content?.length ?? 0;
        playlistsWithCounts.push({
          ...pl,
          postCount,
        });
      } catch (err) {
        playlistsWithCounts.push({
          ...pl,
          postCount: 0,
        });
      }
    }
    allPlaylists.value = playlistsWithCounts;
    selectedPlaylists.value = new Set(savedPlaylistsSlugs.value);
  } catch (error) {
    console.error('Failed to load playlists', error);
  } finally {
    isLoading.value = false;
  }
};

const open = async () => {
  showModal.value = true;
  await checkSaved(props.postSlug);
  await loadPlaylists();
};

const closeModal = () => {
  showModal.value = false;
};

const togglePlaylist = (slug: string) => {
  if (selectedPlaylists.value.has(slug)) {
    selectedPlaylists.value.delete(slug);
  } else {
    selectedPlaylists.value.add(slug);
  }
};

const saveChanges = async () => {
  isSavingChanges.value = true;
  try {
    const previous = new Set(savedPlaylistsSlugs.value);
    const current = selectedPlaylists.value;

    for (const slug of current) {
      if (!previous.has(slug)) await addPostToPlaylist(slug, props.postSlug);
    }

    for (const slug of previous) {
      if (!current.has(slug)) await removePostFromPlaylist(slug, props.postSlug);
    }

    savedPlaylistsSlugs.value = [...current];
    closeModal();
  } catch (error: unknown) {
    const message = 'Failed to save track. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isSavingChanges.value = false;
  }
};

const goToCreatePlaylist = async () => {
  closeModal();
  await router.push('/playlists/create');
};

watch(
  () => props.postSlug,
  () => {
    checkSaved(props.postSlug);
  },
  { immediate: true }
);

defineExpose({
  open,
});
</script>

<template>
  <div>
    <Teleport v-if="showModal" to="body">
      <transition name="modal-fade" appear>
        <div class="save-modal-overlay" @click="closeModal">
          <div class="save-modal" @click.stop>
            <button class="close-btn" @click="closeModal">✕</button>

            <div class="modal-content">
              <div class="modal-header">
                <div class="header-content">
                  <div class="track-cover">
                    <img
                      :src="props.imageUrl || player.current?.imageUrl || '/assets/images/png/no-album-art.png'"
                      :alt="audioTitle || 'Track cover'" />
                  </div>
                  <div class="header-text">
                    <h3>Save Track</h3>
                    <p class="track-name" v-if="audioTitle">{{ audioTitle }}</p>
                  </div>
                </div>
              </div>

              <div v-if="isLoading" class="loading-state">
                <div class="loading-spinner"></div>
                <p>Loading playlists...</p>
              </div>

              <div v-else-if="allPlaylists.length === 0" class="empty-state">
                <div class="empty-icon">
                  <ion-icon :icon="musicalNotesOutline" />
                </div>
                <p class="empty-text">No playlists yet</p>
                <p class="empty-subtext">Create your first playlist to save tracks</p>
                <button class="create-first-btn" @click="goToCreatePlaylist">
                  <ion-icon :icon="addCircleOutline" />
                  <span>Create Playlist</span>
                </button>
              </div>

              <div v-else class="playlists-section">
                <div class="list-header">
                  <span>Your Playlists</span>
                  <button class="add-playlist-btn" @click="goToCreatePlaylist" title="Create new playlist">
                    <ion-icon :icon="addCircleOutline" />
                  </button>
                </div>

                <div class="playlists-scroll">
                  <button
                    v-for="playlist in allPlaylists"
                    :key="playlist.slug"
                    class="playlist-item"
                    :class="{ selected: selectedPlaylists.has(playlist.slug) }"
                    @click="togglePlaylist(playlist.slug)">
                    <div class="playlist-cover">
                      <img v-if="playlist.imageUrl" :src="playlist.imageUrl" :alt="playlist.name" />
                      <div v-else class="playlist-placeholder">
                        <ion-icon :icon="musicalNotesOutline" />
                      </div>
                    </div>
                    <div class="playlist-details">
                      <span class="playlist-name">{{ playlist.name }}</span>
                      <span class="playlist-count">{{ playlist.postCount || 0 }} tracks</span>
                    </div>
                    <div class="playlist-check">
                      <ion-icon :icon="checkmarkCircle" />
                    </div>
                  </button>
                </div>

                <button class="save-btn" :disabled="isSavingChanges" @click="saveChanges">
                  <span v-if="!isSavingChanges">Save Changes</span>
                  <span v-else>Saving...</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<style scoped>
.save-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.save-modal {
  position: relative;
  background: linear-gradient(145deg, rgba(15, 23, 42, 0.98), rgba(10, 15, 28, 0.96));
  border-radius: 20px;
  width: 100%;
  max-width: 420px;
  max-height: 85vh;
  backdrop-filter: blur(24px) saturate(180%);
  -webkit-backdrop-filter: blur(24px) saturate(180%);
  border: 1px solid rgba(59, 130, 246, 0.2);
  box-shadow:
    0 24px 48px rgba(0, 0, 0, 0.7),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  display: flex;
  flex-direction: column;
}

.close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.6);
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 16px;
  font-weight: 300;
  transition: all 0.2s ease;
  z-index: 1;
}

.close-btn:hover {
  background: rgba(239, 68, 68, 0.15);
  border-color: rgba(239, 68, 68, 0.3);
  color: #f87171;
  transform: rotate(90deg);
}

.close-btn:active {
  transform: rotate(90deg) scale(0.9);
}

.modal-content {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  flex: 1;
  min-height: 0;
}

.modal-header {
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(59, 130, 246, 0.15);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.track-cover {
  width: 64px;
  height: 64px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(59, 130, 246, 0.3);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.track-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.header-text {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.header-text h3 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: -0.02em;
}

.track-name {
  margin: 0;
  font-size: 0.9rem;
  color: #9aa3ad;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-state {
  text-align: center;
  padding: 2rem 1rem;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1rem;
  background: rgba(59, 130, 246, 0.15);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px dashed rgba(59, 130, 246, 0.3);
}

.empty-icon ion-icon {
  font-size: 40px;
  color: #60a5fa;
}

.empty-text {
  margin: 0 0 0.5rem 0;
  font-size: 1.2rem;
  font-weight: 600;
  color: #ffffff;
}

.empty-subtext {
  margin: 0 0 1.5rem 0;
  font-size: 0.9rem;
  color: #9aa3ad;
}

.create-first-btn {
  appearance: none;
  border: 0;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 50%, #60a5fa);
  color: #ffffff;
  font-size: 1rem;
  font-weight: 700;
  padding: 0.875rem 1.5rem;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  box-shadow:
    0 8px 20px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.create-first-btn ion-icon {
  font-size: 22px;
}

.create-first-btn:hover {
  transform: translateY(-2px);
  box-shadow:
    0 10px 24px rgba(37, 99, 235, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.create-first-btn:active {
  transform: translateY(0);
}

.playlists-section {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.list-header span {
  font-size: 0.85rem;
  font-weight: 600;
  color: #9aa3ad;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.add-playlist-btn {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.25);
  background: rgba(59, 130, 246, 0.1);
  color: #60a5fa;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-playlist-btn ion-icon {
  font-size: 20px;
}

.add-playlist-btn:hover {
  background: rgba(59, 130, 246, 0.2);
  border-color: rgba(59, 130, 246, 0.4);
  transform: scale(1.05);
}

.add-playlist-btn:active {
  transform: scale(0.95);
}

.playlists-scroll {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding-right: 0.25rem;
}

.playlists-scroll::-webkit-scrollbar {
  width: 6px;
}

.playlists-scroll::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 3px;
}

.playlists-scroll::-webkit-scrollbar-thumb {
  background: rgba(96, 165, 250, 0.3);
  border-radius: 3px;
}

.playlists-scroll::-webkit-scrollbar-thumb:hover {
  background: rgba(96, 165, 250, 0.5);
}

.playlist-item {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.03);
  padding: 0.75rem;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.playlist-item:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(59, 130, 246, 0.2);
  transform: translateX(2px);
}

.playlist-item.selected {
  background: rgba(59, 130, 246, 0.12);
  border-color: rgba(59, 130, 246, 0.4);
}

.playlist-item:active {
  transform: scale(0.98);
}

.playlist-cover {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.playlist-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.playlist-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(59, 130, 246, 0.15);
}

.playlist-placeholder ion-icon {
  font-size: 24px;
  color: #60a5fa;
}

.playlist-details {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.playlist-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: #ffffff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.playlist-count {
  font-size: 0.8rem;
  color: #9aa3ad;
}

.playlist-check {
  flex-shrink: 0;
  opacity: 0;
  transition: all 0.2s ease;
}

.playlist-check ion-icon {
  font-size: 24px;
  color: #60a5fa;
}

.playlist-item.selected .playlist-check {
  opacity: 1;
  animation: checkPop 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes checkPop {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
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

.save-btn {
  width: 100%;
  height: 48px;
  border: 0;
  border-radius: 12px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 50%, #60a5fa);
  box-shadow:
    0 10px 30px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
  transition: all 0.2s ease;
}

.save-btn:hover:not(:disabled) {
  box-shadow:
    0 12px 32px rgba(37, 99, 235, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.save-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow:
    0 8px 24px rgba(37, 99, 235, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.save-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.modal-fade-enter-active {
  animation: modalFadeIn 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active {
  animation: modalFadeIn 0.2s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

.modal-fade-enter-active .save-modal {
  animation: modalSlideUp 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.modal-fade-leave-active .save-modal {
  animation: modalSlideUp 0.25s cubic-bezier(0.8, 0.2, 0.8, 0.2) reverse;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes modalSlideUp {
  from {
    opacity: 0;
    transform: scale(0.95) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@media (max-width: 480px) {
  .save-modal {
    max-width: calc(100% - 2rem);
    max-height: 90vh;
  }

  .modal-content {
    padding: 1.25rem;
  }

  .header-content {
    gap: 0.75rem;
  }

  .track-cover {
    width: 56px;
    height: 56px;
  }

  .header-text h3 {
    font-size: 1.3rem;
  }

  .track-name {
    font-size: 0.85rem;
  }

  .playlist-item {
    padding: 0.65rem;
  }

  .playlist-cover {
    width: 44px;
    height: 44px;
  }
}
</style>
