<script setup lang="ts">
import { alertController, IonButton, IonCard, IonIcon, onIonViewWillEnter, toastController } from '@ionic/vue';
import { deleteBearerToken, deleteUserData } from '@/store/localStorage';
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { User } from '@/entity/User';
import { redirectExplore } from '@/utils';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import {
  deleteUserAccount,
  OAuthProvider,
  retrieveAuthenticatedProfile,
  retrieveLinkedOAuthProviders,
} from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import OAuthButton from '@/view/user/OAuthButton.vue';
import { logOutOutline, trashOutline } from 'ionicons/icons';
import { useAuthStore } from '@/store/reactiveStorage';

const route = useRoute();
const router = useRouter();
const userData = ref<User | null>(null);
const linkedProviders = ref<OAuthProvider[]>([]);
const providers: OAuthProvider[] = ['google'];
const authStore = useAuthStore();

const fetchUserData = async () => {
  try {
    userData.value = await retrieveAuthenticatedProfile();
    linkedProviders.value = await retrieveLinkedOAuthProviders();
  } catch (error) {
    console.error('Failed to fetch user data:', error);
  }
};

const logout = async () => {
  await deleteBearerToken();
  await deleteUserData();
  authStore.setAuthenticated(false);
  await redirectExplore(router);
};

const deleteAccount = async () => {
  const userSlug = userData.value?.slug;

  if (!userSlug) {
    const toast = await toastController.create({
      message: 'User identifier not found.',
      duration: 1500,
      position: 'top',
    });
    await toast.present();
    return;
  }

  const alert = await alertController.create({
    header: 'Confirm Account Deletion',
    message: 'Are you sure you want to delete your account?',
    buttons: [
      {
        text: 'Cancel',
        role: 'cancel',
      },
      {
        text: 'Delete',
        handler: async () => {
          try {
            await deleteUserAccount(userSlug);
            await logout();
          } catch (error) {
            const toast = await toastController.create({
              message: 'Failed to delete account.',
              duration: 1500,
              position: 'top',
            });
            await toast.present();
          }
        },
      },
    ],
  });

  await alert.present();
};

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
</script>

<template>
  <ApplicationViewport title="Account" authenticated>
    <div v-if="!userData" class="loading">
      <p>Loading...</p>
    </div>

    <ion-card v-else>
      <ProfileComponent :profile="userData" />
    </ion-card>

    <OAuthButton
      v-for="provider in providers"
      :key="provider"
      :provider="provider"
      :linked="linkedProviders.includes(provider)"
      @unlink="fetchUserData" />

    <ion-button expand="block" @click="logout" id="logout-button">
      <ion-icon :icon="logOutOutline" />
      Logout
    </ion-button>

    <ion-button expand="block" @click="deleteAccount" id="delete-account-button">
      <ion-icon :icon="trashOutline" />
      Delete Account
    </ion-button>
  </ApplicationViewport>
</template>

<style scoped>
.loading {
  text-align: center;
  font-size: 2em;
  color: #8c8c8c;
  font-weight: 800;
}

ion-card {
  border-radius: 20px;
  margin: 0 20px 20px 20px;
}

#logout-button {
  --background: #cc2222;
  --color: white;
  border-radius: 20px;
  margin: 0 20px 20px 20px;
}

#delete-account-button {
  --background: #ff3b30;
  --color: white;
  border-radius: 20px;
  margin: 0 20px 20px 20px;
}

ion-icon {
  margin-right: 10px;
}
</style>
