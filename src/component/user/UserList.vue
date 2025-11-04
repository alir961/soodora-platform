<script setup lang="ts">
import { PropType, ref } from 'vue';
import { User } from '@/entity/User';
import { modalController, IonList, IonButton, IonContent, IonIcon } from '@ionic/vue';
import UserListItem from '@/component/user/UserListItem.vue';
import { arrowBackOutline } from 'ionicons/icons';

const props = defineProps({
  title: {
    type: String,
    required: true,
  },
  users: {
    type: Array as PropType<User[]>,
    required: true,
  },
});

const displayedUsers = ref<User[]>(props.users);
</script>

<template>
  <ion-content>
    <div class="floating-header">
      <ion-button
        class="inline-back-button"
        fill="clear"
        size="large"
        aria-label="Back"
        @click="modalController.dismiss()">
        <ion-icon :icon="arrowBackOutline" class="back-icon" />
        <span>Back</span>
      </ion-button>
      <h2 class="floating-header-title">{{ title }}</h2>
    </div>
    <ion-list class="modal-item">
      <UserListItem
        v-for="user in displayedUsers"
        :key="user.slug"
        :user="user"
        @select="() => modalController.dismiss()" />
    </ion-list>
    <p v-if="displayedUsers.length === 0" class="no-content">Nothing to display!</p>
  </ion-content>
</template>

<style scoped>
.floating-header {
  position: relative;
  display: flex;
  flex-direction: row;
  align-items: center;
  padding-top: 3em;
  padding-bottom: 1em;
  margin-bottom: 1.5em;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.floating-header-title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 1.5em;
  color: var(--ion-color-light);
  z-index: 1;
  margin: 0;
}

.inline-back-button {
  font-size: 1.2em;
  color: var(--ion-color-primary);
  padding: 0 0.5em;
}

.back-icon {
  height: 20px;
  width: 20px;
  padding-bottom: 0.1em;
  margin-right: 0.2em;
  color: var(--ion-color-primary);
}

.modal-item {
  padding-left: 1.5em;
  padding-right: 1.5em;
}

ion-content {
  --padding-start: 1em;
  --padding-end: 1em;

  & .no-content {
    width: 100%;
    text-align: center;
    font-size: 1.2em;
    margin-top: 10em;
  }
}

ion-content::part(scroll) {
  padding: 0;
  background-color: var(--ion-color-primary-contrast);
}
</style>
