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
  router.push('/explore');
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
  <ApplicationViewport title="Soodora" offline :hideTabBar="true">
    <div class="container">
      <div class="glow-background"></div>
      <div class="content">
        <h1 class="glow">Maintenance</h1>
        <h2 class="animated-text">We're tuning up the vibe</h2>
        <ion-button @click="redirectHome" class="button refresh">Refresh Page</ion-button>
        <p class="refresh">Automatic refresh in {{ counter }} {{ `second${counter > 0 ? 's' : ''}` }}</p>
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
    font-size: 3.5rem;
    color: #ffffff;
    text-shadow:
      0 0 15px #0088ff,
      0 0 25px #0033cc;
    margin-bottom: 10px;
    animation: float 4s ease-in-out infinite;
  }

  & h2.animated-text {
    font-size: 1.4rem;
    color: #8c8c8c;
    opacity: 0;
    animation: slideIn 1.5s ease forwards;
    animation-delay: 0.5s;
    margin-bottom: 1em;
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
