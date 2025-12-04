<script setup lang="ts">
import { ref, watch, computed, onMounted, onUnmounted } from 'vue';
import { IonContent, IonList, IonInfiniteScroll, IonInfiniteScrollContent, IonIcon } from '@ionic/vue';
import {
  searchOutline,
  personOutline,
  micOutline,
  documentTextOutline,
  musicalNotesOutline,
  albumsOutline,
} from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import Header from '@/component/AppHeader.vue';
import type { User } from '@/entity/User';
import type { Post } from '@/entity/Post';
import { callSearchPosts } from '@/client/postClient';
import { PostSearchParams } from '@/entity/PostSearch';
import PostCard from '@/component/explore/PostCard.vue';
import TrackCard from '@/component/music/TrackCard.vue';
import { callSearchUsers } from '@/client/userClient';
import SearchBarWithFilters from '@/component/search/SearchBarWithFilters.vue';
import UserListItem from '@/component/user/UserListItem.vue';
import { Playlist } from '@/entity/Playlist';
import { searchPlaylists } from '@/client/playlistClient';
import { PlaylistSearchParams } from '@/entity/PlaylistSearch';
import PlaylistCard from '@/component/playlist/PlaylistCard.vue';
import { usePlayerStore } from '@/store/playerStore';
import { audioPlayerService } from '@/services/audioPlayerService';
import SaveTrackCard from '@/component/actions/SaveTrackCard.vue';

type SearchMode = '' | 'user' | 'artist' | 'post' | 'music' | 'playlist';

const PAGE_SIZE = 10;
const playerStore = usePlayerStore();

const searchInput = ref('');
const searchMode = ref<SearchMode>('');

const artists = ref<User[]>([]);
const users = ref<User[]>([]);
const postsText = ref<Post[]>([]);
const postsAudio = ref<Post[]>([]);
const playlists = ref<Playlist[]>([]);

const searchTimeout = ref<ReturnType<typeof setTimeout> | null>(null);
const hasSearchCompleted = ref(false);
const lastSearchQuery = ref('');
const isInitialState = ref(true);

const currentPageUsers = ref(0);
const hasMoreUsers = ref(true);
const isLoadingUsers = ref(false);

const currentPageText = ref(0);
const currentPageAudio = ref(0);

const hasMoreText = ref(true);
const hasMoreAudio = ref(true);

const isLoadingText = ref(false);
const isLoadingAudio = ref(false);

const currentPagePlaylists = ref(0);
const hasMorePlaylists = ref(true);
const isLoadingPlaylists = ref(false);

const showFilterModal = ref(false);
const saveTrackCardRef = ref<InstanceType<typeof SaveTrackCard> | null>(null);
const selectedTrackForSave = ref<Post | null>(null);

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
    isInitialState.value = true;
    return;
  }

  if (isLoadingUsers.value || (!hasMoreUsers.value && !reset)) return;

  if (reset) {
    currentPageUsers.value = 0;
    hasMoreUsers.value = true;
  }

  isLoadingUsers.value = true;

  try {
    const { content } = await callSearchUsers(searchInput.value, false, currentPageUsers.value, PAGE_SIZE, 'username');
    const isArtist = (user: User) => {
      return user.roles.includes('ARTIST') || user.roles.includes('PREMIUM');
    };

    if (reset) {
      if (searchMode.value === 'artist') {
        artists.value = content.filter(isArtist);
      } else if (searchMode.value === '') {
        artists.value = content.filter(isArtist);
        users.value = content.filter((user) => !isArtist(user));
      } else {
        users.value = content.filter((user) => !isArtist(user));
        artists.value = [];
      }
    } else {
      if (searchMode.value === 'artist') {
        artists.value.push(...content.filter(isArtist));
      } else if (searchMode.value === '') {
        artists.value.push(...content.filter(isArtist));
        users.value.push(...content.filter((user) => !isArtist(user)));
      } else {
        users.value.push(...content.filter((user) => !isArtist(user)));
      }
    }

    hasMoreUsers.value = content.length === PAGE_SIZE;
    if (hasMoreUsers.value) currentPageUsers.value++;
    isInitialState.value = false;
  } catch (error) {
    console.error('Search profiles failed', error);
    hasMoreUsers.value = false;
  } finally {
    isLoadingUsers.value = false;
  }
};

