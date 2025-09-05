<script setup lang="ts">
import {
  IonButton,
  IonContent,
  IonIcon,
  IonInput,
  IonItem,
  IonLabel,
  IonList,
  IonModal,
  IonSelect,
  IonSelectOption,
  IonTextarea,
  toastController,
} from '@ionic/vue';
import { addOutline, arrowBackOutline, cloudDownloadOutline, trashOutline } from 'ionicons/icons';
import { onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  addMediaKitItem,
  deleteMediaKit,
  deleteMediaKitItem,
  exportMediaKitPdf,
  getMediaKitByArtistSlug,
  getMediaKitBySlug,
  updateMediaKit,
  updateMediaKitItem,
} from '@/client/mediaKitClient';
import { useAuthStore } from '@/store/reactiveStorage';
import type { MediaKitDTO, MediaKitItemDTO } from '@/entity/MediaKit';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const slug = ref<string | undefined>(route.params.slug as string | undefined);
const mediaKit = ref<MediaKitDTO | null>(null);
const mediaKitItems = ref<MediaKitItemDTO[]>([]);
const isLoading = ref(false);

const showEditMediaKitModal = ref(false);
const showAddItemModal = ref(false);
const showEditItemModal = ref(false);

const editedMediaKit = ref<MediaKitDTO>({
  slug: '',
  title: '',
  shortBio: '',
  longBio: '',
  contactEmail: '',
  contactPhone: '',
  items: [],
});

const newItem = ref<{ type: string; title: string; content: string }>({ type: '', title: '', content: '' });
const selectedFile = ref<File | null>(null);

const editedItem = ref<MediaKitItemDTO | null>(null);
const editedSelectedFile = ref<File | null>(null);

const showToast = async (message: string, color: 'danger' | 'success' = 'danger') => {
  const toast = await toastController.create({ message, duration: 2000, color });
  await toast.present();
};

async function fetchMediaKit(slugToLoad: string) {
  isLoading.value = true;
  try {
    const mk = await getMediaKitBySlug(slugToLoad);
    mediaKit.value = mk;
    mediaKitItems.value = mk.items || [];
  } catch {
    await showToast('Failed to load MediaKit');
  } finally {
    isLoading.value = false;
  }
}

const openEditMediaKitModal = () => {
  if (!mediaKit.value) return;
  editedMediaKit.value = { ...mediaKit.value };
  showEditMediaKitModal.value = true;
};

const saveEditedMediaKit = async () => {
  if (!editedMediaKit.value || !mediaKit.value) return;
  try {
    mediaKit.value = await updateMediaKit(mediaKit.value.slug, editedMediaKit.value);
    showEditMediaKitModal.value = false;
    await router.push(`/mediakits/${mediaKit.value.slug}`);
  } catch {
    await showToast('Failed to save MediaKit');
  }
};

const removeMediaKitHandler = async () => {
  if (!mediaKit.value) return;
  try {
    await deleteMediaKit(mediaKit.value.slug);
    mediaKit.value = null;
    mediaKitItems.value = [];
    editedItem.value = null;
    await router.push('/create');
  } catch {
    await showToast('Failed to delete MediaKit');
  }
};

const downloadPdfHandler = async () => {
  if (!mediaKit.value) return;
  try {
    const blob = await exportMediaKitPdf(mediaKit.value.slug);
    const url = URL.createObjectURL(blob);
    const link = Object.assign(document.createElement('a'), {
      href: url,
      download: `mediakit_${mediaKit.value.slug}.pdf`,
    });
    link.click();
    URL.revokeObjectURL(url);
  } catch {
    await showToast('Failed to download PDF');
  }
};

const openAddItemModalHandler = () => {
  newItem.value = { type: '', title: '', content: '' };
  selectedFile.value = null;
  showAddItemModal.value = true;
};

const onFileSelected = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    selectedFile.value = target.files[0];
    newItem.value.content = '';
  } else {
    selectedFile.value = null;
  }
};

const saveNewItemHandler = async () => {
  const artistSlug = authStore.getAuthenticatedUserSlug;
  if (!mediaKit.value || !artistSlug) return;

  try {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(newItem.value)], { type: 'application/json' }));

    if (newItem.value.type === 'Image' && selectedFile.value) {
      formData.append('file', selectedFile.value);
    }

    const created = await addMediaKitItem(artistSlug, formData);
    mediaKitItems.value.push(created);
    showAddItemModal.value = false;
    selectedFile.value = null;
  } catch {
    await showToast('Failed to add item');
  }
};

