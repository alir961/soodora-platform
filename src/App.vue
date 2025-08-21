<script setup lang="ts">
import { cartOutline, searchOutline, personOutline } from 'ionicons/icons';
import { capitalize } from '@/utils';
import { onBeforeMount } from 'vue';
import { useAuthStore } from '@/store/reactiveStorage';
import { User } from '@/entity/User';
import { callRetrieveProfile } from '@/client/profileClient';
import { deleteBearerToken, saveBearerToken } from '@/store/localStorage';

interface TabEntry {
  name: string;
  icon: string;
  requiresAuth?: boolean;
  requiresAdminOrUser?: boolean;
}

const authStore = useAuthStore();

const tabs: TabEntry[] = [
  { name: 'marketplace', icon: cartOutline, requiresAuth: false },
  { name: 'search', icon: searchOutline, requiresAuth: false },
  { name: 'users', icon: personOutline, requiresAuth: true },
];

const redirectToApplication = () => {
  const url = import.meta.env.VITE_APPLICATION_URL;

  if (url) window.location.href = url;
  else console.error('Unable to redirect to application');
};

const authenticateUser = async () => {
  try {
    const user: User = await callRetrieveProfile();

    authStore.authenticate(user);
  } catch (error) {
    console.warn(error);
    redirectToApplication();
  }
};

onBeforeMount(async () => {
  const queryParams = new URLSearchParams(window.location.search);
  const token = queryParams.get('token');

  if (token) saveBearerToken(token);
  if (!authStore.isAuthenticated) await authenticateUser();
  const user: User | null = authStore.getAuthenticatedUser;

  if (user && user.roles.includes('ADMIN')) return;
  authStore.logout();
  deleteBearerToken();
  redirectToApplication();
});
</script>

<template>
  <div id="app">
    <header>
      <div class="banner-container">
        <img src="/assets/images/png/banner-light.png" alt="Soodora banner" />
      </div>

      <nav>
        <router-link v-for="tab in tabs" :key="tab.name" :to="`/${tab.name}`" class="tab-button">
          <ion-icon :icon="tab.icon" />
          <span>{{ capitalize(tab.name) }}</span>
        </router-link>
      </nav>
    </header>
    <main>
      <router-view />
    </main>
  </div>
</template>

<style scoped>
#app {
  background-color: white;
  min-height: 100vh;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 75px;
  padding: 1rem;
  background-color: #0d0d0d;

  & .banner-container {
    height: 50%;

    & img {
      margin-left: 1rem;
      height: 100%;
    }
  }
}

nav {
  display: flex;

  & a {
    display: flex;
    font-size: 1.5em;
    text-decoration: none;
    color: white;
    border: 1px solid transparent;

    &:hover {
      border-bottom: 1px solid white;
    }

    &:nth-child(n + 2) {
      margin-left: 1rem;
    }

    & span {
      margin-left: 0.2rem;
    }
  }
}
</style>
