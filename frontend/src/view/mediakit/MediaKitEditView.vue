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
import { showToast } from '@/utils';
import FileUpload from '@/component/file/FileUpload.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const IMAGE = 'Image';

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

async function fetchMediaKit(slugToLoad: string) {
  isLoading.value = true;
  try {
    const mk = await getMediaKitBySlug(slugToLoad);
    mediaKit.value = mk;
    mediaKitItems.value = mk.items || [];
    await showToast('Succeed to load MediaKit');
  } catch (error: unknown) {
    const message = 'Failed to load MediaKit. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
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
    await showToast('Succeed to save MediaKit');
  } catch (error: unknown) {
    const message = 'Failed to save MediaKit. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
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
    await showToast('Succeed to delete MediaKit');
  } catch (error: unknown) {
    const message = 'Failed to delete MediaKit. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
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
    await showToast('Succeed to download PDF');
  } catch (error: unknown) {
    const message = 'Failed to download PDF. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

const openAddItemModalHandler = () => {
  newItem.value = { type: '', title: '', content: '' };
  selectedFile.value = null;
  showAddItemModal.value = true;
};

const onFileSelected = (file: File | null) => {
  selectedFile.value = file;
  if (file) newItem.value.content = '';
};

const saveNewItemHandler = async () => {
  const artistSlug = authStore.getAuthenticatedUserSlug;
  if (!mediaKit.value || !artistSlug) return;

  try {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(newItem.value)], { type: 'application/json' }));

    if (newItem.value.type === IMAGE && selectedFile.value) {
      formData.append('file', selectedFile.value);
    }

    const created = await addMediaKitItem(artistSlug, formData);
    mediaKitItems.value.push(created);
    showAddItemModal.value = false;
    selectedFile.value = null;
    await showToast('Succeed to add item');
  } catch (error: unknown) {
    const message = 'Failed to add item. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

const openEditItemModalHandler = (item: MediaKitItemDTO) => {
  editedItem.value = { ...item };
  editedSelectedFile.value = null;
  showEditItemModal.value = true;
};

const onEditedFileSelected = (file: File | null) => {
  editedSelectedFile.value = file;
  if (file && editedItem.value) {
    editedItem.value.content = '';
  }
};

const saveEditedItemHandler = async () => {
  if (!editedItem.value) return;

  try {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(editedItem.value)], { type: 'application/json' }));

    if (editedItem.value.type === IMAGE && editedSelectedFile.value) {
      formData.append('file', editedSelectedFile.value);
    }

    const updated = await updateMediaKitItem(editedItem.value.slug, formData);
    const index = mediaKitItems.value.findIndex((i) => i.slug === updated.slug);
    if (index >= 0) mediaKitItems.value[index] = updated;
    showEditItemModal.value = false;
    selectedFile.value = null;
    await showToast('Succeed to update item');
  } catch (error: unknown) {
    const message = 'Failed to update item. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

const removeItemHandler = async (slug: string) => {
  try {
    await deleteMediaKitItem(slug);
    mediaKitItems.value = mediaKitItems.value.filter((i) => i.slug !== slug);
    await showToast('Succeed to delete item');
  } catch (error: unknown) {
    const message = 'Failed to delete item. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
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
    } catch (error: unknown) {
      const message = 'Redirection to MediaKit failed. ' + (error instanceof Error ? error.message : '');
      await showToast(message, 'danger');
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

          <IonItem v-if="newItem.type === IMAGE">
            <FileUpload fieldLabel="Select Image" accept="image/*" @file-selected="onFileSelected" />
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

          <IonItem v-if="editedItem.type === IMAGE">
            <FileUpload fieldLabel="Select Image" accept="image/*" @file-selected="onEditedFileSelected" />
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
