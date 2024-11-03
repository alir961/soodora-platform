<script setup lang="ts">
import { Ref, ref } from 'vue';
import { IonButton, IonIcon, onIonViewWillEnter } from '@ionic/vue';
import { arrowBack } from 'ionicons/icons';
import { useRoute, useRouter } from 'vue-router';
import { retrievePlaylistBySlug } from '@/client/playlistClient';
import { Playlist } from '@/entity/Playlist';
import PlaylistDetails from '@/component/playlist/PlaylistDetails.vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const route = useRoute();
const router = useRouter();

const slug: string = route.params.slug as string;

const playlist: Ref<Playlist | null> = ref(null);

const loadPlaylist = async () => {
  try {
    playlist.value = await retrievePlaylistBySlug(slug);
  } catch (error) {
    console.error('Failed to load playlist:', error);
  }
};

const goBack = () => {
  document.querySelectorAll('[aria-hidden="true"]').forEach((element) => {
    element.removeAttribute('aria-hidden');
  });
  router.back();
};

onIonViewWillEnter(async () => {
  await loadPlaylist();
});
</script>

<template>
  <ApplicationViewport :title="playlist ? playlist.name : 'Loading'" authenticated>
    <template #buttons>
      <ion-button @click="goBack()">
        <ion-icon :icon="arrowBack" style="color: #3880ff"></ion-icon>
      </ion-button>
    </template>
    <PlaylistDetails v-if="playlist" :playlist="playlist" @update="loadPlaylist" />
  </ApplicationViewport>
</template>

<style scoped>
ion-card-subtitle {
  font-size: 15px;
}
</style>
