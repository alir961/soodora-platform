<script setup lang="ts">
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonButton, IonCardTitle, IonCard, IonCardSubtitle, IonCardHeader, IonCardContent } from '@ionic/vue';
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
      <ion-header collapse="condense">
        <ion-toolbar>
          <div id="toolbar">
            <div id="toolbar-title">
              <ion-title size="large">Account</ion-title>
            </div>
          </div>
        </ion-toolbar>
      </ion-header>

      <div v-if="userInfo === null">
        <div id="loading-text">
          <p>Loading...</p>
        </div>
      </div>

      <div v-else>
        <div id="profile-container">
          <ion-card style="border-radius: 20px;">
            <ion-card-header>
              <ion-card-title>Welcome, {{ userInfo.username }}</ion-card-title>
              <ion-card-subtitle>Account information</ion-card-subtitle>
            </ion-card-header>
        
            <ion-card-content>
              <p>Email: {{ userInfo.email }}</p>
              <p>Firstname: {{ userInfo.firstname }}</p>
              <p>Lastname: {{ userInfo.lastname }}</p>
              <p>Roles: {{ userInfo.roles.toString() }}</p>
            </ion-card-content>
            <ion-button style="margin-bottom: 10%; padding-right: 30%; padding-left: 30%;" expand="block" color="danger" @click="logout">
              Log Out
            </ion-button>
          </ion-card>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<style scoped>

#toolbar {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 4%;
  margin-bottom: 4%;
}

#toolbar-title {
  padding-left: 3%;
  padding-top: 2%;
  padding-bottom: 2%;
}

#loading-text {
  text-align: center;
  top: 50%;
  transform: translateY(-50%);
  position: absolute;
  left: 0;
  right: 0;
  font-size: 2.0em;
  color: #8c8c8c;
  font-weight: 800;
}

#profile-container {
  padding: 5%;
  position: absolute;
  left: 0;
  right: 0;
  top: 40%;
  transform: translateY(-50%);
}

#profile-container strong {
  font-size: 1.5em;
  line-height: 2em;
}

#profile-container p {
  font-size: 1em;
  line-height: 1.375em;
  margin: 1em 0;
}

</style>