<template>
  <ion-app>
    <ion-tabs v-if="route.path !== '/auth'"> <!-- Show tabs only if not on the /auth route -->
      <ion-router-outlet></ion-router-outlet>
      <ion-tab-bar slot="bottom">

        <ion-tab-button tab="home" href="/home">
          <ion-icon :icon="homeOutline" />
          <ion-label>Home</ion-label>
        </ion-tab-button>

        <ion-tab-button tab="account" href="/account">
          <ion-icon :icon="personOutline" />
          <ion-label>Account</ion-label>
        </ion-tab-button>

        <ion-tab-button tab="marketplace" href="/marketplace">
          <ion-icon :icon="cartOutline" />
          <ion-label>Marketplace</ion-label>
        </ion-tab-button>

        <ion-tab-button tab="playlist" href="/playlist">
          <ion-icon :icon="musicalNotesOutline" />
          <ion-label>Playlist</ion-label>
        </ion-tab-button>

      </ion-tab-bar>
    </ion-tabs>

    <ion-router-outlet v-if="route.path === '/auth'"></ion-router-outlet>
  </ion-app>
</template>

<script setup lang="ts">
import { IonApp, IonIcon, IonLabel, IonRouterOutlet, IonTabBar, IonTabButton, IonTabs } from '@ionic/vue';
import { cartOutline, homeOutline, musicalNotesOutline, personOutline } from "ionicons/icons";
import { useRoute } from 'vue-router';

const route = useRoute();
</script>

<style>
.header-background {
  display: none;
}
</style>