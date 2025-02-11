<script setup lang="ts">
import { IonButton, IonInput, IonItem, toastController, onIonViewWillEnter } from '@ionic/vue';
import { loadBearerToken, saveBearerToken } from '@/store/localStorage';
import { Ref, ref } from 'vue';
import { callLogin, callRegister, RegisterRequest } from '@/client/authenticationClient';
import { Router, useRouter } from 'vue-router';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';

const email: Ref<string> = ref('');
const password: Ref<string> = ref('');
const username: Ref<string> = ref('');
const firstname: Ref<string> = ref('');
const lastname: Ref<string> = ref('');
const isLogin: Ref<boolean> = ref(true);
const isLogged: Ref<boolean> = ref(false);
const router: Router = useRouter();

const login = async () => {
  try {
    const token = await callLogin({ email: email.value, password: password.value });

    await saveBearerToken(token);
    await router.push('/explore');
    isLogged.value = true;
    location.reload();
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
    };

    await callRegister(request);
  } catch (error) {
    const toast = await toastController.create({
      message: 'Sign up Failed!',
      duration: 1500,
      position: 'top',
    });

    await toast.present();
  }
};

const toggleAuth = () => {
  isLogin.value = !isLogin.value;
};

onIonViewWillEnter(async () => {
  try {
    await loadBearerToken();
    await retrieveAuthenticatedProfile();

    await router.push('/explore');
  } catch (e) {
    isLogged.value = false;
  }
});
</script>

<template>
  <ApplicationViewport title="Authentication" fullscreen>
    <div id="container">
      <strong>{{ isLogin ? 'Welcome Back!' : 'Create Your Account' }}</strong>
      <p>{{ isLogin ? 'Please log in to continue' : 'Sign up to get started' }}</p>

      <div v-if="isLogin">
        <ion-item>
          <ion-input placeholder="Email" type="email" v-model="email"></ion-input>
        </ion-item>

        <ion-item>
          <ion-input placeholder="Password" type="password" v-model="password"></ion-input>
        </ion-item>
      </div>

      <div v-else>
        <ion-item>
          <ion-input placeholder="Username" type="text" v-model="username"></ion-input>
        </ion-item>

        <ion-item>
          <ion-input placeholder="Firstname" type="text" v-model="firstname"></ion-input>
        </ion-item>

        <ion-item>
          <ion-input placeholder="Lastname" type="text" v-model="lastname"></ion-input>
        </ion-item>

        <ion-item>
          <ion-input placeholder="Email" type="email" v-model="email"></ion-input>
        </ion-item>

        <ion-item>
          <ion-input placeholder="Password" type="password" v-model="password"></ion-input>
        </ion-item>
      </div>

      <ion-button expand="block" @click="isLogin ? login() : signup()">
        {{ isLogin ? 'Log In' : 'Sign Up' }}
      </ion-button>

      <p class="small-text">
        {{ isLogin ? "Don't have an account?" : 'Already have an account?' }}
        <span @click="toggleAuth" class="interactive-link">{{ isLogin ? 'Sign up here' : 'Log in here' }}</span>
      </p>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
#container {
  text-align: center;
  padding: 5% 15%;
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  transform: translateY(-50%);

  & strong {
    font-size: 1.5em;
    line-height: 2em;
  }

  & p {
    font-size: 1em;
    line-height: 1.375em;
    color: #8c8c8c;
    margin: 1em 0;
  }

  & ion-item {
    margin-top: 5%;
  }

  & ion-button {
    margin-top: 5%;
  }
}

.small-text {
  margin-top: 4%;
  color: #8c8c8c;
  font-size: 0.875em;

  & .interactive-link {
    color: #3880ff;
    cursor: pointer;
    text-decoration: none;
    transition: color 0.3s ease;

    &:hover {
      text-decoration: underline;
      color: #0056b3;
    }
  }
}
</style>
