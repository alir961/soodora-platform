<script setup lang="ts">
import { IonApp, IonIcon, IonLabel, IonRouterOutlet, IonTabBar, IonTabButton, IonTabs } from '@ionic/vue';
import { bagHandleOutline, searchOutline, personCircleOutline, compassOutline, albumsOutline } from 'ionicons/icons';
import { capitalize } from '@/utils';
import { useAuthStore } from '@/store/reactiveStorage';
import { computed, ref, watch } from 'vue';
import NowPlayingBar from '@/component/player/NowPlayingBar.vue';
import DesktopWarning from '@/component/DesktopWarning.vue';
import { User } from '@/entity/User';
import { callRetrieveConfiguration } from '@/client/profileClient';
import ConfigurationEditor from '@/component/ConfigurationEditor.vue';

interface TabEntry {
  name: string;
  icon: string;
  authenticated: boolean;
}

const authStore = useAuthStore();
const fullscreenRoutes: string[] = ['/auth', '/maintenance'];
const configured = ref<boolean>(true);

const tabs: TabEntry[] = [
  { name: 'explore', icon: compassOutline, authenticated: false },
  { name: 'marketplace', icon: bagHandleOutline, authenticated: false },
  { name: 'search', icon: searchOutline, authenticated: false },
  { name: 'playlists', icon: albumsOutline, authenticated: true },
  { name: 'authentication', icon: personCircleOutline, authenticated: false },
  { name: 'profile', icon: personCircleOutline, authenticated: true },
];

const filteredTabs = computed(() => {
  return tabs.filter((tab) => {
    if (tab.name === 'authentication') return !authStore.isAuthenticated;
    if (tab.authenticated) return authStore.isAuthenticated;
    return true;
  });
});

const fetchConfiguration = async () => {
  const user: User | null = authStore.getAuthenticatedUser;

  if (!user) configured.value = true;
  else configured.value = await callRetrieveConfiguration();
};

watch(
  () => authStore.getAuthenticatedUser,
  async () => await fetchConfiguration()
);
</script>

<template>
  <ion-app>
    <DesktopWarning />
    <ion-tabs>
      <ion-router-outlet />
      <NowPlayingBar />
      <ion-tab-bar v-if="!fullscreenRoutes.includes($route.path) && configured" slot="bottom">
        <ion-tab-button v-for="tab in filteredTabs" :key="tab.name" :tab="tab.name" :href="`/${tab.name}`">
          <ion-icon :icon="tab.icon" />
          <ion-label>{{ capitalize(tab.name) }}</ion-label>
        </ion-tab-button>
      </ion-tab-bar>
      <ConfigurationEditor v-if="!configured" @configured="fetchConfiguration" />
    </ion-tabs>
  </ion-app>
</template>
