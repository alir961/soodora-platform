<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import {
  IonButton,
  IonItem,
  IonInput,
  IonLabel,
  IonTextarea,
  IonIcon,
  IonHeader,
  IonToolbar,
  IonTitle,
} from '@ionic/vue';
import { arrowBackOutline } from 'ionicons/icons';
import { showToast } from '@/utils';
import { createQuote } from '@/client/quoteClient';
import type { CreateQuoteRequest } from '@/entity/Quote';

const router = useRouter();

const newQuote = ref<CreateQuoteRequest>({
  name: '',
  clientName: '',
  clientEmail: '',
  clientAddress: '',
  clientPhone: '',
  description: '',
});

const saveNewQuote = async () => {
  try {
    console.log(newQuote.value);
    const created = await createQuote(newQuote.value);
    await router.push(`/quotes/${created.slug}`);
    await showToast('Succeed to create Quote');
  } catch (error: unknown) {
    const message = 'Failed to create Quote. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  }
};
</script>

<template>
  <ApplicationViewport title="Create Quote">
    <IonHeader>
      <IonButton class="inline-back-button" slot="start" fill="clear" @click="$router.back()">
        <IonIcon :icon="arrowBackOutline" />
        Back
      </IonButton>
      <IonToolbar>
        <IonTitle>Create Quote</IonTitle>
      </IonToolbar>
    </IonHeader>

    <IonItem>
      <IonLabel position="stacked">Quote Name</IonLabel>
      <IonInput v-model="newQuote.name" />
    </IonItem>

    <IonItem>
      <IonLabel position="stacked">Customer Name</IonLabel>
      <IonInput v-model="newQuote.clientName" />
    </IonItem>

    <IonItem>
      <IonLabel position="stacked">Customer Email</IonLabel>
      <IonInput type="email" v-model="newQuote.clientEmail" />
    </IonItem>

    <IonItem>
      <IonLabel position="stacked">Customer Address</IonLabel>
      <IonInput v-model="newQuote.clientAddress" />
    </IonItem>

    <IonItem>
      <IonLabel position="stacked">Customer Phone</IonLabel>
      <IonInput type="tel" v-model="newQuote.clientPhone" />
    </IonItem>

    <IonItem>
      <IonLabel position="stacked">Quote Description</IonLabel>
      <IonTextarea :rows="3" v-model="newQuote.description" />
    </IonItem>

    <IonButton expand="block" color="primary" @click="saveNewQuote"> Create Quote </IonButton>
  </ApplicationViewport>
</template>
