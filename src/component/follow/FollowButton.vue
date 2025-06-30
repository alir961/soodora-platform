<script setup lang="ts">
import { IonButton } from '@ionic/vue';
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
  <ion-button
    class="follow-button"
    fill="outline"
    size="small"
    :color="isFollowing ? 'danger' : 'primary'"
    @click="toggleFollow">
    {{ isFollowing ? 'Unfollow' : 'Follow' }}
  </ion-button>
</template>

<style scoped>
.follow-button {
  --border-radius: 20px;
  --padding-start: 15px;
  --padding-end: 15px;
  --padding-top: 5px;
  --padding-bottom: 5px;
  font-weight: 600;
  font-size: 1rem;
  text-transform: none;
}
.follow-button:active {
  filter: brightness(0.9);
}
</style>
