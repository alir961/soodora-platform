<script setup lang="ts">
import { ref, Ref } from 'vue';
import {
  onIonViewWillEnter,
  toastController,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
  IonGrid,
  IonRow,
  IonPage,
} from '@ionic/vue';
import { Swiper, SwiperSlide } from 'swiper/vue';
import { Swiper as SwiperClass } from 'swiper';
import 'swiper/css';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import PostCard from '@/component/explore/PostCard.vue';
import { Post } from '@/entity/Post';
import { callRetrievePosts } from '@/client/postClient';

const posts: Ref<Post[]> = ref([]);
const followingPosts: Ref<Post[]> = ref([]);
const currentPage = ref(0);
const isLoading = ref(false);
const hasMore = ref(true);
const PAGE_SIZE = 10;
const activeTab = ref<'foryou' | 'following'>('foryou');

const swiperInstance = ref<SwiperClass | null>(null);

const onSwiperInit = (instance: SwiperClass) => {
  swiperInstance.value = instance;
};

const loadPosts = async (reset = false, tab = 'foryou') => {
  if (isLoading.value || (!hasMore.value && !reset)) return;

  isLoading.value = true;
  try {
    const page = reset ? 0 : currentPage.value;
    const filter = tab === 'following' ? 'following' : undefined;
    const { content, hasMore: more } = await callRetrievePosts({
      page,
      size: PAGE_SIZE,
      sort: 'createdAt,desc',
      ...(filter && { filter }),
    });

    if (reset) {
      if (tab === 'foryou') posts.value = content;
      else followingPosts.value = content;
      currentPage.value = 1;
    } else {
      if (tab === 'foryou') posts.value.push(...content);
      else followingPosts.value.push(...content);
      currentPage.value++;
    }

    hasMore.value = more;
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
  await loadPosts(false, activeTab.value);
  await (ev.target as HTMLIonInfiniteScrollElement).complete();
};

const onSlideChange = () => {
  const swiper = swiperInstance.value;
  if (!swiper) return;
  const index = swiper.activeIndex;
  activeTab.value = index === 0 ? 'foryou' : 'following';
  if (activeTab.value === 'foryou') {
    loadPosts(true, 'foryou');
  }
};

const goToTab = (index: number) => {
  swiperInstance.value?.slideTo(index);
};

onIonViewWillEnter(() => {
  loadPosts(true, 'foryou');
});
</script>

<template>
  <ion-page>
    <ApplicationViewport title="Explore">
      <div class="tab-header">
        <div :class="['tab-button', activeTab === 'foryou' ? 'active' : '']" @click="goToTab(0)">For You</div>
        <div :class="['tab-button', activeTab === 'following' ? 'active' : '']" @click="goToTab(1)">Following</div>
      </div>

      <swiper @swiper="onSwiperInit" @slideChange="onSlideChange" :initial-slide="0" :speed="250">
        <swiper-slide>
          <ion-grid>
            <ion-row>
              <div v-for="post in posts" :key="post.slug" class="post-wrapper">
                <PostCard
                  :post="post"
                  @post-deleted="() => loadPosts(true, 'foryou')"
                  @post-like="post.likeCount++"
                  @post-unlike="post.likeCount--"
                  @post-repost="post.repostCount++"
                  @post-unrepost="post.repostCount--"
                  class="postcard" />
              </div>
            </ion-row>
          </ion-grid>
          <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMore">
            <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
          </ion-infinite-scroll>
        </swiper-slide>

        <swiper-slide>
          <div style="display: flex; justify-content: center; align-items: center; height: 50vh; font-size: 1.2rem">
            <p>Following feed is coming soon!</p>
          </div>
        </swiper-slide>
      </swiper>
    </ApplicationViewport>
  </ion-page>
</template>

<style scoped>
ion-grid {
  width: 100%;
  padding: 0;
  margin: 0;
}

.post-wrapper {
  width: 100%;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.postcard {
  padding: 0;
  margin: 0;
}

.tab-header {
  display: flex;
  justify-content: space-around;
  padding: 1rem 0;
  background-color: #111;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.tab-button {
  color: #aaa;
  font-weight: 600;
  cursor: pointer;
}
.tab-button.active {
  color: white;
  border-bottom: 2px solid white;
}
</style>
