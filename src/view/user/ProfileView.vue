<script setup lang="ts">
import { IonButton, IonCard, IonIcon, modalController, onIonViewWillEnter, toastController } from '@ionic/vue';
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import { OAuthProvider, callRetrieveLinkedOAuthProviders, retrieveAuthenticatedProfile } from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import OAuthButton from '@/component/auth/OAuthButton.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import customSettingsIcon from '@/assets/icons/settings.svg';
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
</script>

<template>
  <ApplicationViewport title="Account" authenticated>
    <template #toolbar>
      <div id="toolbar-button">
        <ion-button size="default" @click="openProfileEditionModal" fill="clear">
          <ion-icon :src="customSettingsIcon" style="width: 25px; height: 25px" />
        </ion-button>
      </div>
    </template>

    <div v-if="!authStore.getAuthenticatedUser" class="loading">
      <p>Loading...</p>
    </div>

    <ion-card v-else>
      <ProfileComponent :profile="authStore.getAuthenticatedUser" />
    </ion-card>

    <OAuthButton
      v-for="provider in providers"
      :key="provider"
      :provider="provider"
      :linked="linkedProviders.includes(provider)"
      @unlink="fetchUserData" />

    <div v-if="authStore.getAuthenticatedUser" class="post-list">
      <UserContentList :user="authStore.getAuthenticatedUser" />
    </div>

    <EditProfileForm />
  </ApplicationViewport>
</template>

<style scoped>
#toolbar-button {
  display: flex;
  align-items: center;
  height: 100%;
}

#toolbar-button ion-button {
  --border-radius: 15px;
  --background: transparent;
  --color: #fff;
}

.post-list {
  margin-top: 2rem !important;
}
</style>
