<script setup lang="ts">
import { PropType } from 'vue';
import { IonItem, IonAvatar } from '@ionic/vue';
import { User } from '@/entity/User';
import { useRouter } from 'vue-router';
import Avatar from '@/component/user/Avatar.vue';
import UsernameTag from '@/component/user/UsernameTag.vue';
import FollowButton from '../user/FollowButton.vue';

const props = defineProps({
  user: {
    type: Object as PropType<User & {
      canFollow?: boolean;
      isFollowing?: boolean;
      toggleFollow?: () => void;
    }>,
    required: true,
  },
  isLast: {
    type: Boolean,
    default: false,
  },
});

const router = useRouter();

console.log('UserListItem user:', props.user);
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
      @toggle="user.toggleFollow"
    />
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