const fetchTextPosts = async (reset = false) => {
  if (!searchInput.value.trim()) {
    postsText.value = [];
    currentPageText.value = 0;
    hasMoreText.value = true;
    isInitialState.value = true;
    return;
  }

  if (isLoadingText.value || (!hasMoreText.value && !reset)) return;

  if (reset) {
    currentPageText.value = 0;
    hasMoreText.value = true;
  }

  isLoadingText.value = true;

  try {
    const params: PostSearchParams = {
      page: currentPageText.value,
      size: PAGE_SIZE,
      sort: 'createdAt,desc',
      title: searchInput.value.trim(),
      description: searchInput.value.trim(),
    };

    const response = await callSearchPosts(
      params.title ?? null,
      params.audioTitle ?? null,
      params.description ?? null,
      params.hasAudio ?? undefined,
      params.page,
      params.size,
      params.sort
    );

    const fetchedPosts = response.content.map((postsText) => ({
      ...postsText,
      createdAt: new Date(postsText.createdAt),
    }));

    if (reset) {
      postsText.value = fetchedPosts;
    } else {
      postsText.value.push(...fetchedPosts);
    }

    hasMoreText.value = fetchedPosts.length === PAGE_SIZE;
    if (hasMoreText.value) currentPageText.value++;
    isInitialState.value = false;
  } catch (error) {
    console.error('Search text posts failed:', error);
    hasMoreText.value = false;
  } finally {
    isLoadingText.value = false;
  }
};

const fetchAudioPosts = async (reset = false) => {
  if (!searchInput.value.trim()) {
    postsAudio.value = [];
    currentPageAudio.value = 0;
    hasMoreAudio.value = true;
    isInitialState.value = true;
    return;
  }

  if (isLoadingAudio.value || (!hasMoreAudio.value && !reset)) return;

  if (reset) {
    currentPageAudio.value = 0;
    hasMoreAudio.value = true;
  }

  isLoadingAudio.value = true;

  try {
    const params: PostSearchParams = {
      page: currentPageAudio.value,
      size: PAGE_SIZE,
      sort: 'createdAt,desc',
      audioTitle: searchInput.value.trim(),
      hasAudio: true,
    };

    const response = await callSearchPosts(
      params.title ?? null,
      params.audioTitle ?? null,
      params.description ?? null,
      params.hasAudio ?? undefined,
      params.page,
      params.size,
      params.sort
    );

    const fetchedPosts = response.content.map((postsAudio) => ({
      ...postsAudio,
      createdAt: new Date(postsAudio.createdAt),
    }));

    if (reset) {
      postsAudio.value = fetchedPosts;
    } else {
      postsAudio.value.push(...fetchedPosts);
    }

    hasMoreAudio.value = fetchedPosts.length === PAGE_SIZE;
    if (hasMoreAudio.value) currentPageAudio.value++;
    isInitialState.value = false;
  } catch (error) {
    console.error('Search audio posts failed:', error);
    hasMoreAudio.value = false;
  } finally {
    isLoadingAudio.value = false;
  }
};

const fetchPlaylists = async (reset = false) => {
  if (!searchInput.value.trim()) {
    playlists.value = [];
    currentPagePlaylists.value = 0;
    hasMorePlaylists.value = true;
    isInitialState.value = true;
    return;
  }

  if (isLoadingPlaylists.value || (!hasMorePlaylists.value && !reset)) return;

  if (reset) {
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

    if (reset) {
      playlists.value = response.content;
    } else {
      playlists.value.push(...response.content);
    }

    hasMorePlaylists.value = response.content.length === PAGE_SIZE;
    if (hasMorePlaylists.value) currentPagePlaylists.value++;
    isInitialState.value = false;
  } catch (error) {
    console.error('Search playlists failed:', error);
    hasMorePlaylists.value = false;
  } finally {
    isLoadingPlaylists.value = false;
  }
};

