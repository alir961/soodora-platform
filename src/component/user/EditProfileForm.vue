<script setup lang="ts">
import { checkmarkOutline, logOutOutline, trashOutline, cameraOutline, pencilOutline } from 'ionicons/icons';
import { alertController, IonIcon } from '@ionic/vue';
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
const emit = defineEmits(['close']);

const usernameInput = ref('');
const firstnameInput = ref('');
const lastnameInput = ref('');
const emailInput = ref('');
const descriptionInput = ref('');
const avatarFile = ref<File | null>(null);
const avatarPreview = ref<string>('');

const saving = ref(false);

const originalData = ref({
  username: '',
  firstname: '',
  lastname: '',
  email: '',
  description: '',
});

const hasChanges = computed(() => {
  return (
    usernameInput.value !== originalData.value.username ||
    firstnameInput.value !== originalData.value.firstname ||
    lastnameInput.value !== originalData.value.lastname ||
    emailInput.value !== originalData.value.email ||
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
    message: 'Are you sure you want to delete your account? This cannot be undone.',
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
    await showToast('Profile updated successfully!');
    emit('close');
  } catch (error: unknown) {
    const message = 'Failed to update your profile. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    saving.value = false;
  }
};

const onAvatarSelected = (file: File) => {
  avatarFile.value = file;
  const reader = new FileReader();
  reader.onload = (e) => {
    avatarPreview.value = e.target?.result as string;
  };
  reader.readAsDataURL(file);
};

const triggerFileInput = () => {
  const input = document.getElementById('avatar-upload') as HTMLInputElement;
  input?.click();
};

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (file) {
    onAvatarSelected(file);
  }
};

onMounted(() => {
  const user: User | null = authStore.getAuthenticatedUser;
  if (!user) return;

  usernameInput.value = user.username;
  firstnameInput.value = user.firstname;
  lastnameInput.value = user.lastname;
  emailInput.value = user.email;
  descriptionInput.value = user.description || '';
  
  const avatarUrl = (user as any).avatarUrl || (user as any).profilePicture || (user as any).picture || (user as any).image;
  
  if (avatarUrl) {
    if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://') || avatarUrl.startsWith('data:')) {
      avatarPreview.value = avatarUrl;
    } else {
      avatarPreview.value = `${import.meta.env.VITE_API_URL}${avatarUrl}`;
    }
  }

  originalData.value = {
    username: user.username,
    firstname: user.firstname,
    lastname: user.lastname,
    email: user.email,
    description: user.description || '',
  };
});
</script>

<template>
  <div class="edit-container">
    <div class="profile-header">
      <div class="avatar-wrapper" @click="triggerFileInput">
        <div class="avatar-preview">
          <img 
            v-if="avatarPreview" 
            :src="avatarPreview" 
            alt="Profile avatar"
            class="avatar-img"
          />
          <div v-else class="avatar-placeholder">
            <span>{{ usernameInput.charAt(0).toUpperCase() || '?' }}</span>
          </div>
          <div class="avatar-badge">
            <ion-icon :icon="cameraOutline" />
          </div>
        </div>
        <input 
          id="avatar-upload"
          type="file" 
          accept="image/*" 
          @change="handleFileChange"
          style="display: none"
        />
      </div>
      
      <div class="username-section">
        <div class="username-wrapper">
          <span class="at-symbol">@</span>
          <input 
            v-model="usernameInput" 
            class="username-input"
            placeholder="username"
            autocomplete="off"
            autocorrect="off"
            autocapitalize="off"
            spellcheck="false"
            @contextmenu.prevent
          />
          <ion-icon :icon="pencilOutline" class="edit-icon" />
        </div>
      </div>
    </div>

    <div class="form-section">
      <div class="field-row">
        <div class="field">
          <label>Email</label>
          <input 
            v-model="emailInput" 
            type="email" 
            placeholder="you@example.com"
            @contextmenu.prevent
          />
        </div>
        <div class="field">
          <label>Firstname</label>
          <input 
            v-model="firstnameInput" 
            placeholder="John"
            @contextmenu.prevent
          />
        </div>
      </div>

      <div class="field-row">
        <div class="field">
          <label>Lastname</label>
          <input 
            v-model="lastnameInput" 
            placeholder="Doe"
            @contextmenu.prevent
          />
        </div>
      </div>

      <div class="field">
        <label>Biography</label>
        <textarea
          v-model="descriptionInput"
          placeholder="Tell us about yourself..."
          rows="4"
          @contextmenu.prevent
        ></textarea>
      </div>

      <button 
        v-if="hasChanges"
        :disabled="saving"
        class="save-btn" 
        @click="updateAccount">
        <ion-icon :icon="checkmarkOutline" />
        {{ saving ? 'Saving...' : 'Save Changes' }}
      </button>
    </div>

    <div class="actions">
      <button class="action-btn logout" @click="logout">
        <ion-icon :icon="logOutOutline" />
        Logout
      </button>
      <button class="action-btn delete" @click="deleteAccount">
        <ion-icon :icon="trashOutline" />
        Delete Account
      </button>
    </div>
  </div>
