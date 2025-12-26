<script setup lang="ts">
import { addOutline, createOutline, ellipsisHorizontal, mapOutline } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import AppHeader from '@/component/AppHeader.vue';
import EventMap from '@/component/event/EventMap.vue';
import { callRetrieveArtistEvents } from '@/client/eventClient';
import { useRoute, useRouter } from 'vue-router';
import { computed, onMounted, Ref, ref } from 'vue';
import { IonIcon, onIonViewWillEnter } from '@ionic/vue';
import { Event } from '@/entity/Event';
import ActionsPopup, { ActionItem } from '@/component/actions/ActionsPopup.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import { User } from '@/entity/User';

const route = useRoute();
const router = useRouter();
const artistSlug = route.params.slug as string;

const authStore = useAuthStore();

const isPopupDisplayed: Ref<boolean> = ref(false);
const popupX: Ref<number> = ref(0);
const popupY: Ref<number> = ref(0);

const events: Ref<Event[]> = ref([]);

const authenticatedUser = computed<User | null>(() => authStore.getAuthenticatedUser);

const canEditMap = computed<boolean>(() => {
  const user = authenticatedUser.value;
  const allowedRoles = new Set(['ADMIN', 'PREMIUM', 'ARTIST']);

  if (!user) return false;
  if (user.slug !== artistSlug) return false;
  return user.roles.some((role) => allowedRoles.has(role));
});

const fetchArtistEvents = async () => {
  events.value = await callRetrieveArtistEvents(artistSlug);
};

const openActionsPopup = (event: MouseEvent) => {
  const button = event.currentTarget as HTMLElement;
  const bounds = button.getBoundingClientRect();

  popupX.value = bounds.right - 220;
  popupY.value = bounds.bottom + 8;
  isPopupDisplayed.value = true;
};

const handleCreateEvent = async () => {
  const user: User | null = authenticatedUser.value;

  if (!user) return;
  await router.push(`/user/${user.slug}/events/create`);
};

const handleEditEvent = async () => {
  const user: User | null = authenticatedUser.value;

  if (!user) return;
  await router.push(`/user/${user.slug}/events/edit`);
};

const actionItems: ActionItem[] = [
  {
    icon: addOutline,
    label: 'Create Event',
    action: handleCreateEvent,
  },
  {
    icon: createOutline,
    label: 'Edit Event',
    action: handleEditEvent,
  },
];

onMounted(fetchArtistEvents);
onIonViewWillEnter(fetchArtistEvents);
</script>

<template>
  <ApplicationViewport>
    <div id="main-wrapper">
      <AppHeader
        title="Artists Events"
        :subtitle="`Map of ${authenticatedUser ? authenticatedUser.username : 'artist'} events`"
        :icon="mapOutline">
        <template #actions>
          <button v-if="canEditMap" class="actions-button" @click="openActionsPopup($event)">
            <ion-icon :icon="ellipsisHorizontal" />
          </button>
        </template>
      </AppHeader>

      <ActionsPopup
        :is-open="isPopupDisplayed"
        :x="popupX"
        :y="popupY"
        :actions="actionItems"
        @close="isPopupDisplayed = false" />

      <div class="map-host">
        <EventMap :events="events" />
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
}
</style>
