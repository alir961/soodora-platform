<script setup lang="ts">
import { showToast } from '@/utils';
// SVG icons for platforms
import instagramIcon from '@/assets/icons/instagram.svg';
import xIcon from '@/assets/icons/x.svg';
import youtubeIcon from '@/assets/icons/youtube.svg';
import soundcloudIcon from '@/assets/icons/soundcloud.svg';
import tiktokIcon from '@/assets/icons/tiktok.svg';

const platformIcons: Record<string, string> = {
  instagram: instagramIcon,
  x: xIcon,
  youtube: youtubeIcon,
  soundcloud: soundcloudIcon,
  tiktok: tiktokIcon,
};
import { ref, computed, watch } from 'vue';
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  IonButton,
  IonInput,
  IonTextarea,
  IonContent,
  onIonViewDidEnter,
  IonIcon,
  IonItem,
  IonLabel,
  IonSelect,
  IonSelectOption,
  alertController,
} from '@ionic/vue';
import { arrowBackOutline, trashBin } from 'ionicons/icons';
import {
  createLinkPage,
  getLinkPage,
  updateLinkPage,
  deleteLinkPage,
  createLink,
  updateLink,
  deleteLink,
  getUserLinkPage,
  CreateLinkPageRequest,
  CreateLinkRequest,
} from '@/client/linkPageClient';
import { Link } from '@/entity/Link';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { useAuthStore } from '@/store/reactiveStorage';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const pageSlug = computed(() => route.params.slug as string | undefined);
const isCreateMode = computed(() => !pageSlug.value);

const pageData = ref({
  title: '',
  description: '',
});

const checkedOwnership = ref(false);

type EditableLink = CreateLinkRequest & { slug?: string };
const links = ref<EditableLink[]>([]);
const isOwner = ref(false);
const activeNewLinkIndex = ref<number | null>(null);

// Dropdown options for iconUrl/platform selection
const iconOptions = [
  { label: 'Instagram', value: 'instagram' },
  { label: 'X', value: 'x' },
  { label: 'YouTube', value: 'youtube' },
  { label: 'SoundCloud', value: 'soundcloud' },
  { label: 'TikTok', value: 'tiktok' },
  { label: 'Other', value: 'other' },
];

function addLink() {
  const newIndex = links.value.length;
  links.value.push({
    title: '',
    description: '',
    url: '',
    iconUrl: '',
  });
  activeNewLinkIndex.value = newIndex;
}

function resetData() {
  pageData.value.title = '';
  pageData.value.description = '';
  links.value = [];
  isOwner.value = false;
}

