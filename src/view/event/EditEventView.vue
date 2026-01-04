<script setup lang="ts">
import { arrowBackOutline } from 'ionicons/icons';
import AppHeader from '@/component/AppHeader.vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import SaveEventForm from '@/component/event/SaveEventForm.vue';
import { Router, useRoute, useRouter } from 'vue-router';
import { onMounted, ref, Ref } from 'vue';
import { onIonViewWillEnter } from '@ionic/vue';
import { callRetrieveArtistEvents } from '@/client/eventClient';
import { Event } from '@/entity/Event';

const route = useRoute();
const router: Router = useRouter();
const artistSlug = route.params.slug as string;
const eventSlug = route.params.eventSlug as string;

const events: Ref<Event[]> = ref([]);
const event: Ref<Event | undefined> = ref(undefined);

const fetchArtistEvents = async () => {
  events.value = await callRetrieveArtistEvents(artistSlug);
  event.value = events.value.find((event: Event) => event.slug === eventSlug);
  if (!event.value) router.back();
};

onMounted(fetchArtistEvents);
onIonViewWillEnter(fetchArtistEvents);
</script>

<template>
  <ApplicationViewport>
    <AppHeader
      title="Artist Events"
      subtitle="Edit an existing event"
      :icon="arrowBackOutline"
      :on-icon-click="() => router.back()" />

    <SaveEventForm v-if="event" editing :event="event" />
  </ApplicationViewport>
</template>
