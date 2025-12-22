<template>
  <Transition name="fade">
    <div v-if="showWarning" class="desktop-warning-overlay">
      <!-- Animated orbs background -->
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
      <div class="orb orb-4"></div>

      <div class="warning-content">
        <!-- Soodora Logo -->
        <div class="logo-container">
          <img src="/assets/images/png/banner-light.png" alt="Soodora" class="soodora-logo" />
        </div>

        <div class="icon-container">
          <ion-icon :icon="phonePortraitOutline" class="phone-icon"></ion-icon>
        </div>

        <h1 class="warning-title">Mobile Experience Only</h1>
        <p class="warning-message">Soodora is designed exclusively for mobile devices.</p>
        <p class="warning-submessage">
          Please open this site on your phone or resize your browser window for the optimal experience.
        </p>

        <div class="resize-hint">
          <ion-icon :icon="resizeOutline" class="resize-icon"></ion-icon>
          <span>{{ windowWidth }}px wide</span>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { IonIcon } from '@ionic/vue';
import { phonePortraitOutline, resizeOutline } from 'ionicons/icons';

const MAX_MOBILE_WIDTH = 768; // Standard mobile breakpoint
const showWarning = ref(false);
const windowWidth = ref(window.innerWidth);

const checkWindowSize = () => {
  windowWidth.value = window.innerWidth;
  showWarning.value = window.innerWidth > MAX_MOBILE_WIDTH;
};

onMounted(() => {
  checkWindowSize();
  window.addEventListener('resize', checkWindowSize);
});

onUnmounted(() => {
  window.removeEventListener('resize', checkWindowSize);
});
</script>

<style scoped>
.desktop-warning-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #0a0a0f;
  background:
    radial-gradient(ellipse at top left, rgba(23, 107, 253, 0.15) 0%, transparent 50%),
    radial-gradient(ellipse at bottom right, rgba(23, 107, 253, 0.1) 0%, transparent 50%), #0a0a0f;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  padding: 2rem;
  overflow: hidden;
}

/* Animated floating orbs */
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 20s ease-in-out infinite;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(23, 107, 253, 0.4) 0%, transparent 70%);
  top: -200px;
  left: -200px;
  animation-delay: 0s;
  animation-duration: 25s;
}

.orb-2 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(23, 107, 253, 0.3) 0%, transparent 70%);
  bottom: -150px;
  right: -150px;
  animation-delay: 5s;
  animation-duration: 30s;
}

.orb-3 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(91, 136, 246, 0.25) 0%, transparent 70%);
  top: 50%;
  right: 10%;
  animation-delay: 10s;
  animation-duration: 22s;
}

.orb-4 {
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, rgba(23, 107, 253, 0.2) 0%, transparent 70%);
  bottom: 20%;
  left: 15%;
  animation-delay: 7s;
  animation-duration: 28s;
}

@keyframes float {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(30px, -30px) scale(1.05);
  }
  50% {
    transform: translate(-20px, 20px) scale(0.95);
  }
  75% {
    transform: translate(20px, 30px) scale(1.02);
  }
}

.warning-content {
  background: rgba(22, 22, 32, 0.95);
  border: 1px solid rgba(23, 107, 253, 0.25);
  border-radius: 28px;
  padding: 3.5rem 3rem;
  max-width: 540px;
  text-align: center;
  box-shadow:
    0 30px 90px rgba(0, 0, 0, 0.8),
    0 0 0 1px rgba(23, 107, 253, 0.2) inset,
    0 0 60px rgba(23, 107, 253, 0.15);
  backdrop-filter: blur(40px) saturate(150%);
  -webkit-backdrop-filter: blur(40px) saturate(150%);
  animation: slideIn 0.8s cubic-bezier(0.16, 1, 0.3, 1);
  position: relative;
  z-index: 1;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-40px) scale(0.92);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.logo-container {
  margin-bottom: 2.5rem;
  animation: fadeInDown 1s cubic-bezier(0.16, 1, 0.3, 1) 0.2s both;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.soodora-logo {
  height: 50px;
  width: auto;
  filter: drop-shadow(0 4px 12px rgba(23, 107, 253, 0.4));
  animation: logoGlow 3s ease-in-out infinite;
}

@keyframes logoGlow {
  0%,
  100% {
    filter: drop-shadow(0 4px 12px rgba(23, 107, 253, 0.4));
  }
  50% {
    filter: drop-shadow(0 6px 20px rgba(23, 107, 253, 0.6));
  }
}

.icon-container {
  margin-bottom: 2rem;
  animation: floatIcon 3s ease-in-out infinite;
}

@keyframes floatIcon {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-12px);
  }
}

