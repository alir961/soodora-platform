<script setup lang="ts">
import { IonButton, IonContent } from '@ionic/vue';
import { Router, useRouter } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const router: Router = useRouter();
const props = defineProps({
  title: String,
  button: String,
});

async function goToMarketplace() {
  await router.push('/marketplace');
}
</script>
<template>
  <ApplicationViewport title="PaymentCancel">
    <ion-content class="ion-padding">
      <h2>{{ props.title }}</h2>
      <ion-button @click="goToMarketplace">{{ props.button }}</ion-button>
    </ion-content>
  </ApplicationViewport>
</template>
