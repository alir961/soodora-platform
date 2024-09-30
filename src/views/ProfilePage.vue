<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import {IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonButton} from '@ionic/vue';
import axios from 'axios';
import {getToken, logout} from "@/utils/localStorage";

const userInfo = ref(null);
const router = useRouter();
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
      console.log('Succeed to fetch user info');
    } catch (error) {
      console.error('Failed to fetch user info:', error);
    }
  } else
    await router.push('/auth')
};

onMounted(() => {
  getUserInfo();
});
</script>

<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>Profile</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <ion-title size="large">Profile</ion-title>
        </ion-toolbar>
      </ion-header>

      <div v-if="userInfo == null">
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
#profile-container {
  text-align: center;
  padding: 20px;

  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

#profile-container strong {
  font-size: 24px;
  line-height: 32px;
}

#profile-container p {
  font-size: 16px;
  line-height: 22px;
  color: #8c8c8c;
  margin: 8px 0;
}

ion-button {
  margin-top: 20px;
}
</style>
