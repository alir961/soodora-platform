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
  IonRow,
  IonIcon,
} from '@ionic/vue';
import { useRouter } from 'vue-router';

import ApplicationViewport from '@/view/ApplicationViewport.vue';
import type { User } from '@/entity/User';
import type { Post } from '@/entity/Post';
import { searchProfile } from '@/client/profileClient';
import { searchPosts } from '@/client/postClient';
import { PostSearchParams } from '@/entity/PostSearch';
import PostCard from '@/component/explore/PostCard.vue';
import { chatboxEllipsesOutline, personCircleOutline } from 'ionicons/icons';

const searchTerm = ref('');
const searchMode = ref<'profiles' | 'posts'>('profiles');

const artists = ref<User[]>([]);
const users = ref<User[]>([]);
const posts = ref<Post[]>([]);

const searchTimeout = ref<ReturnType<typeof setTimeout> | null>(null);
const router = useRouter();

const currentPageProfiles = ref(0);
const hasMoreProfiles = ref(true);
const isLoadingProfiles = ref(false);

const currentPagePosts = ref(0);
const hasMorePosts = ref(true);
const isLoadingPosts = ref(false);
const PAGE_SIZE = 10;

const fetchProfiles = async (reset = false) => {
  if (!searchTerm.value.trim()) {
    artists.value = [];
    users.value = [];
    currentPageProfiles.value = 0;
    hasMoreProfiles.value = true;
    return;
  }

  if (isLoadingProfiles.value || (!hasMoreProfiles.value && !reset)) return;

  if (reset) {
    artists.value = [];
    users.value = [];
    currentPageProfiles.value = 0;
    hasMoreProfiles.value = true;
  }

  isLoadingProfiles.value = true;

  try {
    const response = await searchProfile(searchTerm.value, false, currentPageProfiles.value, PAGE_SIZE, 'username');

    const allUsers = response.content;

    artists.value.push(...allUsers.filter((u) => u.roles.includes('ARTIST')));
    users.value.push(...allUsers.filter((u) => !u.roles.includes('ARTIST')));

    hasMoreProfiles.value = allUsers.length === PAGE_SIZE;
    if (hasMoreProfiles.value) currentPageProfiles.value++;
  } catch (error) {
    console.error('Search profiles failed:', error);
    hasMoreProfiles.value = false;
  } finally {
    isLoadingProfiles.value = false;
  }
};

const fetchPosts = async (reset = false) => {
  if (!searchTerm.value.trim()) {
    posts.value = [];
    currentPagePosts.value = 0;
    hasMorePosts.value = true;
    return;
  }

  if (isLoadingPosts.value || (!hasMorePosts.value && !reset)) return;

  if (reset) {
    posts.value = [];
    currentPagePosts.value = 0;
    hasMorePosts.value = true;
  }

  isLoadingPosts.value = true;

  try {
    const params: PostSearchParams = {
      page: currentPagePosts.value,
      size: PAGE_SIZE,
      sort: 'createdAt,desc',
      title: searchTerm.value.trim(),
      description: searchTerm.value.trim(),
    };

    const response = await searchPosts(
      params.title ?? null,
      params.description ?? null,
      params.page,
      params.size,
      params.sort
    );

    const fetchedPosts = response.content.map((post) => ({
      ...post,
      createdAt: new Date(post.createdAt),
    }));
    posts.value.push(...fetchedPosts);

    hasMorePosts.value = fetchedPosts.length === PAGE_SIZE;
    if (hasMorePosts.value) currentPagePosts.value++;
  } catch (error) {
    console.error('Search posts failed:', error);
    hasMorePosts.value = false;
  } finally {
    isLoadingPosts.value = false;
  }
};

const fetchResults = async (reset = false) => {
  if (searchMode.value === 'profiles') {
    await fetchProfiles(reset);
  } else {
    await fetchPosts(reset);
  }
};

watch(searchTerm, () => {
  if (searchTimeout.value) clearTimeout(searchTimeout.value);

  searchTimeout.value = setTimeout(() => {
    fetchResults(true);
  }, 300);
});

const loadMore = async (ev: CustomEvent) => {
  await fetchResults();
  await (ev.target as HTMLIonInfiniteScrollElement).complete();
};

const redirectToUserProfile = (user: User) => {
  router.push(`/user/${user.slug}`);
};
</script>

<template>
  <ApplicationViewport title="Search">
    <ion-content class="ion-padding">
      <div class="search-toggle">
        <ion-icon
          :icon="personCircleOutline"
          class="mode-icon"
          :class="{ active: searchMode === 'profiles' }"
          @click="searchMode = 'profiles'" />
        <ion-icon
          :icon="chatboxEllipsesOutline"
          class="mode-icon"
          :class="{ active: searchMode === 'posts' }"
          @click="searchMode = 'posts'" />
      </div>
      <ion-searchbar placeholder="Search" v-model="searchTerm" :debounce="0" />

      <div v-if="searchTerm.length > 0">
        <template v-if="searchMode === 'profiles'">
          <h2 class="section-title">Artists</h2>
          <ion-list v-if="artists.length > 0">
            <ion-item v-for="artist in artists" :key="artist.slug" button @click="redirectToUserProfile(artist)">
              <ion-avatar slot="start">
                <img :src="artist.avatarUrl || '/assets/images/svg/avatar.svg'" alt="avatar" />
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
                <img :src="user.avatarUrl || '/assets/images/svg/avatar.svg'" alt="avatar" />
              </ion-avatar>
              <ion-label>@{{ user.username }}</ion-label>
            </ion-item>
          </ion-list>
          <ion-list v-else>
            <ion-item>
              <ion-label>No matching users yet</ion-label>
            </ion-item>
          </ion-list>
        </template>

        <template v-else-if="searchMode === 'posts'">
          <h2 class="section-title">Posts</h2>
          <ion-list v-if="posts.length > 0">
            <ion-row>
              <PostCard
                v-for="post in posts"
                :key="post.slug"
                :post="post"
                @post-deleted="(slug) => (posts = posts.filter((p) => p.slug !== slug))"
                @post-like="post.likeCount++"
                @post-unlike="post.likeCount--"
                @post-repost="post.repostCount++"
                @post-unrepost="post.repostCount--" />
            </ion-row>
          </ion-list>
          <ion-list v-else>
            <ion-item>
              <ion-label>No matching posts yet</ion-label>
            </ion-item>
          </ion-list>
        </template>

        <ion-infinite-scroll
          @ionInfinite="loadMore"
          threshold="100px"
          :disabled="searchMode === 'profiles' ? !hasMoreProfiles : !hasMorePosts">
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

.search-toggle {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
  border-bottom: 0.125rem solid #ccc;
  padding-bottom: 0.75rem;
}

.mode-icon {
  font-size: 2.25rem;
  cursor: pointer;
  color: var(--ion-color-medium);
  padding: 0.5rem;
  border-radius: 0.625rem;
  transition: all 0.2s ease;
}

.mode-icon:hover {
  background-color: var(--ion-color-light);
}

.mode-icon.active {
  background-color: var(--ion-color-primary);
  color: white;
  transform: scale(1.1);
}
</style>
