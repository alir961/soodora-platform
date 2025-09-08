<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { createMediaKit } from '@/client/mediaKitClient';
import type { MediaKitDTO } from '@/entity/MediaKit';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { IonButton, IonItem, IonInput, IonTextarea, IonLabel } from '@ionic/vue';
import { showToast } from '@/utils';

const router = useRouter();

const newMediaKit = ref<MediaKitDTO>({
  slug: '',
  title: '',
  shortBio: '',
  longBio: '',
  contactEmail: '',
  contactPhone: '',
  items: [],
});

const saveNewMediaKit = async () => {
  try {
    const created = await createMediaKit(newMediaKit.value);
    await router.push(`/mediakits/${created.slug}`);
    await showToast('Succeed to create MediaKit');
  } catch (error: unknown) {
    const message = 'Failed to create MediaKit. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};
</script>

<template>
  <ApplicationViewport title="Create Media Kit">
    <h2>Create New MediaKit</h2>
    <IonItem>
      <IonLabel position="stacked">Title</IonLabel>
      <IonInput v-model="newMediaKit.title" />
    </IonItem>
    <IonItem>
      <IonLabel position="stacked">Short Bio</IonLabel>
      <IonTextarea v-model="newMediaKit.shortBio" :rows="3" />
    </IonItem>
    <IonItem>
      <IonLabel position="stacked">Long Bio</IonLabel>
      <IonTextarea v-model="newMediaKit.longBio" :rows="5" />
    </IonItem>
    <IonItem>
      <IonLabel position="stacked">Contact Email</IonLabel>
      <IonInput v-model="newMediaKit.contactEmail" />
    </IonItem>
    <IonItem>
      <IonLabel position="stacked">Contact Phone</IonLabel>
      <IonInput v-model="newMediaKit.contactPhone" />
    </IonItem>
    <IonButton expand="block" color="primary" @click="saveNewMediaKit">Create MediaKit</IonButton>
  </ApplicationViewport>
</template>
