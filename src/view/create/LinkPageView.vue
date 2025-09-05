<script setup lang="ts">
// SVG icons for platforms
const platformIcons: Record<string, string> = {
  instagram: `<svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" height="30" viewBox="0,0,256,256"style="fill:#FFFFFF;"><g fill="#ffffff" fill-rule="nonzero" stroke="none" stroke-width="1" stroke-linecap="butt" stroke-linejoin="miter" stroke-miterlimit="10" stroke-dasharray="" stroke-dashoffset="0" font-family="none" font-weight="none" font-size="none" text-anchor="none" style="mix-blend-mode: normal"><g transform="scale(10.66667,10.66667)"><path d="M8,3c-2.757,0 -5,2.243 -5,5v8c0,2.757 2.243,5 5,5h8c2.757,0 5,-2.243 5,-5v-8c0,-2.757 -2.243,-5 -5,-5zM8,5h8c1.654,0 3,1.346 3,3v8c0,1.654 -1.346,3 -3,3h-8c-1.654,0 -3,-1.346 -3,-3v-8c0,-1.654 1.346,-3 3,-3zM17,6c-0.55228,0 -1,0.44772 -1,1c0,0.55228 0.44772,1 1,1c0.55228,0 1,-0.44772 1,-1c0,-0.55228 -0.44772,-1 -1,-1zM12,7c-2.757,0 -5,2.243 -5,5c0,2.757 2.243,5 5,5c2.757,0 5,-2.243 5,-5c0,-2.757 -2.243,-5 -5,-5zM12,9c1.654,0 3,1.346 3,3c0,1.654 -1.346,3 -3,3c-1.654,0 -3,-1.346 -3,-3c0,-1.654 1.346,-3 3,-3z"></path></g></g></svg>`,
  x: `<svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" height="30" viewBox="0,0,256,256"style="fill:#FFFFFF;"><g fill="#ffffff" fill-rule="nonzero" stroke="none" stroke-width="1" stroke-linecap="butt" stroke-linejoin="miter" stroke-miterlimit="10" stroke-dasharray="" stroke-dashoffset="0" font-family="none" font-weight="none" font-size="none" text-anchor="none" style="mix-blend-mode: normal"><g transform="scale(5.12,5.12)"><path d="M5.91992,6l14.66211,21.375l-14.35156,16.625h3.17969l12.57617,-14.57812l10,14.57813h12.01367l-15.31836,-22.33008l13.51758,-15.66992h-3.16992l-11.75391,13.61719l-9.3418,-13.61719zM9.7168,8h7.16406l23.32227,34h-7.16406z"></path></g></g></svg>`,
  youtube: `<svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" height="30" viewBox="0,0,256,256"style="fill:#FFFFFF;"><g fill="#ffffff" fill-rule="nonzero" stroke="none" stroke-width="1" stroke-linecap="butt" stroke-linejoin="miter" stroke-miterlimit="10" stroke-dasharray="" stroke-dashoffset="0" font-family="none" font-weight="none" font-size="none" text-anchor="none" style="mix-blend-mode: normal"><g transform="scale(5.12,5.12)"><path d="M44.89844,14.5c-0.39844,-2.19922 -2.29687,-3.80078 -4.5,-4.30078c-3.29687,-0.69922 -9.39844,-1.19922 -16,-1.19922c-6.59766,0 -12.79687,0.5 -16.09766,1.19922c-2.19922,0.5 -4.10156,2 -4.5,4.30078c-0.40234,2.5 -0.80078,6 -0.80078,10.5c0,4.5 0.39844,8 0.89844,10.5c0.40234,2.19922 2.30078,3.80078 4.5,4.30078c3.5,0.69922 9.5,1.19922 16.10156,1.19922c6.60156,0 12.60156,-0.5 16.10156,-1.19922c2.19922,-0.5 4.09766,-2 4.5,-4.30078c0.39844,-2.5 0.89844,-6.10156 1,-10.5c-0.20312,-4.5 -0.70312,-8 -1.20312,-10.5zM19,32v-14l12.19922,7z"></path></g></g></svg>`,
  soundcloud: `<svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" height="30" viewBox="0,0,256,256"style="fill:#FFFFFF;"><g fill="#ffffff" fill-rule="nonzero" stroke="none" stroke-width="1" stroke-linecap="butt" stroke-linejoin="miter" stroke-miterlimit="10" stroke-dasharray="" stroke-dashoffset="0" font-family="none" font-weight="none" font-size="none" text-anchor="none" style="mix-blend-mode: normal"><g transform="scale(16,16)"><path d="M9.8125,4c-0.44531,0 -0.875,0.08594 -1.25781,0.23438c-0.14844,0.05859 -0.1875,0.12109 -0.1875,0.23438v6.29297c0,0.12109 0.09375,0.21484 0.21094,0.22656c0.00391,0.00391 5.45313,0 5.45313,0c1.08594,0 1.96875,-0.875 1.96875,-1.97266c0,-1.09375 -0.88281,-1.98437 -1.96875,-1.98437c-0.26953,0 -0.52734,0.05859 -0.76172,0.15625c-0.15234,-1.78516 -1.64453,-3.1875 -3.45703,-3.1875zM7.96094,4.46484c-0.05078,0 -0.09766,0.01953 -0.13281,0.05078c-0.05078,0.03906 -0.08203,0.10156 -0.08203,0.16797v0.01953l-0.08984,4.44531l0.04688,0.82031l0.04297,0.80078c0.00391,0.11719 0.09766,0.21484 0.21484,0.21484c0.12109,0 0.21484,-0.09766 0.21875,-0.21875l0.09766,-1.61719l-0.09766,-4.46484c-0.00391,-0.07812 -0.04687,-0.14844 -0.10937,-0.1875c-0.03125,-0.01953 -0.06641,-0.03125 -0.10938,-0.03125zM7.28906,4.85156c-0.03906,0 -0.07422,0.01172 -0.10937,0.03125c-0.05469,0.03516 -0.08984,0.09766 -0.09375,0.17188v0.03906l-0.07422,4.05469l0.07422,1.64453c0,0.00391 0,0.00391 0,0.00781c0.00391,0.04688 0.02344,0.08984 0.05078,0.12109c0.03516,0.04688 0.09375,0.07422 0.15234,0.07422c0.05859,0 0.10938,-0.02344 0.14453,-0.05859c0.03516,-0.03906 0.05859,-0.08984 0.05859,-0.14453l0.01172,-0.16406l0.07422,-1.48047l-0.08594,-4.09375c0,-0.07031 -0.03516,-0.13281 -0.08984,-0.17187c-0.03125,-0.01953 -0.07031,-0.03125 -0.11328,-0.03125zM5.28125,5.32031c-0.08984,0 -0.16016,0.07031 -0.16406,0.16406l-0.10156,3.66406l0.10156,1.67969c0.00391,0.08984 0.07422,0.16016 0.16406,0.16016c0.08984,0 0.16016,-0.07031 0.16406,-0.16016l0.10938,-1.67969l-0.10937,-3.66406c-0.00391,-0.09375 -0.07422,-0.16406 -0.16406,-0.16406zM5.94531,5.39844c-0.09766,0 -0.17187,0.07813 -0.17578,0.17969l-0.09375,3.57031l0.09375,1.66406c0.00391,0.09766 0.07813,0.17578 0.17578,0.17578c0.09375,0 0.17188,-0.07812 0.17188,-0.17578l0.10547,-1.66406l-0.10547,-3.57422c0,-0.09766 -0.07422,-0.17578 -0.17187,-0.17578zM4.62109,5.45313c-0.08203,0 -0.14453,0.0625 -0.14453,0.14844l-0.11328,3.54688l0.10938,1.69141c0.00391,0.08203 0.07031,0.14844 0.14844,0.14844c0.07813,0 0.14453,-0.0625 0.14844,-0.14453v-0.00391l0.12109,-1.69141l-0.12109,-3.54688c-0.00391,-0.08203 -0.07031,-0.14844 -0.14844,-0.14844zM6.61719,5.51563c-0.10547,0 -0.19141,0.08203 -0.19141,0.1875l-0.08203,3.44531l0.08203,1.65234c0.00391,0.10547 0.08203,0.19141 0.1875,0.19141c0.10156,0 0.1875,-0.08203 0.19141,-0.1875v-0.00391l0.09375,-1.65234l-0.09375,-3.44531c-0.00391,-0.10547 -0.08594,-0.1875 -0.1875,-0.1875zM3.95703,5.75c-0.07031,0 -0.13281,0.05859 -0.13672,0.13281l-0.11719,3.27344l0.11719,1.70703c0.00391,0.07813 0.0625,0.13672 0.13672,0.13672c0.07031,0 0.13281,-0.05859 0.13672,-0.13281v-0.00391l0.13281,-1.70703l-0.13281,-3.27344c-0.00391,-0.07422 -0.06641,-0.13281 -0.13672,-0.13281zM3.32031,6.35938c-0.06641,0 -0.12109,0.05469 -0.125,0.125l-0.125,2.66406l0.125,1.71875c0.00391,0.06641 0.05859,0.11719 0.125,0.11719c0.0625,0 0.11328,-0.05078 0.11719,-0.12109l0.14063,-1.71484l-0.14062,-2.66406c-0.00391,-0.07031 -0.05469,-0.125 -0.11719,-0.125zM2.03516,7.28906c-0.05078,0 -0.08984,0.03906 -0.09375,0.09375l-0.14062,1.76172l0.14063,1.70703c0.00391,0.05469 0.04297,0.09375 0.09375,0.09375c0.05078,0 0.08984,-0.03906 0.09375,-0.09375l0.16016,-1.70703l-0.16016,-1.76172c-0.00391,-0.05469 -0.04297,-0.09375 -0.09375,-0.09375zM1.39844,7.34766c-0.04297,0 -0.07422,0.03516 -0.07812,0.08203l-0.14844,1.71484l0.14844,1.65625c0.00391,0.04688 0.03906,0.07813 0.07813,0.07813c0.04297,0 0.07813,-0.03125 0.08203,-0.07812l0.16797,-1.65625l-0.16406,-1.71484c-0.00781,-0.04687 -0.04297,-0.08203 -0.08594,-0.08203zM2.67188,7.40234c-0.05469,0 -0.10156,0.04688 -0.10547,0.10547l-0.13281,1.63672l0.13281,1.71875c0.00391,0.0625 0.05078,0.10938 0.10938,0.10938c0.05859,0 0.10156,-0.04687 0.10547,-0.10937l0.15234,-1.71875l-0.15234,-1.63672c-0.00391,-0.05859 -0.05078,-0.10547 -0.10937,-0.10547zM0.77344,7.63281c-0.03516,0 -0.0625,0.02734 -0.07031,0.0625l-0.15625,1.44922l0.15625,1.41797c0.00781,0.03516 0.03516,0.0625 0.07031,0.0625c0.03516,0 0.05859,-0.02734 0.06641,-0.0625l0.17969,-1.41797l-0.17969,-1.44922c-0.00391,-0.03516 -0.03516,-0.0625 -0.06641,-0.0625zM0.17969,8.1875c-0.03125,0 -0.05859,0.02344 -0.0625,0.0625l-0.11719,0.89453l0.11719,0.88281c0.00391,0.03516 0.03125,0.0625 0.0625,0.0625c0.03516,0 0.05859,-0.02734 0.0625,-0.0625l0.14063,-0.87891l-0.14063,-0.89844c-0.00391,-0.03906 -0.02734,-0.0625 -0.0625,-0.0625z"></path></g></g></svg>`,
  tiktok: `<svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" height="30" viewBox="0,0,256,256"style="fill:#FFFFFF;"><g fill="#ffffff" fill-rule="nonzero" stroke="none" stroke-width="1" stroke-linecap="butt" stroke-linejoin="miter" stroke-miterlimit="10" stroke-dasharray="" stroke-dashoffset="0" font-family="none" font-weight="none" font-size="none" text-anchor="none" style="mix-blend-mode: normal"><g transform="scale(5.12,5.12)"><path d="M41,4h-32c-2.757,0 -5,2.243 -5,5v32c0,2.757 2.243,5 5,5h32c2.757,0 5,-2.243 5,-5v-32c0,-2.757 -2.243,-5 -5,-5zM37.006,22.323c-0.227,0.021 -0.457,0.035 -0.69,0.035c-2.623,0 -4.928,-1.349 -6.269,-3.388c0,5.349 0,11.435 0,11.537c0,4.709 -3.818,8.527 -8.527,8.527c-4.709,0 -8.527,-3.818 -8.527,-8.527c0,-4.709 3.818,-8.527 8.527,-8.527c0.178,0 0.352,0.016 0.527,0.027v4.202c-0.175,-0.021 -0.347,-0.053 -0.527,-0.053c-2.404,0 -4.352,1.948 -4.352,4.352c0,2.404 1.948,4.352 4.352,4.352c2.404,0 4.527,-1.894 4.527,-4.298c0,-0.095 0.042,-19.594 0.042,-19.594h4.016c0.378,3.591 3.277,6.425 6.901,6.685z"></path></g></g></svg>`,
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

    router.push(`/linkpages/${created.slug}`);
  } catch (error) {
    alert('Unable to create Link Page : ' + error);
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
  } catch (error) {
    if (isOwner.value) alert('Unable to load the Link Page : ' + error);
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

    alert('Link Page updated');
  } catch (error) {
    alert('Unable to update the Link Page : ' + error);
  }
}

async function deletePage() {
  if (!pageSlug.value) return;
  if (confirm('Confirm Link Page deletion ?')) {
    try {
      await deleteLinkPage(pageSlug.value);
      await router.push('/linkpages');
    } catch (error) {
      alert('Unable to delete the Link Page : ' + error);
    }
  }
}

async function submitLinkDeletion(index: number) {
  const link = links.value[index];

  if (confirm('Confirm the link deletion ?')) {
    try {
      if (pageSlug.value && link.slug) {
        await deleteLink(pageSlug.value, link.slug);
      }
      links.value.splice(index, 1);
    } catch (error) {
      alert('Unable to delete the link : ' + error);
    }
  }
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
                <div v-if="platformIcons[link.iconUrl]" v-html="platformIcons[link.iconUrl]" />
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
