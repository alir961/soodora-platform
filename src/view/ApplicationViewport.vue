<script setup lang="ts">
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonButtons } from '@ionic/vue';
import { checkStatus } from '@/client/authenticationClient';
import { onMounted } from 'vue';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';
import { useAuthStore } from '@/store/reactiveStorage';
import { loadBearerToken } from '@/store/localStorage';

const props = defineProps({
  title: {
    required: true,
    type: String,
  },
  authenticated: Boolean,
  offline: Boolean,
  fullscreen: Boolean,
});
const authStore = useAuthStore();

onMounted(async () => {
  if (props.offline) return;

  await checkStatus().catch(console.error);
  try {
    await loadBearerToken();

    authStore.authenticate(await retrieveAuthenticatedProfile());
  } catch (error) {
    authStore.logout();
  }
});
</script>

<template>
  <ion-page>
    <ion-header v-if="!fullscreen" :translucent="true">
      <ion-toolbar>
        <ion-title>{{ $props.title }}</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-header v-if="!fullscreen" collapse="condense">
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

      <slot></slot>
    </ion-content>
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
