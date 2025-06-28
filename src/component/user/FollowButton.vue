<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';
import { IonButton } from '@ionic/vue';

defineProps({
  canFollow: { type: Boolean, required: true },
  isFollowing: { type: Boolean, required: true },
});

const emit = defineEmits(['toggle']);

const toggleFollow = () => {
  emit('toggle');
};
</script>

<template>
  <div v-if="canFollow" class="follow-button">
    <ion-button fill="outline" size="small" color="danger" @click="toggleFollow">
      {{ isFollowing ? 'Unfollow' : 'Follow' }}
    </ion-button>
  </div>
</template>

<style scoped>
.follow-button {
  margin-left: auto;
}
</style>