async function submitCreate() {
  try {
    const pageCommand: CreateLinkPageRequest = {
      title: pageData.value.title,
      description: pageData.value.description,
    };
    const created = await createLinkPage(pageCommand);

    for (const link of links.value) {
      await createLink(created.slug, link);
    }

    await showToast('Succeed to create Link Page.');
    await router.push(`/linkpages/${created.slug}`);
  } catch (error: unknown) {
    const message = 'Unable to create Link Page. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
}

async function loadPage() {
  if (!pageSlug.value || isCreateMode.value) {
    return;
  }
  try {
    const page = await getLinkPage(pageSlug.value);
    pageData.value.title = page.title;
    pageData.value.description = page.description;
    links.value = (page.links || []).map((link: Link) => ({
      slug: link.slug,
      title: link.title,
      description: link.description,
      url: link.url,
      iconUrl: link.iconUrl,
    }));

    isOwner.value = authStore.getAuthenticatedUserSlug === page.owner.slug;
  } catch (error: unknown) {
    if (isOwner.value) {
      const message = 'Unable to load the Link Page.' + (error instanceof Error ? error.message : '');
      await showToast(message, 'danger');
    }
  }
}

async function submitUpdate() {
  if (!pageSlug.value) return;
  try {
    await updateLinkPage(pageSlug.value, {
      title: pageData.value.title,
      description: pageData.value.description,
    });

    for (const link of links.value) {
      if ((link as Link).slug) {
        await updateLink(pageSlug.value, (link as Link).slug, link);
      } else {
        await createLink(pageSlug.value, link);
      }
    }

    await showToast('Succeed to update Link Page.');
  } catch (error: unknown) {
    const message = 'Unable to update the Link Page.' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
}

async function deletePage() {
  if (!pageSlug.value) return;

  const alert = await alertController.create({
    header: 'Confirm Deletion',
    message: 'Are you sure you want to delete this Link Page ?',
    buttons: [
      {
        text: 'Cancel',
        role: 'cancel',
      },
      {
        text: 'Delete',
        role: 'destructive',
        handler: async () => {
          try {
            await deleteLinkPage(pageSlug.value!);
            await router.push('/linkpages');
          } catch (error: unknown) {
            const message = 'Unable to delete the Link Page.' + (error instanceof Error ? error.message : '');
            await showToast(message, 'danger');
          }
        },
      },
    ],
  });

  await alert.present();
}

async function submitLinkDeletion(index: number) {
  const link = links.value[index];

  const alert = await alertController.create({
    header: 'Confirm Deletion',
    message: 'Are you sure you want to delete this link ?',
    buttons: [
      {
        text: 'Cancel',
        role: 'cancel',
      },
      {
        text: 'Delete',
        role: 'destructive',
        handler: async () => {
          try {
            if (pageSlug.value && link.slug) {
              await deleteLink(pageSlug.value, link.slug);
            }
            links.value.splice(index, 1);
          } catch (error: unknown) {
            const message = 'Unable to delete the Link.' + (error instanceof Error ? error.message : '');
            await showToast(message, 'danger');
          }
        },
      },
    ],
  });

  await alert.present();
}

async function checkIfUserHasLinkPage() {
  try {
    const userSlug = authStore.getAuthenticatedUserSlug;

    if (!userSlug) {
      isOwner.value = false;
      checkedOwnership.value = true;
      return;
    }

    if (!pageSlug.value) {
      const userPage = await getUserLinkPage(userSlug);

      if (userPage) {
        await router.push(`/linkpages/${userPage.slug}`);
        checkedOwnership.value = true;
        return;
      }
      isOwner.value = false;
    } else {
      const page = await getLinkPage(pageSlug.value);

      isOwner.value = userSlug === page.owner.slug;
    }
    checkedOwnership.value = true;
  } catch (error) {
    isOwner.value = false;
    checkedOwnership.value = true;
  }
}

function copyPageUrl() {
  if (pageSlug.value) {
    navigator.clipboard.writeText(`${window.location.origin}/linkpages/${pageSlug.value}`);
  }
}

onIonViewDidEnter(async () => {
  await checkIfUserHasLinkPage();
  await loadPage();
});

onMounted(async () => {
  await checkIfUserHasLinkPage();
  await loadPage();
});

watch(
  () => route.fullPath,
  (newPath) => {
    if (newPath === '/linkpages' && isCreateMode.value) {
      checkIfUserHasLinkPage();
    }
  },
  { immediate: true }
);

watch(
  () => route.params.slug,
  (newSlug) => {
    if (!newSlug) {
      resetData();
    }
  }
);
</script>

<template>
  <ApplicationViewport title="Link Page">
    <ion-content class="ion-padding">
      <!-- Show create UI immediately -->
      <div v-if="isCreateMode">
        <IonButton class="inline-back-button" slot="start" fill="clear" @click="$router.back()">
          <IonIcon :icon="arrowBackOutline" />
          Back
        </IonButton>
        <h2>Link Page creation</h2>
        <IonInput v-model="pageData.title" placeholder="Link Page title" class="form-group" />
        <IonTextarea v-model="pageData.description" placeholder="Link Page description" class="form-group" />

        <div class="buttons">
          <IonButton expand="block" @click="submitCreate">Create the Link Page</IonButton>
        </div>
      </div>

      <!-- For slug pages -->
      <div v-else>
        <div v-if="!checkedOwnership">
          <p>Loading...</p>
        </div>

        <div v-else>
          <div v-if="isOwner">
            <IonButton class="inline-back-button" slot="start" fill="clear" @click="$router.back()">
              <IonIcon :icon="arrowBackOutline" />
              Back
            </IonButton>
            <div class="links-header">
              <h3>Copy Link Page Link</h3>
              <IonButton size="small" fill="outline" @click="copyPageUrl">Copy</IonButton>
            </div>
            <IonInput v-model="pageData.title" placeholder="Link Page title" class="form-group" />
            <IonTextarea v-model="pageData.description" placeholder="Link Page description" class="form-group" />

            <div class="links-header">
              <h3>Links</h3>
              <IonButton size="small" fill="outline" @click="addLink">+ Add Link</IonButton>
            </div>

            <div v-for="(link, idx) in links" :key="idx" class="link-block">
              <IonInput v-model="link.title" placeholder="Link title" class="form-group" />
              <IonInput v-model="link.description" placeholder="Link description" class="form-group" />
              <IonInput v-model="link.url" placeholder="Link URL" class="form-group" />
              <IonItem class="form-group">
                <IonLabel>Platform</IonLabel>
                <IonSelect v-model="link.iconUrl" placeholder="Choose one">
                  <IonSelectOption v-for="option in iconOptions" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </IonSelectOption>
                </IonSelect>
              </IonItem>
              <div class="delete-icon-wrapper" @click="submitLinkDeletion(idx)">
                <IonIcon class="delete-icon" :icon="trashBin" />
              </div>
            </div>

            <div class="buttons">
              <IonButton expand="block" @click="submitUpdate">Submit Link Page modification</IonButton>
              <IonButton expand="block" color="danger" @click="deletePage">Delete the Link Page</IonButton>
            </div>
          </div>

          <div v-else>
            <div>
              <h2>{{ pageData.title }}</h2>
              <p>{{ pageData.description }}</p>
            </div>
            <div v-for="(link, idx) in links" :key="idx" class="link-block">
              <h3>{{ link.title }}</h3>
              <p>{{ link.description }}</p>
              <div class="form-group" style="display: flex; align-items: center; gap: 10px">
                <IonIcon v-if="platformIcons[link.iconUrl]" :src="platformIcons[link.iconUrl]" />
                <a
                  :href="link.url.startsWith('http') ? link.url : 'https://' + link.url"
                  target="_blank"
                  rel="noopener noreferrer"
                  style="color: var(--ion-color-primary); text-decoration: underline">
                  {{ link.url }}
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
.form-group {
  margin-bottom: 16px;
}

.delete-icon-wrapper {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 8px;
  cursor: pointer;
  z-index: 2;
}

.delete-icon {
  color: var(--ion-color-danger);
  font-size: 20px;
}

.link-block {
  position: relative;
  padding: 16px;
  margin-bottom: 20px;
  border: 1px solid var(--ion-color-light);
  border-radius: 8px;
  background: var(--ion-background-color, #fff);
}

.buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
  margin-bottom: 6rem;
}

ion-button[expand='block'] {
  margin-top: 10px;
}

.links-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 24px 0 12px;
}

.inline-back-button {
  font-size: 1.2em;
  color: var(--ion-color-primary);
  position: relative;
  right: 1.5rem;
}
</style>
