<script setup lang="ts">
import { arrowBackOutline } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import AppHeader from '@/component/AppHeader.vue';
import EventMap from '@/component/event/EventMap.vue';
import { callRetrieveAllEvents } from '@/client/eventClient';
import { useRouter } from 'vue-router';
import { onMounted, Ref, ref } from 'vue';
import { onIonViewWillEnter } from '@ionic/vue';
import { Event } from '@/entity/Event';

const router = useRouter();

const events: Ref<Event[]> = ref([]);
const eventMapRef = ref<InstanceType<typeof EventMap> | null>(null);

const fetchArtistEvents = async () => {
  events.value = await callRetrieveAllEvents();
};

const goBack = () => {
  router.back();
};

onMounted(fetchArtistEvents);
onIonViewWillEnter(() => {
  fetchArtistEvents();
});
</script>

<template>
  <ApplicationViewport>
    <div id="main-wrapper">
      <AppHeader
        title="Events Map"
        :subtitle="`${events.length} event${events.length !== 1 ? 's' : ''} on map`"
        :icon="arrowBackOutline"
        :on-icon-click="goBack">
      </AppHeader>

      <div class="map-host">
        <EventMap ref="eventMapRef" :events="events" />
      </div>
    </div>
  </ApplicationViewport>
</template>

<style scoped>
#main-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

.map-host {
  flex: 1;
  position: relative;
  width: 100%;
  min-height: 0;
  overflow: hidden;
}

.fab-button ion-icon {
  font-size: 28px;
  color: white;
}
</style>
