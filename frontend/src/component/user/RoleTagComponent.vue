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
  if (role === 'ARTIST') return { color: '#fff', backgroundColor: 'var(--ion-color-primary)' };
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
  border-radius: 20px;
  padding-left: 10px;
  padding-right: 10px;
  padding-top: 5px;
  padding-bottom: 5px;
  font-weight: 600;
  font-size: 1rem;
  text-transform: none;
}
</style>
