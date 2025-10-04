<script setup lang="ts">
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { computed, onMounted, ref, Ref } from 'vue';
import { closeOutline } from 'ionicons/icons';
import { IonButton, IonCardTitle, IonIcon } from '@ionic/vue';
import { useRoute, useRouter } from 'vue-router';
import { User } from '@/entity/User';
import { callRetrieveUser } from '@/client/userClient';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import UserContentList from '@/component/user/UserContentList.vue';

const route = useRoute();
const router = useRouter();

const user = ref(null) as Ref<User | null>;
const showFollowList = ref<{ showing: boolean; type?: 'followers' | 'following' }>({ showing: false });
const profileComponentRef = ref<any>(null);

onMounted(async () => {
  const userSlug = route.params.slug as string;

  try {
    if (userSlug) user.value = await callRetrieveUser(userSlug);
  } catch (error) {
    console.error(error);
  }
});

const handleListStateChange = (state: { showing: boolean; type?: 'followers' | 'following' }) => {
  showFollowList.value = state;
};

const closeFollowList = () => {
  profileComponentRef.value?.closeList();
  showFollowList.value = { showing: false };
};

const headerTitle = computed(() => {
  if (showFollowList.value.showing) {
    return showFollowList.value.type === 'followers' ? 'Followers' : 'Following';
  }
  return 'About Me';
});

const handleBackButton = () => {
  if (showFollowList.value.showing) {
    closeFollowList();
  } else {
    router.back();
  }
};
</script>

<template>
  <ApplicationViewport title="User Profile" back-button>
    <div class="profile-header">
      <ion-card-title class="profile-title">{{ headerTitle }}</ion-card-title>
      <ion-button 
        size="small" 
        fill="outline" 
        class="back-button" 
        @click="handleBackButton">
        <ion-icon slot="start" :icon="closeOutline" />
        Back
      </ion-button>
    </div>
    
    <ProfileComponent 
      v-if="user" 
      ref="profileComponentRef"
      :profile="user"
      @list-state-change="handleListStateChange" />
    
    <UserContentList v-if="user && !showFollowList.showing" :user="user" />
    
    <div v-if="!user" class="loading">
      <p>Loading...</p>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.profile-title {
  font-size: 1.4rem;
  font-weight: 200;
  color: var(--ion-color-medium);
}

.back-button {
  --color: var(--ion-color-medium);
  font-size: 1rem;
  --border-radius: 20px;
  --border-color: var(--ion-color-medium);
  --border-width: 1px;
  --background-activated: rgb(93, 99, 108);
  --color-activated: rgba(0, 0, 0, 0.87);
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
