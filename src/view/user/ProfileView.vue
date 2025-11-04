<script setup lang="ts">
import { IonIcon, onIonViewWillEnter } from '@ionic/vue';
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import { OAuthProvider, callRetrieveLinkedOAuthProviders, retrieveAuthenticatedProfile } from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import OAuthButton from '@/component/auth/OAuthButton.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import UserContentList from '@/component/user/UserContentList.vue';
import EditProfileForm from '@/component/user/EditProfileForm.vue';
import { User } from '@/entity/User';
import { loadBearerToken, deleteBearerToken } from '@/store/localStorage';
import { showToast } from '@/utils';
import {
  closeOutline,
  personCircleOutline,
  ellipsisHorizontal,
  settingsOutline,
  logOutOutline,
  linkOutline,
  briefcaseOutline,
  chatbubblesOutline,
} from 'ionicons/icons';
import ActionsPopup, { type ActionItem } from '@/component/actions/ActionsPopup.vue';
import { getMediaKitByArtistSlug } from '@/client/mediaKitClient';
import { getUserLinkPage } from '@/client/linkPageClient';

const route = useRoute();
const router = useRouter();
const linkedProviders = ref<OAuthProvider[]>([]);
const providers: OAuthProvider[] = ['google'];
const authStore = useAuthStore();
const showEditForm = ref(false);
const showFollowList = ref<{ showing: boolean; type?: 'followers' | 'following' }>({ showing: false });
const profileComponentRef = ref<InstanceType<typeof ProfileComponent> | null>(null);
const showActionsPopup = ref(false);
const popupX = ref(0);
const popupY = ref(0);

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

const openSettingsPopup = (event: MouseEvent) => {
  const button = event.currentTarget as HTMLElement;
  const rect = button.getBoundingClientRect();
  popupX.value = rect.right - 220; // Align right edge of popup with right edge of button
  popupY.value = rect.bottom + 8; // 8px below the button
  showActionsPopup.value = true;
};

const closeActionsPopup = () => {
  showActionsPopup.value = false;
};

const handleSettings = () => {
  showEditForm.value = true;
};

const handleSignOut = async () => {
  authStore.logout();
  await deleteBearerToken();
  await router.push('/authentication');
  window.location.reload();
};

const handleLinkHub = async () => {
  const user = authStore.getAuthenticatedUser;
  if (!user) return;

  try {
    const linkPage = await getUserLinkPage(user.slug);
    if (linkPage) {
      router.push(`/linkpages/${linkPage.slug}`);
      return;
    }
  } catch (error) {
    // User doesn't have a link page yet, redirect to create
  }
  router.push('/linkpages');
};

const handleMediaKit = async () => {
  const user = authStore.getAuthenticatedUser;
  if (!user) return;

  try {
    if (user.roles.includes('ARTIST')) {
      const mediaKit = await getMediaKitByArtistSlug(user.slug);
      if (mediaKit) {
        router.push(`/mediakits/${mediaKit.slug}`);
        return;
      }
    }
  } catch (error) {
    // User doesn't have a mediakit yet, redirect to create
  }
  router.push('/mediakits/create');
};

const handleQuotes = () => {
  router.push('/quotes/me');
};

const canCreateContent = computed(() => {
  const user = authStore.getAuthenticatedUser;
  if (!user) return false;
  return user.roles.some((role) => ['ADMIN', 'PREMIUM', 'ARTIST'].includes(role));
});

const actionItems = computed<ActionItem[]>(() => {
  const items: ActionItem[] = [];

  // Only show artist/premium features if user has appropriate role
  if (canCreateContent.value) {
    items.push(
      {
        icon: linkOutline,
        label: 'Link Hub',
        action: handleLinkHub,
      },
      {
        icon: briefcaseOutline,
        label: 'Soodora Kit',
        action: handleMediaKit,
      },
      {
        icon: chatbubblesOutline,
        label: 'My Quotes',
        action: handleQuotes,
      }
    );
  }

  // Always show Settings and Sign Out
  items.push(
    {
      icon: settingsOutline,
      label: 'Settings',
      action: handleSettings,
    },
    {
      icon: logOutOutline,
      label: 'Sign Out',
      action: handleSignOut,
      destructive: true,
    }
  );

  return items;
});

const headerTitle = computed(() => {
  if (showEditForm.value) return 'About You';
  if (showFollowList.value.showing) {
    return showFollowList.value.type === 'followers' ? 'Followers' : 'Following';
  }
  return 'About You';
});

const headerButtonAction = computed(() => {
  if (showFollowList.value.showing) return closeFollowList;
  if (showEditForm.value) return closeProfileEditor;
  return openSettingsPopup;
});

const headerButtonIcon = computed(() => {
  if (showFollowList.value.showing || showEditForm.value) return closeOutline;
  return ellipsisHorizontal;
});

