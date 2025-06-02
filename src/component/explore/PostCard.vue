<script setup lang="ts">
import {
  IonCard,
  IonButton,
  IonCardContent,
  IonCardHeader,
  IonCardTitle,
  IonCol,
  IonCardSubtitle,
  IonImg,
  IonIcon,
  IonNote,
  alertController,
  toastController,
} from '@ionic/vue';
import { PropType, Ref, ref } from 'vue';
import { Post } from '@/entity/Post';
import { trashOutline } from 'ionicons/icons';
import { deletePost } from '@/client/postClient';
import { useAuthStore } from '@/store/reactiveStorage';

defineProps({
  post: {
    required: true,
    type: Object as PropType<Post>,
  },
});

const emit = defineEmits(['post-deleted']);

const focusedPost: Ref<Post | null> = ref(null);

const focusPost = (post: Post) => {
  focusedPost.value = focusedPost.value === post ? null : post;
};

const askDeletePost = async (slug: string) => {
  const alert = await alertController.create({
    header: 'Confirm Post Deletion',
    message: 'Are you sure you want to delete this post?',
    buttons: [
      { text: 'Cancel', role: 'cancel' },
      {
        text: 'Delete',
        handler: async () => {
          try {
            await deletePost(slug);
            emit('post-deleted', slug);
          } catch (error) {
            const toast = await toastController.create({
              message: 'Failed to delete post.',
              duration: 1500,
              position: 'top',
            });
            await toast.present();
          }
        },
      },
    ],
  });
  await alert.present();
};

const authStore = useAuthStore();
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4">
    <ion-card @click="focusPost(post)" :class="{ focused: focusedPost === post }">
      <ion-card-header>
        <div>
          <ion-card-title class="artist-name"><span>@</span>{{ post.owner.username }}</ion-card-title>
          <ion-note>{{ post.createdAt.toLocaleString() }}</ion-note>
          <ion-card-subtitle>{{ post.type.replaceAll('_', ' ') }}</ion-card-subtitle>
        </div>
      </ion-card-header>
      <ion-card-content>
        <ion-card-title>{{ post.title }}</ion-card-title>
        <p>{{ post.content }}</p>
        <ion-img v-if="post.imageUrl" :src="post.imageUrl" :alt="`Post ${post.title}`" />
        <div v-if="post.audioUrl" class="audio-content">
          <audio :src="post.audioUrl" controls></audio>
        </div>
        <div
          v-if="focusedPost === post && post.owner.slug === authStore.getAuthenticatedUserSlug"
          class="settings-container">
          <ion-button fill="clear" size="small" @click.stop="askDeletePost(post.slug)">
            Delete
            <ion-icon :icon="trashOutline" />
          </ion-button>
        </div>
      </ion-card-content>
    </ion-card>
  </ion-col>
</template>

<style scoped>
ion-card {
  margin: 0 0.4em 1em 0.4em;

  &.focused {
    outline: 0.2em solid #3880ff;
  }
}

ion-card-header > div {
  display: flex;
  justify-content: space-between;
  align-content: center;

  & .artist-name {
    font-size: 1em;
    color: #999999;

    & span {
      color: white;
    }
  }
}

ion-card-content {
  & ion-card-title {
    font-size: 1.3rem;
    margin-bottom: 0.4em;
  }

  & p {
    text-align: justify;
  }

  & ion-img {
    border-radius: 5px;
    overflow: hidden;
    margin-top: 1em;
  }

  & .audio-content {
    padding-top: 0;
  }

  & audio {
    width: 100%;
    margin-top: 10px;
  }

  & .settings-container {
    margin-top: 0.8em;
    display: flex;
    justify-content: flex-end;

    & ion-button {
      margin: 0;
      font-weight: bold;

      & ion-icon {
        margin-left: 0.4em;
      }
    }
  }
}
</style>
