<script setup lang="ts">
import { onIonViewWillEnter } from '@ionic/vue';
import { saveBearerToken, loadBearerToken, deleteBearerToken } from '@/store/localStorage';
import { Ref, ref } from 'vue';
import { Router, useRouter, useRoute } from 'vue-router';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import AuthenticationForm from '@/component/auth/AuthenticationForm.vue';
import OAuthButton from '@/component/auth/OAuthButton.vue';
import { showToast } from '@/utils';

const isLogin: Ref<boolean> = ref(true);
const router: Router = useRouter();
const route = useRoute();

const toggleAuth = () => {
  isLogin.value = !isLogin.value;
};

const errorMessages = {
  oauth: 'Unable to connect to OAuth provider!',
  invalid: 'Invalid OAuth credentials, please try again!',
};

onIonViewWillEnter(async () => {
  const token = route.query.token as string | undefined;
  const error = route.query.error as string | undefined;

  if (route.query.external) isLogin.value = false;

  if (token) await saveBearerToken(token);
  if (error) {
    const message = error === 'oauth' ? errorMessages.oauth : errorMessages.invalid;
    await showToast(message, 'danger');
  }

  try {
    await loadBearerToken();
    await retrieveAuthenticatedProfile();

    await router.push('/explore');
  } catch (error) {
    await deleteBearerToken().catch(() => console.warn('Unable to delete bearer token', error));
  }
});
</script>

<template>
  <ApplicationViewport title="Authentication">
    <div class="auth-shell">
      <div class="bg-ornament one" />
      <div class="bg-ornament two" />
      <div class="bg-ornament three" />

      <section class="panel animate-in">
        <div class="panel-body">
          <header class="heading">
            <h1>{{ isLogin ? 'Welcome back' : 'Create your account' }}</h1>
            <p>
              {{ isLogin ? 'Log in to continue your journey with Soodora' : 'Join Soodora and get started in seconds' }}
            </p>
          </header>

          <div v-if="!route.query.external" class="segmented" role="tablist" aria-label="Authentication mode">
            <button role="tab" :aria-selected="isLogin" :class="{ active: isLogin }" @click="isLogin = true">
              Log in
            </button>
            <button role="tab" :aria-selected="!isLogin" :class="{ active: !isLogin }" @click="isLogin = false">
              Sign up
            </button>
            <span class="indicator" :style="{ transform: isLogin ? 'translateX(0%)' : 'translateX(100%)' }"></span>
          </div>

          <div class="form-slot">
            <AuthenticationForm :is-login="isLogin" />
          </div>

          <div v-if="!route.query.external" class="divider">
            <span></span>
            <em>or</em>
            <span></span>
          </div>

          <div v-if="!route.query.external" class="oauth">
            <OAuthButton class="google-button" provider="google" mode="auth" :fluid="true" />
          </div>

          <p v-if="!route.query.external" class="switch">
            {{ isLogin ? "Don't have an account?" : 'Already have an account?' }}
            <button class="link" @click="toggleAuth">{{ isLogin ? 'Sign up' : 'Log in' }}</button>
          </p>

          <div v-if="!route.query.external" class="dance-eq" aria-hidden="true">
            <span class="bar"></span>
            <span class="bar"></span>
            <span class="bar"></span>
            <span class="bar"></span>
            <span class="bar"></span>
            <span class="bar"></span>
            <span class="bar"></span>
          </div>
        </div>
      </section>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.auth-shell {
  --shell-pad: 16px;
  position: relative;
  height: 100%;
  width: 100%;
  padding: var(--shell-pad);
  display: grid;
  place-items: center;
  overflow: hidden;
}

.bg-ornament {
  position: absolute;
  border-radius: 999px;
  filter: blur(60px);
  opacity: 0.5;
  pointer-events: none;
  mix-blend-mode: screen;
  animation: float 14s ease-in-out infinite alternate;
}
.bg-ornament.one {
  width: 420px;
  height: 420px;
  background: radial-gradient(circle, rgba(56, 128, 255, 0.4), rgba(56, 128, 255, 0.06));
  top: -80px;
  left: -80px;
}
.bg-ornament.two {
  width: 520px;
  height: 520px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.32), rgba(59, 130, 246, 0.05));
  bottom: -140px;
  right: -120px;
  animation-delay: 0.6s;
}
.bg-ornament.three {
  width: 360px;
  height: 360px;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.26), rgba(96, 165, 250, 0.04));
  bottom: 20%;
  left: 10%;
  animation-delay: 1.2s;
}
@keyframes float {
  0% {
    transform: translateY(0px) translateX(0px) scale(1);
    opacity: 0.55;
  }
  100% {
    transform: translateY(-12px) translateX(8px) scale(1.04);
    opacity: 0.75;
  }
}

