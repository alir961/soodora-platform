<script setup lang="ts">
import { IonButton } from '@ionic/vue';
import { Router, useRouter } from 'vue-router';
import { computed } from 'vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const router: Router = useRouter();
const props = defineProps({
  title: String,
  button: String,
});

const isSuccess = computed(() => {
  const title = props.title?.toLowerCase() || '';
  return title.includes('validated') || title.includes('success') || title.includes('completed');
});

async function goToMarketplace() {
  await router.push('/marketplace');
}
</script>
<template>
  <ApplicationViewport>
    <div class="payment-result-container">
      <div class="ambient-glow">
        <div class="glow-orb orb-1" :class="{ success: isSuccess }"></div>
        <div class="glow-orb orb-2" :class="{ success: isSuccess }"></div>
        <div class="glow-orb orb-3" :class="{ success: isSuccess }"></div>
      </div>

      <div class="content">
        <div class="icon-wrapper">
          <div class="status-icon" :class="{ success: isSuccess }">
            {{ isSuccess ? '✓' : '✕' }}
          </div>
        </div>

        <h1 class="title">{{ props.title }}</h1>

        <ion-button @click="goToMarketplace" class="action-button">
          <span>{{ 'Back to marketplace' }}</span>
        </ion-button>
      </div>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.payment-result-container {
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
  transition: background 0.5s ease;
}

.glow-orb.orb-1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(239, 68, 68, 0.25) 0%, rgba(239, 68, 68, 0.08) 50%, transparent 100%);
  top: -150px;
  left: -150px;
}

.glow-orb.orb-1.success {
  background: radial-gradient(circle, rgba(34, 197, 94, 0.25) 0%, rgba(34, 197, 94, 0.08) 50%, transparent 100%);
}

.glow-orb.orb-2 {
  width: 450px;
  height: 450px;
  background: radial-gradient(circle, rgba(239, 68, 68, 0.2) 0%, rgba(239, 68, 68, 0.06) 50%, transparent 100%);
  bottom: -100px;
  right: -100px;
  animation-delay: 5s;
}

.glow-orb.orb-2.success {
  background: radial-gradient(circle, rgba(34, 197, 94, 0.2) 0%, rgba(34, 197, 94, 0.06) 50%, transparent 100%);
}

.glow-orb.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(239, 68, 68, 0.18) 0%, rgba(239, 68, 68, 0.05) 50%, transparent 100%);
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 10s;
}

.glow-orb.orb-3.success {
  background: radial-gradient(circle, rgba(34, 197, 94, 0.18) 0%, rgba(34, 197, 94, 0.05) 50%, transparent 100%);
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

.icon-wrapper {
  margin-bottom: 2rem;
  animation: scaleIn 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.5);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.status-icon {
  width: 120px;
  height: 120px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.15), rgba(220, 38, 38, 0.1));
  border-radius: 50%;
  border: 2px solid rgba(239, 68, 68, 0.3);
  font-size: 4rem;
  color: rgba(239, 68, 68, 1);
  filter: drop-shadow(0 0 30px rgba(239, 68, 68, 0.5));
  animation: pulse 2s ease-in-out infinite;
}

.status-icon.success {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.15), rgba(22, 163, 74, 0.1));
  border-color: rgba(34, 197, 94, 0.3);
  color: rgba(34, 197, 94, 1);
  filter: drop-shadow(0 0 30px rgba(34, 197, 94, 0.5));
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.title {
  font-size: 2.5rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 3rem 0;
  line-height: 1.15;
  letter-spacing: -0.03em;
}

.action-button {
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

.action-button::part(native) {
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

.action-button::part(native)::before {
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

.action-button:hover::part(native) {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.8) 0%, rgba(37, 99, 235, 0.85) 100%);
  border-color: rgba(59, 130, 246, 0.6);
  transform: translateY(-2px);
  box-shadow:
    0 12px 40px rgba(37, 99, 235, 0.5),
    0 1px 2px rgba(255, 255, 255, 0.25) inset,
    0 -1px 2px rgba(0, 0, 0, 0.15) inset;
}

.action-button:active::part(native) {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.7) 0%, rgba(37, 99, 235, 0.75) 100%);
  border-color: rgba(59, 130, 246, 0.5);
  transform: translateY(0);
  box-shadow:
    0 4px 20px rgba(37, 99, 235, 0.35),
    0 1px 2px rgba(255, 255, 255, 0.15) inset,
    0 -1px 2px rgba(0, 0, 0, 0.2) inset;
}

@media (max-width: 640px) {
  .status-icon {
    width: 100px;
    height: 100px;
    font-size: 3.5rem;
  }

  .title {
    font-size: 2rem;
  }
}
</style>
