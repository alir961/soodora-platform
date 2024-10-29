<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonRow, IonGrid, IonCol, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent, IonButton, IonIcon, IonModal, IonInput, IonItem, IonSelect, IonSelectOption } from '@ionic/vue';
import { add } from 'ionicons/icons';
import { getUserInfoFromPreferences } from "@/utils/localStorage";
import { fetchPlaylists, createPlaylist } from "@/services/playlistService";
import { useRouter } from 'vue-router';
import { getToken } from '@/utils/localStorage';

const router = useRouter();
const playlists = ref([]);
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

const goToPlaylistDetails = (slug: string) => {
  router.push({ name: 'PlaylistDetails', params: { slug } });
};

onMounted(async () => {
  const token = await getToken();

  if (!token) {
    await router.push('/auth');
  } else {
    await loadPlaylists();
  }
});
</script>

<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>Playlists</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <div id="toolbar">
            <div id="toolbar-title">
              <ion-title size="large">Playlists</ion-title>
            </div>
            <div id="toolbar-button">
              <ion-button style="height: 70%; width: 50%; --border-radius: 15px;" size="small" @click="showModal = true" :inert="showModal">
                <ion-icon :icon="add"></ion-icon>
              </ion-button>
            </div>
          </div>
        </ion-toolbar>
      </ion-header>

      <ion-grid>
        <ion-row>
          <ion-col size="12" size-md="6" size-lg="4" v-for="playlist in playlists" :key="playlist.slug">
            <ion-card @click="goToPlaylistDetails(playlist.slug)">
              <div id="card-text">
                <ion-card-header>
                  <ion-card-title>{{ playlist.name }}</ion-card-title>
                  <ion-card-subtitle>{{ playlist.privacy }}</ion-card-subtitle>
                </ion-card-header>
                <ion-card-content>
                  <p>{{ playlist.description }}</p>
                </ion-card-content>
              </div>
              <div id="card-image">
                <img id="image" :src="playlist.imageUrl" alt="Playlist Image"/>
              </div>
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
                  <ion-select-option value="PROTECTED">Protected</ion-select-option>
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
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  height: 15vh;
}

ion-card-title {
  font-size: 1.5rem;
}

ion-card-subtitle {
  color: #666;
}

#card-text {
  height: 100%;
  width: 60%;
}

#card-image {
  width: 40%;
  height: 80%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-right: 3%;
}

#image {
  height: 100%;
  border-radius: 20px;
}

#toolbar {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 4%;
  margin-bottom: 4%;
}

#toolbar-title {
  padding-left: 3%;
  padding-top: 2%;
  padding-bottom: 2%;
}

#toolbar-button {
  width: 25%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

.modal-content {
  height: 100%;
}
</style>
