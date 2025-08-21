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
  return 'imageUrl' in props.user && props.user.imageUrl !== undefined;
});

const avatarUrl = computed(() => {
  if (isArtist.value) return (props.user as Artist).imageUrl || '/assets/images/svg/avatar.svg';
  return (props.user as User).avatarUrl || '/assets/images/svg/avatar.svg';
});

const redirectToProfile = async () => {
  const baseRoute = isArtist.value ? 'artist' : 'user';
  await router.push(`/${baseRoute}/${props.user.slug}`);
};
</script>

<template>
  <img :src="avatarUrl" alt="User avatar" class="avatar" @click.stop="redirectToProfile" />
</template>
