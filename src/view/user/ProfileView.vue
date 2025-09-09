<script setup lang="ts">
import { IonButton, IonCardTitle, modalController, onIonViewWillEnter } from '@ionic/vue';
import { computed, ref } from 'vue';
import { useRoute } from 'vue-router';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import { OAuthProvider, callRetrieveLinkedOAuthProviders, retrieveAuthenticatedProfile } from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import OAuthButton from '@/component/auth/OAuthButton.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import UserContentList from '@/component/user/UserContentList.vue';
import EditProfileForm from '@/component/user/EditProfileForm.vue';
import { User } from '@/entity/User';
import { loadBearerToken } from '@/store/localStorage';
import { showToast } from '@/utils';

const route = useRoute();
const linkedProviders = ref<OAuthProvider[]>([]);
const providers: OAuthProvider[] = ['google'];
const authStore = useAuthStore();

onIonViewWillEnter(async () => {
  if (route.query.error === 'oauth') {
    await showToast('Unable to connect to OAuth provider!', 'danger');
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

const openAdministrationPanel = async () => {
  const panelUrl = import.meta.env.VITE_ADMINISTRATION_URL;
  const token = await loadBearerToken();

  if (!panelUrl) return;
  window.open(`${panelUrl}?token=${token}`, '_blank', 'noopener');
};

const isAdministrator = computed(() => {
  const user: User | null = authStore.getAuthenticatedUser;

  if (!user) return false;
  return user.roles.includes('ADMIN');
});
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

    <div class="button feedback-button" @click="openFeedbackForm"><span>Give Us Feedback</span></div>

    <div v-if="isAdministrator" class="button administration-button" @click="openAdministrationPanel">
      <span>Administration Panel</span>
    </div>

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
  padding: 0.5rem 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);

  & .profile-title {
    font-size: 1.4rem;
    font-weight: 200;
    color: var(--ion-color-medium);
  }

  & .edit-button {
    --color: var(--ion-color-medium);
    font-size: 1rem;
    --border-radius: 20px;
    --border-color: var(--ion-color-medium);
    --border-width: 1px;
  }
}

.button {
  width: 70%;
  margin: 0 auto;
  padding: 0.4rem 1rem;
  text-align: center;
  border-radius: 15px;
  font-weight: bold;
  font-size: 1.2em;
  color: #fff;
  cursor: pointer;

  & > span {
    height: 24px;
  }
}

.feedback-button {
  background: linear-gradient(270deg, #6a11cb, #9425fc, #6a11cb, #9425fc);
  background-size: 400% 400%;
  animation: gradientFlow 6s ease-in-out infinite;
}

.administration-button {
  background-color: red;
  margin-top: 0.8rem;
}

@keyframes gradientFlow {
  0% {
    background-position: 0 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0 50%;
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
