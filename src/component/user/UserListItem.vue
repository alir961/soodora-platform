<script setup lang="ts">
import { IonItem, IonAvatar } from '@ionic/vue';
import { User } from '@/entity/User';
import { useRouter } from 'vue-router';
import Avatar from '@/component/user/AvatarItem.vue';
import UsernameTag from '@/component/user/UsernameTag.vue';
import FollowButton from '../user/FollowButton.vue';

const props = defineProps<{
  user: User & {
    canFollow?: boolean;
    isFollowing?: boolean;
    toggleFollow?: () => void;
  };
  isLast?: boolean;
}>();

const { user, isLast } = props;

const router = useRouter();

const redirectToProfile = async () => {
  await router.push(`/user/${props.user.slug}`);
};
</script>

<template>
  <ion-item button @click="redirectToProfile">
    <ion-avatar slot="start">
      <Avatar :user="user" />
    </ion-avatar>
    <UsernameTag :user="user" />
    <!-- This doesn't work, I have to remember to fix it later pleaseee -->
    <FollowButton
      v-if="user.canFollow"
      :canFollow="user.canFollow"
      :isFollowing="user.isFollowing ?? false"
      @toggle="user.toggleFollow" />
    <!-- This doesn't work, I have to remember to fix it later pleaseee -->
  </ion-item>
  <div v-if="!isLast" class="post-separator"></div>
</template>

<style scoped>
ion-item {
  --border-width: 0;
  --border-style: none;
}

ion-avatar {
  width: 30px;
  height: 30px;
}

.post-separator {
  height: 1px;
  background-color: rgba(255, 255, 255, 0.15);
  margin: 0.5em 0.5em;
}
</style>
