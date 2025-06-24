<script setup lang="ts">
import { Swiper, SwiperSlide } from 'swiper/vue';
import { onMounted, PropType, Ref, ref } from 'vue';
import { Swiper as SwiperClass } from 'swiper';
import { IonGrid, IonInfiniteScroll, IonInfiniteScrollContent, IonRow, toastController } from '@ionic/vue';
import PostCard from '@/component/explore/PostCard.vue';
import { Post } from '@/entity/Post';
import { callRetrieveUserAudios, callRetrieveUserPosts, callRetrieveUserReposts } from '@/client/userClient';
import { User } from '@/entity/User';
import { PostPage } from '@/client/postClient';

type PostFilter = 'posts' | 'reposts' | 'audios';

const PAGE_SIZE = 10;

const props = defineProps({
  user: {
    required: true,
    type: Object as PropType<User>,
  },
});

const isLoading = ref(false);

const posts: Ref<Post[]> = ref([]);

const currentPage = ref(0);
const hasMore = ref(true);

const postFilter = ref<PostFilter>('posts');
const swiperInstance = ref<SwiperClass | null>(null);

const onSwiperInit = (instance: SwiperClass) => {
  swiperInstance.value = instance;
};

const onSlideChange = () => {
  const swiper = swiperInstance.value;
  const index = swiper?.activeIndex;

  if (!swiper) return;
  if (index == 0) postFilter.value = 'posts';
  else if (index == 1) postFilter.value = 'reposts';
  else if (index == 2) postFilter.value = 'audios';
  loadPosts(postFilter.value, true);
};

const goToTab = (index: number) => {
  const swiper = swiperInstance.value;

  if (!swiper) return;
  swiper.slideTo(index);
};

const loadPosts = async (filter: PostFilter, reset: boolean) => {
  if (isLoading.value || (!hasMore.value && !reset)) return;
  try {
    const page = reset ? 0 : currentPage.value;
    const pageable = { page, size: PAGE_SIZE };
    let postPage: PostPage | null = null;

    isLoading.value = true;
    if (filter === 'posts') postPage = await callRetrieveUserPosts(props.user, pageable);
    if (filter === 'reposts') postPage = await callRetrieveUserReposts(props.user, pageable);
    if (filter === 'audios') postPage = await callRetrieveUserAudios(props.user, pageable);
    if (!postPage) return;

    if (reset) {
      posts.value = postPage.content;
      currentPage.value = 1;
    } else {
      posts.value.push(...postPage.content);
      currentPage.value++;
    }
    hasMore.value = postPage.hasMore;
  } catch (error) {
    const toast = await toastController.create({
      message: 'Failed to load user posts!',
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
  await loadPosts(postFilter.value, false);
  await (ev.target as HTMLIonInfiniteScrollElement).complete();
};

onMounted(() => {
  loadPosts('posts', true);
});
</script>

<template>
  <div class="tab-header">
    <div :class="['tab-button', postFilter === 'posts' ? 'active' : '']" @click="goToTab(0)">Posts</div>
    <div :class="['tab-button', postFilter === 'reposts' ? 'active' : '']" @click="goToTab(1)">Reposts</div>
    <div :class="['tab-button', postFilter === 'audios' ? 'active' : '']" @click="goToTab(2)">Audios</div>
  </div>

  <swiper @swiper="onSwiperInit" @slideChange="onSlideChange" :initial-slide="0" :speed="250">
    <swiper-slide>
      <ion-grid>
        <ion-row>
          <div v-for="post in posts" :key="post.slug" class="post-wrapper">
            <PostCard
              :post="post"
              @post-deleted="() => loadPosts('posts', true)"
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
      <ion-grid>
        <ion-row>
          <div v-for="post in posts" :key="post.slug" class="post-wrapper">
            <PostCard
              :post="post"
              @post-deleted="() => loadPosts('reposts', true)"
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
      <ion-grid>
        <ion-row>
          <div v-for="post in posts" :key="post.slug" class="post-wrapper">
            <PostCard
              :post="post"
              @post-deleted="() => loadPosts('audios', true)"
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
  </swiper>
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
