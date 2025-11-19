<script setup lang="ts">
import { IonIcon } from '@ionic/vue';
import { play, pause, closeOutline, heart, heartOutline, addOutline, playForwardOutline } from 'ionicons/icons';
import { computed, ref, watch, onMounted, onUnmounted, nextTick, createApp } from 'vue';
import { usePlayerStore } from '@/store/playerStore';
import { useLikeStore } from '@/store/likeStore';
import { audioPlayerService } from '@/services/audioPlayerService';
import { useAuthStore } from '@/store/reactiveStorage';
import UnauthenticatedActionCard from '@/component/auth/UnauthenticatedActionCard.vue';

const player = usePlayerStore();
const authStore = useAuthStore();
const unauthActionCardRef = ref<InstanceType<typeof UnauthenticatedActionCard> | null>(null);

// Initialize audio service
audioPlayerService.init(player);

// Type for global arrays
declare global {
  interface Window {
    __playlistPosts?: Array<{ slug: string }>;
    __explorePosts?: Array<{ slug: string }>;
    __exploreFollowingPosts?: Array<{ slug: string }>;
  }
}

function playNextTrack() {
  if (!player.current) return;

  // Try to play next track from the stored context
  audioPlayerService.playNext();
}
const likeStore = useLikeStore();
const isLiked = ref(false);
const barEl = ref<HTMLElement | null>(null);
let ro: ResizeObserver | null = null;

const progressBackground = computed(() => {
  if (!player.duration) return '';
  const pct = (player.currentTime / player.duration) * 100;
  return `background: linear-gradient(90deg, var(--ion-color-primary) ${pct}%, rgba(42, 42, 42, 0.5) ${pct}%)`;
});

const toggle = () => {
  if (player.isPlaying) {
    audioPlayerService.pause();
  } else {
    audioPlayerService.play();
  }
};

const onSeek = (e: Event) => {
  const val = Number((e.target as HTMLInputElement).value);
  audioPlayerService.seek(val);
};

const closePlayer = () => {
  // Stop the audio playback
  audioPlayerService.stop();
  // Clear the player store
  player.clear();
};

const formatTime = (time: number) => {
  if (!isFinite(time)) return '0:00';
  const m = Math.floor(time / 60);
  const s = Math.floor(time % 60)
    .toString()
    .padStart(2, '0');
  return `${m}:${s}`;
};

async function loadLiked() {
  const slug = player.current?.slug;
  if (!slug) {
    isLiked.value = false;
    return;
  }
  likeStore.initFromPost({ slug });
  await likeStore.ensureLikedLoaded(slug);
  isLiked.value = likeStore.isLiked(slug);
}

async function toggleLike() {
  if (!authStore.isAuthenticated) {
    unauthActionCardRef.value?.open();
    return;
  }

  const slug = player.current?.slug;
  if (!slug) return;
  await likeStore.toggle(slug);
  isLiked.value = likeStore.isLiked(slug);
}

watch(
  () => player.current?.slug,
  () => {
    loadLiked();
  },
  { immediate: true }
);

watch(
  () => player.current?.slug && likeStore.liked[player.current.slug],
  () => {
    const slug = player.current?.slug;
    if (slug) isLiked.value = likeStore.isLiked(slug);
  }
);

function publishOffset(px: number) {
  if (typeof document === 'undefined') return;
  document.dispatchEvent(new CustomEvent('now-playing-offset', { detail: { px } }));
}

async function updateOffset() {
  await nextTick();
  const el = barEl.value;
  if (!el) return;
  publishOffset(el.getBoundingClientRect().height);
}

onMounted(() => {
  if (player.hasTrack) updateOffset();
  if (typeof ResizeObserver !== 'undefined') {
    ro = new ResizeObserver(() => updateOffset());
    if (barEl.value) ro.observe(barEl.value);
  }
});

onUnmounted(() => {
  if (ro && barEl.value) ro.unobserve(barEl.value);
  ro = null;
  publishOffset(0);
});

watch(
  () => player.hasTrack,
  (has) => {
    if (has) updateOffset();
    else publishOffset(0);
  }
);

