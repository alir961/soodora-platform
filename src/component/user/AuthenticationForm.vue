<script setup lang="ts">
import { IonButton, IonInput, IonItem, toastController } from '@ionic/vue';
import { ref, Ref } from 'vue';
import { callLogin, callRegister, RegisterRequest } from '@/client/authenticationClient';
import { saveBearerToken } from '@/store/localStorage';
import { useAuthStore } from '@/store/reactiveStorage';
import { Router, useRoute, useRouter } from 'vue-router';

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
    await router.push('/explore');
    authStore.setAuthenticated(true);
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
  } catch (error) {
    const toast = await toastController.create({
      message: 'Sign up Failed!',
      duration: 1500,
      position: 'top',
    });

    await toast.present();
  }
};
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
</style>
