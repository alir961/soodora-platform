<script setup lang="ts">
import {
  alertController,
  AlertInput,
  IonButton,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCol,
  IonIcon,
  IonImg,
  IonNote,
  IonAvatar,
  toastController,
} from '@ionic/vue';
import { PropType, Ref, ref, watch, computed, onMounted } from 'vue';
import { Post } from '@/entity/Post';
import {
  callPostUnlike,
  callDeletePost,
  callGetPostLikes,
  callPostLike,
  callPostUnrepost,
  callPostRepost,
  callGetPostReposts,
} from '@/client/postClient';
import { useAuthStore } from '@/store/reactiveStorage';
import { createPostCanvas, downloadCanvas, ExportTheme } from '@/graphics/canvasExport';
import { Router, useRouter } from 'vue-router';
import customLikeIcon from '@/assets/icons/like.svg';
import customCommentIcon from '@/assets/icons/comment.svg';
import customRepostIcon from '@/assets/icons/repost.svg';
import customShareIcon from '@/assets/icons/share.svg';
import customTrashIcon from '@/assets/icons/trash.svg';
import customPlayIcon from '@/assets/icons/play.svg';
import customPauseIcon from '@/assets/icons/pause.svg';
import UsernameTag from '@/component/user/UsernameTag.vue';

const router: Router = useRouter();
const authStore = useAuthStore();

const props = defineProps({
  post: {
    required: true,
    type: Object as PropType<Post>,
  },
});

const emit = defineEmits(['post-deleted', 'post-like', 'post-unlike', 'post-repost', 'post-unrepost']);

const isLiked: Ref<boolean> = ref(false);
const isReposted: Ref<boolean> = ref(false);

const audioRef = ref<HTMLAudioElement | null>(null);
const isPlaying = ref(false);
const duration = ref(0);
const currentTime = ref(0);

const progressBackground = computed(() => {
  if (duration.value === 0) return '';
  const percent = (currentTime.value / duration.value) * 100;
  return `background: linear-gradient(90deg, var(--ion-color-primary) ${percent}%, #161515 ${percent}%)`;
});

const togglePlay = () => {
  const audio = audioRef.value;
  if (!audio) return;
  if (audio.paused) {
    audio.play();
    isPlaying.value = true;
  } else {
    audio.pause();
    isPlaying.value = false;
  }
};

const onLoadedMetadata = () => {
  const audio = audioRef.value;
  if (audio) duration.value = audio.duration;
};

const onTimeUpdate = () => {
  const audio = audioRef.value;
  if (audio) currentTime.value = audio.currentTime;
};

const seekAudio = () => {
  const audio = audioRef.value;
  if (audio) audio.currentTime = currentTime.value;
};

const exportThemes = [
  {
    label: 'Black',
    type: 'radio',
    value: 'black',
  },
  {
    label: 'White',
    type: 'radio',
    value: 'white',
  },
  {
    label: 'Transparent',
    type: 'radio',
    value: 'transparent',
  },
] as AlertInput[];

const exportPost = async (post: Post) => {
  const alert = await alertController.create({
    header: 'Select theme for export',
    inputs: exportThemes,
    buttons: [
      {
        text: 'Cancel',
        role: 'cancel',
      },
      {
        text: 'Export',
        handler: async (selectedTheme: string) => {
          if (!selectedTheme) return;
          try {
            const canvas = await createPostCanvas(post, selectedTheme as ExportTheme);

            downloadCanvas(canvas);
          } catch (err) {
            console.error('Export failed:', err);
          }
        },
      },
    ],
  });
  await alert.present();
};

const askDeletePost = async (slug: string) => {
  const alert = await alertController.create({
    header: 'Confirm Post Deletion',
    message: 'Are you sure you want to delete this post?',
    buttons: [
      { text: 'Cancel', role: 'cancel' },
      {
        text: 'Delete',
        handler: async () => {
          try {
            await callDeletePost(slug);
            emit('post-deleted', slug);
          } catch (error) {
            const toast = await toastController.create({
              message: 'Failed to delete post.',
              duration: 1500,
              position: 'top',
            });
            await toast.present();
          }
        },
      },
    ],
  });
  await alert.present();
};