</template>

<style scoped>
.edit-container {
  padding: 1rem;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 18px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.avatar-wrapper {
  cursor: pointer;
  position: relative;
  flex-shrink: 0;
}

.avatar-preview {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(59, 130, 246, 0.3);
  transition: border-color 0.2s ease;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 700;
  color: #ffffff;
}

.avatar-badge {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  border: 2px solid #0b0b0b;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
}

.avatar-badge ion-icon {
  font-size: 16px;
}

.username-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.username-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 12px;
  padding: 12px 14px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.username-wrapper:focus-within {
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow: 0 0 0 6px rgba(59, 130, 246, 0.1);
}

.at-symbol {
  color: #60a5fa;
  font-weight: 700;
  font-size: 18px;
  margin-right: 4px;
  flex-shrink: 0;
}

.username-input {
  appearance: none;
  background: transparent;
  border: 0;
  outline: none;
  color: #ffffff;
  font-size: 18px;
  font-weight: 600;
  flex: 1;
  padding: 0;
  min-width: 0;
}

.username-input::placeholder {
  color: #7f7f7f;
}

.edit-icon {
  color: #9aa3ad;
  font-size: 18px;
  flex-shrink: 0;
  margin-left: 8px;
}

@media (max-width: 600px) {
  .profile-header {
    gap: 12px;
  }
  
  .avatar-preview {
    width: 70px;
    height: 70px;
  }
  
  .avatar-placeholder {
    font-size: 28px;
  }
  
  .avatar-badge {
    width: 24px;
    height: 24px;
  }
  
  .avatar-badge ion-icon {
    font-size: 14px;
  }
}

.form-section {
  display: grid;
  gap: 16px;
  max-width: 600px;
  margin: 0 auto 24px auto;
}

.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

@media (max-width: 600px) {
  .field-row {
    grid-template-columns: 1fr;
  }
}

.field {
  display: grid;
  gap: 6px;
}

.field label {
  color: #cfcfcf;
  font-size: 12px;
  letter-spacing: 0.02em;
  padding-left: 4px;
  font-weight: 600;
}

.field input,
.field textarea {
  appearance: none;
  outline: none;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: color-mix(in oklab, #0b0b0b 75%, #0f0f0f);
  color: #fff;
  padding: 12px 14px;
  border-radius: 12px;
  font-size: 15px;
  font-family: inherit;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.field input::placeholder,
.field textarea::placeholder {
  color: #7f7f7f;
}

.field input:focus,
.field textarea:focus {
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow: 0 0 0 6px rgba(59, 130, 246, 0.1), 0 8px 24px rgba(0, 0, 0, 0.25);
}

.field textarea {
  resize: vertical;
  min-height: 80px;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 600px;
  margin: 0 auto;
}

.action-btn {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.04);
  color: #fff;
  font-weight: 600;
  font-size: 15px;
  padding: 14px 20px;
  border-radius: 12px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: background 0.2s ease, border-color 0.2s ease, transform 0.06s ease;
}

.action-btn:active {
  transform: translateY(1px);
}

.action-btn ion-icon {
  font-size: 20px;
}

.action-btn.logout {
  color: #ffa500;
  border-color: rgba(255, 165, 0, 0.3);
}

.action-btn.delete {
  color: #ff4444;
  border-color: rgba(255, 68, 68, 0.3);
}

.save-btn {
  width: 100%;
  height: 48px;
  border: 0;
  border-radius: 12px;
  cursor: pointer;
  color: #fff;
  font-weight: 700;
  letter-spacing: 0.01em;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  box-shadow: 0 10px 30px rgba(37, 99, 235, 0.35), inset 0 1px 0 rgba(255, 255, 255, 0.12);
  transition: transform 0.06s ease, filter 0.2s ease, box-shadow 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 8px;
}

.save-btn:active {
  transform: translateY(1px);
}

.save-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.save-btn ion-icon {
  font-size: 20px;
}
</style>
