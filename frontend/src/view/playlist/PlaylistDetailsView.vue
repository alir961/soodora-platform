<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  deletePlaylistBySlug,
  fetchPlaylistBySlug,
  removePostFromPlaylist,
  updatePlaylistBySlug,
} from '@/client/playlistClient';
import { LIKED_AUDIO_PLAYLIST_NAME, PlaylistFull, PlaylistPrivacy } from '@/entity/Playlist';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { arrowBackOutline, createOutline, musicalNotesOutline, trashOutline } from 'ionicons/icons';
import {
  IonAlert,
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonIcon,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
  IonInput,
  IonItem,
  IonLabel,
  IonList,
  IonListHeader,
  IonModal,
  IonSelect,
  IonSelectOption,
  IonTextarea,
  IonTitle,
  IonToolbar,
} from '@ionic/vue';
import PostCard from '@/component/explore/PostCard.vue';
import FileUpload from '@/component/file/FileUpload.vue';
import { Post } from '@/entity/Post';
import { showToast } from '@/utils';
import { useAuthStore } from '@/store/reactiveStorage';

const route = useRoute();
const router = useRouter();

const playlist = ref<PlaylistFull | null>(null);
const posts = ref<Post[]>([]);
const currentPage = ref(0);
const totalPages = ref(0);
const loading = ref(false);
const showDeleteConfirm = ref(false);
const showEditModal = ref(false);

const editName = ref('');
const editDescription = ref('');
const editPrivacy = ref<PlaylistPrivacy>('PUBLIC');

const authStore = useAuthStore();
const isOwner = computed(() => {
  return playlist.value?.playlistSimpleDTO.owner.slug === authStore.getAuthenticatedUserSlug;
});

const isLikedAudioPlaylist = computed(() => {
  return playlist.value?.playlistSimpleDTO.name === LIKED_AUDIO_PLAYLIST_NAME;
});

const loadPlaylist = async (slug: string, page = 0) => {
  loading.value = true;
  try {
    const pageable = { page, size: 20 };
    const data = await fetchPlaylistBySlug(slug, pageable);
    playlist.value = data;

    if (page === 0) posts.value = data.postDTO.content;
    else posts.value.push(...data.postDTO.content);

    currentPage.value = data.postDTO.page.number;
    totalPages.value = data.postDTO.page.totalPages;

    if (page === 0) {
      editName.value = data.playlistSimpleDTO.name;
      editDescription.value = data.playlistSimpleDTO.description;
      editPrivacy.value = data.playlistSimpleDTO.privacy;
    }
  } finally {
    loading.value = false;
  }
};

const loadMore = async (event: CustomEvent) => {
  const infiniteScroll = event.target as HTMLIonInfiniteScrollElement;

  if (!playlist.value) {
    await infiniteScroll.complete();
    return;
  }

  if (currentPage.value + 1 < totalPages.value)
    await loadPlaylist(playlist.value.playlistSimpleDTO.slug, currentPage.value + 1);

  await infiniteScroll.complete();
};

const openEditModal = () => {
  showEditModal.value = true;
};

const submitEdit = async () => {
  if (!playlist.value) return;

  const formData = new FormData();
  formData.append(
    'data',
    new Blob(
      [
        JSON.stringify({
          name: editName.value,
          description: editDescription.value,
          privacy: editPrivacy.value,
        }),
      ],
      { type: 'application/json' }
    )
  );
  if (selectedFile.value) formData.append('file', selectedFile.value);

  try {
    playlist.value.playlistSimpleDTO = await updatePlaylistBySlug(playlist.value.playlistSimpleDTO.slug, formData);
    showEditModal.value = false;
    selectedFile.value = null;
    await showToast('Playlist updated successfully');
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : 'Failed to update playlist';
    await showToast(message, 'danger');
  }
};

const confirmDeletePlaylist = () => {
  showDeleteConfirm.value = true;
};
const deletePlaylist = async () => {
  if (!playlist.value) return;
  await deletePlaylistBySlug(playlist.value.playlistSimpleDTO.slug);
  await router.push('/playlists/me');
};

const removePost = async (postSlug: string) => {
  if (!playlist.value) return;
  await removePostFromPlaylist(playlist.value.playlistSimpleDTO.slug, postSlug);
  posts.value = posts.value.filter((p) => p.slug !== postSlug);
};

const selectedFile = ref<File | null>(null);