const openEditItemModalHandler = (item: MediaKitItemDTO) => {
  editedItem.value = { ...item };
  editedSelectedFile.value = null;
  showEditItemModal.value = true;
};

const onEditedFileSelected = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    editedSelectedFile.value = target.files[0];
    if (editedItem.value) editedItem.value.content = '';
  } else {
    editedSelectedFile.value = null;
  }
};

const saveEditedItemHandler = async () => {
  if (!editedItem.value) return;

  try {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(editedItem.value)], { type: 'application/json' }));

    if (editedItem.value.type === 'Image' && selectedFile.value) {
      formData.append('file', selectedFile.value);
    }

    const updated = await updateMediaKitItem(editedItem.value.slug, formData);
    const index = mediaKitItems.value.findIndex((i) => i.slug === updated.slug);
    if (index >= 0) mediaKitItems.value[index] = updated;
    showEditItemModal.value = false;
    selectedFile.value = null;
  } catch {
    await showToast('Failed to update item');
  }
};

const removeItemHandler = async (slug: string) => {
  try {
    await deleteMediaKitItem(slug);
    mediaKitItems.value = mediaKitItems.value.filter((i) => i.slug !== slug);
  } catch {
    await showToast('Failed to delete item');
  }
};

onMounted(async () => {
  if (slug.value) {
    await fetchMediaKit(slug.value);
  } else {
    const artistSlug = authStore.getAuthenticatedUserSlug;
    try {
      const existing = artistSlug ? await getMediaKitByArtistSlug(artistSlug) : null;
      if (existing) await router.replace(`/mediakits/${existing.slug}`);
    } catch {
      await showToast('Redirection to MediaKit failed');
    }
  }
});

watch(
  () => route.params.slug,
  async (newSlug) => {
    slug.value = newSlug as string | undefined;
    if (slug.value) await fetchMediaKit(slug.value);
  }
);
</script>

