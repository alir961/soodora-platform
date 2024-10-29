<script setup lang="ts">
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonInput, IonButton, IonItem } from '@ionic/vue';
import { getToken, saveToken, saveUserInfo } from '@/utils/localStorage';
import { onMounted, ref } from 'vue';
import {getUserInfo, login as authLogin, signup as authSignup} from '@/services/authService';
import { useRouter } from 'vue-router';

const email = ref('');
const password = ref('');
const username = ref('');
const firstname = ref('');
const lastname = ref('');
const isLogin = ref(true);
const isLogged = ref(false);
const userInfo = ref(null);
const router = useRouter();

const login = async () => {
  try {
    const { token } = await authLogin(email.value, password.value);
    await saveToken(token);
    userInfo.value = await getUserInfo();
    await saveUserInfo(userInfo);
    isLogged.value = true;
    console.log('Login successful, Token:', token);

    await router.push('/home');
  } catch (error) {
    console.error('Login failed:', error);
  }
};

const signup = async () => {
  try {
    await authSignup(username.value, firstname.value, lastname.value, email.value, password.value);
    toggleAuth();
  } catch (error) {
    console.error('SignUp failed:', error);
  }
};

const toggleAuth = () => {
  isLogin.value = !isLogin.value;
};

onMounted(async () => {
  const token = await getToken();
  isLogged.value = token !== undefined && token !== null;
  if (isLogged.value) {
    await router.push('/home');
  }
});
</script>

<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>GrooveConductor</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <div id="toolbar">
            <div id="toolbar-title">
              <ion-title size="large">GrooveConductor</ion-title>
            </div>
          </div>
        </ion-toolbar>
      </ion-header>

      <div id="auth-container" v-if="!isLogged">
        <strong>{{ isLogin ? 'Welcome Back!' : 'Create Your Account' }}</strong>
        <p>{{ isLogin ? 'Please log in to continue' : 'Sign up to get started' }}</p>

        <ion-item v-if="isLogin">
          <ion-input placeholder="Email" type="email" v-model="email"></ion-input>
        </ion-item>

        <ion-item v-if="isLogin">
          <ion-input placeholder="Password" type="password" v-model="password"></ion-input>
        </ion-item>

        <ion-item v-if="!isLogin">
          <ion-input placeholder="Username" type="text" v-model="username"></ion-input>
        </ion-item>

        <ion-item v-if="!isLogin">
          <ion-input placeholder="Firstname" type="text" v-model="firstname"></ion-input>
        </ion-item>

        <ion-item v-if="!isLogin">
          <ion-input placeholder="Lastname" type="text" v-model="lastname"></ion-input>
        </ion-item>

        <ion-item v-if="!isLogin">
          <ion-input placeholder="Email" type="email" v-model="email"></ion-input>
        </ion-item>

        <ion-item v-if="!isLogin">
          <ion-input placeholder="Password" type="password" v-model="password"></ion-input>
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

ion-button {
  margin-top: 5%;
}

#toolbar {
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-top: 10%;
  margin-bottom: 4%;
}

#toolbar-title {
  padding-left: 3%;
  padding-top: 2%;
  padding-bottom: 2%;
}

#auth-container {
  text-align: center;
  padding: 5%;
  padding-left: 15%;
  padding-right: 15%;
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

#auth-container strong {
  font-size: 1.5em;
  line-height: 2em;
}

#auth-container p {
  font-size: 1em;
  line-height: 1.375em;
  color: #8c8c8c;
  margin: 1em 0;
}

#auth-container ion-item {
  margin-top: 5%;
}

.small-text {
  margin-top: 4%;
  color: #8c8c8c;
  font-size: 0.875em;
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

</style>