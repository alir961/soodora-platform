<script setup lang="ts">
import { IonCardContent, IonCardHeader, IonCardSubtitle, IonCardTitle } from '@ionic/vue';
import { User } from '@/entity/User';
import { PropType } from 'vue';
import RoleTagComponent from '@/component/user/RoleTagComponent.vue';
import FollowCounter from '@/component/follow/FollowCounter.vue';

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
        <img v-else src="/assets/images/svg/avatar.svg" :alt="`${profile.username} avatar`" />
        {{ profile.username }}
      </ion-card-title>
      <ion-card-subtitle>Account information</ion-card-subtitle>
    </ion-card-header>

    <ion-card-content>
      <p>{{ profile.email }}</p>

      <RoleTagComponent v-for="role in profile.roles" :key="role" :role="role" class="role-tag" />

      <FollowCounter :user="profile" class="followers" />
    </ion-card-content>
  </div>
</template>

<style scoped>
#profile-container {
  color: #e0e0f0;
  font-family: 'Overpass', sans-serif;

  ion-card-title {
    display: flex;
    align-items: center;
    margin-top: 0.5em;
    font-weight: 700;
    font-size: 1.8em;
    letter-spacing: 0.05em;
    text-shadow: 0 0 2px rgba(0, 116, 228, 0.5);

    img {
      border-radius: 50%;
      height: 2.5em;
      width: 2.5em;
      margin-right: 0.75em;
      border: 1px solid #0074e4;
      box-shadow: 0 0 4px #0074e4;
      transition: transform 0.3s ease;
    }
    img:hover {
      transform: scale(1.1);
    }
  }

  ion-card-subtitle {
    color: #b0b0d0;
    font-weight: 500;
    font-size: 1.05em;
    margin-bottom: 1.2em;
  }

  ion-card-content {
    p {
      font-size: 1.1em;
      line-height: 1.6em;
      margin-bottom: 1.5em;
      color: #c0c0e0;
      user-select: text;
    }

    strong {
      font-size: 1.3em;
      line-height: 1.8em;
      color: #db3030;
      font-weight: 700;
    }

    .followers {
      width: 100%;
      margin-top: 1.5em;
      border-top: 1px solid #3a3a5a;
      padding-top: 1em;
    }

    .role-tag {
      padding: 0.2em 0.8em;
    }
  }
}
</style>
