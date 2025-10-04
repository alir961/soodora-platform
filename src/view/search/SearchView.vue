<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import {
  IonContent,
  IonList,
  IonLabel,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
  IonRow,
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
import { Playlist } from '@/entity/Playlist';
import { searchPlaylists } from '@/client/playlistClient';
import { PlaylistSearchParams } from '@/entity/PlaylistSearch';
import PlaylistItem from '@/component/playlist/PlaylistItem.vue';

type SearchMode = '' | 'user' | 'artist' | 'post' | 'music' | 'playlist';

const PAGE_SIZE = 10;

const searchInput = ref('');
const searchMode = ref<SearchMode>('');

const artists = ref<User[]>([]);
const users = ref<User[]>([]);
const posts = ref<Post[]>([]);
const playlists = ref<Playlist[]>([]);

const searchTimeout = ref<ReturnType<typeof setTimeout> | null>(null);

const currentPageUsers = ref(0);
const hasMoreUsers = ref(true);
const isLoadingUsers = ref(false);

const currentPagePosts = ref(0);
const hasMorePosts = ref(true);
const isLoadingPosts = ref(false);

const currentPagePlaylists = ref(0);
const hasMorePlaylists = ref(true);
const isLoadingPlaylists = ref(false);

const showFilterModal = ref(false);

const toggleFilters = () => {
  showFilterModal.value = !showFilterModal.value;
};

const applyFilter = (mode: SearchMode) => {
  searchMode.value = mode;
};

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
      artists.value.push(...content.filter(isArtist));
      users.value.push(...content.filter((user) => !isArtist(user)));
    } else {
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

const fetchPlaylists = async (reset = false) => {
  if (!searchInput.value.trim()) {
    playlists.value = [];
    currentPagePlaylists.value = 0;
    hasMorePlaylists.value = true;
    return;
  }

  if (isLoadingPlaylists.value || (!hasMorePlaylists.value && !reset)) return;

  if (reset) {
    playlists.value = [];
    currentPagePlaylists.value = 0;
    hasMorePlaylists.value = true;
  }

  isLoadingPlaylists.value = true;

  try {
    const params: PlaylistSearchParams = {
      page: currentPagePlaylists.value,
      size: PAGE_SIZE,
      sort: 'createdAt,desc',
      name: searchInput.value.trim(),
    };

    const response = await searchPlaylists(undefined, params.name, undefined, params.page, params.size, params.sort);

    playlists.value.push(...response.content);

    hasMorePlaylists.value = response.content.length === PAGE_SIZE;
    if (hasMorePlaylists.value) currentPagePlaylists.value++;
  } catch (error) {
    console.error('Search playlists failed:', error);
    hasMorePlaylists.value = false;
  } finally {
    isLoadingPlaylists.value = false;
  }
};

const fetchResults = async (reset = false) => {
  if (searchMode.value === '' || searchMode.value === null) {
    await Promise.all([fetchUsers(reset), fetchPosts(reset), fetchPlaylists(reset)]);
  } else if (searchMode.value === 'user' || searchMode.value === 'artist') {
    await fetchUsers(reset);
  } else if (searchMode.value === 'post' || searchMode.value === 'music') {
    await fetchPosts(reset);
  } else if (searchMode.value === 'playlist') {
    await fetchPlaylists(reset);
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
    case 'playlist':
      return 'Search for playlist...';
    default:
      return 'Search for anything...';
  }
});
</script>

