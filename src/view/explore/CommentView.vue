<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { IonButton, IonContent, IonIcon, IonInput, IonItem, IonLabel } from '@ionic/vue';
import { add, heart, heartOutline, trash } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import {
  callCreateComment,
  callDeleteComment,
  callGetComments,
  callLikeComment,
  callUnlikeComment,
} from '@/client/postClient';
import { Comment } from '@/entity/Post';
import { User } from '@/entity/User';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const postSlug = route.params.slug as string;

const comments = ref<Comment[]>([]);
const likedComments = ref<Set<string>>(new Set());
const newComment = ref<string>('');

const loadComments = async () => {
  try {
    comments.value = await callGetComments(postSlug);
    likedComments.value.clear();
  } catch (error) {
    console.error(error);
  }
};

const toggleLike = async (commentSlug: string) => {
  try {
    if (likedComments.value.has(commentSlug)) {
      await callUnlikeComment(postSlug, commentSlug);
      likedComments.value.delete(commentSlug);
    } else {
      await callLikeComment(postSlug, commentSlug);
      likedComments.value.add(commentSlug);
    }
  } catch (error) {
    console.error(error);
  }
};

const handleDelete = async (commentSlug: string) => {
  try {
    await callDeleteComment(postSlug, commentSlug);
    comments.value = comments.value.filter((c) => c.slug !== commentSlug);
  } catch (error) {
    console.error(error);
  }
};

const handleCreate = async () => {
  const content = newComment.value.trim();
  if (!content) return;

  try {
    await callCreateComment(postSlug, content);
    newComment.value = '';
    await loadComments();
  } catch (error) {
    console.error(error);
  }
};

const redirectUserProfile = async (user: User) => {
  await router.push(`/user/${user.slug}`);
};

onMounted(loadComments);
</script>

<template>
  <ApplicationViewport title="Comments Section" back-button>
    <ion-content class="ion-padding">
      <ion-item>
        <ion-input v-model="newComment" placeholder="Écris un commentaire..." clearInput />
        <ion-button @click="handleCreate">
          <ion-icon :icon="add" slot="icon-only" />
        </ion-button>
      </ion-item>

      <ion-item v-for="comment in comments" :key="comment.slug" class="ion-padding-vertical">
        <ion-label class="ion-text-wrap">
          <h3 @click="redirectUserProfile(comment.user)">{{ comment.user.username }}</h3>
          <p>{{ comment.content }}</p>
          <small>{{ new Date(comment.createdAt).toLocaleString() }}</small>
        </ion-label>

        <ion-button fill="clear" @click="toggleLike(comment.slug)">
          <ion-icon :icon="likedComments.has(comment.slug) ? heart : heartOutline" slot="icon-only" />
        </ion-button>

        <ion-button
          fill="clear"
          color="danger"
          @click="handleDelete(comment.slug)"
          v-if="comment.user.slug === authStore.getAuthenticatedUserSlug">
          <ion-icon :icon="trash" slot="icon-only" />
        </ion-button>
      </ion-item>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
ion-item {
  border-bottom: 1px solid #ccc;
}
</style>
