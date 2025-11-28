<script setup lang="ts">
import { ref, watchEffect, onActivated, watch } from 'vue';
import { useRoute } from 'vue-router';
import { searchPlaylists } from '@/client/playlistClient';
import { Playlist } from '@/entity/Playlist';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import Header from '@/component/AppHeader.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import { IonContent, IonIcon, IonRefresher, IonRefresherContent } from '@ionic/vue';
import { addCircleOutline, musicalNotesOutline, lockClosedOutline, eyeOffOutline } from 'ionicons/icons';
import { showToast } from '@/utils';
import { useRouter } from 'vue-router';

const playlists = ref<Playlist[]>([]);
const loading = ref(false);
const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const goToCreatePlaylist = () => {
  (document.activeElement as HTMLElement)?.blur();
  router.push('/playlists/create');
};

const goToPlaylist = (slug: string) => {
  (document.activeElement as HTMLElement)?.blur();
  router.push(`/playlists/${slug}`);
};

const loadPlaylistsForUser = async (userSlug: string) => {
  loading.value = true;
  try {
    const playlistPage = await searchPlaylists(userSlug);
    playlists.value = playlistPage.content;
  } catch (err) {
    const message = (err as Error)?.message ?? 'Unable to load playlists';
    playlists.value = [];
    await showToast(message, 'danger');
  } finally {
    loading.value = false;
  }
};

const handleRefresh = async (event: CustomEvent) => {
  const slug = authStore.getAuthenticatedUserSlug;
  if (slug) {
    await loadPlaylistsForUser(slug);
  }
  (event.target as HTMLIonRefresherElement).complete();
};

const getPrivacyIcon = (privacy: string) => {
  switch (privacy) {
    case 'PRIVATE':
      return lockClosedOutline;
    case 'UNLISTED':
      return eyeOffOutline;
    default:
      return musicalNotesOutline;
  }
};

const getPrivacyColor = (privacy: string) => {
  switch (privacy) {
    case 'PRIVATE':
      return 'rgba(239, 68, 68, 0.12)';
    case 'UNLISTED':
      return 'rgba(251, 191, 36, 0.12)';
    default:
      return 'rgba(59, 130, 246, 0.12)';
  }
};

watchEffect(() => {
  const slug = authStore.getAuthenticatedUserSlug;
  if (slug) loadPlaylistsForUser(slug);
});

watch(
  () => route.query.t,
  () => {
    const slug = authStore.getAuthenticatedUserSlug;
    if (slug) loadPlaylistsForUser(slug);
  }
);

onActivated(() => {
  const slug = authStore.getAuthenticatedUserSlug;
  if (slug) loadPlaylistsForUser(slug);
});
</script>

