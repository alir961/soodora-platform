<script setup lang="ts">
import { IonPage, IonTitle, IonToolbar, IonHeader, IonButton, IonContent } from "@ionic/vue";
import { useRouter } from 'vue-router';

const router = useRouter();

const goHome = () => {
  router.push('/home');
};
</script>

<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>GrooveConductor</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <div id="toolbar">
            <div id="toolbar-title">
              <ion-title size="large">GrooveConductor</ion-title>
            </div>
          </div>
        </ion-toolbar>
      </ion-header>

      <ion-content class="not-found-container">
        <div class="not-found-content">
          <h1>404</h1>
          <h2>Oops! Page Not Found 😕</h2>
          <p>Sorry, the page you are looking for doesn't exist.</p>
          <ion-button @click="goHome" class="home-button">Go to Home</ion-button>
        </div>
      </ion-content>
    </ion-content>
  </ion-page>
</template>

<style scoped>

#toolbar {
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-top: 4%;
  margin-bottom: 4%;
}

#toolbar-title {
  padding-left: 3%;
  padding-top: 2%;
  padding-bottom: 2%;
}

.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.not-found-content {
  text-align: center;
  margin-top: 15%;
}

h1 {
  font-size: 6em;
  padding-bottom: 50px;
}

h2 {
  margin: 0.5em 0;
}

p {
  margin: 1em 0;
}

.home-button {
  margin-top: 1em;
}
</style>