<script setup lang="ts">
import {IonCardContent, IonCardHeader, IonCardSubtitle, IonCardTitle} from "@ionic/vue";
import {User} from "@/entity/User";
import {PropType} from "vue";
import RoleTagComponent from "@/component/user/RoleTagComponent.vue";

defineProps({
  profile: {
    required: true,
    type: Object as PropType<User>,
  },
})
</script>

<template>
  <div id="profile-container">
    <ion-card-header>
      <ion-card-title>Welcome, {{ $props.profile.username }}</ion-card-title>
      <ion-card-subtitle>Account information</ion-card-subtitle>
    </ion-card-header>

    <ion-card-content>
      <p>{{ $props.profile.email }}</p>

      <RoleTagComponent v-for="role in $props.profile.roles" :key="role" :role="role"/>
    </ion-card-content>
  </div>
</template>

<style scoped>
#profile-container {

  & strong {
    font-size: 1.5em;
    line-height: 2em;
  }

  & p {
    font-size: 1em;
    line-height: 1.375em;
    margin: 0 0 1em 0;
  }
}

</style>