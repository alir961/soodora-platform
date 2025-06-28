<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import {
  IonContent,
  IonList,
  IonLabel,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
  IonRow,
  IonButton,
  IonSelect,
  IonSelectOption,
  IonModal,
} from '@ionic/vue';

import ApplicationViewport from '@/view/ApplicationViewport.vue';
import type { User } from '@/entity/User';
import type { Post } from '@/entity/Post';
import { callSearchPosts } from '@/client/postClient';
import { PostSearchParams } from '@/entity/PostSearch';
import PostCard from '@/component/explore/PostCard.vue';
import { callSearchUsers } from '@/client/userClient';
import SearchBarWithFilters from '@/component/search/SearchBarWithFilters.vue';
import UserListItem from '@/component/user/UserListItem.vue';

type SearchMode = '' | 'user' | 'artist' | 'post' | 'music';

const PAGE_SIZE = 10;

const searchInput = ref('');
const searchMode = ref<SearchMode>('');

const artists = ref<User[]>([]);
const users = ref<User[]>([]);
const posts = ref<Post[]>([]);

const searchTimeout = ref<ReturnType<typeof setTimeout> | null>(null);

const currentPageUsers = ref(0);
const hasMoreUsers = ref(true);
const isLoadingUsers = ref(false);

const currentPagePosts = ref(0);
const hasMorePosts = ref(true);
const isLoadingPosts = ref(false);

const showFilterModal = ref(false);

const isFilterActive = computed(() => searchMode.value !== '');

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

    if (searchMode.value === 'artist') {
      artists.value.push(...content.filter(isArtist));
    } else if (searchMode.value === '') {
      // Default mode: separate users and artists
      artists.value.push(...content.filter(isArtist));
      users.value.push(...content.filter((user) => !isArtist(user)));
    } else {
      // 'user' mode: only non-artists
      users.value.push(...content.filter((user) => !isArtist(user)));
      artists.value = [];
    }

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
      ...(searchMode.value === 'music' ? { hasAudio: true } : {}),
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
  if (searchMode.value === '' || searchMode.value === null) {
    await Promise.all([fetchUsers(reset), fetchPosts(reset)]);
  } else if (searchMode.value === 'user' || searchMode.value === 'artist') {
    await fetchUsers(reset);
  } else if (searchMode.value === 'post' || searchMode.value === 'music') {
    await fetchPosts(reset);
  }
};

watch(searchInput, () => {
  if (searchTimeout.value) clearTimeout(searchTimeout.value);

  searchTimeout.value = setTimeout(() => {
    fetchResults(true);
  }, 300);
});

watch(searchMode, () => {
  fetchResults(true);
});

const loadMore = async (ev: CustomEvent) => {
  await fetchResults();
  await (ev.target as HTMLIonInfiniteScrollElement).complete();
};

const searchPlaceholder = computed(() => {
  switch (searchMode.value) {
    case 'artist':
      return 'Search for artists...';
    case 'user':
      return 'Search for users...';
    case 'post':
      return 'Search for posts...';
    case 'music':
      return 'Search for music...';
    default:
      return 'Search for anything...';
  }
});
</script>

