<script setup lang="ts">
import { PropType } from 'vue';
import { User } from '@/entity/User';
import {
  modalController,
  IonList,
  IonHeader,
  IonButtons,
  IonButton,
  IonTitle,
  IonToolbar,
  IonContent,
  IonIcon,
} from '@ionic/vue';
import UserListItem from '@/component/user/UserListItem.vue';
import { arrowBackOutline } from 'ionicons/icons';

defineProps({
  title: {
    type: String,
    required: true,
  },
  users: {
    type: Array as PropType<User[]>,
    required: true,
  },
});

const closeList = () => {
  modalController.dismiss(null, 'cancel');
};
</script>

<template>
  <ion-header>
    <ion-toolbar>
      <ion-buttons slot="start">
        <ion-button fill="clear" size="default" aria-label="Back" @click="closeList">
          <ion-icon :icon="arrowBackOutline" class="back-icon" />
          <span>Close</span>
        </ion-button>
      </ion-buttons>
      <ion-title>{{ title }}</ion-title>
    </ion-toolbar>
  </ion-header>
  <ion-content class="ion-padding">
    <ion-list>
      <UserListItem v-for="user in users" :key="user.slug" :user="user" @click="closeList" />
    </ion-list>
    <p v-if="users.length === 0" class="no-content">Nothing to display!</p>
  </ion-content>
</template>

<style scoped>
ion-header {
  position: relative;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  padding: 20px;

  & ion-toolbar {
    --background: var(--ion-background-color, #fff);

    & ion-icon {
      font-size: 1.2em;
      vertical-align: middle;
    }

    & span {
      margin-left: 0.1em;
      font-size: 1.2em;
    }

    & ion-title {
      color: white;
      font-size: 1.5em;
    }
  }
}

ion-content {
  padding: 1em;

  & ion-list {
    background: var(--ion-background-color, #fff);
  }

  & .no-content {
    width: 100%;
    text-align: center;
    font-size: 1.2em;
  }
}
</style>
