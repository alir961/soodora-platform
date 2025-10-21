<script setup lang="ts">
import { IonButton, IonCardTitle, IonIcon, onIonViewWillEnter } from '@ionic/vue';
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
import { closeOutline } from 'ionicons/icons';
import { cogOutline } from 'ionicons/icons';

const route = useRoute();
const linkedProviders = ref<OAuthProvider[]>([]);
const providers: OAuthProvider[] = ['google'];
const authStore = useAuthStore();
const showEditForm = ref(false);
const showFollowList = ref<{ showing: boolean; type?: 'followers' | 'following' }>({ showing: false });
const profileComponentRef = ref<InstanceType<typeof ProfileComponent> | null>(null);

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

const openProfileEditor = () => {
  showEditForm.value = true;
};

const closeProfileEditor = async () => {
  showEditForm.value = false;
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

const handleListStateChange = (state: { showing: boolean; type?: 'followers' | 'following' }) => {
  showFollowList.value = state;
};

const closeFollowList = () => {
  profileComponentRef.value?.closeList();
  showFollowList.value = { showing: false };
};

const headerTitle = computed(() => {
  if (showEditForm.value) return 'About You';
  if (showFollowList.value.showing) {
    return showFollowList.value.type === 'followers' ? 'Followers' : 'Following';
  }
  return 'About You';
});

const headerButtonAction = computed(() => {
  if (showFollowList.value.showing) return closeFollowList;
  return showEditForm.value ? closeProfileEditor : openProfileEditor;
});

const headerButtonIcon = computed(() => {
  if (showFollowList.value.showing || showEditForm.value) return closeOutline;
  return cogOutline;
});

const headerButtonText = computed(() => {
  if (showFollowList.value.showing) return 'Back';
  return showEditForm.value ? 'Cancel' : 'Settings';
});
</script>

<template>
  <ApplicationViewport title="Account" authenticated>
    <div class="profile-header">
      <ion-card-title class="profile-title">{{ headerTitle }}</ion-card-title>
      <ion-button size="small" fill="outline" class="edit-button" @click="headerButtonAction">
        <ion-icon slot="start" :icon="headerButtonIcon" />
        {{ headerButtonText }}
      </ion-button>
    </div>

    <div v-if="!authStore.getAuthenticatedUser" class="loading">
      <p>Loading...</p>
    </div>

    <div v-if="authStore.getAuthenticatedUser" class="content-wrapper">
      <transition name="fade" mode="out-in">
        <div v-if="!showEditForm" key="profile-view">
          <ProfileComponent
            ref="profileComponentRef"
            :profile="authStore.getAuthenticatedUser"
            @list-state-change="handleListStateChange" />

          <template v-if="!showFollowList.showing">
            <OAuthButton
              class="button google-button"
              v-for="provider in providers"
              :key="provider"
              :provider="provider"
              :linked="linkedProviders.includes(provider)"
              @unlink="fetchUserData" />

            <div v-if="!isAdministrator" class="button feedback-button" @click="openFeedbackForm">
              <span>Give Us Feedback</span>
            </div>

            <div v-if="isAdministrator" class="button administration-button" @click="openAdministrationPanel">
              <span>Admin Panel</span>
            </div>

            <div class="post-list">
              <UserContentList :user="authStore.getAuthenticatedUser" />
            </div>
          </template>
        </div>

        <EditProfileForm v-else key="edit-form" @close="closeProfileEditor" />
      </transition>
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
    --background-activated: rgb(93, 99, 108);
    --color-activated: rgba(0, 0, 0, 0.87);
  }
}

.content-wrapper {
  position: relative;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.google-button,
.feedback-button,
.administration-button {
  width: 90%;
  margin: 0 auto;
  padding: 0.4rem 1rem;
  text-align: center;
  border-radius: 15px;
  font-weight: bold;
  font-size: 1.2em;
  color: #fff;
  cursor: pointer;
}

.feedback-button > span,
.administration-button > span {
  height: 24px;
}

.google-button {
  padding: 0 !important;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  line-height: 1;
  background: linear-gradient(270deg, #232a36 0%, #273042 25%, #32405a 50%, #273042 75%, #232a36 100%);
  background-size: 400% 400%;
  animation: gradientFlow 6s ease-in-out infinite;
}

.feedback-button {
  background: linear-gradient(270deg, #1b406b 0%, #235080 25%, #2e6fb2 50%, #235080 75%, #1b406b 100%);
  background-size: 400% 400%;
  animation: gradientFlow 6s ease-in-out infinite;
  border: 1px solid rgba(255, 255, 255, 0.12);
  margin-top: 0.8rem;
}

.administration-button {
  background: linear-gradient(270deg, #6b2323 0%, #7a2929 25%, #b23a3a 50%, #7a2929 75%, #6b2323 100%);
  background-size: 400% 400%;
  animation: gradientFlow 6s ease-in-out infinite;
  border: 1px solid rgba(255, 255, 255, 0.12);
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
