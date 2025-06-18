<script setup lang="ts">
import { ref, watch } from 'vue';
import {
  IonContent,
  IonSearchbar,
  IonList,
  IonItem,
  IonLabel,
  IonAvatar,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
} from '@ionic/vue';
import { useRouter } from 'vue-router';

import ApplicationViewport from '@/view/ApplicationViewport.vue';
import type { User } from '@/entity/User';
import { searchProfile } from '@/client/profileClient';

const searchTerm = ref('');
const artists = ref<User[]>([]);
const users = ref<User[]>([]);
const searchTimeout = ref<ReturnType<typeof setTimeout> | null>(null);
const router = useRouter();

const currentPage = ref(0);
const hasMore = ref(true);
const isLoading = ref(false);

const fetchProfiles = async (reset = false) => {
  if (!searchTerm.value.trim()) {
    artists.value = [];
    users.value = [];
    currentPage.value = 0;
    hasMore.value = true;
    return;
  }

  if (isLoading.value || !hasMore.value) return;

  isLoading.value = true;

  try {
    if (reset) {
      currentPage.value = 0;
      hasMore.value = true;
      artists.value = [];
      users.value = [];
    }

    const response = await searchProfile(searchTerm.value, false, currentPage.value, 10, 'username');
    const allUsers = response.content;

    artists.value.push(...allUsers.filter((u) => u.roles.includes('ARTIST')));
    users.value.push(...allUsers.filter((u) => !u.roles.includes('ARTIST')));

    hasMore.value = allUsers.length === 10;
    if (hasMore.value) currentPage.value++;
  } catch (error) {
    console.error('Search failed:', error);
    hasMore.value = false;
  } finally {
    isLoading.value = false;
  }
};

watch(searchTerm, () => {
  if (searchTimeout.value) clearTimeout(searchTimeout.value);

  searchTimeout.value = setTimeout(() => {
    fetchProfiles(true);
  }, 500);
});

const loadMore = async (ev: CustomEvent) => {
  await fetchProfiles();
  await (ev.target as HTMLIonInfiniteScrollElement).complete();
};

const redirectToUserProfile = (user: User) => {
  router.push(`/user/${user.slug}`);
};
</script>

<template>
  <ApplicationViewport title="Search">
    <ion-content class="ion-padding">
      <ion-searchbar placeholder="Search for artists or users" v-model="searchTerm" :debounce="0" />

      <div v-if="searchTerm.length > 0">
        <h2 class="section-title">Artists</h2>
        <ion-list v-if="artists.length > 0">
          <ion-item v-for="artist in artists" :key="artist.slug" button @click="redirectToUserProfile(artist)">
            <ion-avatar slot="start">
              <img :src="artist.avatarUrl || '/assets/default-avatar.png'" alt="avatar" />
            </ion-avatar>
            <ion-label>@{{ artist.username }}</ion-label>
          </ion-item>
        </ion-list>
        <ion-list v-else>
          <ion-item>
            <ion-label>No matching artists yet</ion-label>
          </ion-item>
        </ion-list>

        <h2 class="section-title">Users</h2>
        <ion-list v-if="users.length > 0">
          <ion-item v-for="user in users" :key="user.slug" button @click="redirectToUserProfile(user)">
            <ion-avatar slot="start">
              <img :src="user.avatarUrl || '/assets/default-avatar.png'" alt="avatar" />
            </ion-avatar>
            <ion-label>@{{ user.username }}</ion-label>
          </ion-item>
        </ion-list>
        <ion-list v-else>
          <ion-item>
            <ion-label>No matching users yet</ion-label>
          </ion-item>
        </ion-list>

        <ion-infinite-scroll @ionInfinite="loadMore" threshold="100px" :disabled="!hasMore">
          <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
        </ion-infinite-scroll>
      </div>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-top: 20px;
  margin-bottom: 10px;
}

ion-avatar img {
  object-fit: cover;
  width: 100%;
  height: 100%;
  border-radius: 50%;
}
</style>
