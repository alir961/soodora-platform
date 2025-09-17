<script setup lang="ts">
import { ref, defineProps, watch, computed } from 'vue';
import { IonButton, IonIcon, alertController } from '@ionic/vue';
import { bookmarkOutline, bookmark } from 'ionicons/icons';
import { showToast } from '@/utils';
import { useAuthStore } from '@/store/reactiveStorage';
import {
  searchPlaylists,
  addPostToPlaylist,
  removePostFromPlaylist,
  fetchPlaylistBySlug,
} from '@/client/playlistClient';
import router from '@/router';
import { Pageable } from '@/entity/PageableParams';

const props = defineProps<{
  postSlug: string;
  audioTitle?: string | null;
  audioUrl?: string | null;
  currentPlaylistSlug?: string | null;
}>();

const authStore = useAuthStore();
const isLoading = ref(false);
const savedPlaylistsSlugs = ref<string[]>([]);
const isSaved = computed(() => savedPlaylistsSlugs.value.length > 0);

const checkSaved = async (postSlug: string) => {
  const userSlug = authStore.getAuthenticatedUserSlug;
  if (!userSlug) return;

  const defaultPage: Pageable = { page: 0, size: 20 };
  savedPlaylistsSlugs.value = [];

  const playlists = await searchPlaylists(userSlug);

  for (const pl of playlists.content) {
    const fullPlaylist = await fetchPlaylistBySlug(pl.slug, defaultPage);
    if (fullPlaylist.postDTO.content.some((p) => p.slug === postSlug)) {
      savedPlaylistsSlugs.value.push(pl.slug);
    }
  }
};

const handleSavePost = async () => {
  if (!props.postSlug) return;
  isLoading.value = true;

  try {
    const userSlug = authStore.getAuthenticatedUserSlug;
    if (!userSlug) return;

    const userPlaylists = await searchPlaylists(userSlug);

    if (!userPlaylists.content.length) {
      await router.push('/playlists/create');
      return;
    }

    const alert = await alertController.create({
      header: 'Save to Playlists',
      inputs: userPlaylists.content.map((pl) => ({
        type: 'checkbox' as const,
        label: pl.name,
        value: pl.slug,
        checked: savedPlaylistsSlugs.value.includes(pl.slug), // sync state
      })),
      buttons: [
        { text: 'Cancel', role: 'cancel' },
        {
          text: 'Save',
          handler: async (selected: string[]) => {
            const previous = new Set(savedPlaylistsSlugs.value);
            const current = new Set(selected);

            for (const slug of current) {
              if (!previous.has(slug)) await addPostToPlaylist(slug, props.postSlug);
            }

            for (const slug of previous) {
              if (!current.has(slug)) await removePostFromPlaylist(slug, props.postSlug);
            }

            savedPlaylistsSlugs.value = [...current];
            await checkSaved(props.postSlug);
            await showToast('Playlists updated!', 'success');
          },
        },
      ],
    });

    await alert.present();
  } catch (error: unknown) {
    const message = 'Failed to update playlists. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
};

watch(
  () => props.postSlug,
  () => {
    checkSaved(props.postSlug);
  },
  { immediate: true }
);
</script>

<template>
  <ion-button
    v-if="props.audioTitle && props.audioUrl"
    fill="clear"
    size="default"
    @click.stop="handleSavePost"
    :disabled="isLoading">
    <ion-icon :icon="isSaved ? bookmark : bookmarkOutline" />
  </ion-button>
</template>
