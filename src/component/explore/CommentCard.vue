<script setup lang="ts">
import { heart, heartOutline, trash } from 'ionicons/icons';
import { IonButton, IonIcon, IonItem, IonLabel } from '@ionic/vue';
import { callDeleteComment, callGetCommentLikes, callLikeComment, callUnlikeComment } from '@/client/postClient';
import { User } from '@/entity/User';
import { PropType, ref, Ref, watch } from 'vue';
import { Comment } from '@/entity/Post';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/reactiveStorage';

const props = defineProps({
  comment: {
    required: true,
    type: Object as PropType<Comment>,
  },
});
const emit = defineEmits(['comment-deleted']);

const router = useRouter();
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

const redirectUserProfile = async (user: User) => {
  await router.push(`/user/${user.slug}`);
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
  <ion-item class="ion-padding-vertical">
    <ion-label class="ion-text-wrap">
      <h3 @click="redirectUserProfile(comment.user)">{{ comment.user.username }}</h3>
      <p>{{ comment.content }}</p>
      <small>{{ new Date(comment.createdAt).toLocaleString() }}</small>
    </ion-label>

    <ion-button fill="clear" @click="toggleLike(comment.slug)">
      <ion-icon :icon="isLiked ? heart : heartOutline" slot="icon-only" />
    </ion-button>

    <ion-button
      v-if="comment.user.slug === authStore.getAuthenticatedUserSlug"
      fill="clear"
      color="danger"
      @click="handleDelete(comment.slug)">
      <ion-icon :icon="trash" slot="icon-only" />
    </ion-button>
  </ion-item>
</template>
