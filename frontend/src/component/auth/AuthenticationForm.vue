<script setup lang="ts">
import { computed, ref, Ref } from 'vue';
import { callLogin, callRegister, RegisterRequest } from '@/client/authenticationClient';
import { saveBearerToken } from '@/store/localStorage';
import { useAuthStore } from '@/store/reactiveStorage';
import { Router, useRoute, useRouter } from 'vue-router';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';
import { showToast } from '@/utils';
import { IonIcon } from '@ionic/vue';
import { eyeOutline, eyeOffOutline } from 'ionicons/icons';

const { isLogin } = defineProps({
  isLogin: Boolean,
});

const router: Router = useRouter();
const route = useRoute();

const authStore = useAuthStore();

const email: Ref<string> = ref('');
const password: Ref<string> = ref('');
const username: Ref<string> = ref('');
const firstname: Ref<string> = ref('');
const lastname: Ref<string> = ref('');
const identifier: Ref<string> = ref('');
const showPassword: Ref<boolean> = ref(false);

const login = async () => {
  try {
    const token = await callLogin({ identifier: identifier.value, password: password.value });

    await saveBearerToken(token);
    authStore.authenticate(await retrieveAuthenticatedProfile());
    await router.push('/explore');
  } catch (error: unknown) {
    const message = 'Login Failed, invalid credentials !' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
    console.warn(error);
  }
};

