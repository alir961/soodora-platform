<script setup lang="ts">
import { IonButton, IonSpinner, onIonViewWillEnter } from '@ionic/vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { useRoute, useRouter } from 'vue-router';
import { computed, ref } from 'vue';
import { callConfirmEmail } from '@/client/authenticationClient';

const route = useRoute();
const router = useRouter();

const secret = computed(() => {
  return route.query.secret as string | undefined;
});

const isLoading = ref<boolean>(true);
const isConfirmed = ref<boolean>(false);

const redirectAuthentication = async () => {
  await router.push('/authentication');
};

onIonViewWillEnter(() => {
  if (secret.value)
    callConfirmEmail(secret.value)
      .then(() => (isConfirmed.value = true))
      .catch(() => (isConfirmed.value = false))
      .finally(() => {
        isLoading.value = false;
      });
});
</script>

<template>
  <ApplicationViewport>
    <div class="confirmation-container">
      <div class="ambient-glow">
        <div
          class="glow-orb orb-1"
          :class="{ success: !isLoading && isConfirmed, error: !isLoading && !isConfirmed }"></div>
        <div
          class="glow-orb orb-2"
          :class="{ success: !isLoading && isConfirmed, error: !isLoading && !isConfirmed }"></div>
        <div
          class="glow-orb orb-3"
          :class="{ success: !isLoading && isConfirmed, error: !isLoading && !isConfirmed }"></div>
      </div>

      <div class="content">
        <div v-if="secret && isLoading" class="loading-state">
          <div class="spinner-wrapper">
            <ion-spinner name="circular" class="custom-spinner" />
          </div>
          <h1 class="title">Confirming your email...</h1>
          <p class="message">Please wait while we verify your account.</p>
        </div>

        <div v-else-if="secret && !isLoading" class="result-state">
          <div class="icon-wrapper">
            <div class="status-icon" :class="{ success: isConfirmed, error: !isConfirmed }">
              {{ isConfirmed ? '✓' : '✕' }}
            </div>
          </div>

          <h1 class="title">{{ isConfirmed ? 'Welcome!' : 'Oops...' }}</h1>
          <p class="message">
            {{
              isConfirmed
                ? 'Your email has been successfully confirmed.'
                : "We couldn't confirm your email. Please try registering again."
            }}
          </p>
          <p v-if="isConfirmed" class="submessage">You can now log in to your account.</p>

          <ion-button @click="redirectAuthentication" class="action-button">
            <span>Go to Login</span>
          </ion-button>
        </div>

        <div v-else class="no-secret-state">
          <div class="icon-wrapper">
            <div class="info-icon">✉</div>
          </div>

          <h1 class="title">Confirm your email</h1>
          <p class="message">A confirmation email has been sent to your address.</p>
          <p class="submessage">Please check your inbox and click the confirmation link.</p>
        </div>
      </div>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.confirmation-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
  overflow: hidden;
  background: #0a0a0f;
}

/* Ambient Background */
.ambient-glow {
  position: absolute;
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
  background: radial-gradient(circle, rgba(59, 130, 246, 0.25) 0%, rgba(59, 130, 246, 0.08) 50%, transparent 100%);
}

.glow-orb.orb-1 {
  width: 500px;
  height: 500px;
  top: -150px;
  left: -150px;
}

.glow-orb.orb-1.success {
  background: radial-gradient(circle, rgba(34, 197, 94, 0.25) 0%, rgba(34, 197, 94, 0.08) 50%, transparent 100%);
}

.glow-orb.orb-1.error {
  background: radial-gradient(circle, rgba(239, 68, 68, 0.25) 0%, rgba(239, 68, 68, 0.08) 50%, transparent 100%);
}

.glow-orb.orb-2 {
  width: 450px;
  height: 450px;
  bottom: -100px;
  right: -100px;
  animation-delay: 5s;
}

.glow-orb.orb-2.success {
  background: radial-gradient(circle, rgba(34, 197, 94, 0.2) 0%, rgba(34, 197, 94, 0.06) 50%, transparent 100%);
}

.glow-orb.orb-2.error {
  background: radial-gradient(circle, rgba(239, 68, 68, 0.2) 0%, rgba(239, 68, 68, 0.06) 50%, transparent 100%);
}

.glow-orb.orb-3 {
  width: 400px;
  height: 400px;
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 10s;
}

.glow-orb.orb-3.success {
  background: radial-gradient(circle, rgba(34, 197, 94, 0.18) 0%, rgba(34, 197, 94, 0.05) 50%, transparent 100%);
}

.glow-orb.orb-3.error {
  background: radial-gradient(circle, rgba(239, 68, 68, 0.18) 0%, rgba(239, 68, 68, 0.05) 50%, transparent 100%);
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
  width: 100%;
}

.loading-state,
.result-state,
.no-secret-state {
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

/* Spinner */
.spinner-wrapper {
  margin-bottom: 2rem;
}

.custom-spinner {
  width: 80px;
  height: 80px;
  --color: rgba(59, 130, 246, 1);
  filter: drop-shadow(0 0 20px rgba(59, 130, 246, 0.5));
}

/* Status Icons */
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
  border-radius: 50%;
  font-size: 4rem;
  animation: pulse 2s ease-in-out infinite;
}

.status-icon.success {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.15), rgba(22, 163, 74, 0.1));
  border: 2px solid rgba(34, 197, 94, 0.3);
  color: rgba(34, 197, 94, 1);
  filter: drop-shadow(0 0 30px rgba(34, 197, 94, 0.5));
}

.status-icon.error {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.15), rgba(220, 38, 38, 0.1));
  border: 2px solid rgba(239, 68, 68, 0.3);
  color: rgba(239, 68, 68, 1);
  filter: drop-shadow(0 0 30px rgba(239, 68, 68, 0.5));
}

.info-icon {
  width: 120px;
  height: 120px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15), rgba(37, 99, 235, 0.1));
  border-radius: 50%;
  border: 2px solid rgba(59, 130, 246, 0.3);
  font-size: 4rem;
  color: rgba(59, 130, 246, 1);
  filter: drop-shadow(0 0 30px rgba(59, 130, 246, 0.5));
  animation: pulse 2s ease-in-out infinite;
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

/* Typography */
.title {
  font-size: 2.5rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 1rem 0;
  line-height: 1.15;
  letter-spacing: -0.03em;
}

.message {
  font-size: 1.2rem;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 0.5rem 0;
  line-height: 1.5;
  letter-spacing: -0.01em;
}

.submessage {
  font-size: 1rem;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.5);
  margin: 0;
  line-height: 1.6;
}

/* Action Button */
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
  margin: 2rem 0 0 0;
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
  .status-icon,
  .info-icon {
    width: 100px;
    height: 100px;
    font-size: 3.5rem;
  }

  .title {
    font-size: 2rem;
  }

  .message {
    font-size: 1.1rem;
  }
}
</style>
