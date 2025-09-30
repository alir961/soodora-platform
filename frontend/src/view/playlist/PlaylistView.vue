<script setup lang="ts">
import { ref, watchEffect } from 'vue';
import { searchPlaylists } from '@/client/playlistClient';
import { Playlist } from '@/entity/Playlist';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import { IonButton, IonContent, IonHeader, IonIcon, IonList, IonLoading, IonTitle, IonToolbar } from '@ionic/vue';
import { arrowBackOutline } from 'ionicons/icons';
import PlaylistItem from '@/component/playlist/PlaylistItem.vue';
import { showToast } from '@/utils';

const playlists = ref<Playlist[]>([]);
const loading = ref(false);

const authStore = useAuthStore();

const loadPlaylistsForUser = async (userSlug: string) => {
  loading.value = true;
  try {
    const playlistPage = await searchPlaylists(userSlug);
    playlists.value = playlistPage.content;
  } catch (err) {
    const message = (err as Error)?.message ?? 'Unable to load playlists';
    playlists.value = [];
    await showToast(message, 'danger');
  } finally {
    loading.value = false;
  }
};

watchEffect(() => {
  const slug = authStore.getAuthenticatedUserSlug;
  if (slug) loadPlaylistsForUser(slug);
});
</script>

<template>
  <ApplicationViewport title="Playlists">
    <IonHeader>
      <IonToolbar>
        <IonButton class="inline-back-button" slot="start" fill="clear" @click="$router.back()">
          <IonIcon :icon="arrowBackOutline" />
          Back
        </IonButton>
        <IonTitle>My Playlists</IonTitle>
      </IonToolbar>
    </IonHeader>

    <IonContent class="ion-padding">
      <IonLoading :is-open="loading" message="Loading playlists..." />

      <IonList v-if="!loading && playlists.length">
        <PlaylistItem v-for="p in playlists" :key="p.slug" :playlist="p" />
      </IonList>

      <div v-if="!loading && !playlists.length" class="ion-text-center ion-padding">
        <p>No playlists found.</p>
      </div>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
.inline-back-button {
  display: flex;
  align-items: center;
}
</style>
