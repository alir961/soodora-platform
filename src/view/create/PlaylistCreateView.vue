<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { createPlaylist } from '@/client/playlistClient';
import { showToast } from '@/utils';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import FileUpload from '@/component/file/FileUpload.vue';
import {
  IonItem,
  IonLabel,
  IonInput,
  IonSelect,
  IonSelectOption,
  IonButton,
  IonIcon,
  IonTitle,
  IonHeader,
  IonContent,
  IonToolbar,
} from '@ionic/vue';
import { arrowBackOutline } from 'ionicons/icons';

const router = useRouter();

const newPlaylist = ref<{ name: string; description: string; privacy: string }>({
  name: '',
  description: '',
  privacy: 'PUBLIC',
});

const playlistCoverFile = ref<File | null>(null);

const onPlaylistCoverSelected = (file: File | null) => {
  playlistCoverFile.value = file;
};

const callCreatePlaylist = async () => {
  try {
    if (!newPlaylist.value.name.trim()) {
      await showToast('Name is required', 'danger');
      return;
    }

    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(newPlaylist.value)], { type: 'application/json' }));

    if (playlistCoverFile.value) {
      formData.append('file', playlistCoverFile.value, playlistCoverFile.value.name);
    }

    const createdPlaylist = await createPlaylist(formData);
    await showToast('Playlist created!');
    await router.push({ name: 'PlaylistDetailsView', params: { slug: createdPlaylist.slug } });
  } catch (error: unknown) {
    const message = 'Failed to create playlist. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};
</script>

<template>
  <ApplicationViewport title="Create Playlist">
    <IonHeader>
      <IonButton class="inline-back-button" slot="start" fill="clear" @click="$router.back()">
        <IonIcon :icon="arrowBackOutline" />
        Back
      </IonButton>
      <IonToolbar>
        <IonTitle>Create Playlist</IonTitle>
      </IonToolbar>
    </IonHeader>

    <IonContent class="ion-padding">
      <IonItem>
        <IonLabel position="stacked">Name</IonLabel>
        <IonInput v-model="newPlaylist.name" />
      </IonItem>

      <IonItem>
        <IonLabel position="stacked">Description</IonLabel>
        <IonInput v-model="newPlaylist.description" />
      </IonItem>

      <IonItem>
        <IonLabel position="stacked">Privacy</IonLabel>
        <IonSelect v-model="newPlaylist.privacy">
          <IonSelectOption value="PUBLIC">Public</IonSelectOption>
          <IonSelectOption value="PRIVATE">Private</IonSelectOption>
          <IonSelectOption value="UNLISTED">Unlisted</IonSelectOption>
        </IonSelect>
      </IonItem>

      <IonItem>
        <FileUpload
          fieldLabel="Select Image"
          label="Choose File"
          accept="image/*"
          @file-selected="onPlaylistCoverSelected" />
      </IonItem>

      <IonButton expand="block" @click="callCreatePlaylist">Create</IonButton>
    </IonContent>
  </ApplicationViewport>
</template>
