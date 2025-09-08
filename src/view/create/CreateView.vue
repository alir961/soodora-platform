<script setup lang="ts">
import {
  IonButton,
  IonModal,
  IonContent,
  IonItem,
  IonInput,
  IonIcon,
  IonSelect,
  IonSelectOption,
  IonLabel,
} from '@ionic/vue';

import {
  createOutline,
  bagAddOutline,
  linkOutline,
  arrowBackOutline,
  trashOutline,
  cloudDownloadOutline,
} from 'ionicons/icons';
import { callCreatePost, CreatePostRequest } from '@/client/postClient';
import { createProduct } from '@/client/productClient';
import { ref, watch } from 'vue';
import { getUserLinkPage } from '@/client/linkPageClient';
import { useAuthStore } from '@/store/reactiveStorage';
import { useRouter } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { getMediaKitByArtistSlug } from '@/client/mediaKitClient';
import { showToast } from '@/utils';

const router = useRouter();
const authStore = useAuthStore();

const hasLinkPage = ref(false);

const checkUserLinkPage = async () => {
  const userSlug = authStore.getAuthenticatedUserSlug;

  if (userSlug) {
    try {
      const userPage = await getUserLinkPage(userSlug);

      hasLinkPage.value = !!userPage;
    } catch (error) {
      console.log('Unable to fetch artist link page', error);
      hasLinkPage.value = false;
    }
  } else hasLinkPage.value = false;
};

watch(
  () => authStore.getAuthenticatedUser,
  (newUser) => {
    if (newUser) {
      checkUserLinkPage();
    }
  },
  { immediate: true }
);

const goToLinkPages = async () => {
  const userSlug = authStore.getAuthenticatedUserSlug;

  if (!userSlug) return;
  try {
    const userPage = await getUserLinkPage(userSlug);

    if (userPage) {
      await router.push(`/linkpages/${userPage.slug}`);
      return;
    }
  } catch (error) {
    console.log('Unable to fetch artist link page', error);
  }
  await router.push('/linkpages');
};

const showModal = ref(false);
const newPost = ref<CreatePostRequest>({
  title: '',
  content: '',
  imageUrl: '',
  audioUrl: '',
  createdAt: new Date(),
  likeCount: 0,
  repostCount: 0,
  commentCount: 0,
});
const editedPostFile = ref<File | null>(null);

const isValidAudioUrl = (url: string) => /\.mp3$/i.test(url);

const publishPost = async () => {
  if (newPost.value.audioUrl && !isValidAudioUrl(newPost.value.audioUrl)) {
    await showToast('Only mp3 file URL are accepted', 'danger');
    return;
  }

  try {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(newPost.value)], { type: 'application/json' }));

    if (editedPostFile.value) formData.append('file', editedPostFile.value);

    await callCreatePost(formData);
    showModal.value = false;
    await showToast('Succeed to publish post.');
    await router.push('/explore');
  } catch (error: unknown) {
    const message =
      'Failed to create post, check all the form fields. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

const showProductModal = ref(false);
const newProduct = ref({ name: '', description: '', category: '', price: 0, quantity: 0 });
const editedProductFile = ref<File | null>(null);

const publishProduct = async () => {
  try {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(newProduct.value)], { type: 'application/json' }));

    if (editedProductFile.value) formData.append('file', editedProductFile.value);

    await createProduct(formData);
    showProductModal.value = false;
    await router.push('/marketplace');
    await showToast('Succeed to publish product.');
  } catch (error: unknown) {
    const message =
      'Failed to create product, check all the form fields. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

const goToSalesPage = async () => {
  await router.push('/marketplace/mysales');
};

const goToMediaKit = async () => {
  const userSlug = authStore.getAuthenticatedUserSlug;
  if (!userSlug) return;

  try {
    const userPage = await getMediaKitByArtistSlug(userSlug);
    if (userPage) return router.push(`/mediakits/${userPage.slug}`);
  } catch (error: unknown) {
    const message =
      'Unable to fetch Media Kit, redirecting to creation form. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'warning');
  }
  await router.push('/mediakits/create');
};

const onPostFileSelected = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) editedPostFile.value = target.files[0];
};

const onProductFileSelected = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) editedProductFile.value = target.files[0];
};
</script>

