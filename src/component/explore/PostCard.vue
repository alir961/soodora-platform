<script setup lang="ts">
import { IonCard, IonButton, IonCardContent, IonCardHeader, IonCardTitle, IonCol, IonCardSubtitle, IonImg } from '@ionic/vue';
import { PropType, Ref, ref } from 'vue';
import { Post } from '@/entity/Post';

defineProps({
  post: {
    required: true,
    type: Object as PropType<Post>,
  },
});

const formatDuration = (duration: number): string => {
  const minutes = Math.floor(duration / 60);
  const seconds = duration % 60;
  return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
};

const playAudio = (audio: Post) => {
  const audioElement = document.createElement('audio');
  audioElement.src = audio.audioUrl;
  audioElement.play();
};

const focusedPost: Ref<Post | null> = ref(null);

const focusPost = (post: Post) => {
  focusedPost.value = focusedPost.value === post ? null : post;
};
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4">
    <ion-card @click="focusPost(post)" :class="{ focused: focusedPost === post }">
      <ion-card-header>
        <div>
          <ion-card-title class="header-title" >{{ post.owner.username }}</ion-card-title>
          <ion-card-subtitle>{{ post.type.replace(/_/g, ' ') }}</ion-card-subtitle>
        </div>
      </ion-card-header>
      <ion-card-content>{{ post.content }}</ion-card-content>
      <ion-card-content>
        <div class="rounded-image">
          <IonImg :src="post.imageUrl" :alt="`Post ${post.title}`" />
        </div>
        <ion-card-title>{{ post.title }}</ion-card-title>
      </ion-card-content>
      <ion-card-content v-if="post.audioUrl">
        <ion-button v-if="focusedPost === post" expand="block" @click.stop="playAudio(post)">Play Audio</ion-button>
        <audio v-if="focusedPost === post" :src="post.audioUrl" controls></audio>
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

.rounded-image {
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 2.5em;
}

audio {
  width: 100%;
  margin-top: 10px;
}

</style>
