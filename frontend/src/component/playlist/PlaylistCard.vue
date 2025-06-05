<script setup lang="ts">
import { IonCard, IonCardContent, IonCardHeader, IonCardSubtitle, IonCardTitle, IonCol } from '@ionic/vue';
import { useRouter } from 'vue-router';
import { Playlist } from '@/entity/Playlist';
import { PropType } from 'vue';

const router = useRouter();

const goToPlaylistDetails = (slug: string) => {
  router.push({ name: 'PlaylistDetails', params: { slug } });
};

defineProps({
  playlist: {
    required: true,
    type: Object as PropType<Playlist>,
  },
});
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4">
    <ion-card @click="goToPlaylistDetails(playlist.slug)">
      <div id="card-text">
        <ion-card-header>
          <ion-card-title>{{ playlist.name }}</ion-card-title>
          <ion-card-subtitle>{{ playlist.privacy }}</ion-card-subtitle>
        </ion-card-header>
        <ion-card-content>
          <p>{{ playlist.description }}</p>
          <p>Tracks: {{ playlist.numberOfTracks }}</p>
        </ion-card-content>
      </div>
      <div id="card-image">
        <img id="image" :src="playlist.imageUrl" :alt="`Playlist ${playlist.name} thumbnail`" />
      </div>
    </ion-card>
  </ion-col>
</template>

<style scoped>
ion-card {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  min-height: 15vh;
  margin: 0 10px 15px 10px;

  & #card-text {
    height: 100%;
    width: 60%;
    margin: 0 5px 10px 5px !important;
  }

  & #card-image {
    width: 40%;
    height: 80%;
    display: flex;
    justify-content: center;
    align-items: center;
    padding-right: 3%;
  }

  & #image {
    height: 100%;
    border-radius: 20px;
  }
}

ion-card-title {
  font-size: 1.5rem;
}

ion-card-subtitle {
  color: #666;
}
</style>
