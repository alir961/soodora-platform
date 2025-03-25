<script setup lang="ts">
import {
  IonCard,
  IonButton,
  IonCardContent,
  IonCardHeader,
  IonCardTitle,
  IonCol,
  IonCardSubtitle,
  IonImg,
  IonIcon,
  alertController,
  toastController,
} from '@ionic/vue';
import { PropType, Ref, ref } from 'vue';
import { Post } from '@/entity/Post';
import { trashOutline } from 'ionicons/icons';
import { deletePost } from '@/client/postClient';

defineProps({
  post: {
    required: true,
    type: Object as PropType<Post>,
  },
});

const playAudio = (audio: Post) => {
  const audioElement = document.createElement('audio');
  audioElement.src = audio.audioUrl;
  audioElement.play();
};

const focusedPost: Ref<Post | null> = ref(null);

const focusPost = (post: Post) => {
  focusedPost.value = focusedPost.value === post ? null : post;
};

const deleteCurrentPost = async (slug: string) => {
  const alert = await alertController.create({
    header: 'Confirm Post Deletion',
    message: 'Are you sure you want to delete this post?',
    buttons: [
      { text: 'Cancel', role: 'cancel' },
      {
        text: 'Delete',
        handler: async () => {
          try {
            await deletePost(slug);
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
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4">
    <ion-card @click="focusPost(post)" :class="{ focused: focusedPost === post }">
      <div v-if="focusedPost === post" class="delete-button-container">
        <ion-button fill="clear" size="small" @click.stop="deleteCurrentPost(post.slug)">
          <ion-icon :icon="trashOutline" />
        </ion-button>
      </div>
      <ion-card-header>
        <div>
          <ion-card-title class="header-title">{{ post.owner.username }}</ion-card-title>
          <ion-card-subtitle>{{ post.type.replaceAll('_', ' ') }}</ion-card-subtitle>
        </div>
      </ion-card-header>
      <ion-card-content>{{ post.content }}</ion-card-content>
      <ion-card-content class="image-content">
        <div class="image">
          <IonImg :src="post.imageUrl" :alt="`Post ${post.title}`" />
        </div>
        <ion-card-title>{{ post.title }}</ion-card-title>
      </ion-card-content>
      <ion-card-content class="audio-content">
        <div v-if="post.audioUrl">
          <ion-button v-if="focusedPost === post" expand="block" @click.stop="playAudio(post)"> Play Audio </ion-button>
          <audio v-if="focusedPost === post" :src="post.audioUrl" controls></audio>
        </div>
      </ion-card-content>
    </ion-card>
  </ion-col>
</template>

<style scoped>
.focused {
  border: 0.2em solid #3880ff;
  transform: scale(1.05);
}

.header-title {
  font-size: 2em;
  margin-bottom: 0.2em;
}

.image-content {
  padding-top: 0;
}

.image {
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 1.5em;
}

.audio-content {
  padding-top: 0;
}

audio {
  width: 100%;
  margin-top: 10px;
}

.delete-button-container {
  position: absolute;
  top: 5px;
  right: 5px;
  z-index: 10;
}
</style>
