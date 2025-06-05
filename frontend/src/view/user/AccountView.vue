<script setup lang="ts">
import {
  alertController,
  IonButton,
  IonCard,
  IonIcon,
  IonModal,
  IonInput,
  IonItem,
  IonLabel,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonContent,
  onIonViewWillEnter,
  toastController,
} from '@ionic/vue';
import { deleteBearerToken } from '@/store/localStorage';
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { User } from '@/entity/User';
import { redirectExplore, userIdentifierNotFoundToast } from '@/utils';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import {
  OAuthProvider,
  retrieveAuthenticatedProfile,
  callRetrieveLinkedOAuthProviders,
  callDeleteUserAvatar,
} from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import OAuthButton from '@/view/user/OAuthButton.vue';
import { logOutOutline, trashOutline, settingsOutline } from 'ionicons/icons';
import { useAuthStore } from '@/store/reactiveStorage';
import { UserUpdatePayload } from '@/entity/UserUpdatePayload';
import { callDeleteUser, callUpdateUser } from '@/client/userClient';

const router = useRouter();
const route = useRoute();
const userData = ref<User | null>(null);
const linkedProviders = ref<OAuthProvider[]>([]);
const providers: OAuthProvider[] = ['google'];
const authStore = useAuthStore();

const showModal = ref(false);
const usernameInput = ref('');
const firstnameInput = ref('');
const lastnameInput = ref('');
const avatarUrlInput = ref('');

watch(showModal, (newVal) => {
  if (newVal && userData.value) {
    usernameInput.value = userData.value.username;
    firstnameInput.value = userData.value.firstname;
    lastnameInput.value = userData.value.lastname;
    avatarUrlInput.value = userData.value.avatarUrl ?? '';
  }
});

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
    userData.value = authStore.getAuthenticatedUser;
    linkedProviders.value = await callRetrieveLinkedOAuthProviders();
  } catch (error) {
    console.error('Failed to fetch user data:', error);
  }
};

const logout = async () => {
  await deleteBearerToken();
  authStore.logout();
  await redirectExplore(router);
};

const deleteAccount = async () => {
  const userSlug = userData.value?.slug;
  if (!userSlug) {
    const toast = await userIdentifierNotFoundToast();
    await toast.present();
    return;
  }
  const alert = await alertController.create({
    header: 'Confirm Account Deletion',
    message: 'Are you sure you want to delete your account?',
    buttons: [
      { text: 'Cancel', role: 'cancel' },
      {
        text: 'Delete',
        handler: async () => {
          try {
            await callDeleteUser(userSlug);
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

const updateAccount = async () => {
  const slug = userData.value?.slug;
  if (!slug) {
    const toast = await userIdentifierNotFoundToast();
    await toast.present();
    return;
  }
  try {
    const payload: UserUpdatePayload = {
      username: usernameInput.value,
      firstname: firstnameInput.value,
      lastname: lastnameInput.value,
      avatarUrl: avatarUrlInput.value,
    };

    await callUpdateUser(slug, payload);
    authStore.authenticate(await retrieveAuthenticatedProfile());
    showModal.value = false;
  } catch (error) {
    const toast = await toastController.create({
      message: 'Failed to update account.',
      duration: 1500,
      position: 'top',
    });

    await toast.present();
  }
};

const deleteAvatar = async () => {
  try {
    await callDeleteUserAvatar();
    await fetchUserData();
  } catch (error) {
    const toast = await toastController.create({
      message: 'Failed to delete account avatar.',
      duration: 1500,
      position: 'top',
    });

    await toast.present();
  }
  showModal.value = false;
};

const openProfileEditionModal = () => {
  userData.value = authStore.getAuthenticatedUser;
  showModal.value = true;
};
</script>

<template>
  <ApplicationViewport title="Account" authenticated>
    <template #toolbar>
      <div id="toolbar-button">
        <ion-button size="small" @click="openProfileEditionModal">
          <ion-icon :icon="settingsOutline" />
        </ion-button>
      </div>
    </template>

    <ion-modal :isOpen="showModal">
      <div class="modal-content">
        <ion-header>
          <ion-toolbar>
            <ion-title>Edit Profile</ion-title>
            <ion-button slot="end" @click="showModal = false">Close</ion-button>
          </ion-toolbar>
        </ion-header>
        <ion-content>
          <ion-item>
            <ion-label position="stacked">Username</ion-label>
            <ion-input v-model="usernameInput" />
          </ion-item>
          <ion-item>
            <ion-label position="stacked">First Name</ion-label>
            <ion-input v-model="firstnameInput" />
          </ion-item>
          <ion-item>
            <ion-label position="stacked">Last Name</ion-label>
            <ion-input v-model="lastnameInput" />
          </ion-item>
          <ion-item>
            <ion-label position="stacked">Avatar URL</ion-label>
            <ion-input v-model="avatarUrlInput" />
          </ion-item>
          <ion-button expand="block" @click="updateAccount">Save</ion-button>
          <ion-button expand="block" @click="deleteAvatar">Delete Avatar</ion-button>
        </ion-content>
      </div>
    </ion-modal>

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

    <ion-button expand="block" @click="logout" id="logout-button">
      <ion-icon class="icon" :icon="logOutOutline" />
      Logout
    </ion-button>

    <ion-button expand="block" @click="deleteAccount" id="delete-account-button">
      <ion-icon class="icon" :icon="trashOutline" />
      Delete Account
    </ion-button>
  </ApplicationViewport>
</template>

<style scoped>
#toolbar-button {
  width: 25%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;

  & ion-button {
    --border-radius: 15px;

    height: 70%;
    width: 50%;
  }
}

.modal-content {
  height: 100%;

  & ion-button {
    margin: 20px;
  }
}

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

.icon {
  margin-right: 10px;
}
</style>
