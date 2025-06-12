<script setup lang="ts">
import {
  IonButton,
  IonModal,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  IonItem,
  IonLabel,
  IonInput,
  IonSelect,
  IonSelectOption,
  toastController,
  IonIcon,
} from '@ionic/vue';

import { createOutline, bagAddOutline, linkOutline } from 'ionicons/icons';
import { createPost, CreatePostRequest } from '@/client/postClient';
import { createProduct, CreateProductRequest } from '@/client/productClient';
import { ref, onMounted } from 'vue';
import { getUserLinkPage } from '@/client/linkPageClient';
import { useAuthStore } from '@/store/reactiveStorage';
import { useRouter } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const router = useRouter();
const authStore = useAuthStore();

const hasLinkPage = ref(false);

const checkUserLinkPage = async () => {
  const currentUser = authStore.getAuthenticatedUser;
  if (currentUser) {
    try {
      const userPage = await getUserLinkPage(currentUser.slug);
      hasLinkPage.value = !!userPage;
    } catch (error) {
      hasLinkPage.value = false;
    }
  } else {
    hasLinkPage.value = false;
  }
};

onMounted(() => {
  checkUserLinkPage();
});

const goToLinkPages = async () => {
  const currentUser = authStore.getAuthenticatedUser;
  if (currentUser) {
    try {
      const userPage = await getUserLinkPage(currentUser.slug);
      if (userPage) {
        router.push(`/linkpages/${userPage.slug}`);
        return;
      }
    } catch (error) {
      // error ignored, fallback to /linkpages
    }
  }
  router.push('/linkpages');
};

const showModal = ref(false);
const newPost = ref<CreatePostRequest>({
  title: '',
  content: '',
  imageUrl: '',
  audioUrl: '',
  type: 'ANNOUNCE',
  createdAt: new Date().toISOString(),
});

const publishPost = async () => {
  try {
    await createPost(newPost.value);
    showModal.value = false;
    router.push('/explore');
  } catch (error) {
    const toast = await toastController.create({
      message: 'Error creating post',
      duration: 2000,
      color: 'danger',
    });
    await toast.present();
  }
};

const showProductModal = ref(false);

const newProduct = ref<CreateProductRequest>({
  name: '',
  description: '',
  imageUrl: '',
  category: '',
  price: 0,
  quantity: 0,
});

const publishProduct = async () => {
  try {
    await createProduct(newProduct.value);
    showProductModal.value = false;
    router.push('/marketplace');
  } catch (error) {
    const toast = await toastController.create({
      message: 'Error creating product',
      duration: 2000,
      color: 'danger',
    });
    await toast.present();
  }
};
</script>

<template>
  <ApplicationViewport title="Create" authenticated>
    <div class="create-container ion-padding">
      <h1 class="page-title">Create</h1>
      <p class="page-description">
        Create your world with Soodora.
        <br />
        Linkpages, posts, products and exports.
        <br />
        All from here
      </p>
      <ion-button @click="showModal = true" expand="block" class="go-button" color="primary" fill="solid">
        Create a Post
        <ion-icon :icon="createOutline" slot="end"></ion-icon>
      </ion-button>
      <ion-button @click="showProductModal = true" expand="block" class="go-button" color="primary" fill="solid">
        Create a Product
        <ion-icon :icon="bagAddOutline" slot="end"></ion-icon>
      </ion-button>
      <ion-button @click="goToLinkPages" expand="block" class="go-button" color="primary" fill="solid">
        {{ hasLinkPage ? 'Edit your LinkPage' : 'Create a LinkPage' }}
        <ion-icon :icon="linkOutline" slot="end"></ion-icon>
      </ion-button>
      <ion-modal :is-open="showModal" @did-dismiss="showModal = false">
        <ion-content>
          <div class="modal-content">
            <ion-header>
              <ion-toolbar>
                <ion-title>Create a new post</ion-title>
                <ion-button slot="end" @click="showModal = false">Close</ion-button>
              </ion-toolbar>
            </ion-header>
            <ion-item>
              <ion-input label="Title" label-placement="floating" placeholder="Enter title" v-model="newPost.title">
              </ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Content"
                label-placement="floating"
                placeholder="Enter content"
                v-model="newPost.content">
              </ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Image URL"
                label-placement="floating"
                placeholder="Enter link to image"
                v-model="newPost.imageUrl"
                clearInput>
              </ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Audio URL"
                label-placement="floating"
                placeholder="Enter link to audio"
                v-model="newPost.audioUrl"
                clearInput>
              </ion-input>
            </ion-item>
            <ion-item>
              <ion-label>Type</ion-label>
              <ion-select v-model="newPost.type" placeholder="Select type">
                <ion-select-option value="ANNOUNCE">Announce</ion-select-option>
                <ion-select-option value="MERCH_DROP">Merch Drop</ion-select-option>
                <ion-select-option value="NEW_EVENT">New Event</ion-select-option>
              </ion-select>
            </ion-item>
            <ion-button expand="block" @click="publishPost" class="post-button"> Create Post </ion-button>
          </div>
        </ion-content>
      </ion-modal>
      <ion-modal :is-open="showProductModal" @did-dismiss="showProductModal = false">
        <ion-content>
          <div class="modal-content">
            <ion-header>
              <ion-toolbar>
                <ion-title>Create a new product</ion-title>
                <ion-button slot="end" @click="showProductModal = false">Close</ion-button>
              </ion-toolbar>
            </ion-header>
            <ion-item>
              <ion-input
                label="Name"
                label-placement="floating"
                placeholder="Enter product name"
                v-model="newProduct.name"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Description"
                label-placement="floating"
                placeholder="Enter product description"
                v-model="newProduct.description"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Image URL"
                label-placement="floating"
                placeholder="Enter image URL"
                v-model="newProduct.imageUrl"
                clearInput></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Category"
                label-placement="floating"
                placeholder="Enter category"
                v-model="newProduct.category"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Price"
                label-placement="floating"
                type="number"
                placeholder="Enter price in €"
                v-model.number="newProduct.price"></ion-input>
            </ion-item>
            <ion-item>
              <ion-input
                label="Quantity"
                label-placement="floating"
                type="number"
                placeholder="Enter quantity"
                v-model.number="newProduct.quantity"></ion-input>
            </ion-item>
            <ion-button expand="block" class="post-button" @click="publishProduct"> Create Product </ion-button>
          </div>
        </ion-content>
      </ion-modal>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.create-container {
  display: flex;
  flex-direction: column;
  margin: 20px;
  padding: 10px 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 0px;
}

.page-description {
  font-size: 19px;
  color: var(--ion-color-medium);
  margin-bottom: 20px;
}

.go-button {
  margin-top: 12px;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 136, 255, 0.3);
  transition:
    background-color 0.3s ease,
    box-shadow 0.3s ease;
  --color: white;
}

.go-button:hover {
  background-color: var(--ion-color-primary-shade);
  box-shadow: 0 6px 12px rgba(0, 136, 255, 0.5);
}

.post-button {
  margin: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 8px rgba(0, 136, 255, 0.3);
  transition:
    background-color 0.3s ease,
    box-shadow 0.3s ease;
  --color: white;
}

.post-button:hover {
  background-color: var(--ion-color-primary-shade);
  box-shadow: 0 6px 12px rgba(0, 136, 255, 0.5);
}
</style>

<style scoped>
ion-button.go-button::part(native) {
  color: white !important;
}

ion-button.post-button::part(native) {
  color: white !important;
}
</style>
