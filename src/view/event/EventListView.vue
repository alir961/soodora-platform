<script setup lang="ts">
import {
  arrowBackOutline,
  calendarOutline,
  linkOutline,
  createOutline,
  trashOutline,
  addOutline,
  mapOutline,
  ellipsisVertical,
} from 'ionicons/icons';
import AppHeader from '@/component/AppHeader.vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { callRetrieveArtistEvents, callDeleteEvent } from '@/client/eventClient';
import { onMounted, ref, Ref, computed } from 'vue';
import { Event, EventType } from '@/entity/Event';
import { Router, useRoute, useRouter } from 'vue-router';
import { onIonViewWillEnter, IonContent, IonIcon } from '@ionic/vue';
import { showToast } from '@/utils';

const route = useRoute();
const router: Router = useRouter();
const artistSlug = route.params.slug as string;

const events: Ref<Event[]> = ref([]);
const isLoading = ref(true);
const deletingSlug = ref<string | null>(null);
const activeMenuSlug = ref<string | null>(null);

const fetchArtistEvents = async () => {
  isLoading.value = true;
  try {
    events.value = await callRetrieveArtistEvents(artistSlug);
  } catch {
    events.value = [];
  } finally {
    isLoading.value = false;
  }
};

const eventsCount = computed(() => {
  return `${events.value.length} event${events.value.length !== 1 ? 's' : ''}`;
});

const formatShortDate = (dateTime: string): string => {
  const date = new Date(dateTime);
  const day = date.getDate();
  const month = date.toLocaleDateString('en-US', { month: 'short' });
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  return `${day} ${month} · ${hours}:${minutes}`;
};

const getEventTypeLabel = (type: EventType): string => {
  const labels: Record<EventType, string> = {
    CONCERT: 'Concert',
    FESTIVAL: 'Festival',
    COMPETITION: 'Competition',
    PERFORMANCE_SHOW: 'Show',
    EDUCATIONAL: 'Educational',
    DANCE_MUSIC: 'Dance',
    RELIGIOUS_SPIRITUAL: 'Spiritual',
    OTHER: 'Other',
  };
  return labels[type] || type;
};

const getEventTypeEmoji = (type: EventType): string => {
  const emojis: Record<EventType, string> = {
    CONCERT: '🎸',
    FESTIVAL: '🎪',
    COMPETITION: '🏆',
    PERFORMANCE_SHOW: '🎭',
    EDUCATIONAL: '📚',
    DANCE_MUSIC: '💃',
    RELIGIOUS_SPIRITUAL: '🙏',
    OTHER: '🎵',
  };
  return emojis[type] || '🎵';
};

const toggleMenu = (eventSlug: string) => {
  activeMenuSlug.value = activeMenuSlug.value === eventSlug ? null : eventSlug;
};

const closeMenu = () => {
  activeMenuSlug.value = null;
};

const editEvent = (eventSlug: string) => {
  closeMenu();
  router.push(`/user/${artistSlug}/events/${eventSlug}/edit`);
};

const deleteEvent = async (eventSlug: string) => {
  if (deletingSlug.value !== eventSlug) {
    deletingSlug.value = eventSlug;
    setTimeout(() => {
      if (deletingSlug.value === eventSlug) {
        deletingSlug.value = null;
      }
    }, 3000);
    return;
  }

  try {
    await callDeleteEvent(eventSlug);
    await showToast('Event deleted successfully');
    events.value = events.value.filter((e) => e.slug !== eventSlug);
  } catch {
    await showToast('Unable to delete event', 'warning');
  } finally {
    deletingSlug.value = null;
    closeMenu();
  }
};

const goToMap = () => {
  router.push(`/user/${artistSlug}/events`);
};

const goBack = () => {
  router.push('/profile');
};

onMounted(fetchArtistEvents);
onIonViewWillEnter(fetchArtistEvents);
</script>

