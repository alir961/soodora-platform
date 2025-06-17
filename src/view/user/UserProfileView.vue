<script setup lang="ts">
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { onMounted, ref, Ref } from 'vue';
import { useRoute } from 'vue-router';
import { User } from '@/entity/User';
import { callRetrieveUser } from '@/client/userClient';
import ProfileComponent from '@/component/user/ProfileComponent.vue';

const route = useRoute();

const user = ref(null) as Ref<User | null>;

onMounted(async () => {
  const userSlug = route.params.slug as string;

  try {
    if (userSlug) user.value = await callRetrieveUser(userSlug);
  } catch (error) {
    console.error(error);
  }
});
</script>

<template>
  <ApplicationViewport title="User Profile" back-button>
    <ProfileComponent v-if="user" :profile="user" />
    <div v-else class="loading">
      <p>Loading...</p>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.loading {
  text-align: center;
}
</style>
