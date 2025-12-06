<script setup lang="ts">
const props = defineProps({
  duration: {
    required: true,
    type: Number,
  },
  sales: {
    required: true,
    type: Number,
  },
});

function formatDuration(): string {
  const diffMs = props.duration;
  const diffMinutes = Math.floor(diffMs / (1000 * 60));

  if (diffMinutes < 1 && props.sales <= 0) return 'no sales yet...';

  if (diffMinutes < 1) return 'instantly';
  if (diffMinutes < 60) return `${diffMinutes}m`;

  const diffHours = Math.floor(diffMinutes / 60);
  if (diffHours < 24) return `${diffHours}h`;

  const diffDays = Math.floor(diffHours / 24);
  if (diffDays < 8) return `${diffDays}d`;

  const diffMonths = Math.floor(diffDays / 30);
  if (diffMonths < 12) return `${diffMonths}mo`;

  return 'more than a year';
}
</script>

<template>
  <span>{{ formatDuration() }}</span>
</template>
