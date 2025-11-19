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

const emit = defineEmits(['select']);

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
  <ion-item
    button
    @click="
      () => {
        emit('select');
        redirectToProfile();
      }
    ">
    <ion-avatar slot="start">
      <Avatar :user="user" />
    </ion-avatar>
    <div class="username-wrapper">
      <UsernameTag :user="user" />
    </div>
    <div v-if="canFollow" slot="end" class="follow-button-wrapper">
      <FollowButton :user="user" />
    </div>
  </ion-item>
  <div class="item-separator" />
</template>

<style scoped>
ion-item {
  --background: transparent;
  --background-hover: rgba(255, 255, 255, 0.05);
  --background-activated: rgba(255, 255, 255, 0.08);
  --border-width: 0;
  --border-style: none;
  --padding-start: 1rem;
  --padding-end: 1rem;
  --inner-padding-end: 12px;
  --min-height: 70px;

  & ion-avatar {
    width: 48px;
    height: 48px;
    margin-right: 0.75rem;
  }
}

.username-wrapper {
  flex: 1;
  min-width: 0;
  margin-right: 12px;
}

@media (max-width: 640px) {
  ion-item {
    --padding-start: 0.75rem;
    --padding-end: 0.75rem;
    --inner-padding-end: 10px;
    --min-height: 65px;

    & ion-avatar {
      width: 44px;
      height: 44px;
    }
  }
}

.item-separator {
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.1) 10%,
    rgba(255, 255, 255, 0.1) 90%,
    transparent 100%
  );
  margin: 0 1rem;

  &:nth-last-child(1) {
    display: none;
  }
}

@media (max-width: 640px) {
  .item-separator {
    margin: 0 0.75rem;
  }
}
</style>