const fetchResults = async (reset = false) => {
  if (searchMode.value === '' || searchMode.value === null) {
    await Promise.all([fetchUsers(reset), fetchTextPosts(reset), fetchAudioPosts(reset), fetchPlaylists(reset)]);
  } else if (searchMode.value === 'user' || searchMode.value === 'artist') {
    await fetchUsers(reset);
  } else if (searchMode.value === 'post') {
    await fetchTextPosts(reset);
  } else if (searchMode.value === 'music') {
    await fetchAudioPosts(reset);
  } else if (searchMode.value === 'playlist') {
    await fetchPlaylists(reset);
  }
};

watch(searchInput, () => {
  if (hasAnyResults.value || isInitialState.value) {
    hasSearchCompleted.value = false;
  }

  if (searchTimeout.value) clearTimeout(searchTimeout.value);

  searchTimeout.value = setTimeout(async () => {
    try {
      await fetchResults(true);
      lastSearchQuery.value = searchInput.value.trim();
    } finally {
      hasSearchCompleted.value = true;
    }
  }, 300);
});

watch(searchMode, async () => {
  if (hasAnyResults.value) {
    hasSearchCompleted.value = false;
  }

  try {
    await fetchResults(true);
    lastSearchQuery.value = searchInput.value.trim();
  } finally {
    hasSearchCompleted.value = true;
  }
});

const loadMore = async (ev: CustomEvent) => {
  await fetchResults();
  await (ev.target as HTMLIonInfiniteScrollElement).complete();
};

// Function to play a track with full context
import { provide } from 'vue';

const playTrackWithContext = async (post: Post) => {
  if (!post.audioUrl) return;

  // Create context with all audio tracks from search results
  const context = {
    contextId: 'search:audio',
    tracks: postsAudio.value
      .filter((p) => p.audioUrl)
      .map((p) => ({
        slug: p.slug,
        title: p.title,
        audioTitle: p.audioTitle || p.title,
        artist: p.owner?.username || p.owner?.slug || 'Unknown',
        imageUrl: p.imageUrl || undefined,
        audioUrl: p.audioUrl!,
        duration: 0,
        owner: p.owner,
      })),
  };

  await audioPlayerService.playTrack(
    {
      slug: post.slug,
      title: post.title,
      audioTitle: post.audioTitle || post.title,
      artist: post.owner?.username || post.owner?.slug || 'Unknown',
      imageUrl: post.imageUrl || undefined,
      audioUrl: post.audioUrl,
      duration: 0,
      owner: post.owner,
    },
    context
  );
};

const openSaveTrack = (post: Post) => {
  selectedTrackForSave.value = post;
  saveTrackCardRef.value?.open();
};

// Provide the function to child components
provide('playTrackWithContext', playTrackWithContext);

onMounted(() => {
  // Initialize audio service
  audioPlayerService.init(playerStore);
});

onUnmounted(() => {
  // Cleanup if needed
});

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
      return 'Search for playlists...';
    default:
      return 'Search for anything...';
  }
});

const hasAnyResults = computed(() => {
  if (!searchInput.value.trim()) return true;

  if (searchMode.value === 'artist') return artists.value.length > 0;
  if (searchMode.value === 'user') return users.value.length > 0;
  if (searchMode.value === 'post') return postsText.value.length > 0;
  if (searchMode.value === 'music') return postsAudio.value.length > 0;
  if (searchMode.value === 'playlist') return playlists.value.length > 0;

  return (
    artists.value.length > 0 ||
    users.value.length > 0 ||
    postsText.value.length > 0 ||
    postsAudio.value.length > 0 ||
    playlists.value.length > 0
  );
});

const shouldShowNoResults = computed(() => {
  return (
    searchInput.value.trim().length > 0 && !hasAnyResults.value && hasSearchCompleted.value && !isInitialState.value
  );
});

const shouldShowPlaceholder = computed(() => {
  return searchInput.value.trim().length === 0 || isInitialState.value;
});

