<script setup lang="ts">
import {
  IonButton,
  IonContent,
  IonHeader,
  IonTitle,
  IonToolbar,
  IonList,
  IonItem,
  IonLabel,
  IonInput,
  IonModal,
  IonTextarea,
  IonIcon,
  toastController,
  IonSelect,
  IonSelectOption,
} from '@ionic/vue';
import { trashOutline, addOutline, cloudDownloadOutline, arrowBackOutline } from 'ionicons/icons';
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import {
  getMediaKitBySlug,
  updateMediaKit,
  createMediaKit,
  deleteMediaKit,
  exportMediaKitPdf,
  addMediaKitItem,
  updateMediaKitItem,
  deleteMediaKitItem,
  getMediaKitByArtistSlug,
} from '@/client/mediaKitClient';

import type { MediaKitDTO, MediaKitItemDTO } from '@/entity/MediaKit';
import { useAuthStore } from '@/store/reactiveStorage';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const slug = ref<string | undefined>(route.params.slug as string | undefined);
const mediaKit = ref<MediaKitDTO | null>(null);
const mediaKitItems = ref<MediaKitItemDTO[]>([]);
const isLoading = ref(false);

const showEditMediaKitModal = ref(false);
const showEditItemModal = ref(false);
const showAddItemModal = ref(false);

const newMediaKit = ref<MediaKitDTO>({
  slug: '',
  title: '',
  shortBio: '',
  longBio: '',
  contactEmail: '',
  contactPhone: '',
  items: [],
});

const editedMediaKit = ref<MediaKitDTO>({
  slug: '',
  title: '',
  shortBio: '',
  longBio: '',
  contactEmail: '',
  contactPhone: '',
  items: [],
});

const editedItem = ref<MediaKitItemDTO | null>(null);
const newItem = ref<{ type: string; title: string; content: string }>({ type: '', title: '', content: '' });

const emptyMediaKit = (): MediaKitDTO => ({
  slug: '',
  title: '',
  shortBio: '',
  longBio: '',
  contactEmail: '',
  contactPhone: '',
  items: [],
});

const showToast = async (message: string, color: 'danger' | 'success' = 'danger') => {
  const toast = await toastController.create({ message, duration: 2000, color });
  await toast.present();
};

async function fetchMediaKit(slugToLoad: string) {
  isLoading.value = true;
  try {
    const response = await getMediaKitBySlug(slugToLoad);
    mediaKit.value = response;
    mediaKitItems.value = response.items || [];
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
    await updateMediaKit(mediaKit.value.slug, editedMediaKit.value);
    mediaKit.value = editedMediaKit.value;
    showEditMediaKitModal.value = false;
    await router.push(`/mediakits/${mediaKit.value.slug}`);
  } catch {
    await showToast('Failed to save MediaKit');
  }
};

const saveNewMediaKit = async () => {
  try {
    const created = await createMediaKit(newMediaKit.value);
    mediaKit.value = created;
    mediaKitItems.value = created.items || [];
    slug.value = created.slug;
    newMediaKit.value = emptyMediaKit();
    await router.push(`/mediakits/${slug.value}`);
  } catch {
    await showToast('Failed to create MediaKit');
  }
};

