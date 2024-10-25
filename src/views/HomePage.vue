<template>
  <ion-page>
    <ion-header :translucent="true">
      <ion-toolbar>
        <ion-title>Home</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <ion-title size="large">Home</ion-title>
        </ion-toolbar>
      </ion-header>

      <div id="container">
        <strong>Coming soon</strong>
        <p>Start with
          <a 
              href="http://letmegooglethat.com/?q=How+to+make+a+home+page+in+vue" 
              target="_blank" 
              rel="noopener noreferrer" 
              style="text-decoration: none; color: #3880ff;">
              GrooveConductor
          </a>
        </p>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar } from '@ionic/vue';
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getToken } from '@/utils/localStorage';

const router = useRouter();

onMounted(async () => {
  const token = await getToken();
  
  if (!token) {
    router.push('/auth');
  }
});

</script>

<style scoped>
#container {
  text-align: center;
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

#container strong {
  font-size: 20px;
  line-height: 26px;
}

#container p {
  font-size: 16px;
  line-height: 22px;
  
  color: #8c8c8c;
  
  margin: 0;
}

#container a {
  text-decoration: none;
}
</style>