const noResultsMessage = computed(() => {
  const query = lastSearchQuery.value || searchInput.value.trim();

  if (searchMode.value === 'artist') return `No artists found for "${query}"`;
  if (searchMode.value === 'user') return `No users found for "${query}"`;
  if (searchMode.value === 'post') return `No posts found for "${query}"`;
  if (searchMode.value === 'music') return `No music found for "${query}"`;
  if (searchMode.value === 'playlist') return `No playlists found for "${query}"`;

  return `No results found for "${query}"`;
});
</script>

<template>
  <ApplicationViewport title="Search">
    <ion-content>
      <div v-if="shouldShowPlaceholder || shouldShowNoResults" class="ambient-glow">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
      </div>

      <Header title="Search" subtitle="Discover music, artists, and more" :icon="searchOutline" />

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
              <button :class="['filter-chip', { active: searchMode === '' }]" @click="applyFilter('')">All</button>
              <button :class="['filter-chip', { active: searchMode === 'artist' }]" @click="applyFilter('artist')">
                Artists
              </button>
              <button :class="['filter-chip', { active: searchMode === 'user' }]" @click="applyFilter('user')">
                Users
              </button>
              <button :class="['filter-chip', { active: searchMode === 'post' }]" @click="applyFilter('post')">
                Posts
              </button>
              <button :class="['filter-chip', { active: searchMode === 'music' }]" @click="applyFilter('music')">
                Music
              </button>
              <button :class="['filter-chip', { active: searchMode === 'playlist' }]" @click="applyFilter('playlist')">
                Playlists
              </button>
            </div>
          </div>
        </div>
      </transition>

      <div v-if="shouldShowPlaceholder" class="search-placeholder-message" :class="{ 'pushed-down': showFilterModal }">
        <div class="placeholder-cards">
          <div class="placeholder-card main-card">
            <div class="icon-wrapper">
              <div class="search-icon">🔍</div>
              <div class="icon-glow"></div>
            </div>
            <h2 class="placeholder-title">Start Your Search</h2>
            <p class="placeholder-text">
              Discover artists, users, posts,<br />
              playlists, and music all in one place
            </p>
          </div>

          <div class="placeholder-card info-card">
            <p class="info-text">
              💡 <strong>Pro tip:</strong> Use filters to narrow down your results and find exactly what you're looking
              for
            </p>
          </div>
        </div>
      </div>

      <div v-if="shouldShowNoResults" class="no-results-container" :class="{ 'pushed-down': showFilterModal }">
        <div class="no-results-card">
          <div class="no-results-icon">🔍</div>
          <h3 class="no-results-title">{{ noResultsMessage }}</h3>
          <p class="no-results-hint">Try adjusting your search or filters</p>
        </div>
      </div>

      <div v-if="hasAnyResults && searchInput.trim().length > 0" class="results-container">
        <div class="ambient-glow">
          <div class="glow-orb orb-1"></div>
          <div class="glow-orb orb-2"></div>
          <div class="glow-orb orb-3"></div>
        </div>

        <template v-if="searchMode === '' || searchMode === 'artist'">
          <div v-if="artists.length" class="results-section">
            <div class="section-header">
              <div class="section-icon-wrapper">
                <ion-icon :icon="micOutline" class="section-icon" />
              </div>
              <h2 class="section-title">Artists</h2>
              <div class="section-count">{{ artists.length }}</div>
            </div>
            <div class="section-content">
              <ion-list class="name-list">
                <UserListItem v-for="artist in artists" :key="artist.slug" :user="artist" />
              </ion-list>
            </div>
          </div>
        </template>

        <template v-if="searchMode === '' || searchMode === 'user'">
          <div v-if="users.length > 0" class="results-section">
            <div class="section-header">
              <div class="section-icon-wrapper">
                <ion-icon :icon="personOutline" class="section-icon" />
              </div>
              <h2 class="section-title">Users</h2>
              <div class="section-count">{{ users.length }}</div>
            </div>
            <div class="section-content">
              <ion-list class="name-list">
                <UserListItem v-for="user in users" :key="user.slug" :user="user" />
              </ion-list>
            </div>
          </div>
        </template>

        <template v-if="searchMode === '' || searchMode === 'post'">
          <div v-if="postsText.length > 0" class="results-section">
            <div class="section-header">
              <div class="section-icon-wrapper">
                <ion-icon :icon="documentTextOutline" class="section-icon" />
              </div>
              <h2 class="section-title">Posts</h2>
              <div class="section-count">{{ postsText.length }}</div>
            </div>
            <div class="section-content">
              <ion-list class="posts-list">
                <div v-for="(post, index) in postsText" :key="post.slug" class="post-wrapper">
                  <PostCard
                    :post="post"
                    @post-deleted="(slug) => (postsText = postsText.filter((p) => p.slug !== slug))"
                    @post-like="post.likeCount++"
                    @post-unlike="post.likeCount--"
                    @post-repost="post.repostCount++"
                    @post-unrepost="post.repostCount--"
                    class="post-card" />
                  <div class="post-separator" v-if="index !== postsText.length - 1"></div>
                </div>
              </ion-list>
            </div>
          </div>
        </template>

        <template v-if="searchMode === '' || searchMode === 'music'">
          <div v-if="postsAudio.length > 0" class="results-section">
            <div class="section-header">
              <div class="section-icon-wrapper">
                <ion-icon :icon="musicalNotesOutline" class="section-icon" />
              </div>
              <h2 class="section-title">Music</h2>
              <div class="section-count">{{ postsAudio.length }}</div>
            </div>
            <div class="section-content">
              <ion-list class="tracks-list">
                <div v-for="(post, index) in postsAudio" :key="post.slug" class="track-wrapper">
                  <TrackCard :post="post" @save-track="openSaveTrack(post)" />
                  <div class="track-separator" v-if="index !== postsAudio.length - 1"></div>
                </div>
              </ion-list>
            </div>
          </div>
        </template>

        <template v-if="searchMode === '' || searchMode === 'playlist'">
          <div v-if="playlists.length > 0" class="results-section">
            <div class="section-header">
              <div class="section-icon-wrapper">
                <ion-icon :icon="albumsOutline" class="section-icon" />
              </div>
              <h2 class="section-title">Playlists</h2>
              <div class="section-count">{{ playlists.length }}</div>
            </div>
            <div class="section-content">
              <ion-list class="playlists-list">
                <div v-for="(playlist, index) in playlists" :key="playlist.slug" class="playlist-wrapper">
                  <PlaylistCard :playlist="playlist" />
                  <div class="playlist-separator" v-if="index !== playlists.length - 1"></div>
                </div>
              </ion-list>
            </div>
          </div>
        </template>
      </div>

      <ion-infinite-scroll
        @ionInfinite="loadMore"
        threshold="100px"
        :disabled="
          searchMode === 'user'
            ? !hasMoreUsers
            : searchMode === 'post'
              ? !hasMoreText
              : searchMode === 'music'
                ? !hasMoreAudio
                : searchMode === 'playlist'
                  ? !hasMorePlaylists
                  : !hasMoreUsers && !hasMoreText && !hasMoreAudio && !hasMorePlaylists
        ">
        <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
      </ion-infinite-scroll>
    </ion-content>

    <!-- Save Track Modal -->
    <SaveTrackCard
      ref="saveTrackCardRef"
      :post-slug="selectedTrackForSave?.slug || ''"
      :audio-title="selectedTrackForSave?.audioTitle"
      :audio-url="selectedTrackForSave?.audioUrl"
      :image-url="selectedTrackForSave?.imageUrl" />
  </ApplicationViewport>
