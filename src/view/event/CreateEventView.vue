<script setup lang="ts">
import { calendarNumberOutline } from 'ionicons/icons';
import AppHeader from '@/component/AppHeader.vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import CreateEventForm from '@/component/event/CreateEventForm.vue';
</script>

<template>
  <ApplicationViewport>
    <div id="main-wrapper">
      <AppHeader title="Artist Events" subtitle="Register your next event here" :icon="calendarNumberOutline" />

      <CreateEventForm />
    </div>
  </ApplicationViewport>
</template>

<style scoped></style>