const onFileSelected = (file: File | null) => {
  selectedFile.value = file;
};

watch(
  () => route.params.slug,
  (newSlug) => {
    if (typeof newSlug === 'string') loadPlaylist(newSlug, 0);
  },
  { immediate: true }
);
</script>

<template>
  <ApplicationViewport title="Playlist Details">
    <IonHeader>
      <IonToolbar>
        <IonButtons slot="start">
          <IonButton fill="clear" @click="$router.back()">
            <IonIcon :icon="arrowBackOutline" slot="start" />
            Back
          </IonButton>
        </IonButtons>

        <IonButtons slot="end" v-if="isOwner && !isLikedAudioPlaylist">
          <IonButton fill="clear" @click="openEditModal">
            <IonIcon :icon="createOutline" />
          </IonButton>
          <IonButton fill="clear" color="danger" @click="confirmDeletePlaylist">
            <IonIcon :icon="trashOutline" />
          </IonButton>
        </IonButtons>
      </IonToolbar>
      <IonTitle>{{ playlist?.playlistSimpleDTO.name }}</IonTitle>
    </IonHeader>

    <IonContent class="ion-padding">
      <div v-if="playlist?.playlistSimpleDTO.imageUrl" class="playlist-cover">
        <img :src="playlist.playlistSimpleDTO.imageUrl" alt="Playlist cover" />
      </div>

      <p class="playlist-description">{{ playlist?.playlistSimpleDTO.description }}</p>

      <IonList v-if="posts.length">
        <IonListHeader>
          <IonLabel>Tracks</IonLabel>
        </IonListHeader>

        <PostCard
          v-for="post in posts"
          :key="post.slug"
          :post="post"
          :currentPlaylistSlug="playlist?.playlistSimpleDTO.slug"
          @remove="removePost(post.slug)" />
      </IonList>

      <div v-else-if="!loading" class="empty-state">
        <IonIcon :icon="musicalNotesOutline" size="large" />
        <p>No tracks in this playlist yet.</p>
      </div>

      <IonInfiniteScroll threshold="100px" @ionInfinite="loadMore" :disabled="currentPage + 1 >= totalPages">
        <IonInfiniteScrollContent loading-spinner="bubbles" loading-text="Loading more tracks..." />
      </IonInfiniteScroll>
    </IonContent>

    <IonAlert
      :is-open="showDeleteConfirm"
      header="Delete Playlist"
      message="Are you sure you want to delete this playlist?"
      :buttons="[
        { text: 'Cancel', role: 'cancel', handler: () => (showDeleteConfirm = false) },
        { text: 'Delete', role: 'destructive', handler: deletePlaylist },
      ]"
      @didDismiss="showDeleteConfirm = false" />

    <IonModal :is-open="showEditModal" @didDismiss="showEditModal = false">
      <IonButton fill="clear" @click="$router.back()">
        <IonIcon :icon="arrowBackOutline" slot="start" />
        Back
      </IonButton>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Edit Playlist</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent class="ion-padding">
        <IonItem>
          <IonLabel position="stacked">Name</IonLabel>
          <IonInput v-model="editName" />
        </IonItem>

        <IonItem>
          <IonLabel position="stacked">Description</IonLabel>
          <IonTextarea v-model="editDescription" />
        </IonItem>

        <IonItem>
          <IonLabel position="stacked">Privacy</IonLabel>
          <IonSelect v-model="editPrivacy">
            <IonSelectOption value="PUBLIC">Public</IonSelectOption>
            <IonSelectOption value="PRIVATE">Private</IonSelectOption>
            <IonSelectOption value="UNLISTED">Unlisted</IonSelectOption>
          </IonSelect>
        </IonItem>

        <IonItem>
          <IonLabel position="stacked">Image</IonLabel>
          <FileUpload fieldLabel="Select Image" accept="image/*" @file-selected="onFileSelected" />
        </IonItem>

        <IonButton expand="block" @click="submitEdit">Save</IonButton>
      </IonContent>
    </IonModal>
  </ApplicationViewport>
</template>

<style scoped>
.playlist-cover img {
  width: 100%;
  border-radius: 12px;
  margin-bottom: 1rem;
}
.playlist-description {
  margin: 1rem 0;
  font-size: 0.95rem;
  color: var(--ion-color-medium);
  white-space: pre-wrap;
}
.empty-state {
  text-align: center;
  margin-top: 2rem;
  color: var(--ion-color-medium);
}
</style>
