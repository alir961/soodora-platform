<script setup lang="ts">
import { IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonCol, IonCardSubtitle } from '@ionic/vue';
import { PropType, Ref, ref } from 'vue';
import { Post } from '@/entity/Post';

defineProps({
  post: {
    required: true,
    type: Object as PropType<Post>,
  },
});

const focusedPost: Ref<Post | null> = ref(null);

const focusPost = (post: Post) => {
  focusedPost.value = focusedPost.value === post ? null : post;
};
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4">
    <ion-card @click="focusPost(post)" :class="{ focused: focusedPost === post }">
      <ion-card-header>
        <ion-card-title>{{ post.owner.username }}</ion-card-title>
        <ion-card-subtitle>{{ post.title }}</ion-card-subtitle>
      </ion-card-header>
      <ion-card-content>{{ post.content }}</ion-card-content>
    </ion-card>
  </ion-col>
</template>

<style scoped>
.focused {
  border: 0.2em solid #3880ff;
  transform: scale(1.05);
}
</style>
