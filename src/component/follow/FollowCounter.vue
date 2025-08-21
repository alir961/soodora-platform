<script setup lang="ts">
import { onMounted, PropType, ref, watch } from 'vue';
import { User } from '@/entity/User';
import { callRetrieveFollowers, callRetrieveFollowing } from '@/client/userClient';
import { useRoute } from 'vue-router';

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
      <div>
        <span>{{ followers.length }}</span> Followers
      </div>
      <div>
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

.counters > div {
  padding: 0.3em 0.8em;
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.06);
  font-weight: bold;
  font-size: 0.9em;
  color: var(--ion-color-medium);
  display: inline-block;
  cursor: pointer;
}

.counters > div span {
  color: white;
}

.counters > div:active {
  background-color: rgba(255, 255, 255, 0.15);
}
</style>
