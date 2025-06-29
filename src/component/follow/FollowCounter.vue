<script setup lang="ts">
import { computed, onMounted, PropType, ref, watch } from 'vue';
import { User } from '@/entity/User';
import { callRetrieveFollowers, callRetrieveFollowing } from '@/client/userClient';
import { useAuthStore } from '@/store/reactiveStorage';
import { modalController } from '@ionic/vue';
import { useRoute } from 'vue-router';
import UserList from '@/component/user/UserList.vue';
import FollowButton from '@/component/follow/FollowButton.vue';

const followers = ref<User[]>([]);
const following = ref<User[]>([]);

const route = useRoute();

const props = defineProps({
  user: {
    type: Object as PropType<User>,
    required: true,
  },
});

const authStore = useAuthStore();

const canFollowUser = computed(() => {
  const authenticatedUserSlug = authStore.getAuthenticatedUserSlug;

  return props.user.slug !== authenticatedUserSlug;
});

const reloadFollowers = () => {
  callRetrieveFollowers(props.user)
    .then((data) => (followers.value = data))
    .catch((error) => console.error(error));
};

const reloadFollowing = () => {
  callRetrieveFollowing(props.user)
    .then((data) => (following.value = data))
    .catch((error) => console.error(error));
};

const openFollowersList = async () => {
  const modal = await modalController.create({
    component: UserList,
    componentProps: {
      title: 'Followers',
      users: followers.value,
    },
  });

  await modal.present();
};

const openFollowingList = async () => {
  const modal = await modalController.create({
    component: UserList,
    componentProps: {
      title: 'Following',
      users: following.value,
    },
  });

  await modal.present();
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
      <p @click="openFollowersList">
        <span>{{ followers.length }}</span> Followers
      </p>
      <p @click="openFollowingList">
        <span>{{ following.length }}</span> Following
      </p>
    </div>
    <FollowButton v-if="canFollowUser" :user="user" class="follow-button" @toggle="reloadFollowers" />
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