const openSaveTrack = async () => {
  if (!authStore.isAuthenticated) {
    unauthActionCardRef.value?.open();
    return;
  }

  if (!player.current) return;

  const { default: SaveTrackButton } = await import('@/component/actions/SaveTrackCard.vue');

  const container = document.createElement('div');
  document.body.appendChild(container);

  const app = createApp(SaveTrackButton, {
    postSlug: player.current.slug,
    audioTitle: player.current.audioTitle,
    audioUrl: player.current.audioUrl,
    imageUrl: player.current.imageUrl,
  });

  const instance = app.mount(container) as InstanceType<typeof SaveTrackButton>;

  const cleanup = () => {
    try {
      app.unmount();
      if (document.body.contains(container)) {
        document.body.removeChild(container);
      }
    } catch (e) {
      console.error('Cleanup error:', e);
    }
  };

  if (instance && typeof instance.open === 'function') {
    instance.open();
  }

  const checkInterval = setInterval(() => {
    if (!document.body.contains(container)) {
      cleanup();
      clearInterval(checkInterval);
    }
  }, 500);

  setTimeout(() => {
    clearInterval(checkInterval);
  }, 120000);
};
</script>

<template>
  <div v-if="player.hasTrack" class="now-playing" ref="barEl">
    <div class="top-row">
      <div class="album-art">
        <img
          :src="player.current?.imageUrl || '/assets/images/png/no-album-art.png'"
          :alt="player.current?.audioTitle || 'Album art'" />
      </div>

      <div class="track-info">
        <div class="track-title marquee">{{ player.current?.audioTitle || 'Unknown Track' }}</div>
        <div v-if="player.current?.owner" class="track-artist">
          {{ player.current.owner.username || player.current.owner.slug }}
        </div>
      </div>

      <div class="action-buttons">
        <button class="aux-btn" :class="{ active: isLiked }" @click="toggleLike" aria-label="Like">
          <ion-icon :icon="isLiked ? heart : heartOutline" />
        </button>
        <button class="aux-btn save-btn" @click="openSaveTrack" aria-label="Save to playlist">
          <ion-icon :icon="addOutline" />
        </button>
        <button class="close-btn" @click="closePlayer" aria-label="Close">
          <ion-icon :icon="closeOutline" />
        </button>
      </div>
    </div>

    <div class="controls-row">
      <button class="play-btn" @click="toggle" aria-label="Play/Pause">
        <ion-icon :icon="player.isPlaying ? pause : play" />
      </button>
      <div class="slider-container">
        <span class="time">{{ formatTime(player.currentTime) }}</span>
        <input
          class="slider swiper-no-swiping"
          type="range"
          min="0"
          :max="player.duration"
          step="0.1"
          :value="player.currentTime"
          @input="onSeek"
          @touchstart.stop
          @touchmove.stop
          @touchend.stop
          @mousedown.stop
          :style="progressBackground" />
        <span class="time">{{ formatTime(player.duration) }}</span>
      </div>
      <button class="next-btn" @click="playNextTrack" aria-label="Next Song">
        <ion-icon :icon="playForwardOutline" />
      </button>
    </div>
  </div>

  <UnauthenticatedActionCard ref="unauthActionCardRef" />
</template>

<style scoped>
.next-btn {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.2);
  background: rgba(59, 130, 246, 0.08);
  color: #60a5fa;
  width: 48px;
  height: 25px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.next-btn ion-icon {
  font-size: 18px;
}
.now-playing {
  position: fixed;
  width: 92%;
  margin: 0 auto;
  bottom: max(12px, env(safe-area-inset-bottom));
  padding: 10px;
  background: linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(10, 15, 28, 0.92));
  border: 1px solid rgba(59, 130, 246, 0.15);
  border-radius: 16px;
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.6),
    0 0 0 1px rgba(59, 130, 246, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.04);
  backdrop-filter: blur(24px) saturate(180%);
  -webkit-backdrop-filter: blur(24px) saturate(180%);
  z-index: 2000;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.top-row {
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 52px;
}