<template>
  <ApplicationViewport title="Search">
    <ion-content>
      <ion-modal
        :is-open="showFilterModal"
        @didDismiss="showFilterModal = false"
        :breakpoints="[0, 0.3, 0.5]"
        initial-breakpoint="0.3"
        handle-behavior="cycle">
        <ion-content class="ion-padding">
          <div class="filter-modal">
            <h2>Search Filters</h2>
            <ion-select v-model="searchMode" class="filter-select">
              <div slot="label">Search Type:</div>
              <ion-select-option value="">All</ion-select-option>
              <ion-select-option value="artist">Artists</ion-select-option>
              <ion-select-option value="user">Users</ion-select-option>
              <ion-select-option value="post">Posts</ion-select-option>
              <ion-select-option value="music">Music</ion-select-option>
            </ion-select>
            <ion-button expand="block" @click="showFilterModal = false">Apply</ion-button>
          </div>
        </ion-content>
      </ion-modal>
      <SearchBarWithFilters
        v-model="searchInput"
        :placeholder="searchPlaceholder"
        :isFilterActive="isFilterActive"
        @filterClick="showFilterModal = true" />

      <div v-if="searchInput.trim().length === 0" class="search-placeholder-message">
        <div class="glow-background"></div>
        <div class="search-info-glow-wrapper">
          <ion-label class="search-info-label-glow">
            Begin your search adventure!<br />
            Find users, artists, posts, <br />
            or tunes here.
          </ion-label>
          <div class="search-info-icon">🔍</div>
        </div>
      </div>

      <template v-if="(searchMode === '' || searchMode === 'artist') && searchInput.trim().length > 0">
        <h2 class="section-title">Artists</h2>
        <ion-list v-if="artists.length" class="name-list">
          <UserListItem
            v-for="(artist, index) in artists"
            :key="artist.slug"
            :user="artist"
            :isLast="index === artists.length - 1" />
        </ion-list>
        <ion-list v-else>
          <ion-label class="no-results-label">No matching artists found</ion-label>
        </ion-list>
      </template>

      <template v-if="(searchMode === '' || searchMode === 'user') && searchInput.trim().length > 0">
        <h2 class="section-title">Users</h2>
        <ion-list v-if="users.length > 0" class="name-list">
          <UserListItem
            v-for="(user, index) in users"
            :key="user.slug"
            :user="user"
            :isLast="index === users.length - 1" />
        </ion-list>
        <ion-list v-else>
          <ion-label class="no-results-label">No matching users found</ion-label>
        </ion-list>
      </template>

      <template
        v-if="(searchMode === '' || searchMode === 'post' || searchMode === 'music') && searchInput.trim().length > 0">
        <h2 class="section-title">{{ searchMode === 'music' ? 'Music' : 'Posts' }}</h2>
        <ion-list v-if="posts.length > 0">
          <ion-row>
            <div v-for="post in posts" :key="post.slug" class="post-wrapper">
              <div class="post-separator"></div>
              <PostCard
                :post="post"
                @post-deleted="(slug) => (posts = posts.filter((p) => p.slug !== slug))"
                @post-like="post.likeCount++"
                @post-unlike="post.likeCount--"
                @post-repost="post.repostCount++"
                @post-unrepost="post.repostCount--"
                class="post-card" />
            </div>
          </ion-row>
        </ion-list>
        <ion-list v-else>
          <ion-label class="no-results-label">No matching posts found</ion-label>
        </ion-list>
      </template>
      <ion-infinite-scroll
        @ionInfinite="loadMore"
        threshold="100px"
        :disabled="searchMode === 'user' ? !hasMoreUsers : !hasMorePosts">
        <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
      </ion-infinite-scroll>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
.search-placeholder-message {
  position: fixed;
  top: 40%;
  left: 0;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 30vh;
  text-align: center;
  padding: 0 1.5rem;
  animation: float 4s ease-in-out infinite;
}

.search-info-label-glow {
  color: var(--ion-color-medium);
  font-size: 1.4rem;
  line-height: 1.5;
  color: #ffffff;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.glow-background {
  position: absolute;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(0, 136, 255, 0.6) 0%, rgba(0, 51, 204, 0.4) 40%, rgba(0, 0, 0, 0) 100%);
  filter: blur(100px);
  opacity: 0.6;
  z-index: 0;
  animation: pulse 6s ease-in-out infinite alternate;
}

.search-info-icon {
  font-size: 2.2rem;
  margin-top: 0.5rem;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  padding-top: 1em;
  padding-left: 1.5em;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
}

.name-list {
  padding-left: 1em;
  padding-right: 1em;
}

.post-wrapper {
  margin-left: 0.5em;
  margin-right: 0.5em;
  width: calc(100% - 1em);
}

.post-separator {
  height: 1px;
  background-color: rgba(255, 255, 255, 0.15);
  width: calc(100% - 2em);
  margin: 0 auto;
}

.post-card {
  padding: 0;
  margin: 0;
}

.no-results-label {
  padding-left: 1.8em;
  color: var(--ion-color-medium);
}

.filter-modal {
  display: flex;
  flex-direction: column;
  padding: 1.5rem;
  border-radius: 12px;
}

ion-button {
  --color: white;
}
</style>
