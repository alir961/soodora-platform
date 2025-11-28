<script setup lang="ts">
import { computed } from 'vue';
import { personOutline, trashOutline } from 'ionicons/icons';
import ActionsPopup, { type ActionItem } from '@/component/actions/ActionsPopup.vue';

interface Props {
  isOpen: boolean;
  x: number;
  y: number;
  showRemove: boolean;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  viewArtist: [];
  removeTrack: [];
  close: [];
}>();

const handleViewArtist = () => {
  emit('viewArtist');
};

const handleRemoveTrack = () => {
  emit('removeTrack');
};

const actionItems = computed<ActionItem[]>(() => {
  const items: ActionItem[] = [
    {
      icon: personOutline,
      label: 'View Artist',
      action: handleViewArtist,
    },
  ];

  if (props.showRemove) {
    items.push({
      icon: trashOutline,
      label: 'Remove from Playlist',
      action: handleRemoveTrack,
      destructive: true,
    });
  }

  return items;
});
</script>

<template>
  <ActionsPopup :is-open="isOpen" :x="x" :y="y" :actions="actionItems" @close="emit('close')" />
</template>
