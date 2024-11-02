<script setup lang="ts">
import {IonButton, IonCard, IonIcon, onIonViewWillEnter, toastController} from '@ionic/vue';
import {deleteBearerToken, deleteUserData} from '@/store/localStorage';
import {Ref, ref} from 'vue';
import {Router, useRoute, useRouter} from 'vue-router';
import {User} from '@/entity/User';
import {redirectLogin} from '@/utils';
import ProfileComponent from '@/component/user/ProfileComponent.vue';
import {OAuthProvider, retrieveAuthenticatedProfile, retrieveLinkedOAuthProviders} from '@/client/profileClient';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import OAuthButton from '@/view/user/OAuthButton.vue';
import {logOutOutline} from 'ionicons/icons';

const route = useRoute();
const router: Router = useRouter();
const userData: Ref<User | null> = ref(null);
const linkedProviders: Ref<OAuthProvider[]> = ref([]);
const providers: OAuthProvider[] = ['google'];

const fetchUserData = async () => {
  try {
    userData.value = await retrieveAuthenticatedProfile();
    linkedProviders.value = await retrieveLinkedOAuthProviders();
  } catch (error) {
    console.error('Failed to fetch user data:', error);
  }
};

const logout = async () => {
  await deleteBearerToken();
  await deleteUserData();
  await redirectLogin(router);
};

onIonViewWillEnter(async () => {
  if (route.query.error === 'oauth') {
    const toast = await toastController.create({
      message: 'Unable to connect to OAuth provider!',
      duration: 1500,
      position: 'top',
    });

    await toast.present();
  }

  await fetchUserData();
});
</script>

<template>
  <ApplicationViewport title="Account" authenticated>
    <div v-if="!userData" class="loading">
      <p>Loading...</p>
    </div>

    <ion-card v-else>
      <ProfileComponent :profile="userData"/>
    </ion-card>

    <OAuthButton v-for="provider in providers"
                 :key="provider"
                 :provider="provider"
                 :linked="linkedProviders.includes(provider)"
                 @unlink="fetchUserData"/>

    <ion-button expand="block" @click="logout" id="logout-button">
      <ion-icon :icon="logOutOutline"/>
      Logout
    </ion-button>
  </ApplicationViewport>
</template>

<style scoped>
.loading {
  text-align: center;
  font-size: 2.0em;
  color: #8c8c8c;
  font-weight: 800;
}

ion-card {
  border-radius: 20px;
  margin: 0 20px 20px 20px;
}

#logout-button {
  --background: #cc2222;
  --color: white;

  border-radius: 20px;
  margin: 0 20px 20px 20px;

  & ion-icon {
    margin-right: 10px;
  }
}
</style>