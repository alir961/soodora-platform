<script setup lang="ts">
import { PropType } from 'vue';
import { User } from '@/entity/User';
import { useRouter } from 'vue-router';

const props = defineProps({
  user: {
    type: Object as PropType<User>,
    required: true,
  },
});

const router = useRouter();

const redirectToProfile = async () => {
  await router.push(`/user/${props.user.slug}`);
};
</script>

<template>
  <img
    :src="user.avatarUrl || '/assets/images/svg/avatar.svg'"
    alt="User avatar"
    class="avatar"
    @click="redirectToProfile" />
</template>
