<script setup lang="ts">
import { onIonViewWillEnter } from '@ionic/vue';
import { computed, onMounted, ref } from 'vue';
import ApplicationViewport from '@/view/ApplicationViewport.vue';
import { useAuthStore } from '@/store/reactiveStorage';
import { callComputePostsAnalytics, callComputeProductsAnalytics } from '@/client/analyticsClient';
import { PostAnalytics } from '@/entity/Post';
import { ProductAnalytics } from '@/entity/Product';
import GlobalPostAnalytics from '@/component/analytics/GlobalPostAnalytics.vue';
import GlobalProductAnalytics from '@/component/analytics/GlobalProductAnalytics.vue';
import AppHeader from '@/component/AppHeader.vue';
import { arrowBackOutline } from 'ionicons/icons';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const postAnalytics = ref<PostAnalytics | null>(null);
const productAnalytics = ref<ProductAnalytics | null>(null);

const fetchAnalytics = async () => {
  try {
    postAnalytics.value = await callComputePostsAnalytics();
    productAnalytics.value = await callComputeProductsAnalytics();
  } catch (error) {
    console.error('Failed to fetch analytics:', error);
  }
};

const goBack = () => {
  router.back();
};

const isLoading = computed(() => !authStore.getAuthenticatedUser && !postAnalytics.value && !productAnalytics.value);

onIonViewWillEnter(async () => await fetchAnalytics());

onMounted(async () => await fetchAnalytics());
</script>

<template>
  <ApplicationViewport>
    <AppHeader
      title="Global Analytics"
      subtitle="All your data in one place"
      :icon="arrowBackOutline"
      :on-icon-click="goBack" />

    <div class="ambient-glow">
      <div class="glow-orb" />
      <div class="glow-orb" />
      <div class="glow-orb" />
    </div>

    <div v-if="isLoading" class="loading">
      <p>Loading...</p>
    </div>

    <div v-if="postAnalytics && productAnalytics">
      <GlobalPostAnalytics :analytics="postAnalytics" />
      <GlobalProductAnalytics :analytics="productAnalytics" />
    </div>
  </ApplicationViewport>
</template>

<style scoped>
.loading {
  text-align: center;
  font-size: 1.3em;
  padding-top: 3em;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
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
