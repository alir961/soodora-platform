<script setup lang="ts">
import { IonButton, IonCardTitle, modalController, onIonViewWillEnter, toastController } from '@ionic/vue';
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import { OAuthProvider, callRetrieveLinkedOAuthProviders, retrieveAuthenticatedProfile } from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import OAuthButton from '@/component/auth/OAuthButton.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import UserContentList from '@/component/user/UserContentList.vue';
import EditProfileForm from '@/component/user/EditProfileForm.vue';

const route = useRoute();
const linkedProviders = ref<OAuthProvider[]>([]);
const providers: OAuthProvider[] = ['google'];
const authStore = useAuthStore();

onIonViewWillEnter(async () => {
  if (route.query.error === 'oauth') {
    const toast = await toastController.create({
      message: 'Unable to connect to OAuth provider!',
      duration: 1500,
      position: 'top',
    });
    await toast.present();
  }
  await fetchUserData();
});

const fetchUserData = async () => {
  try {
    linkedProviders.value = await callRetrieveLinkedOAuthProviders();
  } catch (error) {
    console.error('Failed to fetch user data:', error);
  }
};

const openProfileEditionModal = async () => {
  const modal = await modalController.create({
    component: EditProfileForm,
  });

  await modal.present();
  await modal.onWillDismiss();

  const updatedProfile = await retrieveAuthenticatedProfile();

  authStore.authenticate(updatedProfile);
};

const openFeedbackForm = () => {
  window.open('https://tally.so/r/wAYAVl', '_blank', 'noopener');
};
</script>

<template>
  <ApplicationViewport title="Account" authenticated>
    <div class="profile-header">
      <ion-card-title class="profile-title">About You</ion-card-title>
      <ion-button size="small" fill="outline" class="edit-button" @click="openProfileEditionModal">
        Edit Profile
      </ion-button>
    </div>
    <div v-if="!authStore.getAuthenticatedUser" class="loading">
      <p>Loading...</p>
    </div>

    <ProfileComponent v-else :profile="authStore.getAuthenticatedUser" />

    <OAuthButton
      v-for="provider in providers"
      :key="provider"
      :provider="provider"
      :linked="linkedProviders.includes(provider)"
      @unlink="fetchUserData" />

    <div class="feedback-button" @click="openFeedbackForm" style="cursor: pointer">Give Us Feedback</div>

    <div v-if="authStore.getAuthenticatedUser" class="post-list">
      <UserContentList :user="authStore.getAuthenticatedUser" />
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.5em;
  padding-bottom: 0.5em;
  padding-left: 1em;
  padding-right: 1em;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.profile-title {
  font-size: 1.5em;
  font-weight: 200;
  color: var(--ion-color-medium);
}

.edit-button {
  --color: var(--ion-color-medium);
  font-size: 0.9em;
  --border-radius: 20px;
  --border-color: var(--ion-color-medium);
  --border-width: 1px;
}

.feedback-button {
  width: calc(100% - 2em);
  margin-left: 1em;
  margin-right: 1em;
  background: linear-gradient(270deg, #6a11cb, #9425fc, #6a11cb, #9425fc);
  background-size: 400% 400%;
  animation: gradientFlow 6s ease-in-out infinite;
  color: #fff;
  text-align: center;
  border-radius: 50px;
  font-weight: 300;
  padding: 0.5em 0;
  font-size: 1.3em;
}

@keyframes gradientFlow {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.post-list {
  margin-top: 1rem;
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
