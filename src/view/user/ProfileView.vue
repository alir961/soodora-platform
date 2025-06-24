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
  IonContent,
  onIonViewWillEnter,
  toastController,
} from '@ionic/vue';
import { deleteBearerToken } from '@/store/localStorage';
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { User } from '@/entity/User';
import { userIdentifierNotFoundToast } from '@/utils';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import {
  OAuthProvider,
  retrieveAuthenticatedProfile,
  callRetrieveLinkedOAuthProviders,
  callDeleteUserAvatar,
  callDeleteProfile,
  callUpdateProfile,
} from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import OAuthButton from '@/component/user/OAuthButton.vue';
import { logOutOutline, trashOutline, checkmarkOutline, arrowBackOutline } from 'ionicons/icons';
import { useAuthStore } from '@/store/reactiveStorage';
import { UserUpdatePayload } from '@/entity/UserUpdatePayload';
import customSettingsIcon from '@/assets/icons/settings.svg';

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
const saving = ref(false);

const originalData = ref({
  username: '',
  firstname: '',
  lastname: '',
  avatarUrl: '',
});

watch(showModal, (newVal) => {
  if (newVal && userData.value) {
    usernameInput.value = userData.value.username;
    firstnameInput.value = userData.value.firstname;
    lastnameInput.value = userData.value.lastname;
    avatarUrlInput.value = userData.value.avatarUrl ?? '';

    // Store original data for comparison
    originalData.value = {
      username: userData.value.username,
      firstname: userData.value.firstname,
      lastname: userData.value.lastname,
      avatarUrl: userData.value.avatarUrl ?? '',
    };
  }
});

const hasChanges = computed(() => {
  return (
    usernameInput.value !== originalData.value.username ||
    firstnameInput.value !== originalData.value.firstname ||
    lastnameInput.value !== originalData.value.lastname ||
    avatarUrlInput.value !== originalData.value.avatarUrl
  );
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
  await router.push('/authentication');
  window.location.reload();
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
            await callDeleteProfile();
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
    saving.value = true;

    if (avatarUrlInput.value === '') {
      await callDeleteUserAvatar();
    }

    const payload: UserUpdatePayload = {
      username: usernameInput.value,
      firstname: firstnameInput.value,
      lastname: lastnameInput.value,
      avatarUrl: avatarUrlInput.value || '',
    };

    await callUpdateProfile(payload);

    const freshProfile = await retrieveAuthenticatedProfile();
    authStore.authenticate(freshProfile);
    userData.value = freshProfile;
    avatarUrlInput.value = freshProfile.avatarUrl ?? '';
    showModal.value = false;
  } catch (error) {
    const toast = await toastController.create({
      message: 'Failed to update account.',
      duration: 1500,
      position: 'top',
    });
    await toast.present();
  } finally {
    saving.value = false;
  }
};

const openProfileEditionModal = () => {
  userData.value = authStore.getAuthenticatedUser;
  showModal.value = true;
};

const clearAvatarUrl = () => {
  avatarUrlInput.value = '';
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

    <ion-modal :isOpen="showModal">
      <div class="modal-content">
        <ion-content>
          <div class="floating-header">
            <ion-button
              class="inline-back-button"
              fill="clear"
              size="large"
              @click="showModal = false"
              aria-label="Back">
              <ion-icon :icon="arrowBackOutline" class="back-icon" />
              <span>Back</span>
            </ion-button>
            <h2>Edit Profile</h2>
            <ion-button
              v-if="hasChanges"
              class="inline-save-button"
              :disabled="saving"
              @click="updateAccount"
              size="small"
              fill="outline"
              color="primary"
              aria-label="Save changes">
              <ion-icon v-if="!saving" :icon="checkmarkOutline" class="save-button-icon" />
              <span v-if="!saving" class="save-button-text">Save</span>
            </ion-button>
          </div>
          <div id="section-content">
            <ion-item class="modal-item">
              <ion-label position="stacked">Username</ion-label>
              <ion-input placeholder="Add a username" v-model="usernameInput"></ion-input>
            </ion-item>
            <ion-item class="modal-item">
              <ion-label position="stacked">First Name</ion-label>
              <ion-input placeholder="Add a first name" v-model="firstnameInput"></ion-input>
            </ion-item>
            <ion-item class="modal-item">
              <ion-label position="stacked">Last Name</ion-label>
              <ion-input placeholder="Add a last name" v-model="lastnameInput"></ion-input>
            </ion-item>
            <ion-item class="modal-item">
              <div class="profile-picture-url-wrapper">
                <div class="profile-picture-url-label-row">
                  <span>Profile Picture URL</span>
                  <ion-icon
                    v-if="avatarUrlInput"
                    :icon="trashOutline"
                    class="profile-picture-url-delete-icon"
                    @click="clearAvatarUrl" />
                </div>
                <ion-input placeholder="Add a profile picture URL" v-model="avatarUrlInput" />
              </div>
            </ion-item>
          </div>
          <div class="settings-actions">
            <ion-button class="settings-logout" @click="logout">
              <ion-icon class="icon" :icon="logOutOutline" />
              Logout
            </ion-button>
            <ion-button class="settings-delete-account" @click="deleteAccount">
              <ion-icon class="icon" :icon="trashOutline" />
              Delete Account
            </ion-button>
          </div>
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

.modal-content {
  height: 100%;
}

#section-content {
  padding: 20px 40px;
}

.modal-item {
  --background: var(--ion-background-color, #fff);
  --padding-start: 0;
}

.back-icon {
  margin-right: 6px;
}

.settings-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 20px;
}

.settings-actions ion-button {
  max-width: 300px;
  width: 100%;
  font-weight: 600;
  font-size: 1rem;
  padding: 14px;
  --border-radius: 12px;
  --box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.settings-actions .icon {
  margin-right: 8px;
}

.settings-logout {
  --background: #ffffff;
  --color: rgb(255, 0, 0);
}

.settings-delete-account {
  --background: #db3030;
  --color: white;
}

.profile-picture-url-wrapper {
  margin-top: 12px;
  width: 100%;
}

.profile-picture-url-label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-picture-url-delete-icon {
  color: #db3030;
}

.floating-header {
  position: relative;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.inline-back-button {
  position: absolute;
  left: 0px;
  top: 50%;
  transform: translateY(-50%);
  --border-radius: 12px;
  min-width: 80px;
  min-height: 30px;
}

.inline-save-button {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  --border-radius: 12px;
  min-width: 80px;
  min-height: 30px;
}

.save-button-text {
  color: #fff;
  padding-left: 4px;
}

.save-button-icon {
  color: #fff;
}
</style>
