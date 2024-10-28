<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { IonTitle, IonToolbar, IonHeader, IonContent, IonPage, IonCard, IonCardHeader, IonCardContent, IonCardSubtitle, IonIcon, IonModal, IonButton, IonItem, IonInput, IonSelect, IonSelectOption } from '@ionic/vue';
import { createOutline } from 'ionicons/icons';
import { useRoute } from 'vue-router';
import { fetchPlaylistBySlug } from '@/services/playlistService';
import { getUserInfoFromPreferences } from '@/utils/localStorage';

const route = useRoute();
const playlist = ref({ name: '', description: '', imageUrl: '', privacy: '' });
const userInfo = ref(null);
const showModal = ref(false);

// Load the playlist data by slug
const loadPlaylist = async () => {
  const slug = route.params.slug;
  try {
    playlist.value = await fetchPlaylistBySlug(slug);
  } catch (error) {
    console.error('Failed to load playlist:', error);
  }
};

// Load the user information
const fetchUserInfo = async () => {
  try {
    userInfo.value = await getUserInfoFromPreferences();
  } catch (error) {
    console.error('Failed to fetch user info:', error);
  }
};

// Save the updated playlist
const handleUpdatePlaylist = async () => {
  try {
    await updatePlaylist(route.params.slug, playlist.value);
    showModal.value = false; // Close the modal after updating
  } catch (error) {
    console.error('Failed to update playlist:', error);
  }
};

onMounted(async () => {
  await loadPlaylist();
  await fetchUserInfo();
});
</script>

<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>{{ playlist.name }}</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <div id="toolbar">
            <div id="toolbar-title">
              <ion-title size="large">{{ playlist.name }}</ion-title>
            </div>
          </div>
        </ion-toolbar>
      </ion-header>
      <ion-card>
        <ion-card-content>
          <div class="playlist">
            <div class="playlist-info">
              <img class="playlist-image" :src="playlist.imageUrl" alt="Playlist Image"/>
              <div class="playlist-info-text">
                <ion-card-subtitle>{{ playlist.privacy }} PLAYLIST</ion-card-subtitle>
                <p v-if="userInfo">{{ userInfo.username }}</p>
                <p>{{ playlist.description }}</p>
              </div>
              <div class="edit-button">
                <ion-icon id="button" size="large" :icon="createOutline" @click="showModal = true"></ion-icon>
              </div>
            </div>
            <div class="playlist-songs">
              <!-- ADD logic to display this ONLY if the playlist is empty -->
              <h1 style="font-weight:700; font-size:large; color:antiquewhite">Your playlist looks empty !</h1>
              <ion-button style="font-weight: 700;" color="dark" id="empty-button" size="default" >Add to this playlist</ion-button>
            </div>
          </div>
        </ion-card-content>
      </ion-card>

      <ion-modal :isOpen="showModal">
        <div class="modal-content">
          <ion-header>
            <ion-toolbar>
              <ion-title>Edit Playlist</ion-title>
              <ion-button slot="end" @click="showModal = false">Close</ion-button>
            </ion-toolbar>
          </ion-header>
          <ion-content>
            <form @submit.prevent="handleUpdatePlaylist">
              <ion-item>
                <ion-input placeholder="Name" v-model="playlist.name"></ion-input>
              </ion-item>
              <ion-item>
                <ion-input placeholder="Description" v-model="playlist.description"></ion-input>
              </ion-item>
              <ion-item>
                <ion-input placeholder="Image Url" v-model="playlist.imageUrl"></ion-input>
              </ion-item>
              <ion-item>
                <ion-select v-model="playlist.privacy" placeholder="Privacy">
                  <ion-select-option value="PUBLIC">Public</ion-select-option>
                  <ion-select-option value="PRIVATE">Private</ion-select-option>
                  <ion-select-option value="PROTECTED">Protected</ion-select-option>
                </ion-select>
              </ion-item>
              <ion-button expand="block" type="submit">Update Playlist</ion-button>
            </form>
          </ion-content>
        </div>
      </ion-modal>
    </ion-content>
  </ion-page>
</template>

<style scoped>

ion-card-subtitle{
  font-size: 15px
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

.playlist {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.playlist-info {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
}

.playlist-songs {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding-top: 8%;
  padding-bottom: 8%;
}

.playlist-image {
  width: 25%;
  max-width: 25%;
  max-height: 25%;
  border-radius: 15px;
  margin-bottom: 20px;
}

.playlist-info-text {
  display: flex;
  flex-direction: column;
  font-size: 40px;
  margin-left: 6%;
  width: 50%;
  height: 100%;
}

.edit-button {
  display: flex;
  flex-direction: row;
  justify-content: end;
  height: 100%;
  width: 20%;
}

#empty-button {
  margin-top: 6%;
  width: 60%;
  font-weight: 500;
  --border-radius: 50px;
}

.modal-content {
  height: 100%;
}
</style>