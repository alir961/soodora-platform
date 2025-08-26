<script setup lang="ts">
import { PropType, computed } from 'vue';
import { useRouter } from 'vue-router';
import { UserProfile } from '@/entity/User';

const props = defineProps({
  user: {
    type: Object as PropType<UserProfile>,
    required: true,
  },
});

const router = useRouter();

const avatarUrl = computed(() => {
  if (!props.user) return '/assets/images/svg/avatar.svg';
  return props.user.avatarUrl || '/assets/images/svg/avatar.svg';
});

const redirectToProfile = async () => {
  await router.push(`/user/${props.user.slug}`);
};
</script>

<template>
  <img :src="avatarUrl" alt="User avatar" class="avatar" @click.stop="redirectToProfile" />
</template>
