<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { IonButton, IonInput, IonTextarea, IonContent, onIonViewDidEnter } from '@ionic/vue';
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

type EditableLink = CreateLinkRequest & { slug?: string };
const links = ref<EditableLink[]>([]);
const isOwner = ref(false);

function addLink() {
  links.value.push({
    title: '',
    description: '',
    url: '',
    iconUrl: '',
  });
}

function removeLink(index: number) {
  links.value.splice(index, 1);
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
  if (!pageSlug.value || isCreateMode.value) return;
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
    const currentUser = authStore.getAuthenticatedUser;
    if (!currentUser) {
      isOwner.value = false;
      return;
    }

    if (!pageSlug.value) {
      const userPage = await getUserLinkPage(currentUser.slug);
      if (userPage) {
        await router.push(`/linkpages/${userPage.slug}`);
        return;
      }
      isOwner.value = false;
    } else {
      const page = await getLinkPage(pageSlug.value);
      isOwner.value = currentUser.slug === page.owner.slug;
    }
  } catch (error) {
    isOwner.value = false;
  }
}

onIonViewDidEnter(async () => {
  await checkIfUserHasLinkPage();
  await loadPage();
});

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
      <div v-if="isCreateMode">
        <h2>Link Page creation</h2>
        <IonInput v-model="pageData.title" placeholder="Link Page title" />
        <IonTextarea v-model="pageData.description" placeholder="Link Page description" />

        <div v-for="(link, idx) in links" :key="idx">
          <IonInput v-model="link.title" placeholder="Link title" />
          <IonInput v-model="link.description" placeholder="Link description" />
          <IonInput v-model="link.url" placeholder="Link URL" />
          <IonInput v-model="link.iconUrl" placeholder="Link icon URL" />
          <IonButton color="danger" @click="removeLink(idx)">Delete</IonButton>
        </div>

        <IonButton @click="addLink">Add a link</IonButton>
        <IonButton @click="submitCreate">Create the Link Page</IonButton>
      </div>

      <div v-else>
        <h2 v-if="isOwner">Editing page: {{ pageSlug }}</h2>
        <h2 v-else>Link Page: {{ pageSlug }}</h2>

        <IonInput v-model="pageData.title" placeholder="Link Page title" :readonly="!isOwner" />
        <IonTextarea v-model="pageData.description" placeholder="Link Page description" :readonly="!isOwner" />

        <div v-for="(link, idx) in links" :key="idx">
          <IonInput v-model="link.title" placeholder="Link title" :readonly="!isOwner" />
          <IonInput v-model="link.description" placeholder="Link description" :readonly="!isOwner" />
          <template v-if="isOwner">
            <IonInput v-model="link.url" placeholder="Link URL" />
          </template>
          <template v-else>
            <a
              :href="link.url.startsWith('http') ? link.url : 'https://' + link.url"
              target="_blank"
              rel="noopener noreferrer"
              style="color: var(--ion-color-primary); text-decoration: underline">
              {{ link.url }}
            </a>
          </template>
          <IonInput v-model="link.iconUrl" placeholder="Link icon URL" :readonly="!isOwner" />
          <IonButton v-if="isOwner" color="danger" @click="submitLinkDeletion(idx)">Delete</IonButton>
        </div>

        <div style="margin-bottom: 6rem">
          <IonButton v-if="isOwner" @click="addLink">Add a link</IonButton>
          <IonButton v-if="isOwner" @click="submitUpdate">Submit Link Page modification</IonButton>
          <IonButton v-if="isOwner" color="danger" @click="deletePage">Delete the Link Page</IonButton>
        </div>
      </div>
    </ion-content>
  </ApplicationViewport>
</template>
