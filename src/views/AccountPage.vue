<script setup lang="ts">
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonInput, IonButton, IonItem } from '@ionic/vue';
import { getToken, getUserInfoFromPreferences, removeToken, removeUserInfo, saveToken, saveUserInfo } from '@/utils/localStorage';
import { onMounted, ref } from 'vue';
import axios from 'axios';

const email = ref('');
const password = ref('');
const username = ref('');
const firstname = ref('');
const lastname = ref('');
const isLogin = ref(true);
const userInfo = ref(null);
const isLogged = ref(false);
const apiUrl = import.meta.env.VITE_API_URL;

const getUserInfo = async () => {
  const token = await getToken();

  if (token) {
    try {
      const response = await axios.get(apiUrl + '/profile', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      userInfo.value = response.data;
      await saveUserInfo(userInfo);
      console.log('Succeed to fetch user info');
    } catch (error) {
      console.error('Failed to fetch user info:', error);
    }
  }
};

const login = async () => {
  try {
    const response = await axios.post(apiUrl + '/auth/login', {
      email: email.value,
      password: password.value
    });
    const token = response.data.token;
    await saveToken(token);
    isLogged.value = true;
    await getUserInfo();
    console.log('Login successful, Token:', token);
  } catch (error) {
    console.error('Login failed:', error);
  }
};

const toggleAuth = () => {
  isLogin.value = !isLogin.value;
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
    console.error('SignUp failed:', error.request.response);
  }
};

const logout = async () => {
  await removeToken();
  await removeUserInfo();
  isLogged.value = false;
  userInfo.value = null;
  console.log('Logged out successfully, token and user infos removed.');
};

onMounted(async () => {
  const token = await getToken();
  isLogged.value = token !== undefined && token !== null;
  userInfo.value = await getUserInfoFromPreferences();
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

      <div v-else-if="userInfo === null">
        <p>Loading...</p>
      </div>

      <div v-else>
        <div id="profile-container">
          <strong>Welcome, {{ userInfo.username }}</strong>
          <p>Email : {{ userInfo.email }}</p>
          <p>Firstname : {{ userInfo.firstname }}</p>
          <p>Lastname : {{ userInfo.lastname }}</p>

          <ion-button expand="block" color="danger" @click="logout">
            Log Out
          </ion-button>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<style scoped>
#auth-container {
  text-align: center;
  padding: 5%;
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

#auth-container a {
  text-decoration: none;
  color: #3880ff;
}

#profile-container {
  text-align: center;
  padding: 5%;
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

#profile-container strong {
  font-size: 1.5em;
  line-height: 2em;
}

#profile-container p {
  font-size: 1em;
  line-height: 1.375em;
  color: #8c8c8c;
  margin: 1em 0;
}

ion-button {
  margin-top: 5%;
}
</style>