const loadLikes = async (postSlug: string) => {
  try {
    const likes = await callGetPostLikes(postSlug);
    const userSlug = authStore.getAuthenticatedUserSlug;

    if (likes.some((like) => like === userSlug)) isLiked.value = true;
  } catch (error) {
    console.error('Unable to load likes', error);
  }
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
  try {
    if (isLiked.value && props.post) {
      await callPostUnlike(postSlug);
      isLiked.value = false;
      emit('post-unlike');
    } else {
      await callPostLike(postSlug);
      isLiked.value = true;
      emit('post-like');
    }
  } catch (error) {
    console.error('Unable to update like', error);
  }
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
  } catch (error) {
    console.error('Unable to update repost', error);
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
});

function formatPostDate(dateInput: string | Date): string {
  const date = typeof dateInput === 'string' ? new Date(dateInput) : dateInput;
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();

  const diffMinutes = Math.floor(diffMs / (1000 * 60));
  if (diffMinutes < 60) {
    return `${diffMinutes}m`;
  }

  const diffHours = Math.floor(diffMinutes / 60);
  if (diffHours < 24) {
    return `${diffHours}h`;
  }

  const diffDays = Math.floor(diffHours / 24);
  if (diffDays < 8) {
    return `${diffDays}d`;
  }

  // Otherwise, show date as DD/MM/YYYY
  return date.toLocaleDateString();
}

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
</script>

<template>
  <ion-col size="12" size-md="7" size-lg="5">
    <ion-card>
      <ion-card-header class="post-header">
        <div class="header-line">
          <ion-avatar>
            <img :src="post.owner.avatarUrl || '/assets/images/svg/avatar.svg'" alt="avatar" />
          </ion-avatar>
          <div class="post-header-content">
            <div class="header-row">
              <UsernameTag :user="post.owner" />
              <span class="post-date">{{ formatPostDate(post.createdAt) }}</span>
            </div>
            <span class="post-title">{{ post.title }}</span>
          </div>
        </div>
      </ion-card-header>
      <ion-card-content>
        <p>{{ post.content }}</p>
        <ion-img v-if="post.imageUrl" :src="post.imageUrl" :alt="`Post ${post.title}`" />
        <div v-if="post.audioUrl" class="audio-content">
          <audio :src="post.audioUrl" ref="audioRef" @loadedmetadata="onLoadedMetadata" @timeupdate="onTimeUpdate" />
          <div class="custom-audio-controls">
            <ion-icon :src="isPlaying ? customPauseIcon : customPlayIcon" @click="togglePlay" class="play-pause-icon" />
            <span class="audio-time">{{ formatTime(currentTime) }}</span>
            <input
              type="range"
              min="0"
              :max="duration"
              step="0.1"
              v-model="currentTime"
              @input="seekAudio"
              :style="progressBackground" />
            <span class="audio-duration">{{ formatTime(duration) }}</span>
          </div>
        </div>
        <div class="settings-container">
          <div class="post-actions">
            <ion-button
              class="like-button"
              fill="clear"
              size="default"
              @click.stop="toggleLikePost(post.slug)"
              :class="{ 'heart-active': isLiked }">
              <ion-icon :src="customLikeIcon" />
              <ion-note v-if="formatCount(post.likeCount)">{{ formatCount(post.likeCount) }}</ion-note>
            </ion-button>

            <ion-button
              class="comment-button"
              fill="clear"
              size="default"
              @click.stop="redirectToPostComments(post.slug)">
              <ion-icon :src="customCommentIcon" />
              <ion-note v-if="formatCount(post.commentCount)">{{ formatCount(post.commentCount) }}</ion-note>
            </ion-button>

            <ion-button
              class="repost-button"
              fill="clear"
              size="default"
              @click.stop="toggleRepostPost(post.slug)"
              :class="{ 'repost-active': isReposted }">
              <ion-icon :src="customRepostIcon" />
              <ion-note v-if="formatCount(post.repostCount)">{{ formatCount(post.repostCount) }}</ion-note>
            </ion-button>

            <ion-button fill="clear" size="default" @click.stop="exportPost(post)">
              <ion-icon :src="customShareIcon" />
            </ion-button>
          </div>
          <ion-button
            v-if="post.owner.slug === authStore.getAuthenticatedUserSlug"
            fill="clear"
            size="default"
            @click.stop="askDeletePost(post.slug)"
            class="delete-button">
            <ion-icon :src="customTrashIcon" />
          </ion-button>
        </div>
      </ion-card-content>
    </ion-card>
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
}
</style>