.phone-icon {
  font-size: 76px;
  color: #176bfd;
  filter: drop-shadow(0 8px 24px rgba(23, 107, 253, 0.5));
  opacity: 0.95;
}

.warning-title {
  font-family: 'Overpass', sans-serif;
  font-size: 2.25rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 1.25rem 0;
  line-height: 1.15;
  letter-spacing: -0.02em;
  text-shadow: 0 2px 20px rgba(23, 107, 253, 0.3);
}

.warning-message {
  font-family: 'Overpass', sans-serif;
  font-size: 1.15rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.85);
  margin: 0 0 1rem 0;
  line-height: 1.5;
  letter-spacing: -0.01em;
}

.warning-submessage {
  font-family: 'Overpass', sans-serif;
  font-size: 0.95rem;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.5);
  margin: 0 0 2.5rem 0;
  line-height: 1.6;
  letter-spacing: 0;
}

.resize-hint {
  display: inline-flex;
  align-items: center;
  gap: 0.625rem;
  padding: 0.875rem 1.75rem;
  background: rgba(23, 107, 253, 0.15);
  border: 1px solid rgba(23, 107, 253, 0.3);
  border-radius: 100px;
  color: rgba(255, 255, 255, 0.9);
  font-family: 'Overpass', sans-serif;
  font-weight: 600;
  font-size: 0.875rem;
  letter-spacing: 0.02em;
  box-shadow:
    0 4px 16px rgba(23, 107, 253, 0.2),
    0 0 0 1px rgba(23, 107, 253, 0.1) inset;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: pulseGlow 3s ease-in-out infinite;
}

@keyframes pulseGlow {
  0%,
  100% {
    box-shadow:
      0 4px 16px rgba(23, 107, 253, 0.2),
      0 0 0 1px rgba(23, 107, 253, 0.1) inset;
  }
  50% {
    box-shadow:
      0 6px 24px rgba(23, 107, 253, 0.4),
      0 0 0 1px rgba(23, 107, 253, 0.2) inset;
  }
}

.resize-hint:hover {
  background: rgba(23, 107, 253, 0.25);
  border-color: rgba(23, 107, 253, 0.5);
  transform: translateY(-2px);
  box-shadow:
    0 8px 24px rgba(23, 107, 253, 0.3),
    0 0 0 1px rgba(23, 107, 253, 0.2) inset;
}

.resize-icon {
  font-size: 18px;
}

/* Fade transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Responsive adjustments for smaller desktop screens */
@media (max-width: 900px) {
  .warning-content {
    padding: 3rem 2.5rem;
    max-width: 480px;
  }

  .soodora-logo {
    height: 45px;
  }

  .phone-icon {
    font-size: 68px;
  }

  .warning-title {
    font-size: 2rem;
  }

  .warning-message {
    font-size: 1.05rem;
  }

  .orb {
    filter: blur(60px);
  }
}

@media (max-width: 1024px) {
  .orb-1,
  .orb-2 {
    width: 300px;
    height: 300px;
  }

  .orb-3,
  .orb-4 {
    width: 250px;
    height: 250px;
  }
}
</style>
