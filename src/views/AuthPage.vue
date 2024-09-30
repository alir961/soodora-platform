<script setup lang="ts">
import {IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonInput, IonButton, IonItem, IonLabel} from '@ionic/vue';
import {getToken, saveToken} from '@/utils/localStorage';
import { useRouter } from 'vue-router';
import {onMounted, ref} from 'vue';
import axios from 'axios';

const email = ref('');
const password = ref('');
const username = ref('');
const firstname = ref('');
const lastname = ref('');
const isLogin = ref(true);
const router = useRouter();
const apiUrl = import.meta.env.VITE_API_URL;

const login = async () => {
  try {
    const response = await axios.post(apiUrl + '/auth/login', {
      email: email.value,
      password: password.value
    });
    const token = response.data.token;
    await saveToken(token);
    await router.push('/profile');
    console.log('Login successful, Token:', token);
    window.location.reload();
  } catch (error) {
    console.error('Login failed:', error);
  }
};

const signup = async () => {
  try {
    const response = await axios.post(apiUrl + '/auth/register', {
      username: username.value,
      firstname: firstname.value,
      lastname: lastname.value,
      email: email.value,
      password: password.value
    });
    console.log('SignUp successful:', response);
    toggleAuth();
  } catch (error) {
    console.error('SignUp failed:', error);
  }
};

const toggleAuth = () => {
  isLogin.value = !isLogin.value;
};

onMounted(() => {
  if (getToken())
    router.push('/profile');
});
</script>

<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>{{ isLogin ? 'Login' : 'Sign Up' }}</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <ion-title size="large">{{ isLogin ? 'Login' : 'Sign Up' }}</ion-title>
        </ion-toolbar>
      </ion-header>

      <div id="auth-container">
        <strong>{{ isLogin ? 'Welcome Back!' : 'Create Your Account' }}</strong>
        <p>{{ isLogin ? 'Please log in to continue' : 'Sign up to get started' }}</p>

        <ion-item v-if="isLogin">
          <ion-label position="floating">Email</ion-label>
          <ion-input type="email" v-model="email"></ion-input>
        </ion-item>

        <ion-item v-if="isLogin">
          <ion-label position="floating">Password</ion-label>
          <ion-input type="password" v-model="password"></ion-input>
        </ion-item>

        <ion-item v-if="!isLogin">
          <ion-label position="floating">Username</ion-label>
          <ion-input type="text" v-model="username"></ion-input>
        </ion-item>

        <ion-item v-if="!isLogin">
          <ion-label position="floating">Firstname</ion-label>
          <ion-input type="text" v-model="firstname"></ion-input>
        </ion-item>

        <ion-item v-if="!isLogin">
          <ion-label position="floating">Lastname</ion-label>
          <ion-input type="text" v-model="lastname"></ion-input>
        </ion-item>

        <ion-item v-if="!isLogin">
          <ion-label position="floating">Email</ion-label>
          <ion-input type="email" v-model="email"></ion-input>
        </ion-item>

        <ion-item v-if="!isLogin">
          <ion-label position="floating">Password</ion-label>
          <ion-input type="password" v-model="password"></ion-input>
        </ion-item>

        <ion-button expand="block" @click="isLogin ? login() : signup()">
          {{ isLogin ? 'Log In' : 'Sign Up' }}
        </ion-button>

        <p class="small-text">
          {{ isLogin ? "Don't have an account?" : 'Already have an account?' }}
          <span @click="toggleAuth" class="interactive-link">{{ isLogin ? 'Sign up here' : 'Log in here' }}</span>
        </p>
      </div>
    </ion-content>
  </ion-page>
</template>

<style scoped>
#auth-container {
  text-align: center;
  padding: 20px;
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

#auth-container strong {
  font-size: 24px;
  line-height: 32px;
}

#auth-container p {
  font-size: 16px;
  line-height: 22px;
  color: #8c8c8c;
  margin: 8px 0;
}

#auth-container ion-item {
  margin-top: 20px;
}

.small-text {
  margin-top: 16px;
  color: #8c8c8c;
  font-size: 14px;
}

.interactive-link {
  color: #3880ff;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.3s ease;
}

.interactive-link:hover {
  text-decoration: underline;
  color: #0056b3;
}

#auth-container a {
  text-decoration: none;
  color: #3880ff;
}
</style>
