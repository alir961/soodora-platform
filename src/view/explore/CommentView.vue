<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { IonButton, IonContent, IonIcon, IonInput, IonItem } from '@ionic/vue';
import { add } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { callCreateComment, callGetComments } from '@/client/postClient';
import { Comment } from '@/entity/Post';
import CommentCard from '@/component/explore/CommentCard.vue';

const route = useRoute();
const postSlug = route.params.slug as string;

const comments = ref<Comment[]>([]);
const commentInput = ref<string>('');

const loadComments = async () => {
  try {
    comments.value = await callGetComments(postSlug);
  } catch (error) {
    console.error(error);
  }
};

const createComment = async () => {
  const content = commentInput.value.trim();

  if (!content) return;
  try {
    await callCreateComment(postSlug, content);
    commentInput.value = '';
    await loadComments();
  } catch (error) {
    console.error(error);
  }
};

onMounted(loadComments);
</script>

<template>
  <ApplicationViewport title="Comments Section" back-button>
    <ion-content class="ion-padding">
      <ion-item>
        <ion-input v-model="commentInput" placeholder="Écris un commentaire..." clearInput />
        <ion-button @click="createComment">
          <ion-icon :icon="add" slot="icon-only" />
        </ion-button>
      </ion-item>

      <CommentCard v-for="comment in comments" :key="comment.slug" :comment="comment" @comment-deleted="loadComments" />
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
ion-item {
  border-bottom: 1px solid #ccc;
}
</style>
