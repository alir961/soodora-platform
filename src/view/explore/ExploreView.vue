<script setup lang="ts">
import { Ref, ref, onMounted } from 'vue';
import { IonGrid, IonRow, onIonViewWillEnter, toastController } from '@ionic/vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { User } from '@/entity/User';
import { useAuthStore } from '@/store/reactiveStorage';
import PostCard from '@/component/explore/PostCard.vue';
import { Post } from '@/entity/Post';
import { callRetrievePosts } from '@/client/postClient';

const posts: Ref<Post[]> = ref([]);
const userData: Ref<User | null> = ref(null);
const authStore = useAuthStore();

const loadPosts = async () => {
  try {
    posts.value = await callRetrievePosts();
  } catch (error) {
    const toast = await toastController.create({
      message: `Failed to load feed!`,
      duration: 1500,
      position: 'top',
    });

    console.error(error);
    await toast.present();
  }
};

onIonViewWillEnter(async () => {
  await loadPosts();
});

onMounted(() => {
  if (authStore.isAuthenticated) userData.value = authStore.getAuthenticatedUser;
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
          @post-deleted="loadPosts"
          @post-like="post.likeCount++"
          @post-unlike="post.likeCount--"
          @post-repost="post.repostCount++"
          @post-unrepost="post.repostCount--" />
      </ion-row>
    </ion-grid>
  </ApplicationViewport>
</template>
