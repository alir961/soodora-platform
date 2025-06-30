<script setup lang="ts">
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { onMounted, ref, Ref } from 'vue';
import { arrowBackOutline } from 'ionicons/icons';
import { IonIcon } from '@ionic/vue';
import { useRoute } from 'vue-router';
import { User } from '@/entity/User';
import { callRetrieveUser } from '@/client/userClient';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import UserContentList from '@/component/user/UserContentList.vue';

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
    <div class="top-bar">
      <ion-icon :icon="arrowBackOutline" class="back-btn" style="cursor: pointer" @click="$router.back()" />
      <span class="top-bar-title">About Me</span>
      <ion-icon class="back-btn" style="visibility: hidden" />
    </div>
    <ProfileComponent v-if="user" :profile="user" />
    <UserContentList v-if="user" :user="user" />
    <div v-else class="loading">
      <p>Loading...</p>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.top-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding-left: 1em;
  padding-right: 1em;
  padding-top: 0.5em;
  padding-bottom: 0.5em;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.back-btn {
  color: var(--ion-color-light);
  font-size: 1.5em;
}

.top-bar-title {
  font-size: 1.2em;
  font-weight: bold;
  color: var(--ion-color-light);
  margin: 0 auto;
}

.loading {
  text-align: center;
  font-size: 1.3em;
  padding-top: 3em;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
</style>
