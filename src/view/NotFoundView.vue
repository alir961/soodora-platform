<script setup lang="ts">
import {IonButton} from '@ionic/vue';
import {useRouter} from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const router = useRouter();

const redirectHome = () => {
  router.push('/home');
};
</script>

<template>
  <ApplicationViewport title="GrooveConductor" authenticated>
    <div class="container">
      <div class="content">
        <h1>404</h1>
        <h2>Oops! Page Not Found 😕</h2>
        <p>Sorry, the page you are looking for doesn't exist.</p>
        <ion-button @click="redirectHome" class="button">Go to Home</ion-button>
      </div>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.content {
  text-align: center;
  margin-top: 15%;

  & h1 {
    font-size: 6em;
    padding-bottom: 50px;
  }

  & h2 {
    margin: 0.5em 0;
  }

  & p {
    margin: 1em 0;
  }

  & .button {
    margin-top: 1em;
  }
}
</style>