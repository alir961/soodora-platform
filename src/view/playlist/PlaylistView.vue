<script setup lang="ts">
import { Ref, ref } from 'vue';
import {
  IonButton,
  IonContent,
  IonGrid,
  IonHeader,
  IonIcon,
  IonInput,
  IonItem,
  IonModal,
  IonRow,
  IonSelect,
  IonSelectOption,
  IonTitle,
  IonToolbar,
  toastController,
  onIonViewWillEnter,
} from '@ionic/vue';
import { add, logoYoutube } from 'ionicons/icons';
import {
  createPlaylist,
  PlaylistCreateRequest,
  retrieveUserPlaylists,
  retrieveYoutubePlaylists,
} from '@/client/playlistClient';
import { Playlist } from '@/entity/Playlist';
import { getAuthenticatedUser, reject } from '@/utils';
import PlaylistCard from '@/component/playlist/PlaylistCard.vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { useAuthStore } from '@/store/reactiveStorage';

const playlists: Ref<Playlist[]> = ref([]);
const showModal: Ref<boolean> = ref(false);
const newPlaylist: Ref<PlaylistCreateRequest> = ref({
  name: '',
  description: '',
  imageUrl: '',
  numberOfTracks: 0,
  privacy: 'PUBLIC',
});
const authStore = useAuthStore();

const loadPlaylists = async () => {
  try {
    const userData = await getAuthenticatedUser();

    playlists.value = await retrieveUserPlaylists(userData.slug);
  } catch (error) {
    await reject(error as Error);
  }
};

const handleCreatePlaylist = async () => {
  try {
    await createPlaylist(newPlaylist.value);
    await loadPlaylists();
    showModal.value = false;
  } catch (error) {
    const toast = await toastController.create({
      message: 'Failed to create playlists, please check your account connection',
      duration: 1500,
      position: 'top',
    });

    await toast.present();
  }
};

const FetchYoutubePlaylist = async () => {
  try {
    const playlists: PlaylistCreateRequest[] = await retrieveYoutubePlaylists();

    await Promise.all(playlists.map((playlist) => createPlaylist(playlist)));
    await loadPlaylists();
  } catch (error) {
    const toast = await toastController.create({
      message: 'Failed to create playlists from Youtube playlists',
      duration: 1500,
      position: 'top',
    });

    await toast.present();
  }
};

onIonViewWillEnter(async () => {
  if (authStore.isAuthenticated) await loadPlaylists();
});
</script>

<template>
  <ApplicationViewport title="Playlist">
    <template #toolbar>
      <div id="toolbar-button">
        <ion-button :inert="showModal" size="small" @click="showModal = true">
          <ion-icon :icon="add" />
        </ion-button>
        <ion-button size="small" @click="FetchYoutubePlaylist">
          <ion-icon :icon="logoYoutube" />
        </ion-button>
      </div>
    </template>

    <ion-grid>
      <ion-row>
        <PlaylistCard v-for="playlist in playlists" :key="playlist.slug" :playlist="playlist" />
      </ion-row>
    </ion-grid>

    <ion-modal :isOpen="showModal">
      <div class="modal-content">
        <ion-header>
          <ion-toolbar>
            <ion-title>Create Playlist</ion-title>
            <ion-button slot="end" @click="showModal = false">Close</ion-button>
          </ion-toolbar>
        </ion-header>
        <ion-content>
          <form @submit.prevent="handleCreatePlaylist">
            <ion-item>
              <ion-input
                label="Name"
                label-placement="floating"
                placeholder="Enter name"
                v-model="newPlaylist.name"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Description"
                label-placement="floating"
                placeholder="Enter description"
                v-model="newPlaylist.description"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Image URL"
                label-placement="floating"
                placeholder="Enter link to image"
                v-model="newPlaylist.imageUrl"
                clearInput></ion-input>
            </ion-item>
            <ion-item>
              <ion-select v-model="newPlaylist.privacy" placeholder="Privacy">
                <ion-select-option value="PUBLIC">Public</ion-select-option>
                <ion-select-option value="PRIVATE">Private</ion-select-option>
                <ion-select-option value="UNLISTED">Unlisted</ion-select-option>
              </ion-select>
            </ion-item>
            <ion-button expand="block" type="submit" :disabled="!newPlaylist.name">Create Playlist</ion-button>
          </form>
        </ion-content>
      </div>
    </ion-modal>
  </ApplicationViewport>
</template>

<style scoped>
#toolbar-button {
  width: 25%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;

  & ion-button {
    --border-radius: 15px;

    height: 70%;
    width: 50%;
  }
}

.modal-content {
  height: 100%;

  & ion-button {
    margin: 20px;
  }
}
</style>
