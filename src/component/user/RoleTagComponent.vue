<script setup lang="ts">
import { capitalize } from '@/utils';
import { PropType } from 'vue';
import { Role } from '@/entity/User';

interface RoleColors {
  color: string;
  backgroundColor: string;
}

function getRoleColors(role: Role): RoleColors {
  if (role === 'PREMIUM') return { color: '#fff', backgroundColor: '#e91' };
  if (role === 'ARTIST') return { color: '#fff', backgroundColor: '#a5d' };
  if (role === 'ADMIN') return { color: '#fff', backgroundColor: '#c22' };
  if (role === 'USER') return { color: '#fff', backgroundColor: '#555' };
  return { color: '#fff', backgroundColor: '#fff' };
}

defineProps({
  role: {
    required: true,
    type: String as PropType<Role>,
  },
});
</script>

<template>
  <span :style="{ ...getRoleColors($props.role) }">
    {{ capitalize($props.role) }}
  </span>
</template>

<style scoped>
span {
  border-radius: 15px;
  padding: 2px 10px;
}
</style>
