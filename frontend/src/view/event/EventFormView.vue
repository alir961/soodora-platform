<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import Header from '@/component/AppHeader.vue';
import { IonIcon, IonContent, IonDatetime, IonModal } from '@ionic/vue';
import { closeOutline, calendarOutline, locationOutline, documentTextOutline } from 'ionicons/icons';
import { showToast, formatDate } from '@/utils';
import { callCreateEvent, callUpdateEvent, callRetrieveArtistEvents, SaveEventPayload } from '@/client/eventClient';
import type { Event, EventType } from '@/entity/Event';

const router = useRouter();
const route = useRoute();

const isEditMode = computed(() => !!route.params.eventSlug);
const artistSlug = computed(() => route.params.slug as string);
const eventSlug = computed(() => route.params.eventSlug as string);

// Get coordinates from query params (for create mode)
const initialLat = computed(() => {
  const lat = route.query.lat;
  return lat ? parseFloat(lat as string) : 0;
});

const initialLng = computed(() => {
  const lng = route.query.lng;
  return lng ? parseFloat(lng as string) : 0;
});

const type = ref<EventType | ''>('');
const title = ref<string>('');
const description = ref<string>('');
const url = ref<string>('');
const currentImageUrl = ref<string>('');
const latitude = ref<number>(0);
const longitude = ref<number>(0);
const dateTime = ref<string>(new Date().toISOString());
const isLoading = ref(false);
const isDateModalOpen = ref(false);

const closeForm = () => {
  router.back();
};

const loadEvent = async () => {
  if (!isEditMode.value) {
    // For new events, use query params coordinates
    latitude.value = initialLat.value;
    longitude.value = initialLng.value;
    return;
  }

  try {
    isLoading.value = true;
    const events = await callRetrieveArtistEvents(artistSlug.value);
    const existingEvent = events.find((e: Event) => e.slug === eventSlug.value);

    if (!existingEvent) {
      await showToast('Event not found', 'danger');
      router.back();
      return;
    }

    type.value = existingEvent.type.toLowerCase() as EventType;
    title.value = existingEvent.title;
    description.value = existingEvent.description;
    url.value = existingEvent.url;
    currentImageUrl.value = existingEvent.thumbnailUrl;
    latitude.value = existingEvent.latitude;
    longitude.value = existingEvent.longitude;
    dateTime.value = existingEvent.dateTime;
  } catch (error: unknown) {
    const message = 'Failed to load event. ' + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
    router.back();
  } finally {
    isLoading.value = false;
  }
};

const handleDateChange = (event: CustomEvent) => {
  dateTime.value = event.detail.value;
};

const saveEvent = async () => {
  try {
    isLoading.value = true;

    const eventPayload: SaveEventPayload = {
      type: type.value.toUpperCase(),
      title: title.value,
      description: description.value,
      url: url.value,
      thumbnailUrl: currentImageUrl.value || '',
      latitude: latitude.value,
      longitude: longitude.value,
      dateTime: dateTime.value,
    };

    if (isEditMode.value) {
      await callUpdateEvent(eventSlug.value, eventPayload);
      await showToast('Event updated successfully');
    } else {
      await callCreateEvent(eventPayload);
      await showToast('Event created successfully');
    }
    router.back();
  } catch (error: unknown) {
    const action = isEditMode.value ? 'update' : 'create';
    const message = `Failed to ${action} event. ` + (error instanceof Error ? error.message : '');
    await showToast(message, 'danger');
  } finally {
    isLoading.value = false;
  }
};

const isSubmitDisabled = computed(() => {
  if (isLoading.value) return true;
  if (!type.value || !title.value.trim() || !description.value.trim()) return true;
  return false;
});

onMounted(() => {
  loadEvent();
});
</script>

