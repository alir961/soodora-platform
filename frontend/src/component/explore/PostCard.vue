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
import { audioPlayerService } from '@/services/audioPlayerService';
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
import ExportCard from '@/component/actions/ExportCard.vue';
import DeleteCard from '@/component/actions/DeleteCard.vue';
import SaveTrackCard from '@/component/actions/SaveTrackCard.vue';
import TrackCard from '@/component/music/TrackCard.vue';

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

const isMusicPost = computed(() => {
  return props.post.audioUrl && props.post.audioTitle;
});

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

  const stopWatch = watch(
    () => [likeStore.liked[props.post.slug], likeStore.counts[props.post.slug]],
    () => {
      isLiked.value = likeStore.isLiked(props.post.slug);
    }
  );

  // Listen for play-next-track event from NowPlayingBar
  const handlePlayNext = async (e: Event) => {
    const { slug } = (e as CustomEvent).detail || {};
    if (slug === props.post.slug && props.post.audioUrl) {
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
    }
  };
  document.addEventListener('play-next-track', handlePlayNext as EventListener);

  onUnmounted(() => {
    stopWatch();
    document.removeEventListener('play-next-track', handlePlayNext as EventListener);
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
          <TrackCard :post="post" @save-track="openSaveTrack" />

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

.music-actions {
  margin-top: 0;
}
</style>
