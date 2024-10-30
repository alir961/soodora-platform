<script setup lang="ts">
import {
  IonContent,
  IonHeader,
  IonIcon,
  IonLabel,
  IonPage,
  IonTabBar,
  IonTabButton,
  IonTitle,
  IonToolbar,
} from '@ionic/vue';
import {capitalize, getAuthenticatedUser} from '@/utils';
import {deleteUserData} from '@/store/localStorage';
import {checkStatus} from '@/client/authenticationClient';
import {cartOutline, homeOutline, musicalNotesOutline, personOutline} from 'ionicons/icons';
import {onMounted} from 'vue';

interface TabEntry {
  name: string;
  icon: string;
}

const props = defineProps({
  title: {
    required: true,
    type: String,
  },
  authenticated: Boolean,
  offline: Boolean,
  fullscreen: Boolean,
});

const tabs: TabEntry[] = [
  {name: 'home', icon: homeOutline},
  {name: 'marketplace', icon: cartOutline},
  {name: 'playlist', icon: musicalNotesOutline},
  {name: 'account', icon: personOutline},
];

onMounted(async () => {
  try {
    if (!props.offline)
      await checkStatus();
    if (props.authenticated) {
      await deleteUserData();
      await getAuthenticatedUser();
    }
  } catch (error) {
    console.error(error);
  }
});
</script>

<template>
  <ion-page>
    <ion-header v-if="!fullscreen" collapse="condense" slot="toolbar">
      <ion-buttons slot="start">
        <slot name="buttons"></slot>
      </ion-buttons>
      <ion-toolbar>
        <div id="toolbar">
          <div id="toolbar-title">
            <ion-title size="large">{{ $props.title }}</ion-title>
          </div>
          <slot name="toolbar"></slot>
        </div>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <slot></slot>
    </ion-content>

    <ion-tab-bar v-if="!fullscreen" slot="bottom">
      <ion-tab-button v-for="tab in tabs" :key="tab.name" :tab="tab.name" :href="`/${tab.name}`">
        <ion-icon :icon="tab.icon"/>
        <ion-label>{{ capitalize(tab.name) }}</ion-label>
      </ion-tab-button>
    </ion-tab-bar>
  </ion-page>
</template>

<style scoped>
ion-title {
  align-items: center;
}

#toolbar {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 4%;
  margin-bottom: 4%;
}

#toolbar-title {
  padding-left: 3%;
  padding-top: 2%;
  padding-bottom: 2%;
}
</style>