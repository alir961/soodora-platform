<script setup lang="ts">
import { ref, Ref } from 'vue';
import {
  toastController,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
  IonRow,
  IonPage,
  onIonViewWillEnter,
} from '@ionic/vue';
import { Swiper, SwiperSlide } from 'swiper/vue';
import { Swiper as SwiperClass } from 'swiper';
import 'swiper/css';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import PostCard from '@/component/explore/PostCard.vue';
import { Post } from '@/entity/Post';
import { callRetrieveFollowingPosts, callRetrieveRecommendedPosts, PostPage } from '@/client/postClient';

const PAGE_SIZE = 10;

type Tab = 'for-you' | 'following';

const posts: Ref<Post[]> = ref([]);
const followingPosts: Ref<Post[]> = ref([]);
const currentPage = ref(0);
const isLoading = ref(false);
const hasMore = ref(true);
const activeTab = ref<Tab>('for-you');

const swiperInstance = ref<SwiperClass | null>(null);

const onSwiperInit = (instance: SwiperClass) => {
  swiperInstance.value = instance;
};

const loadPosts = async (tab: Tab, reset: boolean) => {
  if (isLoading.value || (!hasMore.value && !reset)) return;

  try {
    const page = reset ? 0 : currentPage.value;
    const pageable = { page, size: PAGE_SIZE };
    let postsPage: PostPage;

    isLoading.value = true;
    if (tab === 'for-you') postsPage = await callRetrieveRecommendedPosts(pageable);
    else postsPage = await callRetrieveFollowingPosts(pageable);
    if (reset) {
      if (tab === 'for-you') posts.value = postsPage.content;
      else followingPosts.value = postsPage.content;
      currentPage.value = 1;
    } else {
      if (tab === 'for-you') posts.value.push(...postsPage.content);
      else followingPosts.value.push(...postsPage.content);
      currentPage.value++;
    }
    hasMore.value = postsPage.hasMore;
  } catch (error) {
    const toast = await toastController.create({
      message: 'Failed to load feed!',
      duration: 1500,
      position: 'top',
    });
    console.error(error);
    await toast.present();
  } finally {
    isLoading.value = false;
  }
};

const onInfinite = async (ev: CustomEvent<void>) => {
  await loadPosts(activeTab.value, false);
  await (ev.target as HTMLIonInfiniteScrollElement).complete();
};

const onSlideChange = () => {
  const swiper = swiperInstance.value;
  const index = swiper?.activeIndex;

  if (!swiper) return;
  activeTab.value = index === 0 ? 'for-you' : 'following';
  loadPosts(activeTab.value, true);
};

const goToTab = (index: number) => {
  const swiper = swiperInstance.value;

  if (!swiper) return;
  swiper.slideTo(index);
};

onIonViewWillEnter(() => {
  loadPosts('for-you', true);
});
</script>

<template>
  <ion-page>
    <ApplicationViewport title="Explore">
      <div class="tab-header">
        <div :class="['tab-button', activeTab === 'for-you' ? 'active' : '']" @click="goToTab(0)">For You</div>
        <div :class="['tab-button', activeTab === 'following' ? 'active' : '']" @click="goToTab(1)">Following</div>
      </div>

      <swiper @swiper="onSwiperInit" @slideChange="onSlideChange" :initial-slide="0" :speed="250">
        <swiper-slide>
          <ion-row no-bounce>
            <div v-for="post in posts" :key="post.slug" class="post-wrapper">
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
        </swiper-slide>

        <swiper-slide>
          <ion-row no-bounce>
            <div v-for="post in followingPosts" :key="post.slug" class="post-wrapper">
              <PostCard
                :post="post"
                @post-deleted="() => loadPosts('following', true)"
                @post-like="post.likeCount++"
                @post-unlike="post.likeCount--"
                @post-repost="post.repostCount++"
                @post-unrepost="post.repostCount--"
                class="postcard" />
            </div>
            <div v-if="followingPosts.length === 0" class="no-content">
              <p class="no-content-text">
                Follow someone<br />
                to see their posts!
              </p>
            </div>
          </ion-row>
          <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMore">
            <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
          </ion-infinite-scroll>
        </swiper-slide>
      </swiper>
    </ApplicationViewport>
  </ion-page>
</template>

<style scoped>
.tab-header {
  display: flex;
  justify-content: space-around;
  padding: 1rem 0;
  background-color: #111;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);

  & .tab-button {
    color: #aaa;
    font-weight: 600;
    cursor: pointer;

    &.active {
      color: white;
      border-bottom: 2px solid white;
    }
  }
}

.post-wrapper {
  width: 100%;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);

  & .postcard {
    padding: 0;
    margin: 0;
  }
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

.no-content-text {
  text-align: center;
  width: 100%;
}
</style>
