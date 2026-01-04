<script setup lang="ts">
import { arrowBackOutline } from 'ionicons/icons';
import AppHeader from '@/component/AppHeader.vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { callRetrieveArtistEvents } from '@/client/eventClient';
import { onMounted, ref, Ref } from 'vue';
import { Event } from '@/entity/Event';
import EventListItem from '@/component/event/EventListItem.vue';
import { Router, useRoute, useRouter } from 'vue-router';
import { onIonViewWillEnter } from '@ionic/vue';

const route = useRoute();
const router: Router = useRouter();
const artistSlug = route.params.slug as string;

const events: Ref<Event[]> = ref([]);

const fetchArtistEvents = async () => {
  events.value = await callRetrieveArtistEvents(artistSlug);
};

onMounted(fetchArtistEvents);
onIonViewWillEnter(fetchArtistEvents);
</script>

<template>
  <ApplicationViewport id="viewport">
    <AppHeader
      title="Artist Events"
      subtitle="Edit or delete an event"
      :icon="arrowBackOutline"
      :on-icon-click="() => router.back()" />

    <div class="ambient-glow">
      <div class="glow-orb" />
      <div class="glow-orb" />
      <div class="glow-orb" />
    </div>

    <EventListItem v-for="event in events" :event="event" :key="event.slug" />
  </ApplicationViewport>
</template>

<style scoped>
.ambient-glow {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;

  & .glow-orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(100px);
    opacity: 1;
    animation: glowFloat 10s ease-in-out infinite;

    &:nth-child(1) {
      width: 500px;
      height: 500px;
      background: radial-gradient(circle, rgba(59, 130, 246, 0.25) 0%, rgba(59, 130, 246, 0.08) 50%, transparent 100%);
      top: -150px;
      left: -150px;
    }

    &:nth-child(2) {
      width: 450px;
      height: 450px;
      background: radial-gradient(circle, rgba(96, 165, 250, 0.2) 0%, rgba(96, 165, 250, 0.06) 50%, transparent 100%);
      bottom: -100px;
      right: -100px;
    }

    &:nth-child(3) {
      width: 400px;
      height: 400px;
      background: radial-gradient(circle, rgba(37, 99, 235, 0.18) 0%, rgba(37, 99, 235, 0.05) 50%, transparent 100%);
      top: 40%;
      left: 50%;
      transform: translate(-50%, -50%);
    }
  }
}

@keyframes glowFloat {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(-20px, -25px) scale(1.05);
  }
  66% {
    transform: translate(20px, 20px) scale(0.95);
  }
}
</style>
