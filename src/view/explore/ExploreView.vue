<script setup lang="ts">
import { computed, onMounted, provide, ref, Ref, watch } from 'vue';
import { IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonPage, IonRow, onIonViewWillEnter } from '@ionic/vue';
import { addCircleOutline, compassOutline, ellipsisHorizontal, mapOutline } from 'ionicons/icons';
import { useRouter } from 'vue-router';
import { Swiper, SwiperSlide } from 'swiper/vue';
import { Swiper as SwiperClass } from 'swiper';
import 'swiper/css';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import PostCard from '@/component/explore/PostCard.vue';
import { Post } from '@/entity/Post';
import { callRetrieveFollowingPosts, callRetrieveRecommendedPosts, PostPage } from '@/client/postClient';
import { showToast } from '@/utils';
import { useAuthStore } from '@/store/reactiveStorage';
import { usePlayerStore } from '@/store/playerStore';
import { audioPlayerService } from '@/services/audioPlayerService';
import AppHeader from '@/component/AppHeader.vue';
import ActionsPopup, { ActionItem } from '@/component/actions/ActionsPopup.vue';

const router = useRouter();
const authStore = useAuthStore();
const playerStore = usePlayerStore();

const PAGE_SIZE = 10;

type Tab = 'for-you' | 'following';

const posts: Ref<Post[]> = ref([]);
const followingPosts: Ref<Post[]> = ref([]);
const currentPage = ref(0);
const isLoading = ref(false);
const hasMore = ref(true);

const currentPageFollowing = ref(0);
const hasMoreFollowing = ref(true);

const activeTab = ref<Tab>('for-you');
const swiperInstance = ref<SwiperClass | null>(null);

const showActionsPopup = ref(false);
const popupX = ref(0);
const popupY = ref(0);

const canCreateContent = computed(() => {
  const allowedRoles = new Set(['ADMIN', 'PREMIUM', 'ARTIST']);
  const user = authStore.getAuthenticatedUser;

  if (!user) return false;
  return user.roles.some((role) => allowedRoles.has(role));
});

const onSwiperInit = (instance: SwiperClass) => {
  swiperInstance.value = instance;
};

const onSlideChange = () => {
  const swiper = swiperInstance.value;
  const index = swiper?.activeIndex;

  if (!swiper) return;
  if (index === 0) {
    activeTab.value = 'for-you';
    if (posts.value.length === 0) loadPosts('for-you', true);
  } else if (index === 1) {
    activeTab.value = 'following';
    if (followingPosts.value.length === 0) loadPosts('following', true);
  }
};

const swipeToTab = (tab: Tab) => {
  const swiper = swiperInstance.value;
  const index = tab === 'for-you' ? 0 : 1;

  if (!swiper) return;
  swiper.slideTo(index);
};