</template>

<style scoped>
.filter-panel-wrapper {
  width: 90%;
  margin: 0 auto 1rem auto;
  position: relative;
  z-index: 100;
}

.filter-panel {
  background: linear-gradient(to bottom, rgba(18, 22, 30, 0.75), rgba(18, 22, 30, 0.65));
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
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
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

.search-placeholder-message {
  width: 90%;
  max-width: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 4rem auto 2rem auto;
  pointer-events: none;
  animation: fadeIn 0.5s ease;
}

.search-placeholder-message.pushed-down {
  margin-top: 1rem;
}

.placeholder-cards {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.placeholder-card {
  background: linear-gradient(to bottom, rgba(18, 22, 30, 0.75), rgba(18, 22, 30, 0.65));
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  text-align: center;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.04);
  position: relative;
  overflow: hidden;
}

.placeholder-card.main-card {
  padding: 3rem 2rem;
}

.placeholder-card.info-card {
  padding: 1.25rem 1.5rem;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(18, 22, 30, 0.68), rgba(13, 27, 42, 0.72));
}

.placeholder-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.3), transparent);
}

.icon-wrapper {
  position: relative;
  margin-bottom: 1.5rem;
  animation: float 3s ease-in-out infinite;
}

.search-icon {
  position: relative;
  z-index: 1;
  font-size: 4rem;
  filter: drop-shadow(0 4px 16px rgba(59, 130, 246, 0.5));
  animation: pulse 2s ease-in-out infinite;
}

