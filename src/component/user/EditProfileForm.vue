<script setup lang="ts">
import { arrowBackOutline, checkmarkOutline, cloudDownloadOutline, logOutOutline, trashOutline } from 'ionicons/icons';
import {
  alertController,
  IonButton,
  IonContent,
  IonIcon,
  IonInput,
  IonItem,
  IonLabel,
  IonTextarea,
  modalController,
} from '@ionic/vue';
import { callDeleteProfile, callUpdateProfile } from '@/client/profileClient';
import { UserUpdatePayload } from '@/entity/UserUpdatePayload';
import { deleteBearerToken } from '@/store/localStorage';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/reactiveStorage';
import { computed, onMounted, ref } from 'vue';
import { User } from '@/entity/User';
import { showToast } from '@/utils';

const router = useRouter();

const authStore = useAuthStore();

const usernameInput = ref('');
const firstnameInput = ref('');
const lastnameInput = ref('');
const descriptionInput = ref('');
const avatarFile = ref<File | null>(null);

const saving = ref(false);

const originalData = ref({
  username: '',
  firstname: '',
  lastname: '',
  description: '',
});

const hasChanges = computed(() => {
  return (
    usernameInput.value !== originalData.value.username ||
    firstnameInput.value !== originalData.value.firstname ||
    lastnameInput.value !== originalData.value.lastname ||
    descriptionInput.value !== originalData.value.description ||
    avatarFile.value !== null
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
        role: 'destructive',
        handler: async () => {
          try {
            await callDeleteProfile();
            await logout();
          } catch (error: unknown) {
            const message = 'Failed to delete your account. ' + (error instanceof Error ? error.message : '');
            await showToast(message, 'danger');
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
    };

    const formData = new FormData();
    const blob = new Blob([JSON.stringify(payload)], { type: 'application/json' });
    formData.append('data', blob);

    if (avatarFile.value) formData.append('file', avatarFile.value);

    saving.value = true;
    await callUpdateProfile(formData);
    await modalController.dismiss();
    await showToast('Succeed to update your profile');
  } catch (error: unknown) {
    const message = 'Failed to update your profile. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    saving.value = false;
  }
};

const onAvatarSelected = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) avatarFile.value = target.files[0];
};

onMounted(() => {
  const user: User | null = authStore.getAuthenticatedUser;
  if (!user) return;

  usernameInput.value = user.username;
  firstnameInput.value = user.firstname;
  lastnameInput.value = user.lastname;
  descriptionInput.value = user.description;

  originalData.value = {
    username: user.username,
    firstname: user.firstname,
    lastname: user.lastname,
    description: user.description,
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
      <h2 class="floating-header-title">Edit Profile</h2>
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
        <ion-textarea placeholder="Add a description" v-model="descriptionInput" auto-grow />
      </ion-item>
      <ion-item class="modal-item">
        <IonLabel position="stacked">Select Image</IonLabel>
        <div class="file-upload-container">
          <IonLabel class="file-upload-button">
            <ion-icon :icon="cloudDownloadOutline" />
            Choose File
            <input type="file" @change="onAvatarSelected" accept="image/*" />
          </IonLabel>
          <span v-if="avatarFile" class="file-name">{{ avatarFile.name }}</span>
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
  height: 20px;
  width: 20px;
  padding-bottom: 0.1em;
  margin-right: 0.2em;
  color: var(--ion-color-primary);
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

.floating-header {
  position: relative;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding-top: 3em;
  padding-bottom: 1em;
  margin-bottom: 1.5em;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.floating-header-title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 1.5em;
  color: var(--ion-color-light);
  z-index: 1;
  margin: 0;
}

.inline-back-button {
  font-size: 1.2em;
  color: var(--ion-color-primary);
  padding: 0 0.5em;
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

.file-upload-container {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
  margin-top: 0.75rem;
}

.file-upload-button {
  display: inline-block;
  padding: 8px 15px;
  background-color: var(--ion-color-secondary);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.file-upload-button input[type='file'] {
  position: absolute;
  left: 0;
  top: 0;
  opacity: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.file-name {
  margin-top: 5px;
  font-size: 0.9em;
  color: #555;
}
</style>