const removeMediaKit = async () => {
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

const downloadPdf = async () => {
  if (!mediaKit.value) return;
  try {
    const data = await exportMediaKitPdf(mediaKit.value.slug);
    const blob = new Blob([data], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);
    const link = Object.assign(document.createElement('a'), {
      href: url,
      download: `mediakit_${mediaKit.value.slug}.pdf`,
    });
    link.click();
    URL.revokeObjectURL(url);
  } catch {
    await showToast('Failed to download PDF, please check your item');
  }
};

const openAddItemModal = () => {
  newItem.value = { type: '', title: '', content: '' };
  showAddItemModal.value = true;
};

const saveNewItem = async () => {
  const artistSlug = authStore.getAuthenticatedUserSlug;
  if (!mediaKit.value || !artistSlug) return;
  try {
    const created = await addMediaKitItem(artistSlug, newItem.value);
    mediaKitItems.value.push(created);
    showAddItemModal.value = false;
  } catch {
    await showToast('Failed to add item');
  }
};

const openEditItemModal = (item: MediaKitItemDTO) => {
  editedItem.value = { ...item };
  showEditItemModal.value = true;
};

const saveEditedItem = async () => {
  if (!editedItem.value) return;
  try {
    const updated = await updateMediaKitItem(editedItem.value.slug, editedItem.value);
    const index = mediaKitItems.value.findIndex((i) => i.slug === updated.slug);
    if (index >= 0) mediaKitItems.value[index] = updated;
    showEditItemModal.value = false;
  } catch {
    await showToast('Failed to update item');
  }
};

const removeItem = async (slug: string) => {
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
      if (existing) {
        await router.replace(`/mediakits/${existing.slug}`);
      } else {
        newMediaKit.value = emptyMediaKit();
      }
    } catch {
      newMediaKit.value = emptyMediaKit();
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
    <IonHeader>
      <IonToolbar>
        <IonButton slot="start" fill="clear" @click="$router.back()">
          <IonIcon :icon="arrowBackOutline" />
          Back
        </IonButton>
        <IonTitle> MediaKit: {{ mediaKit?.title || (slug ? 'Loading...' : 'New MediaKit') }} </IonTitle>
      </IonToolbar>
    </IonHeader>

    <IonContent :fullscreen="true" class="ion-padding">
      <div v-if="isLoading">Loading...</div>
      <div v-else>
        <div v-if="!slug && !mediaKit">
          <h2>Create New MediaKit</h2>
          <IonItem>
            <IonLabel position="stacked">Title</IonLabel>
            <IonInput v-model="newMediaKit.title" />
          </IonItem>
          <IonItem>
            <IonLabel position="stacked">Short Bio</IonLabel>
            <IonTextarea v-model="newMediaKit.shortBio" :rows="3" />
          </IonItem>
          <IonItem>
            <IonLabel position="stacked">Long Bio</IonLabel>
            <IonTextarea v-model="newMediaKit.longBio" :rows="5" />
          </IonItem>
          <IonItem>
            <IonLabel position="stacked">Contact Email</IonLabel>
            <IonInput v-model="newMediaKit.contactEmail" />
          </IonItem>
          <IonItem>
            <IonLabel position="stacked">Contact Phone</IonLabel>
            <IonInput v-model="newMediaKit.contactPhone" />
          </IonItem>
          <IonButton expand="block" color="primary" @click="saveNewMediaKit">Create MediaKit</IonButton>
        </div>

        <div v-if="mediaKit">
          <h2>{{ mediaKit.title }}</h2>
          <p><strong>Short Bio:</strong> {{ mediaKit.shortBio }}</p>
          <p><strong>Long Bio:</strong> {{ mediaKit.longBio }}</p>
          <p><strong>Contact Email:</strong> {{ mediaKit.contactEmail }}</p>
          <p><strong>Contact Phone:</strong> {{ mediaKit.contactPhone }}</p>

          <IonButton color="primary" @click="openEditMediaKitModal">Edit MediaKit</IonButton>
          <IonButton color="danger" @click="removeMediaKit">Delete MediaKit</IonButton>
          <IonButton color="secondary" @click="downloadPdf">
            Download PDF
            <IonIcon :icon="cloudDownloadOutline" slot="end" />
          </IonButton>

          <h3 style="margin-top: 25px">MediaKit Items</h3>
          <IonButton color="primary" @click="openAddItemModal" style="margin-bottom: 15px">
            Add Item
            <IonIcon :icon="addOutline" slot="end" />
          </IonButton>

          <IonList>
            <IonItem v-for="item in mediaKitItems" :key="item.slug">
              <IonButton fill="clear" color="primary" @click="openEditItemModal(item)">Edit</IonButton>
              <IonButton fill="clear" color="danger" @click="removeItem(item.slug)">
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
          <IonContent>
            <div class="floating-header">
              <IonButton fill="clear" size="large" @click="showEditMediaKitModal = false">
                <IonIcon :icon="arrowBackOutline" />
                Back
              </IonButton>
              <h2>Edit MediaKit</h2>
            </div>
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
          <IonContent>
            <div class="floating-header">
              <IonButton fill="clear" size="large" @click="showAddItemModal = false">
                <IonIcon :icon="arrowBackOutline" />
                Back
              </IonButton>
              <h2>Add New Item</h2>
            </div>
            <IonItem>
              <IonLabel position="stacked">Type</IonLabel>
              <IonSelect v-model="newItem.type" placeholder="Select type">
                <IonSelectOption value="Image">Image</IonSelectOption>
                <IonSelectOption value="Video">Video</IonSelectOption>
                <IonSelectOption value="Audio">Audio</IonSelectOption>
                <IonSelectOption value="Text">Text</IonSelectOption>
              </IonSelect>
            </IonItem>
            <IonItem>
              <IonLabel position="stacked">Title</IonLabel>
              <IonInput v-model="newItem.title" />
            </IonItem>
            <IonItem>
              <IonLabel position="stacked">Content</IonLabel>
              <IonTextarea v-model="newItem.content" :rows="5" />
            </IonItem>
            <IonButton expand="block" color="primary" @click="saveNewItem">Add Item</IonButton>
          </IonContent>
        </IonModal>

        <IonModal v-if="editedItem" :is-open="showEditItemModal" @did-dismiss="showEditItemModal = false">
          <IonContent>
            <div class="floating-header">
              <IonButton fill="clear" size="large" @click="showEditItemModal = false">
                <IonIcon :icon="arrowBackOutline" />
                Back
              </IonButton>
              <h2>Edit Item</h2>
            </div>
            <IonItem>
              <IonLabel position="stacked">Type</IonLabel>
              <IonSelect v-model="editedItem.type" placeholder="Select type">
                <IonSelectOption value="Image">Image</IonSelectOption>
                <IonSelectOption value="Video">Video</IonSelectOption>
                <IonSelectOption value="Audio">Audio</IonSelectOption>
                <IonSelectOption value="Text">Text</IonSelectOption>
              </IonSelect>
            </IonItem>
            <IonItem>
              <IonLabel position="stacked">Title</IonLabel>
              <IonInput v-model="editedItem.title" />
            </IonItem>
            <IonItem>
              <IonLabel position="stacked">Content</IonLabel>
              <IonTextarea v-model="editedItem.content" :rows="5" />
            </IonItem>
            <IonButton expand="block" color="primary" @click="saveEditedItem">Save Item</IonButton>
          </IonContent>
        </IonModal>
      </div>
    </IonContent>
  </ApplicationViewport>
</template>