.album-art {
  width: 52px;
  height: 52px;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.album-art img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.track-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 2px;
  min-width: 0;
}

.track-title {
  font-size: 1rem !important;
  font-weight: 600;
  color: #ffffff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  line-height: 1.3;
}

.track-artist {
  font-size: 0.7rem !important;
  color: #cbd5e1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.2;
}

.action-buttons {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
  align-items: center;
}

.aux-btn,
.close-btn {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.6);
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  position: relative;
  overflow: hidden;
}

.aux-btn::before,
.close-btn::before {
  border: 1px solid rgba(255, 255, 255, 0.1);
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.3) 0%, transparent 70%);
  opacity: 0;
  transform: scale(0);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.aux-btn ion-icon,
.close-btn ion-icon {
  font-size: 15px;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.aux-btn:hover,
.close-btn:hover {
  background: rgba(59, 130, 246, 0.12);
  border-color: rgba(59, 130, 246, 0.2);
  color: #60a5fa;
}

.aux-btn:active,
.close-btn:active {
  transform: scale(0.95);
}

.aux-btn:active::before,
.close-btn:active::before {
  opacity: 1;
  transform: scale(2);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.aux-btn.active {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.25), rgba(37, 99, 235, 0.3));
  border-color: rgba(59, 130, 246, 0.4);
  color: #60a5fa;
  animation: ripple 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes ripple {
  0% {
    box-shadow: 0 0 0 0 rgba(59, 130, 246, 0.4);
  }
  50% {
    box-shadow: 0 0 0 8px rgba(59, 130, 246, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(59, 130, 246, 0);
  }
}

@keyframes like-pop {
  0% {
    transform: scale(0.9);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.save-btn {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
}

.controls-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.play-btn {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.25);
  background: rgba(255, 255, 255, 0.05);
  width: 52px;
  height: 32px;
  flex-shrink: 0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.play-btn ion-icon {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.6);
}

.play-btn:hover {
  background: rgba(59, 130, 246, 0.2);
  border-color: rgba(59, 130, 246, 0.4);
  transform: scale(1.05);
  box-shadow: 0 0 12px rgba(59, 130, 246, 0.3);
}

.play-btn:active {
  transform: scale(0.95);
}

.slider-container {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
  overflow: hidden;
}

.time {
  font-size: 0.65rem;
  color: #9aa3ad;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
  min-width: 30px;
  width: 30px;
  text-align: center;
  flex-shrink: 0;
}

.slider {
  -webkit-appearance: none;
  appearance: none;
  flex: 1;
  min-width: 40px;
  height: 5px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  outline: none;
  cursor: pointer;
}

.slider::-webkit-slider-runnable-track {
  height: 5px;
  background: transparent;
  border-radius: 999px;
}

.slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 12px;
  height: 12px;
  background: #ffffff;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
  cursor: pointer;
  transition: transform 0.15s ease;
  margin-top: -3.5px;
}

.slider::-webkit-slider-thumb:hover {
  transform: scale(1.2);
}

.slider::-moz-range-track {
  height: 5px;
  background: transparent;
  border-radius: 999px;
}

.slider::-moz-range-thumb {
  width: 12px;
  height: 12px;
  background: #ffffff;
  border: none;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
  cursor: pointer;
  margin-top: -3.5px;
}

@media (max-width: 768px) {
  .now-playing {
    left: 8px;
    right: 8px;
    padding: 8px;
    gap: 6px;
  }

  .top-row {
    min-height: 48px;
    gap: 8px;
  }

  .album-art {
    width: 48px;
    height: 48px;
  }

  .track-title {
    font-size: 1.1rem !important;
  }

  .track-artist {
    font-size: 0.8rem !important;
  }

  .play-btn {
    width: 48px;
    height: 25px;
  }

  .play-btn ion-icon {
    font-size: 18px;
  }

  .aux-btn,
  .close-btn {
    width: 26px;
    height: 26px;
  }

  .aux-btn ion-icon,
  .close-btn ion-icon {
    font-size: 14px;
  }

  .time {
    font-size: 0.8rem;
    min-width: 24px;
  }
}
</style>
