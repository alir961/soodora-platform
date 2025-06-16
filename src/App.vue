<script setup lang="ts">
import { IonApp, IonIcon, IonLabel, IonRouterOutlet, IonTabBar, IonTabButton, IonTabs } from '@ionic/vue';
import { cartOutline, addCircleOutline, searchOutline, personOutline, planetOutline } from 'ionicons/icons';
import { capitalize } from '@/utils';
import { useAuthStore } from '@/store/reactiveStorage';
import { ref, computed, watchEffect, onMounted } from 'vue';

interface TabEntry {
  name: string;
  icon: string;
  requiresAuth?: boolean;
  requiresAdminOrUser?: boolean;
}

const authStore = useAuthStore();
const isAdminOrArtist = ref(false);
const fullscreenRoutes: string[] = ['/auth'];

const tabs: TabEntry[] = [
  { name: 'explore', icon: planetOutline, requiresAuth: false },
  { name: 'marketplace', icon: cartOutline, requiresAuth: false },
  { name: 'create', icon: addCircleOutline, requiresAdminOrUser: true },
  { name: 'search', icon: searchOutline, requiresAuth: false },
  { name: 'authentication', icon: personOutline, requiresAuth: false },
  { name: 'profile', icon: personOutline, requiresAuth: true },
];

const loadUser = async () => {
  if (authStore.isAuthenticated) {
    const user = authStore.getAuthenticatedUser;

    if (user === null) return;
    try {
      isAdminOrArtist.value = user.roles.includes('ADMIN') || user.roles.includes('ARTIST');
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