<template>
  <ApplicationViewport title="Media Kit">
    <IonContent class="ion-padding">
      <div v-if="isLoading">Loading...</div>

      <div v-else-if="mediaKit">
        <IonButton slot="start" fill="clear" @click="$router.back()">
          <IonIcon :icon="arrowBackOutline" />
          Back
        </IonButton>
        <h2>{{ mediaKit.title }}</h2>
        <p><strong>Short Bio:</strong> {{ mediaKit.shortBio }}</p>
        <p><strong>Long Bio:</strong> {{ mediaKit.longBio }}</p>
        <p><strong>Contact Email:</strong> {{ mediaKit.contactEmail }}</p>
        <p><strong>Contact Phone:</strong> {{ mediaKit.contactPhone }}</p>

        <IonButton color="primary" @click="openEditMediaKitModal">Edit MediaKit</IonButton>
        <IonButton color="danger" @click="removeMediaKitHandler">Delete MediaKit</IonButton>
        <IonButton color="secondary" @click="downloadPdfHandler">
          Download PDF
          <IonIcon :icon="cloudDownloadOutline" slot="end" />
        </IonButton>

        <h3 style="margin-top: 25px">MediaKit Items</h3>
        <IonButton color="primary" @click="openAddItemModalHandler" style="margin-bottom: 15px">
          Add Item
          <IonIcon :icon="addOutline" slot="end" />
        </IonButton>

        <IonList>
          <IonItem v-for="item in mediaKitItems" :key="item.slug">
            <IonButton fill="clear" color="primary" @click="openEditItemModalHandler(item)">Edit</IonButton>
            <IonButton fill="clear" color="danger" @click="removeItemHandler(item.slug)">
              <IonIcon :icon="trashOutline" />
            </IonButton>
            <IonLabel>
              <h4>{{ item.title }} ({{ item.type }})</h4>
              <p>{{ item.content }}</p>
            </IonLabel>
          </IonItem>
        </IonList>
      </div>

      <IonModal :is-open="showEditMediaKitModal" @did-dismiss="showEditMediaKitModal = false">
        <IonContent class="ion-padding">
          <IonButton fill="clear" size="large" @click="showEditMediaKitModal = false">
            <IonIcon :icon="arrowBackOutline" />
            Back
          </IonButton>
          <h2>Edit MediaKit</h2>
          <IonItem>
            <IonLabel position="stacked">Title</IonLabel>
            <IonInput v-model="editedMediaKit.title" />
          </IonItem>
          <IonItem>
            <IonLabel position="stacked">Short Bio</IonLabel>
            <IonTextarea v-model="editedMediaKit.shortBio" :rows="3" />
          </IonItem>
          <IonItem>
            <IonLabel position="stacked">Long Bio</IonLabel>
            <IonTextarea v-model="editedMediaKit.longBio" :rows="5" />
          </IonItem>
          <IonItem>
            <IonLabel position="stacked">Contact Email</IonLabel>
            <IonInput v-model="editedMediaKit.contactEmail" />
          </IonItem>
          <IonItem>
            <IonLabel position="stacked">Contact Phone</IonLabel>
            <IonInput v-model="editedMediaKit.contactPhone" />
          </IonItem>
          <IonButton expand="block" color="primary" @click="saveEditedMediaKit">Save MediaKit</IonButton>
        </IonContent>
      </IonModal>

      <IonModal :is-open="showAddItemModal" @did-dismiss="showAddItemModal = false">
        <IonContent class="ion-padding">
          <IonButton fill="clear" size="large" @click="showAddItemModal = false">
            <IonIcon :icon="arrowBackOutline" />
            Back
          </IonButton>
          <h2>Add New Item</h2>
          <IonItem>
            <IonLabel position="stacked">Type</IonLabel>
            <IonSelect v-model="newItem.type" placeholder="Select type">
              <IonSelectOption value="Image">Image</IonSelectOption>
              <IonSelectOption value="Video">Video</IonSelectOption>
              <IonSelectOption value="Audio">Audio</IonSelectOption>
              <IonSelectOption value="Text">Text</IonSelectOption>
            </IonSelect>
          </IonItem>

          <IonItem v-if="newItem.type === 'Image'">
            <IonLabel position="stacked">Select Image</IonLabel>
            <div class="file-upload-container">
              <IonLabel class="file-upload-button">
                <ion-icon :icon="cloudDownloadOutline" />
                Choose File
                <input type="file" @change="onFileSelected" accept="image/*" />
              </IonLabel>
              <span v-if="selectedFile" class="file-name">{{ selectedFile.name }}</span>
            </div>
          </IonItem>

          <IonItem>
            <IonLabel position="stacked">Title</IonLabel>
            <IonInput v-model="newItem.title" />
          </IonItem>
          <IonItem>
            <IonLabel position="stacked">Content</IonLabel>
            <IonTextarea :disabled="selectedFile !== null" v-model="newItem.content" :rows="5" />
          </IonItem>
          <IonButton expand="block" color="primary" @click="saveNewItemHandler">Add Item</IonButton>
        </IonContent>
      </IonModal>

      <IonModal v-if="editedItem" :is-open="showEditItemModal" @did-dismiss="showEditItemModal = false">
        <IonContent class="ion-padding">
          <IonButton fill="clear" size="large" @click="showEditItemModal = false">
            <IonIcon :icon="arrowBackOutline" />
            Back
          </IonButton>
          <h2>Edit Item</h2>
          <IonItem>
            <IonLabel position="stacked">Type</IonLabel>
            <IonSelect v-model="editedItem.type" placeholder="Select type">
              <IonSelectOption value="Image">Image</IonSelectOption>
              <IonSelectOption value="Video">Video</IonSelectOption>
              <IonSelectOption value="Audio">Audio</IonSelectOption>
              <IonSelectOption value="Text">Text</IonSelectOption>
            </IonSelect>
          </IonItem>

          <IonItem v-if="editedItem.type === 'Image'">
            <IonLabel position="stacked">Select Image</IonLabel>
            <div class="file-upload-container">
              <IonLabel class="file-upload-button">
                <ion-icon :icon="cloudDownloadOutline" />
                Choose File
                <input type="file" @change="onEditedFileSelected" accept="image/*" />
              </IonLabel>
              <span v-if="editedSelectedFile" class="file-name">{{ editedSelectedFile.name }}</span>
            </div>
          </IonItem>

          <IonItem>
            <IonLabel position="stacked">Title</IonLabel>
            <IonInput v-model="editedItem.title" />
          </IonItem>
          <IonItem>
            <IonLabel position="stacked">Content</IonLabel>
            <IonTextarea :disabled="editedSelectedFile !== null" v-model="editedItem.content" :rows="5" />
          </IonItem>
          <IonButton expand="block" color="primary" @click="saveEditedItemHandler">Save Item</IonButton>
        </IonContent>
      </IonModal>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
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
