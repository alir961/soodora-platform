<script setup lang="ts">
import { addOutline, arrowBackOutline, locationOutline } from 'ionicons/icons';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import AppHeader from '@/component/AppHeader.vue';
import EventMap from '@/component/event/EventMap.vue';
import { callRetrieveArtistEvents } from '@/client/eventClient';
import { useRoute, useRouter } from 'vue-router';
import { computed, onMounted, Ref, ref } from 'vue';
import { IonIcon, onIonViewWillEnter } from '@ionic/vue';
import { Event } from '@/entity/Event';
import { useAuthStore } from '@/store/reactiveStorage';
import { User } from '@/entity/User';

const route = useRoute();
const router = useRouter();
const artistSlug = route.params.slug as string;

const authStore = useAuthStore();

const events: Ref<Event[]> = ref([]);
const placedPinCoords = ref<{ lat: number; lng: number } | null>(null);
const eventMapRef = ref<InstanceType<typeof EventMap> | null>(null);
const showInstructionToast = ref(true);

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

const handlePinPlaced = (coords: { lat: number; lng: number }) => {
  placedPinCoords.value = coords;
  showInstructionToast.value = false;
};

const handlePinCleared = () => {
  placedPinCoords.value = null;
};

const handleCreateEvent = async () => {
  const user: User | null = authenticatedUser.value;

  if (!user || !placedPinCoords.value) return;

  // Pass coordinates via query params
  await router.push({
    path: `/user/${user.slug}/events/create`,
    query: {
      lat: placedPinCoords.value.lat.toString(),
      lng: placedPinCoords.value.lng.toString(),
    },
  });
};

const goBack = () => {
  router.back();
};

const dismissInstruction = () => {
  showInstructionToast.value = false;
};

onMounted(fetchArtistEvents);
onIonViewWillEnter(() => {
  fetchArtistEvents();
  placedPinCoords.value = null;
  showInstructionToast.value = true;
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
        <EventMap
          ref="eventMapRef"
          :events="events"
          :allow-pin-placement="canEditMap"
          @pin-placed="handlePinPlaced"
          @pin-cleared="handlePinCleared" />

        <!-- Instruction Banner (Top) -->
        <Transition name="slide-down">
          <div
            v-if="canEditMap && showInstructionToast && !placedPinCoords"
            class="map-banner instruction-banner"
            @click="dismissInstruction">
            <div class="banner-content">
              <div class="banner-icon-wrapper">
                <ion-icon :icon="locationOutline" />
              </div>
              <div class="banner-text">
                <span class="banner-title">Hold map to place pin</span>
                <span class="banner-subtitle">Long-press to drop a pin</span>
              </div>
            </div>
            <button class="banner-action-btn">Got it</button>
          </div>
        </Transition>

        <!-- Pin Placed Banner (Top) -->
        <Transition name="slide-down">
          <div v-if="placedPinCoords" class="map-banner pin-placed-banner">
            <div class="banner-content">
              <div class="banner-icon-wrapper success">
                <ion-icon :icon="locationOutline" />
              </div>
              <div class="banner-text">
                <span class="banner-title">Pin placed successfully</span>
                <span class="banner-subtitle">Tap <span class="highlight">+</span> to create event here</span>
              </div>
            </div>
          </div>
        </Transition>

        <!-- Floating Action Button -->
        <Transition name="scale">
          <button v-if="canEditMap && placedPinCoords" class="fab-button" @click="handleCreateEvent">
            <ion-icon :icon="addOutline" />
          </button>
        </Transition>
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

.header-edit-btn {
  width: 38px;
  height: 38px;
  border: none;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: white;
}

.header-edit-btn:hover {
  background: rgba(255, 255, 255, 0.12);
}

.header-edit-btn ion-icon {
  font-size: 20px;
}

/* Base Banner Styles */
.map-banner {
  position: absolute;
  top: 16px;
  left: 16px;
  right: 16px;
  margin: 0 auto;
  max-width: 400px;
  background: rgba(15, 23, 42, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  z-index: 100;
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.3),
    0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.banner-icon-wrapper {
  width: 40px;
  height: 40px;
  background: rgba(59, 130, 246, 0.15);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.banner-icon-wrapper.success {
  background: rgba(34, 197, 94, 0.15);
  border-color: rgba(34, 197, 94, 0.2);
}

.banner-icon-wrapper ion-icon {
  font-size: 20px;
  color: #60a5fa;
}

.banner-icon-wrapper.success ion-icon {
  color: #4ade80;
}

.banner-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
  min-width: 0;
}

.banner-title {
  font-size: 14px;
  font-weight: 600;
  color: white;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.banner-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.highlight {
  color: #60a5fa;
  font-weight: 700;
  font-size: 14px;
}

.banner-action-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.banner-action-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* Floating Action Button */
.fab-button {
  position: absolute;
  bottom: 24px;
  right: 24px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  box-shadow:
    0 8px 24px rgba(37, 99, 235, 0.4),
    0 4px 12px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  transition: all 0.2s ease;
}

.fab-button:hover {
  transform: scale(1.1);
  box-shadow:
    0 12px 32px rgba(37, 99, 235, 0.5),
    0 6px 16px rgba(0, 0, 0, 0.4);
}

.fab-button:active {
  transform: scale(0.95);
}

.fab-button ion-icon {
  font-size: 28px;
  color: white;
}

/* Safe area for absolute positioned elements on iOS */
@supports (padding-bottom: env(safe-area-inset-bottom)) {
  .fab-button {
    bottom: calc(24px + env(safe-area-inset-bottom));
  }
}

/* Transitions */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.slide-down-enter-from,
.slide-down-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}

.scale-enter-active,
.scale-leave-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.scale-enter-from,
.scale-leave-to {
  transform: scale(0);
  opacity: 0;
}
</style>
