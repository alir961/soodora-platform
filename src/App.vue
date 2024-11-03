<script setup lang="ts">
import { IonApp, IonIcon, IonLabel, IonRouterOutlet, IonTabBar, IonTabButton, IonTabs } from '@ionic/vue';
import { cartOutline, homeOutline, musicalNotesOutline, personOutline } from 'ionicons/icons';
import { capitalize } from '@/utils';

interface TabEntry {
  name: string;
  icon: string;
}

const tabs: TabEntry[] = [
  { name: 'home', icon: homeOutline },
  { name: 'marketplace', icon: cartOutline },
  { name: 'playlist', icon: musicalNotesOutline },
  { name: 'account', icon: personOutline },
];

const fullscreenRoutes: string[] = ['/auth', '/maintenance'];
</script>

<template>
  <ion-app>
    <ion-tabs>
      <ion-router-outlet />

      <ion-tab-bar v-if="!fullscreenRoutes.includes($route.path)" slot="bottom">
        <ion-tab-button v-for="tab in tabs" :key="tab.name" :tab="tab.name" :href="`/${tab.name}`">
          <ion-icon :icon="tab.icon" />
          <ion-label>{{ capitalize(tab.name) }}</ion-label>
        </ion-tab-button>
      </ion-tab-bar>
    </ion-tabs>
  </ion-app>
</template>
