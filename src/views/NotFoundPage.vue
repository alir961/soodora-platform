<template>
    <ion-page>
      <ion-header>
      </ion-header>
      <ion-content class="not-found-container">
        <div class="not-found-content">
          <h1>😔</h1>
          <h2>Oops! Page Not Found</h2>
          <p>Sorry, the page you are looking for doesn't exist.</p>
          <ion-button @click="goHome" class="home-button">Go to Home</ion-button>
        </div>
      </ion-content>
    </ion-page>
  </template>
  
  <script setup lang="ts">
  import { useRouter } from 'vue-router';
  
  const router = useRouter();
  
  const goHome = () => {
    router.push('/home');
  };
  </script>
  
  <style scoped>
  .not-found-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
  }
  
  .not-found-content {
    text-align: center;
    padding-top: 160px;
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