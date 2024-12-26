<script setup lang="ts">
import { IonButton, IonCard, IonCardContent, IonCardHeader, IonCardSubtitle, IonCardTitle, IonCol } from '@ionic/vue';
import { PropType, Ref, ref } from 'vue';

interface Audio {
  title: string;
  genre: string;
  imageUrl: string;
  duration: number;
  fileUrl: string;
}

defineProps({
  audio: {
    required: true,
    type: Object as PropType<Audio>,
  },
});

const focusedAudio: Ref<Audio | null> = ref(null);

const focusAudio = (audio: Audio) => {
  focusedAudio.value = focusedAudio.value === audio ? null : audio;
};

const formatDuration = (duration: number): string => {
  const minutes = Math.floor(duration / 60);
  const seconds = duration % 60;
  return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
};

const playAudio = (audio: Audio) => {
  const audioElement = document.createElement('audio');
  audioElement.src = audio.fileUrl;
  audioElement.play();
};
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4">
    <ion-card @click="focusAudio(audio)" :class="{ focused: focusedAudio === audio }">
      <img :src="audio.imageUrl" :alt="`Product ${audio.title}`" />
      <ion-card-header>
        <ion-card-title>{{ audio.title }}</ion-card-title>
        <ion-card-subtitle>{{ audio.genre }}</ion-card-subtitle>
      </ion-card-header>
      <ion-card-content>
        <p>Duration: {{ formatDuration(audio.duration) }}</p>
        <ion-button v-if="focusedAudio === audio" expand="block" @click.stop="playAudio(audio)">Play Audio</ion-button>
        <audio v-if="focusedAudio === audio" :src="audio.fileUrl" controls></audio>
      </ion-card-content>
    </ion-card>
  </ion-col>
</template>

<style scoped>
.focused {
  border: 0.2em solid #3880ff;
  transform: scale(1.05);
}

ion-card img {
  width: 100%;
  height: auto;
  border-radius: 10px;
}

audio {
  width: 100%;
  margin-top: 10px;
}
</style>