<script setup lang="ts">
import {
  IonButton,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCol,
  IonIcon,
  IonImg,
  IonNote,
  IonAvatar,
} from '@ionic/vue';
import { PropType, Ref, ref, watch, computed, onMounted, onUnmounted } from 'vue';
import { usePlayerStore } from '@/store/playerStore';
import { Post } from '@/entity/Post';
import { callDeletePost, callPostUnrepost, callPostRepost, callGetPostReposts } from '@/client/postClient';
import { useAuthStore } from '@/store/reactiveStorage';
import { Router, useRouter } from 'vue-router';
import customLikeIcon from '@/assets/icons/like.svg';
import customCommentIcon from '@/assets/icons/comment.svg';
import customRepostIcon from '@/assets/icons/repost.svg';
import UsernameTag from '@/component/user/UsernameTag.vue';
import Avatar from '@/component/user/AvatarItem.vue';
import DateDisplay from '@/component/date/DateDisplay.vue';
import { showToast } from '@/utils';
import { useLikeStore } from '@/store/likeStore';
import customShareIcon from '@/assets/icons/share.svg';
import customTrashIcon from '@/assets/icons/trash.svg';
import { addOutline } from 'ionicons/icons';
import ExportCard from '@/component/actions/ExportCard.vue';
import DeleteCard from '@/component/actions/DeleteCard.vue';
import SaveTrackCard from '@/component/actions/SaveTrackCard.vue';

const router: Router = useRouter();
const authStore = useAuthStore();

const props = defineProps({
  post: {
    required: true,
    type: Object as PropType<Post>,
  },
  currentPlaylistSlug: {
    required: false,
    type: String,
  },
});

const emit = defineEmits(['post-deleted', 'post-like', 'post-unlike', 'post-repost', 'post-unrepost']);

const likeStore = useLikeStore();
const isLiked: Ref<boolean> = ref(false);
const isReposted: Ref<boolean> = ref(false);
const displayedLikeCount = computed(() => likeStore.likeCount(props.post.slug));

const audioRef = ref<HTMLAudioElement | null>(null);
const isPlaying = ref(false);
const duration = ref(0);
const currentTime = ref(0);
const player = usePlayerStore();

const isMusicPost = computed(() => {
  return props.post.audioUrl && props.post.audioTitle;
});

function isCurrentTrack(): boolean {
  return !!player.current && player.current.slug === props.post.slug;
}

const onLoadedMetadata = () => {
  const audio = audioRef.value;
  if (!audio) return;
  duration.value = audio.duration;
  if (!props.post.audioUrl) return;
  if (isCurrentTrack()) {
    player.updateDuration(audio.duration || 0);
  }
};

const onTimeUpdate = () => {
  const audio = audioRef.value;
  if (!audio) return;
  currentTime.value = audio.currentTime;
  if (isCurrentTrack()) {
    player.updateTime(currentTime.value);
  }
};

const onAudioPlay = () => {
  isPlaying.value = true;
  document.dispatchEvent(new CustomEvent('post-audio-play', { detail: { slug: props.post.slug } }));
  if (!props.post.audioUrl) return;
  const currentEl = audioRef.value;
  document.querySelectorAll<HTMLAudioElement>('audio[data-post-slug]').forEach((el) => {
    if (el !== currentEl && !el.paused) {
      try {
        el.pause();
      } catch {
        /* ignore */
      }
    }
  });
  if (!isCurrentTrack()) {
    player.setTrack({
      slug: props.post.slug,
      title: props.post.title,
      audioTitle: props.post.audioTitle,
      artist: props.post.owner?.username || props.post.owner?.slug || 'Unknown',
      imageUrl: props.post.imageUrl || undefined,
      audioUrl: props.post.audioUrl,
      duration: duration.value || 0,
      owner: props.post.owner,
    });
  }
  player.isPlaying = true;
};

const onAudioPause = () => {
  isPlaying.value = false;
  if (isCurrentTrack()) player.isPlaying = false;
};