const loadPosts = async (tab: Tab, reset: boolean) => {
  if (isLoading.value || (!hasMore.value && !reset && tab === 'for-you')) return;
  if (isLoading.value || (!hasMoreFollowing.value && !reset && tab === 'following')) return;

  try {
    const page = reset ? 0 : tab === 'for-you' ? currentPage.value : currentPageFollowing.value;
    const pageable = { page, size: PAGE_SIZE, sort: 'createdAt,desc' };
    let postsPage: PostPage;

    isLoading.value = true;
    if (tab === 'for-you') postsPage = await callRetrieveRecommendedPosts(pageable);
    else postsPage = await callRetrieveFollowingPosts(pageable);
    if (reset) {
      if (tab === 'for-you') {
        posts.value = postsPage.content;
        hasMore.value = postsPage.hasMore;
        currentPage.value = 1;
      } else {
        followingPosts.value = postsPage.content;
        hasMoreFollowing.value = postsPage.hasMore;
        currentPageFollowing.value = 1;
      }
    } else if (tab === 'for-you') {
      posts.value.push(...postsPage.content);
      hasMore.value = postsPage.hasMore;
      currentPage.value++;
    } else {
      followingPosts.value.push(...postsPage.content);
      hasMoreFollowing.value = postsPage.hasMore;
      currentPageFollowing.value++;
    }
  } catch (error: unknown) {
    const message = 'Failed to load feed. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
};

const onInfinite = async (ev: CustomEvent<void>) => {
  await loadPosts(activeTab.value, false);
  await (ev.target as HTMLIonInfiniteScrollElement).complete();
};

const handleEventsMap = () => {
  router.push('/events');
};

const handleCreatePost = () => {
  router.push('/explore/post/create');
};

const playTrackWithContext = async (post: Post) => {
  if (!post.audioUrl) return;

  const postsArray = activeTab.value === 'for-you' ? posts.value : followingPosts.value;
  const context = {
    contextId: `explore:${activeTab.value}`,
    tracks: postsArray
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

const openActionsPopup = (event: MouseEvent) => {
  const button = event.currentTarget as HTMLElement;
  const bounds = button.getBoundingClientRect();

  popupX.value = bounds.right - 220;
  popupY.value = bounds.bottom + 8;
  showActionsPopup.value = true;
};

const closeActionsPopup = () => {
  showActionsPopup.value = false;
};

provide('playTrackWithContext', playTrackWithContext);

onMounted(() => {
  audioPlayerService.init(playerStore);
});

onIonViewWillEnter(() => {
  loadPosts('for-you', true);

  if (typeof window !== 'undefined') {
    window.__explorePosts = posts.value.map((p) => ({ slug: p.slug }));
    window.__exploreFollowingPosts = followingPosts.value.map((p) => ({ slug: p.slug }));
  }
});

watch(posts, (newPosts: Post[]) => {
  if (typeof window !== 'undefined') {
    window.__explorePosts = newPosts.map((p: Post) => ({ slug: p.slug }));
  }
});

watch(followingPosts, (newPosts: Post[]) => {
  if (typeof window !== 'undefined') {
    window.__exploreFollowingPosts = newPosts.map((p: Post) => ({ slug: p.slug }));
  }
});

const ITEMS: ActionItem[] = [
  { icon: addCircleOutline, label: 'Create Post', action: handleCreatePost },
  { icon: mapOutline, label: 'Events Map', action: handleEventsMap },
];
</script>

<template>
  <ion-page>
    <ApplicationViewport>
      <div class="ambient-glow">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
      </div>

      <AppHeader
        v-if="!authStore.isAuthenticated"
        title="Explore"
        subtitle="Discover new music and connect with artists."
        :icon="compassOutline" />

      <AppHeader v-else :icon="compassOutline">
        <template #actions>
          <div class="tab-switcher">
            <div :class="{ 'tab-pill': true, active: activeTab === 'for-you' }" @click="swipeToTab('for-you')">
              For You
            </div>
            <div :class="{ 'tab-pill': true, active: activeTab === 'following' }" @click="swipeToTab('following')">
              Following
            </div>
          </div>

          <button v-if="!canCreateContent" class="events-button" @click="handleEventsMap">
            <ion-icon :icon="mapOutline" />
          </button>
          <button v-else class="actions-button" @click="openActionsPopup">
            <ion-icon :icon="ellipsisHorizontal" />
          </button>
        </template>
      </AppHeader>

      <ActionsPopup :is-open="showActionsPopup" :x="popupX" :y="popupY" :actions="ITEMS" @close="closeActionsPopup" />

      <Swiper
        v-if="authStore.isAuthenticated"
        @swiper="onSwiperInit"
        @slideChange="onSlideChange"
        :initial-slide="0"
        :speed="250"
        :allowTouchMove="true">
        <!-- For You Tab -->
        <SwiperSlide>
          <div class="tab-content">
            <ion-row no-bounce>
              <div
                v-for="(post, index) in posts"
                :key="post.slug"
                :class="['post-wrapper', index === posts.length - 1 ? 'last' : '']">
                <PostCard
                  :post="post"
                  @post-deleted="() => loadPosts('for-you', true)"
                  @post-like="post.likeCount++"
                  @post-unlike="post.likeCount--"
                  @post-repost="post.repostCount++"
                  @post-unrepost="post.repostCount--"
                  class="postcard" />
              </div>
            </ion-row>
            <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMore">
              <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
            </ion-infinite-scroll>
          </div>
        </SwiperSlide>

        <!-- Following Tab -->
        <SwiperSlide>
          <div class="tab-content">
            <ion-row no-bounce>
              <div
                v-for="(post, index) in followingPosts"
                :key="post.slug"
                :class="['post-wrapper', index === followingPosts.length - 1 ? 'last' : '']">
                <PostCard
                  :post="post"
                  @post-deleted="() => loadPosts('following', true)"
                  @post-like="post.likeCount++"
                  @post-unlike="post.likeCount--"
                  @post-repost="post.repostCount++"
                  @post-unrepost="post.repostCount--"
                  class="postcard" />
              </div>
            </ion-row>
            <div v-if="followingPosts.length === 0" class="no-content">No posts from users you follow.</div>
            <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMoreFollowing">
              <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
            </ion-infinite-scroll>
          </div>
        </SwiperSlide>
      </Swiper>

      <!-- Single tab for non-authenticated users -->
      <div v-else class="tab-content">
        <ion-row no-bounce>
          <div
            v-for="(post, index) in posts"
            :key="post.slug"
            :class="['post-wrapper', index === posts.length - 1 ? 'last' : '']">
            <PostCard
              :post="post"
              @post-deleted="() => loadPosts('for-you', true)"
              @post-like="post.likeCount++"
              @post-unlike="post.likeCount--"
              @post-repost="post.repostCount++"
              @post-unrepost="post.repostCount--"
              class="postcard" />
          </div>
        </ion-row>
        <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMore">
          <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
        </ion-infinite-scroll>
      </div>
    </ApplicationViewport>
  </ion-page>
</template>

<style scoped>
.tab-switcher {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.tab-pill {
  padding: 0.6rem 1.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 24px;
  background: transparent;
  letter-spacing: 0.01em;
  text-align: center;
  white-space: nowrap;
  border: 1px solid transparent;
}

.tab-pill:active {
  transform: scale(0.96);
}

.tab-pill.active {
  color: #ffffff;
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.3);
  box-shadow:
    0 2px 12px rgba(59, 130, 246, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.tab-content {
  width: 100%;
  height: 100%;
  min-height: 100%;
  overflow-y: auto;
}

.swiper {
  width: 100%;
  height: calc(100% - 75px);
}

.swiper-container {
  width: 100%;
  height: auto;
}

.post-wrapper {
  width: 100%;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);

  & .postcard {
    padding: 0;
    margin: 0;
  }
}

.post-wrapper.last {
  border-bottom: none;
}

.no-content {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100vw;
  height: calc(50vh);
  margin: 0;
  font-size: 1.2rem;
  color: var(--ion-color-medium);
}

.ambient-glow {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
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

@media (max-width: 640px) {
  .tab-pill {
    padding: 0.5rem 1.25rem;
    font-size: 0.85rem;
  }
}
</style>
