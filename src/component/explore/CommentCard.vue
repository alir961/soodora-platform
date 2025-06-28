<script setup lang="ts">
import trashIcon from '@/assets/icons/trash.svg';
import { heart, heartOutline } from 'ionicons/icons';
import {
  IonButton,
  IonIcon,
  IonItem,
  IonLabel,
  IonAvatar,
  IonItemSliding,
  IonItemOptions,
  IonItemOption,
} from '@ionic/vue';
import { callDeleteComment, callGetCommentLikes, callLikeComment, callUnlikeComment } from '@/client/postClient';
import { PropType, ref, Ref, watch } from 'vue';
import { Comment } from '@/entity/Post';
import { useAuthStore } from '@/store/reactiveStorage';
import UsernameTag from '@/component/user/UsernameTag.vue';
import Avatar from '@/component/user/AvatarItem.vue';
import DateDisplay from '@/component/date/DateDisplay.vue';

const props = defineProps({
  comment: {
    required: true,
    type: Object as PropType<Comment>,
  },
});

const emit = defineEmits(['comment-deleted']);
const authStore = useAuthStore();
const isLiked: Ref<boolean> = ref(false);

const loadCommentLikes = async (postSlug: string, commentSlug: string) => {
  try {
    const likes = await callGetCommentLikes(postSlug, commentSlug);
    const userSlug = authStore.getAuthenticatedUserSlug;

    if (likes.some((like) => like === userSlug)) isLiked.value = true;
  } catch (error) {
    console.error('Unable to load likes', error);
  }
};

const toggleLike = async (commentSlug: string) => {
  try {
    if (isLiked.value) {
      await callUnlikeComment(props.comment.postSlug, commentSlug);
      isLiked.value = false;
    } else {
      await callLikeComment(props.comment.postSlug, commentSlug);
      isLiked.value = true;
    }
  } catch (error) {
    console.error(error);
  }
};

const handleDelete = async (commentSlug: string) => {
  try {
    await callDeleteComment(props.comment.postSlug, commentSlug);
    emit('comment-deleted');
  } catch (error) {
    console.error(error);
  }
};

watch(
  () => authStore.getAuthenticatedUser,
  (newUser) => {
    if (newUser) {
      const comment: Comment = props.comment;

      loadCommentLikes(comment.postSlug, comment.slug);
    }
  },
  { immediate: true }
);
</script>

<template>
  <ion-item-sliding>
    <ion-item lines="none" class="comment-card">
      <ion-label>
        <div class="comment-header">
          <ion-avatar>
            <Avatar :user="comment.user" />
          </ion-avatar>
          <div class="user-info">
            <UsernameTag :user="comment.user" />
            <DateDisplay :date="comment.createdAt" class="comment-date" />
          </div>
        </div>
        <div class="comment-content-wrapper">
          <div class="content-with-heart">
            <p>{{ comment.content }}</p>
            <ion-button fill="clear" class="heart-button" @click="toggleLike(comment.slug)">
              <ion-icon :icon="isLiked ? heart : heartOutline" />
            </ion-button>
          </div>
        </div>
      </ion-label>
    </ion-item>
    <ion-item-options side="end">
      <ion-item-option color="danger" expandable @click="handleDelete(comment.slug)">
        <img :src="trashIcon" alt="delete" class="delete-icon" />
      </ion-item-option>
    </ion-item-options>
  </ion-item-sliding>
</template>

<style scoped>
.comment-card {
  padding: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  padding-left: 0%;
}

.comment-header ion-avatar {
  width: 32px;
  height: 32px;
  margin-right: 8px;
  flex-shrink: 0;
}

ion-label {
  padding-left: 0;
  padding-right: 0;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.comment-date {
  font-size: 1.2rem;
  font-weight: 400;
  color: var(--ion-color-medium);
  margin-left: auto;
}

ion-label p {
  font-size: 1.1rem;
  word-break: break-word;
  overflow-wrap: anywhere;
}

.comment-content-wrapper {
  margin-left: 40px;
  color: var(--ion-color-medium);
}

.content-with-heart {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--heart-gap, 0em);
}

.heart-button {
  --padding-start: 0;
  --padding-end: 0;
  --padding-top: 0;
  --padding-bottom: 0;
  height: 100%;
  padding: 0 0 0 0.3em;
  display: flex;
  align-items: center;
}

.delete-icon {
  width: 24px;
  height: 24px;
  filter: brightness(0) invert(1);
  margin-right: 1em;
  margin-left: 1em;
}
</style>
