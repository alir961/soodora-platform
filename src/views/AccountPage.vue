<script setup lang="ts">
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonButton } from '@ionic/vue';
import {getToken, getUserInfoFromPreferences, removeToken, removeUserInfo} from '@/utils/localStorage';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const userInfo = ref(null);
const isLogged = ref(false);
const router = useRouter();

const fetchUserInfo = async () => {
  try {
    userInfo.value = await getUserInfoFromPreferences();
  } catch (error) {
    console.error('Failed to fetch user info:', error);
  }
};

const logout = async () => {
  await removeToken();
  await removeUserInfo();
  isLogged.value = false;
  await router.push('/auth');
};

onMounted(async () => {
  const token = await getToken();
  isLogged.value = token !== undefined && token !== null;

  if (!isLogged.value) {
    await router.push('/auth');
  } else {
    await fetchUserInfo();
  }
});
</script>

<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>Account</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <div v-if="userInfo === null">
        <p>Loading...</p>
      </div>

      <div v-else>
        <div id="profile-container">
          <strong>Welcome, {{ userInfo.username }}</strong>
          <p>Email: {{ userInfo.email }}</p>
          <p>Firstname: {{ userInfo.firstname }}</p>
          <p>Lastname: {{ userInfo.lastname }}</p>
          <p>Roles: {{ userInfo.roles.toString() }}</p>

          <ion-button expand="block" color="danger" @click="logout">
            Log Out
          </ion-button>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<style scoped>
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
  padding-top: 40px;
  padding-left: 35%;
  padding-right: 35%;

}
</style>