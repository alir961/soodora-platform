<script setup lang="ts">
import { PropType, ref, computed, onMounted, inject } from 'vue';
import { IonIcon } from '@ionic/vue';
import { addOutline } from 'ionicons/icons';
import { Post } from '@/entity/Post';
import { usePlayerStore } from '@/store/playerStore';
import { audioPlayerService } from '@/services/audioPlayerService';
import { useAuthStore } from '@/store/reactiveStorage';
import UnauthenticatedActionCard from '@/component/auth/UnauthenticatedActionCard.vue';

const props = defineProps({
  post: {
    required: true,
    type: Object as PropType<Post>,
  },
  showSaveButton: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits(['save-track']);

const authStore = useAuthStore();
const player = usePlayerStore();
const trackDuration = ref<number>(0);
const unauthActionCardRef = ref<InstanceType<typeof UnauthenticatedActionCard> | null>(null);

// Try to inject the play function with context from parent view
const playTrackWithContext = inject<((post: Post) => Promise<void>) | null>('playTrackWithContext', null);

const isPlaying = computed(() => {
  // Make it fully reactive to playerStore changes
  // Check if this post is the current track AND player is playing
  return player.current?.slug === props.post.slug && player.isPlaying;
});

const displayDuration = computed(() => {
  // If this is the current track and has a duration, use that
  if (player.current?.slug === props.post.slug && player.duration > 0) {
    return player.duration;
  }
  // Otherwise use the preloaded duration
  return trackDuration.value;
});

const playTrack = async () => {
  if (!props.post.audioUrl) return;

  // Use the context-aware function if available
  if (playTrackWithContext) {
    await playTrackWithContext(props.post);
    return;
  }

  // Fallback to direct play without context
  await audioPlayerService.playTrack({
    slug: props.post.slug,
    title: props.post.title,
    audioTitle: props.post.audioTitle || props.post.title,
    artist: props.post.owner?.username || props.post.owner?.slug || 'Unknown',
    imageUrl: props.post.imageUrl || undefined,
    audioUrl: props.post.audioUrl,
    duration: 0,
    owner: props.post.owner,
  });
};

const formatTime = (time: number) => {
  const minutes = Math.floor(time / 60);
  const seconds = Math.floor(time % 60);
  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
};

const handleSaveTrack = (event: Event) => {
  event.stopPropagation();

  if (!authStore.isAuthenticated) {
    unauthActionCardRef.value?.open();
    return;
  }

  emit('save-track');
};

onMounted(() => {
  // Preload track duration if it's a music post
  if (props.post.audioUrl) {
    audioPlayerService
      .preloadTrackMetadata(props.post.audioUrl)
      .then((duration) => {
        if (duration > 0) {
          trackDuration.value = duration;
        }
      })
      .catch(() => {
        // Ignore errors
      });
  }
});
</script>

<template>
  <div class="track-card" @click="playTrack">
    <div class="track-cover">
      <img :src="post.imageUrl || '/assets/images/png/no-album-art.png'" :alt="post.audioTitle" />

      <div class="visualizer-overlay" :class="{ active: isPlaying }">
        <div class="visualizer-bar"></div>
        <div class="visualizer-bar"></div>
        <div class="visualizer-bar"></div>
        <div class="visualizer-bar"></div>
      </div>
    </div>

    <div class="track-info">
      <h3 class="track-title">{{ post.audioTitle }}</h3>
      <p class="track-artist">{{ post.owner.username || post.owner.slug }}</p>
    </div>

    <div class="track-meta">
      <span class="track-duration">{{ formatTime(displayDuration) }}</span>
      <button v-if="showSaveButton" class="track-save-btn" @click="handleSaveTrack" title="Add to playlist">
        <ion-icon :icon="addOutline" />
      </button>
    </div>
  </div>

  <UnauthenticatedActionCard ref="unauthActionCardRef" />
</template>

<style scoped>
.track-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: linear-gradient(135deg, rgba(13, 27, 42, 0.6), rgba(9, 18, 30, 0.5));
  border: 1px solid rgba(59, 130, 246, 0.15);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  margin-top: 0.75rem;
}

.track-card::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05), transparent 50%);
  opacity: 0;
  transition: opacity 0.25s ease;
}

.track-card:hover {
  transform: translateY(-2px);
  border-color: rgba(59, 130, 246, 0.3);
  box-shadow:
    0 8px 24px rgba(0, 0, 0, 0.3),
    0 0 0 1px rgba(59, 130, 246, 0.1);
}

.track-card:hover::before {
  opacity: 1;
}

.track-card:active {
  transform: translateY(0);
}

.track-cover {
  position: relative;
  width: 64px;
  height: 64px;
  flex-shrink: 0;
  border-radius: 10px;
  overflow: hidden;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.track-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.track-card:hover .track-cover img {
  transform: scale(1.05);
}

.visualizer-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.75);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 3px;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
  border-radius: 10px;
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
    transform: scaleY(1.5);
  }
}

.track-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.track-title {
  font-size: 1rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.track-artist {
  font-size: 0.85rem;
  color: #9aa3ad;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.track-meta {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-shrink: 0;
}

.track-duration {
  font-size: 0.875rem;
  font-weight: 500;
  color: #9aa3ad;
  font-variant-numeric: tabular-nums;
  min-width: 40px;
  text-align: right;
}

.track-save-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(59, 130, 246, 0.15);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #60a5fa;
  font-size: 18px;
}

.track-save-btn:hover {
  background: rgba(59, 130, 246, 0.25);
  transform: scale(1.05);
}

.track-save-btn:active {
  transform: scale(0.95);
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .track-card {
    padding: 0.875rem;
    gap: 0.875rem;
  }

  .track-cover {
    width: 56px;
    height: 56px;
  }

  .track-title {
    font-size: 0.9375rem;
  }

  .track-artist {
    font-size: 0.8125rem;
  }

  .track-duration {
    font-size: 0.8125rem;
  }

  .track-save-btn {
    width: 28px;
    height: 28px;
    font-size: 16px;
  }
}
</style>
