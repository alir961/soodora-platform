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
import { PropType, Ref, ref, watch } from 'vue';
import { Post } from '@/entity/Post';
import { chatbubbleOutline, heart, heartOutline, shareOutline, syncOutline, trashOutline } from 'ionicons/icons';
import {
  callPostUnlike,
  callDeletePost,
  callGetPostLikes,
  callPostLike,
  callPostUnrepost,
  callPostRepost,
  callGetPostReposts,
} from '@/client/postClient';
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

const emit = defineEmits(['post-deleted', 'post-like', 'post-unlike', 'post-repost', 'post-unrepost']);

const isFocused: Ref<boolean> = ref(false);
const isLiked: Ref<boolean> = ref(false);
const isReposted: Ref<boolean> = ref(false);

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
            await callDeletePost(slug);
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

const loadLikes = async (postSlug: string) => {
  try {
    const likes = await callGetPostLikes(postSlug);
    const userSlug = authStore.getAuthenticatedUserSlug;

    if (likes.some((like) => like === userSlug)) isLiked.value = true;
  } catch (error) {
    console.error('Unable to load likes', error);
  }
};

const loadReposts = async (postSlug: string) => {
  try {
    const reposts = await callGetPostReposts(postSlug);
    const userSlug = authStore.getAuthenticatedUserSlug;

    if (reposts.some((repost) => repost === userSlug)) isReposted.value = true;
  } catch (error) {
    console.error('Unable to load reposts', error);
  }
};

const toggleLikePost = async (postSlug: string) => {
  try {
    if (isLiked.value && props.post) {
      await callPostUnlike(postSlug);
      isLiked.value = false;
      emit('post-unlike');
    } else {
      await callPostLike(postSlug);
      isLiked.value = true;
      emit('post-like');
    }
  } catch (error) {
    console.error('Unable to update like', error);
  }
};

const toggleRepostPost = async (postSlug: string) => {
  try {
    if (isReposted.value && props.post) {
      await callPostUnrepost(postSlug);
      isReposted.value = false;
      emit('post-unrepost');
    } else {
      await callPostRepost(postSlug);
      isReposted.value = true;
      emit('post-repost');
    }
  } catch (error) {
    console.error('Unable to update repost', error);
  }
};

const redirectToUserProfile = async (user: User) => {
  await router.push(`/user/${user.slug}`);
};

const redirectToPostComments = async (postSlug: string) => {
  await router.push({ name: 'CommentView', params: { slug: postSlug } });
};

watch(
  () => authStore.getAuthenticatedUser,
  (newUser) => {
    if (newUser) {
      const postSlug = props.post.slug;

      loadLikes(postSlug);
      loadReposts(postSlug);
    }
  },
  { immediate: true }
);
</script>

<template>
  <ion-col size="12" size-md="6" size-lg="4">
    <ion-card @click="isFocused = !isFocused" :class="{ focused: isFocused }">
      <ion-card-header>
        <div>
          <ion-card-title class="artist-name" @click="redirectToUserProfile(post.owner)">
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
        <div v-if="isFocused" class="settings-container">
          <ion-button fill="clear" size="small" @click.stop="toggleLikePost(post.slug)">
            <ion-icon :icon="isLiked ? heart : heartOutline" :color="isLiked ? 'danger' : ''" slot="start" />
            <ion-note>{{ post.likeCount }}</ion-note>
          </ion-button>

          <ion-button fill="clear" size="small" @click.stop="toggleRepostPost(post.slug)">
            <ion-icon
              :icon="syncOutline"
              :color="isReposted ? 'danger' : ''"
              slot="start"
              :class="{ rotate: isReposted }" />
            <ion-note>{{ post.repostCount }}</ion-note>
          </ion-button>

          <ion-button fill="clear" size="small" @click.stop="redirectToPostComments(post.slug)">
            <ion-icon :icon="chatbubbleOutline" slot="start" />
            <ion-note>{{ post.commentCount }}</ion-note>
          </ion-button>

          <ion-button fill="clear" size="small" @click.stop="exportPost(post)" id="export-button">
            <ion-icon :icon="shareOutline" />
            <ion-note>Export</ion-note>
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
        margin-right: 0.4em;
      }
    }
  }
}

.rotate {
  animation: rotate 3s infinite;
}

@-webkit-keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
