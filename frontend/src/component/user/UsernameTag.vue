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
  <span class="username" @click="redirectToProfile"> @{{ user.username }} </span>
</template>

<style scoped>
.username {
  font-size: 1.2rem;
  color: white;
  cursor: pointer;
  font-weight: bold;
}
</style>
