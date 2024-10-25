<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonRow, IonGrid, IonCol, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent, IonButton, IonIcon, IonModal, IonInput, IonItem , IonSelect, IonSelectOption } from '@ionic/vue';
import { add } from 'ionicons/icons';
import { getUserInfoFromPreferences } from "@/utils/localStorage";
import { fetchPlaylists, createPlaylist } from "@/services/playlistService";
import { useRouter } from 'vue-router';
import { getToken } from '@/utils/localStorage';

const router = useRouter();

onMounted(async () => {
  const token = await getToken();
  
  if (!token) {
    router.push('/auth');
  }
});

const playlists = ref([]);
const focusedPlaylist = ref(null);
const showModal = ref(false);
const newPlaylist = ref({ name: '', description: '', imageUrl: '', privacy: 'PUBLIC' });

const loadPlaylists = async () => {
  const userInfo = await getUserInfoFromPreferences();
  try {
    playlists.value = await fetchPlaylists(userInfo.slug);
  } catch (error) {
    console.error('Failed to load playlists:', error);
  }
};

const handleCreatePlaylist = async () => {
  try {
    await createPlaylist(newPlaylist.value);
    await loadPlaylists();
    showModal.value = false;
  } catch (error) {
    console.error('Failed to create playlist:', error);
  }
};

const focusPlaylist = (playlist) => {
  focusedPlaylist.value = focusedPlaylist.value === playlist ? null : playlist;
};

onMounted(() => {
  loadPlaylists();
});
</script>

<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>My Playlists</ion-title>
        <ion-button slot="end" @click="showModal = true" :inert="showModal">
          <ion-icon :icon="add"></ion-icon>
        </ion-button>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <ion-title size="large">My Playlists</ion-title>
        </ion-toolbar>
      </ion-header>

      <ion-grid>
        <ion-row>
          <ion-col size="12" size-md="6" size-lg="4" v-for="playlist in playlists" :key="playlist.id">
            <ion-card @click="focusPlaylist(playlist)" :class="{ focused: focusedPlaylist === playlist }">
              <img :src="playlist.imageUrl" alt="Playlist Image" />
              <ion-card-header>
                <ion-card-title>{{ playlist.name }}</ion-card-title>
                <ion-card-subtitle>{{ playlist.privacy }}</ion-card-subtitle>
              </ion-card-header>
              <ion-card-content>
                <p>{{ playlist.description }}</p>
                <ion-button v-if="focusedPlaylist === playlist" expand="block">View Details</ion-button>
              </ion-card-content>
            </ion-card>
          </ion-col>
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
                <ion-input placeholder="Name" v-model="newPlaylist.name"></ion-input>
              </ion-item>
              <ion-item>
                <ion-input placeholder="Description" v-model="newPlaylist.description"></ion-input>
              </ion-item>
              <ion-item>
                <ion-input placeholder="Image Url" v-model="newPlaylist.imageUrl"></ion-input>
              </ion-item>
              <ion-item>
                <ion-select v-model="newPlaylist.privacy" placeholder="Privacy">
                  <ion-select-option value="PUBLIC">Public</ion-select-option>
                  <ion-select-option value="PRIVATE">Private</ion-select-option>
                  <ion-select-option value="PRIVATE">Protected</ion-select-option>
                </ion-select>
              </ion-item>
              <ion-button expand="block" type="submit" :disabled="!newPlaylist.name">Create Playlist</ion-button>
            </form>
          </ion-content>
        </div>
      </ion-modal>

    </ion-content>
  </ion-page>
</template>

<style scoped>
ion-card {
  margin: 2%;
}

ion-card-title {
  font-size: 1.5rem;
}

ion-card-subtitle {
  color: #666;
}

img {
  width: 100%;
  height: auto;
  border-radius: 0.5em;
}

.focused {
  border: 0.200em solid #3880ff;
  transform: scale(1.05);
}

.modal-content {
  height: 100%;
}
</style>
