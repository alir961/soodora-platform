<script setup lang="ts">
import { IonApp, IonIcon, IonLabel, IonRouterOutlet, IonTabBar, IonTabButton, IonTabs } from '@ionic/vue';
import { cartOutline, planetOutline, musicalNotesOutline, personOutline } from 'ionicons/icons';
import { capitalize } from '@/utils';
import { AUTHENTICATED, loadUserStatus } from '@/store/localStorage';
import { computed, onMounted, ref } from 'vue';

interface TabEntry {
  name: string;
  icon: string;
  requiresAuth: boolean;
}

const tabs: TabEntry[] = [
  { name: 'explore', icon: planetOutline, requiresAuth: false },
  { name: 'marketplace', icon: cartOutline, requiresAuth: false },
  { name: 'playlist', icon: musicalNotesOutline, requiresAuth: false },
  { name: 'authentication', icon: personOutline, requiresAuth: false },
  { name: 'account', icon: personOutline, requiresAuth: true },
];

const userStatus = ref<string>('');

onMounted(async () => {
  userStatus.value = await loadUserStatus();
});

const filteredTabs = computed(() => {
  return tabs.filter((tab) => {
    if (tab.name === 'authentication' && userStatus.value === AUTHENTICATED) return false;
    return !tab.requiresAuth || userStatus.value === AUTHENTICATED;
  });
});
const fullscreenRoutes: string[] = ['/auth', '/maintenance'];
</script>

<template>
  <ion-app>
    <ion-tabs>
      <ion-router-outlet />

      <ion-tab-bar v-if="!fullscreenRoutes.includes($route.path)" slot="bottom">
        <ion-tab-button v-for="tab in filteredTabs" :key="tab.name" :tab="tab.name" :href="`/${tab.name}`">
          <ion-icon :icon="tab.icon" />
          <ion-label>{{ capitalize(tab.name) }}</ion-label>
        </ion-tab-button>
      </ion-tab-bar>
    </ion-tabs>
  </ion-app>
</template>