<template>
  <ApplicationViewport title="Search">
    <ion-content>
      <SearchBarWithFilters
        v-model="searchInput"
        :placeholder="searchPlaceholder"
        :isFilterActive="isFilterActive"
        :showFilters="showFilterModal"
        @filterClick="toggleFilters" />

      <transition name="slide-down">
        <div v-if="showFilterModal" class="filter-panel-wrapper">
          <div class="filter-panel">
            <div class="filter-header">
              <h3>Search Filters</h3>
              <button class="clear-btn" @click="applyFilter('')">Clear All</button>
            </div>
            <div class="filter-options">
              <button 
                :class="['filter-chip', { active: searchMode === '' }]"
                @click="applyFilter('')">
                All
              </button>
              <button 
                :class="['filter-chip', { active: searchMode === 'artist' }]"
                @click="applyFilter('artist')">
                Artists
              </button>
              <button 
                :class="['filter-chip', { active: searchMode === 'user' }]"
                @click="applyFilter('user')">
                Users
              </button>
              <button 
                :class="['filter-chip', { active: searchMode === 'post' }]"
                @click="applyFilter('post')">
                Posts
              </button>
              <button 
                :class="['filter-chip', { active: searchMode === 'music' }]"
                @click="applyFilter('music')">
                Music
              </button>
              <button 
                :class="['filter-chip', { active: searchMode === 'playlist' }]"
                @click="applyFilter('playlist')">
                Playlists
              </button>
            </div>
          </div>
        </div>
      </transition>

      <div v-if="searchInput.trim().length === 0" class="search-placeholder-message">
        <div class="glow-background"></div>
        <div class="search-info-glow-wrapper">
          <ion-label class="search-info-label-glow">
            Begin your search adventure!<br />
            Find users, artists, posts, <br />
            playlists or tunes here.
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
            :isLast="index === artists.length - 1"
            :user="artist" />
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
            :isLast="index === users.length - 1"
            :user="user" />
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
            <div
              v-for="(post, index) in posts"
              :key="post.slug"
              :class="['post-wrapper', index === posts.length - 1 ? 'last' : '']">
              <div class="post-separator" v-if="index !== posts.length - 1"></div>
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

      <template v-if="(searchMode === '' || searchMode === 'playlist') && searchInput.trim().length > 0">
        <h2 class="section-title">Playlists</h2>
        <ion-list v-if="playlists.length > 0" class="name-list">
          <PlaylistItem v-for="pl in playlists" :key="pl.slug" :playlist="pl" />
        </ion-list>
        <ion-list v-else>
          <ion-label class="no-results-label">No matching playlists found</ion-label>
        </ion-list>
      </template>

      <ion-infinite-scroll
        @ionInfinite="loadMore"
        threshold="100px"
        :disabled="
          searchMode === 'user'
            ? !hasMoreUsers
            : searchMode === 'post' || searchMode === 'music'
              ? !hasMorePosts
              : searchMode === 'playlist'
                ? !hasMorePlaylists
                : !hasMoreUsers && !hasMorePosts && !hasMorePlaylists
        ">
        <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
      </ion-infinite-scroll>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
.filter-panel-wrapper {
  width: 90%;
  margin: 0 auto;
}

.filter-panel {
  background: linear-gradient(
    to bottom,
    rgba(18, 22, 30, 0.75),
    rgba(18, 22, 30, 0.65)
  );
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: 1.25rem 1.5rem;
  border-radius: 16px;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  box-shadow: 
    0 8px 24px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.04);
  position: relative;
}

.filter-panel::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  border-radius: 16px 16px 0 0;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.1),
    transparent
  );
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.filter-header h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.95);
  margin: 0;
  letter-spacing: -0.01em;
}

.clear-btn {
  appearance: none;
  border: 0;
  background: transparent;
  color: #60a5fa;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.clear-btn:active {
  background: rgba(96, 165, 250, 0.12);
  transform: scale(0.96);
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-chip {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.95rem;
  font-weight: 600;
  padding: 10px 18px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.filter-chip:active {
  transform: scale(0.96);
}

.filter-chip.active {
  background: linear-gradient(135deg, rgba(29, 78, 216, 0.9), rgba(37, 99, 235, 0.9));
  border-color: rgba(96, 165, 250, 0.3);
  color: #ffffff;
  box-shadow: 
    0 4px 12px rgba(37, 99, 235, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.slide-down-enter-active {
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.slide-down-leave-active {
  transition: all 0.25s cubic-bezier(0.8, 0.2, 0.8, 0.2);
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-12px) scale(0.97);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.98);
}

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
  z-index: 0; /* Behind everything */
  pointer-events: none; /* Don't block clicks */
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
</style>
