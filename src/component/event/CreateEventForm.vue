<script setup lang="ts">
import { IonDatetime, IonIcon, IonModal } from '@ionic/vue';
import { checkmarkOutline } from 'ionicons/icons';
import { ref, Ref } from 'vue';
import { EventType } from '@/entity/Event';
import { formatDate, showToast } from '@/utils';
import { callCreateEvent, CreateEventPayload } from '@/client/eventClient';
import { useRouter } from 'vue-router';

const router = useRouter();

const type: Ref<EventType> = ref('CONCERT');
const title: Ref<string> = ref('');
const description: Ref<string> = ref('');
const url: Ref<string> = ref('');
const thumbnailUrl: Ref<string> = ref('');
const latitude: Ref<number> = ref(0);
const longitude: Ref<number> = ref(0);
const dateTime: Ref<string> = ref(new Date().toISOString());

const isDateModalOpen = ref(false);

const handleDateChange = (event: CustomEvent) => {
  dateTime.value = event.detail.value;
};

const createEvent = async () => {
  const payload: CreateEventPayload = {
    type: type.value,
    title: title.value,
    description: description.value,
    url: url.value,
    thumbnailUrl: thumbnailUrl.value,
    latitude: latitude.value,
    longitude: longitude.value,
    dateTime: dateTime.value,
  };

  callCreateEvent(payload)
    .then(() => showToast('Event created with success'))
    .catch(() => showToast('Unable to create event', 'warning'))
    .finally(() => router.back());
};
</script>

<template>
  <form id="event-form" @submit.prevent="createEvent">
    <div class="form-section">
      <div class="field">
        <label for="type">Event Type</label>
        <div class="select-wrapper">
          <select v-model="type" name="type" id="type" required>
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

      <div class="field">
        <label for="title">Title</label>
        <input v-model="title" type="text" name="title" id="title" placeholder="e.g. Summer Jazz Night" required />
      </div>

      <div class="field">
        <label for="description">Description</label>
        <textarea
          v-model="description"
          name="description"
          id="description"
          placeholder="Describe the event details..."
          rows="4"
          required></textarea>
      </div>

      <div class="field">
        <label for="datetime">Date & Time</label>
        <button type="button" name="datetime" id="datetime" class="date-trigger-btn" @click="isDateModalOpen = true">
          {{ formatDate(dateTime) }}
        </button>

        <ion-modal :is-open="isDateModalOpen" @didDismiss="isDateModalOpen = false" class="date-modal">
          <div class="sheet-container">
            <div class="scrollable-body">
              <ion-datetime
                id="datetime-picker"
                presentation="date-time"
                hour-cycle="h23"
                :value="dateTime"
                @ionChange="handleDateChange"
                class="custom-datetime" />
            </div>

            <div class="fixed-footer">
              <button class="modal-close-btn" @click="isDateModalOpen = false">Confirm Date</button>
            </div>
          </div>
        </ion-modal>
      </div>

      <div class="field">
        <label for="url">Event URL</label>
        <input v-model="url" type="url" name="url" id="url" placeholder="https://ticket-link.com" />
      </div>

      <div class="field">
        <label for="thumbnail-url">Thumbnail Image URL</label>
        <input
          v-model="thumbnailUrl"
          type="url"
          name="thumbnail-url"
          id="thumbnail-url"
          placeholder="https://image-source.com/poster.jpg" />
      </div>

      <div class="field-row">
        <div class="field">
          <label for="latitude">Latitude</label>
          <input
            v-model="latitude"
            type="number"
            step="any"
            name="latitude"
            id="latitude"
            placeholder="48.8566"
            required />
        </div>
        <div class="field">
          <label for="longitude">Longitude</label>
          <input
            v-model="longitude"
            type="number"
            step="any"
            name="longitude"
            id="longitude"
            placeholder="2.3522"
            required />
        </div>
      </div>

      <button type="submit" class="save-btn">
        <ion-icon :icon="checkmarkOutline" />
        Register Event
      </button>
    </div>
  </form>
</template>

<style scoped>
form {
  padding: 1rem;
}

.form-section {
  display: grid;
  gap: 16px;
  max-width: 600px;
  margin: 0 auto;
}

.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.field {
  display: grid;
  gap: 6px;
}

.field label {
  color: #cfcfcf;
  font-size: 12px;
  letter-spacing: 0.02em;
  padding-left: 4px;
  font-weight: 600;
}

.field input,
.field textarea,
.field select,
.date-trigger-btn {
  appearance: none;
  outline: none;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: color-mix(in oklab, #0b0b0b 75%, #0f0f0f);
  color: #fff;
  padding: 12px 14px;
  border-radius: 12px;
  font-size: 15px;
  font-family: inherit;
  width: 100%;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.field input::placeholder,
.field textarea::placeholder {
  color: #7f7f7f;
}

.field input:focus,
.field textarea:focus,
.field select:focus,
.date-trigger-btn:focus {
  border-color: rgba(59, 130, 246, 0.55);
  box-shadow:
    0 0 0 6px rgba(59, 130, 246, 0.1),
    0 8px 24px rgba(0, 0, 0, 0.25);
}

.field textarea {
  resize: vertical;
  min-height: 80px;
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
  justify-content: space-between;
}

.save-btn {
  width: 100%;
  height: 48px;
  border: 0;
  border-radius: 12px;
  cursor: pointer;
  color: #fff;
  font-weight: 700;
  letter-spacing: 0.01em;
  background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa);
  box-shadow:
    0 10px 30px rgba(37, 99, 235, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.12);
  transition:
    transform 0.06s ease,
    filter 0.2s ease,
    box-shadow 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
}

.save-btn:active {
  transform: translateY(1px);
}

.save-btn ion-icon {
  font-size: 20px;
}

.date-modal {
  --width: 100%;
  --height: 480px;
  align-items: flex-end;

  --ion-background-color: #1a1a1a;
  --border-radius: 16px 16px 0 0;
  --box-shadow: 0 -10px 20px rgba(0, 0, 0, 0.3);
}

.sheet-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #1a1a1a;
}

.scrollable-body {
  flex: 1;
  overflow-y: auto;
  position: relative;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.fixed-footer {
  flex-shrink: 0;
  background: #1a1a1a;
  padding: 16px 20px calc(16px + env(safe-area-inset-bottom));
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  justify-content: center;
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

.modal-close-btn:active {
  transform: scale(0.98);
}

.custom-datetime {
  border-radius: 16px;
  --background: #1a1a1a;
  --title-color: #ffffff;
  color: #ffffff;
}

.custom-datetime::part(calendar-day) {
  color: #ffffff;
}

.custom-datetime::part(calendar-day today) {
  color: #60a5fa;
  --background: rgba(96, 165, 250, 0.2);
}

.custom-datetime::part(time-button) {
  background: #2a2a2a;
  color: #ffffff;
  border-radius: 8px;
  padding: 5px 10px;
}

.custom-datetime::part(time-button active) {
  background: #2563eb;
  color: #ffffff;
}
</style>
