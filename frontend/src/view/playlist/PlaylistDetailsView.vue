<script setup lang="ts">
import { computed, ref, watch, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { fetchPlaylistBySlug, removePostFromPlaylist, deletePlaylistBySlug } from '@/client/playlistClient';
import { LIKED_AUDIO_PLAYLIST_NAME, PlaylistFull } from '@/entity/Playlist';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import Header from '@/component/AppHeader.vue';
import {
  arrowBackOutline,
  createOutline,
  musicalNotesOutline,
  trashOutline,
  playOutline,
  pauseOutline,
  timeOutline,
  heartOutline,
  repeatOutline,
  personOutline,
  ellipsisHorizontalOutline,
} from 'ionicons/icons';
import { IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonContent } from '@ionic/vue';
import { Post } from '@/entity/Post';
import { useAuthStore } from '@/store/reactiveStorage';
import { usePlayerStore } from '@/store/playerStore';
import TrackActionsPopup from '@/component/playlist/TrackActionsPopup.vue';
import DeleteCard from '@/component/actions/DeleteCard.vue';
import { ref as vueRef } from 'vue';
import { audioPlayerService } from '@/services/audioPlayerService';

const route = useRoute();
const router = useRouter();
const playerStore = usePlayerStore();

const deleteCardRef = vueRef();
const playlist = ref<PlaylistFull | null>(null);
const posts = ref<Post[]>([]);
const currentPage = ref(0);
const totalPages = ref(0);
const loading = ref(false);
const selectedTrack = ref<Post | null>(null);
const showTrackActions = ref(false);
const popupX = ref(0);
const popupY = ref(0);
const trackDurations = ref<Map<string, number>>(new Map());

const authStore = useAuthStore();
const isOwner = computed(() => {
  return playlist.value?.playlistSimpleDTO.owner.slug === authStore.getAuthenticatedUserSlug;
});

const isLikedAudioPlaylist = computed(() => {
  return playlist.value?.playlistSimpleDTO.name === LIKED_AUDIO_PLAYLIST_NAME;
});

// Track count stat for top
const trackCountStat = computed(() => {
  return `${posts.value.length} tracks`;
});

// Total playlist duration in minutes
const totalMinutes = computed(() => {
  let totalSeconds = 0;
  let hasAnyDuration = false;

  posts.value.forEach((post) => {
    const duration = trackDurations.value.get(post.slug);
    if (duration && duration > 0) {
      totalSeconds += duration;
      hasAnyDuration = true;
    }
  });

  if (!hasAnyDuration) {
    // Fallback: estimate based on track count
    const estimatedSeconds = posts.value.length * 180; // 3 min average
    const mins = Math.floor(estimatedSeconds / 60);
    return mins > 0 ? `~${mins} min${mins > 1 ? 's' : ''}` : '0 min';
  }

  const mins = Math.floor(totalSeconds / 60);
  return mins > 0 ? `${mins} min${mins > 1 ? 's' : ''}` : '0 min';
});

// Get duration for a track
const getTrackDuration = (post: Post) => {
  // First check if this is the current track with loaded duration
  if (audioPlayerService.isCurrentTrack(post.slug) && playerStore.duration > 0) {
    return formatDuration(Math.floor(playerStore.duration));
  }

  // Otherwise use cached duration
  const duration = trackDurations.value.get(post.slug);
  if (duration && duration > 0) {
    return formatDuration(Math.floor(duration));
  }

  return '--:--';
};

// Check if a track is currently playing
const isTrackPlaying = (post: Post) => {
  // Make it fully reactive to playerStore changes
  return playerStore.current?.slug === post.slug && playerStore.isPlaying;
};

const loadPlaylist = async (slug: string, page = 0) => {
  loading.value = true;
  try {
    const pageable = { page, size: 20 };
    const data = await fetchPlaylistBySlug(slug, pageable);
    playlist.value = data;

    if (page === 0) posts.value = data.postDTO.content;
    else posts.value.push(...data.postDTO.content);

    currentPage.value = data.postDTO.page.number;
    totalPages.value = data.postDTO.page.totalPages;

    // Preload durations for tracks that don't have them yet
    preloadTrackDurations();
  } finally {
    loading.value = false;
  }
};

// Preload durations for tracks in the background
const preloadTrackDurations = async () => {
  for (const post of posts.value) {
    // Skip if we already have the duration
    if (trackDurations.value.has(post.slug)) continue;
    if (!post.audioUrl) continue;

    // Load duration in the background
    try {
      const duration = await audioPlayerService.preloadTrackMetadata(post.audioUrl);
      if (duration > 0) {
        trackDurations.value.set(post.slug, duration);
      }
    } catch (err) {
      // Ignore errors for individual tracks
    }
  }
};

const loadMore = async (event: CustomEvent) => {
  const infiniteScroll = event.target as HTMLIonInfiniteScrollElement;

  if (!playlist.value) {
    await infiniteScroll.complete();
    return;
  }

  if (currentPage.value + 1 < totalPages.value)
    await loadPlaylist(playlist.value.playlistSimpleDTO.slug, currentPage.value + 1);

  await infiniteScroll.complete();
};

const goToEditPlaylist = () => {
  if (!playlist.value) return;
  (document.activeElement as HTMLElement)?.blur();
  router.push(`/playlists/${playlist.value.playlistSimpleDTO.slug}/edit`);
};

const goBackToPlaylists = () => {
  (document.activeElement as HTMLElement)?.blur();

  // Check if we can go back in history and if the previous page wasn't the edit page
  const canGoBack = window.history.state?.back;
  const isFromEdit = window.history.state?.back?.includes('/edit');

  if (canGoBack && !isFromEdit) {
    // Smart back - go to wherever we came from (search, playlists, etc.)
    router.back();
  } else {
    // Default to playlists page if no valid history or came from edit
    router.push(`/playlists/me?t=${Date.now()}`);
  }
};

const handleDeletePlaylist = async () => {
  if (!playlist.value) return;
  try {
    await deletePlaylistBySlug(playlist.value.playlistSimpleDTO.slug);
    await router.push(`/playlists/me?t=${Date.now()}`);
  } catch {
    // Error handling already done by client
  }
};

const removePost = async (postSlug: string) => {
  if (!playlist.value) return;
  await removePostFromPlaylist(playlist.value.playlistSimpleDTO.slug, postSlug);
  posts.value = posts.value.filter((p) => p.slug !== postSlug);
  // Reload to get fresh data
  await loadPlaylist(playlist.value.playlistSimpleDTO.slug);
};

const openTrackActions = (post: Post, event: MouseEvent) => {
  event.stopPropagation();
  selectedTrack.value = post;

  // Get click position
  const button = event.currentTarget as HTMLElement;
  const rect = button.getBoundingClientRect();

  // Position popup near the button (to the left and slightly below)
  popupX.value = rect.left - 220 + rect.width; // Align right edge with button
  popupY.value = rect.bottom + 8; // 8px gap below button

  showTrackActions.value = true;
};

const closeTrackActions = () => {
  showTrackActions.value = false;
  selectedTrack.value = null;
};

const playTrack = async (post: Post) => {
  if (!post.audioUrl) {
    return;
  }

  // Create context with all tracks in the playlist
  const context = {
    contextId: `playlist:${playlist.value?.playlistSimpleDTO.slug}`,
    tracks: posts.value
      .filter((p) => p.audioUrl)
      .map((p) => ({
        slug: p.slug,
        title: p.title,
        audioTitle: p.audioTitle || p.title,
        artist: p.owner?.username || p.owner?.slug || 'Unknown',
        imageUrl: p.imageUrl || undefined,
        audioUrl: p.audioUrl!,
        duration: 0,
        owner: p.owner,
      })),
  };

  await audioPlayerService.playTrack(
    {
      slug: post.slug,
      title: post.title,
      audioTitle: post.audioTitle || post.title,
      artist: post.owner?.username || post.owner?.slug || 'Unknown',
      imageUrl: post.imageUrl || undefined,
      audioUrl: post.audioUrl,
      duration: 0,
      owner: post.owner,
    },
    context
  );
};

const playPlaylist = () => {
  // Play the first track in the playlist
  if (posts.value.length > 0 && posts.value[0].audioUrl) {
    playTrack(posts.value[0]);
  }
};

onMounted(() => {
  // Initialize the audio service
  audioPlayerService.init(playerStore);
});

onUnmounted(() => {
  // Cleanup if needed
});

const goToUserProfile = (slug: string) => {
  router.push(`/user/${slug}`);
};

const formatDuration = (seconds: number) => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins}:${secs.toString().padStart(2, '0')}`;
};

watch(
  () => [route.params.slug, route.query.t],
  ([newSlug]) => {
    // Only load playlist if we're actually on a playlist route
    if (typeof newSlug === 'string' && route.path.startsWith('/playlists/')) {
      loadPlaylist(newSlug, 0);
    }
  },
  { immediate: true }
);

watch(
  () => playerStore.current?.slug,
  (newSlug) => {
    // Force reactivity update when track changes from external controls (like NowPlayingBar next button)
    if (newSlug) {
      // This will trigger re-evaluation of isTrackPlaying for all tracks
      posts.value = [...posts.value];
    }
  }
);
</script>

<template>
  <ApplicationViewport title="Playlist">
    <IonContent class="playlist-detail-content">
      <div class="ambient-glow">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
      </div>

      <div class="playlist-content">
        <!-- Header with Back Button and Title -->
        <Header
          v-if="playlist"
          :title="playlist.playlistSimpleDTO.name"
          :subtitle="trackCountStat"
          :icon="arrowBackOutline"
          :on-icon-click="goBackToPlaylists">
          <template #actions>
            <div v-if="isOwner && !isLikedAudioPlaylist" class="header-actions">
              <button class="edit-fab" @click="goToEditPlaylist" tabindex="0">
                <IonIcon :icon="createOutline" />
              </button>
              <button class="delete-fab" @click="deleteCardRef?.open()" tabindex="0">
                <IonIcon :icon="trashOutline" />
              </button>
              <DeleteCard
                ref="deleteCardRef"
                item-type="playlist"
                :item-slug="playlist.playlistSimpleDTO.slug"
                :delete-function="handleDeletePlaylist" />
            </div>
          </template>
        </Header>

        <!-- Merged Playlist Info + Tracks Card -->
        <div class="playlist-card-merged" v-if="playlist && posts.length">
          <!-- Playlist Info Section -->
          <div class="info-section">
            <div class="info-content">
              <div class="playlist-cover-wrapper">
                <img
                  v-if="playlist.playlistSimpleDTO.imageUrl"
                  :src="playlist.playlistSimpleDTO.imageUrl"
                  alt="Playlist cover"
                  class="playlist-cover" />
                <div v-else class="playlist-cover-placeholder">
                  <IonIcon :icon="musicalNotesOutline" />
                </div>
              </div>

              <div class="playlist-details">
                <p class="playlist-description" v-if="playlist.playlistSimpleDTO.description">
                  {{ playlist.playlistSimpleDTO.description }}
                </p>

                <div class="playlist-meta">
                  <span class="meta-item" @click="goToUserProfile(playlist.playlistSimpleDTO.owner.slug)">
                    <IonIcon :icon="personOutline" />
                    {{ playlist.playlistSimpleDTO.owner.username }}
                  </span>
                  <span class="meta-divider">•</span>
                  <span class="meta-item">
                    <IonIcon :icon="timeOutline" />
                    {{ totalMinutes }}
                  </span>
                </div>
              </div>

              <!-- Play Button - Right Side -->
              <button class="play-btn-circle" @click="playPlaylist">
                <IonIcon :icon="posts.length > 0 && isTrackPlaying(posts[0]) ? pauseOutline : playOutline" />
              </button>
            </div>
          </div>

          <!-- Tracks Section (merged into same card) -->
          <div class="tracks-header">
            <span class="track-number">#</span>
            <span class="track-title-header">Title</span>
            <span class="track-duration-header">Duration</span>
            <span class="track-stats-header">Stats</span>
          </div>

          <div class="tracks-list">
            <div
              v-for="(post, index) in posts"
              :key="post.slug"
              :class="['track-item', { 'track-item--playing': isTrackPlaying(post) }]"
              @click="playTrack(post)">
              <div class="track-number">{{ index + 1 }}</div>

              <div class="track-main">
                <div class="track-image-wrapper">
                  <template v-if="post.imageUrl && !isTrackPlaying(post)">
                    <img :src="post.imageUrl" :alt="post.audioTitle || post.title" class="track-image" />
                  </template>
                  <template v-else-if="isTrackPlaying(post)">
                    <div class="visualizer-overlay active">
                      <div class="visualizer-bar"></div>
                      <div class="visualizer-bar"></div>
                      <div class="visualizer-bar"></div>
                      <div class="visualizer-bar"></div>
                    </div>
                  </template>
                  <template v-else>
                    <img src="/assets/images/png/no-album-art.png" alt="No album art" class="track-image" />
                  </template>
                </div>
                <div class="track-info">
                  <h3 class="track-name">{{ post.audioTitle || post.title }}</h3>
                  <p class="track-artist">
                    {{ post.owner.username }}
                  </p>
                </div>
              </div>

              <div class="track-duration">{{ getTrackDuration(post) }}</div>

              <div class="track-stats">
                <span class="stat-item">
                  <IonIcon :icon="heartOutline" />
                  {{ post.likeCount }}
                </span>
                <span class="stat-item">
                  <IonIcon :icon="repeatOutline" />
                  {{ post.repostCount }}
                </span>
              </div>

              <button class="track-more" @click.stop="openTrackActions(post, $event)">
                <IonIcon :icon="ellipsisHorizontalOutline" />
              </button>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else-if="!loading && posts.length === 0" class="empty-state">
          <div class="empty-icon-wrapper">
            <IonIcon :icon="musicalNotesOutline" class="empty-icon" />
          </div>
          <h2 class="empty-title">No Tracks Yet</h2>
          <p class="empty-description">This playlist is empty. Add some tracks to get started!</p>
        </div>

        <IonInfiniteScroll threshold="100px" @ionInfinite="loadMore" :disabled="currentPage + 1 >= totalPages">
          <IonInfiniteScrollContent loading-spinner="bubbles" loading-text="Loading more tracks..." />
        </IonInfiniteScroll>
      </div>

      <!-- Track Actions Popup -->
      <TrackActionsPopup
        :is-open="showTrackActions"
        :x="popupX"
        :y="popupY"
        :show-remove="isOwner"
        @view-artist="selectedTrack && goToUserProfile(selectedTrack.owner.slug)"
        @remove-track="selectedTrack && removePost(selectedTrack.slug)"
        @close="closeTrackActions" />
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
  33% {
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

/* Content */
.playlist-content {
  position: relative;
  z-index: 1;
}

/* Header Actions */
.header-actions {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  flex-shrink: 0;
}

.edit-fab,
.delete-fab {
  appearance: none;
  border: none;
  height: 38px;
  width: 38px;
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
}

.edit-fab ion-icon,
.delete-fab ion-icon {
  font-size: 22px;
  color: #fff;
  padding-bottom: 0;
  flex-shrink: 0;
}

.delete-fab {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.delete-fab ion-icon {
  color: rgba(239, 68, 68, 1);
}

.edit-fab:hover,
.delete-fab:hover {
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.25);
  transform: translateY(-2px);
}

.edit-fab:hover {
  background: linear-gradient(135deg, #1e40af 60%, #1e293b 100%);
}

.delete-fab:hover {
  background: rgba(255, 255, 255, 0.1);
}

.edit-fab:active,
.delete-fab:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(30, 41, 59, 0.12);
}

/* Merged Card */
.playlist-card-merged {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 1rem;
  overflow: hidden;
  margin: 1.5rem 1rem 1.5rem 1rem;
}

.info-section {
  padding: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.info-content {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.playlist-cover-wrapper {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 0.5rem;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.05);
}

.playlist-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.playlist-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(249, 115, 22, 0.2), rgba(234, 88, 12, 0.2));
}

.playlist-cover-placeholder ion-icon {
  font-size: 2rem;
  color: rgba(249, 115, 22, 0.8);
}

.playlist-details {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  justify-content: center;
}

.playlist-description {
  font-size: 0.85rem;
  color: #fff;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.playlist-meta {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.6);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  cursor: pointer;
  transition: color 0.2s ease;
}

.meta-item:hover {
  color: rgba(249, 115, 22, 1);
}

.meta-item ion-icon {
  font-size: 0.85rem;
}

.meta-divider {
  color: rgba(255, 255, 255, 0.3);
}

/* Play Button */
.play-btn {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 0.75rem;
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: white;
  font-size: 1rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
  margin-bottom: 1rem;
}

.play-btn ion-icon {
  font-size: 1.3rem;
}

.play-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(249, 115, 22, 0.4);
}

/* Play Button Circle - Right Side */
.play-btn-circle {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px) saturate(180%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.play-btn-circle ion-icon {
  font-size: 1.4rem;
  color: rgba(249, 115, 22, 1);
}

.play-btn-circle:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(246, 131, 59, 0.4);
  transform: scale(1.05);
  box-shadow: 0 4px 16px rgba(246, 128, 59, 0.2);
}

.play-btn-circle:active {
  transform: scale(0.95);
  background: rgba(255, 255, 255, 0.15);
}

/* Tracks Section */
.tracks-header {
  display: grid;
  grid-template-columns: 40px 1fr 70px 80px 40px;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  font-size: 0.75rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.track-title-header {
  padding-left: 0.5rem;
}

.track-duration-header {
  text-align: center;
}

.track-stats-header {
  text-align: center;
}

.tracks-list {
  display: flex;
  flex-direction: column;
}

.track-item {
  display: grid;
  grid-template-columns: 40px 1fr 70px 80px 40px;
  gap: 0.75rem;
  align-items: center;
  padding: 0.75rem 1rem;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.track-duration {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.7);
  text-align: center;
  font-variant-numeric: tabular-nums;
}

.track-item:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(59, 130, 246, 0.3);
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.08);
}

.track-item--playing {
  background: rgba(59, 130, 246, 0.08);
  border-color: rgba(59, 130, 246, 0.15);
}

.track-item:last-child {
  border-bottom: none;
}

.track-number {
  font-size: 0.9rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  text-align: center;
}

.track-main {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  min-width: 0;
}

.track-image-wrapper {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 0.5rem;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.05);
}

.track-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.track-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.18), rgba(59, 130, 246, 0.12)); /* subtle blue */
}

.track-image-icon {
  font-size: 1.6rem;
  color: #f97316; /* orange, no transparency */
}

.visualizer-overlay {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 3px;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(59, 130, 246, 0.1));
}

.visualizer-overlay.active {
  opacity: 1;
}

.visualizer-bar {
  width: 3px;
  height: 12px;
  background: linear-gradient(to top, #60a5fa, #93c5fd);
  border-radius: 2px;
  transform-origin: bottom;
  animation: visualize 0.6s ease-in-out infinite;
}

.visualizer-bar:nth-child(1) {
  animation-delay: 0s;
}

.visualizer-bar:nth-child(2) {
  animation-delay: 0.15s;
}

.visualizer-bar:nth-child(3) {
  animation-delay: 0.3s;
}

.visualizer-bar:nth-child(4) {
  animation-delay: 0.45s;
}

@keyframes visualize {
  0%,
  100% {
    transform: scaleY(0.5);
  }
  50% {
    transform: scaleY(1);
  }
}

.track-info {
  flex: 1;
  min-width: 0;
}

.track-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: white;
  margin: 0 0 0.25rem 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.track-artist {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.track-stats {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  align-items: center;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
}

.stat-item ion-icon {
  font-size: 0.85rem;
}

.track-more {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.5);
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.track-more:hover {
  background: rgba(255, 255, 255, 0.08);
  color: white;
}

.track-more ion-icon {
  font-size: 1.2rem;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(40px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 1rem;
  margin: 0 1rem 1.5rem 1rem;
}

.empty-icon-wrapper {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(249, 115, 22, 0.15), rgba(234, 88, 12, 0.15));
  border-radius: 1rem;
}

.empty-icon {
  font-size: 2.5rem;
  color: rgba(249, 115, 22, 0.8);
}

.empty-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: white;
  margin: 0 0 0.75rem 0;
}

.empty-description {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
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

  .edit-fab {
    height: 38px;
    border-radius: 10px;
  }

  .edit-fab ion-icon {
    font-size: 19px;
  }

  .playlist-cover-wrapper {
    width: 70px;
    height: 70px;
  }

  .playlist-description {
    font-size: 0.8rem;
  }

  .tracks-header {
    display: none;
  }

  .track-item {
    grid-template-columns: 1fr 60px 40px;
    gap: 0.5rem;
  }

  .track-number {
    display: none;
  }

  .track-stats {
    display: none;
  }

  .track-duration {
    font-size: 0.8rem;
  }
}
</style>