const register = async () => {
  try {
    const request: RegisterRequest = {
      username: username.value,
      firstname: firstname.value,
      lastname: lastname.value,
      email: email.value,
      password: password.value,
      external: route.query.external as string | undefined,
    };

    await callRegister(request);
    await router.push('/confirm');
  } catch (error: unknown) {
    const message = 'Sign up Failed. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};

const passwordCriteria = computed(() => {
  const pwd = password.value;
  const hasLowerCase = /[a-z]/.test(pwd);
  const hasUpperCase = /[A-Z]/.test(pwd);
  const hasDigit = /\d/.test(pwd);
  const hasSpecial = /[^a-zA-Z0-9]/.test(pwd);
  const minLength = pwd.length >= 6;

  return { hasLowerCase, hasUpperCase, hasDigit, hasSpecial, minLength };
});

const passwordTouched: Ref<boolean> = ref(false);

const allCriteria = computed(() => {
  const c = passwordCriteria.value;
  return [
    { key: 'minLength', label: '6+ characters', ok: c.minLength },
    { key: 'hasLowerCase', label: 'lowercase letter', ok: c.hasLowerCase },
    { key: 'hasUpperCase', label: 'uppercase letter', ok: c.hasUpperCase },
    { key: 'hasDigit', label: 'number', ok: c.hasDigit },
    { key: 'hasSpecial', label: 'symbol', ok: c.hasSpecial },
  ];
});
const unmetCriteria = computed(() => allCriteria.value.filter((r) => !r.ok).map((r) => r.label));
const metCount = computed(() => allCriteria.value.filter((r) => r.ok).length);
const strengthPercent = computed(() => Math.round((metCount.value / allCriteria.value.length) * 100));
const strengthLabel = computed(() => {
  const m = metCount.value;
  if (m <= 1) return 'Very weak';
  if (m === 2) return 'Weak';
  if (m === 3) return 'Fair';
  if (m === 4) return 'Strong';
  return 'Excellent';
});
</script>

<template>
  <div class="auth-form" @keyup.enter="isLogin ? login() : register()">
    <div v-if="!isLogin" class="row two-col">
      <div class="field with-prefix">
        <label>Username</label>
        <div class="input-prefix">
          <span :class="{ filled: !!username }" aria-hidden="true">@</span>
          <input placeholder="MarcusLee.wav" type="text" v-model="username" autocomplete="username" />
        </div>
      </div>
      <div class="field">
        <label>Firstname</label>
        <input placeholder="Marcus" type="text" v-model="firstname" autocomplete="given-name" />
      </div>
    </div>

    <div v-if="!isLogin" class="field">
      <label>Lastname</label>
      <input placeholder="Lee" type="text" v-model="lastname" autocomplete="family-name" />
    </div>

    <div v-if="!isLogin" class="field">
      <label>Email</label>
      <input placeholder="marcuslee.wav@example.com" type="email" v-model="email" autocomplete="email" />
    </div>
    <div v-else class="field">
      <label>Username or Email</label>
      <input placeholder="MarcusLee.wav" type="text" v-model="identifier" autocomplete="username" />
    </div>

    <div class="field">
      <label>Password</label>
      <div class="input-toggle">
        <input
          :type="showPassword ? 'text' : 'password'"
          placeholder="•••••••••••"
          v-model="password"
          autocomplete="current-password"
          @input="passwordTouched = true" />
        <button
          type="button"
          class="toggle"
          :aria-pressed="showPassword"
          :aria-label="showPassword ? 'Hide password' : 'Show password'"
          :title="showPassword ? 'Hide password' : 'Show password'"
          @click="showPassword = !showPassword">
          <ion-icon :icon="showPassword ? eyeOffOutline : eyeOutline" aria-hidden="true" />
        </button>
      </div>

      <div v-if="!isLogin && passwordTouched" class="pw-meta" aria-live="polite">
        <div class="strength" :data-label="strengthLabel">
          <span class="bar" :style="{ width: strengthPercent + '%' }"></span>
        </div>
        <transition-group name="chip" tag="div" class="chips" v-if="password.length > 0 && unmetCriteria.length">
          <span class="chip" v-for="label in unmetCriteria" :key="label">Add {{ label }}</span>
        </transition-group>
        <small v-else-if="password.length > 0" class="hint ok">Looking good ✓</small>
      </div>
    </div>

    <button class="primary-btn" @click="isLogin ? login() : register()">
      {{ isLogin ? 'Log in' : 'Create account' }}
    </button>
  </div>
</template>

<style scoped>
.auth-form {
  display: grid;
  gap: 14px;
}

.row.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
@media (max-width: 520px) {
  .row.two-col {
    grid-template-columns: 1fr;
  }
}

/* Field styling */
.field {
  display: grid;
  gap: 5px;
}
.field label {
  color: #cfcfcf;
  font-size: 12px;
  letter-spacing: 0.02em;
  padding-left: 2px;
}
.field input {
  appearance: none;
  outline: none;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: color-mix(in oklab, #0b0b0b 75%, #0f0f0f);
  color: #fff;
  padding: 12px 14px;
  border-radius: 12px;
  font-size: 15px;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease,
    transform 0.02s ease;
}
.field input::placeholder {
  color: #7f7f7f;
}
.field input:hover {
  border-color: rgba(255, 255, 255, 0.18);
}
.field input:focus {
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow:
    0 0 0 6px rgba(59, 130, 246, 0.1),
    0 8px 24px rgba(0, 0, 0, 0.25);
}

.pw-meta {
  display: grid;
  gap: 8px;
  margin-top: 6px;
}
.pw-meta .strength {
  position: relative;
  height: 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  overflow: hidden;
}
.pw-meta .strength .bar {
  display: block;
  height: 100%;
  width: 0%;
  border-radius: inherit;
  background: linear-gradient(90deg, #60a5fa, #3b82f6);
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.25);
  transition: width 0.25s ease;
}
.pw-meta .strength::after {
  content: attr(data-label);
  position: absolute;
  top: -18px;
  right: 0;
  color: #9aa3ad;
  font-size: 11px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.chip {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #cfd6de;
  font-size: 11px;
  padding: 6px 8px;
  border-radius: 8px;
  line-height: 1;
}
.hint.ok {
  color: #9aa3ad;
  font-size: 12px;
}

.chip-enter-active,
.chip-leave-active {
  transition:
    opacity 0.18s ease,
    transform 0.18s ease;
}
.chip-enter-from,
.chip-leave-to {
  opacity: 0;
  transform: translateY(4px);
}

.primary-btn {
  width: 100%;
  height: 46px;
  border: 0;
  border-radius: 12px;
  cursor: pointer;
  color: #fff;
  font-weight: 700;
  letter-spacing: 0.01em;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  box-shadow:
    0 10px 30px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
  transition:
    transform 0.06s ease,
    filter 0.2s ease,
    box-shadow 0.2s ease;
}
.primary-btn:hover {
  box-shadow:
    0 14px 36px rgba(37, 99, 235, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.16);
}
.primary-btn:active {
  transform: translateY(1px);
}

.with-prefix .input-prefix {
  position: relative;
  width: 100%;
}
.with-prefix .input-prefix > span {
  position: absolute;
  left: 15px;
  top: 51%;
  transform: translateY(-50%);
  color: #7f7f7f;
  font-weight: 600;
  font-size: 15px;
  pointer-events: none;
  user-select: none;
}
.with-prefix .input-prefix > span.filled {
  color: #ffffff;
}
.with-prefix .input-prefix > input {
  width: 100%;
  padding-left: 27px;
}

.input-toggle {
  position: relative;
  width: 100%;
}
.input-toggle > input {
  width: 100%;
  padding-right: 44px;
}
.input-toggle > .toggle {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  height: 32px;
  width: 32px;
  display: grid;
  place-items: center;
  border: 0;
  background: transparent !important;
  color: #7f7f7f;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.06s ease;
}
.input-toggle > .toggle:hover {
  background: transparent;
}
.input-toggle > .toggle:active {
  transform: translateY(-50%) scale(0.98);
}
.input-toggle > .toggle:focus-visible {
  outline: 2px solid rgba(59, 130, 246, 0.6);
  outline-offset: 2px;
}
.input-toggle > .toggle ion-icon {
  width: 20px;
  height: 20px;
  display: block;
}
</style>
