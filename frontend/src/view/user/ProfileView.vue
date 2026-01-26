<script setup lang="ts">
import { IonIcon, onIonViewWillEnter } from '@ionic/vue';
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import {
  callRetrieveLinkedOAuthProviders,
  OAuthProvider,
  retrieveAuthenticatedProfile,
  callUpdateConfiguration,
} from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import Header from '@/component/AppHeader.vue';
import OAuthButton from '@/component/auth/OAuthButton.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import UserContentList from '@/component/user/UserContentList.vue';
import EditProfileForm from '@/component/user/EditProfileForm.vue';
import { User } from '@/entity/User';
import { deleteBearerToken, loadBearerToken } from '@/store/localStorage';
import { showToast } from '@/utils';
import {
  briefcaseOutline,
  chatbubblesOutline,
  closeOutline,
  ellipsisHorizontal,
  linkOutline,
  logOutOutline,
  mapOutline,
  personCircleOutline,
  settingsOutline,
  statsChartOutline,
  musicalNotesOutline,
  warningOutline,
} from 'ionicons/icons';
import ActionsPopup, { type ActionItem } from '@/component/actions/ActionsPopup.vue';
import { getSoodoraKitArtistSlug } from '@/client/soodoraKitClient';
import { getUserLinkHub } from '@/client/linkHubClient';

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

const showMigrationModal = ref(false);
const isMigrating = ref(false);