.panel {
  --panel-pad: clamp(20px, 4vw, 32px);
  position: relative;
  width: min(92vw, 90%);
  height: calc(100% - (var(--shell-pad) * 2));
  background: rgba(18, 22, 30, 0.68);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 20px;
  padding: 0;
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  overflow: hidden;
}

.panel-body {
  height: 100%;
  padding: var(--panel-pad);
  overflow: auto;
  overscroll-behavior: contain;
  -webkit-overflow-scrolling: touch;
  scroll-behavior: smooth;
  scroll-padding: var(--panel-pad);
  scrollbar-gutter: stable both-edges;
}

.heading {
  padding-top: 30px;
  text-align: center;
  margin-bottom: 18px;
}
.heading h1 {
  font-size: clamp(22px, 3.2vw, 28px);
  line-height: 1.2;
  letter-spacing: -0.01em;
  margin: 0;
}
.heading p {
  margin: 8px 0 0 0;
  color: #a3a3a3;
  font-size: 14px;
}

.segmented {
  position: relative;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
  padding: 6px;
  margin: 14px 0 10px 0;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 15px;
}
.segmented button {
  position: relative;
  z-index: 1;
  appearance: none;
  border: 0;
  background: transparent;
  color: #cfcfcf;
  font-weight: 600;
  padding: 10px 4px;
  border-radius: 10px;
  cursor: pointer;
  transition: color 0.2s ease;
}
.segmented button.active {
  color: #fff;
}
.segmented .indicator {
  position: absolute;
  z-index: 0;
  top: 6px;
  left: 6px;
  width: calc(50% - 6px);
  height: calc(100% - 12px);
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(10, 74, 178, 0.755), rgba(9, 125, 234, 0.759));
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.08),
    0 6px 18px rgba(56, 128, 255, 0.22);
  transition: transform 0.26s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.form-slot {
  margin-top: 8px;
}

.divider {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 12px;
  margin: 18px 0 12px 0;
}
.divider span {
  height: 1px;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0), rgba(255, 255, 255, 0.18), rgba(255, 255, 255, 0));
}
.divider em {
  color: #a6a6a6;
  font-style: normal;
  font-size: 12px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  text-align: center;
  padding: 0 2px;
}

.switch {
  text-align: center;
  color: #9a9a9a;
  margin-top: 14px;
  font-size: 14px;
}
.switch .link {
  appearance: none;
  background: none;
  border: 0;
  color: #66a3ff;
  font-weight: 600;
  margin-left: 6px;
  cursor: pointer;
  padding: 0;
  transition:
    color 0.2s ease,
    text-decoration-color 0.2s ease;
}
.switch .link:hover {
  color: #8bb8ff;
  text-decoration: underline;
  text-underline-offset: 3px;
}

.dance-eq {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 20px;
  height: 60px;
  margin-top: 40px;
  pointer-events: none;
  opacity: 0.8;
  margin-bottom: 30px;
}
.dance-eq .bar {
  width: 8px;
  height: 70%;
  border-radius: 6px;
  background: linear-gradient(180deg, #8bb8ff, #2563eb);
  box-shadow: 0 6px 14px rgba(37, 99, 235, 0.25);
  transform-origin: bottom center;
  animation: eqBeat var(--dur, 1s) ease-in-out infinite;
}
.dance-eq .bar:nth-child(1) {
  --dur: 2.9s;
  animation-delay: -0.1s;
}
.dance-eq .bar:nth-child(2) {
  --dur: 2.75s;
  animation-delay: -0.2s;
}
.dance-eq .bar:nth-child(3) {
  --dur: 3.1s;
  animation-delay: -0.05s;
}
.dance-eq .bar:nth-child(4) {
  --dur: 2.85s;
  animation-delay: -0.25s;
}
.dance-eq .bar:nth-child(5) {
  --dur: 3.2s;
  animation-delay: -0.15s;
}
.dance-eq .bar:nth-child(6) {
  --dur: 2.38s;
  animation-delay: -0.3s;
}
.dance-eq .bar:nth-child(7) {
  --dur: 3.05s;
  animation-delay: -0.18s;
}

@keyframes eqBeat {
  0% {
    transform: scaleY(0.35);
    opacity: 0.95;
  }
  25% {
    transform: scaleY(1);
    opacity: 1;
  }
  50% {
    transform: scaleY(0.55);
    opacity: 0.98;
  }
  75% {
    transform: scaleY(0.85);
    opacity: 1;
  }
  100% {
    transform: scaleY(0.35);
    opacity: 0.95;
  }
}

@media (prefers-reduced-motion: reduce) {
  .dance-eq .bar {
    animation: none;
  }
}

@media (min-width: 920px) {
  .panel {
    width: 520px;
  }
}
</style>
