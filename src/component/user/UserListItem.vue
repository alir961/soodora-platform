<script setup lang="ts">
import { IonItem, IonAvatar } from '@ionic/vue';
import { User } from '@/entity/User';
import { useRouter } from 'vue-router';
import Avatar from '@/component/user/AvatarItem.vue';
import UsernameTag from '@/component/user/UsernameTag.vue';
import FollowButton from '@/component/follow/FollowButton.vue';
import { computed, PropType } from 'vue';
import { useAuthStore } from '@/store/reactiveStorage';

const props = defineProps({
  user: {
    type: Object as PropType<User>,
    required: true,
  },
});

const authStore = useAuthStore();

const canFollow = computed(() => {
  return authStore.getAuthenticatedUserSlug !== props.user.slug;
});

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
    <FollowButton v-if="canFollow" slot="end" :user="user" @click.stop />
  </ion-item>
  <div class="item-separator" />
</template>

<style scoped>
ion-item {
  --border-width: 0;
  --border-style: none;
  --background: var(--ion-background-color, #fff);

  & ion-avatar {
    width: 30px;
    height: 30px;
  }
}

.item-separator {
  height: 1px;
  background-color: rgba(255, 255, 255, 0.15);
  margin: 0.5em 0.5em;

  &:nth-last-child(1) {
    display: none;
  }
}
</style>