<template>
  <ApplicationViewport title="My Playlists">
    <IonContent class="playlists-content">
      <div class="ambient-glow">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
      </div>

      <IonRefresher slot="fixed" @ionRefresh="handleRefresh">
        <IonRefresherContent></IonRefresherContent>
      </IonRefresher>

      <Header
        title="My Playlists"
        :subtitle="`${playlists.length} playlist${playlists.length !== 1 ? 's' : ''}`"
        :icon="musicalNotesOutline">
        <template #actions>
          <button class="create-fab" @click="goToCreatePlaylist" tabindex="0">
            <IonIcon :icon="addCircleOutline" />
          </button>
        </template>
      </Header>

      <div class="playlists-container">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Loading your playlists...</p>
        </div>

        <div v-else-if="!playlists.length" class="empty-state">
          <div class="empty-icon-wrapper">
            <IonIcon :icon="musicalNotesOutline" class="empty-icon" />
          </div>
          <h2 class="empty-title">No Playlists Yet</h2>
          <p class="empty-description">Create your first playlist to organize your favorite tracks</p>
          <button class="empty-cta" @click="goToCreatePlaylist">
            <IonIcon :icon="addCircleOutline" />
            Create Playlist
          </button>
        </div>

        <div v-else class="playlists-grid">
          <div
            v-for="playlist in playlists"
            :key="playlist.slug"
            class="playlist-card"
            @click="goToPlaylist(playlist.slug)">
            <div class="card-image-wrapper">
              <img v-if="playlist.imageUrl" :src="playlist.imageUrl" :alt="playlist.name" class="playlist-image" />
              <div v-else class="playlist-image-placeholder">
                <IonIcon :icon="musicalNotesOutline" />
              </div>

              <div class="image-overlay"></div>
            </div>

            <div class="card-content">
              <h3 class="playlist-name">{{ playlist.name }}</h3>
              <p class="playlist-description">{{ playlist.description }}</p>

              <div class="card-footer">
                <div class="owner-info">
                  <span class="owner-label">by</span>
                  <span class="owner-name">@{{ playlist.owner.username }}</span>
                </div>
              </div>
            </div>

            <div class="card-badges">
              <div
                class="privacy-badge-right"
                :style="{
                  background: getPrivacyColor(playlist.privacy),
                  border:
                    playlist.privacy === 'PRIVATE'
                      ? '1px solid rgba(239, 68, 68, 0.25)'
                      : playlist.privacy === 'UNLISTED'
                        ? '1px solid rgba(251, 191, 36, 0.25)'
                        : '1px solid rgba(59, 130, 246, 0.25)',
                }">
                <IonIcon
                  :icon="getPrivacyIcon(playlist.privacy)"
                  :style="{
                    color:
                      playlist.privacy === 'PRIVATE'
                        ? 'rgba(239, 68, 68, 1)'
                        : playlist.privacy === 'UNLISTED'
                          ? 'rgba(251, 191, 36, 1)'
                          : 'rgba(59, 130, 246, 1)',
                  }" />
                <span>{{ playlist.privacy }}</span>
              </div>

              <div class="tracks-badge-right">
                <IonIcon :icon="musicalNotesOutline" />
                <span>{{ playlist.numberOfTracks }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
.playlists-content {
  --background: #000000;
}

.playlists-container {
  padding: 1.5rem;
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  gap: 1.5rem;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 3px solid rgba(249, 115, 22, 0.15);
  border-top-color: #fb923c;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
  max-width: 500px;
  margin: 0 auto;
}

.empty-icon-wrapper {
  width: 120px;
  height: 120px;
  border-radius: 30px;
  background: linear-gradient(135deg, rgba(249, 115, 22, 0.08), rgba(234, 88, 12, 0.05));
  border: 2px solid rgba(249, 115, 22, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2rem;
  box-shadow: 0 8px 32px rgba(249, 115, 22, 0.1);
}

.empty-icon {
  font-size: 56px;
  color: rgb(251, 146, 60);
  opacity: 0.8;
}

.empty-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 0.75rem 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.empty-description {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.5);
  line-height: 1.6;
  margin: 0 0 2rem 0;
}

.empty-cta {
  appearance: none;
  border: 0;
  height: 52px;
  padding: 0 2rem;
  border-radius: 14px;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  box-shadow:
    0 10px 30px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
  color: #ffffff;
  font-size: 1.05rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.empty-cta ion-icon {
  font-size: 24px;
}

.empty-cta:hover {
  box-shadow:
    0 12px 36px rgba(37, 99, 235, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.16);
  transform: translateY(-2px);
}

.empty-cta:active {
  transform: translateY(0);
  box-shadow:
    0 8px 24px rgba(37, 99, 235, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
}

.playlists-grid {
  margin-top: 0.5em;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.playlist-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(40px) saturate(180%);
  -webkit-backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 1rem;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: row;
  align-items: center;
  height: 110px;
  position: relative;
}

.playlist-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: linear-gradient(to bottom, #3b82f6, #2563eb);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.playlist-card:hover::before {
  opacity: 1;
}

.playlist-card:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(59, 130, 246, 0.15);
}

.playlist-card:hover::before {
  opacity: 1;
}

.playlist-card:active {
  transform: translateY(0);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.1);
}

.card-image-wrapper {
  position: relative;
  flex-shrink: 0;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 14px;
  margin-left: 10px;
}

.playlist-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.playlist-card:hover .playlist-image {
  transform: scale(1.1);
}

.playlist-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(249, 115, 22, 0.1), rgba(234, 88, 12, 0.08));
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.playlist-image-placeholder ion-icon {
  font-size: 48px;
  color: rgb(249, 116, 22);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.playlist-card:hover .playlist-image-placeholder {
  background: linear-gradient(135deg, rgba(249, 115, 22, 0.15), rgba(234, 88, 12, 0.12));
}

.playlist-card:hover .playlist-image-placeholder ion-icon {
  color: rgba(249, 115, 22, 0.7);
  transform: scale(1.15);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(249, 115, 22, 0.15), rgba(234, 88, 12, 0.1));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.playlist-card:hover .image-overlay {
  opacity: 1;
}

.privacy-badge {
  display: none;
}

.tracks-badge {
  display: none;
}

.card-content {
  padding: 1rem 1.25rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 0.4rem;
  flex: 1;
  min-width: 0;
}

.playlist-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
  transition: color 0.2s ease;
}

