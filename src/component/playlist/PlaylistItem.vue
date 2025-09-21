<script setup lang="ts">
import { defineProps } from 'vue';
import { useRouter } from 'vue-router';
import { Playlist } from '@/entity/Playlist';
import { IonItem, IonLabel, IonThumbnail, IonImg, IonIcon } from '@ionic/vue';
import DateDisplay from '@/component/date/DateDisplay.vue';
import { buildOutline, pencilOutline } from 'ionicons/icons';

const props = defineProps<{
  playlist: Playlist;
}>();

const router = useRouter();

const goToPlaylist = () => {
  router.push(`/playlists/${props.playlist.slug}`);
};
</script>

<template>
  <IonItem button detail @click="goToPlaylist">
    <IonThumbnail slot="start" v-if="playlist.imageUrl">
      <IonImg :src="playlist.imageUrl" />
    </IonThumbnail>

    <IonLabel>
      <h2>{{ playlist.name }}</h2>
      <p>{{ playlist.description }}</p>
      <IonIcon :icon="buildOutline" /> <DateDisplay :date="playlist.createdAt" class="date" />
      <p>{{ playlist.numberOfTracks }} Tracks</p>
      <p>By {{ playlist.owner.username }}</p>
      <IonIcon :icon="pencilOutline" /> <DateDisplay :date="playlist.updatedAt" class="date" />
    </IonLabel>
  </IonItem>
</template>

<style scoped>
.date {
  font-size: 1.2rem;
  font-weight: 400;
  color: var(--ion-color-medium);
  width: auto;
}
</style>
