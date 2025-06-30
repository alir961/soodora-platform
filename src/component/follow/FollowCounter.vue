<script setup lang="ts">
import { onMounted, PropType, ref, watch } from 'vue';
import { User } from '@/entity/User';
import { callRetrieveFollowers, callRetrieveFollowing } from '@/client/userClient';
import { modalController } from '@ionic/vue';
import { useRoute } from 'vue-router';
import UserList from '@/component/user/UserList.vue';

const followers = ref<User[]>([]);
const following = ref<User[]>([]);

const route = useRoute();

const props = defineProps({
  user: {
    type: Object as PropType<User>,
    required: true,
  },
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
      <div class="counter" @click="openFollowersList">
        <span>{{ followers.length }}</span> Followers
      </div>
      <div class="counter" @click="openFollowingList">
        <span>{{ following.length }}</span> Following
      </div>
    </div>
  </div>
</template>

<style scoped>
.followers-container {
  & .counters {
    display: flex;
    justify-content: space-between;
  }
}

.counter {
  padding: 0.3em 1em;
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.06);
  font-weight: bold;
  font-size: 1.2em;
  color: var(--ion-color-medium);
  display: inline-block;
  cursor: pointer;
}

.counter span {
  color: white;
}

.counter:active {
  background-color: rgba(255, 255, 255, 0.15);
}

.follow-button {
  margin-top: 1em;
}
</style>
