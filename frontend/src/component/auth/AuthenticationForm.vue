<script setup lang="ts">
import { IonButton, IonInput, IonItem, toastController } from '@ionic/vue';
import { computed, ref, Ref } from 'vue';
import { callLogin, callRegister, RegisterRequest } from '@/client/authenticationClient';
import { saveBearerToken } from '@/store/localStorage';
import { useAuthStore } from '@/store/reactiveStorage';
import { Router, useRoute, useRouter } from 'vue-router';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';

const { isLogin } = defineProps({
  isLogin: Boolean,
});

const router: Router = useRouter();
const route = useRoute();
const emit = defineEmits(['toggle']);

const authStore = useAuthStore();

const email: Ref<string> = ref('');
const password: Ref<string> = ref('');
const username: Ref<string> = ref('');
const firstname: Ref<string> = ref('');
const lastname: Ref<string> = ref('');

const login = async () => {
  try {
    const token = await callLogin({ email: email.value, password: password.value });
    await saveBearerToken(token);

    const user = await retrieveAuthenticatedProfile();
    authStore.authenticate(user);

    await router.push('/explore');
  } catch (error) {
    const toast = await toastController.create({
      message: 'Login Failed!',
      duration: 1500,
      position: 'top',
    });
    await toast.present();
  }
};

const signup = async () => {
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
    await router.push({ query: {} });
    emit('toggle');
  } catch (error: unknown) {
    let message: string = 'Sign up Failed!';
    if (error instanceof Error) message = error.message;

    const toast = await toastController.create({
      message: message,
      duration: 1500,
      position: 'top',
    });

    await toast.present();
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
</script>

<template>
  <div @keyup.enter="isLogin ? login() : signup()">
    <div v-if="!isLogin">
      <ion-item>
        <ion-input placeholder="Username" type="text" v-model="username"></ion-input>
      </ion-item>

      <ion-item>
        <ion-input placeholder="Firstname" type="text" v-model="firstname"></ion-input>
      </ion-item>

      <ion-item>
        <ion-input placeholder="Lastname" type="text" v-model="lastname"></ion-input>
      </ion-item>
    </div>

    <ion-item>
      <ion-input placeholder="Email" type="email" v-model="email"></ion-input>
    </ion-item>

    <ion-item>
      <ion-input placeholder="Password" type="password" v-model="password"></ion-input>
    </ion-item>

    <ul v-if="!isLogin" class="criteria">
      <li :style="{ color: passwordCriteria.minLength ? 'gray' : 'red' }">Minimum 6 characters</li>
      <li :style="{ color: passwordCriteria.hasLowerCase ? 'gray' : 'red' }">At least one lowercase letter</li>
      <li :style="{ color: passwordCriteria.hasUpperCase ? 'gray' : 'red' }">At least one uppercase letter</li>
      <li :style="{ color: passwordCriteria.hasDigit ? 'gray' : 'red' }">At least one digit</li>
      <li :style="{ color: passwordCriteria.hasSpecial ? 'gray' : 'red' }">At least one special character</li>
    </ul>

    <ion-button expand="block" @click="isLogin ? login() : signup()">
      {{ isLogin ? 'Log In' : 'Sign Up' }}
    </ion-button>
  </div>
</template>

<style scoped>
ion-item {
  margin-top: 5%;
}

ion-button {
  margin-top: 5%;
}

.criteria {
  color: red;
  margin-left: 1em;
  font-size: 0.9em;
  text-align: left;
}
</style>
