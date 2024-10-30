<script setup lang="ts">
import {ref} from 'vue';
import {IonButton, IonIcon, onIonViewWillEnter} from '@ionic/vue';
import {arrowBack} from 'ionicons/icons';
import {useRoute, useRouter} from 'vue-router';
import {retrievePlaylistBySlug} from '@/client/playlistClient';
import {loadUserData} from '@/store/localStorage';
import {Playlist} from '@/entity/Playlist';
import {User} from '@/entity/User';
import PlaylistDetails from '@/component/playlist/PlaylistDetails.vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const route = useRoute();
const router = useRouter();
const userInfo = ref(null as User | null);

const slug: string = route.params.slug as string;
const playlist = ref(null as Playlist | null);

const loadPlaylist = async () => {
  try {
    playlist.value = await retrievePlaylistBySlug(slug);
  } catch (error) {
    console.error('Failed to load playlist:', error);
  }
};

const fetchUserInfo = async () => {
  try {
    userInfo.value = await loadUserData();
  } catch (error) {
    console.error('Failed to fetch user info:', error);
  }
};

const goBack = () => {
  document.querySelectorAll('[aria-hidden="true"]').forEach(element => {
    element.removeAttribute('aria-hidden');
  });
  router.back();
};

onIonViewWillEnter(async () => {
  await loadPlaylist();
  await fetchUserInfo();
});
</script>

<template>
  <ApplicationViewport :title="playlist ? playlist.name : 'Loading'" authenticated>
    <template #buttons>
      <ion-button @click="goBack()">
        <ion-icon :icon="arrowBack" style="color: #3880ff"></ion-icon>
      </ion-button>
    </template>
    <PlaylistDetails v-if="playlist" :playlist="playlist"/>
  </ApplicationViewport>
</template>

<style scoped>
ion-card-subtitle {
  font-size: 15px
}
</style>