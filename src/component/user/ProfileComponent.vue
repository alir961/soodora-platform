<script setup lang="ts">
import { IonCardContent, IonCardHeader, IonCardSubtitle, IonCardTitle } from '@ionic/vue';
import { User } from '@/entity/User';
import { PropType } from 'vue';
import RoleTagComponent from '@/component/user/RoleTagComponent.vue';
import FollowersComponent from '@/component/user/FollowersComponent.vue';

defineProps({
  profile: {
    required: true,
    type: Object as PropType<User>,
  },
});
</script>

<template>
  <div id="profile-container">
    <ion-card-header>
      <ion-card-title>
        <img v-if="profile.avatarUrl" :src="profile.avatarUrl" :alt="`${profile.username} avatar`" />
        <img v-else src="/assets/avatar.svg" :alt="`${profile.username} avatar`" />
        {{ profile.username }}
      </ion-card-title>
      <ion-card-subtitle>Account information</ion-card-subtitle>
    </ion-card-header>

    <ion-card-content>
      <p>{{ profile.email }}</p>

      <RoleTagComponent v-for="role in profile.roles" :key="role" :role="role" />

      <FollowersComponent :profile="profile" class="followers" />
    </ion-card-content>
  </div>
</template>

<style scoped>
#profile-container {
  & ion-card-title {
    display: flex;
    align-items: end;
    margin-top: 0.5em;

    & img {
      border-radius: 100%;
      height: 1.5em;
      margin-right: 0.5em;
    }
  }

  & strong {
    font-size: 1.5em;
    line-height: 2em;
  }

  & p {
    font-size: 1em;
    line-height: 1.375em;
    margin: 0 0 1em 0;
  }

  .followers {
    width: 200px;
  }
}
</style>