<template>
  <ApplicationViewport>
    <IonContent class="form-content-wrapper">
      <Header
        :title="isEditMode ? 'Edit Event' : 'Create Event'"
        :subtitle="title || 'New event'"
        :icon="documentTextOutline">
        <template #actions>
          <button class="close-btn" @click="closeForm">
            <IonIcon :icon="closeOutline" />
          </button>
        </template>
      </Header>

      <div v-if="isLoading && isEditMode" class="loading">
        <p>Loading event...</p>
      </div>

      <div v-else class="form-content">
        <div class="form-section">
          <div class="section-header">
            <IonIcon :icon="documentTextOutline" class="section-icon" />
            <h3>Event Details</h3>
          </div>

          <div class="form-field">
            <label>Event Type <span class="required">*</span></label>
            <div class="select-wrapper">
              <select v-model="type" required>
                <option value="" disabled selected>Select event type</option>
                <option value="concert">Concert</option>
                <option value="festival">Festival</option>
                <option value="competition">Competition</option>
                <option value="performance_show">Performance Show</option>
                <option value="educational">Educational</option>
                <option value="dance_music">Dance Music</option>
                <option value="religious_spiritual">Religious / Spiritual</option>
                <option value="other">Other</option>
              </select>
            </div>
          </div>

          <div class="form-field">
            <label>Title <span class="required">*</span></label>
            <input v-model="title" type="text" placeholder="e.g. Summer Jazz Night" required />
          </div>

          <div class="form-field">
            <label>Description <span class="required">*</span></label>
            <textarea v-model="description" placeholder="Describe the event details..." rows="4" required></textarea>
          </div>

          <div class="form-field">
            <label>Date & Time <span class="required">*</span></label>
            <button type="button" class="date-trigger-btn" @click="isDateModalOpen = true">
              <IonIcon :icon="calendarOutline" />
              {{ formatDate(dateTime) }}
            </button>
          </div>

          <div class="form-field">
            <label>Event URL <span class="optional">(Optional)</span></label>
            <input v-model="url" type="url" placeholder="https://ticket-link.com" />
          </div>

          <div class="form-field">
            <label>Thumbnail Image URL <span class="optional">(Optional)</span></label>
            <input v-model="currentImageUrl" type="url" placeholder="https://image-source.com/event-poster.jpg" />
          </div>

          <div v-if="!isEditMode && (latitude !== 0 || longitude !== 0)" class="location-banner">
            <IonIcon :icon="locationOutline" />
            <span>Location pinned on map</span>
          </div>
        </div>

        <button class="submit-btn" @click="saveEvent" :disabled="isSubmitDisabled">
          {{ isLoading ? (isEditMode ? 'Updating...' : 'Creating...') : isEditMode ? 'Update Event' : 'Create Event' }}
        </button>
      </div>

      <IonModal :is-open="isDateModalOpen" @didDismiss="isDateModalOpen = false" class="date-modal ion-palette-dark">
        <div class="sheet-container">
          <div class="scrollable-body">
            <IonDatetime
              id="datetime-picker"
              presentation="date-time"
              hour-cycle="h23"
              :value="dateTime"
              @ionChange="handleDateChange"
              class="custom-datetime ion-palette-dark" />
          </div>
          <div class="fixed-footer">
            <button class="modal-close-btn" @click="isDateModalOpen = false">Confirm Date</button>
          </div>
        </div>
      </IonModal>
    </IonContent>
  </ApplicationViewport>
</template>

<style scoped>
.form-content-wrapper {
  --background: linear-gradient(to bottom, rgba(13, 27, 42, 0.95), rgba(9, 18, 30, 0.92));
}

.close-btn {
  appearance: none;
  border: 1px solid rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
  font-size: 0.9rem;
  font-weight: 600;
  padding: 8px 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
}

.close-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  box-shadow: 0 0 12px rgba(239, 68, 68, 0.2);
}

.close-btn:active {
  transform: scale(0.95);
}

.close-btn ion-icon {
  font-size: 22px;
}

.loading {
  text-align: center;
  padding: 3rem 1.5rem;
  color: #9aa3ad;
}

.form-content {
  padding: 1.5rem;
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  background: linear-gradient(to bottom, rgba(13, 27, 42, 0.6), rgba(9, 18, 30, 0.5));
  border-radius: 16px;
  margin-top: 1rem;
  margin-bottom: 1rem;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.section-icon {
  font-size: 24px;
  color: #60a5fa;
}

.section-header h3 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #ffffff;
  margin: 0;
  letter-spacing: 0.01em;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-field label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #cfcfcf;
  letter-spacing: 0.01em;
  display: flex;
  align-items: center;
  gap: 6px;
}

