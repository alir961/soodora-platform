<script setup lang="ts">
import { IonApp, IonIcon, IonLabel, IonRouterOutlet, IonTabBar, IonTabButton, IonTabs } from '@ionic/vue';
import { bagHandleOutline, searchOutline, personCircleOutline, compassOutline, albumsOutline } from 'ionicons/icons';
import { capitalize } from '@/utils';
import { useAuthStore } from '@/store/reactiveStorage';
import { ref, computed, watchEffect, onMounted } from 'vue';
import NowPlayingBar from '@/component/player/NowPlayingBar.vue';
import DesktopWarning from '@/component/DesktopWarning.vue';

interface TabEntry {
  name: string;
  icon: string;
  requiresAuth?: boolean;
  requiresAdminOrUser?: boolean;
}

const authStore = useAuthStore();
const isAdminOrArtist = ref(false);
const fullscreenRoutes: string[] = ['/auth', '/maintenance'];

const tabs: TabEntry[] = [
  { name: 'explore', icon: compassOutline, requiresAuth: false },
  { name: 'marketplace', icon: bagHandleOutline, requiresAuth: false },
  { name: 'search', icon: searchOutline, requiresAuth: false },
  { name: 'playlists', icon: albumsOutline, requiresAuth: true },
  { name: 'authentication', icon: personCircleOutline, requiresAuth: false },
  { name: 'profile', icon: personCircleOutline, requiresAuth: true },
];

const loadUser = async () => {
  if (authStore.isAuthenticated) {
    const user = authStore.getAuthenticatedUser;
    const allowedRoles = ['ADMIN', 'ARTIST', 'PREMIUM'];

    if (user === null) return;
    try {
      isAdminOrArtist.value = user.roles.some((role) => allowedRoles.includes(role));
    } catch (error) {
      console.error('Unable to load user data:', error);
      isAdminOrArtist.value = false;
    }
  } else {
    isAdminOrArtist.value = false;
  }
};

const filteredTabs = computed(() => {
  return tabs.filter((tab) => {
    if (tab.name === 'authentication') {
      return !authStore.isAuthenticated;
    }
    if (tab.requiresAuth) {
      return authStore.isAuthenticated;
    }
    if (tab.requiresAdminOrUser) {
      return isAdminOrArtist.value;
    }
    return true;
  });
});

onMounted(() => {
  loadUser();
});

watchEffect(() => {
  if (authStore.isAuthenticated) {
    loadUser();
  } else {
    isAdminOrArtist.value = false;
  }
});
</script>

<template>
  <ion-app>
    <DesktopWarning />
    <ion-tabs>
      <ion-router-outlet />
      <NowPlayingBar />
      <ion-tab-bar v-if="!fullscreenRoutes.includes($route.path)" slot="bottom">
        <ion-tab-button v-for="tab in filteredTabs" :key="tab.name" :tab="tab.name" :href="`/${tab.name}`">
          <ion-icon :icon="tab.icon" />
          <ion-label>{{ capitalize(tab.name) }}</ion-label>
        </ion-tab-button>
      </ion-tab-bar>
    </ion-tabs>
  </ion-app>
</template>
