<script setup lang="ts">
import { IonIcon } from '@ionic/vue';
import { play, pause, closeOutline, heart, heartOutline } from 'ionicons/icons';
import { computed, ref, watch, onMounted, onUnmounted, nextTick } from 'vue';
import { usePlayerStore } from '@/store/playerStore';
import UsernameTag from '@/component/user/UsernameTag.vue';
import { useLikeStore } from '@/store/likeStore';

const player = usePlayerStore();
const likeStore = useLikeStore();
const isLiked = ref(false);
const barEl = ref<HTMLElement | null>(null);
let ro: ResizeObserver | null = null;

const progressBackground = computed(() => {
  if (!player.duration) return '';
  const pct = (player.currentTime / player.duration) * 100;
  return `background: linear-gradient(90deg, var(--ion-color-primary) ${pct}%, #2a2a2a ${pct}%)`;
});

const toggle = () => (player.isPlaying ? player.pause() : player.play());
const onSeek = (e: Event) => {
  const val = Number((e.target as HTMLInputElement).value);
  player.seek(val);
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
  const slug = player.current?.slug;
  if (!slug) return;
  await likeStore.toggle(slug);
  isLiked.value = likeStore.isLiked(slug);
}

watch(
  () => player.current?.slug,
  () => {
    loadLiked();
    const slug = player.current?.slug;
    if (!slug) return;
    document.querySelectorAll<HTMLAudioElement>('audio[data-post-slug]').forEach((el) => {
      const elSlug = el.getAttribute('data-post-slug');
      if (elSlug && elSlug !== slug && !el.paused) {
        try {
          el.pause();
        } catch {
          /* ignore */
        }
      }
    });
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
</script>

<template>
  <div v-if="player.hasTrack" class="now-playing" ref="barEl">
    <button class="aux-btn" :class="{ active: isLiked }" @click="toggleLike" aria-label="Like">
      <ion-icon :icon="isLiked ? heart : heartOutline" />
    </button>
    <button class="close-btn" @click="player.clear" aria-label="Close">
      <ion-icon :icon="closeOutline" />
    </button>

    <div v-if="player.current?.owner" class="artist-row">
      <UsernameTag :user="player.current.owner" />
    </div>

    <div class="main-row">
      <button class="play-btn" @click="toggle" aria-label="Play/Pause">
        <ion-icon :icon="player.isPlaying ? pause : play" />
      </button>
      <div class="main-content">
        <div class="title marquee">{{ player.current?.title }}</div>
        <div class="slider-wrap">
          <span class="time left">{{ formatTime(player.currentTime) }}</span>
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
          <span class="time right">{{ formatTime(player.duration) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.now-playing {
  position: fixed;
  left: 12px;
  right: 12px;
  bottom: max(12px, env(safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 10px 12px 12px 12px;
  background:
    linear-gradient(
      135deg,
      rgba(255, 140, 0, 0.09) 0%,
      rgba(255, 140, 0, 0.06) 35%,
      rgba(255, 140, 0, 0.03) 65%,
      rgba(255, 255, 255, 0) 100%
    ),
    rgba(18, 18, 18, 0.75);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
  backdrop-filter: saturate(150%) blur(14px);
  -webkit-backdrop-filter: saturate(150%) blur(14px);
  z-index: 2000;
}

.artist-row {
  padding-right: 60px;
}

.main-row {
  display: grid;
  grid-template-columns: auto 1fr;
  align-items: stretch;
  gap: 10px;
}
.main-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
  width: 100%;
}
.title {
  font-size: 14px;
  color: #fff;
  font-weight: 600;
  letter-spacing: 0.2px;
}

.artist-line {
  margin-top: 2px;
}

.marquee {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.slider-wrap {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  width: 100%;
}

.time {
  font-size: 11px;
  color: var(--ion-color-medium);
  width: 36px;
  text-align: left;
  font-variant-numeric: tabular-nums;
}

.time.right {
  text-align: right;
}

.slider {
  -webkit-appearance: none;
  appearance: none;
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  outline: none;
}
.slider::-webkit-slider-runnable-track {
  height: 6px;
  background: transparent;
  border-radius: 999px;
}
.slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 16px;
  height: 16px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.35);
  margin-top: -5px;
}
.slider::-moz-range-track {
  height: 6px;
  background: transparent;
  border-radius: 999px;
}
.slider::-moz-range-thumb {
  width: 16px;
  height: 16px;
  background: #fff;
  border: none;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.35);
}

.play-btn {
  align-self: stretch;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  transition:
    transform 120ms ease,
    box-shadow 180ms ease;
}
.play-btn ion-icon {
  font-size: 24px;
  line-height: 1;
  color: var(--ion-color-primary);
}

.play-btn:active {
  transform: scale(0.965);
}
.close-btn,
.aux-btn {
  transition: transform 120ms ease;
}
.close-btn:active,
.aux-btn:active {
  transform: scale(0.965);
}

.close-btn {
  position: absolute;
  right: 14px;
  top: 10px;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.close-btn ion-icon {
  font-size: 16px;
}

.aux-btn {
  position: absolute;
  right: 58px;
  top: 10px;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.aux-btn ion-icon {
  font-size: 16px;
}
.aux-btn.active {
  background: var(--ion-color-primary);
  border-color: var(--ion-color-primary);
  animation: like-pop 140ms ease;
}

@keyframes like-pop {
  0% {
    transform: scale(0.94);
  }
  60% {
    transform: scale(1.06);
  }
  100% {
    transform: scale(1);
  }
}
</style>
