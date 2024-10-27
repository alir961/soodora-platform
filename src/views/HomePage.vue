<script setup lang="ts">
import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar } from '@ionic/vue';
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getToken } from '@/utils/localStorage';

const router = useRouter();

onMounted(async () => {
  const token = await getToken();
  
  if (!token) {
    await router.push('/auth');
  }
});

</script>

<template>
  <ion-page>
    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <div id="toolbar">
            <div id="toolbar-title">
              <ion-title size="large">Home</ion-title>
            </div>
          </div>
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

<style scoped>

ion-title{
  align-items: center;
}

#toolbar {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  margin-top: 4%;
  margin-bottom: 4%;
}

#toolbar-title{
  padding-left: 3%;
  padding-top: 2%;
  padding-bottom: 2%;
}

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
