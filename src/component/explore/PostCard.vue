<script setup lang="ts">
import {
  alertController,
  AlertInput,
  IonButton,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardSubtitle,
  IonCardTitle,
  IonCol,
  IonIcon,
  IonImg,
  IonNote,
  toastController,
} from '@ionic/vue';
import { onMounted, PropType, Ref, ref } from 'vue';
import { Post, PostCounters } from '@/entity/Post';
import { chatbubbleOutline, heart, heartOutline, shareOutline, trashOutline } from 'ionicons/icons';
import { deleteLike, deletePost, getPostCounters, getPostLikes, postLike } from '@/client/postClient';
import { useAuthStore } from '@/store/reactiveStorage';
import { createPostCanvas, downloadCanvas, ExportTheme } from '@/graphics/canvasExport';
import { Router, useRouter } from 'vue-router';
import { User } from '@/entity/User';

const router: Router = useRouter();
const authStore = useAuthStore();

const props = defineProps({
  post: {
    required: true,
    type: Object as PropType<Post>,
  },
});

const emit = defineEmits(['post-deleted']);

const focusedPost: Ref<Post | null> = ref(null);
const likedPosts: Ref<Set<string>> = ref(new Set());
const postCounters: Ref<PostCounters> = ref({});

const exportThemes = [
  {
    label: 'Black',
    type: 'radio',
    value: 'black',
  },
  {
    label: 'White',
    type: 'radio',
    value: 'white',
  },
  {
    label: 'Transparent',
    type: 'radio',
    value: 'transparent',
  },
] as AlertInput[];

const focusPost = (post: Post) => {
  focusedPost.value = focusedPost.value === post ? null : post;
};

const exportPost = async (post: Post) => {
  const alert = await alertController.create({
    header: 'Select theme for export',
    inputs: exportThemes,
    buttons: [
      {
        text: 'Cancel',
        role: 'cancel',
      },
      {
        text: 'Export',
        handler: async (selectedTheme: string) => {
          if (!selectedTheme) return;
          try {
            const canvas = await createPostCanvas(post, selectedTheme as ExportTheme);

            downloadCanvas(canvas);
          } catch (err) {
            console.error('Export failed:', err);
          }
        },
      },
    ],
  });
  await alert.present();
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

const toggleLikePost = async (slug: string) => {
  const isLiked = likedPosts.value.has(slug);

  try {
    if (isLiked) {
      await deleteLike(slug);
      likedPosts.value.delete(slug);
    } else {
      await postLike(slug);
      likedPosts.value.add(slug);
    }

    await loadCounters(slug);
  } catch (error) {
    console.error('Unable to update like: ', error);
  }
};

const loadCounters = async (slug: string) => {
  try {
    postCounters.value[slug] = await getPostCounters(slug);
  } catch (error) {
    console.error('Unable to load post counters:', error);
  }
};

const loadLikes = async (slug: string) => {
  try {
    const likes = await getPostLikes(slug);
    const userSlug = authStore.getAuthenticatedUserSlug;

    if (likes.some((like) => like.user.slug === userSlug)) {
      likedPosts.value.add(slug);
    }
  } catch (error) {
    console.error('Unable to load likes:', error);
  }
};

const openUserProfile = async (user: User) => {
  await router.push(`/user/${user.slug}`);
};

const goToComments = async (slug: string) => {
  await router.push({ name: 'CommentView', params: { slug } });
};

onMounted(() => {
  loadCounters(props.post.slug);
  loadLikes(props.post.slug);
});
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4">
    <ion-card @click="focusPost(post)" :class="{ focused: focusedPost === post }">
      <ion-card-header>
        <div>
          <ion-card-title class="artist-name" @click="openUserProfile(post.owner)">
            <span>@</span>{{ post.owner.username }}
          </ion-card-title>
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
        <div v-if="focusedPost === post" class="settings-container">
          <ion-button fill="clear" size="small" @click.stop="toggleLikePost(post.slug)">
            <ion-icon
              :icon="likedPosts.has(post.slug) ? heart : heartOutline"
              :color="likedPosts.has(post.slug) ? 'danger' : ''"
              slot="start" />
            <ion-note v-if="postCounters[post.slug]">({{ postCounters[post.slug].likeCount }})</ion-note>
          </ion-button>

          <ion-button fill="clear" size="small" @click.stop="goToComments(post.slug)">
            <ion-icon :icon="chatbubbleOutline" slot="start" />
            <ion-note v-if="postCounters[post.slug]">({{ postCounters[post.slug].commentCount }})</ion-note>
          </ion-button>

          <ion-button fill="clear" size="small" @click.stop="exportPost(post)" id="export-button">
            Export
            <ion-icon :icon="shareOutline" />
          </ion-button>

          <ion-button
            v-if="post.owner.slug === authStore.getAuthenticatedUserSlug"
            fill="clear"
            size="small"
            @click.stop="askDeletePost(post.slug)">
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
