<script setup lang="ts">
import { IonButton, onIonViewWillEnter } from '@ionic/vue';
import { useRouter } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { Ref, ref } from 'vue';
import { checkStatus } from '@/client/authenticationClient';

const INITIAL_COUNTER = 15;

const router = useRouter();
const counter: Ref<number> = ref(INITIAL_COUNTER);

const redirectHome = () => {
  router.push('/home');
};

const redirectIfOnline = () => {
  const checkIfOnline = () => {
    checkStatus().then(redirectHome).catch(redirectIfOnline);
    console.log('Check server status...');
  };

  setTimeout(() => {
    if (counter.value <= 1) {
      counter.value = INITIAL_COUNTER;
      checkIfOnline();
    } else {
      counter.value--;
      redirectIfOnline();
    }
  }, 1000);
};

onIonViewWillEnter(redirectIfOnline);
</script>

<template>
  <ApplicationViewport title="GrooveConductor" offline fullscreen>
    <div class="container">
      <h1>Maintenance</h1>
      <h2>Oops! Application under maintenance 😕</h2>

      <ion-button @click="redirectHome" class="button refresh">Refresh Page</ion-button>
      <p class="refresh">Automatic refresh in {{ counter }} {{ `second${counter > 0 ? 's' : ''}` }}</p>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.container {
  text-align: center;
  margin-top: 15%;
  padding: 0 20px;

  & h1 {
    font-size: 3rem;
  }

  & h2 {
    margin: 1em 0 0.5em 0;
    font-size: 1.5rem;
    color: #8c8c8c;
  }

  & p {
    font-size: 1rem;
    line-height: 22px;
    color: #8c8c8c;
    margin: 1em 0;
  }

  & p.refresh {
    font-size: 1rem;
    margin-bottom: 0;
  }

  & .refresh {
    color: white;
    font-style: italic;
  }

  & .button {
    margin-top: 1em;
  }
}
</style>
