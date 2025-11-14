<script setup lang="ts">
import { ref, watch } from 'vue';
import { alertController } from '@ionic/vue';
import { showToast } from '@/utils';
import { useAuthStore } from '@/store/reactiveStorage';
import {
  searchPlaylists,
  addPostToPlaylist,
  removePostFromPlaylist,
  fetchPlaylistBySlug,
} from '@/client/playlistClient';
import router from '@/router';

const props = defineProps<{
  postSlug: string;
  audioTitle?: string | null;
  audioUrl?: string | null;
  currentPlaylistSlug?: string | null;
}>();

const authStore = useAuthStore();
const savedPlaylistsSlugs = ref<string[]>([]);

const checkSaved = async (postSlug: string) => {
  const userSlug = authStore.getAuthenticatedUserSlug;
  if (!userSlug) return;

  const defaultPage = { page: 0, size: 20 };
  savedPlaylistsSlugs.value = [];

  try {
    const playlists = await searchPlaylists(userSlug);

    for (const pl of playlists.content) {
      const fullPlaylist = await fetchPlaylistBySlug(pl.slug, defaultPage);
      if (fullPlaylist.postDTO.content.some((p) => p.slug === postSlug)) {
        savedPlaylistsSlugs.value.push(pl.slug);
      }
    }
  } catch (error) {
    console.error('Failed to check saved playlists', error);
  }
};

const open = async () => {
  if (!props.postSlug) return;

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
        checked: savedPlaylistsSlugs.value.includes(pl.slug),
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
          },
        },
      ],
    });

    await alert.present();
  } catch (error: unknown) {
    const message = 'Failed to update playlists. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

watch(
  () => props.postSlug,
  () => {
    checkSaved(props.postSlug);
  },
  { immediate: true }
);

defineExpose({
  open,
});
</script>
