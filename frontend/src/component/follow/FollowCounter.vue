<script setup lang="ts">
import { onMounted, PropType, ref, watch } from 'vue';
import { User } from '@/entity/User';
import { callRetrieveFollowers, callRetrieveFollowing } from '@/client/userClient';
import { useRoute, useRouter } from 'vue-router';
import FollowButton from '@/component/follow/FollowButton.vue';

const followers = ref<User[]>([]);
const following = ref<User[]>([]);
const showList = ref<'followers' | 'following' | null>(null);

const route = useRoute();
const router = useRouter();

const props = defineProps({
  user: {
    type: Object as PropType<User>,
    required: true,
  },
});

const emit = defineEmits<{
  'list-opened': [type: 'followers' | 'following'];
  'list-closed': [];
}>();

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

const openFollowersList = () => {
  showList.value = 'followers';
  emit('list-opened', 'followers');
};

const openFollowingList = () => {
  showList.value = 'following';
  emit('list-opened', 'following');
};

const closeList = () => {
  showList.value = null;
  emit('list-closed');
};

const navigateToProfile = (slug: string) => {
  router.push(`/user/${slug}`);
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

defineExpose({ closeList });
</script>

<template>
  <div v-if="!showList" class="followers-container">
    <div class="counters">
      <div @click="openFollowersList">
        <span>{{ followers.length }}</span> Followers
      </div>
      <div @click="openFollowingList">
        <span>{{ following.length }}</span> Following
      </div>
    </div>
  </div>

  <div v-else class="list-container">
    <div class="user-list">
      <div
        v-for="user in showList === 'followers' ? followers : following"
        :key="user.username"
        class="user-card"
        @click="navigateToProfile(user.slug)">
        <div class="user-avatar">
          <img v-if="user.avatarUrl" :src="user.avatarUrl" :alt="user.username" />
          <div v-else class="avatar-placeholder">
            {{ user.username.charAt(0).toUpperCase() }}
          </div>
        </div>
        <div class="user-info">
          <div class="user-username">@{{ user.username }}</div>
          <div class="user-fullname">{{ user.firstname }} {{ user.lastname }}</div>
        </div>
        <FollowButton :user="user" @click.stop class="follow-btn" />
      </div>

      <div v-if="(showList === 'followers' ? followers : following).length === 0" class="empty-state">
        <p>No {{ showList }} yet</p>
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

.list-container {
  padding: 1rem 0;
  width: 100%;
  overflow: hidden;
}

.user-list {
  display: grid;
  gap: 12px;
  width: 100%;
  max-width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  padding: 14px 16px;
  cursor: pointer;
  transition:
    background 0.2s ease,
    border-color 0.2s ease,
    transform 0.06s ease;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  overflow: hidden;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid rgba(59, 130, 246, 0.3);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
  overflow: hidden;
  max-width: 100%;
}

.user-username {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}

.user-fullname {
  font-size: 14px;
  color: #9aa3ad;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}

.follow-btn {
  flex-shrink: 0;
}

.empty-state {
  text-align: center;
  padding: 3em 0;
  color: #9aa3ad;
  font-size: 1.1rem;
}

@media (max-width: 600px) {
  .user-avatar {
    width: 45px;
    height: 45px;
  }

  .avatar-placeholder {
    font-size: 18px;
  }
}
</style>
