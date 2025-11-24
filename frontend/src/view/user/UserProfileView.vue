<script setup lang="ts">
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import Header from '@/component/AppHeader.vue';
import { computed, onMounted, ref, Ref } from 'vue';
import { briefcaseOutline, closeOutline, ellipsisHorizontal, linkOutline, personCircleOutline } from 'ionicons/icons';
import { IonIcon } from '@ionic/vue';
import { useRoute, useRouter } from 'vue-router';
import { User } from '@/entity/User';
import { callRetrieveUser } from '@/client/userClient';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import UserContentList from '@/component/user/UserContentList.vue';
import ActionsPopup, { ActionItem } from '@/component/actions/ActionsPopup.vue';
import { getUserLinkHub } from '@/client/linkHubClient';
import { exportSoodoraKitPdf, getSoodoraKitArtistSlug } from '@/client/soodoraKitClient';
import { showToast } from '@/utils';

const route = useRoute();
const router = useRouter();

const user = ref(null) as Ref<User | null>;
const showFollowList = ref<{ showing: boolean; type?: 'followers' | 'following' }>({ showing: false });
const profileComponentRef = ref<InstanceType<typeof ProfileComponent> | null>(null);

const showActionsPopup = ref(false);
const popupX = ref(0);
const popupY = ref(0);

onMounted(async () => {
  const userSlug = route.params.slug as string;

  try {
    if (userSlug) user.value = await callRetrieveUser(userSlug);
  } catch (error) {
    console.error(error);
  }
});

const handleListStateChange = (state: { showing: boolean; type?: 'followers' | 'following' }) => {
  showFollowList.value = state;
};

const closeFollowList = () => {
  profileComponentRef.value?.closeList();
  showFollowList.value = { showing: false };
};

const headerTitle = computed(() => {
  if (showFollowList.value.showing) {
    return showFollowList.value.type === 'followers' ? 'Followers' : 'Following';
  }
  return 'About Me';
});

const handleBackButton = () => {
  if (showFollowList.value.showing) {
    closeFollowList();
  } else {
    router.back();
  }
};

const userRole = computed(() => {
  if (!user.value || !user.value.roles.length) return 'User';

  if (user.value.roles.includes('ARTIST')) return 'Artist';
  if (user.value.roles.includes('PREMIUM')) return 'Premium';
  if (user.value.roles.includes('ADMIN')) return 'Admin';
  return 'User';
});

const handleLinkHub = async () => {
  const userSlug = route.params.slug as string;

  try {
    const linkHub = await getUserLinkHub(userSlug);
    if (linkHub) {
      await router.push(`/linkhub/${linkHub.slug}`);
      return;
    }
  } catch (error: unknown) {
    await showToast('Artist may not have a Link Hub', 'warning');
  }
};

const openSettingsPopup = (event: MouseEvent) => {
  const button = event.currentTarget as HTMLElement;
  const rect = button.getBoundingClientRect();
  popupX.value = rect.right - 220;
  popupY.value = rect.bottom + 8;
  showActionsPopup.value = true;
};

const handleSoodoraKit = async () => {
  const userSlug = route.params.slug as string;
  const soodoraKit = await getSoodoraKitArtistSlug(userSlug);

  try {
    const blob = await exportSoodoraKitPdf(soodoraKit.slug);
    const url = URL.createObjectURL(blob);
    const link = Object.assign(document.createElement('a'), {
      href: url,
      download: `soodorakit_${soodoraKit.slug}.pdf`,
    });
    link.click();
    URL.revokeObjectURL(url);
  } catch (error: unknown) {
    const message =
      'Failed to download PDF, Artist may not have a Soodora Kit. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'warning');
  }
};

const actionItems = computed<ActionItem[]>(() => {
  const items: ActionItem[] = [];
  items.push(
    {
      icon: linkOutline,
      label: 'Visit Link Hub',
      action: handleLinkHub,
    },
    {
      icon: briefcaseOutline,
      label: 'Download Soodora Kit',
      action: handleSoodoraKit,
    }
  );
  return items;
});

const closeActionsPopup = () => {
  showActionsPopup.value = false;
};
</script>

<template>
  <ApplicationViewport title="User Profile" back-button>
    <div class="ambient-glow">
      <div class="glow-orb orb-1"></div>
      <div class="glow-orb orb-2"></div>
      <div class="glow-orb orb-3"></div>
    </div>

    <Header :title="headerTitle" :subtitle="userRole" :icon="personCircleOutline">
      <template #actions>
        <button class="about-btn" @click="openSettingsPopup($event)">
          <ion-icon :icon="ellipsisHorizontal" />
        </button>
        <button class="back-button" @click="handleBackButton">
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

    <ProfileComponent
      v-if="user"
      ref="profileComponentRef"
      :profile="user"
      @list-state-change="handleListStateChange" />

    <UserContentList v-if="user && !showFollowList.showing" :user="user" />

    <div v-if="!user" class="loading">
      <p>Loading...</p>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
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
</style>