.icon-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 140px;
  height: 140px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.4) 0%, rgba(59, 130, 246, 0.1) 50%, transparent 100%);
  filter: blur(30px);
  animation: glowPulse 3s ease-in-out infinite;
  z-index: 0;
}

.placeholder-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 0.75rem 0;
  letter-spacing: -0.01em;
  text-shadow: 0 2px 12px rgba(59, 130, 246, 0.3);
}

.placeholder-text {
  font-size: 1rem;
  line-height: 1.6;
  color: #9aa3ad;
  margin: 0;
  font-weight: 400;
}

.info-text {
  font-size: 0.9rem;
  line-height: 1.5;
  color: #b8c5d3;
  margin: 0;
  text-align: left;
}

.info-text strong {
  color: #e0e7ef;
  font-weight: 600;
}

.no-results-container {
  width: 90%;
  max-width: 400px;
  margin: 4rem auto 2rem auto;
  pointer-events: none;
  animation: fadeIn 0.4s ease;
}

.no-results-container.pushed-down {
  margin-top: 1rem;
}

.no-results-card {
  background: linear-gradient(to bottom, rgba(18, 22, 30, 0.75), rgba(18, 22, 30, 0.65));
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  padding: 2.5rem 2rem;
  text-align: center;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.04);
  position: relative;
  overflow: hidden;
}

.no-results-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(239, 68, 68, 0.3), transparent);
}

.no-results-icon {
  font-size: 3.5rem;
  opacity: 0.5;
  margin-bottom: 1rem;
  filter: grayscale(0.5);
}

.no-results-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 0.5rem 0;
  letter-spacing: -0.01em;
  word-break: break-word;
}

.no-results-hint {
  font-size: 0.9rem;
  line-height: 1.5;
  color: #9aa3ad;
  margin: 0;
  font-weight: 400;
}

.ambient-glow {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: -1;
  pointer-events: none;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 1;
  animation: glowFloat 10s ease-in-out infinite;
}

.glow-orb.orb-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.25) 0%, rgba(59, 130, 246, 0.08) 50%, transparent 100%);
  top: -150px;
  left: -150px;
}

.glow-orb.orb-2 {
  width: 450px;
  height: 450px;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.2) 0%, rgba(96, 165, 250, 0.06) 50%, transparent 100%);
  bottom: -100px;
  right: -100px;
}

.glow-orb.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.18) 0%, rgba(37, 99, 235, 0.05) 50%, transparent 100%);
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
}

