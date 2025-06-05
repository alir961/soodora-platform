<script setup lang="ts">
import { IonButton, onIonViewWillEnter, IonIcon, toastController } from '@ionic/vue';
import { saveBearerToken, loadBearerToken, deleteBearerToken } from '@/store/localStorage';
import { Ref, ref } from 'vue';
import { Router, useRouter, useRoute } from 'vue-router';
import { retrieveAuthenticatedProfile } from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { logoGoogle } from 'ionicons/icons';
import AuthenticationForm from '@/component/user/AuthenticationForm.vue';
import { redirectToUrl } from '@/axiosConfiguration';

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
    const toast = await toastController.create({
      message: message,
      duration: 1500,
      position: 'top',
    });

    await toast.present();
  }

  try {
    await loadBearerToken();
    await retrieveAuthenticatedProfile();

    await router.push('/explore');
  } catch (error) {
    await deleteBearerToken().catch(() => console.warn('Unable to delete bearer token', error));
  }
});

function redirectGoogleOAuth() {
  redirectToUrl(`${import.meta.env.VITE_API_URL}/auth/google`);
}
</script>

<template>
  <ApplicationViewport title="Authentication" fullscreen>
    <div id="container">
      <strong>{{ isLogin ? 'Welcome Back!' : 'Create Your Account' }}</strong>
      <p>{{ isLogin ? 'Please log in to continue' : 'Sign up to get started' }}</p>

      <AuthenticationForm :is-login="isLogin" @toggle="toggleAuth" />

      <div v-if="!route.query.external">
        <div class="spacer" />

        <ion-button expand="block" class="btn-google" @click="redirectGoogleOAuth">
          <ion-icon :icon="logoGoogle" />
          Connect with Google
        </ion-button>

        <p class="small-text">
          {{ isLogin ? 'Do not have an account?' : 'Already have an account?' }}
          <span @click="toggleAuth" class="interactive-link">{{ isLogin ? 'Sign up here' : 'Log in here' }}</span>
        </p>
      </div>
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

  & ion-button.btn-google {
    margin-top: 5%;

    & ion-icon {
      margin-right: 10px;
    }
  }

  .spacer {
    width: 100%;
    height: 1px;
    border-radius: 10px;
    margin: 20px 0;
    background-color: white;
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