const onEnded = () => {
  isPlaying.value = false;
  currentTime.value = 0;
  if (isCurrentTrack()) {
    player.isPlaying = false;
    player.updateTime(0);
  }
};

const loadLikes = async (postSlug: string) => {
  likeStore.initFromPost(props.post);
  await likeStore.ensureLikedLoaded(postSlug);
  isLiked.value = likeStore.isLiked(postSlug);
};

const loadReposts = async (postSlug: string) => {
  try {
    const reposts = await callGetPostReposts(postSlug);
    const userSlug = authStore.getAuthenticatedUserSlug;

    if (reposts.some((repost) => repost === userSlug)) isReposted.value = true;
  } catch (error) {
    console.error('Unable to load reposts', error);
  }
};

const toggleLikePost = async (postSlug: string) => {
  await likeStore.toggle(postSlug);
  isLiked.value = likeStore.isLiked(postSlug);
  emit(isLiked.value ? 'post-like' : 'post-unlike');
};

const toggleRepostPost = async (postSlug: string) => {
  try {
    if (isReposted.value && props.post) {
      await callPostUnrepost(postSlug);
      isReposted.value = false;
      emit('post-unrepost');
    } else {
      await callPostRepost(postSlug);
      isReposted.value = true;
      emit('post-repost');
    }
  } catch (error: unknown) {
    const message = 'Unable to update repost. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

const redirectToPostComments = async (postSlug: string) => {
  await router.push({ name: 'CommentView', params: { slug: postSlug } });
};

watch(
  () => authStore.getAuthenticatedUser,
  (newUser) => {
    if (newUser) {
      const postSlug = props.post.slug;

      loadLikes(postSlug);
      loadReposts(postSlug);
    }
  },
  { immediate: true }
);

onMounted(() => {
  const postSlug = props.post.slug;

  loadLikes(postSlug);
  loadReposts(postSlug);
  const handleExternalPlay = (e: Event) => {
    const detail = (e as CustomEvent).detail as { slug?: string } | undefined;
    const otherSlug = detail?.slug;
    if (otherSlug && otherSlug !== props.post.slug) {
      const audio = audioRef.value;
      if (audio && !audio.paused) {
        audio.pause();
      }
    }
  };
  document.addEventListener('post-audio-play', handleExternalPlay as EventListener);

  const stopWatch = watch(
    () => [likeStore.liked[props.post.slug], likeStore.counts[props.post.slug]],
    () => {
      isLiked.value = likeStore.isLiked(props.post.slug);
    }
  );

  const handlePlayerControl = (e: Event) => {
    const { action, slug, time } = (e as CustomEvent).detail || {};
    const audio = audioRef.value;
    if (!audio) return;
    if (slug && slug !== props.post.slug) return;

    if (action === 'play') {
      audio.play();
    } else if (action === 'pause') {
      if (!audio.paused) audio.pause();
    } else if (action === 'seek' && typeof time === 'number') {
      audio.currentTime = time;
    } else if (action === 'stop') {
      if (!audio.paused) audio.pause();
      audio.currentTime = 0;
    }
  };
  document.addEventListener('player-control', handlePlayerControl as EventListener);

  onUnmounted(() => {
    document.removeEventListener('post-audio-play', handleExternalPlay as EventListener);
    stopWatch();
    document.removeEventListener('player-control', handlePlayerControl as EventListener);
    const audio = audioRef.value;
    if (audio && !audio.paused) {
      audio.pause();
    }
  });
});

function formatCount(count: number): string {
  if (count === 0) return '';
  if (count >= 1000000) {
    return (count / 1000000).toFixed(1).replace(/\.0$/, '') + 'M';
  }
  if (count >= 1000) {
    return (count / 1000).toFixed(1).replace(/\.0$/, '') + 'K';
  }
  return count.toString();
}

const formatTime = (time: number) => {
  const minutes = Math.floor(time / 60);
  const seconds = Math.floor(time % 60);
  return `${minutes}:${seconds.toString().padStart(2, '0')}`;
};

const exportCardRef = ref<InstanceType<typeof ExportCard> | null>(null);
const deleteCardRef = ref<InstanceType<typeof DeleteCard> | null>(null);
const saveTrackCardRef = ref<InstanceType<typeof SaveTrackCard> | null>(null);

const openExportModal = () => {
  exportCardRef.value?.open();
};

const openDeleteConfirm = () => {
  deleteCardRef.value?.open();
};

const openSaveTrack = () => {
  saveTrackCardRef.value?.open();
};

const handlePostDeleted = (slug: string) => {
  emit('post-deleted', slug);
};

const playTrack = () => {
  const audio = audioRef.value;
  if (!audio) return;

  if (audio.paused) {
    audio.play();
  } else {
    audio.pause();
  }
};
</script>

<template>
  <ion-col size="12" size-md="7" size-lg="5">
    <ion-card>
      <ion-card-header class="post-header">
        <div class="header-line">
          <ion-avatar>
            <Avatar :user="post.owner" />
          </ion-avatar>
          <div class="post-header-content">
            <div class="header-row">
              <UsernameTag :user="post.owner" />
              <DateDisplay :date="post.createdAt" class="post-date" />
            </div>
            <span v-show="!isMusicPost" class="post-title">{{ post.title }}</span>
          </div>
        </div>
      </ion-card-header>
      <ion-card-content>
        <template v-if="!isMusicPost">
          <p>{{ post.content }}</p>
          <ion-img v-show="post.imageUrl" :src="post.imageUrl" :alt="`Post ${post.title}`" />

          <div class="settings-container">
            <div class="post-actions">
              <ion-button
                class="like-button"
                fill="clear"
                size="default"
                @click.stop="toggleLikePost(post.slug)"
                :class="{ 'heart-active': isLiked }">
                <ion-icon :src="customLikeIcon" />
                <ion-note v-show="formatCount(displayedLikeCount)">{{ formatCount(displayedLikeCount) }}</ion-note>
              </ion-button>

              <ion-button
                class="comment-button"
                fill="clear"
                size="default"
                @click.stop="redirectToPostComments(post.slug)">
                <ion-icon :src="customCommentIcon" />
                <ion-note v-show="formatCount(post.commentCount)">{{ formatCount(post.commentCount) }}</ion-note>
              </ion-button>

              <ion-button
                class="repost-button"
                fill="clear"
                size="default"
                @click.stop="toggleRepostPost(post.slug)"
                :class="{ 'repost-active': isReposted }">
                <ion-icon :src="customRepostIcon" />
                <ion-note v-show="formatCount(post.repostCount)">{{ formatCount(post.repostCount) }}</ion-note>
              </ion-button>

              <ion-button class="export-button" fill="clear" size="default" @click.stop="openExportModal">
                <ion-icon :src="customShareIcon" />
              </ion-button>
            </div>

            <ion-button
              v-show="post.owner.slug === authStore.getAuthenticatedUserSlug"
              class="delete-button"
              fill="clear"
              size="default"
              @click.stop="openDeleteConfirm">
              <ion-icon :src="customTrashIcon" />
            </ion-button>
          </div>
        </template>

        <template v-else>
          <audio
            :src="post.audioUrl"
            ref="audioRef"
            :data-post-slug="post.slug"
            @loadedmetadata="onLoadedMetadata"
            @timeupdate="onTimeUpdate"
            @play="onAudioPlay"
            @pause="onAudioPause"
            @ended="onEnded"
            style="display: none" />

          <div class="track-card" @click="playTrack">
            <div class="track-cover">
              <img :src="post.imageUrl || '/assets/images/png/no-album-art.png'" :alt="post.audioTitle" />

              <div class="visualizer-overlay" :class="{ active: isPlaying && isCurrentTrack() }">
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
              <span class="track-duration">{{ formatTime(duration) }}</span>
              <button class="track-save-btn" @click.stop="openSaveTrack" title="Add to playlist">
                <ion-icon :icon="addOutline" />
              </button>
            </div>
          </div>

          <div class="settings-container music-actions">
            <div class="post-actions">
              <ion-button
                class="like-button"
                fill="clear"
                size="default"
                @click.stop="toggleLikePost(post.slug)"
                :class="{ 'heart-active': isLiked }">
                <ion-icon :src="customLikeIcon" />
                <ion-note v-show="formatCount(displayedLikeCount)">{{ formatCount(displayedLikeCount) }}</ion-note>
              </ion-button>

              <ion-button
                class="comment-button"
                fill="clear"
                size="default"
                @click.stop="redirectToPostComments(post.slug)">
                <ion-icon :src="customCommentIcon" />
                <ion-note v-show="formatCount(post.commentCount)">{{ formatCount(post.commentCount) }}</ion-note>
              </ion-button>

              <ion-button
                class="repost-button"
                fill="clear"
                size="default"
                @click.stop="toggleRepostPost(post.slug)"
                :class="{ 'repost-active': isReposted }">
                <ion-icon :src="customRepostIcon" />
                <ion-note v-show="formatCount(post.repostCount)">{{ formatCount(post.repostCount) }}</ion-note>
              </ion-button>

              <ion-button
                v-if="!post.audioTitle"
                class="export-button"
                fill="clear"
                size="default"
                @click.stop="openExportModal">
                <ion-icon :src="customShareIcon" />
              </ion-button>
            </div>

            <ion-button
              v-if="post.owner.slug === authStore.getAuthenticatedUserSlug"
              class="delete-button"
              fill="clear"
              size="default"
              @click.stop="openDeleteConfirm">
              <ion-icon :src="customTrashIcon" />
            </ion-button>
          </div>
        </template>
      </ion-card-content>
    </ion-card>

    <ExportCard v-if="!post.audioTitle" :item="post" type="post" ref="exportCardRef" />

    <DeleteCard
      item-type="post"
      :item-slug="post.slug"
      :delete-function="callDeletePost"
      @deleted="handlePostDeleted"
      ref="deleteCardRef" />

    <SaveTrackCard
      v-show="isMusicPost"
      :post-slug="post.slug"
      :audio-title="post.audioTitle"
      :audio-url="post.audioUrl"
      :image-url="post.imageUrl"
      :current-playlist-slug="currentPlaylistSlug"
      ref="saveTrackCardRef" />
  </ion-col>
</template>

<style scoped>
ion-card {
  margin: 0;
  padding: 0;
  border-radius: 0;
  background: transparent !important;
  --background: transparent !important;
}

ion-card-header {
  padding-bottom: 0.2em;
  padding-top: 1em;
}

ion-card-content {
  padding-bottom: 0;

  & p {
    text-align: justify;
    font-size: 1.1rem;
  }

  & ion-img {
    border-radius: 10px;
    overflow: hidden;
    margin-top: 0.3em;
  }

  .custom-audio-controls {
    display: flex;
    align-items: center;
    padding-left: 0;
    padding-right: 0;
    margin-left: 0;
    margin-right: 0;
    gap: 0;
    box-sizing: border-box;
  }

  .play-pause-icon {
    color: white;
    font-size: 24px;
    cursor: pointer;
  }

  .audio-time,
  .audio-duration {
    font-size: 1rem;
    color: var(--ion-color-medium);
    font-weight: 500;
    text-align: center;
  }

  .custom-audio-controls input[type='range'] {
    flex: 1;
    appearance: none;
    height: 4px;
    background: transparent;
    border-radius: 2px;
    outline: none;
    margin: 0 0.5em;
  }

  .custom-audio-controls input[type='range']::-webkit-slider-thumb {
    appearance: none;
    width: 12px;
    height: 12px;
    background: rgb(255, 255, 255);
    border-radius: 50%;
    cursor: pointer;
  }

  & audio::-webkit-media-controls-play-button,
  & audio::-webkit-media-controls-mute-button,
  & audio::-webkit-media-controls-volume-slider,
  & audio::-webkit-media-controls-timeline,
  & audio::-webkit-media-controls-current-time-display,
  & audio::-webkit-media-controls-time-remaining-display {
    filter: invert(1);
    opacity: 0.9;
  }

  & audio::-webkit-media-controls-play-button:hover {
    opacity: 1;
  }
}

.post-header {
  padding-bottom: 0.2em;
  padding-top: 1em;
}

.header-line {
  display: flex;
  align-items: center;
  gap: 0.6em;
}

ion-avatar {
  min-width: 40px;
  min-height: 40px;
  width: 40px;
  height: 40px;
  flex-shrink: 0;
}

ion-avatar img {
  border-radius: 50% !important;
  object-fit: cover;
  width: 100%;
  height: 100%;
}

.post-header-content {
  display: flex;
  flex-direction: column;
  max-width: 100%;
  width: 100%;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.header-row {
  display: flex;
  justify-content: space-between;
  max-width: 100%;
}

.post-date {
  font-size: 1.2rem;
  font-weight: 400;
  color: var(--ion-color-medium);
  width: auto;
}

.post-title {
  font-size: 1.1rem;
  color: white;
  font-weight: 400;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}

.settings-container {
  display: flex;
  justify-content: space-between;
  align-items: center;

  & ion-button {
    margin: 0;
    font-weight: bold;
    color: white;
    --color: white;
  }

  & ion-note {
    font-size: 1rem;
    font-weight: 500;
    color: var(--ion-color-medium);
    margin-left: 0.3em;
    padding: 0;
    line-height: 1rem;
    height: 0.8rem;
    display: inline-block;
    vertical-align: middle;
  }

  & ion-icon {
    vertical-align: middle;
    display: inline-block;
    line-height: 1;
  }
}

.post-actions {
  display: flex;
  align-items: center;
}

.like-button {
  --padding-start: 0;
  --padding-end: 0;
}

.comment-button {
  --padding-end: 0;
}

.repost-button {
  --padding-end: 0;
}

.export-button {
  --padding-end: 0;
  --color: #60a5fa;
  color: #60a5fa;
}

.save-button {
  --padding-end: 0;
}

.settings-container ion-button.heart-active {
  --color: var(--ion-color-primary);
  color: var(--ion-color-primary);
}

.settings-container ion-button.repost-active {
  --color: var(--ion-color-primary);
  color: var(--ion-color-primary);
}

.delete-button {
  --padding-start: 0;
  --padding-end: 0;
  --color: #f87171;
  color: #f87171;
}

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
  gap: 12px;
  flex-shrink: 0;
}

.track-duration {
  font-size: 0.9rem;
  font-weight: 600;
  color: #9aa3ad;
  font-variant-numeric: tabular-nums;
  min-width: 40px;
  text-align: right;
}

.track-save-btn {
  appearance: none;
  border: 1px solid rgba(59, 130, 246, 0.25);
  background: rgba(59, 130, 246, 0.1);
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #60a5fa;
}

.track-save-btn ion-icon {
  font-size: 20px;
}

.track-save-btn:hover {
  background: rgba(59, 130, 246, 0.2);
  border-color: rgba(59, 130, 246, 0.4);
  transform: scale(1.05);
  box-shadow: 0 0 12px rgba(59, 130, 246, 0.3);
}

.track-save-btn:active {
  transform: scale(0.95);
}

.music-actions {
  margin-top: 0;
}

@media (max-width: 768px) {
  .track-card {
    padding: 0.6rem;
    gap: 0.875rem;
    border-radius: 16px;
  }

  .track-cover {
    width: 56px;
    height: 56px;
  }

  .visualizer-bar {
    width: 2.5px;
    height: 10px;
    gap: 2px;
  }

  .track-title {
    font-size: 0.95rem;
  }

  .track-artist {
    font-size: 0.8rem;
  }

  .track-duration {
    font-size: 0.85rem;
    min-width: 36px;
  }

  .track-save-btn {
    width: 32px;
    height: 32px;
  }

  .track-save-btn ion-icon {
    font-size: 18px;
  }
}
</style>
