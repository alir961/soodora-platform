<script setup lang="ts">
import { Ref, ref } from 'vue';
import {
  IonGrid,
  IonRow,
  IonInfiniteScroll,
  IonInfiniteScrollContent,
  onIonViewWillEnter,
  toastController,
} from '@ionic/vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import PostCard from '@/component/explore/PostCard.vue';
import { Post } from '@/entity/Post';
import { callRetrievePosts } from '@/client/postClient';

const posts: Ref<Post[]> = ref([]);
const currentPage = ref(0);
const isLoading = ref(false);
const hasMore = ref(true);
const PAGE_SIZE = 10;

const loadPosts = async (reset = false) => {
  if (isLoading.value || (!hasMore.value && !reset)) return;

  isLoading.value = true;
  try {
    const page = reset ? 0 : currentPage.value;
    const { content, hasMore: more } = await callRetrievePosts({
      page,
      size: PAGE_SIZE,
      sort: 'createdAt,desc',
    });

    if (reset) {
      posts.value = content;
      currentPage.value = 0;
    } else {
      posts.value.push(...content);
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
  await loadPosts();
  await (ev.target as HTMLIonInfiniteScrollElement).complete();
};

onIonViewWillEnter(() => {
  loadPosts(true);
});
</script>

<template>
  <ApplicationViewport title="Explore">
    <ion-grid>
      <ion-row>
        <PostCard
          v-for="post in posts"
          :key="post.slug"
          :post="post"
          @post-deleted="loadPosts(true)"
          @post-like="post.likeCount++"
          @post-unlike="post.likeCount--"
          @post-repost="post.repostCount++"
          @post-unrepost="post.repostCount--" />
      </ion-row>
    </ion-grid>

    <ion-infinite-scroll @ionInfinite="onInfinite" threshold="6.25rem" :disabled="!hasMore">
      <ion-infinite-scroll-content loading-spinner="bubbles" loading-text="Loading more..." />
    </ion-infinite-scroll>
  </ApplicationViewport>
</template>
