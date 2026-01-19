<script setup lang="ts">
import { PropType, Ref, ref } from 'vue';
import { Event } from '@/entity/Event';
import EventMap from '@/component/event/EventMap.vue';
import { createOutline, trashOutline } from 'ionicons/icons';
import { IonIcon } from '@ionic/vue';
import { Router, useRouter } from 'vue-router';
import { callDeleteEvent } from '@/client/eventClient';
import { showToast } from '@/utils';

const props = defineProps({
  event: {
    type: Object as PropType<Event>,
    required: true,
  },
});

const deleting: Ref<boolean> = ref(false);

const router: Router = useRouter();

const editEvent = async () => {
  await router.push(props.event.slug);
};

const deleteEvent = async () => {
  if (!deleting.value) {
    deleting.value = true;
    setTimeout(() => (deleting.value = false), 3000);
    return;
  }

  callDeleteEvent(props.event.slug)
    .then(() => showToast('Event deleted with success'))
    .catch(() => showToast('Unable to delete event', 'warning'))
    .finally(() => router.back());
};
</script>

<template>
  <div class="event-item">
    <div class="event-map">
      <EventMap :events="[event]" />
    </div>

    <div class="event-details">
      <p>{{ event.title }} - <a v-if="event.url" :href="event.url" target="_blank">Website</a></p>
      <span>{{ event.type }}</span>

      <div class="event-actions">
        <button class="edit" @click="editEvent"><ion-icon :icon="createOutline" />Edit</button>
        <button class="delete" @click="deleteEvent">
          <ion-icon :icon="trashOutline" />{{ deleting ? 'Confirm?' : 'Delete' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.event-item {
  border-bottom: 1px solid #d7d8da;
  height: 100px;
  display: flex;

  & .event-map {
    height: 100px;
    width: 100px;
  }

  & .event-details {
    position: relative;
    width: 100%;
    padding: 0.8rem;

    & span {
      font-size: 0.8em;
      font-weight: bold;
    }

    & p {
      margin: 0;
    }

    & .event-actions {
      width: fit-content;
      height: fit-content;
      display: flex;
      position: absolute;
      bottom: 0.8rem;
      right: 0.8rem;

      & button {
        padding: 0.4rem 0.6rem;
        border-radius: 12px;
        display: flex;
        align-items: center;

        & ion-icon {
          margin: 0 0.4rem 3px 0;
        }

        &.edit {
          margin-right: 0.6rem;
          background: rgba(59, 130, 246, 0.15);
          border: 1px solid rgba(59, 130, 246, 0.3);
          box-shadow:
            0 2px 12px rgba(59, 130, 246, 0.2),
            inset 0 1px 0 rgba(255, 255, 255, 0.1);
        }

        &.delete {
          background: rgba(239, 68, 68, 0.15);
          border: 1px solid rgba(239, 68, 68, 0.3);
          box-shadow:
            0 2px 12px rgba(239, 68, 68, 0.2),
            inset 0 1px 0 rgba(255, 255, 255, 0.1);
        }
      }
    }
  }
}
</style>
