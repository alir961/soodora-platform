<template>
  <ion-app>
    <ion-tabs>
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
  </ion-app>
</template>

<script setup lang="ts">
import { IonApp, IonIcon, IonLabel, IonRouterOutlet, IonTabBar, IonTabButton, IonTabs } from '@ionic/vue';
import { cartOutline, homeOutline, musicalNotesOutline, personOutline } from "ionicons/icons";
</script>
