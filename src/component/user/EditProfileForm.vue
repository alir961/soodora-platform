<script setup lang="ts">
import { arrowBackOutline, checkmarkOutline, logOutOutline, trashOutline } from 'ionicons/icons';
import {
  alertController,
  IonButton,
  IonContent,
  IonIcon,
  IonInput,
  IonItem,
  IonLabel,
  modalController,
  toastController,
} from '@ionic/vue';
import { callDeleteProfile, callDeleteUserAvatar, callUpdateProfile } from '@/client/profileClient';
import { UserUpdatePayload } from '@/entity/UserUpdatePayload';
import { deleteBearerToken } from '@/store/localStorage';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/reactiveStorage';
import { computed, onMounted, ref } from 'vue';
import { User } from '@/entity/User';

const router = useRouter();

const authStore = useAuthStore();

const usernameInput = ref('');
const firstnameInput = ref('');
const lastnameInput = ref('');
const descriptionInput = ref('');
const avatarUrlInput = ref('');

const saving = ref(false);

const originalData = ref({
  username: '',
  firstname: '',
  lastname: '',
  description: '',
  avatarUrl: '',
});

const hasChanges = computed(() => {
  return (
    usernameInput.value !== originalData.value.username ||
    firstnameInput.value !== originalData.value.firstname ||
    lastnameInput.value !== originalData.value.lastname ||
    descriptionInput.value !== originalData.value.description ||
    avatarUrlInput.value !== originalData.value.avatarUrl
  );
});

const logout = async () => {
  authStore.logout();
  await deleteBearerToken();
  await router.push('/authentication');
  window.location.reload();
};

const deleteAccount = async () => {
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
  try {
    const payload: UserUpdatePayload = {
      username: usernameInput.value,
      firstname: firstnameInput.value,
      lastname: lastnameInput.value,
      description: descriptionInput.value,
      avatarUrl: avatarUrlInput.value || '',
    };

    saving.value = true;
    if (avatarUrlInput.value.trim() === '' && originalData.value.avatarUrl.trim() !== '') await callDeleteUserAvatar();
    await callUpdateProfile(payload);
    await modalController.dismiss();
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

onMounted(() => {
  const user: User | null = authStore.getAuthenticatedUser;

  if (!user) return;
  usernameInput.value = user.username;
  firstnameInput.value = user.firstname;
  lastnameInput.value = user.lastname;
  descriptionInput.value = user.description;
  avatarUrlInput.value = user.avatarUrl ?? '';

  originalData.value = {
    username: user.username,
    firstname: user.firstname,
    lastname: user.lastname,
    description: user.description,
    avatarUrl: user.avatarUrl ?? '',
  };
});
</script>

<template>
  <ion-content>
    <div class="floating-header">
      <ion-button
        class="inline-back-button"
        fill="clear"
        size="large"
        aria-label="Back"
        @click="modalController.dismiss()">
        <ion-icon :icon="arrowBackOutline" class="back-icon" />
        <span>Back</span>
      </ion-button>
      <h2>Edit Profile</h2>
      <ion-button
        v-if="hasChanges"
        :disabled="saving"
        class="inline-save-button"
        size="small"
        fill="outline"
        color="primary"
        aria-label="Save changes"
        @click="updateAccount">
        <ion-icon v-if="!saving" :icon="checkmarkOutline" class="save-button-icon" />
        <span v-if="!saving" class="save-button-text">Save</span>
      </ion-button>
    </div>
    <div id="section-content">
      <ion-item class="modal-item">
        <ion-label position="stacked">Username</ion-label>
        <ion-input placeholder="Add a username" v-model="usernameInput" />
      </ion-item>
      <ion-item class="modal-item">
        <ion-label position="stacked">First Name</ion-label>
        <ion-input placeholder="Add a first name" v-model="firstnameInput" />
      </ion-item>
      <ion-item class="modal-item">
        <ion-label position="stacked">Last Name</ion-label>
        <ion-input placeholder="Add a last name" v-model="lastnameInput" />
      </ion-item>
      <ion-item class="modal-item">
        <ion-label position="stacked">Description</ion-label>
        <ion-input placeholder="Add a description" v-model="descriptionInput" />
      </ion-item>
      <ion-item class="modal-item">
        <div class="profile-picture-url-wrapper">
          <div class="profile-picture-url-label-row">
            <span>Profile Picture URL</span>
            <ion-icon
              v-if="avatarUrlInput"
              :icon="trashOutline"
              class="profile-picture-url-delete-icon"
              @click="avatarUrlInput = ''" />
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
</template>

<style scoped>
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
  left: 0;
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
