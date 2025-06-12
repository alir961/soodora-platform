<script setup lang="ts">
import { computed, onMounted, PropType, ref, watch } from 'vue';
import { User } from '@/entity/User';
import { callRetrieveFollowers, callRetrieveFollowing } from '@/client/userClient';
import { useAuthStore } from '@/store/reactiveStorage';
import { callFollowUser, callUnfollowUser } from '@/client/profileClient';
import { IonButton } from '@ionic/vue';
import { useRoute } from 'vue-router';

const followers = ref<User[]>([]);
const following = ref<User[]>([]);

const route = useRoute();

const props = defineProps({
  profile: {
    required: true,
    type: Object as PropType<User>,
  },
});

const authStore = useAuthStore();

const isFollowing = computed(() => {
  const authenticatedUser = authStore.getAuthenticatedUser;

  if (!authenticatedUser) return false;
  return followers.value.some((follower) => follower.slug === authenticatedUser.slug);
});

const canFollowUser = computed(() => {
  const authenticatedUser = authStore.getAuthenticatedUser;

  return props.profile !== authenticatedUser;
});

const reloadFollowers = () => {
  callRetrieveFollowers(props.profile)
    .then((data) => (followers.value = data))
    .catch((error) => console.error(error));
};

const reloadFollowing = () => {
  callRetrieveFollowing(props.profile)
    .then((data) => (following.value = data))
    .catch((error) => console.error(error));
};

const toggleFollow = async () => {
  try {
    const slug = props.profile.slug;

    if (isFollowing.value) await callUnfollowUser(slug);
    else await callFollowUser(slug);
    reloadFollowers();
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  reloadFollowers();
  reloadFollowing();
});

watch(
  () => route.fullPath,
  () => {
    reloadFollowers();
    reloadFollowing();
  }
);
</script>

<template>
  <div class="followers-container">
    <div class="counters">
      <p>
        <span>{{ followers.length }}</span> Followers
      </p>
      <p>
        <span>{{ following.length }}</span> Following
      </p>
    </div>
    <div v-if="canFollowUser" class="follow-button">
      <ion-button fill="outline" size="small" color="danger" @click="toggleFollow">
        {{ isFollowing ? 'Unfollow' : 'Follow' }}
      </ion-button>
    </div>
  </div>
</template>

<style scoped>
.followers-container {
  & .counters {
    display: flex;
    justify-content: space-between;

    & p {
      color: #999999;
      font-weight: bold;

      & > span {
        color: white;
      }
    }
  }
}

.follow-button {
  margin-top: 1em;
}
</style>
