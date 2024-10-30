<script setup lang="ts">

import {createOutline} from "ionicons/icons";
import {
  IonButton,
  IonCard,
  IonCardContent,
  IonCardSubtitle,
  IonContent,
  IonHeader,
  IonIcon,
  IonInput,
  IonItem,
  IonModal,
  IonSelect,
  IonSelectOption,
  IonTitle,
  IonToolbar
} from "@ionic/vue";
import {Playlist} from "@/entity/Playlist";
import {PropType, ref} from "vue";
import {updatePlaylist} from "@/client/playlistClient";

const props = defineProps({
  playlist: {
    required: true,
    type: Object as PropType<Playlist>,
  }
});

const editablePlaylist = ref(props.playlist as Playlist);
const showModal = ref(false);

const handleUpdatePlaylist = async () => {
  try {
    await updatePlaylist(props.playlist?.slug, editablePlaylist.value);
    showModal.value = false;
  } catch (error) {
    console.error('Failed to update playlist:', error);
  }
};
</script>

<template>
  <ion-card>
    <ion-card-content>
      <div class="playlist">
        <div class="playlist-info">
          <img class="playlist-image" :src="playlist.imageUrl" :alt="`Playlist ${playlist.name} thumbnail`"/>
          <div class="playlist-info-text">
            <ion-card-subtitle>{{ playlist.privacy }} PLAYLIST</ion-card-subtitle>
            <p>{{ playlist.owner.username }}</p>
            <p>{{ playlist.description }}</p>
          </div>
          <div class="edit-button">
            <ion-icon id="button" size="large" :icon="createOutline" @click="showModal = true"></ion-icon>
          </div>
        </div>
        <div class="playlist-songs">
          <!-- ADD logic to display this ONLY if the playlist is empty -->
          <h1 style="font-weight:700; font-size:large; color:antiquewhite">Your playlist looks empty !</h1>
          <ion-button style="font-weight: 700;" color="dark" id="empty-button" size="default">Add to this playlist
          </ion-button>
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
            <ion-input placeholder="Name" v-model="editablePlaylist.name"></ion-input>
          </ion-item>
          <ion-item>
            <ion-input placeholder="Description" v-model="editablePlaylist.description"></ion-input>
          </ion-item>
          <ion-item>
            <ion-input placeholder="Image Url" v-model="editablePlaylist.imageUrl"></ion-input>
          </ion-item>
          <ion-item>
            <ion-select v-model="editablePlaylist.privacy" placeholder="Privacy">
              <ion-select-option value="PUBLIC">Public</ion-select-option>
              <ion-select-option value="PRIVATE">Private</ion-select-option>
              <ion-select-option value="UNLISTED">Unlisted</ion-select-option>
            </ion-select>
          </ion-item>
          <ion-button expand="block" type="submit">Update Playlist</ion-button>
        </form>
      </ion-content>
    </div>
  </ion-modal>
</template>

<style scoped>

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