onIonViewWillEnter(async () => {
  if (route.query.error === 'oauth') await showToast('Unable to connect to OAuth provider!', 'danger');
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

const handleListStateChange = (state: { showing: boolean; type?: 'followers' | 'following' }) => {
  showFollowList.value = state;
};

const closeFollowList = () => {
  profileComponentRef.value?.closeList();
  showFollowList.value = { showing: false };
};

const openActionsPopup = (event: MouseEvent) => {
  const button = event.currentTarget as HTMLElement;
  const bounds = button.getBoundingClientRect();

  popupX.value = bounds.right - 220;
  popupY.value = bounds.bottom + 8;
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
  globalThis.location.reload();
};

const handleLinkHub = async () => {
  const user = authStore.getAuthenticatedUser;

  if (!user) return;
  try {
    const linkHub = await getUserLinkHub(user.slug);

    if (linkHub) await router.push(`/linkhub/edit/${linkHub.slug}`);
  } catch (error) {
    console.log("Artist doesn't have a Link Hub");
    await router.push('/linkhub/create');
  }
};

const handleSoodoraKit = async () => {
  const user = authStore.getAuthenticatedUser;

  if (!user) return;
  if (!user.roles.includes('ARTIST')) return;
  try {
    const soodoraKit = await getSoodoraKitArtistSlug(user.slug);

    if (soodoraKit) await router.push(`/soodorakits/${soodoraKit.slug}`);
  } catch (error) {
    console.log("Artist doesn't have a Soodora Kit");
    await router.push('/soodorakits/create');
  }
};

const handleQuotes = async () => {
  await router.push('/quotes/me');
};

const handleEvents = async () => {
  const user: User | null = authStore.getAuthenticatedUser;

  if (!user) return;
  await router.push(`/user/${user.slug}/events/list`);
};

const handleAnalytics = async () => {
  await router.push('/analytics');
};

const handleArtistMigration = async () => {
  showMigrationModal.value = true;
};

const confirmMigration = async () => {
  isMigrating.value = true;
  try {
    await callUpdateConfiguration(true);
    await showToast('Success! You are now an Artist.', 'success');

    const updatedProfile = await retrieveAuthenticatedProfile();

    authStore.authenticate(updatedProfile);
    showMigrationModal.value = false;
  } catch (error) {
    await showToast('Migration failed. Please try again.', 'danger');
    console.error(error);
  } finally {
    isMigrating.value = false;
  }
};

const isAdministrator = computed(() => {
  const user: User | null = authStore.getAuthenticatedUser;

  if (!user) return false;
  return user.roles.includes('ADMIN');
});

const isArtist = computed(() => {
  const user: User | null = authStore.getAuthenticatedUser;

  if (!user) return false;
  return user.roles.includes('ARTIST') || user.roles.includes('PREMIUM');
});

const canCreateContent = computed(() => {
  const user = authStore.getAuthenticatedUser;

  if (!user) return false;
  return user.roles.some((role) => ['ADMIN', 'PREMIUM', 'ARTIST'].includes(role));
});

const actionItems = computed<ActionItem[]>(() => {
  const items: ActionItem[] = [];

  if (canCreateContent.value) {
    items.push(
      { icon: linkOutline, label: 'Link Hub', action: handleLinkHub },
      { icon: briefcaseOutline, label: 'Soodora Kit', action: handleSoodoraKit },
      { icon: chatbubblesOutline, label: 'My Quotes', action: handleQuotes },
      { icon: mapOutline, label: 'My Events', action: handleEvents },
      { icon: statsChartOutline, label: 'Analytics', action: handleAnalytics }
    );
  }

  if (!isArtist.value) items.push({ icon: musicalNotesOutline, label: 'Become Artist', action: handleArtistMigration });
  items.push(
    { icon: settingsOutline, label: 'Settings', action: handleSettings },
    { icon: logOutOutline, label: 'Sign Out', action: handleSignOut, destructive: true }
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
  return openActionsPopup;
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
  <ApplicationViewport>
    <div class="ambient-glow">
      <div class="glow-orb orb-1"></div>
      <div class="glow-orb orb-2"></div>
      <div class="glow-orb orb-3"></div>
    </div>

    <Header :title="headerTitle" :subtitle="userRole" :icon="personCircleOutline">
      <template #actions>
        <button
          v-if="!showFollowList.showing"
          class="actions-button"
          :class="{ 'close-mode': showEditForm }"
          @click="headerButtonAction">
          <ion-icon :icon="headerButtonIcon" />
        </button>
        <button v-else class="actions-button close-mode" @click="headerButtonAction">
          <ion-icon :icon="closeOutline" />
        </button>
      </template>
    </Header>

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

    <div v-if="showMigrationModal" class="modal-overlay" @click.self="showMigrationModal = false">
      <div class="ambient-glow">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
      </div>
      <div class="modal-card">
        <div class="modal-icon-wrapper">
          <ion-icon :icon="warningOutline" class="warning-icon" />
        </div>
        <h3>Confirm Migration</h3>
        <p>
          Are you sure you want to upgrade to an <strong>Artist Account</strong>? <br /><br />
          This action <strong>cannot be undone</strong>. You will gain access to exclusive tools like the Soodora Kit
          and Link Hub.
        </p>

        <div class="modal-actions">
          <button class="modal-btn cancel" @click="showMigrationModal = false" :disabled="isMigrating">Cancel</button>
          <button class="modal-btn confirm" @click="confirmMigration" :disabled="isMigrating">
            <span v-if="!isMigrating">Confirm</span>
            <span v-else>Processing...</span>
          </button>
        </div>
      </div>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.content-wrapper {
  position: relative;
}

.button {
  width: 90%;
  margin: 0 auto 0.8rem auto;
  padding: 0.4rem 1rem;
  text-align: center;
  border-radius: 15px;
  font-weight: bold;
  font-size: 1.2em;
  color: #fff;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.12);

  & > span {
    height: 24px;
  }
}

.google-button {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(270deg, #232a36 0%, #273042 25%, #32405a 50%, #273042 75%, #232a36 100%);
  background-size: 400% 400%;
  animation: gradientFlow 6s ease-in-out infinite;
}

.feedback-button {
  background: linear-gradient(270deg, #1b406b 0%, #235080 25%, #2e6fb2 50%, #235080 75%, #1b406b 100%);
  background-size: 400% 400%;
  animation: gradientFlow 6s ease-in-out infinite;
}

.administration-button {
  background: linear-gradient(270deg, #6b2323 0%, #7a2929 25%, #b23a3a 50%, #7a2929 75%, #6b2323 100%);
  background-size: 400% 400%;
  animation: gradientFlow 6s ease-in-out infinite;
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

  .actions-button {
    height: 38px;
    border-radius: 10px;
  }

  .actions-button ion-icon {
    font-size: 19px;
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  padding: 1rem;
}

.modal-card {
  background: rgba(22, 22, 24, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 24px;
  width: 100%;
  max-width: 340px;
  text-align: center;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
  animation: modalPop 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes modalPop {
  0% {
    transform: scale(0.9);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.modal-icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 200, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px auto;
  border: 1px solid rgba(255, 200, 0, 0.3);
}

.warning-icon {
  font-size: 32px;
  color: #ffc800;
}

.modal-card h3 {
  color: #fff;
  margin: 0 0 12px 0;
  font-size: 1.4rem;
  font-weight: 700;
}

.modal-card p {
  color: #a0a0a0;
  font-size: 0.95rem;
  line-height: 1.5;
  margin-bottom: 24px;
}

.modal-card p strong {
  color: #fff;
}

.modal-actions {
  display: flex;
  gap: 12px;
}

.modal-btn {
  flex: 1;
  padding: 12px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: transform 0.1s;
  border: none;
}

.modal-btn:active {
  transform: scale(0.96);
}

.modal-btn.cancel {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
}

.modal-btn.confirm {
  background: #3b82f6;
  color: #fff;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
}

.modal-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
