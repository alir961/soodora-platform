<script setup lang="ts">
import { Ref, ref, reactive, onMounted } from 'vue';
import {
  IonButton,
  IonContent,
  IonHeader,
  IonIcon,
  IonInput,
  IonItem,
  IonModal,
  IonTitle,
  IonToolbar,
  IonGrid,
  IonRow,
} from '@ionic/vue';
import { add } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';
import { User } from '@/entity/User';
import { uploadMp3 } from '@/client/exploreMP3Client';
import AudioCard from '@/component/explore/AudioCard.vue';

const userData: Ref<User | null> = ref(null);
const showModal: Ref<boolean> = ref(false);
const uploadedAudios: Ref<{ title: string; genre: string; imageUrl: string; fileUrl: string; duration: number }[]> =
  ref([]);

const newMp3 = reactive({
  title: '',
  genre: '',
  imageUrl: '',
  file: null as File | null,
});

const fetchUserData = async () => {
  try {
    userData.value = await retrieveAuthenticatedProfile();
  } catch (error) {
    console.error('Failed to fetch user data:', error);
  }
};

onMounted(fetchUserData);

const showButton = () => {
  return userData.value?.roles.includes('ADMIN') || userData.value?.roles.includes('ARTIST');
};

const handleFileSelection = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    const file = target.files[0];
    if (file.type.startsWith('audio/')) {
      newMp3.file = file;
    } else {
      alert('Please upload a valid audio file!');
      newMp3.file = null;
    }
  }
};

const uploadMp3File = async () => {
  if (!newMp3.title || !newMp3.genre || !newMp3.imageUrl || !newMp3.file) {
    alert('Please fill out all fields!');
    return;
  }

  try {
    await sendMp3ToServer(newMp3);

    const audioElement = new Audio();
    audioElement.src = URL.createObjectURL(newMp3.file);

    audioElement.onloadedmetadata = () => {
      const duration = Math.round(audioElement.duration);

      uploadedAudios.value.push({
        title: newMp3.title,
        genre: newMp3.genre,
        imageUrl: newMp3.imageUrl,
        fileUrl: audioElement.src,
        duration: duration,
      });

      showModal.value = false;
    };
  } catch (error) {
    console.error('Error uploading audio file:', error);
    alert('Error uploading audio file.');
  }
};

const sendMp3ToServer = async (newMp3Data: { title: string; genre: string; file: File | null }) => {
  if (!newMp3Data.file) {
    alert('No file selected!');
    return;
  }

  try {
    await uploadMp3();
    //nommer les variables pour le exploreMP3Client.ts ici (je crois que ça marche comme as)
  } catch (error) {
    console.error('Error uploading MP3:', error);
    throw error;
  }
};
</script>

<template>
  <ApplicationViewport title="Explore" authenticated>
    <template #toolbar>
      <div id="toolbar-button">
        <ion-button v-if="showButton()" size="small" @click="showModal = true" :inert="showModal">
          <ion-icon :icon="add"></ion-icon>
        </ion-button>
      </div>
    </template>

    <ion-grid>
      <ion-row>
        <AudioCard v-for="audio in uploadedAudios" :key="audio.title" :audio="audio" />
      </ion-row>
    </ion-grid>

    <ion-modal :isOpen="showModal">
      <div class="modal-content">
        <ion-header>
          <ion-toolbar>
            <ion-title>Upload Audio File</ion-title>
            <ion-button slot="end" @click="showModal = false">Close</ion-button>
          </ion-toolbar>
        </ion-header>
        <ion-content>
          <form @submit.prevent="uploadMp3File">
            <ion-item>
              <ion-input
                label="Title"
                label-placement="floating"
                placeholder="Enter title"
                v-model="newMp3.title"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Genre"
                label-placement="floating"
                placeholder="Enter genre"
                v-model="newMp3.genre"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Image URL"
                label-placement="floating"
                placeholder="Enter link to image"
                v-model="newMp3.imageUrl"
                clearInput></ion-input>
            </ion-item>
            <ion-item>
              <input type="file" accept="audio/*" @change="handleFileSelection" />
            </ion-item>

            <ion-button expand="block" type="submit"> Upload Audio </ion-button>
          </form>
        </ion-content>
      </div>
    </ion-modal>
  </ApplicationViewport>
</template>

<style scoped>
.modal-content {
  height: 100%;

  & ion-button {
    margin: 20px;
  }
}

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
</style>
