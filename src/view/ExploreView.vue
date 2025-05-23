<script setup lang="ts">
import { Ref, ref, reactive, onMounted, Reactive } from 'vue';
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
  IonSelect,
  IonSelectOption,
  IonLabel,
  onIonViewWillEnter,
  toastController,
} from '@ionic/vue';
import { add } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { User } from '@/entity/User';
import { useAuthStore } from '@/store/reactiveStorage';
import PostCard from '@/component/explore/PostCard.vue';
import { Post } from '@/entity/Post';
import { createPost, CreatePostRequest, retrievePosts } from '@/client/postClient';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';

const userData: Ref<User | null> = ref(null);
const showModal: Ref<boolean> = ref(false);
const posts: Ref<Post[]> = ref([]);
const newPost: Reactive<CreatePostRequest> = reactive({
  title: '',
  content: '',
  imageUrl: '',
  audioUrl: '',
  type: '' as Post['type'],
});
const authStore = useAuthStore();

const fetchUserData = async () => {
  if (authStore.isAuthenticated) {
    try {
      userData.value = await retrieveAuthenticatedProfile();
    } catch (error) {
      console.error('Failed to fetch user data:', error);
    }
  }
};

const loadPosts = async () => {
  try {
    posts.value = await retrievePosts();
  } catch (error) {
    const toast = await toastController.create({
      message: `Failed to load feed!`,
      duration: 1500,
      position: 'top',
    });

    await toast.present();
  }
};

const publishPost = async () => {
  try {
    await createPost(newPost);
    await loadPosts();
    showModal.value = false;
  } catch (error) {
    const toast = await toastController.create({
      message: `Failed to publish your post!`,
      duration: 1500,
      position: 'top',
    });

    await toast.present();
  }
};

onIonViewWillEnter(async () => {
  await loadPosts();
});

onMounted(fetchUserData);

const showButton = () => {
  return userData.value?.roles.includes('ADMIN') || userData.value?.roles.includes('ARTIST');
};
</script>

<template>
  <ApplicationViewport title="Explore">
    <template #toolbar>
      <div id="toolbar-button">
        <ion-button v-if="showButton" size="small" @click="showModal = true" :inert="showModal">
          <ion-icon :icon="add"></ion-icon>
        </ion-button>
      </div>
    </template>

    <ion-grid>
      <ion-row>
        <PostCard v-for="post in posts" :key="post.slug" :post="post" @post-deleted="loadPosts" />
      </ion-row>
    </ion-grid>

    <ion-modal :isOpen="showModal">
      <div class="modal-content">
        <ion-header>
          <ion-toolbar>
            <ion-title>Create a new post</ion-title>
            <ion-button slot="end" @click="showModal = false">Close</ion-button>
          </ion-toolbar>
        </ion-header>
        <ion-content>
          <ion-item>
            <ion-input
              label="Title"
              label-placement="floating"
              placeholder="Enter title"
              v-model="newPost.title"></ion-input>
          </ion-item>
          <ion-item>
            <ion-input
              label="Content"
              label-placement="floating"
              placeholder="Enter content"
              v-model="newPost.content"></ion-input>
          </ion-item>
          <ion-item>
            <ion-input
              label="Image URL"
              label-placement="floating"
              placeholder="Enter link to image"
              v-model="newPost.imageUrl"
              clearInput></ion-input>
          </ion-item>
          <ion-item>
            <ion-input
              label="Audio URL"
              label-placement="floating"
              placeholder="Enter link to audio"
              v-model="newPost.audioUrl"
              clearInput></ion-input>
          </ion-item>
          <ion-item>
            <ion-label>Type</ion-label>
            <ion-select v-model="newPost.type" placeholder="Select type">
              <ion-select-option value="ANNOUNCE">Announce</ion-select-option>
              <ion-select-option value="MERCH_DROP">Merch Drop</ion-select-option>
              <ion-select-option value="NEW_EVENT">New Event</ion-select-option>
            </ion-select>
          </ion-item>

          <ion-button expand="block" @click="publishPost"> Post </ion-button>
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
