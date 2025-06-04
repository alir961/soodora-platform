<script setup lang="ts">
import { ref, computed } from 'vue';
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
import { getAuthenticatedUser } from '@/utils';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const route = useRoute();
const router = useRouter();

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

    const currentUser = await retrieveAuthenticatedProfile();
    isOwner.value = currentUser?.slug === page.owner.slug;
  } catch (error) {
    alert('Unable to load the Link Page : ' + error);
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
      resetData();
      await router.push('/linkpages');
    } catch (error) {
      alert('Unable to delete the Link Page : ' + error);
    }
  }
}

async function submitLinkDeletion(linkSlug?: string) {
  if (!pageSlug.value || !linkSlug) return;
  if (confirm('Confirm the link deletion ?')) {
    try {
      await deleteLink(pageSlug.value, linkSlug);
      links.value = links.value.filter((l) => l.slug !== linkSlug);
    } catch (error) {
      alert('Unable to delete the link : ' + error);
    }
  }
}

async function checkIfUserHasLinkPage() {
  const user = await getAuthenticatedUser();

  try {
    const page = await getUserLinkPage(user.slug);
    if (page?.slug) {
      router.push(`/linkpages/${page.slug}`);
    }
  } catch (error) {
    resetData();
    alert(`You don't have Link Page yet`);
  }
}

onIonViewDidEnter(async () => {
  await checkIfUserHasLinkPage();
  await loadPage();
});
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
        <h2>Édition de la page : {{ pageSlug }}</h2>
        <IonInput v-model="pageData.title" placeholder="Link Page title" :readonly="!isOwner" />
        <IonTextarea v-model="pageData.description" placeholder="Link Page description" :readonly="!isOwner" />

        <div v-for="(link, idx) in links" :key="idx">
          <IonInput v-model="link.title" placeholder="Link title" :readonly="!isOwner" />
          <IonInput v-model="link.description" placeholder="Link description" :readonly="!isOwner" />
          <IonInput v-model="link.url" placeholder="Link URL" :readonly="!isOwner" />
          <IonInput v-model="link.iconUrl" placeholder="Link icon URL" :readonly="!isOwner" />
          <IonButton v-if="isOwner" color="danger" @click="submitLinkDeletion(link.slug)">Delete</IonButton>
        </div>

        <IonButton v-if="isOwner" @click="addLink">Add a link</IonButton>
        <IonButton v-if="isOwner" @click="submitUpdate">Submit Link Page modification</IonButton>
        <IonButton v-if="isOwner" color="danger" @click="deletePage">Delete the Link Page</IonButton>
      </div>
    </ion-content>
  </ApplicationViewport>
</template>
