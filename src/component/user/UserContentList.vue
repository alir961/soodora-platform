<script setup lang="ts">
import { Swiper, SwiperSlide } from 'swiper/vue';
import { onMounted, PropType, Ref, ref } from 'vue';
import { Swiper as SwiperClass } from 'swiper';
import { IonGrid, IonInfiniteScroll, IonInfiniteScrollContent, IonRow } from '@ionic/vue';
import PostCard from '@/component/explore/PostCard.vue';
import { Post } from '@/entity/Post';
import {
  callRetrieveUserAudios,
  callRetrieveUserPosts,
  callRetrieveUserProduct,
  callRetrieveUserReposts,
} from '@/client/userClient';
import { User } from '@/entity/User';
import { PostPage } from '@/client/postClient';
import { Product } from '@/entity/Product';
import ProductCard from '@/component/marketplace/ProductCard.vue';
import { showToast } from '@/utils';

type Tab = 'posts' | 'reposts' | 'audios' | 'products';

const PAGE_SIZE = 10;

const props = defineProps({
  user: {
    required: true,
    type: Object as PropType<User>,
  },
});

const isLoading = ref(false);

const posts: Ref<Post[]> = ref([]);
const products: Ref<Product[]> = ref([]);

const currentPostPage = ref(0);
const hasMorePost = ref(true);

const currentProductPage = ref(0);
const hasMoreProduct = ref(true);

const activeTab = ref<Tab>('posts');
const swiperInstance = ref<SwiperClass | null>(null);

const onSwiperInit = (instance: SwiperClass) => {
  swiperInstance.value = instance;
};

const onSlideChange = async () => {
  const swiper = swiperInstance.value;
  const index = swiper?.activeIndex;

  if (!swiper) return;
  if (index == 0) activeTab.value = 'posts';
  else if (index == 1) activeTab.value = 'reposts';
  else if (index == 2) activeTab.value = 'audios';
  else if (index == 3) activeTab.value = 'products';

  if (activeTab.value === 'products') await loadProducts(true);
  else await loadPosts(activeTab.value, true);
};

const goToTab = (index: number) => {
  const swiper = swiperInstance.value;

  if (!swiper) return;
  swiper.slideTo(index);
};

const loadPosts = async (filter: Tab, reset: boolean) => {
  if (isLoading.value || (!hasMorePost.value && !reset)) return;
  try {
    const page = reset ? 0 : currentPostPage.value;
    const pageable = { page, size: PAGE_SIZE };
    let postPage: PostPage | null = null;

    isLoading.value = true;
    if (props.user.slug) {
      if (filter === 'posts') postPage = await callRetrieveUserPosts(props.user.slug, pageable);
      if (filter === 'audios') postPage = await callRetrieveUserAudios(props.user.slug, pageable);
    }

    if (filter === 'reposts') postPage = await callRetrieveUserReposts(props.user, pageable);
    if (!postPage) return;

    if (reset) {
      posts.value = postPage.content;
      currentPostPage.value = 1;
    } else {
      posts.value.push(...postPage.content);
      currentPostPage.value++;
    }
    hasMorePost.value = postPage.hasMore;
  } catch (error: unknown) {
    const message = 'Failed to load user posts. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
};

const loadProducts = async (reset: boolean) => {
  if (props.user?.slug) {
    const page = reset ? 0 : currentPostPage.value;
    const pageable = { page, size: PAGE_SIZE };
    const productPage = await callRetrieveUserProduct(props.user.slug, pageable);

    if (reset) {
      products.value = productPage.content;
      currentProductPage.value = 1;
    } else {
      products.value.push(...productPage.content);
      currentProductPage.value++;
    }
    hasMoreProduct.value = productPage.hasMore;
  }
};

const onInfinite = async (ev: CustomEvent<void>) => {
  if (activeTab.value === 'products') await loadProducts(false);
  else await loadPosts(activeTab.value, false);
  await (ev.target as HTMLIonInfiniteScrollElement).complete();
};

onMounted(() => {
  loadPosts('posts', true);
});
</script>

<template>
  <div class="tab-header">
    <div :class="['tab-button', activeTab === 'posts' ? 'active' : '']" @click="goToTab(0)">Posts</div>
    <div :class="['tab-button', activeTab === 'reposts' ? 'active' : '']" @click="goToTab(1)">Reposts</div>
    <div :class="['tab-button', activeTab === 'audios' ? 'active' : '']" @click="goToTab(2)">Audios</div>
    <div :class="['tab-button', activeTab === 'products' ? 'active' : '']" @click="goToTab(3)">Products</div>
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
      <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMorePost">
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
      <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMorePost">
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
      <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMorePost">
        <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
      </ion-infinite-scroll>
    </swiper-slide>
    <swiper-slide>
      <ion-grid>
        <ion-row>
          <div v-for="product in products" :key="product.slug" class="post-wrapper">
            <ProductCard :product="product" />
          </div>
        </ion-row>
      </ion-grid>
      <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMorePost">
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
  background-color: #000000;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
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