@keyframes glowFloat {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(-20px, -25px) scale(1.05);
  }
  66% {
    transform: translate(20px, 20px) scale(0.95);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

@keyframes glowPulse {
  0%,
  100% {
    opacity: 0.6;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.9;
    transform: translate(-50%, -50%) scale(1.15);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.results-container {
  position: relative;
  padding: 1rem 0 2rem 0;
  min-height: 50vh;
}

.results-section {
  margin: 0 1rem 2rem 1rem;
  animation: slideUp 0.4s ease;
  background: linear-gradient(to bottom, rgba(18, 22, 30, 0.6), rgba(18, 22, 30, 0.5));
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  box-shadow:
    0 4px 16px rgba(0, 0, 0, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.04);
  overflow: hidden;
}

@media (max-width: 640px) {
  .results-section {
    margin: 0 0.75rem 2rem 0.75rem;
  }
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  background: linear-gradient(to bottom, rgba(18, 22, 30, 0.4), transparent);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  position: relative;
}

@media (max-width: 640px) {
  .section-header {
    padding: 0.875rem 1rem;
  }
}

.section-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.3), transparent);
}

.section-content {
  padding: 0.5rem 0;
}

.section-icon-wrapper {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(37, 99, 235, 0.15));
  border: 1px solid rgba(59, 130, 246, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 12px rgba(59, 130, 246, 0.2);
}

@media (max-width: 640px) {
  .section-icon-wrapper {
    width: 32px;
    height: 32px;
    border-radius: 8px;
  }
}

.section-icon {
  font-size: 20px;
  color: #60a5fa;
}

@media (max-width: 640px) {
  .section-icon {
    font-size: 18px;
  }
}

.section-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  letter-spacing: -0.01em;
  flex: 1;
}

@media (max-width: 640px) {
  .section-title {
    font-size: 1.1rem;
  }
}

.section-count {
  font-size: 0.9rem;
  font-weight: 600;
  color: #60a5fa;
  background: rgba(59, 130, 246, 0.15);
  padding: 4px 12px;
  border-radius: 12px;
  border: 1px solid rgba(59, 130, 246, 0.3);
}

@media (max-width: 640px) {
  .section-count {
    font-size: 0.85rem;
    padding: 3px 10px;
  }
}

.name-list {
  padding: 0 0.5rem;
  background: transparent;
}

@media (max-width: 640px) {
  .name-list {
    padding: 0 0.25rem;
  }
}

.posts-list {
  background: transparent;
  padding: 0;
}

.tracks-list {
  background: transparent;
  padding: 0;
}

.playlists-list {
  background: transparent;
  padding: 0;
}

.post-wrapper {
  width: 100%;
}

.track-wrapper {
  width: 100%;
  padding: 0 1rem;
}

.playlist-wrapper {
  width: 100%;
  padding: 0 1rem;
}

.post-separator {
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.1) 10%,
    rgba(255, 255, 255, 0.1) 90%,
    transparent 100%
  );
  margin: 0.5rem 1rem;
}

.track-separator {
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.1) 10%,
    rgba(255, 255, 255, 0.1) 90%,
    transparent 100%
  );
  margin: 0.5rem 1rem;
}

.playlist-separator {
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.1) 10%,
    rgba(255, 255, 255, 0.1) 90%,
    transparent 100%
  );
  margin: 0.5rem 1rem;
}

@media (max-width: 640px) {
  .post-separator {
    margin: 0.5rem 0.75rem;
  }

  .track-separator {
    margin: 0.5rem 0.75rem;
  }

  .playlist-separator {
    margin: 0.5rem 0.75rem;
  }

  .track-wrapper {
    padding: 0 0.75rem;
  }

  .playlist-wrapper {
    padding: 0 0.75rem;
  }
}

.post-card {
  padding: 0;
  margin: 0;
}

@media (max-width: 640px) {
  .placeholder-card.main-card {
    padding: 2.5rem 1.5rem;
  }

  .placeholder-title {
    font-size: 1.3rem;
  }

  .placeholder-text {
    font-size: 0.95rem;
  }

  .search-icon {
    font-size: 3.5rem;
  }

  .info-text {
    font-size: 0.85rem;
  }

  .no-results-card {
    padding: 2rem 1.5rem;
  }
}

/* Header styles removed - now using Header component */

.header-subtitle {
  font-size: 0.85rem;
  color: #9aa3ad;
  margin: 0;
  font-weight: 500;
}

@media (max-width: 640px) {
  .header-subtitle {
    font-size: 0.8rem;
  }
}
</style>
