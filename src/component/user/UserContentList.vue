<script setup lang="ts">
import { Swiper, SwiperSlide } from 'swiper/vue';
import { onMounted, onUnmounted, PropType, Ref, ref } from 'vue';
import { Swiper as SwiperClass } from 'swiper';
import 'swiper/css';
import { IonGrid, IonInfiniteScroll, IonInfiniteScrollContent, IonRow } from '@ionic/vue';
import PostCard from '@/component/explore/PostCard.vue';
import TrackCard from '@/component/music/TrackCard.vue';
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
import { usePlayerStore } from '@/store/playerStore';
import { audioPlayerService } from '@/services/audioPlayerService';
import SaveTrackCard from '@/component/actions/SaveTrackCard.vue';

type Tab = 'posts' | 'reposts' | 'audios' | 'products';

const PAGE_SIZE = 10;
const playerStore = usePlayerStore();

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
const saveTrackCardRef = ref<InstanceType<typeof SaveTrackCard> | null>(null);
const selectedTrackForSave = ref<Post | null>(null);

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
    const pageable = { page, size: PAGE_SIZE, sort: 'createdAt,desc' };
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

// Provide context to child components
import { provide } from 'vue';

// Function to play a track with full context
const playTrackWithContext = async (post: Post) => {
  if (!post.audioUrl) return;

  // Create context with all audio tracks from user posts
  const context = {
    contextId: `profile:${props.user.slug}:${activeTab.value}`,
    tracks: posts.value
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
  loadPosts('posts', true);

  // Initialize audio service
  audioPlayerService.init(playerStore);
});

onUnmounted(() => {
  // Cleanup if needed
});
</script>

<template>
  <div class="tab-header">
    <div :class="['tab-button', activeTab === 'posts' ? 'active' : '']" @click="goToTab(0)">Posts</div>
    <div :class="['tab-button', activeTab === 'reposts' ? 'active' : '']" @click="goToTab(1)">Reposts</div>
    <div :class="['tab-button', activeTab === 'audios' ? 'active' : '']" @click="goToTab(2)">Tracks</div>
    <div :class="['tab-button', activeTab === 'products' ? 'active' : '']" @click="goToTab(3)">Products</div>
  </div>

  <swiper @swiper="onSwiperInit" @slideChange="onSlideChange" :initial-slide="0" :speed="250">
    <swiper-slide>
      <ion-grid>
        <ion-row>
          <div
            v-for="(post, index) in posts"
            :key="post.slug"
            :class="['post-wrapper', index === posts.length - 1 ? 'last' : '']">
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
          <div
            v-for="(post, index) in posts"
            :key="post.slug"
            :class="['post-wrapper', index === posts.length - 1 ? 'last' : '']">
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
          <div
            v-for="(post, index) in posts"
            :key="post.slug"
            :class="['track-wrapper', index === posts.length - 1 ? 'last' : '']">
            <TrackCard :post="post" @save-track="openSaveTrack(post)" />
          </div>
        </ion-row>
      </ion-grid>
      <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMorePost">
        <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
      </ion-infinite-scroll>
    </swiper-slide>
    <swiper-slide class="products-slide">
      <ion-grid>
        <ion-row>
          <ProductCard
            v-for="product in products"
            :key="product.slug"
            :product="product"
            @product-deleted="() => loadProducts(true)" />
        </ion-row>
      </ion-grid>
      <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMoreProduct">
        <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
      </ion-infinite-scroll>
    </swiper-slide>
  </swiper>

  <!-- Save Track Modal -->
  <SaveTrackCard
    ref="saveTrackCardRef"
    :post-slug="selectedTrackForSave?.slug || ''"
    :audio-title="selectedTrackForSave?.audioTitle"
    :audio-url="selectedTrackForSave?.audioUrl"
    :image-url="selectedTrackForSave?.imageUrl" />
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

.post-wrapper.last {
  border-bottom: none;
}

.track-wrapper {
  width: 100%;
  padding: 0 1rem;
}

.track-wrapper.last {
  border-bottom: none;
}

.postcard {
  padding: 0;
  margin: 0;
}

.tab-header {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: rgba(18, 22, 30, 0.4);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  margin-bottom: 0.5rem;
}

.tab-button {
  padding: 0.5rem 1rem;
  font-size: 0.85rem;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 20px;
  background: transparent;
  letter-spacing: 0.01em;
  text-align: center;
  white-space: nowrap;
  border: 1px solid transparent;
}

.tab-button:active {
  transform: scale(0.96);
}

.tab-button.active {
  color: #ffffff;
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.3);
  box-shadow:
    0 2px 12px rgba(59, 130, 246, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.products-slide {
  padding-top: 1rem;
}
</style>
