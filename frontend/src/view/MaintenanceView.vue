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
  <ApplicationViewport offline>
    <div class="container">
      <!-- Ambient Glow Background -->
      <div class="ambient-glow">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
        <div class="glow-orb orb-3"></div>
      </div>

      <div class="content">
        <div class="maintenance-icon">🔧</div>

        <h1 class="title">Under Maintenance</h1>
        <p class="message">We're making things better.</p>
        <p class="message">We'll be back shortly.</p>

        <div class="action-column">
          <ion-button @click="redirectHome" class="refresh-button">
            <span>Refresh Page</span>
          </ion-button>

          <div class="timer">
            <span class="timer-text">Auto-refresh in </span>
            <span class="timer-value">{{ counter }}s</span>
          </div>
        </div>
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
  padding: 2rem;
  overflow: hidden;
  background: #0a0a0f;
}

/* Ambient Background */
.ambient-glow {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.6;
  animation: glowFloat 10s ease-in-out infinite;
}

.glow-orb.orb-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.25) 0%, rgba(59, 130, 246, 0.08) 50%, transparent 100%);
  top: -150px;
  left: -150px;
}

.glow-orb.orb-2 {
  width: 450px;
  height: 450px;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.2) 0%, rgba(96, 165, 250, 0.06) 50%, transparent 100%);
  bottom: -100px;
  right: -100px;
  animation-delay: 5s;
}

.glow-orb.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(37, 99, 235, 0.18) 0%, rgba(37, 99, 235, 0.05) 50%, transparent 100%);
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 10s;
}

@keyframes glowFloat {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

/* Content */
.content {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 600px;
  animation: fadeIn 1s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.maintenance-icon {
  font-size: 6rem;
  margin: 0 0 2rem 0;
  display: inline-block;
  animation: swing 3s ease-in-out infinite;
  transform-origin: center top;
  filter: drop-shadow(0 0 30px rgba(59, 130, 246, 0.5));
}

@keyframes swing {
  0%,
  100% {
    transform: rotate(-15deg);
  }
  50% {
    transform: rotate(15deg);
  }
}

.title {
  font-size: 2.5rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 1.5rem 0;
  line-height: 1.15;
  letter-spacing: -0.03em;
}

.message {
  font-size: 1.2rem;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 0.5rem 0;
  line-height: 1.5;
  letter-spacing: -0.01em;
}

.message:last-of-type {
  margin-bottom: 3rem;
}

.action-column {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  margin-top: 0.5rem;
}

.refresh-button {
  --background: transparent;
  --background-hover: transparent;
  --background-activated: transparent;
  --color: #ffffff;
  --border-radius: 16px;
  --padding-start: 2.5rem;
  --padding-end: 2.5rem;
  --box-shadow: none;
  height: 56px;
  font-weight: 600;
  font-size: 1.05rem;
  letter-spacing: -0.01em;
  position: relative;
  margin: 0;
}

.refresh-button::part(native) {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.65) 0%, rgba(37, 99, 235, 0.7) 100%);
  border-radius: 16px;
  border: 1px solid rgba(59, 130, 246, 0.4);
  backdrop-filter: blur(40px) saturate(180%);
  -webkit-backdrop-filter: blur(40px) saturate(180%);
  box-shadow:
    0 8px 32px rgba(37, 99, 235, 0.4),
    0 1px 2px rgba(255, 255, 255, 0.2) inset,
    0 -1px 2px rgba(0, 0, 0, 0.15) inset;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.refresh-button::part(native)::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.15) 0%,
    rgba(255, 255, 255, 0) 50%,
    rgba(0, 0, 0, 0.1) 100%
  );
  pointer-events: none;
}

.refresh-button:hover::part(native) {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.8) 0%, rgba(37, 99, 235, 0.85) 100%);
  border-color: rgba(59, 130, 246, 0.6);
  transform: translateY(-2px);
  box-shadow:
    0 12px 40px rgba(37, 99, 235, 0.5),
    0 1px 2px rgba(255, 255, 255, 0.25) inset,
    0 -1px 2px rgba(0, 0, 0, 0.15) inset;
}

.refresh-button:active::part(native) {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.7) 0%, rgba(37, 99, 235, 0.75) 100%);
  border-color: rgba(59, 130, 246, 0.5);
  transform: translateY(0);
  box-shadow:
    0 4px 20px rgba(37, 99, 235, 0.35),
    0 1px 2px rgba(255, 255, 255, 0.15) inset,
    0 -1px 2px rgba(0, 0, 0, 0.2) inset;
}

.timer {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 0.25rem;
}

.timer-text {
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.5);
  font-weight: 400;
}

.timer-value {
  font-size: 1rem;
  color: rgba(59, 130, 246, 1);
  font-weight: 600;
  font-variant-numeric: tabular-nums;
  min-width: 28px;
  text-align: center;
}

@media (max-width: 640px) {
  .maintenance-icon {
    font-size: 5rem;
  }

  .title {
    font-size: 2rem;
  }

  .message {
    font-size: 1.1rem;
  }
}
</style>
