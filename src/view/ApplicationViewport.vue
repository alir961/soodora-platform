<script setup lang="ts">
import { IonContent, IonHeader, IonPage, IonToolbar, IonButtons } from '@ionic/vue';
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
  backButton: Boolean,
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
    <ion-header v-if="!fullscreen" collapse="condense" class="safe-area">
      <ion-buttons slot="start">
        <slot name="buttons"></slot>
      </ion-buttons>
      <ion-toolbar style="--background: #0d0d0d">
        <div id="header">
          <div id="header-image-container">
            <img src="/assets/images/png/banner-light.png" alt="Soodora banner" />
          </div>
          <slot name="toolbar"></slot>
        </div>
      </ion-toolbar>
    </ion-header>
    <ion-content :fullscreen="true">
      <slot></slot>
    </ion-content>
  </ion-page>
</template>

<style scoped>
ion-header {
  background-color: #0d0d0d !important;
  --background: #0d0d0d !important;
  color: white;
}

#header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 4%;
  margin-bottom: 4%;
  height: 50px;
}

#header-image-container {
  padding-left: 1em;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  width: 50%;
}

#header-image-container img {
  width: auto;
  align-items: center;
}

.safe-area {
  padding: calc(env(safe-area-inset-top) - 10px) 0px 0px;
}
</style>