<template>
  <ApplicationViewport title="Create" authenticated>
    <div class="create-container ion-padding">
      <h1 class="page-title">Create</h1>
      <p class="page-description">
        Create your world with Soodora.<br />
        Linkpages, posts, products and exports.<br />
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

      <ion-button expand="block" class="go-button" color="secondary" fill="solid" @click="goToSalesPage">
        My Sales
      </ion-button>

      <ion-button @click="goToMediaKit" expand="block" class="go-button" color="primary" fill="solid">
        Create MediaKit
        <ion-icon :icon="createOutline" slot="end"></ion-icon>
      </ion-button>

      <ion-modal :is-open="showModal" @did-dismiss="showModal = false">
        <ion-content>
          <div>
            <div class="floating-header">
              <ion-button class="inline-back-button" fill="clear" size="large" @click="showModal = false">
                <ion-icon :icon="arrowBackOutline" class="back-icon" />
                <span>Back</span>
              </ion-button>
              <h2 class="floating-header-title">New Post</h2>
            </div>

            <div id="section-content">
              <ion-item class="modal-item">
                <ion-label position="stacked">Title</ion-label>
                <ion-input placeholder="Add a title" v-model="newPost.title"></ion-input>
              </ion-item>

              <ion-item class="modal-item">
                <ion-label position="stacked">Content</ion-label>
                <ion-input placeholder="Add some content" v-model="newPost.content"></ion-input>
              </ion-item>

              <ion-item class="modal-item">
                <IonLabel position="stacked">Select Image</IonLabel>
                <div class="file-upload-container">
                  <IonLabel class="file-upload-button">
                    <ion-icon :icon="cloudDownloadOutline" />
                    Choose File
                    <input type="file" @change="onPostFileSelected" accept="image/*" />
                  </IonLabel>
                  <span v-if="editedPostFile" class="file-name">{{ editedPostFile.name }}</span>
                </div>
                <div class="url-wrapper">
                  <div class="url-label-row">
                    <ion-label position="stacked">Audio URL</ion-label>
                    <ion-icon
                      v-if="newPost.audioUrl"
                      :icon="trashOutline"
                      class="url-delete-icon"
                      @click="newPost.audioUrl = ''" />
                  </div>
                  <ion-input placeholder="Add an audio URL" v-model="newPost.audioUrl" />
                </div>
              </ion-item>
            </div>

            <div class="post-actions">
              <ion-button expand="block" @click="publishPost" class="create-post">Create Post</ion-button>
            </div>
          </div>
        </ion-content>
      </ion-modal>

      <ion-modal :is-open="showProductModal" @did-dismiss="showProductModal = false">
        <ion-content>
          <div>
            <div class="floating-header">
              <ion-button
                class="inline-back-button"
                fill="clear"
                size="large"
                @click="showProductModal = false"
                aria-label="Back">
                <ion-icon :icon="arrowBackOutline" class="back-icon" />
                <span>Back</span>
              </ion-button>
              <h2 class="floating-header-title">New Product</h2>
            </div>

            <div id="section-content">
              <ion-item class="modal-item">
                <ion-label position="stacked">Name</ion-label>
                <ion-input placeholder="Add a product name" v-model="newProduct.name"></ion-input>
              </ion-item>

              <ion-item class="modal-item">
                <ion-label position="stacked">Description</ion-label>
                <ion-input placeholder="Add a product description" v-model="newProduct.description"></ion-input>
              </ion-item>

              <ion-item class="modal-item">
                <IonLabel position="stacked">Select Image</IonLabel>
                <div class="file-upload-container">
                  <IonLabel class="file-upload-button">
                    <ion-icon :icon="cloudDownloadOutline" />
                    Choose File
                    <input type="file" @change="onProductFileSelected" accept="image/*" />
                  </IonLabel>
                  <span v-if="editedProductFile" class="file-name">{{ editedProductFile.name }}</span>
                </div>
              </ion-item>

              <ion-item class="modal-item">
                <ion-label position="stacked">Category</ion-label>
                <ion-select interface="action-sheet" placeholder="Select a category" v-model="newProduct.category">
                  <ion-select-option value="Vinyl">Vinyl</ion-select-option>
                  <ion-select-option value="CD">CD</ion-select-option>
                  <ion-select-option value="Clothing">Clothing</ion-select-option>
                  <ion-select-option value="Concert Tickets">Concert Tickets</ion-select-option>
                  <ion-select-option value="Digital Downloads">Digital Downloads</ion-select-option>
                  <ion-select-option value="Accessories">Accessories</ion-select-option>
                  <ion-select-option value="Posters">Posters</ion-select-option>
                </ion-select>
              </ion-item>

              <ion-item class="modal-item">
                <ion-label position="stacked">Price</ion-label>
                <ion-input type="number" placeholder="Add a price" v-model.number="newProduct.price">
                  <div slot="end">€</div>
                </ion-input>
              </ion-item>

              <ion-item class="modal-item">
                <ion-label position="stacked">Quantity</ion-label>
                <ion-input type="number" placeholder="Add stock" v-model.number="newProduct.quantity"></ion-input>
              </ion-item>
            </div>

            <div class="post-actions">
              <ion-button expand="block" class="post-button" @click="publishProduct">Create Product</ion-button>
            </div>
          </div>
        </ion-content>
      </ion-modal>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.floating-header {
  position: relative;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding-top: 3em;
  padding-bottom: 1em;
  margin-bottom: 1.5em;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.floating-header-title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 1.5em;
  color: var(--ion-color-light);
  z-index: 1;
  margin: 0;
}

.back-icon {
  height: 20px;
  width: 20px;
  padding-bottom: 0.1em;
  margin-right: 0.2em;
  color: var(--ion-color-primary);
}

.inline-back-button {
  font-size: 1.2em;
  color: var(--ion-color-primary);
  padding: 0 0.5em;
}

#section-content {
  padding: 20px 40px;
}

.modal-item {
  --background: var(--ion-background-color, #fff);
  --padding-start: 0;
}

.file-name {
  margin-top: 5px;
  font-size: 0.9em;
  color: #555;
}

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
}

ion-button.go-button::part(native) {
  color: white !important;
}

ion-button.post-button::part(native) {
  color: white !important;
}

.post-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 20px;
}

.post-actions ion-button {
  max-width: 300px;
  width: 100%;
  font-weight: 600;
  font-size: 1rem;
  padding: 14px;
  --border-radius: 12px;
  --box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.create-post {
  --background: var(--ion-color-primary);
  --color: rgb(255, 255, 255);
}

.go-button {
  margin-top: 20px;
  background: var(--ion-background-color);
  border-radius: 12px;
  padding: 10px 20px;
}

.file-upload-container {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
  margin-top: 0.75rem;
}

.file-upload-button {
  display: inline-block;
  padding: 8px 15px;
  background-color: var(--ion-color-secondary);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.file-upload-button input[type='file'] {
  position: absolute;
  left: 0;
  top: 0;
  opacity: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.file-name {
  margin-top: 5px;
  font-size: 0.9em;
  color: #555;
}
</style>
