<script setup lang="ts">
import { IonButton, IonIcon } from '@ionic/vue';

import { createOutline, bagAddOutline, linkOutline, documentLock } from 'ionicons/icons';
import { ref, watch } from 'vue';
import { getUserLinkPage } from '@/client/linkPageClient';
import { useAuthStore } from '@/store/reactiveStorage';
import { useRouter } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { getMediaKitByArtistSlug } from '@/client/mediaKitClient';

const router = useRouter();
const authStore = useAuthStore();

const hasLinkPage = ref(false);

const checkUserLinkPage = async () => {
  const userSlug = authStore.getAuthenticatedUserSlug;

  if (userSlug) {
    try {
      const userPage = await getUserLinkPage(userSlug);
      hasLinkPage.value = !!userPage;
    } catch (error) {
      console.log('Unable to fetch artist link hub', error);
      hasLinkPage.value = false;
    }
  } else hasLinkPage.value = false;
};

watch(
  () => authStore.getAuthenticatedUser,
  (newUser) => {
    if (newUser) {
      checkUserLinkPage();
    }
  },
  { immediate: true }
);

const goToCreatePost = async () => {
  await router.push('/explore/post/create');
};

const goToCreateProduct = async () => {
  await router.push('/marketplace/product/create');
};

const goToLinkPages = async () => {
  const userSlug = authStore.getAuthenticatedUserSlug;

  if (!userSlug) return;
  try {
    const userPage = await getUserLinkPage(userSlug);

    if (userPage) {
      await router.push(`/linkpages/${userPage.slug}`);
      return;
    }
  } catch (error) {
    console.log('Unable to fetch artist link hub', error);
  }
  await router.push('/linkpages');
};

const goToSalesPage = async () => {
  await router.push('/marketplace/mysales');
};

const goToMediaKit = async () => {
  const userSlug = authStore.getAuthenticatedUserSlug;
  if (!userSlug) return;

  try {
    const userPage = await getMediaKitByArtistSlug(userSlug);
    if (userPage) {
      await router.push(`/mediakits/${userPage.slug}`);
      return;
    }
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : '';
    console.log('Error fetching Media Kit:', message);
  }

  await router.push('/mediakits/create');
};

const goToQuotes = async () => {
  return router.push(`/quotes/create`);
};

const goToMyQuotes = async () => {
  return router.push(`/quotes/me`);
};
</script>

<template>
  <ApplicationViewport title="Create" authenticated>
    <div class="create-container ion-padding">
      <h1 class="page-title">Create</h1>
      <p class="page-description">
        Create your world with Soodora.<br />
        Linkhubs, posts, products and exports.<br />
        All from here
      </p>

      <ion-button @click="goToCreatePost" expand="block" class="go-button" color="primary" fill="solid">
        Create a Post
        <ion-icon :icon="createOutline" slot="end"></ion-icon>
      </ion-button>

      <ion-button @click="goToCreateProduct" expand="block" class="go-button" color="primary" fill="solid">
        Create a Product
        <ion-icon :icon="bagAddOutline" slot="end"></ion-icon>
      </ion-button>

      <ion-button expand="block" class="go-button" color="secondary" fill="solid" @click="goToSalesPage">
        My Sales
      </ion-button>

      <ion-button @click="goToLinkPages" expand="block" class="go-button" color="primary" fill="solid">
        {{ hasLinkPage ? 'Edit your Link Hub' : 'Create a Link Hub' }}
        <ion-icon :icon="linkOutline" slot="end"></ion-icon>
      </ion-button>

      <ion-button @click="goToMediaKit" expand="block" class="go-button" color="primary" fill="solid">
        Create MediaKit
        <ion-icon :icon="createOutline" slot="end"></ion-icon>
      </ion-button>

      <ion-button @click="goToQuotes" expand="block" class="go-button" color="primary" fill="solid">
        Create Quote
        <ion-icon :icon="createOutline" slot="end"></ion-icon>
      </ion-button>

      <ion-button @click="goToMyQuotes" expand="block" class="go-button" color="secondary" fill="solid">
        My Quotes
        <ion-icon :icon="documentLock" slot="end"></ion-icon>
      </ion-button>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.create-container {
  display: flex;
  flex-direction: column;
  margin: 20px;
  padding: 10px 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 0;
}

.page-description {
  font-size: 19px;
  color: var(--ion-color-medium);
  margin-bottom: 20px;
}

.go-button {
  margin-top: 12px;
  border-radius: 12px;
}

ion-button.go-button::part(native) {
  color: white !important;
}
</style>
