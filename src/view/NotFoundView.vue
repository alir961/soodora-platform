<script setup lang="ts">
import { IonButton } from '@ionic/vue';
import { useRouter } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const router = useRouter();

const redirectHome = () => {
  router.push('/explore');
};
</script>

<template>
  <ApplicationViewport title="404 Page Not Found" authenticated>
    <div class="container">
      <div class="glow-background"></div>
      <div class="content">
        <h1 class="glow">404</h1>
        <h2 class="animated-text">Lost in the groove?</h2>
        <p>This page doesn’t seem to exist.</p>
        <ion-button @click="redirectHome" class="button">Back to grooving</ion-button>
      </div>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  background: #0a0a0a;
  color: #fff;
}

.glow-background {
  position: absolute;
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(0, 136, 255, 0.6) 0%, rgba(0, 51, 204, 0.4) 40%, rgba(0, 0, 0, 0) 100%);
  filter: blur(100px);
  opacity: 0.6;
  z-index: 0;
  animation: pulse 6s ease-in-out infinite alternate;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.5;
  }
  100% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

.content {
  text-align: center;
  z-index: 1;

  & h1.glow {
    font-size: 7em;
    color: #ffffff;
    text-shadow:
      0 0 15px #0088ff,
      0 0 25px #0033cc;
    margin-bottom: 10px;
    animation: float 4s ease-in-out infinite;
  }

  & h2.animated-text {
    font-size: 1.8em;
    opacity: 0;
    animation: slideIn 1.5s ease forwards;
    animation-delay: 0.5s;
  }

  & p {
    margin-top: 1em;
    color: #ccc;
  }

  & .button {
    margin-top: 2em;
    --background: #0088ff;
    --background-hover: #0055cc;
    --color: #fff;
  }
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes slideIn {
  0% {
    transform: translateY(20px);
    opacity: 0;
  }
  100% {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