.required {
  color: #f87171;
  font-size: 1rem;
}

.optional {
  color: #9aa3ad;
  font-size: 0.8rem;
  font-weight: 500;
}

.form-field input,
.form-field textarea,
.form-field select,
.date-trigger-btn {
  appearance: none;
  outline: none;
  border: 1px solid rgba(59, 130, 246, 0.2);
  background: rgba(13, 27, 42, 0.4);
  color: #fff;
  padding: 12px 14px;
  border-radius: 12px;
  font-size: 15px;
  font-family: inherit;
  width: 100%;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background 0.2s ease;
}

.form-field textarea {
  resize: vertical;
  min-height: 100px;
}

.form-field input::placeholder,
.form-field textarea::placeholder {
  color: #7f7f7f;
}

.form-field input:hover,
.form-field textarea:hover,
.form-field select:hover,
.date-trigger-btn:hover {
  border-color: rgba(59, 130, 246, 0.35);
  background: rgba(13, 27, 42, 0.6);
}

.form-field input:focus,
.form-field textarea:focus,
.form-field select:focus,
.date-trigger-btn:focus {
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow:
    0 0 0 6px rgba(59, 130, 246, 0.1),
    0 8px 24px rgba(0, 0, 0, 0.25);
  background: rgba(13, 27, 42, 0.7);
}

.select-wrapper {
  position: relative;
}

.select-wrapper::after {
  content: '▼';
  font-size: 10px;
  color: #7f7f7f;
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
}

.date-trigger-btn {
  text-align: left;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-trigger-btn ion-icon {
  font-size: 18px;
  color: #60a5fa;
}

.location-banner {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  background: rgba(37, 99, 235, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 12px;
  color: #60a5fa;
  font-size: 14px;
  font-weight: 600;
  backdrop-filter: blur(8px);
}

.submit-btn {
  width: 100%;
  height: 48px;
  border: 0;
  border-radius: 12px;
  cursor: pointer;
  color: #fff;
  font-weight: 700;
  font-size: 1rem;
  letter-spacing: 0.01em;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  box-shadow:
    0 10px 30px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
  transition: all 0.2s ease;
  margin-top: 0.5rem;
}

.submit-btn:hover:not(:disabled) {
  box-shadow:
    0 12px 32px rgba(37, 99, 235, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.16);
  transform: translateY(-1px);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow:
    0 8px 24px rgba(37, 99, 235, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  background: linear-gradient(135deg, #374151, #4b5563);
  box-shadow: none;
}

/* Modal Styles */
.date-modal {
  --width: 100%;
  --height: 480px;
  align-items: flex-end;
  --ion-background-color: transparent;
  --border-radius: 16px 16px 0 0;
}

.sheet-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #1a1a1a;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
}

.scrollable-body {
  flex: 1;
  overflow-y: auto;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.fixed-footer {
  flex-shrink: 0;
  background: #1a1a1a;
  padding: 16px 20px calc(16px + env(safe-area-inset-bottom));
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.modal-close-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
}

.custom-datetime {
  border-radius: 16px;
  --background: #1a1a1a;
  --background-rgb: 26, 26, 26;
  --title-color: #ffffff;
  color: #ffffff;

  /* Picker wheel colors */
  --ion-color-base: #1a1a1a;
  --ion-text-color: #ffffff;
  --ion-color-step-150: #2a2a2a;
  --ion-color-step-250: #3a3a3a;
  --ion-color-step-550: #7a7a7a;
  --ion-color-step-850: #d5d5d5;

  /* Wheel item colors */
  --wheel-highlight-background: rgba(59, 130, 246, 0.15);
  --wheel-fade-background-rgb: 26, 26, 26;
}

@media (max-width: 768px) {
  .form-content {
    padding: 1.25rem;
    margin-top: 0.75rem;
    margin-bottom: 0.75rem;
  }

  .form-field input,
  .form-field textarea,
  .form-field select {
    font-size: 16px;
  }
}
</style>
