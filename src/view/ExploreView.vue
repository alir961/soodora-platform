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
  onIonViewWillEnter,
  toastController,
} from '@ionic/vue';
import { add } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';
import { User } from '@/entity/User';
import { useAuthStore } from '@/store/reactiveStorage';
import PostCard from '@/component/explore/PostCard.vue';
import { Post } from '@/entity/Post';
import { createPost, CreatePostRequest, retrievePosts } from '@/client/postClient';

const userData: Ref<User | null> = ref(null);
const showModal: Ref<boolean> = ref(false);
const posts: Ref<Post[]> = ref([]);
const newPost: Reactive<CreatePostRequest> = reactive({
  title: '',
  content: '',
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
        <PostCard v-for="post in posts" :key="post.slug" :post="post" />
      </ion-row>
    </ion-grid>

    <ion-modal :isOpen="showModal">
      <div class="modal-content">
        <ion-header>
          <ion-toolbar>
            <ion-title>Post</ion-title>
            <ion-button slot="end" @click="showModal = false">Close</ion-button>
          </ion-toolbar>
        </ion-header>
        <ion-content>
          <form @submit.prevent="publishPost">
            <ion-item>
              <ion-input
                label="Title"
                label-placement="floating"
                placeholder="Enter the post title"
                v-model="newPost.title"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Content"
                label-placement="floating"
                placeholder="Enter the post content"
                v-model="newPost.content"></ion-input>
            </ion-item>

            <ion-button expand="block" type="submit"> Post </ion-button>
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
