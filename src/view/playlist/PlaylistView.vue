<script setup lang="ts">
import { IonButton, IonIcon } from '@ionic/vue';
import { createOutline, playCircle } from 'ionicons/icons';
import { useRouter } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const router = useRouter();

const goToPlaylists = async () => {
  return router.push(`/playlists/create`);
};

const goToMyPlaylists = async () => {
  return router.push(`/playlists/me`);
};
</script>

<template>
  <ApplicationViewport title="Playlists" authenticated>
    <div class="create-container ion-padding">
      <h1 class="page-title">Playlists</h1>
      <p class="page-description">Create your mood with Soodora.</p>

      <ion-button @click="goToPlaylists" expand="block" class="go-button" color="primary" fill="solid">
        Create Playlists
        <ion-icon :icon="createOutline" slot="end"></ion-icon>
      </ion-button>

      <ion-button @click="goToMyPlaylists" expand="block" class="go-button" color="secondary" fill="solid">
        My Playlists
        <ion-icon :icon="playCircle" slot="end"></ion-icon>
      </ion-button>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.create-container {
  display: flex;
  flex-direction: column;
  margin: 20px;
  padding: 10px 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 0;
}

.page-description {
  font-size: 19px;
  color: var(--ion-color-medium);
  margin-bottom: 20px;
}

.go-button {
  margin-top: 12px;
  border-radius: 12px;
}

ion-button.go-button::part(native) {
  color: white !important;
}
</style>
