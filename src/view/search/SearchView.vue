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
  IonButton,
} from '@ionic/vue';
import { useRouter } from 'vue-router';

import ApplicationViewport from '@/view/ApplicationViewport.vue';
import type { User } from '@/entity/User';
import type { Post } from '@/entity/Post';
import { callSearchPosts } from '@/client/postClient';
import { PostSearchParams } from '@/entity/PostSearch';
import PostCard from '@/component/explore/PostCard.vue';
import { chatboxEllipsesOutline, personCircleOutline } from 'ionicons/icons';
import { callSearchUsers } from '@/client/userClient';

type SearchMode = 'user' | 'post';

const PAGE_SIZE = 10;

const searchInput = ref('');
const searchMode = ref<SearchMode>('user');

const artists = ref<User[]>([]);
const users = ref<User[]>([]);
const posts = ref<Post[]>([]);

const searchTimeout = ref<ReturnType<typeof setTimeout> | null>(null);
const router = useRouter();

const currentPageUsers = ref(0);
const hasMoreUsers = ref(true);
const isLoadingUsers = ref(false);

const currentPagePosts = ref(0);
const hasMorePosts = ref(true);
const isLoadingPosts = ref(false);

const filterHasAudio = ref<boolean | null>(null);

const fetchUsers = async (reset = false) => {
  if (!searchInput.value.trim()) {
    artists.value = [];
    users.value = [];
    currentPageUsers.value = 0;
    hasMoreUsers.value = true;
    return;
  }

  if (isLoadingUsers.value || (!hasMoreUsers.value && !reset)) return;

  if (reset) {
    artists.value = [];
    users.value = [];
    currentPageUsers.value = 0;
    hasMoreUsers.value = true;
  }

  isLoadingUsers.value = true;

  try {
    const { content } = await callSearchUsers(searchInput.value, false, currentPageUsers.value, PAGE_SIZE, 'username');
    const isArtist = (user: User) => {
      return user.roles.includes('ARTIST') || user.roles.includes('PREMIUM');
    };

    artists.value.push(...content.filter(isArtist));
    users.value.push(...content.filter((user) => !isArtist(user)));

    hasMoreUsers.value = content.length === PAGE_SIZE;
    if (hasMoreUsers.value) currentPageUsers.value++;
  } catch (error) {
    console.error('Search profiles failed', error);
    hasMoreUsers.value = false;
  } finally {
    isLoadingUsers.value = false;
  }
};

const fetchPosts = async (reset = false) => {
  if (!searchInput.value.trim()) {
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
      title: searchInput.value.trim(),
      description: searchInput.value.trim(),
      ...(filterHasAudio.value !== null ? { hasAudio: filterHasAudio.value } : {}),
    };

    const response = await callSearchPosts(
      params.title ?? null,
      params.description ?? null,
      params.hasAudio ?? undefined,
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
  if (searchMode.value === 'user') {
    await fetchUsers(reset);
  } else {
    await fetchPosts(reset);
  }
};

watch(searchInput, () => {
  if (searchTimeout.value) clearTimeout(searchTimeout.value);

  searchTimeout.value = setTimeout(() => {
    fetchResults(true);
  }, 300);
});

watch(filterHasAudio, () => {
  if (searchInput.value.trim()) fetchPosts(true);
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
          :class="{ active: searchMode === 'user' }"
          @click="searchMode = 'user'" />
        <ion-icon
          :icon="chatboxEllipsesOutline"
          class="mode-icon"
          :class="{ active: searchMode === 'post' }"
          @click="searchMode = 'post'" />
      </div>
      <ion-searchbar placeholder="Search" v-model="searchInput" :debounce="0" />

      <div v-if="searchMode === 'post'" class="audio-filter">
        <ion-button :class="{ active: filterHasAudio === null }" @click="filterHasAudio = null">All</ion-button>
        <ion-button :class="{ active: filterHasAudio === true }" @click="filterHasAudio = true">With Audio</ion-button>
      </div>

      <div v-if="searchInput.length > 0">
        <template v-if="searchMode === 'user'">
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

        <template v-else-if="searchMode === 'post'">
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
          :disabled="searchMode === 'user' ? !hasMoreUsers : !hasMorePosts">
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

.audio-filter {
  margin: 1rem 0;
  display: flex;
  gap: 1rem;
}

.audio-filter ion-button {
  --background: var(--ion-color-light);
  --color: var(--ion-color-medium);
  cursor: pointer;
  border-radius: 0.25rem;
  transition:
    background-color 0.3s,
    color 0.3s;
}

.audio-filter ion-button.active {
  --background: var(--ion-color-primary);
  --color: white;
  font-weight: 600;
}
</style>
