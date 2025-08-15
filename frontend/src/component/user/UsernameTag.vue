<script setup lang="ts">
import { PropType, computed } from 'vue';
import { useRouter } from 'vue-router';
import { User } from '@/entity/User';
import { Artist } from '@/entity/Artist';

type Vendor = User | Artist;

const props = defineProps({
  user: {
    type: Object as PropType<Vendor>,
    required: true,
  },
});

const router = useRouter();

const isArtist = computed(() => {
  return 'name' in props.user && !('username' in props.user);
});

const displayName = computed(() => {
  if (isArtist.value) return (props.user as Artist).name;
  return (props.user as User).username;
});

const redirectToProfile = async () => {
  const baseRoute = isArtist.value ? 'artist' : 'user';
  await router.push(`/${baseRoute}/${props.user.slug}`);
};
</script>

<template>
  <span class="username" @click.stop="redirectToProfile"> @{{ displayName }} </span>
</template>

<style scoped>
.username {
  font-size: 1.2rem;
  color: white;
  cursor: pointer;
  font-weight: bold;
}
</style>