const userRole = computed(() => {
  const user: User | null = authStore.getAuthenticatedUser;
  if (!user || !user.roles.length) return 'User';

  if (user.roles.includes('ARTIST')) return 'Artist';
  if (user.roles.includes('PREMIUM')) return 'Premium';
  if (user.roles.includes('ADMIN')) return 'Admin';
  return 'User';
});
</script>

<template>
  <ApplicationViewport title="Account" authenticated>
    <div class="ambient-glow">
      <div class="glow-orb orb-1"></div>
      <div class="glow-orb orb-2"></div>
      <div class="glow-orb orb-3"></div>
    </div>

    <div class="profile-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-icon-wrapper">
            <ion-icon :icon="personCircleOutline" class="header-icon" />
          </div>
          <div class="header-text">
            <h1 class="header-title">{{ headerTitle }}</h1>
            <p class="header-subtitle">{{ userRole }}</p>
          </div>
        </div>
        <button
          v-if="!showFollowList.showing"
          class="settings-button"
          :class="{ 'close-mode': showEditForm }"
          @click="headerButtonAction">
          <ion-icon :icon="headerButtonIcon" />
        </button>
        <button v-else class="settings-button close-mode" @click="headerButtonAction">
          <ion-icon :icon="closeOutline" />
        </button>
      </div>
    </div>

    <ActionsPopup
      :is-open="showActionsPopup"
      :x="popupX"
      :y="popupY"
      :actions="actionItems"
      @close="closeActionsPopup" />

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
  position: sticky;
  top: 0;
  z-index: 10;
  background: rgba(18, 22, 30, 0.85);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
  padding: 1rem 1.5rem;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  max-width: 1400px;
  margin: 0 auto;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex: 1;
  min-width: 0;
}

.header-icon-wrapper {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.2), rgba(37, 99, 235, 0.15));
  border: 1px solid rgba(59, 130, 246, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.2);
}

.header-icon {
  font-size: 22px;
  color: #60a5fa;
}

.header-text {
  flex: 1;
  min-width: 0;
}

.header-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 2px 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.header-subtitle {
  font-size: 0.85rem;
  color: #9aa3ad;
  margin: 0;
  font-weight: 500;
}

.settings-button {
  appearance: none;
  border: none;
  height: 38px;
  width: 38px;
  padding: 0;
  border-radius: 10px;
  background: linear-gradient(135deg, #253a60e0 30%, #1e40af90 100%);
  box-shadow: 0 4px 16px rgba(30, 41, 59, 0.18);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.settings-button.close-mode {
  background: rgba(239, 68, 68, 0.12);
  border: 1px solid rgba(239, 68, 68, 0.25);
  box-shadow: 0 4px 16px rgba(239, 68, 68, 0.2);
}

.settings-button ion-icon {
  font-size: 22px;
  padding-bottom: 0;
  color: #fff;
  flex-shrink: 0;
}

.settings-button.close-mode ion-icon {
  color: rgba(239, 68, 68, 1);
}

.settings-button:hover {
  box-shadow: 0 8px 24px rgba(30, 41, 59, 0.25);
  background: linear-gradient(135deg, #1e40af 60%, #1e293b 100%);
  transform: translateY(-2px);
}

.settings-button.close-mode:hover {
  box-shadow: 0 8px 24px rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.18);
}

.settings-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(30, 41, 59, 0.12);
}

.settings-button.close-mode:active {
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.15);
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

.ambient-glow {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 1;
  animation: glowFloat 10s ease-in-out infinite;
}

.glow-orb.orb-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.25) 0%, rgba(59, 130, 246, 0.08) 50%, transparent 100%);
  top: -150px;
  left: -150px;
}

.glow-orb.orb-2 {
  width: 450px;
  height: 450px;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.2) 0%, rgba(96, 165, 250, 0.06) 50%, transparent 100%);
  bottom: -100px;
  right: -100px;
}

.glow-orb.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.18) 0%, rgba(37, 99, 235, 0.05) 50%, transparent 100%);
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
}

@keyframes glowFloat {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(-20px, -25px) scale(1.05);
  }
  66% {
    transform: translate(20px, 20px) scale(0.95);
  }
}

@media (max-width: 640px) {
  .profile-header {
    padding: 1rem;
  }

  .header-icon-wrapper {
    width: 42px;
    height: 42px;
    border-radius: 10px;
  }

  .header-icon {
    font-size: 20px;
  }

  .header-title {
    font-size: 1.25rem;
  }

  .header-subtitle {
    font-size: 0.8rem;
  }

  .settings-button {
    height: 38px;
    border-radius: 10px;
  }

  .settings-button ion-icon {
    font-size: 19px;
  }
}
</style>
