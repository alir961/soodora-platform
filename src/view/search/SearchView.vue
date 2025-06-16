<script setup lang="ts">
import { ref } from 'vue';
import { IonContent, IonSearchbar, IonList, IonItem, IonLabel } from '@ionic/vue';

import ApplicationViewport from '@/view/ApplicationViewport.vue';

const searchTerm = ref('');

// Function to be replaced with actual search logic
const onSearch = () => {
  // TODO: Call backend search endpoint with `searchTerm.value`
  // Example:
  // const { users, artists } = await searchPeople(searchTerm.value);
};
</script>

<template>
  <ApplicationViewport title="Search">
    <ion-content class="ion-padding">
      <ion-searchbar
        placeholder="Search for artists or users"
        v-model="searchTerm"
        @ionInput="onSearch"
        :debounce="300"></ion-searchbar>

      <div v-if="searchTerm.length > 0">
        <h2 class="section-title">Artists</h2>
        <ion-list>
          <!-- Replace with actual artist search results -->
          <!-- Example: artists.value.map(artist => <ion-item>...</ion-item>) -->
          <ion-item>
            <ion-label>No matching artists yet</ion-label>
          </ion-item>
        </ion-list>

        <h2 class="section-title">Users</h2>
        <ion-list>
          <!-- Replace with actual user search results -->
          <!-- Example: users.value.map(user => <ion-item>...</ion-item>) -->
          <ion-item>
            <ion-label>No matching users yet</ion-label>
          </ion-item>
        </ion-list>
      </div>
    </ion-content>
  </ApplicationViewport>
</template>

<style scoped>
.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-top: 20px;
  margin-bottom: 10px;
}
</style>
