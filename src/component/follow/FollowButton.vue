<script setup lang="ts">
import { callFollowUser, callUnfollowUser } from '@/client/profileClient';
import { User } from '@/entity/User';
import { computed, onMounted, PropType, ref } from 'vue';
import { useAuthStore } from '@/store/reactiveStorage';
import { callRetrieveFollowers } from '@/client/userClient';

const authStore = useAuthStore();

const followers = ref<User[]>([]);

const props = defineProps({
  user: {
    type: Object as PropType<User>,
    required: true,
  },
});

const emit = defineEmits(['toggle']);

const isFollowing = computed(() => {
  const authenticatedUser = authStore.getAuthenticatedUser;

  if (!authenticatedUser) return false;
  return followers.value.some((follower) => follower.slug === authenticatedUser.slug);
});

const reloadFollowers = () => {
  callRetrieveFollowers(props.user)
    .then((data) => (followers.value = data))
    .catch((error) => console.error(error));
};

const toggleFollow = async () => {
  try {
    const slug = props.user.slug;

    if (isFollowing.value) await callUnfollowUser(slug);
    else await callFollowUser(slug);
    reloadFollowers();
    emit('toggle');
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  reloadFollowers();
});
</script>

<template>
  <button class="follow-button" :class="{ 'is-following': isFollowing }" @click="toggleFollow">
    {{ isFollowing ? 'Unfollow' : 'Follow' }}
  </button>
</template>

<style scoped>
.follow-button {
  border-radius: 20px;
  padding: 5px 10px;
  font-weight: 600;
  font-size: 1rem;
  text-transform: none;
  margin: 0;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;

  /* Follow state - Blue gradient */
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.9), rgba(37, 99, 235, 0.9));
  color: #ffffff;
  box-shadow: 0 2px 12px rgba(59, 130, 246, 0.3);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.follow-button:hover {
  background: linear-gradient(135deg, rgba(59, 130, 246, 1), rgba(37, 99, 235, 1));
}

.follow-button:active {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.95), rgba(29, 78, 216, 0.95));
  transform: scale(0.96);
}

.follow-button.is-following {
  /* Unfollow state - Transparent with subtle background */
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.9);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.follow-button.is-following:hover {
  background: rgba(255, 255, 255, 0.12);
}

.follow-button.is-following:active {
  background: rgba(239, 68, 68, 0.15);
}
</style>