<template>
  <ApplicationViewport>
    <IonContent class="events-content">
      <!-- Ambient Glow Background -->
      <div class="ambient-glow">
        <div class="glow-orb orb-1"></div>
        <div class="glow-orb orb-2"></div>
      </div>

      <div class="events-wrapper">
        <!-- Header -->
        <AppHeader title="My Events" :subtitle="eventsCount" :icon="arrowBackOutline" :on-icon-click="goBack">
          <template #actions>
            <button class="header-btn map-btn" @click="goToMap">
              <IonIcon :icon="mapOutline" />
            </button>
            <button class="header-btn add-btn" @click="goToMap">
              <IonIcon :icon="addOutline" />
            </button>
          </template>
        </AppHeader>

        <!-- Loading State -->
        <div v-if="isLoading" class="loading-state">
          <div class="spinner"></div>
        </div>

        <!-- Empty State -->
        <div v-else-if="events.length === 0" class="empty-state">
          <div class="empty-icon">
            <IonIcon :icon="calendarOutline" />
          </div>
          <p>No events yet</p>
          <button class="create-btn" @click="goToMap">
            <IonIcon :icon="addOutline" />
            Create Event
          </button>
        </div>

        <!-- Events List -->
        <div v-else class="events-list">
          <div v-for="event in events" :key="event.slug" class="event-item">
            <!-- Separator Line at top -->
            <div class="separator"></div>

            <!-- Event Card -->
            <div class="event-card">
              <!-- Left: Emoji Icon -->
              <div class="event-emoji">
                {{ getEventTypeEmoji(event.type) }}
              </div>

              <!-- Center: Event Details -->
              <div class="event-details" @click="editEvent(event.slug)">
                <div class="event-header">
                  <h3 class="event-title">{{ event.title }}</h3>
                  <span class="event-type">{{ getEventTypeLabel(event.type) }}</span>
                </div>

                <p v-if="event.description" class="event-description">{{ event.description }}</p>

                <div class="event-meta">
                  <span class="meta-item date-item">
                    <IonIcon :icon="calendarOutline" />
                    {{ formatShortDate(event.dateTime) }}
                  </span>
                  <a v-if="event.url" :href="event.url" target="_blank" class="meta-item meta-link" @click.stop>
                    <IonIcon :icon="linkOutline" />
                    Link
                  </a>
                </div>
              </div>

              <!-- Right: Menu Button -->
              <div class="event-menu-wrapper">
                <button class="menu-btn" @click.stop="toggleMenu(event.slug)">
                  <IonIcon :icon="ellipsisVertical" />
                </button>

                <!-- Popup Menu -->
                <div v-if="activeMenuSlug === event.slug" class="popup-menu">
                  <button class="popup-item edit-item" @click="editEvent(event.slug)">
                    <IonIcon :icon="createOutline" />
                    Edit
                  </button>
                  <button
                    class="popup-item delete-item"
                    :class="{ confirming: deletingSlug === event.slug }"
                    @click="deleteEvent(event.slug)">
                    <IonIcon :icon="trashOutline" />
                    {{ deletingSlug === event.slug ? 'Confirm?' : 'Delete' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
          <!-- Final separator at bottom -->
          <div class="separator"></div>
        </div>
      </div>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
.events-content {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
}

.ambient-glow {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  pointer-events: none;
  overflow: hidden;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, transparent 70%);
  top: -100px;
  left: -100px;
}

.orb-2 {
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(96, 165, 250, 0.15) 0%, transparent 70%);
  bottom: -50px;
  right: -50px;
}

.events-wrapper {
  position: relative;
  z-index: 1;
}

.header-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 12px;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.header-btn ion-icon {
  font-size: 22px;
}

.map-btn {
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.3);
  color: #60a5fa;
}

.map-btn:hover {
  background: rgba(59, 130, 246, 0.25);
}

.add-btn {
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  color: white;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.add-btn:hover {
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
}

/* Loading State */
.loading-state {
  display: flex;
  justify-content: center;
  padding: 3rem;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid rgba(59, 130, 246, 0.2);
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 3rem 1.5rem;
  text-align: center;
}

.empty-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: rgba(59, 130, 246, 0.1);
  border: 1px solid rgba(59, 130, 246, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1rem;
}

.empty-icon ion-icon {
  font-size: 28px;
  color: #60a5fa;
}

.empty-state p {
  color: #9aa3ad;
  margin: 0 0 1.25rem;
  font-size: 0.95rem;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  border-radius: 10px;
  background: linear-gradient(135deg, #1d4ed8, #2563eb);
  border: none;
  color: white;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.create-btn ion-icon {
  font-size: 18px;
}

/* Events List */
.events-list {
  padding: 0;
  margin-top: -8px;
}

.event-item {
  display: flex;
  flex-direction: column;
}

/* Separator */
.separator {
  height: 1px;
  background: rgba(59, 130, 246, 0.2);
}

/* Event Card */
.event-card {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 1.1rem 1rem;
  background: linear-gradient(135deg, rgba(30, 58, 95, 0.25) 0%, rgba(20, 40, 70, 0.15) 100%);
}

.event-emoji {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(59, 130, 246, 0.12);
  border: 1px solid rgba(59, 130, 246, 0.25);
  border-radius: 14px;
  font-size: 1.4rem;
  flex-shrink: 0;
}

.event-details {
  flex: 1;
  min-width: 0;
  cursor: pointer;
}

.event-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.event-title {
  font-size: 1.05rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  line-height: 1.3;
}

.event-type {
  font-size: 0.65rem;
  font-weight: 700;
  color: #60a5fa;
  background: rgba(59, 130, 246, 0.15);
  padding: 3px 8px;
  border-radius: 8px;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}

.event-description {
  font-size: 0.85rem;
  color: #8b9cad;
  margin: 0 0 10px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.event-meta {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8rem;
  color: #7a8a9a;
}

.meta-item ion-icon {
  font-size: 15px;
  color: #60a5fa;
}

.date-item {
  color: #9aa8b8;
}

.meta-link {
  color: #60a5fa;
  text-decoration: none;
  transition: color 0.2s;
  font-weight: 500;
}

.meta-link:hover {
  color: #93c5fd;
}

/* Menu Button & Popup */
.event-menu-wrapper {
  position: relative;
  flex-shrink: 0;
}

.menu-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #9aa8b8;
  cursor: pointer;
  transition: all 0.2s ease;
}

.menu-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.menu-btn ion-icon {
  font-size: 18px;
}

.popup-menu {
  position: absolute;
  top: 40px;
  right: 0;
  min-width: 130px;
  background: rgba(25, 40, 60, 0.98);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 12px;
  padding: 6px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
  z-index: 100;
}

.popup-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #d1d5db;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s ease;
}

.popup-item ion-icon {
  font-size: 17px;
}

.edit-item:hover {
  background: rgba(59, 130, 246, 0.15);
  color: #60a5fa;
}

.delete-item {
  color: #f87171;
}

.delete-item:hover {
  background: rgba(239, 68, 68, 0.15);
}

.delete-item.confirming {
  background: rgba(239, 68, 68, 0.2);
  animation: pulse 0.8s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}
</style>
