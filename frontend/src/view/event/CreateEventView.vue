<script setup lang="ts">
import { arrowBackOutline } from 'ionicons/icons';
import AppHeader from '@/component/AppHeader.vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import SaveEventForm from '@/component/event/SaveEventForm.vue';
import { Router, useRouter } from 'vue-router';

const router: Router = useRouter();
</script>

<template>
  <ApplicationViewport>
    <AppHeader
      title="Artist Events"
      subtitle="Register your next event here"
      :icon="arrowBackOutline"
      :on-icon-click="() => router.back()" />

    <SaveEventForm />
  </ApplicationViewport>
</template>
