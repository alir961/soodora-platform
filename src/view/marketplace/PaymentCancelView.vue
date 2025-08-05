<script setup lang="ts">
import { IonButton, IonContent } from '@ionic/vue';
import { Router, useRouter } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const router: Router = useRouter();

async function goToMarketplace() {
  await router.push('/marketplace');
}
</script>
<template>
  <ApplicationViewport title="PaymentCancel">
    <ion-content class="ion-padding">
      <h2>Votre paiement a été annulé.</h2>
      <ion-button @click="goToMarketplace">Retour au marketplace</ion-button>
    </ion-content>
  </ApplicationViewport>
</template>