.playlist-card:hover .playlist-name {
  color: #fb923c;
}

.playlist-description {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.5);
  line-height: 1.3;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-footer {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-top: 0;
}

.owner-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.75rem;
}

.owner-label {
  color: rgba(255, 255, 255, 0.35);
  font-weight: 500;
}

.owner-name {
  color: rgba(251, 146, 60, 0.8);
  font-weight: 600;
}

.card-badges {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  align-items: flex-end;
  flex-shrink: 0;
}

.privacy-badge-right {
  padding: 8px 14px;
  border-radius: 10px;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.7rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: rgba(255, 255, 255, 0.9);
  transition: all 0.25s ease;
}

.tracks-badge-right {
  padding: 8px 14px;
  border-radius: 10px;
  background: rgba(59, 130, 246, 0.12);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(59, 130, 246, 0.25);
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.25s ease;
}

.playlist-card:hover .privacy-badge-right,
.playlist-card:hover .tracks-badge-right {
  transform: scale(1.08);
}

.privacy-badge-right ion-icon {
  font-size: 13px;
}

.tracks-badge-right ion-icon {
  font-size: 16px;
  color: rgba(59, 130, 246, 1);
}

.tracks-badge-right span {
  font-size: 0.9rem;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.9);
}

@media (max-width: 768px) {
  .playlists-header {
    padding: 1rem;
  }

  .header-icon-wrapper {
    width: 42px;
    height: 42px;
    border-radius: 10px;
  }

  .header-icon {
    font-size: 20px;
  }

  .header-title {
    font-size: 1.25rem;
  }

  .header-subtitle {
    font-size: 0.8rem;
  }

  .create-fab {
    height: 38px;
    border-radius: 10px;
  }

  .create-fab ion-icon {
    font-size: 19px;
  }

  .playlists-container {
    padding: 1rem;
  }

  .playlist-card {
    height: 90px;
  }

  .card-image-wrapper {
    width: 70px;
    height: 70px;
  }

  .playlist-image-placeholder ion-icon {
    font-size: 40px;
  }

  .card-content {
    padding: 0.75rem;
  }

  .playlist-name {
    font-size: 1rem;
  }

  .playlist-description {
    font-size: 0.8rem;
  }

  .owner-info {
    font-size: 0.7rem;
  }

  .card-badges {
    padding: 0.75rem;
    gap: 0.6rem;
  }

  .privacy-badge-right,
  .tracks-badge-right {
    padding: 5px 8px;
    font-size: 0.65rem;
  }

  .privacy-badge-right ion-icon {
    font-size: 12px;
  }

  .tracks-badge-right ion-icon {
    font-size: 13px;
  }

  .tracks-badge-right span {
    font-size: 0.8rem;
  }

  .empty-icon-wrapper {
    width: 100px;
    height: 100px;
    border-radius: 24px;
  }

  .empty-icon {
    font-size: 48px;
  }

  .empty-title {
    font-size: 1.5rem;
  }
}

@media (max-width: 640px) {
  .playlists-grid {
    gap: 1.25rem;
  }

  .card-badges {
    padding: 0.5rem;
  }
}

.ambient-glow {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
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
  33% {
    transform: translate(-20px, -25px) scale(1.05);
  }
  66% {
    transform: translate(20px, 20px) scale(0.95);
  }
}
</style>
