<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import { IonButton, IonContent, IonIcon, IonInput, IonItem } from '@ionic/vue';
import { arrowBackOutline, arrowUp } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { callCreateComment, callGetComments } from '@/client/postClient';
import { Comment } from '@/entity/Post';
import CommentCard from '@/component/explore/CommentCard.vue';

import { Keyboard } from '@capacitor/keyboard';

const route = useRoute();
const postSlug = route.params.slug as string;

const comments = ref<Comment[]>([]);
const commentInput = ref<string>('');

const isKeyboardOpen = ref(false);
const keyboardHeight = ref(0);

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

onMounted(() => {
  loadComments();
  Keyboard.addListener('keyboardWillShow', (info) => {
    isKeyboardOpen.value = true;
    keyboardHeight.value = info.keyboardHeight || 300;
  });
  Keyboard.addListener('keyboardWillHide', () => {
    isKeyboardOpen.value = false;
    keyboardHeight.value = 0;
  });
});
</script>

<template>
  <ApplicationViewport title="Comments Section" back-button>
    <div class="top-bar">
      <ion-icon :icon="arrowBackOutline" class="back-btn" style="cursor: pointer;" @click="$router.back()" />
      <span class="top-bar-title">Join the Conversation</span>
      <ion-icon class="back-btn" style="visibility: hidden;" />
    </div>
    <div v-for="comment in comments" :key="comment.slug" class="comment-wrapper">
      <CommentCard :comment="comment" @comment-deleted="loadComments" />
    </div>
    <div v-if="isKeyboardOpen" class="keyboard-backdrop" :style="{ height: `${keyboardHeight}px` }"></div>
    <div class="comment-input-wrapper">
      <ion-item lines="none" class="comment-input">
        <ion-input v-model="commentInput" placeholder="Drop a comment" clearInput @keyup.enter="createComment" />
        <ion-button class="send-comment-btn" @click="createComment">
          <ion-icon :icon="arrowUp" slot="icon-only" />
        </ion-button>
      </ion-item>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.top-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 1em;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.back-btn {
  color: var(--ion-color-light);
  font-size: 2em;
}

.top-bar-title {
  font-size: 1.2em;
  font-weight: bold;
  color: var(--ion-color-light);
  margin: 0 auto;
}

.comment-input {
  border-radius: 25px 25px 25px 25px;
  border: 1px solid var(--ion-color-primary);
  padding-right: 0.4em;
  --inner-padding-end: 0px;
}

.send-comment-btn {
  --background: var(--ion-color-primary);
  --color: white;
  --border-radius: 50%;
  height: 2.5em;
  width: 2.8em;
}

.comment-wrapper {
  width: 100%;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.comment-input-wrapper {
  position: sticky;
  bottom: env(safe-area-inset-bottom);
  left: 0;
  width: 100%;
  background-color: black;
  z-index: 100;
  padding: 1em;
  box-sizing: border-box;
  transition: bottom 0.3s ease;
}

.keyboard-backdrop {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background-color: rgb(201, 23, 23);
  z-index: 90;
}
</style>